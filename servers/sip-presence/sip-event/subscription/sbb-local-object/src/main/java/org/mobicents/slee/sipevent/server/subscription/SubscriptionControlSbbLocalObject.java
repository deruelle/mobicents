package org.mobicents.slee.sipevent.server.subscription;

public interface SubscriptionControlSbbLocalObject extends ImplementedSubscriptionControlParentSbbLocalObject {

	/**
	 * creates or refreshes an internal subscription
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 * @param expires 
	 * @param content
	 * @param contentType
	 * @param contentSubtype
	 */
	public void newInternalSubscription(String subscriber, String notifier, String eventPackage, String subscriptionId, int expires, String content, String contentType, String contentSubtype);
	
	/**
	 * creates or refreshes an internal subscription
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 * @param expires 
	 */
	public void refreshInternalSubscription(String subscriber, String notifier, String eventPackage, String subscriptionId, int expires);
	
	/**
	 * Requests the termination of an internal subscription.
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 */
	public void removeInternalSubscription(String subscriber, String notifier, String eventPackage, String subscriptionId);
}
