package org.mobicents.slee.service.user.delivery;

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
import javax.slee.InitialEventSelector;
import javax.slee.SbbContext;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
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
 * 
 * @author amit.bhayani
 * 
 */
public abstract class OrderDeliverDateSbb extends CommonSbb {

	private Logger logger = Logger.getLogger(OrderDeliverDateSbb.class);

	private EntityManagerFactory emf;

	private MsProvider msProvider;

	private MediaRaActivityContextInterfaceFactory mediaAcif;

	private TimerFacility timerFacility = null;

	private final String orderDeliveryDate = "audio/UserOrderDeliveryDate.wav";

	String audioFilePath = null;

	String callerSip = null;

	/** Creates a new instance of UserSbb */
	public OrderDeliverDateSbb() {
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

			timerFacility = (TimerFacility) myEnv.lookup("slee/facilities/timer");

			InitialContext newIc = new InitialContext();
			emf = (EntityManagerFactory) newIc.lookup("java:/ShoppingDemoSleeEntityManagerFactory");

		} catch (NamingException ne) {
			ne.printStackTrace();
		}
	}

	public void onOrderCancelled(CustomEvent event, ActivityContextInterface ac) {
		logger.info("======== OrderDeliverDateSbb ORDER_CANCELLED ========");
		if (this.getTimerID() != null) {
			timerFacility.cancelTimer(this.getTimerID());
		}
		ActivityContextInterface[] activities = getSbbContext().getActivities();
		for (int i = 0; i < activities.length; i++) {
			if (activities[i].getActivity() instanceof NullActivity) {
				activities[i].detach(this.getSbbContext().getSbbLocalObject());
			}
		}
	}

	public void onOrderApproved(CustomEvent event, ActivityContextInterface ac) {
		logger.info("======== OrderDeliverDateSbb ORDER_APPROVED ========");
		this.setCustomEvent(event);
		setTimer(ac, 30000);
	}

	public void onOrderProcessed(CustomEvent event, ActivityContextInterface ac) {
		logger.info("======== OrderDeliverDateSbb ORDER_PROCESSED ========");
		this.setCustomEvent(event);
		setTimer(ac, 30000);
	}

	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		logger.info("Timer fired calling makeCall");

		// Detach NullActivity
		aci.detach(this.getSbbContext().getSbbLocalObject());
		makeCall();

	}

	private void setTimer(ActivityContextInterface ac, int duration) {
		TimerOptions options = new TimerOptions();
		options.setPersistent(true);

		// Set the timer on ACI
		TimerID timerID = this.timerFacility.setTimer(ac, null, System.currentTimeMillis() + duration, options);

		this.setTimerID(timerID);
	}

	private void makeCall() {

		CustomEvent event = this.getCustomEvent();
		this.setDateAndTime("");

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

			if (logger.isDebugEnabled()) {
				logger.debug("Obtained dialog in onThirdPCCTriggerEvent : callId = " + dialog.getCallId().getCallId());
			}
			// Get activity context from factory
			ActivityContextInterface sipACI = getSipActivityContextInterfaceFactory().getActivityContextInterface(
					dialog);

			ActivityContextInterface clientSipACI = getSipActivityContextInterfaceFactory()
					.getActivityContextInterface(ct);

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
			// just set the to be cancelled client transaction to null.
			callerSession.setToBeCancelledClientTransaction(null);
			sa.setCallerSession(callerSession);

			// put the callId for the callee dialog in the cache
			getCacheUtility().put(calleeCallId, sa);

			ChildRelation relation = getCallControlSbbChild();
			// Create child SBB
			CallControlSbbLocalObject child = (CallControlSbbLocalObject) relation.create();

			setChildSbbLocalObject(child);

			child.setParent(getSbbContext().getSbbLocalObject());

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
			// TODO Auto-generated catch block
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

	public void onLinkConnected(MsLinkEvent evt, ActivityContextInterface aci) {
		logger.info("--------onLinkCreated------------");

		MsLink link = evt.getSource();
		MsEndpoint endpoint = link.getEndpoints()[1];

		MsEventFactory eventFactory = msProvider.getEventFactory();

		MsPlayRequestedSignal play = null;
		play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);

		String announcementFile = (getClass().getResource(orderDeliveryDate)).toString();
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

	public void onDtmf(MsNotifyEvent evt, ActivityContextInterface aci) {
		logger.info("DTMF received");

		MsDtmfNotifyEvent event = (MsDtmfNotifyEvent) evt;
		MsLink link = (MsLink) evt.getSource();
		MsEndpoint endpoint = link.getEndpoints()[1];
		String seq = event.getSequence();

		boolean success = false;

		String dateAndTime = this.getDateAndTime();

		if ("0".equals(seq)) {
			dateAndTime = dateAndTime + "0";
		} else if ("1".equals(seq)) {

			dateAndTime = dateAndTime + "1";
		} else if ("2".equals(seq)) {
			dateAndTime = dateAndTime + "2";
		} else if ("3".equals(seq)) {
			dateAndTime = dateAndTime + "3";
		} else if ("4".equals(seq)) {
			dateAndTime = dateAndTime + "4";
		} else if ("5".equals(seq)) {
			dateAndTime = dateAndTime + "5";
		} else if ("6".equals(seq)) {
			dateAndTime = dateAndTime + "6";
		} else if ("7".equals(seq)) {
			dateAndTime = dateAndTime + "7";
		} else if ("8".equals(seq)) {
			dateAndTime = dateAndTime + "8";
		} else if ("9".equals(seq)) {
			dateAndTime = dateAndTime + "9";
		} else {
			logger.warn("Couldn't understand DTMF = " + seq);
		}

		// TODO: Add logic to check if date and time is valid. We assume that
		// user is well educated and will always punch right date and time

		if (dateAndTime.length() == 10) {

			EntityManager mgr = null;
			Order order = null;

			StringBuffer audioPath = new StringBuffer(audioFilePath);
			audioPath.append("/");
			audioPath.append(this.getCustomEvent().getUserName());
			audioPath.append(".wav");

			TTSSession ttsSession = getTTSProvider().getNewTTSSession(audioPath.toString(), "kevin");

			char[] c = dateAndTime.toCharArray();

			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("You have selected delivery date to be ");

			String date = "" + c[0] + c[1];
			int iDate = (new Integer(date)).intValue();
			stringBuffer.append(iDate);

			String month = "" + c[2] + c[3];
			int iMonth = (new Integer(month)).intValue();

			String year = "" + c[4] + c[5];
			int iYear = (new Integer(year)).intValue();

			String hour = "" + c[6] + c[7];
			int iHour = (new Integer(hour)).intValue();

			String min = "" + c[8] + c[9];
			int iMin = (new Integer(min)).intValue();

			switch (iMonth) {
			case 1:
				month = "January";
				break;
			case 2:
				month = "February";
				break;
			case 3:
				month = "March";
				break;
			case 4:
				month = "April";
				break;
			case 5:
				month = "May";
				break;
			case 6:
				month = "June";
				break;
			case 7:
				month = "July";
				break;
			case 8:
				month = "August";
				break;
			case 9:
				month = "September";
				break;
			case 10:
				month = "October";
				break;
			case 11:
				month = "November";
				break;
			case 12:
				month = "December";
				break;
			default:
				break;
			}
			stringBuffer.append(" of ");
			stringBuffer.append(month);
			stringBuffer.append(" ");
			stringBuffer.append(2000 + iYear);
			stringBuffer.append(" at ");
			stringBuffer.append(iHour);
			stringBuffer.append(" hour and ");
			stringBuffer.append(iMin);
			stringBuffer.append(" minute. Thank you. Bye.");

			java.sql.Timestamp timeStamp = new java.sql.Timestamp((iYear + 100), iMonth - 1, iDate, iHour, iMin, 0, 0);

			mgr = emf.createEntityManager();

			order = (Order) mgr.createQuery("select o from Order o where o.orderId = :orderId").setParameter("orderId",
					this.getCustomEvent().getOrderId()).getSingleResult();

			order.setDeliveryDate(timeStamp);

			mgr.flush();
			mgr.close();

			ttsSession.textToAudioFile(stringBuffer.toString());

			MsEventFactory eventFactory = msProvider.getEventFactory();

			MsPlayRequestedSignal play = null;
			play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);
			play.setURL("file:" + audioPath.toString());

			MsRequestedEvent onCompleted = null;
			MsRequestedEvent onFailed = null;

			onCompleted = eventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
			onCompleted.setEventAction(MsEventAction.NOTIFY);

			onFailed = eventFactory.createRequestedEvent(MsAnnouncement.FAILED);
			onFailed.setEventAction(MsEventAction.NOTIFY);

			MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { play };
			MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] { onCompleted, onFailed };

			endpoint.execute(requestedSignals, requestedEvents, link);

			success = true;
			this.setSendBye(success);

		} else {
			this.setDateAndTime(dateAndTime);

			MsEventFactory factory = msProvider.getEventFactory();
			MsDtmfRequestedEvent dtmf = (MsDtmfRequestedEvent) factory.createRequestedEvent(DTMF.TONE);
			MsRequestedSignal[] signals = new MsRequestedSignal[] {};
			MsRequestedEvent[] events = new MsRequestedEvent[] { dtmf };

			endpoint.execute(signals, events, link);

		}
	}

	public InitialEventSelector orderIdSelect(InitialEventSelector ies) {
		Object event = ies.getEvent();
		long orderId = 0;
		if (event instanceof CustomEvent) {
			orderId = ((CustomEvent) event).getOrderId();
		} else {
			// If something else, use activity context.
			ies.setActivityContextSelected(true);
			return ies;
		}
		// Set the convergence name
		if (logger.isDebugEnabled()) {
			logger.debug("Setting convergence name to: " + orderId);
		}
		ies.setCustomName(String.valueOf(orderId));
		return ies;
	}// child relation

	public abstract ChildRelation getCallControlSbbChild();

	public abstract void setCustomEvent(CustomEvent customEvent);

	public abstract CustomEvent getCustomEvent();

	public abstract void setSendBye(boolean isBye);

	public abstract boolean getSendBye();

	public abstract void setDateAndTime(String dateAndTime);

	public abstract String getDateAndTime();

	// 'timerID' CMP field setter
	public abstract void setTimerID(TimerID value);

	// 'timerID' CMP field getter
	public abstract TimerID getTimerID();

	public abstract void setChildSbbLocalObject(CallControlSbbLocalObject childSbbLocalObject);

	public abstract CallControlSbbLocalObject getChildSbbLocalObject();

}
