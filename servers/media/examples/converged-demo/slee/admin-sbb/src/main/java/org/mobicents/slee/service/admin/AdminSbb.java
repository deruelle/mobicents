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
package org.mobicents.slee.service.admin;

import java.text.ParseException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
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
 * @author amit bhayani
 */
public abstract class AdminSbb extends CommonSbb {

	private Logger logger = Logger.getLogger(AdminSbb.class);

	private TimerFacility timerFacility = null;

	private EntityManagerFactory emf;

	private final String orderApproved = "audio/AdminOrderApproved.wav";
	private final String orderCancelled = "audio/AdminOrderCancelled.wav";
	private final String orderReConfirm = "audio/AdminReConfirm.wav";

	String audioFilePath = null;

	String callerSip = null;

	String adminSip = null;

	long waitingTime = 0;

	private MsProvider msProvider;

	private MediaRaActivityContextInterfaceFactory mediaAcif;

	/** Creates a new instance of SecondBounceSbb */
	public AdminSbb() {
		super();
	}

	public void setSbbContext(SbbContext sbbContext) {
		super.setSbbContext(sbbContext);
		try {
			Context ctx = (Context) new InitialContext().lookup("java:comp/env");

			audioFilePath = System.getProperty("jboss.server.data.dir") + "/RecordedAdmin.wav";

			callerSip = (String) ctx.lookup("callerSip");

			adminSip = (String) ctx.lookup("adminSip");

			waitingTime = ((Long) ctx.lookup("waitingTiming")).longValue();

			// Getting Timer Facility interface
			timerFacility = (TimerFacility) ctx.lookup("slee/facilities/timer");

			msProvider = (MsProvider) ctx.lookup("slee/resources/media/1.0/provider");

			mediaAcif = (MediaRaActivityContextInterfaceFactory) ctx.lookup("slee/resources/media/1.0/acifactory");

			InitialContext newIc = new InitialContext();
			emf = (EntityManagerFactory) newIc.lookup("java:/ShoppingDemoSleeEntityManagerFactory");

		} catch (NamingException ne) {
			logger.error("Could not set SBB context: " + ne.toString(), ne);
		}
	}

	public void onOrderPlaced(CustomEvent event, ActivityContextInterface ac) {

		logger.info("AdminSbb: " + this + ": received an ORDER_PLACED event. OrderId = " + event.getOrderId()
				+ ". ammount = " + event.getAmmount() + ". Customer Name = " + event.getCustomerName());

		this.setCustomEvent(event);

		TTSSession ttsSession = getTTSProvider().getNewTTSSession(audioFilePath, "kevin");

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(event.getCustomerName());
		stringBuffer.append(" has placed an order of $");
		stringBuffer.append(event.getAmmount());
		stringBuffer.append(". Press 1 to approve and 2 to reject.");

		ttsSession.textToAudioFile(stringBuffer.toString());
		this.setSfDemo(true);
		this.setSendBye(false);
		makeCall(ac);

	}

	public void onOrderCancelled(CustomEvent event, ActivityContextInterface ac) {
		logger.info("****** AdminSbb Recieved ORDER_CANCELLED ******");
		cancelTimer();
	}

	public void onOrderRejected(CustomEvent event, ActivityContextInterface ac) {

		logger.info("****** AdminSbb Recieved ORDER_REJECTED ******* ");
		cancelTimer();
	}

	public void onOrderApproved(CustomEvent event, ActivityContextInterface ac) {

		logger.info("****** AdminSbb Recieved ORDER_APPROVED ******* ");
		cancelTimer();
	}

	public void onBeforeOrderProcessed(CustomEvent event, ActivityContextInterface ac) {

		logger.info("AdminSbb: " + this + ": received an BEFORE_ORDER_PROCESSED event. OrderId = " + event.getOrderId()
				+ ". ammount = " + event.getAmmount() + ". Customer Name = " + event.getCustomerName());

		this.setCustomEvent(event);

		TTSSession ttsSession = getTTSProvider().getNewTTSSession(audioFilePath, "kevin");

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(event.getCustomerName());
		stringBuffer.append(" has placed an order of $");
		stringBuffer.append(event.getAmmount());
		stringBuffer.append(". Press 1 to approve and 2 to reject.");

		ttsSession.textToAudioFile(stringBuffer.toString());

		setTimer(ac);
	}

	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		logger.info("****** AdminSbb Recieved TimerEvent ******* ");
		// Detach NullActivity
		//aci.detach(this.getSbbContext().getSbbLocalObject());

