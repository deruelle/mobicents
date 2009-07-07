package org.mobicents.slee.sipevent.server.subscription;

import javax.sip.RequestEvent;
import javax.sip.message.MessageFactory;
import javax.sip.message.Response;

import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;

public interface EventListSubscriptionControlSbbLocalObject extends XDMClientControlParentSbbLocalObject,FlatListMakerParentSbbLocalObject,EventListSubscriberParentSbbLocalObject {

	/**
	 * Starts the RLS cache.
	 */
	public void initRLSCache();
	
	/**
	 * Shuts down the RLS cache.
	 */
	public void shutdownRLSCache();
	
	/**
	 * Used to set the call back sbb local object in the sbb implementing this
	 * interface. Must be used whenever a new object of this interface is
	 * created.
	 * 
	 * An example:
	 * 
	 * ChildRelation childRelation = getChildRelation();
	 * EventListSbbLocalObject childSbb =
	 * (EventListSbbLocalObject) childRelation.create();
	 * childSbb.setParentSbb(
	 * (EventListParentSbbLocalObject)this.getSbbContext().getSbbLocalObject());
	 * 
	 * 
	 * @param parent
	 */
	public void setParentSbb(
			EventListSubscriptionControlParentSbbLocalObject sbbLocalObject);

	/**
	 * Validates a subscribe request, which may be targeting at a resource list.
	 * If the notifier is a resource list then the request authorization is also
	 * done. If a SIP request event is provided it is validated according to RFC 4826 Section 4.5
	 * 
	 * @param event 
	 * @param notifier
	 * @param subscriber
	 * @param eventPackage
	 * @return status code to be returned in response of the request or 404 (NOT
	 *         FOUND) if the notifier is not a resource list
	 */
	public int validateSubscribeRequest(String subscriber, String notifier, String eventPackage, RequestEvent event);
	
	/**
	 * Creates a new subscription to a resource list. The sbb will create all
	 * virtual subscriptions to each entry in the list and provide the event
	 * notifications.
	 * 
	 * @param subscription
	 * @return true if subscription is created, false otherwise
	 */
	public boolean createSubscription(Subscription subscription);

	/**
	 * 
	 * @param subscription
	 */
	public void refreshSubscription(Subscription subscription);
	
	/**
	 * 
	 * @param subscription
	 */
	public void removeSubscription(Subscription subscription);

}
