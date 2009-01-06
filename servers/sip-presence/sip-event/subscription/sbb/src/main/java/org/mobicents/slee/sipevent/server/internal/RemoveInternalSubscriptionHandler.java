package org.mobicents.slee.sipevent.server.internal;

import javax.persistence.EntityManager;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription.Status;

/**
 * Handles the removal of a SIP subscription
 * 
 * @author martins
 * 
 */
public class RemoveInternalSubscriptionHandler {

	private static Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	private InternalSubscriptionHandler internalSubscriptionHandler;

	public RemoveInternalSubscriptionHandler(
			InternalSubscriptionHandler sipSubscriptionHandler) {
		this.internalSubscriptionHandler = sipSubscriptionHandler;
	}

	public void removeInternalSubscription(String subscriber, String notifier,
			String eventPackage, String subscriptionId,
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
			sbb.getParentSbbCMP().unsubscribeError(subscriber, notifier,
					eventPackage, subscriptionId,
					Response.CONDITIONAL_REQUEST_FAILED);
			return;
		}

		ActivityContextInterface aci = sbb.getActivityContextNamingfacility()
				.lookup(subscriptionKey.toString());
		if (aci == null) {
			logger
					.error("Failed to retrieve aci for internal subscription with key "
							+ subscriptionKey);
			sbb.getParentSbbCMP().unsubscribeError(subscriber, notifier,
					eventPackage, subscriptionId,
					Response.SERVER_INTERNAL_ERROR);
			return;
		}

		// send OK response
		sbb.getParentSbbCMP().unsubscribeOk(subscriber, notifier, eventPackage,
				subscriptionId);

		if (subscription.getResourceList()) {
			internalSubscriptionHandler.sbb.getEventListControlChildSbb().removeSubscription(subscription);
		}
		
		removeInternalSubscription(aci, subscription, entityManager, childSbb);
		
	}

	public void removeInternalSubscription(ActivityContextInterface aci,
			Subscription subscription, EntityManager entityManager,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		// cancel timer
		internalSubscriptionHandler.sbb.getTimerFacility().cancelTimer(
				subscription.getTimerID());

		if (!subscription.getStatus().equals(Status.terminated) && !subscription.getStatus().equals(Status.waiting)) {
			// change subscription state
			subscription.setStatus(Subscription.Status.terminated);
			subscription.setLastEvent(null);
		}

		// notify subscriber
		internalSubscriptionHandler.getInternalSubscriberNotificationHandler()
		.notifyInternalSubscriber(entityManager, subscription, aci,
				childSbb);

		// notify winfo subscription(s)
		internalSubscriptionHandler.sbb
				.getWInfoSubscriptionHandler()
				.notifyWinfoSubscriptions(entityManager, subscription, childSbb);

		// check resulting subscription state
		if (subscription.getStatus().equals(Subscription.Status.terminated)) {
			if (logger.isInfoEnabled()) {
				logger.info("Status changed for " + subscription);
			}
			// remove subscription data
			internalSubscriptionHandler.sbb.removeSubscriptionData(
					entityManager, subscription, null, aci, childSbb);
		} else if (subscription.getStatus().equals(Subscription.Status.waiting)) {
			if (logger.isInfoEnabled()) {
				logger.info("Status changed for " + subscription);
			}
			// keep the subscription for default waiting time so notifier may
			// know about this attemp to subscribe him
			// refresh subscription
			int defaultWaitingExpires = internalSubscriptionHandler.sbb
					.getConfiguration().getDefaultWaitingExpires();
			subscription.refresh(defaultWaitingExpires);
			// set waiting timer
			internalSubscriptionHandler.sbb
					.setSubscriptionTimerAndPersistSubscription(entityManager,
							subscription, defaultWaitingExpires + 1, aci);
		}

	}

}
