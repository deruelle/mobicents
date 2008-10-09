package org.mobicents.slee.sipevent.server.subscription;

import javax.sip.RequestEvent;
import javax.slee.SbbLocalObject;
import javax.xml.bind.Marshaller;

import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;

public interface ImplementedSubscriptionControlSbbLocalObject extends SbbLocalObject {

	/**
	 * Stores the parent sbb local object used for callbacks.
	 * @param sbbLocalObject
	 */
	public void setParentSbb(ImplementedSubscriptionControlParentSbbLocalObject sbbLocalObject);
	
	/**
	 * Asks authorization to concrete implementation for new subscription
	 * request SUBSCRIBE. This method is invoked from the abstract sip event
	 * subscription control to authorize a subscriber, the concrete
	 * implemeentation must then invoke newSubscriptionAuthorization(...) so the
	 * new subscription process is completed
	 * 
	 * @return
	 */
	public abstract void isSubscriberAuthorized(RequestEvent event, String subscriber,
			String notifier, String eventPackage, String eventId, int expires);
	
	/**
	 * Retrieves the content for the NOTIFY request of the specified Subscription
	 * @param subscription
	 * @return
	 */
	public abstract NotifyContent getNotifyContent(Subscription subscription);
	
	/**
	 * Filters content per subscriber.
	 * @return content filtered
	 */
	public abstract Object filterContentPerSubscriber(String subscriber, String notifier, String eventPackage, Object unmarshalledContent);
	
	/**
	 * Retrieves a JAXB Marshaller to convert a JAXBElement to a String. 
	 * @return
	 */
	public abstract Marshaller getMarshaller();
	

	/**
	 * notifies the event package impl that a subscription is about to be removed, may have resources to releases
	 */
	public void removingSubscription(Subscription subscription);
	
	/**
	 * the event packages supported
	 * 
	 * @return
	 */
	public String[] getEventPackages();
			
}
