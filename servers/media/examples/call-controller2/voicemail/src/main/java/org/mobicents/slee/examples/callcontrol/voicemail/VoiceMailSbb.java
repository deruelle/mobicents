/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.examples.callcontrol.voicemail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
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
import javax.slee.ActivityContextInterface;
import javax.slee.Address;
import javax.slee.AddressPlan;
import javax.slee.FactoryException;
import javax.slee.SLEEException;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;

import net.java.slee.resource.sip.DialogActivity;
import net.java.slee.resource.sip.SipActivityContextInterfaceFactory;

import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionMode;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkMode;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.events.MsEventAction;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.events.ann.MsPlayRequestedSignal;
import org.mobicents.mscontrol.events.audio.MsRecordRequestedSignal;
import org.mobicents.mscontrol.events.dtmf.MsDtmfNotifyEvent;
import org.mobicents.mscontrol.events.dtmf.MsDtmfRequestedEvent;
import org.mobicents.mscontrol.events.pkg.DTMF;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.mscontrol.events.pkg.MsAudio;
import org.mobicents.slee.examples.callcontrol.common.SubscriptionProfileSbb;
import org.mobicents.slee.examples.callcontrol.profile.CallControlProfileCMP;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;

/**
 * Voice Mail service logic using SIP RA with dialog support and Media RA.
 * 
 * @author torosvi
 * @author baranowb
 * @author iivanov
 * 
 */
public abstract class VoiceMailSbb extends SubscriptionProfileSbb implements javax.slee.Sbb {

