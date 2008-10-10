package org.mobicents.slee.sippresence.client;

import org.mobicents.slee.sipevent.server.publication.PublicationClientControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionClientControlParentSbbLocalObject;

/**
 * 
 * 
 * @author martins
 *
 */
public interface InternalPresenceClientControlSbbLocalObject extends
		PresenceClientControlSbbLocalObject,
		PublicationClientControlParentSbbLocalObject,
		SubscriptionClientControlParentSbbLocalObject {

}
