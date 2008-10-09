package org.mobicents.slee.sipevent.server.subscription;

import javax.slee.SbbLocalObject;

import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;

/**
 * Call back interface for the parent sbb of the {@link SubscriptionClientControlSbbLocalObject}.
 * Provides the responses to the requests sent by the parent sbb and event notifications.
 * @author Eduardo Martins
 *
 */
public interface SubscriptionClientControlParentSbbLocalObject extends SbbLocalObject {

	/**
	 * informs the parent sbb that a subscribe request was successful
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 * @param expires 
	 */
	public void subscribeOk(String subscriber, String notifier, String eventPackage, String subscriptionId, int expires);
	
	/**
	 * informs the parent sbb that a subscribe request was not successful
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 * @param error the sip error response status code 
	 */
	public void subscribeError(String subscriber, String notifier, String eventPackage, String subscriptionId, int error);
	
	/**
	 * informs the parent sbb that a unsubscribe request was successful
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 */
	public void unsubscribeOk(String subscriber, String notifier, String eventPackage, String subscriptionId);
	
	/**
	 * informs the parent sbb that a unsubscribe request was not successful
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 * @param error the sip error response status code 
	 */
	public void unsubscribeError(String subscriber, String notifier, String eventPackage, String subscriptionId, int error);
	
	/**
	 * 
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param subscriptionId
	 * @param event
	 * @param status
	 * @param document
	 * @param contentType
	 * @param contentSubtype
	 */
	public void notifyEvent(String subscriber, String notifier, String eventPackage, String subscriptionId, Subscription.Event event, Subscription.Status status, String document, String contentType, String contentSubtype);
	
}
