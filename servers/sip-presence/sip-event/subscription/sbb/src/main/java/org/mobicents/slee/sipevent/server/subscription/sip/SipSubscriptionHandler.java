package org.mobicents.slee.sipevent.server.subscription.sip;

import gov.nist.javax.sip.header.ims.PChargingFunctionAddressesHeader;
import gov.nist.javax.sip.header.ims.PChargingVectorHeader;

import java.text.ParseException;

import javax.persistence.EntityManager;
import javax.sip.Dialog;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.internal.InternalSubscriptionHandler;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;

/**
 * Handler for SIP SUBSCRIBE events.
 * 
 * @author martins
 * 
 */
public class SipSubscriptionHandler {

	private static Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	protected SubscriptionControlSbb sbb;
	private NewSipSubscriptionHandler newSipSubscriptionHandler;
	private RefreshSipSubscriptionHandler refreshSipSubscriptionHandler;
	private RemoveSipSubscriptionHandler removeSipSubscriptionHandler;
	private SipSubscriberNotificationHandler sipSubscriberNotificationHandler;

	public SipSubscriptionHandler(SubscriptionControlSbb sbb) {
		this.sbb = sbb;
		newSipSubscriptionHandler = new NewSipSubscriptionHandler(this);
		refreshSipSubscriptionHandler = new RefreshSipSubscriptionHandler(this);
		removeSipSubscriptionHandler = new RemoveSipSubscriptionHandler(this);
		sipSubscriberNotificationHandler = new SipSubscriberNotificationHandler(
				this);
	}

	// --- GETTERS

	public NewSipSubscriptionHandler getNewSipSubscriptionHandler() {
		return newSipSubscriptionHandler;
	}

	public RefreshSipSubscriptionHandler getRefreshSipSubscriptionHandler() {
		return refreshSipSubscriptionHandler;
	}

	public RemoveSipSubscriptionHandler getRemoveSipSubscriptionHandler() {
		return removeSipSubscriptionHandler;
	}

	public SipSubscriberNotificationHandler getSipSubscriberNotificationHandler() {
		return sipSubscriberNotificationHandler;
	}

	// --- LOGIC

