package org.mobicents.slee.sipevent.server.subscription.sip;

import gov.nist.javax.sip.Utils;

import javax.persistence.EntityManager;
import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.address.Address;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.SbbLocalObject;

import net.java.slee.resource.sip.DialogActivity;

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
public class NewSipSubscriptionHandler {

	private static Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	private SipSubscriptionHandler sipSubscriptionHandler;

	public NewSipSubscriptionHandler(
			SipSubscriptionHandler sipSubscriptionHandler) {
		this.sipSubscriptionHandler = sipSubscriptionHandler;
	}

	/**
	 * Starts the process of handling a new sip subscription
	 * 
	 * @param event
	 * @param aci
	 * @param eventPackage
	 * @param eventId
	 * @param expires
	 * @param entityManager
	 * @param childSbb
	 */
	public void newSipSubscription(RequestEvent event,
			ActivityContextInterface aci, String eventPackage, String eventId,
			int expires, EntityManager entityManager,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		// get subscription data from request
		Address fromAddress = ((FromHeader) event.getRequest().getHeader(
				FromHeader.NAME)).getAddress();
		String subscriber = fromAddress.getURI().toString();
		ToHeader toHeader = ((ToHeader) event.getRequest().getHeader(
				ToHeader.NAME));
		String notifier = toHeader.getAddress().getURI().toString();

		// create dialog if does not exists
		if (event.getDialog() == null) {
			try {
				sipSubscriptionHandler.sbb.getSipProvider().getNewDialog(
						event.getServerTransaction());
			} catch (Exception e) {
				logger.error("Can't create dialog", e);
				// cleanup
				try {
					Response response = sipSubscriptionHandler.sbb
							.getMessageFactory().createResponse(
									Response.SERVER_INTERNAL_ERROR,
									event.getRequest());
					response = sipSubscriptionHandler
							.addContactHeader(response);
					event.getServerTransaction().sendResponse(response);
					if (logger.isDebugEnabled()) {
						logger.debug("Response sent:\n" + response.toString());
					}
				} catch (Exception f) {
					logger.error("Can't send RESPONSE", f);
				}
				return;
			}
		}
		// ask authorization
		if (eventPackage.endsWith(".winfo")) {
			// winfo package, only accept subscriptions when subscriber and
			// notifier are the same
			newSipSubscriptionAuthorization(event, subscriber, notifier,
					eventPackage, eventId, expires, (subscriber
							.equals(notifier) ? Response.OK
							: Response.FORBIDDEN), entityManager, childSbb);
		} else {
			childSbb.isSubscriberAuthorized(event, subscriber, notifier,
					eventPackage, eventId, expires);
		}
	}

	/**
	 * Used by {@link ImplementedSubscriptionControlSbbLocalObject} to provide
	 * the authorization to a new sip subscription request.
	 * 
	 * @param event
	 * @param subscriber
	 * @param notifier
	 * @param subscriptionKey
	 * @param expires
	 * @param responseCode
	 * @param entityManager
	 * @param childSbb
	 */
	public void newSipSubscriptionAuthorization(RequestEvent event,
			String subscriber, String notifier, String eventPackage,
			String eventId, int expires, int responseCode,
			EntityManager entityManager,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		Dialog dialog = event.getServerTransaction().getDialog();
		ActivityContextInterface dialogAci = null;

		// send response
		try {
			Response response = sipSubscriptionHandler.sbb.getMessageFactory()
					.createResponse(responseCode, event.getRequest());
			if (responseCode == Response.ACCEPTED
					|| responseCode == Response.OK) {
				ToHeader responseToHeader = (ToHeader) response
						.getHeader(ToHeader.NAME);
				responseToHeader.setTag(Utils.generateTag());
				// attach to dialog
				SbbLocalObject sbbLocalObject = sipSubscriptionHandler.sbb
						.getSbbContext().getSbbLocalObject();
				dialogAci = sipSubscriptionHandler.sbb
						.getSipActivityContextInterfaceFactory()
						.getActivityContextInterface((DialogActivity) dialog);
				dialogAci.attach(sbbLocalObject);
				// finish and send response
				response = sipSubscriptionHandler.addContactHeader(response);
				response.addHeader(sipSubscriptionHandler.sbb
						.getHeaderFactory().createExpiresHeader(expires));
				event.getServerTransaction().sendResponse(response);
				if (logger.isDebugEnabled()) {
					logger.debug("Response sent:\n" + response.toString());
				}
			} else {
				response = sipSubscriptionHandler.addContactHeader(response);
				event.getServerTransaction().sendResponse(response);
				logger.info("Subscription: subscriber=" + subscriber
						+ ",notifier=" + notifier + ",eventPackage="
						+ eventPackage + " not authorized (" + responseCode
						+ ")");
				if (logger.isDebugEnabled()) {
					logger.debug("Response sent:\n" + response.toString());
				}
				return;
			}
		} catch (Exception e) {
			logger.error("Can't send new subscription request's reponse", e);
			// cleanup
			try {
				Response response = sipSubscriptionHandler.sbb
						.getMessageFactory().createResponse(
								Response.SERVER_INTERNAL_ERROR,
								event.getRequest());
				response = sipSubscriptionHandler.addContactHeader(response);
				event.getServerTransaction().sendResponse(response);
				if (logger.isDebugEnabled()) {
					logger.debug("Response sent:\n" + response.toString());
				}
			} catch (Exception f) {
				logger.error("Can't send RESPONSE", f);
			}
			return;
		}

		// create subscription, initial status depends on authorization
		String subscriberDisplayName = ((FromHeader) event.getRequest()
				.getHeader(FromHeader.NAME)).getAddress().getDisplayName();
		SubscriptionKey subscriptionKey = new SubscriptionKey(dialog
				.getCallId().getCallId(), dialog.getRemoteTag(), eventPackage,
				eventId);
		Subscription.Status initialStatus = responseCode == Response.ACCEPTED ? Subscription.Status.pending
				: Subscription.Status.active;
		Subscription subscription = new Subscription(subscriptionKey,
				subscriber, notifier, initialStatus, subscriberDisplayName,
				expires);

		// notify subscriber
		try {
			sipSubscriptionHandler.getSipSubscriberNotificationHandler()
					.createAndSendNotify(entityManager, subscription, dialog,
							childSbb);
		} catch (Exception e) {
			logger.error("failed to notify subscriber", e);
		}

		// notify winfo subscribers
		sipSubscriptionHandler.sbb
				.getWInfoSubscriptionHandler()
				.notifyWinfoSubscriptions(entityManager, subscription, childSbb);

		// bind name for dialog aci
		try {
			sipSubscriptionHandler.sbb.getActivityContextNamingfacility().bind(
					dialogAci, subscriptionKey.toString());
		} catch (Exception e) {
			logger.error("failed to bind a name to dialog's aci", e);
		}

		// set new timer
		sipSubscriptionHandler.sbb.setSubscriptionTimerAndPersistSubscription(
				entityManager, subscription, expires + 1, dialogAci);

		logger.info("Created " + subscription);
	}
}