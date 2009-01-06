package org.mobicents.slee.sipevent.server.subscription;

import org.mobicents.slee.sipevent.server.subscription.eventlist.FlatList;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;

public interface EventListSubscriberSbbLocalObject extends SubscriptionClientControlParentSbbLocalObject {

	public void setParentSbb(EventListSubscriberParentSbbLocalObject parentSbb);
	
	public void subscribe(Subscription subscription, FlatList flatList);
	
	public void resubscribe(Subscription subscription);
	
	public void unsubscribe(Subscription subscription);
	
	public SubscriptionKey getSubscriptionKey();

}
