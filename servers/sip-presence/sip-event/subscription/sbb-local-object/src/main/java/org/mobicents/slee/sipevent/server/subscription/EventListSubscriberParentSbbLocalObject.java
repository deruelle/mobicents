package org.mobicents.slee.sipevent.server.subscription;

import javax.slee.SbbLocalObject;

import org.mobicents.slee.sipevent.server.subscription.eventlist.MultiPart;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;

public interface EventListSubscriberParentSbbLocalObject extends SbbLocalObject {

	/**
	 * Requests notification on a specific subscription, providing the multipart content.
	 * @param key
	 * @param multipart
	 */
	public void notifyEventListSubscriber(SubscriptionKey key, MultiPart multipart);
	
	/**
	 * Requests the subscription for the specified key. The subscription's entity manager must be available.
	 * @param key
	 * @return
	 */
	public Subscription getSubscription(SubscriptionKey key);
}
