/*
 * AppSbb.java
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.slee.service.rules.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;

import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ratype.ConnectionEvent;
import org.mobicents.slee.resource.media.ratype.IVRContext;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;
import org.mobicents.slee.resource.media.ratype.MediaProvider;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;
import org.mobicents.slee.resource.media.ratype.RtpMediaConnection;
import org.mobicents.slee.resource.rules.ra.CallFactImpl;
import org.mobicents.slee.resource.rules.ratype.CallFact;
import org.mobicents.slee.resource.rules.ratype.RulesProvider;
import org.mobicents.slee.resource.rules.ratype.RulesSession;
import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipFactoryProvider;
import org.mobicents.slee.service.rules.util.SipUtils;
import org.mobicents.slee.service.rules.util.SipUtilsFactorySingleton;

/**
 * 
 * @author amit.bhayani
 */
public abstract class AppSbb implements Sbb {

	private Logger logger = Logger.getLogger(AppSbb.class);

	private SbbContext sbbContext;

	private SipFactoryProvider sipFactoryProvider;

	private SipProvider sipProvider;

	private AddressFactory addressFactory;

	private HeaderFactory headerFactory;

	private MessageFactory messageFactory;

	private SipActivityContextInterfaceFactory sipAcif;

	private MediaProvider mediaProvider;

	private MediaRaActivityContextInterfaceFactory mediaAcif;

	private IVRContext mediaContext;

	private ActivityContextNamingFacility namingFacility;

	private RulesProvider rulesProvider;

	private TimerFacility timerFacility;

	private final String NON_DIGIT = "NULL";

	private final String WAV_EXT = ".wav";

	private SipUtils sipUtils;

	/**
	 * Creates a new instance of IvrSbb
	 */
	public AppSbb() {
	}

