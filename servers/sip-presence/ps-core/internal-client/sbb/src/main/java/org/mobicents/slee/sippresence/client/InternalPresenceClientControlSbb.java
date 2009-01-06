package org.mobicents.slee.sippresence.client;

import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.publication.PublicationClientControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.publication.PublicationClientControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionClientControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionClientControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription.Event;
import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription.Status;

public abstract class InternalPresenceClientControlSbb implements Sbb,
		InternalPresenceClientControlSbbLocalObject {

	private static Logger logger = Logger
			.getLogger(InternalPresenceClientControlSbb.class);

	private SbbContext sbbContext = null; // This SBB's context

	/**
	 * Called when an sbb object is created and enters the pooled state.
	 */
	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
	}

	// Implementation of PresenceClientControlSbbLocalObject :
	// all we need to do is forward the requests to the publication or
	// subscription childs, setting the event package to "presence" when needed

	public void setParentSbb(PresenceClientControlParentSbbLocalObject parentSbb) {
		// store in cmp
		setParentSbbCMP(parentSbb);
	}

	public void newPublication(Object requestId, String entity,
			String document, String contentType, String contentSubType,
			int expires) {

		PublicationClientControlSbbLocalObject childSbb = getPublicationClientControlSbbLocalObject();
		if (childSbb != null) {
			childSbb.newPublication(requestId, entity, "presence", document,
					contentType, contentSubType, expires);
		} else {
			getParentSbbCMP().newPublicationError(requestId,
					Response.SERVER_INTERNAL_ERROR);
		}

	}

	public void refreshPublication(Object requestId, String entity,
			String eTag, int expires) {

		PublicationClientControlSbbLocalObject childSbb = getPublicationClientControlSbbLocalObject();
		if (childSbb != null) {
			childSbb.refreshPublication(requestId, entity, "presence", eTag,
					expires);
		} else {
			getParentSbbCMP().refreshPublicationError(requestId,
					Response.SERVER_INTERNAL_ERROR);
		}

	}

	public void modifyPublication(Object requestId, String entity, String eTag,
			String document, String contentType, String contentSubType,
			int expires) {

		PublicationClientControlSbbLocalObject childSbb = getPublicationClientControlSbbLocalObject();
		if (childSbb != null) {
			childSbb.modifyPublication(requestId, entity, "presence", eTag,
					document, contentType, contentSubType, expires);
		} else {
			getParentSbbCMP().modifyPublicationError(requestId,
					Response.SERVER_INTERNAL_ERROR);
		}

	}

	public void removePublication(Object requestId, String entity, String eTag) {

		PublicationClientControlSbbLocalObject childSbb = getPublicationClientControlSbbLocalObject();
		if (childSbb != null) {
			childSbb.removePublication(requestId, entity, "presence", eTag);
		} else {
			getParentSbbCMP().removePublicationError(requestId,
					Response.SERVER_INTERNAL_ERROR);
		}

	}

	public void newSubscription(String subscriber,
			String subscriberdisplayName, String notifier, String eventPackage,
			String subscriptionId, int expires) {

		SubscriptionClientControlSbbLocalObject childSbb = getSubscriptionClientControlSbbLocalObject();
		if (childSbb != null) {
			// presence subscribes doesn't have content
			childSbb.subscribe(subscriber, subscriberdisplayName, notifier,
					eventPackage, subscriptionId, expires, null, null, null);
		} else {
			getParentSbbCMP().newSubscriptionError(subscriber, notifier,
					eventPackage, subscriptionId,
					Response.SERVER_INTERNAL_ERROR);
		}

	}

	public void refreshSubscription(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires) {

		SubscriptionClientControlSbbLocalObject childSbb = getSubscriptionClientControlSbbLocalObject();
		if (childSbb != null) {
			childSbb.resubscribe(subscriber, notifier, eventPackage,
					subscriptionId, expires);
		} else {
			getParentSbbCMP().refreshSubscriptionError(subscriber, notifier,
					eventPackage, subscriptionId,
					Response.SERVER_INTERNAL_ERROR);
		}

	}

	public void removeSubscription(String subscriber, String notifier,
			String eventPackage, String subscriptionId) {

		SubscriptionClientControlSbbLocalObject childSbb = getSubscriptionClientControlSbbLocalObject();
		if (childSbb != null) {
			childSbb.unsubscribe(subscriber, notifier, eventPackage,
					subscriptionId);
		} else {
			getParentSbbCMP().refreshSubscriptionError(subscriber, notifier,
					eventPackage, subscriptionId,
					Response.SERVER_INTERNAL_ERROR);
		}

	}

	// Implementation of PublicationClientControlParentSbbLocalObject :
	// all we need to do is forward the requests to the parent sbb

	public void newPublicationOk(Object requestId, String eTag, int expires)
			throws Exception {

		getParentSbbCMP().newPublicationOk(requestId, eTag, expires);
	}

	public void newPublicationError(Object requestId, int error) {

		getParentSbbCMP().newPublicationError(requestId, error);
	}

	public void refreshPublicationOk(Object requestId, String eTag, int expires)
			throws Exception {

		getParentSbbCMP().refreshPublicationOk(requestId, eTag, expires);
	}

	public void refreshPublicationError(Object requestId, int error) {

		getParentSbbCMP().refreshPublicationError(requestId, error);
	}

	public void modifyPublicationOk(Object requestId, String eTag, int expires)
			throws Exception {

		getParentSbbCMP().modifyPublicationOk(requestId, eTag, expires);
	}

	public void modifyPublicationError(Object requestId, int error) {

		getParentSbbCMP().modifyPublicationError(requestId, error);
	}

	public void removePublicationOk(Object requestId) throws Exception {

		getParentSbbCMP().removePublicationOk(requestId);
	}

	public void removePublicationError(Object requestId, int error) {

		getParentSbbCMP().removePublicationError(requestId, error);
	}

	// Implementation of SubscriptionClientControlParentSbbLocalObject :
	// all we need to do is forward the requests to the parent sbb

	public void subscribeOk(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires,
			int responseCode) {

		getParentSbbCMP().newSubscriptionOk(subscriber, notifier, eventPackage,
				subscriptionId, expires, responseCode);
	}

	public void subscribeError(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int error) {

		getParentSbbCMP().newSubscriptionError(subscriber, notifier,
				eventPackage, subscriptionId, error);
	}

	public void resubscribeOk(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int expires) {

		getParentSbbCMP().refreshSubscriptionOk(subscriber, notifier,
				eventPackage, subscriptionId, expires);
	}

	public void resubscribeError(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int error) {

		getParentSbbCMP().refreshSubscriptionError(subscriber, notifier,
				eventPackage, subscriptionId, error);
	}

	public void unsubscribeOk(String subscriber, String notifier,
			String eventPackage, String subscriptionId) {

		getParentSbbCMP().removeSubscriptionOk(subscriber, notifier,
				eventPackage, subscriptionId);
	}

	public void unsubscribeError(String subscriber, String notifier,
			String eventPackage, String subscriptionId, int error) {

		getParentSbbCMP().removeSubscriptionError(subscriber, notifier,
				eventPackage, subscriptionId, error);
	}

	public void notifyEvent(String subscriber, String notifier,
			String eventPackage, String subscriptionId,
			Event terminationReason, Status status, String content,
			String contentType, String contentSubtype) {
		
		getParentSbbCMP().notifyEvent(subscriber, notifier, eventPackage,
				subscriptionId, terminationReason, status, content,
				contentType, contentSubtype);
	}
	
	// CHILD RELATIONS AND CMP FIELDs

	public abstract void setParentSbbCMP(
			PresenceClientControlParentSbbLocalObject parentSbb);

	public abstract PresenceClientControlParentSbbLocalObject getParentSbbCMP();

	public abstract PublicationClientControlSbbLocalObject getPublicationClientControlChildSbbCMP();

	public abstract void setPublicationClientControlChildSbbCMP(
			PublicationClientControlSbbLocalObject value);

	public abstract ChildRelation getPublicationClientControlChildRelation();

	private PublicationClientControlSbbLocalObject getPublicationClientControlSbbLocalObject() {
		PublicationClientControlSbbLocalObject childSbb = getPublicationClientControlChildSbbCMP();
		if (childSbb == null) {
			try {
				childSbb = (PublicationClientControlSbbLocalObject) getPublicationClientControlChildRelation()
						.create();
			} catch (Exception e) {
				logger.error("Failed to create child sbb", e);
				return null;
			}
			setPublicationClientControlChildSbbCMP(childSbb);
			childSbb
					.setParentSbb((PublicationClientControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		}
		return childSbb;
	}

	public abstract SubscriptionClientControlSbbLocalObject getSubscriptionClientControlChildSbbCMP();

	public abstract void setSubscriptionClientControlChildSbbCMP(
			SubscriptionClientControlSbbLocalObject value);

	public abstract ChildRelation getSubscriptionClientControlChildRelation();

	private SubscriptionClientControlSbbLocalObject getSubscriptionClientControlSbbLocalObject() {
		SubscriptionClientControlSbbLocalObject childSbb = getSubscriptionClientControlChildSbbCMP();
		if (childSbb == null) {
			try {
				childSbb = (SubscriptionClientControlSbbLocalObject) getSubscriptionClientControlChildRelation()
						.create();
			} catch (Exception e) {
				logger.error("Failed to create child sbb", e);
				return null;
			}
			setSubscriptionClientControlChildSbbCMP(childSbb);
			childSbb
					.setParentSbb((SubscriptionClientControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		}
		return childSbb;
	}

	// SBB OBJECT LIFECYCLE METHODS

	public void sbbActivate() {
	}

	public void sbbCreate() throws CreateException {
	}

	public void sbbExceptionThrown(Exception arg0, Object arg1,
			ActivityContextInterface arg2) {
	}

	public void sbbLoad() {
	}

	public void sbbPassivate() {
	}

	public void sbbPostCreate() throws CreateException {
	}

	public void sbbRemove() {
	}

	public void sbbRolledBack(RolledBackContext arg0) {
	}

	public void sbbStore() {
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
	}

}