	/**
	 * SIP SUBSCRIBE event processing
	 * 
	 * @param event
	 * @param aci
	 */
	public void processRequest(RequestEvent event, ActivityContextInterface aci) {

		// get child sbb that handles all the concrete event package subscription logic
		ImplementedSubscriptionControlSbbLocalObject childSbb = sbb
				.getImplementedControlChildSbb();
		if (childSbb == null) {
			try {
				// create response
				Response response = sbb.getMessageFactory().createResponse(
						Response.SERVER_INTERNAL_ERROR, event.getRequest());
				event.getServerTransaction().sendResponse(response);
				if (logger.isDebugEnabled()) {
					logger.debug("Response sent:\n" + response.toString());
				}
			} catch (Exception f) {
				logger.error("Can't send error response!", f);
			}
			return;
		}

		EntityManager entityManager = sbb.getEntityManager();

		if (logger.isDebugEnabled()) {
			logger.debug("Processing SUBSCRIBE request...");
		}

		// get event header
		EventHeader eventHeader = (EventHeader) event.getRequest().getHeader(
				EventHeader.NAME);
		if (eventHeader != null) {
			// check event package
			String eventPackage = eventHeader.getEventType();
			if (acceptsEventPackage(eventPackage, childSbb)) {

				// process expires header
				ExpiresHeader expiresHeader = event.getRequest().getExpires();
				int expires;

				// if expires does not exist then set it's value to default
				// value
				if (expiresHeader == null) {
					expires = sbb.getConfiguration().getDefaultExpires();
				} else {
					expires = expiresHeader.getExpires();
				}

				// check expires value
				if (expires > 0) {
					// check if expires is not less than the allowed min expires
					if (expires >= sbb.getConfiguration().getMinExpires()) {
						// ensure expires is not bigger than max expires
						if (expires > sbb.getConfiguration().getMaxExpires()) {
							expires = sbb.getConfiguration().getMaxExpires();
						}
						// new subscription or subscription refresh ?
						Dialog dialog = event.getDialog();
						if (dialog == null) {
							// no dialog means it's a new subscription for sure
							newSipSubscriptionHandler.newSipSubscription(event,
									aci, eventPackage,
									eventHeader.getEventId(), expires,
									entityManager, childSbb);
						} else {
							String eventId = eventHeader.getEventId();
							// trying to create or refresh a subscription
							Subscription subscription = Subscription
									.getSubscription(entityManager, dialog
											.getCallId().getCallId(), dialog
											.getRemoteTag(), eventPackage,
											eventId);
							if (subscription != null) {
								// subscription exists
								if (subscription.getStatus().equals(
										Subscription.Status.active)
										|| subscription.getStatus().equals(
												Subscription.Status.pending)) {
									// subscription status permits refresh
									refreshSipSubscriptionHandler
											.refreshSipSubscription(event, aci,
													expires, subscription,
													entityManager, childSbb);
								} else {
									// subscription status does not permits
									// refresh
									sendResponse(
											Response.CONDITIONAL_REQUEST_FAILED,
											event.getRequest(), event
													.getServerTransaction(),
											childSbb);
								}
							} else {
								// subscription does not exists
								newSipSubscriptionHandler.newSipSubscription(
										event, aci, eventPackage, eventHeader
												.getEventId(), expires,
										entityManager, childSbb);
							}
						}
					} else {
						// expires is > 0 but < min expires, respond (Interval
						// Too Brief) with Min-Expires = MINEXPIRES
						sendResponse(Response.INTERVAL_TOO_BRIEF, event
								.getRequest(), event.getServerTransaction(),
								childSbb);
					}
				}

				else if (expires == 0) {
					Dialog dialog = event.getDialog();
					if (dialog != null) {
						String eventId = eventHeader.getEventId();
						// trying to remove a subscription
						Subscription subscription = Subscription
								.getSubscription(entityManager, dialog
										.getCallId().getCallId(), dialog
										.getRemoteTag(), eventPackage, eventId);
						if (subscription != null) {
							if (subscription.getStatus().equals(
									Subscription.Status.active)
									|| subscription.getStatus().equals(
											Subscription.Status.pending)) {
								// subscription exists and status permits remove
								try {
									Response response = sbb.getMessageFactory()
											.createResponse(Response.OK,
													event.getRequest());
									response = addContactHeader(response);
									response.addHeader(sbb.getHeaderFactory()
											.createExpiresHeader(expires));
									event.getServerTransaction().sendResponse(
											response);
									if (logger.isDebugEnabled()) {
										logger.debug("Response sent:\n"
												+ response.toString());
									}
								} catch (Exception e) {
									logger.error("Can't send RESPONSE", e);
								}
								// remove subscription
								if (subscription.getResourceList()) {
									sbb.getEventListControlChildSbb().removeSubscription(subscription);
								}
								removeSipSubscriptionHandler
								.removeSipSubscription(aci,
										subscription, entityManager,
										childSbb);
							} else {
								// subscription does exists but status does
								// not permits removal
								sendResponse(
										Response.CONDITIONAL_REQUEST_FAILED,
										event.getRequest(), event
												.getServerTransaction(),
										childSbb);
							}
						} else {
							// subscription does not exists, one shot
							// subscription request, not supported
							sendResponse(Response.CONDITIONAL_REQUEST_FAILED,
									event.getRequest(), event
											.getServerTransaction(), childSbb);
						}
					} else {
						// dialog does not exists, one shot subscription
						// request, not supported
						sendResponse(Response.CONDITIONAL_REQUEST_FAILED, event
								.getRequest(), event.getServerTransaction(),
								childSbb);
					}
				} else {
					// expires can't be negative
					sendResponse(Response.BAD_REQUEST, event.getRequest(),
							event.getServerTransaction(), childSbb);
				}
			} else {
				// wrong event package, send bad event type error
				sendResponse(Response.BAD_EVENT, event.getRequest(), event
						.getServerTransaction(), childSbb);
			}
		} else {
			// subscribe does not have a event header
			sendResponse(Response.BAD_REQUEST, event.getRequest(), event
					.getServerTransaction(), childSbb);
		}

		entityManager.flush();
		entityManager.close();

	}