	private void executeRule() {
		logger.debug("executeRule()");
		System.out.println("executeRule()");

		// Calling in Synch way
		List inObjects = new ArrayList();
		CallFact callFact = this.getCallFact();
		inObjects.add(callFact);

		RulesSession rulesActivity = this.getRulesActivity();

		List result = rulesActivity.executeRules(inObjects);

		if (result != null && result.size() != 0) {

			String action = (String) result.get(0);
			logger.debug("Fact asserted. The consequences are ACTION = "
					+ result.get(0));

			System.out.println("Fact asserted. The consequences are ACTION = "
					+ result.get(0));

			if (action != null && action.equals("FORWARDTO")) {
				// Logic to Forward the call

				String forwardTo = (String) result.get(1);
				logger.debug("Forward the call to" + forwardTo);
				// Logic for forwarding the call

				Address callerAddress = sipUtils
						.convertURIToAddress("sip:callcontrol@nist.gov");

				try {

					callerAddress.setDisplayName(this.getCallerAddress());

					Address calleeAddress = sipUtils
							.convertURIToAddress("sip:abhayani@127.0.0.1");

					RequestEvent inviteRequestEvent = this.getRequestEvent();
					Request inviteRequest = inviteRequestEvent.getRequest();
					CallIdHeader callIdHeader = (CallIdHeader) inviteRequest
							.getHeader(CallIdHeader.NAME);

					Request request = sipUtils.buildInvite(callerAddress,
							calleeAddress, null, 1, callIdHeader);
					ClientTransaction ct = sipProvider
							.getNewClientTransaction(request);

					// Get activity context from factory
					ActivityContextInterface ac = sipAcif
							.getActivityContextInterface(ct);

					Dialog dialog = ct.getDialog();
					if (dialog != null && logger.isDebugEnabled()) {
						logger
								.debug("Obtained dialog from ClientTransaction : automatic dialog support on");
					}
					if (dialog == null) {
						// Automatic dialog support turned off
						try {
							dialog = sipProvider.getNewDialog(ct);
							if (logger.isDebugEnabled()) {
								logger
										.debug("Obtained dialog for INVITE request to callee with getNewDialog");
							}
						} catch (Exception e) {
							logger.error("Error getting dialog", e);
						}
					}
					if (logger.isDebugEnabled()) {
						logger
								.debug("Obtained dialog in onThirdPCCTriggerEvent : callId = "
										+ dialog.getCallId().getCallId());
					}

					this.setCalleeDialog(dialog);

					ac.attach(getSbbLocalObject());

					ct.sendRequest();

				} catch (ParseException parseExc) {
					logger.error("Parsing Exception in AppSbb", parseExc);
				} catch (InvalidArgumentException invalidExc) {
					logger.error(
							"InvalidArgumentException Exception in AppSbb",
							invalidExc);
				} catch (TransactionUnavailableException trxUnavailableExce) {
					logger
							.error(
									"TransactionUnavailableException Exception in AppSbb",
									trxUnavailableExce);
				} catch (UnrecognizedActivityException unrecActExc) {
					logger.error("UnrecognizedActivityException in AppSbb",
							unrecActExc);
				} catch (SipException sipExc) {
					logger.error("SipException in AppSbb", sipExc);
				}

			} else if (action != null && action.equals("PLAY")) {
				// Logic for Playing Audio File

				URL fileRcv = null;

				// The name of the audio file is also acting as Sub Menu
				String audioFile = (String) result.get(1);
				logger.debug("Play the audio file" + audioFile);

				boolean dtmf = false;

				try {
					dtmf = Boolean.parseBoolean((String) result.get(2));
				} catch (Exception ex) {
					logger.error("Some problem while parsing the dtmf digit.",
							ex);
				}

				// Set the subMenu to the one received from .xls
				this.getCallFact().setSubMenu(audioFile);

				// set the DTMF to NON_DIGIT again
				this.getCallFact().setDtmf(NON_DIGIT);

				URL audioFileURL;
				String audioFilePath = this.getAudioFile(audioFile);

				logger.debug("Audio file to play = " + audioFilePath);

				audioFileURL = getClass().getResource(audioFilePath);

				if (audioFileURL == null) {
					logger.warn("Cannot find the AudioFile " + audioFilePath);
				}

				mediaContext.play(audioFileURL);

				return;
			} else if (action != null && action.equals("RECORDING")) {

				URL fileRcv = null;

				long time = System.currentTimeMillis();
				FromHeader fromHeader = (FromHeader) this.getRequestEvent()
						.getRequest().getHeader(FromHeader.NAME);

				String fileName = ((SipURI) fromHeader.getAddress().getURI())
						.getUser();

				String route = null;

				try {
					Context initCtx = new InitialContext();
					Context myEnv = (Context) initCtx.lookup("java:comp/env");

					route = (String) myEnv.lookup("filesRoute");

					String fileRoute = route + fileName;

					if (fileRoute != null)
						fileRcv = new URL(fileRoute);

					mediaContext.record(fileRcv);

				} catch (NamingException e) {
					logger.error(
							"Error while lookingup the filename to record", e);
				} catch (MalformedURLException malExc) {
					logger.error("Exception while Recording", malExc);

				}

				return;

			} else if (action != null && action.equals("HANGUP")) {
				sendByeRequest();

				String callId = this.getCallId();
				endMediaContext(callId);
				return;

			}

		} else {
			// Logic to just execute the call
			logger
					.debug("None of Rules matched. Probably user is not calling sip:08055555@nist.gov. Just execute the call");
			System.out
					.println("None of Rules matched. Probably user is not calling sip:08055555@nist.gov. Just execute the call");
		}

	}

	public void onInviteEvent(RequestEvent evt, ActivityContextInterface aci) {
		setRequestEvent(evt);
		logger.info("Incoming call AppSbb"
				+ evt.getRequest().getHeader(FromHeader.NAME));

		this.setForwardingState("UNINITIALIZED");

		try {

			createDialog(evt);
			sendResponse(evt, Response.TRYING);
			sendResponse(evt, Response.RINGING);
			createRulesSession(evt);
			createMediaConnection(evt);

		} catch (Exception e) {
			logger.error("Failed to create media connection:", e);
			sendResponse(evt, Response.SERVER_INTERNAL_ERROR);
		}
	}

	public void onConnectionConnecting(ConnectionEvent evt,
			ActivityContextInterface aci) {
		System.out.println("------------AppSbb CONNECTING ------------");
		RtpMediaConnection connection = (RtpMediaConnection) evt
				.getConnection();

		mediaContext = (IVRContext) connection.getMediaContext();

		logger.info("AppSbb Allocated media context: " + mediaContext);

		String sdp = connection.getLocalDescriptor();

		Request request = getRequestEvent().getRequest();
		ServerTransaction tx = getRequestEvent().getServerTransaction();

		try {
			ContentTypeHeader contentType = headerFactory
					.createContentTypeHeader("application", "sdp");

			String localAddress = sipProvider.getListeningPoints()[0]
					.getIPAddress();
			int localPort = sipProvider.getListeningPoints()[0].getPort();

			Address contactAddress = addressFactory.createAddress("sip:"
					+ localAddress + ":" + localPort);
			ContactHeader contact = headerFactory
					.createContactHeader(contactAddress);
			Response response = messageFactory.createResponse(Response.OK,
					request, contentType, sdp.getBytes());
			response.setHeader(contact);
			tx.sendResponse(response);

			ActivityContextInterface contextAci = mediaAcif
					.getActivityContextInterface(mediaContext);
			contextAci.attach(sbbContext.getSbbLocalObject());
		} catch (Exception e) {
			logger.error("Unexpected error: ", e);
			sendResponse(getRequestEvent(), Response.SERVER_INTERNAL_ERROR);
			connection.release();
		}
	}

