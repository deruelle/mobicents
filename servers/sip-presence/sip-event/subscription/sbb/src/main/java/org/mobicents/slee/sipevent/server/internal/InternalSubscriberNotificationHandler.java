package org.mobicents.slee.sipevent.server.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

import javax.persistence.EntityManager;
import javax.sip.header.ContentTypeHeader;
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
public class InternalSubscriberNotificationHandler {

	private static Logger logger = Logger
			.getLogger(SubscriptionControlSbb.class);

	private InternalSubscriptionHandler internalSubscriptionHandler;

	public InternalSubscriberNotificationHandler(
			InternalSubscriptionHandler sbb) {
		this.internalSubscriptionHandler = sbb;
	}

	public void notifyInternalSubscriber(EntityManager entityManager,
			Subscription subscription, ActivityContextInterface aci,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {
		
		NotifyContent notifyContent = null; 
		// only get notify content if subscription status is active
		if (subscription.getStatus().equals(Subscription.Status.active)) {
			notifyContent = childSbb.getNotifyContent(subscription); 
		}
			
		if (notifyContent != null) {
			notifyInternalSubscriber(entityManager, subscription, notifyContent
					.getContent(), notifyContent.getContentTypeHeader(), childSbb);
		}
		else {
			notifyInternalSubscriber(entityManager, subscription, null, null, childSbb);
		}
		
	}

	public void notifyInternalSubscriber(EntityManager entityManager,
			Subscription subscription, String content,
			ContentTypeHeader contentTypeHeader,
			ActivityContextInterface subscriptionACI) {
		
			String contentType = null;
			String contentSubtype = null;
			if (contentTypeHeader != null) {
				contentType = contentTypeHeader.getContentType();
				contentSubtype = contentTypeHeader.getContentSubType();
			}
	
			// if subscription status is waiting notify terminated status
			Subscription.Status status = subscription.getStatus();
			if (status.equals(Subscription.Status.waiting)) {
				status = Subscription.Status.terminated;
			}
			// put last event if subscription terminated
			Subscription.Event lastEvent = null;
			if (status.equals(Subscription.Status.terminated)) {
				lastEvent = subscription.getLastEvent();
			}
			InternalNotifyEvent internalNotifyEvent = new InternalNotifyEvent(
				subscription.getSubscriber(), subscription.getNotifier(),
				subscription.getKey().getEventPackage(), subscription.getKey()
						.getRealEventId(), lastEvent,
				status, content, contentType, contentSubtype);

		internalSubscriptionHandler.sbb.fireInternalNotifyEvent(
				internalNotifyEvent, subscriptionACI, null);
		if (logger.isDebugEnabled()) {
			logger.debug("Notifying Internal Subscriber:"
					+ internalNotifyEvent.toString());
		}
	}

	public void notifyInternalSubscriber(EntityManager entityManager,
			Subscription subscription, Object content,
			ContentTypeHeader contentTypeHeader,
			ImplementedSubscriptionControlSbbLocalObject childSbb) {

		try {
			// get subscription aci
			ActivityContextInterface aci = internalSubscriptionHandler.sbb
					.getActivityContextNamingfacility().lookup(
							subscription.getKey().toString());
			if (aci != null) {
				if (!subscription.getResourceList()) {
					notifyInternalSubscriber(entityManager, subscription,
							(content != null ? getFilteredNotifyContent(subscription, content,
									childSbb) : null), contentTypeHeader, aci);
				}
				else {
					// resource list subscription, no filtering
					notifyInternalSubscriber(entityManager, subscription,
							(content != null ? (String)content : null), contentTypeHeader, aci);
				}
			} else {
				// clean up
				logger.warn("Unable to find subscription aci to notify "
						+ subscription.getKey()
						+ ". Removing subscription data");
				internalSubscriptionHandler.sbb.removeSubscriptionData(
						entityManager, subscription, null, null, childSbb);
			}

		} catch (Exception e) {
			logger.error("failed to notify internal subscriber", e);
		}
	}
	
	private String getFilteredNotifyContent(Subscription subscription,
			Object content, ImplementedSubscriptionControlSbbLocalObject childSbb)
			throws JAXBException, ParseException, IOException {

		// filter content per subscriber (notifier rules)
		Object filteredContent = childSbb.filterContentPerSubscriber(
				subscription.getSubscriber(), subscription.getNotifier(),
				subscription.getKey().getEventPackage(), content);
		// filter content per notifier (subscriber rules)
		// TODO
		// marshall content to string
		StringWriter stringWriter = new StringWriter();
		childSbb.getMarshaller().marshal(filteredContent, stringWriter);
		String result = stringWriter.toString();
		stringWriter.close();

		return result;
	}
}
