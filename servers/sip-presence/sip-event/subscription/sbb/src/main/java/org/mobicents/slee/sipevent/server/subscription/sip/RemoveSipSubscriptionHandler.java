package org.mobicents.slee.sipevent.server.subscription.sip;

import java.util.List;

import javax.persistence.EntityManager;
import javax.sip.Dialog;
import javax.sip.ResponseEvent;
import javax.sip.header.EventHeader;
import javax.slee.ActivityContextInterface;
import javax.slee.facilities.TimerEvent;

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
	 * Handles a request to remove an exisiting SIP subscription
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
			logger.info("Status changed for " + subscription);
			// remove subscription data
			removeSipSubscriptionData(entityManager, subscription, dialog, aci,
					childSbb);
		} else if (subscription.getStatus().equals(Subscription.Status.waiting)) {
			logger.info("Status changed for " + subscription);
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
	 * a timer has ocurred in a dialog regarding a SIP subscription
	 * 
	 * @param event
	 * @param aci
	 */
	public void sipSubscriptionExpired(TimerEvent event,
			ActivityContextInterface aci, Dialog dialog) {

		// create jpa entity manager
		EntityManager entityManager = sipSubscriptionHandler.sbb
				.getEntityManager();

		// get subscription
		Subscription subscription = (Subscription) entityManager
				.createNamedQuery("selectSubscriptionFromTimerID")
				.setParameter("timerID", event.getTimerID()).getSingleResult();

		if (subscription != null) {

			logger.info("Timer expired for " + subscription);

			ImplementedSubscriptionControlSbbLocalObject childSbb = null;
			try {
				childSbb = sipSubscriptionHandler.sbb
						.getImplementedControlChildSbb();
			} catch (Exception e) {
				logger.error("Failed to get child sipSubscriptionHandler.sbb",
						e);
				return;
			}

			// check subscription status
			if (subscription.getStatus().equals(Subscription.Status.waiting)) {
				// change subscription status
				subscription.changeStatus(Subscription.Event.giveup);
				logger.info("Status changed for " + subscription);
				// notify winfo subscription(s)
				sipSubscriptionHandler.sbb.getWInfoSubscriptionHandler()
						.notifyWinfoSubscriptions(entityManager, subscription,
								childSbb);
				// remove subscription data
				removeSipSubscriptionData(entityManager, subscription, dialog,
						aci, childSbb);
			} else {
				// remove subscription
				removeSipSubscription(aci, subscription, entityManager,
						childSbb);
				entityManager.flush();
			}
			// close entity manager
			entityManager.close();
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
				logger.info("Removing " + subscription.getKey()
						+ " data due to error on notify response.");
				try {
					removeSipSubscriptionData(entityManager, subscription,
							dialog, sipSubscriptionHandler.sbb
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

	/**
	 * Removes a SIP subscription data.
	 * 
	 * @param entityManager
	 * @param subscription
	 * @param dialog
	 * @param aci
	 * @param sipSubscriptionHandler.sbb
	 * @param childSbb
	 */
	public void removeSipSubscriptionData(EntityManager entityManager,
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
			sipSubscriptionHandler.sbb.getActivityContextNamingfacility()
					.unbind(subscription.getKey().toString());
		} catch (Exception e) {
			logger.error("failed to unbind subscription dialog aci name");
		}
		// verify if dialog is not needed anymore (and remove if that's the
		// case)
		if (dialog != null) {
			verifyDialogSubscriptions(entityManager, subscription, dialog, aci);
		}
		entityManager.flush();

		logger.info("Removed data for " + subscription);
	}

	/**
	 * Removes the specified dialog if no more subscriptions exists
	 * 
	 * @param entityManager
	 * @param removedSubscription
	 * @param dialog
	 * @param dialogAci
	 */
	private void verifyDialogSubscriptions(EntityManager entityManager,
			Subscription removedSubscription, Dialog dialog,
			ActivityContextInterface dialogAci) {
		// get subscriptions of dialog from persistence
		List subscriptionsInDialog = Subscription.getDialogSubscriptions(
				entityManager, dialog.getCallId().getCallId(),dialog.getRemoteTag());
		if (subscriptionsInDialog.size() == 0) {
			logger.info("No more subscriptions on dialog, deleting...");
			// no more subscriptions in dialog, detach and delete the dialog
			dialogAci.detach(sipSubscriptionHandler.sbb.getSbbContext()
					.getSbbLocalObject());
			dialog.delete();
		}
	}
}