	public void onConnectionConnected(ConnectionEvent evt,
			ActivityContextInterface aci) {
		System.out.println("------------AppSbb CONNECTED ------------");
		executeRule();

	}

	public void onContextStarted(MediaContextEvent evt,
			ActivityContextInterface aci) {
		logger.info("Media Context started");
	}

	public void onContextFailed(MediaContextEvent evt,
			ActivityContextInterface aci) {
		logger.info("Media Context failed. Cause: " + evt.getCause());
	}

	public void onDtmfEvent(ConnectionEvent event, ActivityContextInterface aci) {
		TimerID timerID = this.getTimerID();

		// If there is a Timer set we have to cancel it.
		if (timerID != null) {
			timerFacility.cancelTimer(timerID);
		}

		if (mediaContext.getState() == MediaContext.STARTED) {
			mediaContext.stop();
		}

		int cause = event.getCause();

		this.getCallFact().setDtmf(Integer.toString(cause));
		executeRule();
	}

	public void onContextStopped(MediaContextEvent evt,
			ActivityContextInterface aci) {
		logger.info("Media Context stoped. Cause: " + evt.getCause());
		setTimer("waitingDTMF", aci);
	}

	public void onByeEvent(RequestEvent evt, ActivityContextInterface aci) {
		logger.info("---- BYE-----");
		try {

			TimerID timerID = this.getTimerID();

			// If there is a Timer set we have to cancel it.
			if (timerID != null) {
				timerFacility.cancelTimer(timerID);
			}

			String callId = getCallIdFromEvent(evt);
			endMediaContext(callId);

			Response response = messageFactory.createResponse(Response.OK, evt
					.getRequest());
			evt.getServerTransaction().sendResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void endMediaContext(String callId) {
		ActivityContextInterface[] activities = sbbContext.getActivities();
		for (int i = 0; i < activities.length; i++) {
			Object activity = activities[i].getActivity();
			if (activity instanceof MediaConnection) {
				((MediaConnection) activity).release();
				logger.info("Media connection released");
			}
		}
	}

	private void createDialog(RequestEvent evt) {

		try {
			ServerTransaction st = evt.getServerTransaction();
			this.setServerTransaction(st);

			// Attaching to SIP Dialog activity
			Dialog dial = sipProvider.getNewDialog((Transaction) st);

			ActivityContextInterface dialogAci = sipAcif
					.getActivityContextInterface(dial);
			SbbLocalObject slo = this.getSbbLocalObject();
			// attach this SBB object to the Dialog activity to receive
			// subsequent events on this Dialog
			dialogAci.attach(slo);
			this.setCallerDialog(dial);
			logger.info("AppSbb Creation of Dialog Successful");
		} catch (SipException e) {
			logger.error("Creation of Dailog failed " + e.getMessage(), e);
		} catch (UnrecognizedActivityException uactivity) {
			logger.error("Creation of Dialog failed " + uactivity.getMessage(),
					uactivity);
			System.out.println("Creation of Dialog failed " + uactivity);

		}

	}

	private void sendResponse(RequestEvent evt, int cause) {
		Request request = evt.getRequest();
		ServerTransaction tx = evt.getServerTransaction();

		try {
			Response response = messageFactory.createResponse(cause, request);
			tx.sendResponse(response);
		} catch (ParseException e) {
			logger.warn("Invalid request: " + request);
		} catch (SipException e) {
			logger.error("Unexpected error:", e);
		} catch (InvalidArgumentException e) {
			// should never happen
		}
	}

	private String getCallIdFromEvent(RequestEvent evt) {
		return evt.getRequest().getHeader(CallIdHeader.NAME).toString();
	}

	private void createMediaConnection(RequestEvent evt)
			throws ClassNotFoundException {
		String callID = getCallIdFromEvent(evt);
		this.setCallId(callID);
		System.out.println("-----AppSbb CALL-ID: " + callID);

		Request request = evt.getRequest();
		byte[] data = request.getRawContent();

		String sdp = new String(data);
		RtpMediaConnection connection = null;

		connection = (RtpMediaConnection) mediaProvider
				.createConnection("org.mobicents.slee.resource.media.ra.rtp.RtpMediaConnectionImpl");

		try {
			ActivityContextInterface aci = mediaAcif
					.getActivityContextInterface(connection);
			aci.attach(sbbContext.getSbbLocalObject());
		} catch (Exception e) {
			e.printStackTrace();
		}

		connection.setRemoteDescriptor(sdp);
		connection.init("IVR");

	}

	private void createRulesSession(RequestEvent evt) {

		Request request = evt.getRequest();

		FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);

		ToHeader toHeader = (ToHeader) request.getHeader(ToHeader.NAME);

		// From URI
		URI fromURI = fromHeader.getAddress().getURI();
		// To URI
		URI toURI = toHeader.getAddress().getURI();

		// In the Profile Table the port is not used
		((SipURI) fromURI).removePort();
		((SipURI) toURI).removePort();

		logger.debug("fromURI = " + fromURI.toString() + " toURI "
				+ toURI.toString());

		this.setCallerAddress(fromURI.toString());

		CallFact callFact = new CallFactImpl(fromURI.toString(), toURI
				.toString());

		this.setCallFact(callFact);

		RulesSession rulesActivity = rulesProvider
				.getNewRulesSession("MobicentsCallFlow.xls");

		this.setRulesActivity(rulesActivity);
		logger.info("Creation of rulesActivity successfull");

	}

	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		// Timer Event is fired in 2 cases after waiting a number of secs:
		// * After being waiting to receive DTMF digit and do not receive any.
		// * After being recording audio from an UA without receiving a BYE
		// Request.
		logger.info("########## AppSBB: onTimerEvent ##########");
		executeRule();

	}