	/*
	 * Sends a response with the specified status code, adding additional
	 * headers if needed
	 */
	public void sendResponse(int responseCode, Request request,
			ServerTransaction serverTransaction,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {
		try {
			// create response
			Response response = sbb.getMessageFactory().createResponse(
					responseCode, request);
			// add headers if needed
			if (responseCode == Response.BAD_EVENT) {
				String allowEventsHeader = "";
				boolean first = true;
				for (String acceptedEventPackage : childSbb.getEventPackages()) {
					if (first) {
						allowEventsHeader += acceptedEventPackage + ","
								+ acceptedEventPackage + ".winfo";
						first = false;
					} else {
						allowEventsHeader += "," + acceptedEventPackage + ","
								+ acceptedEventPackage + ".winfo";
					}
				}
				response.addHeader(sbb.getHeaderFactory()
						.createAllowEventsHeader(allowEventsHeader));
			} else if (responseCode == Response.INTERVAL_TOO_BRIEF) {
				response.addHeader(sbb.getHeaderFactory()
						.createMinExpiresHeader(
								sbb.getConfiguration().getMinExpires()));
			}
			// 2xx response to SUBSCRIBE need a Contact
			response = addContactHeader(response);
			
			/*...aayush started adding here..(with ref issue#567)
			 * 
			 * The 200 OK of the SUBSCRIBE needs to preserve the P-charging-vector 
			 * header that was received in the request, adding a term-ioi parameter
			 * to it which points to the home domain.
			 */
			
			// 1. Get header from the request
			PChargingVectorHeader pcv = (PChargingVectorHeader) 
			request.getHeader(PChargingVectorHeader.NAME);
			
			// 2. Check for NULL, as the request may not come from IMS all the time.
			if(pcv!=null)
			{
			pcv.setTerminatingIOI(sbb.getConfiguration().getPChargingVectorHeaderTerminatingIOI());
			response.addHeader(pcv);
			}
			
			// We also need to get the P-charging-function-addresses header from the request
			// and add it to the response:
			
			PChargingFunctionAddressesHeader pcfa = (PChargingFunctionAddressesHeader) 
			request.getHeader(PChargingFunctionAddressesHeader.NAME);
			
			if(pcfa!=null)
				response.addHeader(pcfa);
			//...aayush added code till here.
			
			serverTransaction.sendResponse(response);
			if (logger.isDebugEnabled()) {
				logger.debug("Response sent:\n" + response.toString());
			}
		} catch (Exception e) {
			logger.error("Can't send response!", e);
		}
	}

	/*
	 * Adds subscription agent contact header to SIP response
	 */
	public Response addContactHeader(Response response) throws ParseException {

		if (response.getHeader(ContactHeader.NAME) != null) {
			response.removeHeader(ContactHeader.NAME);
		}
		ListeningPoint listeningPoint = sbb.getSipProvider().getListeningPoint(
				"udp");
		Address address = sbb.getAddressFactory().createAddress(
				sbb.getConfiguration().getContactAddressDisplayName()
						+ " <sip:" + listeningPoint.getIPAddress() + ">");
		((SipURI) address.getURI()).setPort(listeningPoint.getPort());
		response.addHeader(sbb.getHeaderFactory().createContactHeader(address));

		return response;
	}

	/**
	 * verifies if the specified event packaged is accepted
	 */
	public boolean acceptsEventPackage(String eventPackage,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {
		if (eventPackage != null) {
			for (String acceptedEventPackage : childSbb.getEventPackages()) {
				if (eventPackage.equals(acceptedEventPackage)
						|| eventPackage.equals(acceptedEventPackage + ".winfo")) {
					return true;
				}
			}
		}
		return false;
	}

}