		makeCall(aci);
	}

	/**
	 * This method sets the Timer Object for passed ACI
	 * 
	 * @param ac
	 */
	private void setTimer(ActivityContextInterface ac) {
		TimerOptions options = new TimerOptions();
		options.setPersistent(true);

		// Set the timer on ACI
		TimerID timerID = this.timerFacility.setTimer(ac, null, System.currentTimeMillis() + waitingTime, options);

		this.setTimerID(timerID);
	}

	private void cancelTimer() {
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

	private void makeCall(ActivityContextInterface ac) {

		try {
			// Set the caller address to the address of our call controller
			Address callerAddress = getSipUtils().convertURIToAddress(callerSip);
			callerAddress.setDisplayName(callerSip);

			// Retrieve the callee addresses from the event
			Address calleeAddress = getSipUtils().convertURIToAddress(adminSip);

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

			Session callerSession = new Session();

			// Create a new caller address from caller URI specified in the
			// event (the real caller address)
			// since we need this in the next INVITE.
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
			
			ac.detach(this.getSbbContext().getSbbLocalObject());

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
		aci.detach(getSbbContext().getSbbLocalObject());
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

		EntityManager mgr = null;
		Order order = null;
		boolean successful = false;
		String audioFile = null;

		String destinationName = "/queue/B";

		InitialContext ic = null;
		ConnectionFactory cf = null;
		Connection jmsConnection = null;

		if ("1".equals(seq)) {
			audioFile = (getClass().getResource(orderApproved)).toString();

			// This piece of code is to integrate with JMS Queue for SalesForce
			// example.
			if (this.getSfDemo()) {

				try {
					ic = new InitialContext();

					cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
					Queue queue = (Queue) ic.lookup(destinationName);
					logger.info("Queue " + destinationName + " exists");

					jmsConnection = cf.createConnection();
					javax.jms.Session jmsSession = jmsConnection.createSession(false,
							javax.jms.Session.AUTO_ACKNOWLEDGE);
					MessageProducer sender = jmsSession.createProducer(queue);

					TextMessage message = jmsSession.createTextMessage(this.getCustomEvent().getOrderId() + ",1");
					sender.send(message);
					logger.info("The message was successfully sent to the " + queue.getQueueName() + " queue");

				} catch (Exception e) {
					logger.error("Exception while trying to send message", e);
				} finally {
					if (ic != null) {
						try {
							ic.close();
						} catch (Exception e) {
							logger.error("Exception while closing the IC", e);
						}
					}
					try {
						if (jmsConnection != null) {
							jmsConnection.close();
						}
					} catch (JMSException jmse) {
						logger.error("Could not close connection " + jmsConnection + " exception was " + jmse, jmse);
					}
				}

			} else {
				mgr = emf.createEntityManager();

				order = (Order) mgr.createQuery("select o from Order o where o.orderId = :orderId").setParameter(
						"orderId", this.getCustomEvent().getOrderId()).getSingleResult();

				order.setStatus(Order.Status.PROCESSING);

				mgr.flush();
				mgr.close();
			}
			successful = true;
		} else if ("2".equals(seq)) {
			audioFile = (getClass().getResource(orderCancelled)).toString();
			if (this.getSfDemo()) {

				try {
					ic = new InitialContext();

					cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
					Queue queue = (Queue) ic.lookup(destinationName);
					logger.info("Queue " + destinationName + " exists");

					jmsConnection = cf.createConnection();
					javax.jms.Session jmsSession = jmsConnection.createSession(false,
							javax.jms.Session.AUTO_ACKNOWLEDGE);
					MessageProducer sender = jmsSession.createProducer(queue);

					TextMessage message = jmsSession.createTextMessage(this.getCustomEvent().getOrderId() + ",2");
					sender.send(message);
					logger.info("The message was successfully sent to the " + queue.getQueueName() + " queue");

				} catch (Exception e) {
					logger.error("Exception while trying to send message", e);
				} finally {
					if (ic != null) {
						try {
							ic.close();
						} catch (Exception e) {
							logger.error("Exception while closing the IC", e);
						}
					}
					try {
						if (jmsConnection != null) {
							jmsConnection.close();
						}
					} catch (JMSException jmse) {
						logger.error("Could not close connection " + jmsConnection + " exception was " + jmse, jmse);
					}
				}

			} else {
				mgr = emf.createEntityManager();

				order = (Order) mgr.createQuery("select o from Order o where o.orderId = :orderId").setParameter(
						"orderId", this.getCustomEvent().getOrderId()).getSingleResult();

				order.setStatus(Order.Status.CANCELLED);

				mgr.flush();
				mgr.close();
			}
			successful = true;
		} else {
			audioFile = (getClass().getResource(orderReConfirm)).toString();

		}
		this.setSendBye(successful);

		MsEventFactory eventFactory = msProvider.getEventFactory();

		MsPlayRequestedSignal play = null;
		play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);
		play.setURL(audioFile);

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
		logger.info("--------onLinkCreated------------");

		MsLink link = evt.getSource();
		MsEndpoint endpoint = link.getEndpoints()[1];

		MsEventFactory eventFactory = msProvider.getEventFactory();

		MsPlayRequestedSignal play = null;
		play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);

		String announcementFile = "file:" + audioFilePath;
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

		System.out.println("EXECUTING PLAY");
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
	}

	// child relation
	public abstract ChildRelation getCallControlSbbChild();

	// 'timerID' CMP field setter
	public abstract void setTimerID(TimerID value);

	// 'timerID' CMP field getter
	public abstract TimerID getTimerID();

	public abstract void setCustomEvent(CustomEvent customEvent);

	public abstract CustomEvent getCustomEvent();

	public abstract void setSendBye(boolean isBye);

	public abstract boolean getSendBye();

	public abstract void setSfDemo(boolean sfDemo);

	public abstract boolean getSfDemo();

	public abstract void setChildSbbLocalObject(CallControlSbbLocalObject childSbbLocalObject);

	public abstract CallControlSbbLocalObject getChildSbbLocalObject();

}
