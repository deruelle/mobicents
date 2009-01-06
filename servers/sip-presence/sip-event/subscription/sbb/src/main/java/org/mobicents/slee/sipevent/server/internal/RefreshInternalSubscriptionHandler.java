package org.mobicents.slee.sipevent.server.internal;

import javax.persistence.EntityManager;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;

/**
 * Handles the refresh of an internal subscription
 * 
 * @author martins
 * 
 */
public class RefreshInternalSubscriptionHandler {

	private static Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	private InternalSubscriptionHandler internalSubscriptionHandler;

	public RefreshInternalSubscriptionHandler(
			InternalSubscriptionHandler sipSubscriptionHandler) {
		this.internalSubscriptionHandler = sipSubscriptionHandler;
	}

	/**
	 * Refreshes an internal subscription
	 * 
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 * @param expires
	 * @param entityManager
	 * @param childSbb
	 */
	public void refreshInternalSubscription(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires,
			EntityManager entityManager,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		SubscriptionControlSbb sbb = internalSubscriptionHandler.sbb;

		// create subscription key
		SubscriptionKey subscriptionKey = new SubscriptionKey(
				SubscriptionKey.NO_CALL_ID, SubscriptionKey.NO_REMOTE_TAG,
				eventPackage, subscriptionId);
		// find subscription
		Subscription subscription = entityManager.find(Subscription.class,
				subscriptionKey);

		if (subscription == null) {
			// subscription does not exists
			sbb.getParentSbbCMP().resubscribeError(subscriber, notifier,
					eventPackage, subscriptionId,
					Response.CONDITIONAL_REQUEST_FAILED);
			return;
		}

		// check if expires is not less than the allowed min expires
		if (expires >= sbb.getConfiguration().getMinExpires()) {
			// ensure expires is not bigger than max expires
			if (expires > sbb.getConfiguration().getMaxExpires()) {
				expires = sbb.getConfiguration().getMaxExpires();
			}
		} else {
			// expires is > 0 but < min expires, respond (Interval
			// Too Brief) with Min-Expires = MINEXPIRES
			sbb.getParentSbbCMP().resubscribeError(subscriber, notifier,
					eventPackage, subscriptionId, Response.INTERVAL_TOO_BRIEF);
			return;
		}

		ActivityContextInterface aci = sbb.getActivityContextNamingfacility()
				.lookup(subscriptionKey.toString());
		if (aci == null) {
			logger
					.error("Failed to retrieve aci for internal subscription with key "
							+ subscriptionKey);
			sbb.getParentSbbCMP().resubscribeError(subscriber, notifier,
					eventPackage, subscriptionId,
					Response.SERVER_INTERNAL_ERROR);
			return;
		}

		// cancel actual timer
		internalSubscriptionHandler.sbb.getTimerFacility().cancelTimer(
				subscription.getTimerID());

		// refresh subscription
		subscription.refresh(expires);

		// send OK response
		internalSubscriptionHandler.sbb.getParentSbbCMP().resubscribeOk(
				subscriber, notifier, eventPackage, subscriptionId, expires);

		if (!subscription.getResourceList()) {
			// notify subscriber
			internalSubscriptionHandler.getInternalSubscriberNotificationHandler()
			.notifyInternalSubscriber(entityManager, subscription, aci,
					childSbb);
		}

		// set new timer
		internalSubscriptionHandler.sbb
				.setSubscriptionTimerAndPersistSubscription(entityManager,
						subscription, expires + 1, aci);

		if (logger.isInfoEnabled()) {
			logger.info("Refreshed " + subscription + " for " + expires
					+ " seconds");
		}
		
		if (subscription.getResourceList()) {
			// it's a resource list subscription thus pas control to rls
			internalSubscriptionHandler.sbb.getEventListControlChildSbb().refreshSubscription(subscription);
		}
	}
}
