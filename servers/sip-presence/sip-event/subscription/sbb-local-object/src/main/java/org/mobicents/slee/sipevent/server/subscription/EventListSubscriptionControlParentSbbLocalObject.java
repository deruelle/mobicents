package org.mobicents.slee.sipevent.server.subscription;

import javax.slee.SbbLocalObject;

import org.mobicents.slee.sipevent.server.subscription.eventlist.MultiPart;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;

/**
 * Call back interface for the parent sbb of the
 * {@link EventListSubscriptionControlSbbLocalObject}. Provides the responses to
 * the requests sent by the parent sbb and event notifications.
 * 
 * @author Eduardo Martins
 * 
 */
public interface EventListSubscriptionControlParentSbbLocalObject extends
		SbbLocalObject {

	/**
	 * @see EventListSubscriberParentSbbLocalObject#notifyEventListSubscriber(SubscriptionKey, Multipart)
	 */
	public void notifyEventListSubscriber(SubscriptionKey key, MultiPart multiPart);
	/**
	 * 
	 * @see EventListSubscriberParentSbbLocalObject#getSubscription(SubscriptionKey)
	 */
	public Subscription getSubscription(SubscriptionKey key);
	
	/**
	 * Warns the parent about an updated RLS Service.
	 * @param uri
	 */
	public void rlsServiceUpdated(String uri);
	
	/**
	 * Warns the parent about a RLS Service that was removed from the XDM
	 * @param uri
	 */
	public void rlsServiceRemoved(String uri);

}
