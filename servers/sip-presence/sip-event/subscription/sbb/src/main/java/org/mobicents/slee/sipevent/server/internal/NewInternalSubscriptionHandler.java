package org.mobicents.slee.sipevent.server.internal;

import javax.persistence.EntityManager;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.nullactivity.NullActivity;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;

/**
 * Handles the creation of a new SIP subscription
 * 
 * @author martins
 * 
 */
public class NewInternalSubscriptionHandler {

	private static Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	private InternalSubscriptionHandler internalSubscriptionHandler;

	public NewInternalSubscriptionHandler(
			InternalSubscriptionHandler internalSubscriptionHandler) {
		this.internalSubscriptionHandler = internalSubscriptionHandler;
	}

	public void newInternalSubscription(String subscriber,
			String subscriberDisplayName, String notifier, String eventPackage,
			String subscriptionId, int expires, String content,
			String contentType, String contentSubtype, boolean eventList,
			EntityManager entityManager,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		if (logger.isDebugEnabled()) {
			logger.debug("newInternalSubscription()");
		}

		SubscriptionControlSbb sbb = internalSubscriptionHandler.sbb;

		// check if expires is not less than the allowed min expires
		if (expires >= sbb.getConfiguration().getMinExpires()) {
			// ensure expires is not bigger than max expires
			if (expires > sbb.getConfiguration().getMaxExpires()) {
				expires = sbb.getConfiguration().getMaxExpires();
			}
		} else {
			// expires is > 0 but < min expires, respond (Interval
			// Too Brief) with Min-Expires = MINEXPIRES
			sbb.getParentSbbCMP().subscribeError(subscriber, notifier,
					eventPackage, subscriptionId, Response.INTERVAL_TOO_BRIEF);
			return;
		}

		// create subscription key
		SubscriptionKey key = new SubscriptionKey(
				SubscriptionKey.NO_CALL_ID, SubscriptionKey.NO_REMOTE_TAG,
				eventPackage, subscriptionId);
		// find subscription
		Subscription subscription = entityManager.find(Subscription.class,
				key);

		if (subscription != null) {
			// subscription exists
			sbb.getParentSbbCMP().subscribeError(subscriber, notifier,
					eventPackage, subscriptionId,
					Response.CONDITIONAL_REQUEST_FAILED);
		} else {
			authorizeNewInternalSubscription(subscriber, subscriberDisplayName, notifier, key, expires, content, contentType, contentSubtype, eventList, entityManager, childSbb);						
		}
	}

	private void authorizeNewInternalSubscription(String subscriber, String subscriberDisplayName, String notifier, SubscriptionKey key, int expires, String content, String contentType, String contentSubtype, boolean eventList, EntityManager entityManager, ImplementedSubscriptionControlSbbLocalObject childSbb) {
		// ask authorization
		if (key.getEventPackage().endsWith(".winfo")) {
			// winfo package, only accept subscriptions when subscriber and
			// notifier are the same
			newInternalSubscriptionAuthorization(subscriber,
					subscriberDisplayName, notifier, key,
					expires, (subscriber.equals(notifier) ? Response.OK
							: Response.FORBIDDEN), eventList, entityManager, childSbb);
		} else {
			childSbb.isSubscriberAuthorized(subscriber,
					subscriberDisplayName, notifier, key,
					expires, content, contentType, contentSubtype,eventList,null);
		}
	}
	/**
	 * Used by {@link ImplementedSubscriptionControlSbbLocalObject} to provide
	 * the authorization to a new internal subscription request.
	 * 
	 * @param event
	 * @param subscriber
	 * @param notifier
	 * @param subscriptionKey
	 * @param expires
	 * @param responseCode
	 * @param eventList 
	 * @param entityManager
	 * @param childSbb
	 */
	public void newInternalSubscriptionAuthorization(String subscriber,
			String subscriberDisplayName, String notifier,
			SubscriptionKey subscriptionKey, int expires, int responseCode,
			boolean eventList, EntityManager entityManager,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		if (logger.isDebugEnabled()) {
			logger.debug("newInternalSubscriptionAuthorization()");
		}

		SubscriptionControlSbb sbb = internalSubscriptionHandler.sbb;
		ActivityContextInterface aci = null;

		// send response
		if (responseCode == Response.ACCEPTED || responseCode == Response.OK) {
			// create null activity, bind a name and attach the sbb
			NullActivity nullActivity = sbb.getNullActivityFactory()
					.createNullActivity();
			try {
				aci = sbb.getNullACIFactory().getActivityContextInterface(
						nullActivity);
				sbb.getActivityContextNamingfacility().bind(aci,
						subscriptionKey.toString());
			} catch (Exception e) {
				logger.error("Failed to create internal subscription aci", e);
				sbb.getParentSbbCMP().subscribeError(subscriber, notifier,
						subscriptionKey.getEventPackage(),
						subscriptionKey.getEventId(),
						Response.SERVER_INTERNAL_ERROR);
				return;
			}
			aci.attach(sbb.getSbbContext().getSbbLocalObject());
			// inform parent
			sbb.getParentSbbCMP().subscribeOk(subscriber, notifier,
					subscriptionKey.getEventPackage(),
					subscriptionKey.getEventId(), expires, responseCode);
		} else {
			sbb.getParentSbbCMP().subscribeError(subscriber, notifier,
					subscriptionKey.getEventPackage(),
					subscriptionKey.getEventId(), responseCode);
			if (logger.isInfoEnabled()) {
				logger.info("Subscription: subscriber=" + subscriber
						+ ",notifier=" + notifier + ",eventPackage="
						+ subscriptionKey.getEventPackage()
						+ " not authorized (" + responseCode + ")");
			}
			return;
		}

		// create subscription, initial status depends on authorization
		Subscription.Status initialStatus = responseCode == Response.ACCEPTED ? Subscription.Status.pending
				: Subscription.Status.active;
		Subscription subscription = new Subscription(subscriptionKey,
				subscriber, notifier, initialStatus, subscriberDisplayName,
				expires, eventList);

		if (!eventList || (responseCode == Response.ACCEPTED)) {
			// notify subscriber
			internalSubscriptionHandler.getInternalSubscriberNotificationHandler()
			.notifyInternalSubscriber(entityManager, subscription, aci,
					childSbb);
		}
		
		// notify winfo subscribers
		sbb.getWInfoSubscriptionHandler().notifyWinfoSubscriptions(
				entityManager, subscription, childSbb);

		// set new timer
		sbb.setSubscriptionTimerAndPersistSubscription(entityManager,
				subscription, expires + 1, aci);

		if (eventList && (responseCode == Response.OK)) {
			// resource list and active subscription, ask the event list control child to create the subscription 
			if (!internalSubscriptionHandler.sbb.getEventListControlChildSbb().createSubscription(subscription)) {
				internalSubscriptionHandler.getRemoveInternalSubscriptionHandler().removeInternalSubscription(aci, subscription, entityManager, childSbb);
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("Created " + subscription);
		}
	}
}