	public void onInvite(javax.sip.RequestEvent event, VoiceMailSbbActivityContextInterface localAci) {
		Response response;
		log.info("########## VOICE MAIL SBB: INVITE ##########");
		// Request
		Request request = event.getRequest();
		// Setting Request
		this.setInviteRequest(request);

		// Server Transaction
		ServerTransaction st = event.getServerTransaction();
		// Setting Server Transaction
		//this.setServerTransaction(st);

		try {
			//localAci.detach(this.getSbbLocalObject());

			if (localAci.getFilteredByAncestor()) {
				log.info("########## VOICE MAIL SBB: FILTERED BY ANCESTOR ##########");
				return;
			}

			// if we are calling to vmail this means we want to check our mail
			// box
			// sameUser = true
			boolean sameUser = sameUser(event);
			URI uri;

			if (sameUser) {
				// The user is the caller
				FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
				uri = fromHeader.getAddress().getURI();
			} else {
				// The user is the callee - we are calling someone else
				ToHeader toHeader = (ToHeader) request.getHeader(ToHeader.NAME);
				uri = toHeader.getAddress().getURI();
			}
			// In the Profile Table the port is not used
			((SipURI) uri).removePort();

			// Responding to the user
			// To know whether the user has the Voice mail service enabled
			boolean isSubscriber = isSubscriber(uri.toString());

			if (isSubscriber) {

				//Formalities of sip, so we dont get retrans
				// Attaching to SIP Dialog activity
				Dialog dial = getSipFactoryProvider().getNewDialog((Transaction) st);
				ActivityContextInterface dialogAci = sipACIF.getActivityContextInterface((DialogActivity)dial);

				// attach this SBB object to the Dialog activity to receive
				// subsequent events on this Dialog
				dialogAci.attach(this.getSbbLocalObject());
				
				// Notify caller that we're TRYING to reach voice mail. Just a
				// formality, we know we can go further than TRYING at this
				// point
				response = getMessageFactory().createResponse(Response.TRYING, request);
				st.sendResponse(response);

				// RINGING. Another formality of the SIP protocol.
				response = getMessageFactory().createResponse(Response.RINGING, request);
				st.sendResponse(response);
				
				
				
				
				
				// Creating Media Session
				MsSession mediaSession = msProvider.createSession();
				// Setting Media Session; should be activity but for simplicity :}, this should not be used in production.
				//this.setMediaSession(mediaSession);
				ActivityContextInterface msAci = null;
				try {
					msAci = msActivityFactory.getActivityContextInterface(mediaSession);
					msAci.attach(this.getSbbLocalObject());
				} catch (Exception ex) {
					log.error("Internal server error", ex);
					//getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, request);
					sendServerError(ex.getMessage(), Response.SERVER_INTERNAL_ERROR);
					return;
				}
				
				
				//We have session, lets create Link
				MsLink link=mediaSession.createLink(MsLinkMode.FULL_DUPLEX);

				// Attaching link AC
				//ActivityContextInterface msAci = null;
				try {
					msAci = msActivityFactory.getActivityContextInterface(link);
					msAci.attach(this.getSbbLocalObject());
				} catch (Exception ex) {
					log.error("Internal server error", ex);
					//getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, request);
					sendServerError(ex.getMessage(), Response.SERVER_INTERNAL_ERROR);
					return;
				}
	
				
				//now join
				link.join(IVR_ENDPOINT_NAME, PRE_ENDPOINT_NAME);
			}
			// Voice Mail service disabled
			else {
				response = getMessageFactory().createResponse(Response.TEMPORARILY_UNAVAILABLE, request);
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
		} catch (NullPointerException e) {
			log.error(e.getMessage(), e);
		} catch (UnrecognizedActivityException e) {
			log.error(e.getMessage(), e);
		}
	}

	
	public void onLinkConnected(MsLinkEvent evt, ActivityContextInterface aci) {
		MsLink link = evt.getSource();
		//Link has been estabilished
		log.info("########## VOICE MAIL SBB: Link Connected ["+link.getEndpoints()[0].getLocalName()+"]<====>["+link.getEndpoints()[1].getLocalName()+"] ##########");
        
        String ivrEndpointName = link.getEndpoints()[0].getLocalName();
        String prEndpointName = link.getEndpoints()[1].getLocalName();
        this.setUserIVREndpoint(ivrEndpointName);
        this.setUserPREndpoint(prEndpointName);
        
        //create connection
        MsConnection connection=this.getMediaSession().createNetworkConnection(prEndpointName);
        ActivityContextInterface msAci = null;
		try {
			msAci = msActivityFactory.getActivityContextInterface(connection);
			msAci.attach(this.getSbbLocalObject());
		} catch (Exception ex) {
			log.error("Internal server error", ex);
			//getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, request);
			sendServerError(ex.getMessage(), Response.SERVER_INTERNAL_ERROR);
			return;
		}
        // SDP Description from the request
		String sdp = new String(getServerTransaction().getRequest().getRawContent());
		//log.info("Creating RTP connection [" + ENDPOINT_NAME + "]");
		connection.setMode(MsConnectionMode.SEND_RECV);
		connection.modify("$", sdp);
    }

    public void onLinkDisconnected(MsLinkEvent evt, ActivityContextInterface aci) {
       
        aci.detach(this.getSbbLocalObject());
    }

    public void onLinkFailed(MsLinkEvent evt, ActivityContextInterface aci) {
    	sendServerError("Failed to create link: "+evt.getMessage()+" : "+evt.getCause(), Response.SERVER_INTERNAL_ERROR);
    }
	
    public void onConnectionOpen(MsConnectionEvent evt, ActivityContextInterface aci) {
		MsConnection connection = evt.getConnection();
		//log.info("Created RTP connection [" + connection.getEndpoint() + "]");
		log.info("########## VOICE MAIL SBB: Created RTP connection [" + connection.getEndpoint().getLocalName() + "] ##########");
		MsConnection msConnection = evt.getConnection();
		String sdp = msConnection.getLocalDescriptor();

		ServerTransaction txn = getServerTransaction();
		if (txn == null) {
			log.error("SIP activity lost, close RTP connection");
			msConnection.release();
			return;
		}

		Request request = txn.getRequest();

		ContentTypeHeader contentType = null;
		try {
			contentType = getHeaderFactory().createContentTypeHeader("application", "sdp");
		} catch (ParseException ex) {
		}

		String localAddress = getSipFactoryProvider().getListeningPoints()[0].getIPAddress();
		int localPort = getSipFactoryProvider().getListeningPoints()[0].getPort();

		javax.sip.address.Address contactAddress = null;
		try {
			contactAddress = getAddressFactory().createAddress("sip:" + localAddress + ":" + localPort);
		} catch (ParseException ex) {
			log.error(ex.getMessage(), ex);
		}
		ContactHeader contact = getHeaderFactory().createContactHeader(contactAddress);

		Response response = null;
		try {
			response = getMessageFactory().createResponse(Response.OK, request, contentType, sdp.getBytes());
		} catch (ParseException ex) {
		}

		response.setHeader(contact);
		try {
			txn.sendResponse(response);
		} catch (InvalidArgumentException ex) {
			log.error(ex.getMessage(), ex);
		} catch (SipException ex) {
			log.error(ex.getMessage(), ex);
		}

		//String endpointName = connection.getEndpoint().getLocalName();
		//this.setUserEndpoint(endpointName);

		MsEventFactory eventFactory = msProvider.getEventFactory();
		URL audioFileURL = null;

		if (this.getSameUser()) {

			log.info("same user, lets play the voice mail");
			String audioFile = getAudioFileString();
			File file = null;
			boolean fileExist = false;

			try {
				file = new File(audioFile);
				fileExist = file.exists();
			} catch (NullPointerException npe) {
				// Ignore
			}

			if (fileExist) {
				audioFileURL = getClass().getResource(waitingDTMF);
			} else {
				audioFileURL = getClass().getResource(novoicemessage);
			}

		} else {
			log.debug("not the same user, start recording after announcement");
			audioFileURL = getClass().getResource(recordAfterTone);
		}

		MsPlayRequestedSignal play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);
		play.setURL(audioFileURL.toString());

		MsRequestedEvent onCompleted = eventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
		onCompleted.setEventAction(MsEventAction.NOTIFY);

		MsRequestedEvent onFailed = eventFactory.createRequestedEvent(MsAnnouncement.FAILED);
		onFailed.setEventAction(MsEventAction.NOTIFY);

		MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { play };
		MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] { onCompleted, onFailed };

		
		MsLink link=getLink();
		
