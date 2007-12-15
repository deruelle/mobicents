package org.mobicents.slee.examples.callforward;

import gov.nist.javax.sip.stack.SIPDialog;
import gov.nist.javax.sip.stack.SIPServerTransaction;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipStack;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.*;

import org.apache.log4j.Logger;
import org.mobicents.slee.examples.callforwardblock.BaseSbb;
import org.mobicents.slee.examples.callforwardblock.controller.ControllerActivityContextInterface;
import org.mobicents.slee.examples.callforwardblock.events.ByeReqEvent;
import org.mobicents.slee.examples.callforwardblock.events.CallForwardFailureEvent;
import org.mobicents.slee.examples.callforwardblock.events.CallForwardSuccessEvent;
import org.mobicents.slee.examples.callforwardblock.session.ClientCallLeg;
import org.mobicents.slee.examples.callforwardblock.session.ServerCallLeg;
import org.mobicents.slee.services.sip.common.SipSendErrorResponseException;

/**
 * The CallForwardSbb is a B2BUA.  The receiving end (caller) of INVITE acts as UAS, 
 * while the sending end acts as UAC to the callee party.
 * <li>CMP field CallLegList is used to store persistent state of active calls
 * @author hchin
 */
public abstract class CallForwardSbb extends BaseSbb {
	
	private static Logger log = Logger.getLogger(CallForwardSbb.class);

	/**
	 * Custom event handler for CallForwardEvent type.  The CallForwardEvent is generated when
	 * another SBB wants to forward a call to another party.
	 * @param event CallForwardEvent - event to be processed
	 * @param aci ActivityContextInterface
	 * @see CallForwardEvent.java
	 */
	
	/**
	 * Handles INVITE requests by acting as B2BUA by creating a new INVITE to send to callee 
	 * @param inviteReq Request
	 * @param aci ActivityContextInterface
	 */
	public void onInviteEvent(RequestEvent event, ActivityContextInterface aci) {
		log.info("Processing onInviteEvent");
		ServerTransaction st = (ServerTransaction) aci.getActivity();
		Request inviteReq = event.getRequest();
		FromHeader fromHdr = (FromHeader)inviteReq.getHeader(FromHeader.NAME);
		ToHeader toHdr = (ToHeader)inviteReq.getHeader(ToHeader.NAME);
		ContentTypeHeader type = (ContentTypeHeader)inviteReq.getHeader(ContentTypeHeader.NAME);
		Object content = inviteReq.getContent();
		Request request = null;
		try {
			CallIdHeader callerCallIdHdr = (CallIdHeader)inviteReq.getHeader(CallIdHeader.NAME);
			log.info("caller callId = " + callerCallIdHdr.getCallId());
			ServerCallLeg callerCallLeg = new ServerCallLeg(callerCallIdHdr.getCallId(), fromHdr.getAddress());
			callerCallLeg.setServerTx(st);
			// act as UAC and send INVITE to destination
			request = buildInvite(inviteReq.getRequestURI(), fromHdr, toHdr, callerCallIdHdr, content, type, 1);
			ClientTransaction ct = sipProvider.getNewClientTransaction(request);
			CallIdHeader calleeCallIdHdr = (CallIdHeader)ct.getRequest().getHeader(CallIdHeader.NAME);
			log.info("callee callId = " + calleeCallIdHdr.getCallId());
			ClientCallLeg calleeCallLeg = new ClientCallLeg(calleeCallIdHdr.getCallId(), toHdr.getAddress());
			callerCallLeg.setCalleeCallId(calleeCallLeg.getCallId());
			calleeCallLeg.setClientTx(ct);
			calleeCallLeg.setCallerCallId(callerCallIdHdr.getCallId());
			
			// act as UAS and send 100 Call_IS_BEING_FORWARDED response back
			Response callFwdResp = msgFactory.createResponse(Response.TRYING, inviteReq);
			st.sendResponse(callFwdResp);
			
			// get server dialog
			Dialog sdialog = st.getDialog();
			if (sdialog == null) {
				log.warn("Automatic dialog support turned off for server transaction");
				try {
					// create new dialog and associate with server transaction
					sdialog = sipProvider.getNewDialog(st);
					log.debug("Obtained dialog for INVITE request from caller");
				} catch (Exception e) {
					log.error("Error getting new server dialog", e);
				}
			}
			
			// attach this SBB to the activity context of ClientTransaction, response events handled by this SBB
			ActivityContextInterface ctAci = activityContextInterfaceFactory.getActivityContextInterface(ct);
			ctAci.attach(sbbContext.getSbbLocalObject());
			
			// get client dialog
			Dialog cdialog = ct.getDialog();
			if ( cdialog == null ) {
				log.info("Automatic dialog support turned off for client transaction");
				try {
					// create new dialog and associate with client transaction
					cdialog = sipProvider.getNewDialog(ct);
					// store each others dialog, so that they have access to it
					cdialog.setApplicationData(sdialog);
					sdialog.setApplicationData(cdialog);
					log.debug("Obtained dialog for INVITE request to callee with getNewDialog");
				} catch (Exception e) {
					log.error("Error getting client dialog", e);
				}
			}
			// send INVITE request to callee
			ct.sendRequest();
		} catch (SipSendErrorResponseException ssere) {
			log.error("Error building new INVITE to callee", ssere);
			sendServerErrorResponse(st, inviteReq, ssere.getStatusCode());
		} catch (Exception e) {
			log.error(e.getClass().getName() + " when sending request", e);
			sendServerErrorResponse(st, inviteReq, Response.SERVER_INTERNAL_ERROR);
		}
	}
	