	public void onSuccessRespEvent(ResponseEvent event,
			ActivityContextInterface aci) {
		System.out.println("########## AppSBB: onSuccessRespEvent ##########");

		if (this.getForwardingState().equals("UNINITIALIZED")) {
			Response response = event.getResponse();
			int status = response.getStatusCode();

			System.out.println("Status Code = " + status);

			Object content = event.getResponse().getContent();
			String contentString = null;
			if (content instanceof byte[]) {
				contentString = new String((byte[]) content);

			} else if (content instanceof String) {
				contentString = (String) content;
			}

			System.out.println("SDPOffer = " + contentString);

			final byte[] sdpOffer = contentString.getBytes();

			String callerAddressStr = this.getCallerAddress();
			Address callerAddress = sipUtils
					.convertURIToAddress(callerAddressStr);

			Address callControllerAddress = sipUtils
					.convertURIToAddress("sip:callcontrol@nist.gov");

			try {

				callControllerAddress.setDisplayName("sip:abhayani@127.0.0.1");

				RequestEvent requestEvent = this.getRequestEvent();
				Request inviteRequest = requestEvent.getRequest();
				CallIdHeader callIdHeader = (CallIdHeader) inviteRequest
						.getHeader(CallIdHeader.NAME);

				Request request = sipUtils.buildInvite(callControllerAddress,
						callerAddress, sdpOffer, 1, callIdHeader);
				ClientTransaction ct = sipProvider
						.getNewClientTransaction(request);

				Dialog callerDialog = this.getCallerDialog();

				System.out.println("Caller Dialog Local Party Display Name"
						+ callerDialog.getLocalParty().getDisplayName());
				
				System.out.println("Caller Dialog Local Party URI"
						+ callerDialog.getLocalParty().getURI());				
				
				System.out.println("Caller Dialog Remote Party Display Name"
						+ callerDialog.getRemoteParty().getDisplayName());
				
				System.out.println("Caller Dialog Remote Party URI"
						+ callerDialog.getRemoteParty().getURI());								

				callerDialog.sendRequest(ct);

				this.setForwardingState("INITIALIZED");

				System.out.println("REINVITE sent");

			} catch (Exception ex) {
				logger.error("Exception in onSuccessRespEvent in AppSbb", ex);
			}
		} else {
			System.out.println("REINVITE is successful lets send ACK");

			try {
				Dialog dialog = sipUtils.getDialog(event);
				Request ackRequest = sipUtils.buildAck(dialog, null);
				dialog.sendAck(ackRequest);
			} catch (SipException e) {
				e.printStackTrace();
			}

			try {
				ClientTransaction ct = event.getClientTransaction();
				final String callerCallId = ((CallIdHeader) ct.getRequest()
						.getHeader(CallIdHeader.NAME)).getCallId();

				Dialog calleeDialog = this.getCalleeDialog();
				Object content = event.getResponse().getContent();
				Request ackRequest = sipUtils.buildAck(calleeDialog, content);
				calleeDialog.sendAck(ackRequest);

			} catch (SipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void setTimer(String waitingTimeFromJndi,
			ActivityContextInterface aci) {
		TimerOptions options = new TimerOptions();
		options.setPersistent(true);
		TimerID timerID;
		long waitingTime;

		try {
			Context initCtx = new InitialContext();
			Context myEnv = (Context) initCtx.lookup("java:comp/env");

			waitingTime = ((Long) myEnv.lookup(waitingTimeFromJndi))
					.longValue();

			timerID = this.timerFacility.setTimer(aci, null, System
					.currentTimeMillis()
					+ waitingTime, options);

			// Setting Timer ID
			this.setTimerID(timerID);

		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Voice Mail will hang up on caller sending a BYE Request.
	 * 
	 */
	private void sendByeRequest() {
		logger.info("########## AppSBB: sendByRequest ##########");
		try {

			Dialog dialog = this.getServerTransaction().getDialog();
			Request request = dialog.createRequest(Request.BYE);
			ClientTransaction ct = sipProvider.getNewClientTransaction(request);

			dialog.sendRequest(ct);

		} catch (TransactionUnavailableException e) {
			logger.error(e.getMessage(), e);
		} catch (SipException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String getAudioFile(String fileName) {
		return "audiofiles/" + fileName + this.WAV_EXT;
	}

	public abstract RequestEvent getRequestEvent();

	public abstract void setRequestEvent(RequestEvent evt);

	public abstract void setCallFact(CallFact callFact);

	public abstract CallFact getCallFact();

	public abstract RulesSession getRulesActivity();

	public abstract void setRulesActivity(RulesSession rulesActivity);

	public abstract void setServerTransaction(ServerTransaction value);

	public abstract ServerTransaction getServerTransaction();

	// 'timerID' CMP field setter
	public abstract void setTimerID(TimerID value);

	// 'timerID' CMP field getter
	public abstract String getCallId();

	// 'CallId' CMP field setter
	public abstract void setCallId(String value);

	// 'CallId' CMP field getter
	public abstract TimerID getTimerID();

	public abstract Dialog getCallerDialog();

	public abstract void setCallerDialog(Dialog dialog);

	public abstract Dialog getCalleeDialog();

	public abstract void setCalleeDialog(Dialog dialog);

	public abstract String getCallerAddress();

	public abstract void setCallerAddress(String address);

	public abstract String getForwardingState();

	public abstract void setForwardingState(String state);

	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
		try {
			logger.info("Called setSbbContext PRBT!!!");

			Context ctx = (Context) new InitialContext()
					.lookup("java:comp/env");
			namingFacility = (ActivityContextNamingFacility) ctx
					.lookup("slee/facilities/activitycontextnaming");

			// initialize SIP API
			sipFactoryProvider = (SipFactoryProvider) ctx
					.lookup("slee/resources/jainsip/1.2/provider");
			sipProvider = sipFactoryProvider.getSipProvider();
			addressFactory = sipFactoryProvider.getAddressFactory();
			headerFactory = sipFactoryProvider.getHeaderFactory();
			messageFactory = sipFactoryProvider.getMessageFactory();
			sipAcif = (SipActivityContextInterfaceFactory) ctx
					.lookup("slee/resources/jainsip/1.2/acifactory");

			// initilize Media API
			mediaProvider = (MediaProvider) ctx
					.lookup("slee/resources/media/1.0/provider");
			mediaAcif = (MediaRaActivityContextInterfaceFactory) ctx
					.lookup("slee/resources/media/1.0/acifactory");

			// initialize Rules API
			rulesProvider = (RulesProvider) ctx
					.lookup("slee/resources/rulesra/1.0/sbb2ra");

			// Getting Timer Facility interface
			timerFacility = (TimerFacility) ctx.lookup("slee/facilities/timer");

			sipUtils = SipUtilsFactorySingleton.getInstance().getSipUtils();

		} catch (Exception ne) {
			logger.error("Could not set SBB context:", ne);
		}
	}

	private final SbbLocalObject getSbbLocalObject() {

		SbbLocalObject sbbLocal = this.sbbContext.getSbbLocalObject();
		return sbbLocal;
	}

	public void unsetSbbContext() {
	}

	public void sbbCreate() throws CreateException {
	}

	public void sbbPostCreate() throws CreateException {
	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbRemove() {
	}

	public void sbbExceptionThrown(Exception exception, Object object,
			ActivityContextInterface activityContextInterface) {
	}

	public void sbbRolledBack(RolledBackContext rolledBackContext) {
	}

}