		log.info("########## VOICE MAIL SBB: Execute on [" + link.getEndpoints()[0].getLocalName() + "] Connection Endpoint:["+connection.getEndpoint().getLocalName()+"] ##########");
		link.getEndpoints()[0].execute(requestedSignals, requestedEvents, link);

	}
	/**
	 * At any time a SIP Client can send a BYE Request. If the Voice Mail is
	 * being used it will be the VoicemailSbb the one that will send OK
	 * Response.
	 * 
	 * @param event
	 * @param aci
	 */
	public void onByeEvent(RequestEvent event, ActivityContextInterface aci) {
		log.info("########## VOICE MAIL SBB: BYE ##########");
		try {
			TimerID timerID = this.getTimerID();

			// If there is a Timer set we have to cancel it.
			if (timerID != null) {
				timerFacility.cancelTimer(timerID);
			}

			releaseState();

			// Sending the OK Response to the BYE Request received.
			byeRequestOkResponse(event);

		} catch (FactoryException e) {
			log.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {

	}

	

	private void releaseState() {
		ActivityContextInterface[] activities = getSbbContext().getActivities();
		SbbLocalObject sbbLocalObject = getSbbContext().getSbbLocalObject();
		MsConnection msConnection = null;
		MsLink msLink = null;
		for (ActivityContextInterface attachedAci : activities) {
			if (attachedAci.getActivity() instanceof Dialog) {
				attachedAci.detach(sbbLocalObject);
			}
			if (attachedAci.getActivity() instanceof MsConnection) {
				attachedAci.detach(sbbLocalObject);
				msConnection = (MsConnection) attachedAci.getActivity();
			}
			
			if (attachedAci.getActivity() instanceof MsLink) {
				attachedAci.detach(sbbLocalObject);
				msLink = (MsLink) attachedAci.getActivity();
			}
		}
		if (msConnection != null) {
			msConnection.release();
		}
		if (msLink != null) {
			msLink.release();
		}
	}

	private void sendServerError(String message, int errorCode) {
		try {
			Response response = getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR,
					this.getInviteRequest(),getHeaderFactory().createContentTypeHeader("text", "plain"),message.getBytes());
			
			this.getServerTransaction().sendResponse(response);

		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		} catch (SipException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidArgumentException e) {
			log.error(e.getMessage(), e);
		}
		
		if(this.getLink()!=null)
		{
			this.getLink().release();
		}
		
		if(this.getConnection()!=null)
		{
			this.getConnection().release();
		}
	}

	public void onAnnouncementFailed(MsNotifyEvent evt, ActivityContextInterface aci) {
		log.error("Announcement Failed");
		
	}
	public void onAnnouncementComplete(MsNotifyEvent evt, ActivityContextInterface aci) {
		log.info("########## VOICE MAIL SBB: onAnnouncementComplete ##########");
		MsLink link = (MsLink) evt.getSource();
		MsEndpoint ivr = link.getEndpoints()[0];

		if (this.getSameUser()) {

			MsEventFactory factory = msProvider.getEventFactory();
			MsDtmfRequestedEvent dtmf = (MsDtmfRequestedEvent) factory.createRequestedEvent(DTMF.TONE);
			MsRequestedSignal[] signals = new MsRequestedSignal[] {};
			MsRequestedEvent[] events = new MsRequestedEvent[] { dtmf };

			ivr.execute(signals, events, link);
		} else {
			ServerTransaction txn = getServerTransaction();
			Request request = txn.getRequest();

			ToHeader toHeader = (ToHeader) request.getHeader(ToHeader.NAME);
			String fileName = ((SipURI) toHeader.getAddress().getURI()).getUser() + WAV_EXT;

			String recordFilePath = null;

			if (route != null) {
				recordFilePath = route + fileName;
			} else {
				recordFilePath = fileName;
			}

			MsEventFactory eventFactory = msProvider.getEventFactory();
			MsRecordRequestedSignal record = (MsRecordRequestedSignal) eventFactory
					.createRequestedSignal(MsAudio.RECORD);
			record.setFile(recordFilePath);

			MsRequestedEvent onFailed = eventFactory.createRequestedEvent(MsAudio.FAILED);
			onFailed.setEventAction(MsEventAction.NOTIFY);

			MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { record };
			MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] { onFailed };

			ivr.execute(requestedSignals, requestedEvents, link);
		}
	}

	public void onDtmf(MsNotifyEvent evt, ActivityContextInterface aci) {
		log.info("########## VOICE MAIL SBB: onDTMFEvent ##########");

		MsDtmfNotifyEvent event = (MsDtmfNotifyEvent) evt;
		MsLink link = (MsLink) evt.getSource();
		String seq = event.getSequence();
		boolean bye = checkDtmfDigit(seq);
	}

	private MsConnection getConnection() {
		ActivityContextInterface[] activities = this.getSbbContext().getActivities();
		for (int i = 0; i < activities.length; i++) {
			if (activities[i].getActivity() instanceof MsConnection) {
				return (MsConnection) activities[i].getActivity();
			}
		}
		return null;
	}
	
	private MsLink getLink() {
		ActivityContextInterface[] activities = this.getSbbContext().getActivities();
		for (int i = 0; i < activities.length; i++) {
			if (activities[i].getActivity() instanceof MsLink) {
				return (MsLink) activities[i].getActivity();
			}
		}
		return null;
	}

	private MsSession getMediaSession() {
		ActivityContextInterface[] activities = this.getSbbContext().getActivities();
		for (int i = 0; i < activities.length; i++) {
			if (activities[i].getActivity() instanceof MsSession) {
				return (MsSession) activities[i].getActivity();
			}
		}
		return null;
	}
	
	private ActivityContextInterface getConnectionActivityContext() {
		ActivityContextInterface[] activities = getSbbContext().getActivities();
		for (int i = 0; i < activities.length; i++) {
			if (activities[i].getActivity() instanceof MsConnection) {
				return activities[i];
			}
		}
		return null;
	}

	private ServerTransaction getServerTransaction()
	{
		ActivityContextInterface[] activities = this.getSbbContext().getActivities();
		for (int i = 0; i < activities.length; i++) {
			if (activities[i].getActivity() instanceof ServerTransaction) {
				return (ServerTransaction) activities[i].getActivity();
			}
		}
		return null;
	}
	
	private Dialog getDialog()
	{
		ActivityContextInterface[] activities = this.getSbbContext().getActivities();
		for (int i = 0; i < activities.length; i++) {
			if (activities[i].getActivity() instanceof Dialog) {
				return (Dialog) activities[i].getActivity();
			}
		}
		return null;
	}
	
	private void startTimer(int duration) throws NamingException {
		Context ctx = (Context) new InitialContext().lookup("java:comp/env");
		timerFacility = (TimerFacility) ctx.lookup("slee/facilities/timer");

		TimerOptions options = new TimerOptions(false, 1000 * duration, TimerPreserveMissed.NONE);
		Address address = new Address(AddressPlan.IP, "127.0.0.1");
		Date now = new Date();

		timerFacility.setTimer(this.getConnectionActivityContext(), address, now.getTime() + 1000 * duration, options);
	}

	/**
	 * Voice Mail will hang up on caller sending a BYE Request.
	 * 
	 */
	private void sendByeRequest() {
		log.info("########## VOICE MAIL SBB: sendByRequest ##########");
		try {
			SipProvider sipProvider = getSipFactoryProvider();
			Dialog dialog = this.getDialog();
			Request request = dialog.createRequest(Request.BYE);
			ClientTransaction ct = sipProvider.getNewClientTransaction(request);

			dialog.sendRequest(ct);

			releaseState();

		} catch (TransactionUnavailableException e) {
			log.error(e.getMessage(), e);
		} catch (SipException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * After receiving a BYE Request, an OK Respose has to be sent.
	 * 
	 * @param byeEvent
	 */
	private void byeRequestOkResponse(RequestEvent byeEvent) {
		log.info("########## VOICE MAIL SBB: byeRequestOkResponse ##########");
		Request request = byeEvent.getRequest();
		ServerTransaction tx = byeEvent.getServerTransaction();
		try {
			Response response = getMessageFactory().createResponse(Response.OK, request);
			tx.sendResponse(response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private String getAudioFileString() {

		FromHeader fromHeader = (FromHeader) this.getInviteRequest().getHeader(FromHeader.NAME);

		String fileName = ((SipURI) fromHeader.getAddress().getURI()).getUser() + WAV_EXT;

		String recordFilePath = System.getProperty("jboss.server.data.dir") + "/";

		if (route != null) {
			recordFilePath = recordFilePath + route + fileName;
		} else {
			recordFilePath = recordFilePath + fileName;
		}

		log.info("The File to be played = " + recordFilePath);

		return recordFilePath;
	}

	private boolean checkDtmfDigit(String dtmf) {
		URL audioFileURL = null;

		boolean bye = false;

		// Press 1 if you want to listen the next message
		if (dtmf.equals("1")) {
			String filePath = getAudioFileString();
			String audioFileString = "file://" + filePath;

			try {
				// Just to check if file exist
				File file = new File(filePath);
				if (file.exists()) {
					audioFileURL = new URL(audioFileString);
				} else {
					audioFileURL = getClass().getResource(novoicemessage);
				}
			} catch (NullPointerException npe) {
				log.error("Ignore. NullPointerException. The file does not exist " + audioFileString, npe);
				audioFileURL = getClass().getResource(dtmf1);

			} catch (MalformedURLException e1) {
				log.error("Ignore. MalformedURLException while trying to create the audio file URL " + audioFileString,
						e1);
				audioFileURL = getClass().getResource(dtmf1);
			}
		}
		// Press 7 if you want to delete the last message
		else if (dtmf.equals("7")) {
			audioFileURL = getClass().getResource(dtmf7);
			String filePath = null;

			filePath = getAudioFileString();
			File fileToBeDeleted = new File(filePath);
			boolean deleted = fileToBeDeleted.delete();
			log.info("Deletion of file " + filePath + " is successful = " + deleted);

		}
		// Press 9 if you want to hang up
		else if (dtmf.equals("9")) {
			// audioFileURL = getClass().getResource(dtmf9);
			this.sendByeRequest();
			bye = true;

		} else {
			audioFileURL = getClass().getResource(tryAgain);
		}

		if (!bye) {

			MsEventFactory eventFactory = msProvider.getEventFactory();
			MsPlayRequestedSignal play = null;
			play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);
			play.setURL(audioFileURL.toString());

			MsRequestedEvent onCompleted = null;
			MsRequestedEvent onFailed = null;

			onCompleted = eventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
			onCompleted.setEventAction(MsEventAction.NOTIFY);

			onFailed = eventFactory.createRequestedEvent(MsAnnouncement.FAILED);
			onFailed.setEventAction(MsEventAction.NOTIFY);

			MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { play };
			MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] { onCompleted, onFailed };

			log.debug("EXECUTING PLAY");
			//MsConnection connection = this.getConnection();
			//connection.getEndpoint().execute(requestedSignals, requestedEvents, connection);
			MsLink link=getLink();
			if(link!=null)
			{
				link.getEndpoints()[0].execute(requestedSignals, requestedEvents,link); 
			}

		}

		return bye;

	}

	/**
	 * To know whether or not the called user has the Voice Mail service
	 * enabled.
	 * 
	 * @param sipAddress:
	 *            Called user address.
	 * @return boolean: TRUE -> Voice Mail enabled. FALSE -> Voice Mail disabled
	 *         for the given user identified by sip address.
	 */
	private boolean isSubscriber(String sipAddress) {
		boolean state = false;
		CallControlProfileCMP profile = lookup(new Address(AddressPlan.SIP, sipAddress));

		if (profile != null) {
			state = profile.getVoicemailState();
		}

		return state;
	}

	/**
	 * This method is used to know if the it is going to be used the voice mail
	 * of the same user or the voice mail of a different user.
	 * 
	 * @param event
	 * @return TRUE: If the called user is sip:vmail@nist.gov
	 */
	private boolean sameUser(javax.sip.RequestEvent event) {
		boolean sameUser = false;
		Request inviteRequest = event.getRequest();

		// Checking if the called user and the caller are the same
		ToHeader toHeader = (ToHeader) inviteRequest.getHeader(ToHeader.NAME);
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
			msProvider = (MsProvider) myEnv.lookup("slee/resources/media/1.0/provider");
			msActivityFactory = (MediaRaActivityContextInterfaceFactory) myEnv
					.lookup("slee/resources/media/1.0/acifactory");
			// Getting Sip Resource Adaptor interface
			sipACIF = (SipActivityContextInterfaceFactory) myEnv.lookup("slee/resources/jainsip/1.2/acifactory");
			// Getting Timer Facility interface
			timerFacility = (TimerFacility) myEnv.lookup("slee/facilities/timer");

			route = (String) myEnv.lookup("filesRoute");

		} catch (NamingException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
		// Setting DTMF
		this.setDtmf(NON_DIGIT);
	}

	public abstract org.mobicents.slee.examples.callcontrol.profile.CallControlProfileCMP getCallControlProfileCMP(
			javax.slee.profile.ProfileID profileID) throws javax.slee.profile.UnrecognizedProfileNameException,
			javax.slee.profile.UnrecognizedProfileTableNameException;

	public abstract org.mobicents.slee.examples.callcontrol.voicemail.VoiceMailSbbActivityContextInterface asSbbActivityContextInterface(
			ActivityContextInterface aci);

	private final HeaderFactory getHeaderFactory() {
		return headerFactory;
	}

	// Interfaces
	private HeaderFactory headerFactory;

	private SipActivityContextInterfaceFactory sipACIF;
	private TimerFacility timerFacility;

	private final String recordAfterTone = "audiofiles/RecordAfterTone.wav";
	private final String waitingDTMF = "audiofiles/WaitingDTMF.wav";
	private final String dtmf1 = "audiofiles/DTMF1.wav";
	private final String dtmf7 = "audiofiles/DTMF7.wav";
	private final String dtmf9 = "audiofiles/DTMF9.wav";
	private final String tryAgain = "audiofiles/TryAgain.wav";
	private final String novoicemessage = "audiofiles/NoVoiceMessage.wav";

	private final String USER = "vmail";
	private final String HOST = System.getProperty("jboss.bind.address", "127.0.0.1");
	private final String NON_DIGIT = "NULL";
	private final String WAV_EXT = ".wav";

	private MsProvider msProvider;
	private MediaRaActivityContextInterfaceFactory msActivityFactory;

	public final static String IVR_ENDPOINT_NAME = "media/trunk/IVR/$";
	//Pre is required since it has capability to transcode
	public final static String PRE_ENDPOINT_NAME = "media/trunk/PacketRelay/$";
	private String route = null;

	/**
	 * ***************************************** ************** CMP Fields
	 * *************** *****************************************
	 */

	// 'mediaSession' CMP field setter
	//public abstract void setMediaSession(MsSession value);

	// 'mediaSession' CMP field getter
	//public abstract MsSession getMediaSession();

	// 'inviteRequest' CMP field setter
	public abstract void setInviteRequest(Request value);

	// 'inviteRequest' CMP field getter
	public abstract Request getInviteRequest();

	// 'serverTransaction' CMP field setter
	//public abstract void setServerTransaction(ServerTransaction value);

	// 'serverTransaction' CMP field getter
	//public abstract ServerTransaction getServerTransaction();

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

	public abstract String getUserIVREndpoint();

	public abstract void setUserIVREndpoint(String endpointName);
	
	public abstract String getUserPREndpoint();

	public abstract void setUserPREndpoint(String endpointName);
	

}