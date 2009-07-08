package org.mobicents.slee.sipevent.server.subscription;

import java.text.ParseException;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.address.AddressFactory;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.Address;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;
import javax.slee.nullactivity.NullActivity;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceActivityFactory;
import javax.slee.serviceactivity.ServiceStartedEvent;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import net.java.slee.resource.sip.SipActivityContextInterfaceFactory;
import net.java.slee.resource.sip.SleeSipProvider;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.internal.InternalNotifyEvent;
import org.mobicents.slee.sipevent.server.internal.InternalSubscriptionHandler;
import org.mobicents.slee.sipevent.server.subscription.eventlist.MultiPart;
import org.mobicents.slee.sipevent.server.subscription.jmx.SubscriptionControlManagement;
import org.mobicents.slee.sipevent.server.subscription.jmx.SubscriptionControlManagementMBean;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription.Event;
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

	public InternalSubscriptionHandler getInternalSubscriptionHandler() {
		return new InternalSubscriptionHandler(this);
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

	private static EntityManagerFactory initEntityManagerFactory() {
		try {
			TransactionManager txMgr = (TransactionManager) new InitialContext()
					.lookup("java:/TransactionManager");

			Transaction tx = null;
			try {
				if (txMgr.getTransaction() != null) {
					tx = txMgr.suspend();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				return Persistence
						.createEntityManagerFactory("sipevent-subscription-pu");
			} finally {
				if (tx != null) {
					txMgr.resume(tx);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	private static void stopEntityManagerFactory() {
		try {
			TransactionManager txMgr = (TransactionManager) new InitialContext()
					.lookup("java:/TransactionManager");

			Transaction tx = null;
			try {
				if (txMgr.getTransaction() != null) {
					tx = txMgr.suspend();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				entityManagerFactory.close();
			} finally {
				if (tx != null) {
					txMgr.resume(tx);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private static EntityManagerFactory entityManagerFactory = initEntityManagerFactory();

	public EntityManager getEntityManager() {
		return entityManagerFactory.createEntityManager();
	}

	// -- STORAGE OF PARENT SBB (USED BY INTERNAL CLIENTS)

	public void setParentSbb(
			SubscriptionClientControlParentSbbLocalObject parentSbb) {
		setParentSbbCMP(parentSbb);
	}

	public abstract SubscriptionClientControlParentSbbLocalObject getParentSbbCMP();

	public abstract void setParentSbbCMP(
			SubscriptionClientControlParentSbbLocalObject value);

	// --- CONCRETE IMPLEMENTATION CHILD SBB

	public abstract ChildRelation getImplementedControlChildRelation();

	public abstract ImplementedSubscriptionControlSbbLocalObject getImplementedControlChildSbbCMP();

	public abstract void setImplementedControlChildSbbCMP(
			ImplementedSubscriptionControlSbbLocalObject value);

	public ImplementedSubscriptionControlSbbLocalObject getImplementedControlChildSbb() {
		ImplementedSubscriptionControlSbbLocalObject childSbb = getImplementedControlChildSbbCMP();
		if (childSbb == null) {
			try {
				childSbb = (ImplementedSubscriptionControlSbbLocalObject) getImplementedControlChildRelation()
						.create();
			} catch (Exception e) {
				logger.error("Failed to create child sbb",e);
				return null;
			}
			setImplementedControlChildSbbCMP(childSbb);
			childSbb
					.setParentSbb((ImplementedSubscriptionControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		}
		return childSbb;
	}
	
	// --- EVENT LIST PROCESSING CHILD SBB

	public abstract ChildRelation getEventListControlChildRelation();

	public abstract EventListSubscriptionControlSbbLocalObject getEventListControlChildSbbCMP();

	public abstract void setEventListControlChildSbbCMP(
			EventListSubscriptionControlSbbLocalObject value);

	public EventListSubscriptionControlSbbLocalObject getEventListControlChildSbb() {
		EventListSubscriptionControlSbbLocalObject childSbb = getEventListControlChildSbbCMP();
		if (childSbb == null) {
			try {
				childSbb = (EventListSubscriptionControlSbbLocalObject) getEventListControlChildRelation()
						.create();
			} catch (Exception e) {
				logger.error("Failed to create child sbb",e);
				return null;
			}
			setEventListControlChildSbbCMP(childSbb);
			childSbb
					.setParentSbb((EventListSubscriptionControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
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
				// init RLS cache
				getEventListControlChildSbb().initRLSCache();
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
			getEventListControlChildSbb().shutdownRLSCache();
			configuration.stopService();
			stopEntityManagerFactory();
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
		// store server transaction id, we need it get it from the dialog
		// activity		
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

	/**
	 * a timer has occurred on a subscription
	 * 
	 * @param event
	 * @param aci
	 */
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {

		Object activity = aci.getActivity();
		Dialog dialog = null;
		if (activity instanceof Dialog) {
			dialog = (Dialog) activity;
		}

		// create jpa entity manager
		EntityManager entityManager = getEntityManager();

		// get subscription
		for (Object object : entityManager.createNamedQuery(
				Subscription.JPA_NAMED_QUERY_SELECT_SUBSCRIPTION_FROM_TIMERID).setParameter("timerID",
				event.getTimerID()).getResultList()) {

			Subscription subscription = (Subscription) object;

			if (logger.isInfoEnabled()) {
				logger.info("Timer expired for " + subscription);
			}

			ImplementedSubscriptionControlSbbLocalObject childSbb = getImplementedControlChildSbb();
			
			// check subscription status
			if (subscription.getStatus().equals(Subscription.Status.waiting)) {
				// change subscription status
				subscription.changeStatus(Subscription.Event.giveup);
				if (logger.isInfoEnabled()) {
					logger.info("Status changed for " + subscription);
				}
				// notify winfo subscription(s)
				getWInfoSubscriptionHandler().notifyWinfoSubscriptions(
						entityManager, subscription, childSbb);
				// remove subscription data
				removeSubscriptionData(entityManager, subscription, dialog,
						aci, childSbb);
			} else {
				subscription.changeStatus(Event.timeout);
				// remove subscription
				if (subscription.getResourceList()) {
					getEventListControlChildSbb().removeSubscription(subscription);
				}
				if (dialog != null) {
					// sip subscription
					new SipSubscriptionHandler(this)
					.getRemoveSipSubscriptionHandler()
					.removeSipSubscription(aci, subscription,
							entityManager, childSbb);
				} else {
					// internal subscription
					new InternalSubscriptionHandler(this)
					.getRemoveInternalSubscriptionHandler()
					.removeInternalSubscription(aci, subscription,
							entityManager, childSbb);
				}
			}
			entityManager.flush();
		}
		// close entity manager
		entityManager.close();
	}

	public void onInternalNotifyEvent(InternalNotifyEvent event,
			ActivityContextInterface aci) {
		
		// notify the parent
		getParentSbbCMP().notifyEvent(event.getSubscriber(),
				event.getNotifier(), event.getEventPackage(),
				event.getSubscriptionId(), event.getTerminationReason(),
				event.getSubscriptionStatus(), event.getContent(),
				event.getContentType(), event.getContentSubtype());
		// if subscription terminated then we remove the null aci
		if (event.getSubscriptionStatus()
				.equals(Subscription.Status.terminated)) {
			((NullActivity) aci.getActivity()).endActivity();
			aci.detach(getSbbContext().getSbbLocalObject());
		}
	}

	// ----------- SBB LOCAL OBJECT

	public void newSubscriptionAuthorization(String subscriber,
			String subscriberDisplayName, String notifier, SubscriptionKey key,
			int expires, int responseCode, boolean eventList, ServerTransaction serverTransaction) {

		EntityManager entityManager = getEntityManager();
		try {

			if (!key.isInternalSubscription()) {
				// sip subscription
				// look for an aci of this server tx
				ActivityContextInterface serverTransactionACI = null;
				for (ActivityContextInterface aci : getSbbContext()
						.getActivities()) {
					Object activity = aci.getActivity();
					if (activity instanceof ServerTransaction) {
						serverTransactionACI = aci;
						break;
					}
				}
				new SipSubscriptionHandler(this).getNewSipSubscriptionHandler()
						.newSipSubscriptionAuthorization(serverTransaction,
								serverTransactionACI, subscriber,
								subscriberDisplayName, notifier, key, expires,
								responseCode, eventList, entityManager,
								getImplementedControlChildSbb());
			} else {
				new InternalSubscriptionHandler(this)
						.getNewInternalSubscriptionHandler()
						.newInternalSubscriptionAuthorization(subscriber,
								subscriberDisplayName, notifier, key, expires,
								responseCode, eventList, entityManager,
								getImplementedControlChildSbb());
			}
			entityManager.flush();
		} catch (Exception e) {
			logger.error("Error processing new subscription authorization", e);
			// cleanup
			if (!key.isInternalSubscription()) {
				if (serverTransaction != null) {
					try {
						Response response = new SipSubscriptionHandler(this)
								.addContactHeader(messageFactory
										.createResponse(
												Response.SERVER_INTERNAL_ERROR,
												serverTransaction.getRequest()));
						serverTransaction.sendResponse(response);
						if (logger.isDebugEnabled()) {
							logger.debug("Response sent:\n"
									+ response.toString());
						}
					} catch (Exception f) {
						logger.error("Can't send RESPONSE", f);
					}
				}
			} else {
				getParentSbbCMP().subscribeError(subscriber, notifier,
						key.getEventPackage(), key.getRealEventId(),
						Response.SERVER_INTERNAL_ERROR);
			}
			return;
		}
		entityManager.close();
	}

	public void notifySubscribers(String notifier, String eventPackage,
			Object content, ContentTypeHeader contentTypeHeader) {

		ImplementedSubscriptionControlSbbLocalObject childSbb = getImplementedControlChildSbb();
		
		// create jpa entity manager
		EntityManager entityManager = getEntityManager();

		// process subscriptions
		for (Object object : entityManager.createNamedQuery(
				Subscription.JPA_NAMED_QUERY_SELECT_SUBSCRIPTIONS_FROM_NOTIFIER_AND_EVENTPACKAGE).setParameter(
				"notifier", notifier)
				.setParameter("eventPackage", eventPackage).getResultList()) {
			Subscription subscription = (Subscription) object;
			if (subscription.getStatus().equals(Subscription.Status.active)) {
				if (subscription.getKey().isInternalSubscription()) {
					// internal subscription
					new InternalSubscriptionHandler(this)
							.getInternalSubscriberNotificationHandler()
							.notifyInternalSubscriber(entityManager,
									subscription, content, contentTypeHeader,
									childSbb);
				} else {
					// sip subscription
					new SipSubscriptionHandler(this)
							.getSipSubscriberNotificationHandler()
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

		ImplementedSubscriptionControlSbbLocalObject childSbb = getImplementedControlChildSbb();
		
		// create jpa entity manager
		EntityManager entityManager = getEntityManager();

		// get subscription
		Subscription subscription = entityManager.find(Subscription.class, key);

		if (subscription != null
				&& subscription.getStatus().equals(Subscription.Status.active)) {
			if (subscription.getKey().isInternalSubscription()) {
				// internal subscription
				new InternalSubscriptionHandler(this)
						.getInternalSubscriberNotificationHandler()
						.notifyInternalSubscriber(entityManager, subscription,
								content, contentTypeHeader, childSbb);
			} else {
				// sip subscription
				new SipSubscriptionHandler(this)
						.getSipSubscriberNotificationHandler()
						.notifySipSubscriber(content, contentTypeHeader,
								subscription, entityManager, childSbb);
			}
		}

		// close entity manager
		entityManager.close();
	}

	public void authorizationChanged(String subscriber, String notifier,
			String eventPackage, String eventId, int authorizationCode) {
		// get entity manager
		EntityManager entityManager = getEntityManager();
		// get this entity dialog (if it's not a internal subscription) and the
		// subscription aci
		Dialog dialog = null;
		ActivityContextInterface subscriptionAci = null;
		for (ActivityContextInterface aci : sbbContext.getActivities()) {
			Object activity = aci.getActivity();
			if (activity instanceof Dialog) {
				subscriptionAci = aci;
				dialog = (Dialog) activity;
				break;
			} else if (activity instanceof NullActivity) {
				subscriptionAci = aci;
				break;
			}
		}

		// let's find the subscription that matches the parameters
		String callId = SubscriptionKey.NO_CALL_ID;
		String remoteTag = SubscriptionKey.NO_REMOTE_TAG;
		if (dialog != null) {
			callId = dialog.getCallId().getCallId();
			remoteTag = dialog.getRemoteTag();
		}

		Subscription subscription = entityManager.find(Subscription.class,
				new SubscriptionKey(callId, remoteTag, eventPackage, eventId));

		if (subscription != null) {
			// we have a subscription match
			Subscription.Status oldStatus = subscription.getStatus();
			switch (authorizationCode) {
			/*
			 * If the <sub-handling> permission changes value to "block", this
			 * causes a "rejected" event to be generated into the subscription
			 * state machine for all affected subscriptions. This will cause the
			 * state machine to move into the "terminated" state, resulting in
			 * the transmission of a NOTIFY to the watcher with a
			 * Subscription-State header field with value "terminated" and a
			 * reason of "rejected" [7], which terminates their subscription.
			 */
			case Response.FORBIDDEN:
				subscription.changeStatus(Subscription.Event.rejected);
				break;

			/*
			 * If the <sub-handling> permission changes value to "confirm", the
			 * processing depends on the states of the affected subscriptions.
			 * Unfortunately, the state machine in RFC 3857 does not define an
			 * event corresponding to an authorization decision of "pending". If
			 * the subscription is in the "active" state, it moves back into the
			 * "pending" state. This causes a NOTIFY to be sent, updating the
			 * Subscription-State [7] to "pending". No reason is included in the
			 * Subscription-State header field (none are defined to handle this
			 * case). No further documents are sent to this watcher. There is no
			 * change in state if the subscription is in the "pending",
			 * "waiting", or "terminated" states.
			 */
			case Response.ACCEPTED:
				if (subscription.getStatus().equals(Subscription.Status.active)) {
					subscription.setStatus(Subscription.Status.pending);
					subscription.setLastEvent(null);
				}
				break;

			/*
			 * If the <sub-handling> permission changes value from "blocked" or
			 * "confirm" to "polite-block" or "allow", this causes an "approved"
			 * event to be generated into the state machine for all affected
			 * subscriptions. If the subscription was in the "pending" state,
			 * the state machine will move to the "active" state, resulting in
			 * the transmission of a NOTIFY with a Subscription-State header
			 * field of "active", and the inclusion of a presence document in
			 * that NOTIFY. If the subscription was in the "waiting" state, it
			 * will move into the "terminated" state.
			 */
			case Response.OK:
				subscription.changeStatus(Subscription.Event.approved);
				break;

			default:
				logger
						.warn("Received authorization update with unknown auth code "
								+ authorizationCode);
				return;
			}

			if (!oldStatus.equals(subscription.getStatus())) {
				// subscription status changed
				if (logger.isInfoEnabled()) {
					logger.info("Status changed for " + subscription);
				}
				ImplementedSubscriptionControlSbbLocalObject childSbb = getImplementedControlChildSbb();
				
				if (subscription.getStatus().equals(
						Subscription.Status.terminated)) {
					if (subscription.getResourceList()) {
						getEventListControlChildSbb().removeSubscription(subscription);
					}
					if (dialog == null) {
						// internal subscription
						new InternalSubscriptionHandler(this)
						.getRemoveInternalSubscriptionHandler()
						.removeInternalSubscription(subscriptionAci, subscription, entityManager, childSbb);
					} else {
						// sip subscription
						new SipSubscriptionHandler(this)
						.getRemoveSipSubscriptionHandler()
						.removeSipSubscription(subscriptionAci, subscription, entityManager, childSbb);
					}
				}
				else {
					boolean notifySubscriber = true;
					if (subscription.getResourceList()) {
						if (subscription.getStatus().equals(
								Subscription.Status.active)) {
							notifySubscriber = false;
							// resource list and subscription now active, create rls subscription
							if (!getEventListControlChildSbb().createSubscription(subscription)) {
								if (dialog == null) {
									// internal subscription
									new InternalSubscriptionHandler(this)
									.getRemoveInternalSubscriptionHandler()
									.removeInternalSubscription(subscriptionAci, subscription, entityManager, childSbb);
								} else {
									// sip subscription
									new SipSubscriptionHandler(this)
									.getRemoveSipSubscriptionHandler()
									.removeSipSubscription(subscriptionAci, subscription, entityManager, childSbb);
								}
							}
						}
					}
					
					if (notifySubscriber) {
						// it's not a resource list or the subscription state is pending
						// (thus no notify content which means it's
						// irrelevant to be resource list), notify subscriber
						if (dialog == null) {
							// internal subscription
							new InternalSubscriptionHandler(this)
							.getInternalSubscriberNotificationHandler()
							.notifyInternalSubscriber(entityManager,
									subscription, subscriptionAci, childSbb);
						} else {
							// sip subscription
							try {
								new SipSubscriptionHandler(this)
								.getSipSubscriberNotificationHandler()
								.createAndSendNotify(entityManager,
										subscription, dialog, childSbb);
							} catch (Exception e) {
								logger.error("failed to notify subscriber", e);
							}
						}						
					}
					
					// notify winfo subscription(s)
					new WInfoSubscriptionHandler(this).notifyWinfoSubscriptions(
							entityManager, subscription, childSbb);

					if (subscription.getStatus().equals(
							Subscription.Status.waiting)) {
						// keep the subscription for default waiting time so
						// notifier may know about this attempt to subscribe
						// him
						int defaultWaitingExpires = getConfiguration()
								.getDefaultWaitingExpires();
						// refresh subscription
						subscription.refresh(defaultWaitingExpires);
						// set waiting timer
						setSubscriptionTimerAndPersistSubscription(entityManager,
								subscription, defaultWaitingExpires + 1,
								subscriptionAci);
					}	
				}
			}  
		}

		entityManager.flush();
		entityManager.close();
	}

	// --- EVENT LIST CALLBACKS
	
	public void notifyEventListSubscriber(SubscriptionKey key, MultiPart multiPart) {
		// notification for subscription on a single resource is no different than resource list
		try {
			ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader(MultiPart.MULTIPART_CONTENT_TYPE, MultiPart.MULTIPART_CONTENT_SUBTYPE);
			contentTypeHeader.setParameter("type", multiPart.getType());			
			contentTypeHeader.setParameter("boundary", multiPart.getBoundary());
			notifySubscriber(key, multiPart.toString(),contentTypeHeader);
		} catch (ParseException e) {
			logger.error("failed to create content type header for event list notification", e);
		}
	}
	
	public Subscription getSubscription(SubscriptionKey key) {
		EntityManager entityManager = getEntityManager();
		Subscription subscription = entityManager.find(Subscription.class, key);
		if (subscription != null) {
			subscription.setEntityManager(entityManager);
		}
		return subscription;
	}
	
	/*
	 * perhaps there are subscriptions for this uri that are
	 * not set for a rls service, this can happen when a new list is
	 * created and a subscription arrives before the rls knows about the new service
	 * 	
	 * (non-Javadoc)
	 * @see org.mobicents.slee.sipevent.server.subscription.EventListSubscriptionControlParentSbbLocalObject#rlsServiceUpdated(java.lang.String)
	 */
	public void rlsServiceUpdated(String uri) {		 
		terminateRlsServiceSubscriptions(uri, Subscription.Event.deactivated);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.sipevent.server.subscription.EventListSubscriptionControlParentSbbLocalObject#rlsServiceRemoved(java.lang.String)
	 */
	public void rlsServiceRemoved(String uri) {
		terminateRlsServiceSubscriptions(uri, Subscription.Event.noresource);
	}
	
	/*
	 * Terminates all subscriptions where the notifier is the specified RLS
	 * service uri, with the provided event.
	 * 
	 * @param notifier
	 * 
	 * @param event
	 */
	private void terminateRlsServiceSubscriptions(String notifier, Subscription.Event event) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("terminating rls service "+notifier+" subscriptions with event "+event);
		}
			
		if (configuration.getEventListSupportOn()) {
			// create jpa entity manager
			EntityManager entityManager = getEntityManager();

			int index = notifier.indexOf(';');
			String notifierWithoutParams = null;
			String notifierParams = null;
			if (index > 0) {
				notifierWithoutParams = notifier.substring(0,index);
				notifierParams = notifier.substring(index);			
			}
			else {
				notifierWithoutParams = notifier;
				notifierParams = null;
			}
			
			// process subscriptions
			for (Object object : entityManager.createNamedQuery(
			Subscription.JPA_NAMED_QUERY_SELECT_SUBSCRIPTIONS_FROM_NOTIFIER_WITH_PARAMS).setParameter(
					"notifier", notifierWithoutParams).setParameter("notifierParams", notifierParams).getResultList()) {				
				Subscription subscription = (Subscription) object;
				if (logger.isDebugEnabled()) {
					logger.debug("terminating rls service "+notifier+" subscription "+subscription+" with event "+event);
				}
				if (subscription.getResourceList()) {
					getEventListControlChildSbb().removeSubscription(subscription);	
				}
				
				subscription.changeStatus(event);
				try {
					// get subscription aci
					ActivityContextInterface aci = getActivityContextNamingfacility().lookup(
							subscription.getKey().toString());
					if (aci != null) {
						if (subscription.getKey().isInternalSubscription()) {							
							new InternalSubscriptionHandler(this).getRemoveInternalSubscriptionHandler().removeInternalSubscription(aci, subscription, entityManager, getImplementedControlChildSbb());
						}
						else {
							new SipSubscriptionHandler(this).getRemoveSipSubscriptionHandler().removeSipSubscription(aci, subscription, entityManager, getImplementedControlChildSbb());
						}
					}
				} catch (Exception e) {
					logger.error("failed to notify internal subscriber", e);
				}

			}

			// close entity manager
			entityManager.close();
		}
	}
	
	// --- INTERNAL SUBSCRIPTIONS

	private void subscribe(String subscriber, String subscriberDisplayName,
			String notifier, String eventPackage, String subscriptionId,
			int expires, String content, String contentType,
			String contentSubtype, boolean eventList) {

		EntityManager entityManager = getEntityManager();

		getInternalSubscriptionHandler().getNewInternalSubscriptionHandler()
				.newInternalSubscription(subscriber, subscriberDisplayName,
						notifier, eventPackage, subscriptionId, expires,
						content, contentType, contentSubtype, eventList, entityManager,
						getImplementedControlChildSbb());

		entityManager.flush();
		entityManager.close();
	}
	
	public void subscribe(String subscriber, String subscriberDisplayName,
			String notifier, String eventPackage, String subscriptionId,
			int expires, String content, String contentType,
			String contentSubtype) {

		if (getConfiguration().getEventListSupportOn()) {
			int rlsResponse = getEventListControlChildSbb().validateSubscribeRequest(subscriber, notifier, eventPackage, null);

			switch (rlsResponse) {
			case Response.NOT_FOUND:
				subscribe(subscriber, subscriberDisplayName, notifier, eventPackage, subscriptionId, expires, content, contentType, contentSubtype, false);
				break;
			case Response.OK:
				subscribe(subscriber, subscriberDisplayName, notifier, eventPackage, subscriptionId, expires, content, contentType, contentSubtype, true);
				break;
			default:
				// rls provided an error while validating event list possible subscribe
				getParentSbbCMP().subscribeError(subscriber, notifier, eventPackage, subscriptionId, rlsResponse);
			break;
			}
		}
		else {
			subscribe(subscriber, subscriberDisplayName, notifier, eventPackage, subscriptionId, expires, content, contentType, contentSubtype, false);
		}
	}

	public void resubscribe(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires) {

		EntityManager entityManager = getEntityManager();

		getInternalSubscriptionHandler()
				.getRefreshInternalSubscriptionHandler()
				.refreshInternalSubscription(subscriber, notifier,
						eventPackage, subscriptionId, expires, entityManager,
						getImplementedControlChildSbb());

		entityManager.flush();
		entityManager.close();
	}

	public void unsubscribe(String subscriber, String notifier,
			String eventPackage, String subscriptionId) {

		EntityManager entityManager = getEntityManager();

		getInternalSubscriptionHandler().getRemoveInternalSubscriptionHandler()
				.removeInternalSubscription(subscriber, notifier, eventPackage,
						subscriptionId, entityManager, getImplementedControlChildSbb());

		entityManager.flush();
		entityManager.close();

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

	/**
	 * Removes a subscription data.
	 * 
	 * @param entityManager
	 * @param subscription
	 * @param dialog
	 * @param aci
	 * @param childSbb
	 */
	public void removeSubscriptionData(EntityManager entityManager,
			Subscription subscription, Dialog dialog,
			ActivityContextInterface aci,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {
		// warn event package impl that subscription is to be removed, may need
		// to clean up resources
		childSbb.removingSubscription(subscription);
		// remove subscription
		entityManager.remove(subscription);
		// remove aci name binding
		try {
			getActivityContextNamingfacility().unbind(
					subscription.getKey().toString());
		} catch (Exception e) {
			logger.error("failed to unbind subscription aci name");
		}
		// if dialog is not null that's a sip subscription and we need to verify
		// if dialog is not needed anymore (and remove if that's the case)
		if (dialog != null) {
			// get subscriptions of dialog from persistence
			List<?> subscriptionsInDialog = Subscription.getDialogSubscriptions(
					entityManager, dialog.getCallId().getCallId(), dialog
							.getRemoteTag());
			if (subscriptionsInDialog.size() == 0) {
				if (logger.isInfoEnabled()) {
					logger.info("No more subscriptions on dialog, deleting...");
				}
				// no more subscriptions in dialog, detach and delete the dialog
				aci.detach(getSbbContext().getSbbLocalObject());
				dialog.delete();
			}
		}

		// note: we don't remove null acis here, otherwise the final notify
		// couldn't be handled

		entityManager.flush();

		if (logger.isInfoEnabled()) {
			logger.info("Removed data for " + subscription);
		}
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

	/**
	 * Used to fire events to notify the right sbb entity of a state change
	 * 
	 * @param event
	 * @param aci
	 * @param address
	 */
	public abstract void fireInternalNotifyEvent(InternalNotifyEvent event,
			ActivityContextInterface aci, Address address);
}