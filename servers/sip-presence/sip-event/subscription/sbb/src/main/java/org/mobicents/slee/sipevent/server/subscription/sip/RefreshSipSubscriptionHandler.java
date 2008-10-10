package org.mobicents.slee.sipevent.server.subscription.sip;

import javax.persistence.EntityManager;
import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;

/**
 * Handles the refresh of a SIP subscription
 * 
 * @author martins
 * 
 */
public class RefreshSipSubscriptionHandler {

	private static Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	private SipSubscriptionHandler sipSubscriptionHandler;

	public RefreshSipSubscriptionHandler(
			SipSubscriptionHandler sipSubscriptionHandler) {
		this.sipSubscriptionHandler = sipSubscriptionHandler;
	}

	/**
	 * Refreshes an existing SIP Subscription.
	 * 
	 * @param event
	 * @param aci
	 * @param expires
	 * @param subscription
	 * @param entityManager
	 * @param sbb
	 * @param childSbb
	 */
	public void refreshSipSubscription(RequestEvent event,
			ActivityContextInterface aci, int expires,
			Subscription subscription, EntityManager entityManager,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		// cancel actual timer
		sipSubscriptionHandler.sbb.getTimerFacility().cancelTimer(
				subscription.getTimerID());

		// refresh subscription
		subscription.refresh(expires);

		// send OK response
		try {
			Response response = sipSubscriptionHandler.sbb.getMessageFactory()
					.createResponse(Response.OK, event.getRequest());
			response = sipSubscriptionHandler.addContactHeader(response);
			response.addHeader(sipSubscriptionHandler.sbb.getHeaderFactory()
					.createExpiresHeader(expires));
			event.getServerTransaction().sendResponse(response);
			if (logger.isDebugEnabled()) {
				logger.debug("Response sent:\n" + response.toString());
			}
		} catch (Exception e) {
			logger.error("Can't send RESPONSE", e);
		}

		// notify subscriber
		try {
			sipSubscriptionHandler.getSipSubscriberNotificationHandler()
					.createAndSendNotify(entityManager, subscription,
							(Dialog) aci.getActivity(), childSbb);
		} catch (Exception e) {
			logger.error("failed to notify subscriber", e);
		}

		// set new timer
		sipSubscriptionHandler.sbb.setSubscriptionTimerAndPersistSubscription(
				entityManager, subscription, expires + 1, aci);

		if (logger.isInfoEnabled()) {
			logger.info("Refreshed " + subscription + " for " + expires
					+ " seconds");
		}
	}

}
