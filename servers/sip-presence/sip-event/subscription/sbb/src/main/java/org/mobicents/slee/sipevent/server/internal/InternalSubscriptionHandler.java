package org.mobicents.slee.sipevent.server.internal;

import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbb;

/**
 * Handler for INTERNAL SUBSCRIPTION related requests.
 * 
 * @author martins
 * 
 */
public class InternalSubscriptionHandler {

	protected SubscriptionControlSbb sbb;

	private NewInternalSubscriptionHandler newInternalSubscriptionHandler;
	private RefreshInternalSubscriptionHandler refreshInternalSubscriptionHandler;
	private RemoveInternalSubscriptionHandler removeInternalSubscriptionHandler;
	private InternalSubscriberNotificationHandler internalSubscriberNotificationHandler;

	public InternalSubscriptionHandler(SubscriptionControlSbb sbb) {
		this.sbb = sbb;
		newInternalSubscriptionHandler = new NewInternalSubscriptionHandler(
				this);
		refreshInternalSubscriptionHandler = new RefreshInternalSubscriptionHandler(
				this);
		removeInternalSubscriptionHandler = new RemoveInternalSubscriptionHandler(
				this);
		internalSubscriberNotificationHandler = new InternalSubscriberNotificationHandler(
				this);
	}

	// getters

	public InternalSubscriberNotificationHandler getInternalSubscriberNotificationHandler() {
		return internalSubscriberNotificationHandler;
	}

	public NewInternalSubscriptionHandler getNewInternalSubscriptionHandler() {
		return newInternalSubscriptionHandler;
	}

	public RefreshInternalSubscriptionHandler getRefreshInternalSubscriptionHandler() {
		return refreshInternalSubscriptionHandler;
	}

	public RemoveInternalSubscriptionHandler getRemoveInternalSubscriptionHandler() {
		return removeInternalSubscriptionHandler;
	}

}
