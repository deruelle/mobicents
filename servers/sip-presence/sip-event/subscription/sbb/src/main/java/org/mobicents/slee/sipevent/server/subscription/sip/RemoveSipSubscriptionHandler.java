package org.mobicents.slee.sipevent.server.subscription.sip;

import javax.persistence.EntityManager;
import javax.sip.Dialog;
import javax.sip.ResponseEvent;
import javax.sip.header.EventHeader;
import javax.slee.ActivityContextInterface;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;

/**
 * Handles the removal of a SIP subscription
 * 
 * @author martins
 * 
 */
public class RemoveSipSubscriptionHandler {

	private static Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	private SipSubscriptionHandler sipSubscriptionHandler;

	public RemoveSipSubscriptionHandler(
			SipSubscriptionHandler sipSubscriptionHandler) {
		this.sipSubscriptionHandler = sipSubscriptionHandler;
	}

	/**
	 * 
	 * Handles a request to remove an existing SIP subscription
	 * 
	 * @param aci
	 * @param eventPackage
	 * @param eventId
	 * @param subscription
	 * @param entityManager
	 * @param childSbb
	 */
	public void removeSipSubscription(ActivityContextInterface aci,
			Subscription subscription, EntityManager entityManager,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		// cancel timer
		sipSubscriptionHandler.sbb.getTimerFacility().cancelTimer(
				subscription.getTimerID());

		// change subscription state, simulate a timeout after a refresh of 0
		// secs
		subscription.changeStatus(Subscription.Event.timeout);

		// get dialog from aci
		Dialog dialog = (Dialog) aci.getActivity();

		// notify subscriber
		try {
			sipSubscriptionHandler.getSipSubscriberNotificationHandler()
					.createAndSendNotify(entityManager, subscription, dialog,
							childSbb);
		} catch (Exception e) {
			logger.error("failed to notify subscriber", e);
		}

		// notify winfo subscription(s)
		sipSubscriptionHandler.sbb
				.getWInfoSubscriptionHandler()
				.notifyWinfoSubscriptions(entityManager, subscription, childSbb);

		// check resulting subscription state
		if (subscription.getStatus().equals(Subscription.Status.terminated)) {
			if (logger.isInfoEnabled()) {
				logger.info("Status changed for " + subscription);
			}
			// remove subscription data
			sipSubscriptionHandler.sbb.removeSubscriptionData(entityManager,
					subscription, dialog, aci, childSbb);
		} else if (subscription.getStatus().equals(Subscription.Status.waiting)) {
			if (logger.isInfoEnabled()) {
				logger.info("Status changed for " + subscription);
			}
			// keep the subscription for default waiting time so notifier may
			// know about this attemp to subscribe him
			// refresh subscription
			int defaultWaitingExpires = sipSubscriptionHandler.sbb
					.getConfiguration().getDefaultWaitingExpires();
			subscription.refresh(defaultWaitingExpires);
			// set waiting timer
			sipSubscriptionHandler.sbb
					.setSubscriptionTimerAndPersistSubscription(entityManager,
							subscription, defaultWaitingExpires + 1, aci);
		}

	}

	/**
	 * removes a subscription due to error response on notify
	 * 
	 * @param event
	 */
	public void removeSipSubscriptionOnNotifyError(ResponseEvent event) {
		EntityManager entityManager = sipSubscriptionHandler.sbb
				.getEntityManager();
		EventHeader eventHeader = (EventHeader) event.getResponse().getHeader(
				EventHeader.NAME);
		Dialog dialog = event.getDialog();
		if (eventHeader != null && dialog != null) {
			Subscription subscription = Subscription.getSubscription(
					entityManager, dialog.getCallId().getCallId(), dialog
							.getRemoteTag(), eventHeader.getEventType(),
					eventHeader.getEventId());
			if (subscription != null) {
				if (logger.isInfoEnabled()) {
					logger.info("Removing " + subscription.getKey()
							+ " data due to error on notify response.");
				}
				try {
					sipSubscriptionHandler.sbb.removeSubscriptionData(
							entityManager, subscription, dialog,
							sipSubscriptionHandler.sbb
									.getActivityContextNamingfacility().lookup(
											subscription.getKey().toString()),
							sipSubscriptionHandler.sbb
									.getImplementedControlChildSbb());
					entityManager.flush();
				} catch (Exception e) {
					logger.error(
							"Failed to retrieve the implemented child sbb", e);
				}
			}
		}
		entityManager.close();
	}
}
