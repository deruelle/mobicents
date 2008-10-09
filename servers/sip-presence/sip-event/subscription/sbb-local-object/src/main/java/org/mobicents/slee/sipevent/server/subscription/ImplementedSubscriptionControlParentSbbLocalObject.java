package org.mobicents.slee.sipevent.server.subscription;

import javax.sip.RequestEvent;
import javax.sip.header.ContentTypeHeader;
import javax.slee.SbbLocalObject;

import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;

public interface ImplementedSubscriptionControlParentSbbLocalObject extends SbbLocalObject {

	public void authorizationChanged(String subscriber, String notifier, String eventPackage, int authorizationCode);
	
	public void newSipSubscriptionAuthorization(RequestEvent event,
			String subscriber, String notifier,
			String eventPackage, String eventId, int expires, int responseCode);
	
	/**
	 * Through this method the subscription control sbb can be informed that the state
	 * of the notifier has changed, allowing subscribers to be notified.
	 */
	public void notifySubscribers(String notifier, String eventPackage,
			Object content, ContentTypeHeader contentTypeHeader);
	
	/**
	 * Requests notification on a specific subscription, providing the content.
	 * @param key
	 * @param content
	 * @param contentTypeHeader
	 */
	public void notifySubscriber(SubscriptionKey key,
			Object content, ContentTypeHeader contentTypeHeader);
}