	/**
	 * ACK event handler for UAS.  B2BUA send ACK request to the correct destination.
	 * Uses LocationService to lookup the address of record used in ToHeader.
	 * Note, sbb-jar.xml file set Ack event as initial-event selection based on callIDSelect method.
	 * Therefore an existing SBB should be selected from the pool, thus CMP fields should be available.
	 * @param event ACK RequestEvent to process
	 * @param aci ActivityContextInterface
	 */
	public void onAckEvent(RequestEvent event, ActivityContextInterface aci) {
		log.info("Processing onAckEvent");
		Request request = event.getRequest();
		ContentTypeHeader ctype = (ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME);
		if (ctype != null)
			log.info("content type " + ctype.toString() + " raw data " + request.getRawContent());
		
		SIPDialog cdialog = (SIPDialog)event.getDialog().getApplicationData();
		if (cdialog == null) {
			log.error("Invalid client dialog");
			return;
		}
		ClientTransaction ct = (ClientTransaction)cdialog.getLastTransaction();
		try {
//			Dialog cdialog = ct.getDialog();
//			Request initialReq = ct.getRequest();
//			log.info("Initial request method " + initialReq.getMethod());
//			CSeqHeader cseq = (CSeqHeader)initialReq.getHeader(CSeqHeader.NAME);
//			log.info("Initial request CSeq " + cseq.getSequenceNumber());
//			Request ackReq = cdialog.createAck(cseq.getSequenceNumber());
//			cdialog.sendAck(ackReq);
			// Manually create ACK request to send to callee
			Request inviteReq = ct.getRequest();
			URI reqUri = inviteReq.getRequestURI();
			CallIdHeader callIdHdr = (CallIdHeader)inviteReq.getHeader(CallIdHeader.NAME);
			CSeqHeader cseqHdr = (CSeqHeader)inviteReq.getHeader(CSeqHeader.NAME);
			cseqHdr.setMethod(Request.ACK);
			FromHeader fromHdr = (FromHeader)inviteReq.getHeader(FromHeader.NAME);
			ToHeader toHdr = (ToHeader)inviteReq.getHeader(ToHeader.NAME);
//			toHdr.setTag(session.getCallee().getToTag()); // set to tag 
			ArrayList viaHeaderList = new ArrayList();
			viaHeaderList.add(buildViaHeader());
			MaxForwardsHeader maxFwdHdr = hdrFactory.createMaxForwardsHeader(70);
			Request ackReq = null;
			if (ctype == null)
				ackReq = msgFactory.createRequest(reqUri, Request.ACK, callIdHdr, cseqHdr, 
					fromHdr, toHdr, viaHeaderList, maxFwdHdr);
			else
				ackReq = msgFactory.createRequest(reqUri, Request.ACK, callIdHdr, cseqHdr, 
						fromHdr, toHdr, viaHeaderList, maxFwdHdr, ctype, request.getRawContent());
			ClientTransaction ackTx = sipProvider.getNewClientTransaction(ackReq);

			// attach ACI
			ActivityContextInterface ctAci = activityContextInterfaceFactory.getActivityContextInterface(ackTx);
			ctAci.attach(sbbContext.getSbbLocalObject());
			ackTx.sendRequest();
			
			// fire CallForwardSuccess event
			log.info("Firing CallForwardSuccessEvent");
			CallForwardSuccessEvent success = new CallForwardSuccessEvent();
			ActivityContextInterface controllerACI = acNamingFacility.lookup(CONTROLLER_ACTIVITY_CONTEXT_NAME);
			fireCallForwardSuccessEvent(success, controllerACI, null);
		} catch (Exception e) {
			log.error(e.getClass().getName() + " handling ACK response", e);
		}
	}
	
