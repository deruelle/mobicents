/*
 * ***************************************************
 *                                                 *
 *  Mobicents: The Open Source JSLEE Platform      *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************
 */
package org.mobicents.slee.service.user;

import java.text.ParseException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.header.CallIdHeader;
import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.SbbContext;
import javax.slee.UnrecognizedActivityException;
import javax.slee.nullactivity.NullActivity;

import org.apache.log4j.Logger;
import org.mobicents.examples.convergeddemo.seam.pojo.Order;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.events.MsEventAction;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.events.ann.MsPlayRequestedSignal;
import org.mobicents.mscontrol.events.dtmf.MsDtmfNotifyEvent;
import org.mobicents.mscontrol.events.dtmf.MsDtmfRequestedEvent;
import org.mobicents.mscontrol.events.pkg.DTMF;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;
import org.mobicents.slee.resource.tts.ratype.TTSSession;
import org.mobicents.slee.service.callcontrol.CallControlSbbLocalObject;
import org.mobicents.slee.service.common.CommonSbb;
import org.mobicents.slee.service.events.CustomEvent;
import org.mobicents.slee.util.Session;
import org.mobicents.slee.util.SessionAssociation;

/**
 * @author amit bhayani
 */
public abstract class UserSbb extends CommonSbb {

	public final static String ANNOUNCEMENT_ENDPOINT = "media/trunk/Announcement/$";

	private Logger logger = Logger.getLogger(UserSbb.class);

	private EntityManagerFactory emf;

	private MsProvider msProvider;

	private MediaRaActivityContextInterfaceFactory mediaAcif;

	String audioFilePath = null;

	String callerSip = null;

	private final String orderConfirmed = "audio/UserOrderConfirmed.wav";
	private final String orderCancelled = "audio/UserOrderCancelled.wav";
	private final String orderReConfirm = "audio/UserReConfirm.wav";

	/** Creates a new instance of UserSbb */
	public UserSbb() {
		super();
	}

	public void setSbbContext(SbbContext context) {
		super.setSbbContext(context);
		try {

			Context myEnv = (Context) new InitialContext().lookup("java:comp/env");

			audioFilePath = System.getProperty("jboss.server.data.dir");

			callerSip = (String) myEnv.lookup("callerSip");

			msProvider = (MsProvider) myEnv.lookup("slee/resources/media/1.0/provider");
			mediaAcif = (MediaRaActivityContextInterfaceFactory) myEnv.lookup("slee/resources/media/1.0/acifactory");

			InitialContext newIc = new InitialContext();
			emf = (EntityManagerFactory) newIc.lookup("java:/ShoppingDemoSleeEntityManagerFactory");

		} catch (NamingException ne) {
			ne.printStackTrace();
		}
	}

	public abstract void fireOrderCancelled(CustomEvent event, ActivityContextInterface aci, javax.slee.Address address);

