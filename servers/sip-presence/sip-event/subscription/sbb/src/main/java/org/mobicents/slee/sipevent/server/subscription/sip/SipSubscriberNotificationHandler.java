package org.mobicents.slee.sipevent.server.subscription.sip;

import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.header.HeaderFactoryExt;
import gov.nist.javax.sip.header.HeaderFactoryImpl;
import gov.nist.javax.sip.header.ims.PChargingVectorHeader;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

import javax.persistence.EntityManager;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.SipException;
import javax.sip.TransactionDoesNotExistException;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.message.Request;
import javax.slee.ActivityContextInterface;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.NotifyContent;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;

/**
 * Handles the notification of a SIP subscriber
 * 
 * @author martins
 * 
 */
public class SipSubscriberNotificationHandler {

	private static Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	private SipSubscriptionHandler sipSubscriptionHandler;

	public SipSubscriberNotificationHandler(
			SipSubscriptionHandler sipSubscriptionHandler) {
		this.sipSubscriptionHandler = sipSubscriptionHandler;
	}

	public void notifySipSubscriber(Object content,
			ContentTypeHeader contentTypeHeader, Subscription subscription,
			EntityManager entityManager,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		try {
			// get subscription dialog
			ActivityContextInterface dialogACI = sipSubscriptionHandler.sbb
					.getActivityContextNamingfacility().lookup(
							subscription.getKey().toString());
			if (dialogACI != null) {
				Dialog dialog = (Dialog) dialogACI.getActivity();
				// create notify
				Request notify = createNotify(dialog, subscription);
				// add content
				if (content != null) {
					notify = setNotifyContent(subscription, notify, content,
							contentTypeHeader, childSbb);
				}
				
				// ....aayush added code here (with ref issue #567)
				notify.addHeader(addPChargingVectorHeader());
				
				// send notify in dialog related with subscription
				dialog.sendRequest(sipSubscriptionHandler.sbb.getSipProvider()
						.getNewClientTransaction(notify));
				if (logger.isDebugEnabled()) {
					logger.debug("NotifySubscribers: subscription "
							+ subscription.getKey() + " sent request:\n"
							+ notify.toString());
				}
			} else {
				// clean up
				logger.warn("Unable to find dialog aci to notify subscription "
						+ subscription.getKey()
						+ ". Removing subscription data");
				sipSubscriptionHandler.sbb.removeSubscriptionData(
						entityManager, subscription, null, null, childSbb);
			}

		} catch (Exception e) {
			logger.error("failed to notify subscriber", e);
		}
	}

	public Request setNotifyContent(Subscription subscription, Request notify,
			Object content, ContentTypeHeader contentTypeHeader,
			ImplementedSubscriptionControlSbbLocalObject childSbb)
			throws JAXBException, ParseException, IOException {

		if (!subscription.getResourceList()) {
			// filter content per subscriber (notifier rules)
			Object filteredContent = childSbb.filterContentPerSubscriber(
					subscription.getSubscriber(), subscription.getNotifier(),
					subscription.getKey().getEventPackage(), content);
			// filter content per notifier (subscriber rules)
			// TODO
			// marshall content to string
			StringWriter stringWriter = new StringWriter();
			childSbb.getMarshaller().marshal(filteredContent, stringWriter);
			notify.setContent(stringWriter.toString(), contentTypeHeader);
			stringWriter.close();
		}
		else {
			// resource list subscription, no filtering
			notify.setContent(content, contentTypeHeader);
		}
		return notify;
	}

	/*
	 * for internal usage, creates a NOTIFY notify, asks the content to the
	 * concrete implementation component, and then sends the request to the
	 * subscriber
	 */
	public void createAndSendNotify(EntityManager entityManager,
			Subscription subscription, Dialog dialog,
			ImplementedSubscriptionControlSbbLocalObject childSbb)
			throws TransactionDoesNotExistException, SipException,
			ParseException {

		// create notify
		Request notify = createNotify(dialog, subscription);
		// add content if subscription is active
		if (subscription.getStatus().equals(Subscription.Status.active)) {
			if (subscription.getKey().getEventPackage().endsWith(".winfo")) {
				// winfo content, increment version before adding the content
				subscription.incrementVersion();
				entityManager.persist(subscription);
				entityManager.flush();
				notify.setContent(
						sipSubscriptionHandler.sbb
								.getWInfoSubscriptionHandler()
								.getFullWatcherInfoContent(entityManager,
										subscription),
						sipSubscriptionHandler.sbb
								.getWInfoSubscriptionHandler()
								.getWatcherInfoContentHeader());
			} else {
				// specific event package content
				NotifyContent notifyContent = childSbb
						.getNotifyContent(subscription);
				// add content
				if (notifyContent != null) {
					try {
						notify = setNotifyContent(subscription, notify,
								notifyContent.getContent(), notifyContent
										.getContentTypeHeader(), childSbb);
					} catch (Exception e) {
						logger.error("failed to set notify content", e);
					}
				}
			}
		}
		
		// ....aayush added code here (with ref issue #567)
		notify.addHeader(addPChargingVectorHeader());
		
		// send notify
		ClientTransaction clientTransaction = sipSubscriptionHandler.sbb
				.getSipProvider().getNewClientTransaction(notify);
		dialog.sendRequest(clientTransaction);
		if (logger.isDebugEnabled()) {
			logger.debug("Request sent:\n" + notify.toString());
		}

	}

