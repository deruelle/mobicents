package org.mobicents.slee.sipevent.server.subscription;

import javax.slee.ActivityContextInterface;

import org.mobicents.slee.sipevent.server.subscription.eventlist.FlatList;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;

public interface EventListSubscriberSbbLocalObject extends SubscriptionClientControlParentSbbLocalObject {

	public void setParentSbb(EventListSubscriberParentSbbLocalObject parentSbb);
	
	public void subscribe(Subscription subscription, FlatList flatList, ActivityContextInterface flatListACI);
	
	public void resubscribe(Subscription subscription, FlatList flatList);
	
	public void unsubscribe(Subscription subscription, FlatList flatList);
	
	public SubscriptionKey getSubscriptionKey();

}