	/**
	 * BYE event handler.
	 * Fires a ByeReqEvent on activitycontext.  The ByeSbb should receive the event for processing 
	 * @param event RequestEvent generated from SIPRA
	 * @param aci ActivityContextInterface activity context wrapping the BYE request event
	 */
	public void onByeEvent(RequestEvent event, ActivityContextInterface aci) {
		log.info("Processing onByeEvent");

		// get dialog to send in ByeReqEvent
		ByeReqEvent byeEvent = new ByeReqEvent();
		byeEvent.setReqDialog(event.getDialog());

		log.info("Firing ByeReqEvent");
		fireByeReqEvent(byeEvent, aci, null);
	}
	
	public void onCancelEvent(RequestEvent event, ActivityContextInterface aci) {
		log.info("Processing onCancelEvent");
	}

	/**
	 * Provisional Response event handler for client call leg.
	 * Looks up ServerTransaction that was used from the original INVITE and sends a response.
	 * <li>100 TRYING is discarded
	 * <li>180 RINGING is sent back to caller
	 * @param event ResponseEvent that generated this request
	 * @param aci ActivityContextInterface
	 */
	public void onInfoRespEvent(ResponseEvent event, ActivityContextInterface aci) {
		log.info("Processing onInfoRespEvent response code: " + event.getResponse().getStatusCode());
		int responseCode = event.getResponse().getStatusCode();
		if (responseCode == Response.TRYING) {
			log.debug("Trying response received, discarding");
			return; // already sent 100 TRYING message to caller
		} else if (responseCode == Response.RINGING) {
			// ringing received from callee, send ringing message back to caller
			try {
				SIPDialog sdialog = (SIPDialog)event.getDialog().getApplicationData();
				SIPServerTransaction st = (SIPServerTransaction)sdialog.getLastTransaction(); 
				Request req = st.getRequest();
				if (sdialog != null) {
					log.info("Using server dialog to send RING response");
					req = sdialog.getLastTransaction().getRequest();
				} else {
					log.error("Server dialog has not been created");
					return;
				}
				Response ringResp = msgFactory.createResponse(Response.RINGING, req);

				// set to tag
				ToHeader ringRespToHdr = (ToHeader)ringResp.getHeader(ToHeader.NAME);
				ToHeader calleeToHdr = (ToHeader)event.getResponse().getHeader(ToHeader.NAME);
				ringRespToHdr.setTag(calleeToHdr.getTag());
				ringResp.setHeader(ringRespToHdr);
				st.sendResponse(ringResp);
			} catch (Exception e) {
				log.error(e.getClass().getName() + " handling info response", e);
			}
		}
	}
	
	/**
	 * 200 OK Response event handler for client call leg.
	 * Creates a new OK response and sets the contact header to point to this server, so that ACK will be received
	 * @param event ResponseEvent for 200 OK
	 * @param aci ActivityContextInterface
	 * @see #onAckEvent(RequestEvent, ActivityContextInterface) for handling of ACK request
	 */
	public void onSuccessRespEvent(ResponseEvent event, ActivityContextInterface aci) {
		log.info("Processing onSuccessRespEvent");
		// pass along content info
		Response response = event.getResponse();
		byte[] content = response.getRawContent();
		ContentTypeHeader ctype = (ContentTypeHeader)response.getHeader(ContentTypeHeader.NAME);
		try {
			// get server dialog
			SIPDialog sdialog = (SIPDialog)event.getDialog().getApplicationData();
			if (sdialog == null) {
				log.error("Server dialog invalid");
				return;
			}
			SIPServerTransaction st = (SIPServerTransaction)sdialog.getLastTransaction(); 
			Response okResp = msgFactory.createResponse(Response.OK, st.getRequest(), ctype, content);
			// set to tag
			ToHeader okRespToHdr = (ToHeader)okResp.getHeader(ToHeader.NAME);
			ToHeader calleeToHdr = (ToHeader)event.getResponse().getHeader(ToHeader.NAME);
			okRespToHdr.setTag(calleeToHdr.getTag());
			okResp.setHeader(okRespToHdr);
			// change contact header to point to Mobicents server
			ContactHeader contact = (ContactHeader)response.getHeader(ContactHeader.NAME);
			String contactName = contact.getAddress().toString().split("@")[0].split(":")[1]; 
			String ipAddr = sipProvider.getSipStack().getIPAddress();
			SipURI sipUri = addrFactory.createSipURI(contactName, ipAddr);
			Address addr = addrFactory.createAddress(contactName, sipUri);
			log.info("Contact address to be used for ACK : " + addr.toString());
			contact = hdrFactory.createContactHeader(addr);
			// add contact header to OK response
			okResp.setHeader(contact);
			st.sendResponse(okResp);
		} catch (Exception e) {
			log.error(e.getClass().getName() + " handling success response", e);
		}
	}
    
    // Lifecycle methods
    public void sbbCreate() throws javax.slee.CreateException {
    	// init CMP field
    	log.info("sbbCreate for CallForwardSbb");
    	setCallLegList(new HashMap());
    }

