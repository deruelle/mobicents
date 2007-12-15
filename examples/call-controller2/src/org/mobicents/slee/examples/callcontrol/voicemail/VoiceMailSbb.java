/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.examples.callcontrol.voicemail;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.Transaction;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.*;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;

import org.mobicents.slee.examples.callcontrol.common.SubscriptionProfileSbb;
import org.mobicents.slee.examples.callcontrol.profile.CallControlProfileCMP;
import org.mobicents.slee.resource.media.events.DtmfEvent;
import org.mobicents.slee.resource.media.events.EndMediaStreamEvent;
import org.mobicents.slee.resource.media.events.SessionResultEvent;
import org.mobicents.slee.resource.media.ratype.MediaSession;
import org.mobicents.slee.resource.media.ratype.MediaProvider;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;


/**
 * Voice Mail service logic using SIP RA with dialog support and Media RA.
 *
 * @author torosvi
 * @author baranowb
 * @author iivanov
 *
 */
public abstract class VoiceMailSbb extends SubscriptionProfileSbb implements 
		javax.slee.Sbb {
	
	public void onInvite(javax.sip.RequestEvent event,
			VoiceMailSbbActivityContextInterface localAci) {		
		Response response;
		log.info("########## VOICE MAIL SBB: INVITE ##########");
		// Request
		Request request = event.getRequest();
		// Setting Request
		this.setInviteRequest(request);
		
		// Server Transaction
		ServerTransaction st = event.getServerTransaction();
		// Setting Server Transaction
		this.setServerTransaction(st);
  
		try {
			localAci.detach(this.getSbbLocalObject());
			
			if (localAci.getFilteredByAncestor()) {
				log.info("########## VOICE MAIL SBB: FILTERED BY ANCESTOR ##########");
				return;
			}
			
			// if we are calling to vmail this means we want to check our mail box
			//sameUser = true
			boolean sameUser = sameUser(event);
			URI uri;

			if (sameUser) {
				// The user is the caller
				FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
				uri = fromHeader.getAddress().getURI();
			}
			else {
				// The user is the callee - we are calling someone else
				ToHeader toHeader = (ToHeader) request.getHeader(ToHeader.NAME);
				uri = toHeader.getAddress().getURI();
			}
			// In the Profile Table the port is not used
			((SipURI)uri).removePort();
			
			// Responding to the user
			// To know whether the user has the Voice mail service enabled
			boolean isSubscriber = isSubscriber(uri.toString());

			if (isSubscriber) {
				// Voice Mail service enabled
				URL audioFileURL;
				boolean receiveDtmf = false;
				String fileRoute = null;
				
				// Looking for the audio file to transmit
				Context initCtx = new InitialContext();
		    	Context myEnv = (Context) initCtx.lookup("java:comp/env");	   		    	

		    	// check if the user is calling their own number to check voice mail
	       		if (sameUser) {
	       			audioFileURL = getClass().getResource(waitingDTMF);
	       			receiveDtmf = true;
	       		}
	       		// or someone else is calling the user to leave a voice message
	       		else {
	       			audioFileURL = getClass().getResource(recordAfterTone);
	       			ToHeader toHeader = (ToHeader) request.getHeader(ToHeader.NAME);
	       			String fileName = ((SipURI)toHeader.getAddress().
	       					getURI()).getUser() + WAV_EXT;
	       			
	    		   	// Setting File Route where recording the voice message        			
			    	String route = (String) myEnv.lookup("filesRoute");
			    	fileRoute = route + fileName;
	       		}

	        	// SDP Description from the request		
	 			String sdp = new String(request.getRawContent());

	 			// Creating Media Session
				mediaSession = mediaProvider.getNewMediaSession();
	 			// Setting Media Session
	 			this.setMediaSession(mediaSession);

		 		// Attaching session AC
		 		ActivityContextInterface mediaSessionAci;
				mediaSessionAci = mediaRaACIF.getActivityContextInterface(mediaSession);
				mediaSessionAci.attach(this.getSbbLocalObject());

				// Attaching to SIP Dialog activity 
				Dialog dial = getSipFactoryProvider().getSipProvider().
						getNewDialog((Transaction) st);
				ActivityContextInterface dialogAci = sipACIF.getActivityContextInterface(dial);
				SbbLocalObject slo = this.getSbbLocalObject();
				// attach this SBB object to the Dialog activity to receive subsequent events on this Dialog
				dialogAci.attach(slo);

				// Notify caller that we're TRYING to reach voice mail. Just a formality, we know we can go further than TRYING at this point
				response = getMessageFactory().createResponse(Response.TRYING, request);
				st.sendResponse(response);

		    	// RINGING. Another formality of the SIP protocol.
				response = getMessageFactory().createResponse(Response.RINGING, request);
				st.sendResponse(response);
				
				URL fileRcv = null;

				if (fileRoute != null) 
					fileRcv = new URL(fileRoute);
				
				// Open the client and server media (RTP) ports.
				// The actual playback will begin when the user ACKnowledges
				// that the call will take place.
		 		mediaSession.createTransmitterReceiver(sdp,	audioFileURL,
		 				fileRcv, receiveDtmf);
				
			}
			// Voice Mail service disabled
			else {
				response = getMessageFactory().createResponse(
						Response.TEMPORARILY_UNAVAILABLE, request);
				log.info("########## NO VOICE MAIL AVAILABLE FOR USER: " + uri.toString());
				st.sendResponse(response);
			}

		} catch (TransactionRequiredLocalException e) {
			log.error(e.getMessage(), e);
		} catch (SLEEException e) {
			log.error(e.getMessage(), e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		} catch (SipException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidArgumentException e) {
			log.error(e.getMessage(), e);
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			log.error(e.getMessage(), e);
		} catch (NamingException e) {
			log.error(e.getMessage(), e);
		} catch (UnrecognizedActivityException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * If Voice Mail is used, VoicemailSbb will probably send
	 * an OK response to the caller. The caller will respond
	 * sending and ACK Request.
	 * onAckEvent method is precisely to handle the ACK event.
	 *  
	 * @param event
	 * @param aci
	 */
	public void onAckEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {
	    mediaSession = this.getMediaSession();
	    log.info("########## VOICE MAIL SBB: ACK ##########");
		try {
			this.setOk(true); 	// OK Response was sent and as
								// a result ACK Request received.
			
			// After receiving the ACK it is time to transmit the audio file.
			mediaSession.startSession();
   						
	   	} catch (FactoryException e) {
	   		log.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			log.error(e.getMessage(), e);
		}		
	}
	
	/**
	 * At any time a SIP Client can send a BYE Request.
	 * If the Voice Mail is being used it will be the
	 * VoicemailSbb the one that will send OK Response.
	 * 
	 * @param event
	 * @param aci
	 */
	public void onByeEvent(RequestEvent event, ActivityContextInterface aci) {
		log.info("########## VOICE MAIL SBB: BYE ##########");
		try {
			TimerID timerID = this.getTimerID();
			
	    	// Setting DTMF
	    	this.setDtmf(NON_DIGIT);

			// If there is a Timer set we have to cancel it.
			if (timerID != null) {
				timerFacility.cancelTimer(timerID);
			}
			
			mediaSession = this.getMediaSession();
			mediaSession.stopSession();
			
			// Sending the OK Response to the BYE Request received.
			byeRequestOkResponse(event);
			
			aci.detach(this.getSbbLocalObject());
			
		} catch (FactoryException e) {
			log.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void onOkEvent(ResponseEvent event, ActivityContextInterface aci) {
		log.info("########## OK Response received ##########");
		aci.detach(this.getSbbLocalObject());
	}
	
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		// Timer Event is fired in 2 cases after waiting a number of secs:
		// * After being waiting to receive DTMF digit and do not receive any.
		// * After being recording audio from an UA without receiving a BYE Request.
		mediaSession = this.getMediaSession();
		
		if (this.getSameUser()) {
			// In this case we were waiting DTMF digit.
			mediaSession.stopSession();
			// We have not received any DTMF digit, so we try it again.
			URL audioFileURL = getClass().getResource(tryAgain);
			mediaSession.createTransmitterReceiver(mediaSession.
					getSdpDescription(), audioFileURL, null, true);
		}
		else {
			// In this case we were waiting a BYE Request.
			mediaSession.stopSession();
			// We have not received the BYE, so we send the Request to the UA.
			sendByeRequest();
			aci.detach(this.getSbbLocalObject());
		}
	}
	
	/**
	 * If the the media session was created successfully, Voice Mail will
	 * send an OK Response to the caller or will start
	 * the session depending on each case.
	 * 
	 * @param event
	 * @param aci
	 */
	public void onSessionResultEvent(SessionResultEvent event, ActivityContextInterface aci) {
		log.info("########## VOICE MAIL SBB: SessionResultEvent ##########");
		String result = event.getResult();
		mediaSession = (MediaSession) aci.getActivity();
				
		if (result.equalsIgnoreCase(mediaSession.OK)) {
			if (this.getOk()) {
				// OK Response has been already sent
				mediaSession.startSession();
			}
			else {
				sendOkResponse();
			}
		}
		else {	
			log.warn(result);
			sendServerInternalError();
		}
	}
	

	private void sendServerInternalError() {
		try {
			Response response = getMessageFactory().createResponse(
					Response.SERVER_INTERNAL_ERROR, this.getInviteRequest());
			this.getServerTransaction().sendResponse(response);
			
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		} catch (SipException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidArgumentException e) {
			log.error(e.getMessage(), e);
		}				
	}
	
	public void onDtmfEvent(DtmfEvent event, ActivityContextInterface aci) {
		log.info("########## VOICE MAIL SBB: onDTMFEvent ##########");
		this.setDtmf(event.getDtmfDigit()); // The digit received
		
		// The features of session can change so we update it
		mediaSession = (MediaSession) aci.getActivity();
		
		// If the audio file was transmitted completely (Processor Stopped),
		// the Timer is set in order to wait DTMF digit.
		if (mediaSession.isProcessorStopped()) {
			// We have received a DTMF digit when the Timer is already set.
			mediaSession.stopSession();
			// We have to cancel the Timer becuase the DTMF digit has been received. 
			timerFacility.cancelTimer(this.getTimerID());
			
			// Setting Media Session
//			this.setMediaSession(mediaSession);

			checkDtmfDigit(this.getDtmf());
		}
		else {
			// The DTMF digit has been received while the audio file was being
			// transmitted, so the Processor was not Stopped and the Timer has
			// not been set. Therefore, we stop the Processor now.
			mediaSession.stopSession();

			// Setting Media Session
//			this.setMediaSession(mediaSession);
		}
	}
	
	public void onEndMediaStreamEvent(EndMediaStreamEvent event, ActivityContextInterface aci) {
		boolean forcedEnd = event.getForcedEnd();		
		log.info("########## VOICE MAIL SBB: onEndMediaStreamEvent ##########");
		if (forcedEnd) {
			if(this.getDtmf().equalsIgnoreCase(NON_DIGIT)) {
				log.info("########## BYE REQUEST RECEIVED ##########");
			}
			else {
				checkDtmfDigit(this.getDtmf());
			}
		}
		else {
			// The features of session can change so we update it
			mediaSession = (MediaSession) aci.getActivity();
			String dtmf = this.getDtmf();

			if (dtmf.equalsIgnoreCase("1") || dtmf.equalsIgnoreCase("7") ||
					dtmf.equalsIgnoreCase("9")) {
				mediaSession.stopSession();
				// We have not received the BYE, so we send the Request to the UA.
				sendByeRequest();
				aci.detach(this.getSbbLocalObject());
			}
			else {
				TimerOptions options = new TimerOptions();
				options.setPersistent(true);
				TimerID timerID;
				long waitingTime;
				
    			try {
    				Context initCtx = new InitialContext();
    		    	Context myEnv = (Context) initCtx.lookup("java:comp/env");	
    		    	
    		    	if (this.getSameUser()) {
    					waitingTime = ((Long)myEnv.lookup("waitingDTMF")).longValue();
    					log.info("########## WAITING DTMF ##########");
    				}				
    				else {
    					waitingTime = ((Long)myEnv.lookup("waitingVoiceMessage")).longValue();
    					mediaSession.startRecording();
    					log.info("########## RECORDING ##########");
    				}
  
					timerID = this.timerFacility.setTimer(aci, null,
							System.currentTimeMillis() + waitingTime, options);
	
					// Setting Timer ID
					this.setTimerID(timerID);
    			
    			} catch (NamingException e) {
    				log.error(e.getMessage(), e);
        		} 
			}
			
			// Setting Media Session
//			this.setMediaSession(mediaSession);
		}
	}
	
	private void sendOkResponse() {
		Response response;
		log.info("########## VOICE MAIL SBB: sendOk ##########");
		// Body content of the message (OK Response) 
    	String sdpData = mediaSession.generateSdpDescription(0,
    			"vmail_robot", "session");

    	if (sdpData != null) {
    		try {
    			// Creating OK Response        			
    			SipProvider sipProvider = getSipFactoryProvider().getSipProvider();
       			// Contact Address of the Voice Mail (contactHeader)
           		String sessionLocalAddress = sipProvider.getListeningPoints()[0].getIPAddress();
    			
           		javax.sip.address.Address contactAddress = getAddressFactory().createAddress(
    					"sip:" + sessionLocalAddress + ":" +
    					Integer.toString(sipProvider.getListeningPoints()[0].getPort()));
    			
    			ContactHeader contactHeader = getHeaderFactory().createContactHeader(contactAddress);

    	    	// Content Type Header (Media Type of the message-body)
    			ContentTypeHeader contentTypeHeader = getHeaderFactory().
    	    			createContentTypeHeader("application", "sdp");
    			
    			response = getMessageFactory().createResponse(Response.OK,
    					this.getInviteRequest(), contentTypeHeader, sdpData.getBytes());
 			
    			ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
    			// The "Tag" parameter - is used in the To and From header fields 
    			// of SIP messages. It serves as a general mechanism to identify 
    			// a dialog, which is the combination of the Call-ID along with 
    			// two tags, one from each participant in the dialog. 
    			toHeader.setTag("12345SomeTagID6789");
    			
    	        response.setHeader(contactHeader);        
    	        response.setHeader(toHeader);
    	        
    	        // This is suspicious, because one of the first things that onInvite does is to detach from the server transaction.
    	        // since there is time between the invite is received and the confirmation event from the media session, the server transaction activity might have expired.
    	        // Although it is unlikely because the sip transaction usually times out after tens of seconds and the media session setup should be established within 100ms.
    	        // Shouldn't the dialog activity be used here? 
    	        ServerTransaction st = this.getServerTransaction();
    	        st.sendResponse(response);
    			        
    		} catch (ParseException e) {
    			log.error(e.getMessage(), e);
    		} catch (SipException e) {
    			log.error(e.getMessage(), e);
    		} catch (InvalidArgumentException e) {
    			log.error(e.getMessage(), e);
    		}
    	}
    	else {
			try {
				response = getMessageFactory().createResponse(
						Response.SERVER_INTERNAL_ERROR, this.getInviteRequest());
				this.getServerTransaction().sendResponse(response);
				
			} catch (ParseException e) {
				log.error(e.getMessage(), e);
			} catch (SipException e) {
				log.error(e.getMessage(), e);
			} catch (InvalidArgumentException e) {
				log.error(e.getMessage(), e);
			}	
    	}
	}
	
	/**
	 * Voice Mail will hang up on caller sending a BYE Request.
	 * 
	 */
	private void sendByeRequest() {
		log.info("########## VOICE MAIL SBB: sendByRequest ##########");
		try {	
			SipProvider sipProvider = getSipFactoryProvider().getSipProvider();
			Dialog dialog = this.getServerTransaction().getDialog();
			Request request = dialog.createRequest(Request.BYE);			
			ClientTransaction ct = sipProvider.getNewClientTransaction(request);
			
			dialog.sendRequest(ct);
			
		} catch (TransactionUnavailableException e) {
			log.error(e.getMessage(), e);
		} catch (SipException e) {
			log.error(e.getMessage(), e);
		} 
	}
	
	/**
	 * After receiving a BYE Request,
	 * an OK Respose has to be sent.
	 * 
	 * @param byeEvent
	 */
	private void byeRequestOkResponse(RequestEvent byeEvent) {
		log.info("########## VOICE MAIL SBB: byeRequestOkResponse ##########");
		try {
			ServerTransaction serverTransaction = byeEvent.getServerTransaction();
			Request request = byeEvent.getRequest();			

			// Creating OK Response        			
			SipProvider sipProvider = getSipFactoryProvider().getSipProvider();
			// Contact Address of the Voice Mail (contactHeader)
			String sessionLocalAddress = sipProvider.getListeningPoints()[0].getIPAddress();
			javax.sip.address.Address contactAddress = getAddressFactory().createAddress(
					"sip:" + sessionLocalAddress + ":" +
					Integer.toString(sipProvider.getListeningPoints()[0].getPort()));	
			ContactHeader contactHeader = getHeaderFactory().createContactHeader(contactAddress);
			Response response = getMessageFactory().createResponse(Response.OK, request);
			ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
			// The "Tag" parameter - is used in the To and From header fields 
			// of SIP messages. It serves as a general mechanism to identify 
			// a dialog, which is the combination of the Call-ID along with 
			// two tags, one from each participant in the dialog. 
			toHeader.setTag("12345SomeTagID6789");

	        response.setHeader(contactHeader);    
	        response.setHeader(toHeader);
	         
			serverTransaction.sendResponse(response);

		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		} catch (SipException e) {
			log.error(e.getMessage(), e);
		}catch (InvalidArgumentException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private void checkDtmfDigit(String dtmf) {
		URL audioFileURL;
   	
		/** TODO: The Feature of listening the next message and
		 *  deleting the last message is not implemented yet.
		 *  After pressing 1 or 7 you will only listen a message
		 *  saying which number you have pressed 
		 */
		
		// Press 1 if you want to listen the next message
		if (dtmf.equals("1")) {
			audioFileURL = getClass().getResource(dtmf1);
			mediaSession.createTransmitterReceiver(mediaSession.
					getSdpDescription(), audioFileURL, null, false);
		}
		// Press 7 if you want to delete the last message
		else if (dtmf.equals("7")) {
			audioFileURL = getClass().getResource(dtmf7);
			mediaSession.createTransmitterReceiver(mediaSession.
					getSdpDescription(), audioFileURL, null, false);
		}
		// Press 9 if you want to hang up
		else if (dtmf.equals("9")) {
			audioFileURL = getClass().getResource(dtmf9);
			mediaSession.createTransmitterReceiver(mediaSession.
					getSdpDescription(), audioFileURL, null, false);
		}
		else {
			audioFileURL = getClass().getResource(tryAgain);
			mediaSession.createTransmitterReceiver(mediaSession.
					getSdpDescription(), audioFileURL, null, true);
		}	
	}
	
	/**
	 * To know whether or not the called user has the Voice Mail service
	 * enabled.
	 * 
	 * @param sipAddress:
	 *            Called user address.
	 * @return boolean: TRUE -> Voice Mail enabled. FALSE -> Voice Mail
	 *         disabled for the given user identified by sip address.
	 */
	private boolean isSubscriber(String sipAddress) {
		boolean state = false;
		CallControlProfileCMP profile = lookup(new Address(AddressPlan.SIP,
				sipAddress));

		if (profile != null) {
			state = profile.getVoicemailState();
		}

		return state;
	}
	
	/**
	 * This method is used to know if the it is going to be used the 
	 * voice mail of the same user or the voice mail of a different user.
	 * 
	 * @param event
	 * @return TRUE: If the called user is sip:vmail@nist.gov
	 */
	private boolean sameUser(javax.sip.RequestEvent event) {
		boolean sameUser = false;
		Request inviteRequest = event.getRequest();
 
		// Checking if the called user and the caller are the same
		ToHeader toHeader = (ToHeader)inviteRequest.getHeader(ToHeader.NAME);
		SipURI toURI = (SipURI) toHeader.getAddress().getURI();
		
		if ((toURI.getUser().equals(USER) && toURI.getHost().equals(HOST))) {
			sameUser = true;
		}
		
		// Setting Same User value
		this.setSameUser(sameUser);
		
		return sameUser;
	}

	// TODO: Perform further operations if required in these methods.
	public void setSbbContext(SbbContext context) {
		super.setSbbContext(context);
		
	    // To create Header objects from a particular implementation of JAIN SIP
	    headerFactory = getSipFactoryProvider().getHeaderFactory();
	    
		try {
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            // Getting Media Resource Adaptor interfaces            
            mediaProvider = (MediaProvider) myEnv.lookup("slee/resources/media/1.0/provider");
            mediaRaACIF = (MediaRaActivityContextInterfaceFactory)
            						myEnv.lookup("slee/resources/media/1.0/acifactory");
            // Getting Sip Resource Adaptor interface
            sipACIF = (SipActivityContextInterfaceFactory)
            						myEnv.lookup("slee/resources/jainsip/1.2/acifactory");
            // Getting Timer Facility interface
            timerFacility = (TimerFacility) myEnv.lookup("slee/facilities/timer");
           
        } catch (NamingException e) {
        	log.error(e.getMessage(), e);
        }		    
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
    	// Setting DTMF
    	this.setDtmf(NON_DIGIT);
	}
	public abstract org.mobicents.slee.examples.callcontrol.profile.CallControlProfileCMP getCallControlProfileCMP(
			javax.slee.profile.ProfileID profileID)
			throws javax.slee.profile.UnrecognizedProfileNameException,
			javax.slee.profile.UnrecognizedProfileTableNameException;

	public abstract org.mobicents.slee.examples.callcontrol.voicemail.VoiceMailSbbActivityContextInterface asSbbActivityContextInterface(
			ActivityContextInterface aci);

	private final HeaderFactory getHeaderFactory() { return headerFactory; }
	
	// Interfaces
	private HeaderFactory headerFactory;
    private MediaProvider mediaProvider;
    private MediaRaActivityContextInterfaceFactory mediaRaACIF;
    private SipActivityContextInterfaceFactory sipACIF;
    private TimerFacility timerFacility;
    
    private final String recordAfterTone = "audiofiles/RecordAfterTone.wav";
    private final String waitingDTMF = "audiofiles/WaitingDTMF.wav";
    private final String dtmf1= "audiofiles/DTMF1.wav";
    private final String dtmf7 = "audiofiles/DTMF7.wav";
    private final String dtmf9 = "audiofiles/DTMF9.wav";
    private final String tryAgain = "audiofiles/TryAgain.wav";
    
    private final String USER = "vmail";
    private final String HOST = "nist.gov";
    private final String NON_DIGIT = "NULL";
    private final String WAV_EXT = ".wav";
    
    private MediaSession mediaSession;
    
	/**
	 * *****************************************
	 * ************** CMP Fields ***************
	 * *****************************************
	 */
    
	// 'mediaSession' CMP field setter
	public abstract void setMediaSession(MediaSession value);
	// 'mediaSession' CMP field getter
	public abstract MediaSession getMediaSession();

	// 'inviteRequest' CMP field setter
	public abstract void setInviteRequest(Request value);
	// 'inviteRequest' CMP field getter
	public abstract Request getInviteRequest();

	// 'serverTransaction' CMP field setter
	public abstract void setServerTransaction(ServerTransaction value);
	// 'serverTransaction' CMP field getter
	public abstract ServerTransaction getServerTransaction();

	// 'ok' CMP field setter
	public abstract void setOk(boolean value);
	// 'ok' CMP field getter
	public abstract boolean getOk();

	// 'sameUser' CMP field setter
	public abstract void setSameUser(boolean value);
	// 'sameUser' CMP field getter
	public abstract boolean getSameUser();

	// 'timerID' CMP field setter
	public abstract void setTimerID(TimerID value);
	// 'timerID' CMP field getter
	public abstract TimerID getTimerID();

	// 'dtmf' CMP field setter
	public abstract void setDtmf(String value);
	// 'dtmf' CMP field getter
	public abstract String getDtmf();
}