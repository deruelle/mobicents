package org.mobicents.slee.sipevent.server.subscription;

import javax.slee.SbbLocalObject;

public interface SubscriptionClientControlSbbLocalObject extends SbbLocalObject {

	/**
	 * Stores the parent sbb local object used for callbacks.
	 * @param sbbLocalObject
	 */
	public void setParentSbb(SubscriptionClientControlParentSbbLocalObject sbbLocalObject);

	/**
	 * creates or refreshes a subscription
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 * @param expires 
	 */
	public void subscribe(String subscriber, String notifier, String eventPackage, String subscriptionId, int expires);
	
	/**
	 * Requests the termination of a subscription.
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 */
	public void unsubscribe(String subscriber, String notifier, String eventPackage, String subscriptionId);
	
	/**
	 * Shutdown the interface to the sip event server
	 */
	public void shutdown();
}