	public void onOrderPlaced(CustomEvent event, ActivityContextInterface ac) {
		logger.info("UserSbb: " + this + ": received an ORDER_PLACED event. OrderId = " + event.getOrderId()
				+ ". ammount = " + event.getAmmount() + ". Customer Name = " + event.getCustomerName());

		// Detach from NullActivity
		ac.detach(this.getSbbContext().getSbbLocalObject());

		this.setCustomEvent(event);

		audioFilePath = audioFilePath + "/" + event.getUserName() + ".wav";

		this.setAudioFile(audioFilePath);

		TTSSession ttsSession = getTTSProvider().getNewTTSSession(audioFilePath, "kevin");

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("Welcome ");
		stringBuffer.append(event.getCustomerName());
		stringBuffer.append(". You have placed an order of $");
		stringBuffer.append(event.getAmmount());
		stringBuffer.append(". Press 1 to confirm and 2 to decline.");

		ttsSession.textToAudioFile(stringBuffer.toString());

		try {
			// Set the caller address to the address of our call controller
			Address callerAddress = getSipUtils().convertURIToAddress(callerSip);
			callerAddress.setDisplayName(callerSip);

			// Retrieve the callee addresses from the event
			Address calleeAddress = getSipUtils().convertURIToAddress(event.getCustomerPhone());

			// Build the INVITE request
			Request request = getSipUtils().buildInvite(callerAddress, calleeAddress, null, 1);

			// Create a new transaction based on the generated request
			ClientTransaction ct = getSipProvider().getNewClientTransaction(request);

			Header h = ct.getRequest().getHeader(CallIdHeader.NAME);
			String calleeCallId = ((CallIdHeader) h).getCallId();

			SessionAssociation sa = new SessionAssociation(
					"org.mobicents.slee.service.callcontrol.CallControlSbb$InitialState");

			Session calleeSession = new Session(calleeCallId);
			calleeSession.setSipAddress(calleeAddress);
			calleeSession.setToBeCancelledClientTransaction(ct);

			// The dialog for the client transaction in which the INVITE is sent
			Dialog dialog = ct.getDialog();
			if (dialog != null && logger.isDebugEnabled()) {
				logger.debug("Obtained dialog from ClientTransaction : automatic dialog support on");
			}
			if (dialog == null) {
				// Automatic dialog support turned off
				try {
					dialog = getSipProvider().getNewDialog(ct);
					if (logger.isDebugEnabled()) {
						logger.debug("Obtained dialog for INVITE request to callee with getNewDialog");
					}
				} catch (Exception e) {
					logger.error("Error getting dialog", e);
				}
			}

			// Get activity context from factory
			ActivityContextInterface sipACI = getSipActivityContextInterfaceFactory().getActivityContextInterface(
					dialog);

			ActivityContextInterface clientSipACI = getSipActivityContextInterfaceFactory()
					.getActivityContextInterface(ct);

			if (logger.isDebugEnabled()) {
				logger.debug("Obtained dialog in onThirdPCCTriggerEvent : callId = " + dialog.getCallId().getCallId());
			}
			dialog.terminateOnBye(true);
			calleeSession.setDialog(dialog);
			sa.setCalleeSession(calleeSession);

			/**
			 * Actually callerSession is not required for this example and clean
			 * up is needed
			 */
			Session callerSession = new Session();

			// Create a new caller address from caller URI specified in the
			// event (the real caller address) since we need this in the next
			// INVITE.
			callerAddress = getSipUtils().convertURIToAddress(callerSip);
			callerSession.setSipAddress(callerAddress);
			// Since we don't have the client transaction for the caller yet,
			// just set the to be canceled client transaction to null.
			callerSession.setToBeCancelledClientTransaction(null);
			sa.setCallerSession(callerSession);

			// put the callId for the callee dialog in the cache
			getCacheUtility().put(calleeCallId, sa);

			ChildRelation relation = getCallControlSbbChild();
			// Create child SBB
			CallControlSbbLocalObject child = (CallControlSbbLocalObject) relation.create();

			setChildSbbLocalObject(child);

			child.setParent(getSbbContext().getSbbLocalObject());

			child.setCustomEvent(event);

			// Attach child SBB to the activity context
			sipACI.attach(child);
			clientSipACI.attach(child);
			sipACI.attach(this.getSbbContext().getSbbLocalObject());
			// Send the INVITE request
			ct.sendRequest();

		} catch (ParseException parExc) {
			logger.error("Parse Exception while parsing the callerAddess", parExc);
		} catch (InvalidArgumentException invalidArgExcep) {
			logger.error("InvalidArgumentException while building Invite Request", invalidArgExcep);
		} catch (TransactionUnavailableException tranUnavExce) {
			logger.error("TransactionUnavailableException when trying to getNewClientTransaction", tranUnavExce);
		} catch (UnrecognizedActivityException e) {

			logger.error("UnrecognizedActivityException when trying to getActivityContextInterface", e);
		} catch (CreateException creaExce) {
			logger.error("CreateException while trying to create Child", creaExce);
		} catch (SipException sipExec) {
			logger.error("SipException while trying to send INVITE Request", sipExec);
		}

	}

	public void onLinkDisconnected(MsLinkEvent evt, ActivityContextInterface aci) {
		logger.info("-----onLinkReleased-----");

		if (this.getSendBye()) {
			getChildSbbLocalObject().sendBye();
		}
	}

	public void onAnnouncementComplete(MsNotifyEvent evt, ActivityContextInterface aci) {
		logger.info("Announcement complete: ");
		if (this.getSendBye()) {
			MsLink link = this.getLink();
			link.release();
		}
	}

	public void onDtmf(MsNotifyEvent evt, ActivityContextInterface aci) {
		logger.info("DTMF received");
		MsDtmfNotifyEvent event = (MsDtmfNotifyEvent) evt;
		MsLink link = (MsLink) evt.getSource();
		String seq = event.getSequence();
		handleDtmf(seq, link);
	}

