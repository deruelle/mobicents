package org.mobicents.slee.sipevent.server.subscription;

import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.address.AddressFactory;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SLEEException;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;
import javax.slee.serviceactivity.ServiceStartedEvent;

import net.java.slee.resource.sip.SipActivityContextInterfaceFactory;
import net.java.slee.resource.sip.SleeSipProvider;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.jmx.SubscriptionControlManagement;
import org.mobicents.slee.sipevent.server.subscription.jmx.SubscriptionControlManagementMBean;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
import org.mobicents.slee.sipevent.server.subscription.sip.SipSubscriptionHandler;
import org.mobicents.slee.sipevent.server.subscription.winfo.WInfoSubscriptionHandler;

/**
 * Sbb to control subscriptions of sip events in a dialog
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class SubscriptionControlSbb implements Sbb,
		SubscriptionControlSbbLocalObject {

	private static final Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	/**
	 * the Management MBean
	 */
	private static final SubscriptionControlManagement configuration = new SubscriptionControlManagement();

	/**
	 * JAIN-SIP provider & factories
	 * 
	 * @return
	 */
	private SipActivityContextInterfaceFactory sipActivityContextInterfaceFactory;
	private SleeSipProvider sipProvider;
	private AddressFactory addressFactory;
	private MessageFactory messageFactory;
	private HeaderFactory headerFactory;

	/**
	 * SLEE Facilities
	 */
	private TimerFacility timerFacility;
	private ActivityContextNamingFacility activityContextNamingfacility;
	private NullActivityContextInterfaceFactory nullACIFactory;
	private NullActivityFactory nullActivityFactory;

	/**
	 * SbbObject's sbb context
	 */
	private SbbContext sbbContext;

	// GETTERS

	public SipSubscriptionHandler getSipSubscribeHandler() {
		return new SipSubscriptionHandler(this);
	}

	public WInfoSubscriptionHandler getWInfoSubscriptionHandler() {
		return new WInfoSubscriptionHandler(this);
	}

	public ActivityContextNamingFacility getActivityContextNamingfacility() {
		return activityContextNamingfacility;
	}

	public AddressFactory getAddressFactory() {
		return addressFactory;
	}

	public HeaderFactory getHeaderFactory() {
		return headerFactory;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

	public NullActivityContextInterfaceFactory getNullACIFactory() {
		return nullACIFactory;
	}

	public NullActivityFactory getNullActivityFactory() {
		return nullActivityFactory;
	}

	public SbbContext getSbbContext() {
		return sbbContext;
	}

	public SipActivityContextInterfaceFactory getSipActivityContextInterfaceFactory() {
		return sipActivityContextInterfaceFactory;
	}

	public SleeSipProvider getSipProvider() {
		return sipProvider;
	}

	public TimerFacility getTimerFacility() {
		return timerFacility;
	}

	/**
	 * Retrieves the current configuration for this component from an MBean
	 * 
	 * @return
	 */
	public SubscriptionControlManagementMBean getConfiguration() {
		return configuration;
	}

	// --- JPA STUFF

	private static EntityManagerFactory entityManagerFactory = Persistence
			.createEntityManagerFactory("sipevent-subscription-pu");

	public EntityManager getEntityManager() {
		return entityManagerFactory.createEntityManager();
	}

	// --- INTERNAL CHILD SBB

	public abstract ChildRelation getImplementedControlChildRelation();

	public abstract ImplementedSubscriptionControlSbbLocalObject getImplementedControlChildSbbCMP();

	public abstract void setImplementedControlChildSbbCMP(
			ImplementedSubscriptionControlSbbLocalObject value);

	public ImplementedSubscriptionControlSbbLocalObject getImplementedControlChildSbb()
			throws TransactionRequiredLocalException, SLEEException,
			CreateException {
		ImplementedSubscriptionControlSbbLocalObject childSbb = getImplementedControlChildSbbCMP();
		if (childSbb == null) {
			childSbb = (ImplementedSubscriptionControlSbbLocalObject) getImplementedControlChildRelation()
					.create();
			setImplementedControlChildSbbCMP(childSbb);
			childSbb.setParentSbb((ImplementedSubscriptionControlParentSbbLocalObject)this.sbbContext.getSbbLocalObject());
		}
		return childSbb;
	}

	// ----------- EVENT HANDLERS

	/**
	 * if event is for this service starts mbean
	 */
	public void onServiceStartedEvent(ServiceStartedEvent event,
			ActivityContextInterface aci) {
		// we want to stay attached to this service activity, to receive the
		// activity end event on service deactivation
		try {
			// get this service activity
			ServiceActivity sa = ((ServiceActivityFactory) new InitialContext()
					.lookup("java:comp/env/slee/serviceactivity/factory"))
					.getActivity();
			if (!sa.equals(aci.getActivity())) {
				aci.detach(this.sbbContext.getSbbLocalObject());
			} else {
				// starts the mbean
				configuration.startService();
			}
		} catch (Exception e) {
			logger.error("failed to process service started event", e);
		}
	}

	/**
	 * If it's a service activity must be the one for this service. It then
	 * closes the jpa EM factory and tops the MBean
	 * 
	 * @param event
	 * @param aci
	 */
	public void onActivityEndEvent(ActivityEndEvent event,
			ActivityContextInterface aci) {
		// close entity manager factory on service deactivation
		Object activity = aci.getActivity();
		if (activity instanceof ServiceActivity) {
			entityManagerFactory.close();
			configuration.stopService();
		}
	}

	/**
	 * event handler for initial subscribe, which is out of dialog
	 * 
	 * @param event
	 * @param aci
	 */
	public void onSubscribeOutOfDialog(RequestEvent event,
			ActivityContextInterface aci) {
		aci.detach(this.sbbContext.getSbbLocalObject());
		new SipSubscriptionHandler(this).processRequest(event, aci);
	}

	/**
	 * event handler for in dialog subscribe
	 * 
	 * @param event
	 * @param aci
	 */
	public void onSubscribeInDialog(RequestEvent event,
			ActivityContextInterface aci) {
		new SipSubscriptionHandler(this).processRequest(event, aci);
	}

	/**
	 * An error as the final response of a NOTIFY sent by this server.
	 * 
	 * @param event
	 * @param aci
	 */
	public void onResponseClientErrorEvent(ResponseEvent event,
			ActivityContextInterface aci) {
		// we got a error response from a notify,
		new SipSubscriptionHandler(this).getRemoveSipSubscriptionHandler()
				.removeSipSubscriptionOnNotifyError(event);
	}

	/**
	 * An error as the final response of a NOTIFY sent by this server.
	 * 
	 * @param event
	 * @param aci
	 */
	public void onResponseServerErrorEvent(ResponseEvent event,
			ActivityContextInterface aci) {
		// we got a error response from a notify,
		new SipSubscriptionHandler(this).getRemoveSipSubscriptionHandler()
				.removeSipSubscriptionOnNotifyError(event);
	}

	// ---- SUBSCRIPTION CREATION OR REFRESH
	// --------------------------------------------------------------

	public void newSipSubscriptionAuthorization(RequestEvent event,
			String subscriber, String notifier, String eventPackage,
			String eventId, int expires, int responseCode) {

		EntityManager entityManager = getEntityManager();
		ImplementedSubscriptionControlSbbLocalObject childSbb = null;
		try {
			childSbb = getImplementedControlChildSbb();
			new SipSubscriptionHandler(this).getNewSipSubscriptionHandler()
					.newSipSubscriptionAuthorization(event, subscriber,
							notifier, eventPackage, eventId, expires,
							responseCode, entityManager, childSbb);
			entityManager.flush();
		} catch (Exception e) {
			logger.error(e);
			// cleanup
			try {
				Response response = new SipSubscriptionHandler(this)
						.addContactHeader(messageFactory.createResponse(
								Response.SERVER_INTERNAL_ERROR, event
										.getRequest()));
				event.getServerTransaction().sendResponse(response);
				if (logger.isDebugEnabled()) {
					logger.debug("Response sent:\n" + response.toString());
				}
			} catch (Exception f) {
				logger.error("Can't send RESPONSE", f);
			}
			return;
		}
		entityManager.close();
	}

	// ---- SUBSCRIPTION REMOVAL
	// -----------------------------------------------------------------

	/**
	 * a timer has ocurred in a dialog regarding a subscription
	 * 
	 * @param event
	 * @param aci
	 */
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {

		Object activity = aci.getActivity();
		if (activity instanceof Dialog) {
			new SipSubscriptionHandler(this).getRemoveSipSubscriptionHandler()
					.sipSubscriptionExpired(event, aci, (Dialog) activity);
		} else {
			// TODO internal subscription
		}
	}

	// ----------- SBB LOCAL OBJECT

	public void notifySubscribers(String notifier, String eventPackage,
			Object content, ContentTypeHeader contentTypeHeader) {

		ImplementedSubscriptionControlSbbLocalObject childSbb = null;
		try {
			childSbb = getImplementedControlChildSbb();
		} catch (Exception e) {
			logger.error("Failed to get child sbb", e);
			return;
		}

		// create jpa entity manager
		EntityManager entityManager = getEntityManager();

		// get subscriptions
		List resultList = entityManager.createNamedQuery(
				"selectSubscriptionsFromNotifierAndEventPackage").setParameter(
				"notifier", notifier)
				.setParameter("eventPackage", eventPackage).getResultList();

		for (Iterator it = resultList.iterator(); it.hasNext();) {
			Subscription subscription = (Subscription) it.next();
			if (subscription.getStatus().equals(Subscription.Status.active)) {
				if (subscription.getKey().isInternalSubscription()) {
					// internal subscription
					// TODO
				} else {
					// sip subscription
					new SipSubscriptionHandler(this).getSipSubscriberNotificationHandler()
							.notifySipSubscriber(content, contentTypeHeader,
									subscription, entityManager, childSbb);
				}
			}
		}

		// close entity manager
		entityManager.close();
	}

	public void notifySubscriber(SubscriptionKey key, Object content,
			ContentTypeHeader contentTypeHeader) {

		ImplementedSubscriptionControlSbbLocalObject childSbb = null;
		try {
			childSbb = getImplementedControlChildSbb();
		} catch (Exception e) {
			logger.error("Failed to get child sbb", e);
			return;
		}

		// create jpa entity manager
		EntityManager entityManager = getEntityManager();

		// get subscription
		Subscription subscription = entityManager.find(Subscription.class, key);

		if (subscription != null
				&& subscription.getStatus().equals(Subscription.Status.active)) {
			if (subscription.getKey().isInternalSubscription()) {
				// internal subscription
				// TODO
			} else {
				// sip subscription
				new SipSubscriptionHandler(this).getSipSubscriberNotificationHandler()
						.notifySipSubscriber(content, contentTypeHeader,
								subscription, entityManager, childSbb);
			}
		}

		// close entity manager
		entityManager.close();
	}

	/**
	 * not internal subs aware yet!!! FIXME
	 */
	public void authorizationChanged(String subscriber, String notifier,
			String eventPackage, int authorizationCode) {
		// get entity manager
		EntityManager entityManager = getEntityManager();
		// get this entity dialog
		Dialog dialog = null;
		ActivityContextInterface dialogACI = null;
		for (ActivityContextInterface aci : sbbContext.getActivities()) {
			Object activity = aci.getActivity();
			if (activity instanceof Dialog) {
				dialogACI = aci;
				dialog = (Dialog) activity;
			}
		}
		if (dialog != null) {
			// process all subscriptions in this dialog
			for (Object object : Subscription.getDialogSubscriptions(
					entityManager, dialog.getCallId().getCallId(),dialog.getRemoteTag())) {
				Subscription subscription = (Subscription) object;
				if (subscription.getSubscriber().equals(subscriber)
						&& subscription.getNotifier().equals(notifier)
						&& subscription.getKey().getEventPackage().equals(
								eventPackage)) {
					// we have a subscription match
					Subscription.Status oldStatus = subscription.getStatus();
					switch (authorizationCode) {
					/*
					 * If the <sub-handling> permission changes value to
					 * "block", this causes a "rejected" event to be generated
					 * into the subscription state machine for all affected
					 * subscriptions. This will cause the state machine to move
					 * into the "terminated" state, resulting in the
					 * transmission of a NOTIFY to the watcher with a
					 * Subscription-State header field with value "terminated"
					 * and a reason of "rejected" [7], which terminates their
					 * subscription.
					 */
					case Response.FORBIDDEN:
						subscription.changeStatus(Subscription.Event.rejected);
						break;

					/*
					 * If the <sub-handling> permission changes value to
					 * "confirm", the processing depends on the states of the
					 * affected subscriptions. Unfortunately, the state machine
					 * in RFC 3857 does not define an event corresponding to an
					 * authorization decision of "pending". If the subscription
					 * is in the "active" state, it moves back into the
					 * "pending" state. This causes a NOTIFY to be sent,
					 * updating the Subscription-State [7] to "pending". No
					 * reason is included in the Subscription-State header field
					 * (none are defined to handle this case). No further
					 * documents are sent to this watcher. There is no change in
					 * state if the subscription is in the "pending", "waiting",
					 * or "terminated" states.
					 */
					case Response.ACCEPTED:
						if (subscription.getStatus().equals(
								Subscription.Status.active)) {
							subscription.setStatus(Subscription.Status.pending);
							subscription.setLastEvent(null);
						}
						break;

					/*
					 * If the <sub-handling> permission changes value from
					 * "blocked" or "confirm" to "polite-block" or "allow", this
					 * causes an "approved" event to be generated into the state
					 * machine for all affected subscriptions. If the
					 * subscription was in the "pending" state, the state
					 * machine will move to the "active" state, resulting in the
					 * transmission of a NOTIFY with a Subscription-State header
					 * field of "active", and the inclusion of a presence
					 * document in that NOTIFY. If the subscription was in the
					 * "waiting" state, it will move into the "terminated"
					 * state.
					 */
					case Response.OK:
						subscription.changeStatus(Subscription.Event.approved);
						break;

					default:
						logger
								.warn("Received authorization update with unknown auth code "
										+ authorizationCode);
						continue;
					}

					if (!oldStatus.equals(subscription.getStatus())) {
						// subscription status changed
						logger.info("Status changed for " + subscription);
						ImplementedSubscriptionControlSbbLocalObject childSbb = null;
						try {
							childSbb = getImplementedControlChildSbb();
						} catch (Exception e) {
							logger.error("Failed to get child sbb", e);
							return;
						}
						// notify subscriber
						try {
							new SipSubscriptionHandler(this)
									.getSipSubscriberNotificationHandler()
									.createAndSendNotify(entityManager,
											subscription, dialog, childSbb);
						} catch (Exception e) {
							logger.error("failed to notify subscriber", e);
						}

						// notify winfo subscription(s)
						new WInfoSubscriptionHandler(this).notifyWinfoSubscriptions(
								entityManager, subscription, childSbb);

						// check resulting subscription state
						if (subscription.getStatus().equals(
								Subscription.Status.terminated)) {
							// remove subscription data
							if (subscription.getKey().isInternalSubscription()) {
								// internal subscription
								// TODO
							} else {
								// sip subscription
								new SipSubscriptionHandler(this)
										.getRemoveSipSubscriptionHandler()
										.removeSipSubscriptionData(
												entityManager, subscription,
												dialog, dialogACI, childSbb);
							}
						} else if (subscription.getStatus().equals(
								Subscription.Status.waiting)) {
							// keep the subscription for default waiting time so
							// notifier may know about this attempt to subscribe
							// him
							int defaultWaitingExpires = getConfiguration()
									.getDefaultWaitingExpires();
							// refresh subscription
							subscription.refresh(defaultWaitingExpires);
							// set waiting timer
							setSubscriptionTimerAndPersistSubscription(
									entityManager, subscription,
									defaultWaitingExpires + 1, dialogACI);
						}
					}
				}
			}
		}
		entityManager.flush();
		entityManager.close();
	}

	// --- INTERNAL SUBSCRIPTIONS

	public void newInternalSubscription(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires,
			String content, String contentType, String contentSubtype) {
		// TODO Auto-generated method stub
		/*
		 * // create subscription key, if event is null then it's an internal
		 * subscription SubscriptionKey subscriptionKey = new
		 * SubscriptionKey(SubscriptionKey.NO_CALL_ID,SubscriptionKey.NO_REMOTE_TAG,eventPackage,subscriptionId);
		 * 
		 * EntityManager entityManager = getEntityManager();
		 *  // find subscription Subscription subscription =
		 * entityManager.find(Subscription.class, subscriptionKey);
		 * 
		 * if (subscription != null) { // subscription exists if
		 * (subscription.getStatus().equals( Subscription.Status.active) ||
		 * subscription.getStatus().equals( Subscription.Status.pending)) { //
		 * subscription status permits refresh refreshSubscription(event, aci,
		 * expires, subscription, entityManager, childSbb); } else { //
		 * subscription status does not permits refresh if (event != null) { //
		 * sip subscription sendResponse( Response.CONDITIONAL_REQUEST_FAILED,
		 * event.getRequest(), event .getServerTransaction(),childSbb); } else { //
		 * internal subscription // TODO } } } else { // subscription does not
		 * exists
		 * newSubscription(event,aci,subscriber,notifier,expires,childSbb,entityManager,subscriptionKey); }
		 */
		/*
		 * // ask authorization if (eventPackage.endsWith(".winfo")) { // winfo
		 * package, only accept subscriptions when subscriber and notifier are
		 * the same // parameter event as null will mean, from now on, that it's
		 * an internal subscription
		 * newSubscriptionAuthorization(null,subscriber,notifier,eventPackage,subscriptionId,
		 * expires, (subscriber.equals(notifier) ? Response.OK :
		 * Response.FORBIDDEN)); } else {
		 * childSbb.isSubscriberAuthorized(null,subscriber,notifier,eventPackage,subscriptionId,expires); }
		 */
	}

	public void refreshInternalSubscription(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires) {
		// TODO Auto-generated method stub

	}

	public void removeInternalSubscription(String subscriber, String notifier,
			String eventPackage, String subscriptionId) {
		// TODO Auto-generated method stub
		/*
		 * EntityManager entityManager = getEntityManager(); // create
		 * subscription key SubscriptionKey subscriptionKey = new
		 * SubscriptionKey(SubscriptionKey.NO_CALL_ID,SubscriptionKey.NO_REMOTE_TAG,eventPackage,subscriptionId); //
		 * get subscription Subscription subscription =
		 * entityManager.find(Subscription.class, subscriptionKey);
		 *  // trying to remove a subscription if (subscription != null) { if
		 * (subscription.getStatus().equals( Subscription.Status.active) ||
		 * subscription .getStatus().equals( Subscription.Status.pending)) { //
		 * subscription exists and status permits remove // TODO reply remove
		 * was sucessfull // remove subscription removeSubscription(aci,
		 * eventPackage, eventId, subscription,entityManager, childSbb); } else { //
		 * subscription does exists but status does // not permits removal
		 * sendResponse( Response.CONDITIONAL_REQUEST_FAILED,
		 * event.getRequest(), event .getServerTransaction(),childSbb); } } else { //
		 * subscription does not exists, one shot subscription request, not
		 * supported sendResponse( Response.CONDITIONAL_REQUEST_FAILED,
		 * event.getRequest(), event .getServerTransaction(),childSbb); }
		 */
	}

	// ----------- AUX METHODS

	public void setSubscriptionTimerAndPersistSubscription(
			EntityManager entityManager, Subscription subscription, long delay,
			ActivityContextInterface aci) {
		TimerOptions options = new TimerOptions();
		options.setPersistent(true);
		options.setPreserveMissed(TimerPreserveMissed.ALL);
		// set timer
		TimerID timerId = timerFacility.setTimer(aci, null, System
				.currentTimeMillis()
				+ (delay * 1000), 1, 1, options);
		subscription.setTimerID(timerId);
		// update subscription
		entityManager.persist(subscription);
	}

	// ----------- SBB OBJECT's LIFE CYCLE

	/**
	 * SbbObject's context setting
	 */
	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
		// retrieve factories, facilities & providers
		try {
			Context context = (Context) new InitialContext()
					.lookup("java:comp/env");
			timerFacility = (TimerFacility) context
					.lookup("slee/facilities/timer");
			nullACIFactory = (NullActivityContextInterfaceFactory) context
					.lookup("slee/nullactivity/activitycontextinterfacefactory");
			nullActivityFactory = (NullActivityFactory) context
					.lookup("slee/nullactivity/factory");
			sipActivityContextInterfaceFactory = (SipActivityContextInterfaceFactory) context
					.lookup("slee/resources/jainsip/1.2/acifactory");
			sipProvider = (SleeSipProvider) context
					.lookup("slee/resources/jainsip/1.2/provider");
			addressFactory = sipProvider.getAddressFactory();
			headerFactory = sipProvider.getHeaderFactory();
			messageFactory = sipProvider.getMessageFactory();
			activityContextNamingfacility = (ActivityContextNamingFacility) context
					.lookup("slee/facilities/activitycontextnaming");
		} catch (Exception e) {
			logger.error(
					"Unable to retrieve factories, facilities & providers", e);
		}
	}

	public void sbbActivate() {
	}

	public void sbbCreate() throws CreateException {
	}

	public void sbbExceptionThrown(Exception arg0, Object arg1,
			ActivityContextInterface arg2) {
	}

	public void sbbLoad() {		
	}

	public void sbbPassivate() {
	}

	public void sbbPostCreate() throws CreateException {
	}

	public void sbbRemove() {
	}

	public void sbbRolledBack(RolledBackContext arg0) {
	}

	public void sbbStore() {
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
	}

}