package org.mobicents.slee.sipevent.server.subscription;

import javax.sip.header.ContentTypeHeader;
import javax.slee.SbbLocalObject;

public interface SubscriptionControlSbbLocalObject extends SbbLocalObject {

	/**
	 * Through this method the subscription control sbb can be informed that the state
	 * of the notifier has changed, allowing subscribers to be notified.
	 */
	public void notifySubscribers(String notifier, String eventPackage,
			Object content, ContentTypeHeader contentTypeHeader);
	
	/**
	 * Requests notification on a specific subscription, providing the content.
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 * @param eventId
	 * @param content
	 * @param contentTypeHeader
	 */
	public void notifySubscriber(String subscriber, String notifier, String eventPackage, String eventId,
			Object content, ContentTypeHeader contentTypeHeader);
}
