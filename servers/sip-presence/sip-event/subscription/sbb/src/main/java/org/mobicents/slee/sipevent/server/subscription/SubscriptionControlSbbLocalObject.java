package org.mobicents.slee.sipevent.server.subscription;

import javax.sip.header.ContentTypeHeader;
import javax.slee.SbbLocalObject;
import javax.xml.bind.JAXBElement;

public interface SubscriptionControlSbbLocalObject extends SbbLocalObject {

	/**
	 * Through this method the subscription control sbb can be informed that the state
	 * of the notifier has changed, allowing subscribers to be notified.
	 */
	public void notifySubscribers(String notifier, String eventPackage,
			JAXBElement content, ContentTypeHeader contentTypeHeader);
	
}