	// creates a notify request and fills headers
	public Request createNotify(Dialog dialog, Subscription subscription) {

		Request notify = null;
		try {
			notify = dialog.createRequest(Request.NOTIFY);
			// add event header
			EventHeader eventHeader = sipSubscriptionHandler.sbb
					.getHeaderFactory().createEventHeader(
							subscription.getKey().getEventPackage());
			if (subscription.getKey().getRealEventId() != null)
				eventHeader.setEventId(subscription.getKey().getRealEventId());
			notify.setHeader(eventHeader);
			// add max forwards header
			notify.setHeader(sipSubscriptionHandler.sbb.getHeaderFactory()
					.createMaxForwardsHeader(
							sipSubscriptionHandler.sbb.getConfiguration()
									.getMaxForwards()));
			/*
			 * NOTIFY requests MUST contain a "Subscription-State" header with a
			 * value of "active", "pending", or "terminated". The "active" value
			 * indicates that the subscription has been accepted and has been
			 * authorized (in most cases; see section 5.2.). The "pending" value
			 * indicates that the subscription has been received, but that
			 * policy information is insufficient to accept or deny the
			 * subscription at this time. The "terminated" value indicates that
			 * the subscription is not active.
			 */
			SubscriptionStateHeader ssh = null;
			if (subscription.getStatus().equals(Subscription.Status.active)
					|| subscription.getStatus().equals(
							Subscription.Status.pending)) {
				ssh = sipSubscriptionHandler.sbb.getHeaderFactory()
						.createSubscriptionStateHeader(
								subscription.getStatus().toString());
				/*
				 * If the value of the "Subscription-State" header is "active"
				 * or "pending", the notifier SHOULD also include in the
				 * "Subscription- State" header an "expires" parameter which
				 * indicates the time remaining on the subscription.
				 */
				ssh.setExpires(subscription.getRemainingExpires());
			} else if (subscription.getStatus().equals(
					Subscription.Status.waiting)
					|| subscription.getStatus().equals(
							Subscription.Status.terminated)) {
				ssh = sipSubscriptionHandler.sbb.getHeaderFactory()
						.createSubscriptionStateHeader("terminated");
				/*
				 * If the value of the "Subscription-State" header is
				 * "terminated", the notifier SHOULD also include a "reason"
				 * parameter.
				 */
				if(subscription.getLastEvent() != null) { 
					ssh.setReasonCode(subscription.getLastEvent().toString());
				}
			}
			notify.addHeader(ssh);
			
			// if it's a RLS notify a required header must be present
			if (subscription.getResourceList()) {
				notify.addHeader(sipSubscriptionHandler.sbb.getHeaderFactory().createRequireHeader("eventlist"));
			}
			
		} catch (Exception e) {
			logger.error("unable to fill notify headers", e);
		}
		return notify;
	}
	/**
	 * 
	 * @return the newly created P-charging-vector header
	 * @throws ParseException 
	 */
	private PChargingVectorHeader addPChargingVectorHeader() throws ParseException
	{
		// aayush..started adding here.
		
		/*
		 * (with ref to issue #567)
		 * Need to add a P-charging-vector header here with a unique ICID parameter
		 * and an orig-ioi parameter pointing to the home domain of the PS.
		 */
		
		// sbb.getHeaderFactory() does not provide the API for creating P-headers.
		HeaderFactoryExt extensions = new HeaderFactoryImpl();
		
		// Ideally,there should also be an ICID generator in Utils, that generates a unique ICID.
		PChargingVectorHeader pcv = extensions.createChargingVectorHeader(Utils.getInstance().generateBranchId()+System.currentTimeMillis());
		pcv.setOriginatingIOI(sipSubscriptionHandler.sbb.getConfiguration().getPChargingVectorHeaderTerminatingIOI());
		
		return pcv;
		//aayush...added code till here.
		
	}
}
