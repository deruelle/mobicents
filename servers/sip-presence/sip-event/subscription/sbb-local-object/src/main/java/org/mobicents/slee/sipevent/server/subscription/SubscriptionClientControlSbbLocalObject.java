package org.mobicents.slee.sipevent.server.subscription;

import javax.slee.SbbLocalObject;

public interface SubscriptionClientControlSbbLocalObject extends SbbLocalObject {

	/**
	 * Stores the parent sbb local object used for callbacks.
	 * 
	 * @param sbbLocalObject
	 */
	public void setParentSbb(
			SubscriptionClientControlParentSbbLocalObject sbbLocalObject);

	/**
	 * creates an internal subscription
	 * 
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 * @param expires
	 * @param content
	 * @param contentType
	 * @param contentSubtype
	 */
	public void subscribe(String subscriber, String subscriberdisplayName,
			String notifier, String eventPackage, String subscriptionId,
			int expires, String content, String contentType,
			String contentSubtype);

	/**
	 * refreshes an internal subscription
	 * 
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 * @param expires
	 */
	public void resubscribe(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires);

	/**
	 * Requests the termination of an internal subscription.
	 * 
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 */
	public void unsubscribe(String subscriber, String notifier,
			String eventPackage, String subscriptionId);

}
