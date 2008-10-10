package org.mobicents.slee.sippresence.client;

import javax.slee.SbbLocalObject;

/**
 * 
 * Client interface to a presence server.
 * @author martins
 *
 */
public interface PresenceClientControlSbbLocalObject extends SbbLocalObject {

	/**
	 * Used to set the call back sbb local object in the sbb implementing this
	 * interface. Must be used whenever a new object of this interface is
	 * created.
	 * 
	 * An example:
	 * 
	 * ChildRelation childRelation = getChildRelation();
	 * PresenceClientControlSbbLocalObject childSbb =
	 * (PresenceClientControlSbbLocalObject) childRelation.create();
	 * childSbb.setParentSbb(
	 * (PresenceClientControlParentSbbLocalObject)this.getSbbContext().getSbbLocalObject());
	 * 
	 * 
	 * @param parent
	 */
	public void setParentSbb(PresenceClientControlParentSbbLocalObject parentSbb);
	
	/**
	 * Creates a new publication for the specified Entity.
	 * 
	 * @param requestId
	 *            an object that identifies the request, the child sbb will
	 *            return it when providing the response
	 * @param entity
	 * @param document
	 * @param contentType
	 * @param contentSubType
	 * @param expires
	 *            the time in seconds, which the publication is valid
	 */
	public void newPublication(Object requestId, String entity, String document, String contentType,
			String contentSubType, int expires);

	/**
	 * Refreshes the publication identified by the specified Entity and ETag.
	 * 
	 * @param requestId
	 *            an object that identifies the request, the child sbb will
	 *            return it when providing the response
	 * @param entity
	 * @param eTag
	 * @param expires
	 *            the time in seconds, which the publication is valid
	 */
	public void refreshPublication(Object requestId, String entity, String eTag, int expires);

	/**
	 * Modifies the publication identified by the specified Entity and ETag.
	 * 
	 * @param requestId
	 *            an object that identifies the request, the child sbb will
	 *            return it when providing the response
	 * @param entity
	 * @param eTag
	 * @param document
	 * @param contentType
	 * @param contentSubType
	 * @param expires
	 *            the time in seconds, which the publication is valid
	 */
	public void modifyPublication(Object requestId, String entity, String eTag, String document,
			String contentType, String contentSubType, int expires);

	/**
	 * Removes the publication identified by the specified Entity and ETag.
	 * 
	 * @param requestId
	 *            an object that identifies the request, the child sbb will
	 *            return it when providing the response
	 * @param entity
	 * @param eventPackage
	 * @param eTag
	 */
	public void removePublication(Object requestId, String entity, String eTag);
	
	/**
	 * Creates a subscription
	 * 
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 *            only event packages "presence" and "presence.winfo" may be
	 *            supported by the presence server
	 * @param subscriptionId
	 * @param expires
	 */
	public void newSubscription(String subscriber, String subscriberdisplayName,
			String notifier, String eventPackage, String subscriptionId,
			int expires);

	/**
	 * Refreshes a subscription
	 * 
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 *            only event packages "presence" and "presence.winfo" may be
	 *            supported by the presence server
	 * @param subscriptionId
	 * @param expires
	 */
	public void refreshSubscription(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires);

	/**
	 * Terminates a subscription.
	 * 
	 * @param subscriber
	 * @param notifier
	 * @param eventPackage
	 *            only event packages "presence" and "presence.winfo" may be
	 *            supported by the presence server
	 * @param subscriptionId
	 */
	public void removeSubscription(String subscriber, String notifier,
			String eventPackage, String subscriptionId);
	
}