	// Define fire event methods
	public abstract void fireCallForwardSuccessEvent(CallForwardSuccessEvent event, 
	        ActivityContextInterface aci, javax.slee.Address address);
	public abstract void fireCallForwardFailureEvent(CallForwardFailureEvent event,
			ActivityContextInterface aci, javax.slee.Address address);
	public abstract void fireByeReqEvent(ByeReqEvent event,
			ActivityContextInterface aci, javax.slee.Address address);
	
	// Define CMP fields
	public abstract void setCallLegList(HashMap value);
	public abstract HashMap getCallLegList();
	
	// Define activity context interface narrow method
	public abstract ControllerActivityContextInterface asSbbActivityContextInterface( ActivityContextInterface aci);
	
	// Define Initial Event Selector method
	/**
	 * For initial events SLEE container calls this method to compute convergence name
	 * to help route the event to the appropriate SBB.
	 * @param ies InitialEventSelector
	 * @return InitialEventSelector with appropriate custom name set
	 */
    public InitialEventSelector callIDSelect( InitialEventSelector ies) {
    	log.info("Current convergence name = " + ies.getCustomName());
		Object event = ies.getEvent();
		String callId = null;
		if (event instanceof ResponseEvent) {
			// If response event, the convergence name to callId
			Response response = ((ResponseEvent) event).getResponse();
			callId = ((CallIdHeader) response.getHeader(CallIdHeader.NAME))
					.getCallId();
		} else if (event instanceof RequestEvent) {
			// If request event, the convergence name to callId
			Request request = ((RequestEvent) event).getRequest();
			callId = ((CallIdHeader) request.getHeader(CallIdHeader.NAME))
					.getCallId();
		} else {
			// If something else, use activity context.
			ies.setActivityContextSelected(true);
			return ies;
		}
		// Set the convergence name
		log.info("Setting convergence name to: " + callId);
		ies.setCustomName(callId);
		return ies;
    }

    /**
	 * Build an INVITE request
	 * @param reqUri Request URI
	 * @param fromHdr FromHeader source of the request
	 * @param toHdr ToHeader destination of the request
	 * @param callIdHdr CallIdHeader from originating UA
	 * @param content Object body content
	 * @param contentType ContentTypeHeader header content type
	 * @param cseq integer cseq value
	 * @return Request
	 * @throws InvalidArgumentException
	 * @throws ParseException
	 */
	private Request buildInvite(URI reqUri, FromHeader fromHdr, ToHeader toHdr, CallIdHeader callIdHdr,  Object content, 
			ContentTypeHeader contentType, int cseq) 
			throws InvalidArgumentException, ParseException, SipSendErrorResponseException {
		Request request = null;
		CSeqHeader cseqHdr = hdrFactory.createCSeqHeader(cseq, Request.INVITE);
		ArrayList viaHeaderList = new ArrayList();
		viaHeaderList.add(buildViaHeader());
		MaxForwardsHeader maxFwdHeader = hdrFactory.createMaxForwardsHeader(70);
		URI requestURI = findLocalTarget(reqUri);
		// reuse the same callId header when communicating with destination UA (callee)
		final CallIdHeader callIdHeader = hdrFactory.createCallIdHeader(callIdHdr.getCallId());
		request = msgFactory.createRequest(requestURI, Request.INVITE, callIdHeader, cseqHdr,
				fromHdr, toHdr, viaHeaderList, maxFwdHeader);
		if (content != null && contentType != null) {
			request.setContent(content, contentType);
			String contentStr = null;
			if (content instanceof byte[]) {
				contentStr = new String((byte[])content);
			} else if (content instanceof String) {
				contentStr = (String)content;
			}
			ContentLengthHeader contentLengthHeader = hdrFactory.createContentLengthHeader(contentStr.length());
			request.setContentLength(contentLengthHeader);
		}
		// add contact header
		request.addHeader(createLocalContactHeader());
		return request;
	}

	private ContactHeader createLocalContactHeader() throws ParseException {

		// First get the sip stack from the sip provider
		SipStack sipStack = sipProvider.getSipStack();
		// Get the host name of this listening point
		final String host = sipStack.getIPAddress();
		// Get the port
		final int port = sipProvider.getListeningPoints()[0].getPort();
		// Get the transport
		final String transport = sipProvider.getListeningPoints()[0]
				.getTransport();

		// Create a SIP URI of the host name
		SipURI sipURI = null;
		sipURI = addrFactory.createSipURI(null, host);
		sipURI.setTransportParam(transport);
		// Attach the port to the SIP URI
		sipURI.setPort(port);
		// Attach the transport

		// Create the contact address using the address factory
		Address contactAddress = addrFactory.createAddress(sipURI);
		// Create the contact header from the contact address
		ContactHeader contactHeader = hdrFactory.createContactHeader(contactAddress);
		return contactHeader;
	}

}