	public void handleDtmf(String cause, MsLink link) {

		EntityManager mgr = null;
		Order order = null;
		boolean successful = false;

		if ("1".equals(cause)) {

			this.setAudioFile((getClass().getResource(orderConfirmed)).toString());

			mgr = emf.createEntityManager();

			order = (Order) mgr.createQuery("select o from Order o where o.orderId = :orderId").setParameter("orderId",
					this.getCustomEvent().getOrderId()).getSingleResult();

			order.setStatus(Order.Status.OPEN);

			mgr.flush();
			mgr.close();

			successful = true;

		} else if ("2".equals(cause)) {
			this.setAudioFile((getClass().getResource(orderCancelled)).toString());

			mgr = emf.createEntityManager();

			order = (Order) mgr.createQuery("select o from Order o where o.orderId = :orderId").setParameter("orderId",
					this.getCustomEvent().getOrderId()).getSingleResult();

			order.setStatus(Order.Status.CANCELLED);

			mgr.flush();
			mgr.close();

			successful = true;

			try {
				NullActivity nullActivity = getNullActivityFactory().createNullActivity();

				ActivityContextInterface nullActivityContextInterface = getNullACIFactory()
						.getActivityContextInterface(nullActivity);

				fireOrderCancelled((CustomEvent) this.getCustomEvent().clone(), nullActivityContextInterface, null);

			} catch (UnrecognizedActivityException unreActExc) {
				unreActExc.printStackTrace();
			}
		} else {
			this.setAudioFile((getClass().getResource(orderReConfirm)).toString());

		}
		this.setSendBye(successful);

		MsEventFactory eventFactory = msProvider.getEventFactory();

		MsPlayRequestedSignal play = null;
		play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);
		play.setURL(this.getAudioFile());

		MsRequestedEvent onCompleted = null;
		MsRequestedEvent onFailed = null;

		onCompleted = eventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
		onCompleted.setEventAction(MsEventAction.NOTIFY);

		onFailed = eventFactory.createRequestedEvent(MsAnnouncement.FAILED);
		onFailed.setEventAction(MsEventAction.NOTIFY);

		MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { play };
		MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] { onCompleted, onFailed };
		link.getEndpoints()[1].execute(requestedSignals, requestedEvents, link);

	}

	public void onLinkConnected(MsLinkEvent evt, ActivityContextInterface aci) {
		logger.info("--------onLinkConnected------------");
		MsLink link = evt.getSource();
		MsEndpoint endpoint = link.getEndpoints()[1];

		MsEventFactory eventFactory = msProvider.getEventFactory();

		MsPlayRequestedSignal play = null;
		play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);

		String announcementFile = "file:" + this.getAudioFile();
		play.setURL(announcementFile);

		MsRequestedEvent onCompleted = null;
		MsRequestedEvent onFailed = null;

		onCompleted = eventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
		onCompleted.setEventAction(MsEventAction.NOTIFY);

		onFailed = eventFactory.createRequestedEvent(MsAnnouncement.FAILED);
		onFailed.setEventAction(MsEventAction.NOTIFY);

		MsDtmfRequestedEvent dtmf = (MsDtmfRequestedEvent) eventFactory.createRequestedEvent(DTMF.TONE);

		MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { play };
		MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] { onCompleted, onFailed, dtmf };

		endpoint.execute(requestedSignals, requestedEvents, link);

	}

	public MsLink getLink() {
		ActivityContextInterface[] activities = getSbbContext().getActivities();
		for (int i = 0; i < activities.length; i++) {
			if (activities[i].getActivity() instanceof MsLink) {
				return (MsLink) activities[i].getActivity();
			}
		}
		return null;
	}

	// child relation
	public abstract ChildRelation getCallControlSbbChild();

	public abstract void setCustomEvent(CustomEvent customEvent);

	public abstract CustomEvent getCustomEvent();

	public abstract void setSendBye(boolean isBye);

	public abstract boolean getSendBye();

	public abstract void setAudioFile(String endPoint);

	public abstract String getAudioFile();

	public abstract void setChildSbbLocalObject(CallControlSbbLocalObject childSbbLocalObject);

	public abstract CallControlSbbLocalObject getChildSbbLocalObject();
}
