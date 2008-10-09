package org.mobicents.slee.sipevent.server.subscription;

import java.util.UUID;

import org.mobicents.slee.sipevent.server.subscription.pojo.Subscription;

public class InternalNotifyEvent {

	private final String eventId = UUID.randomUUID().toString();
	
	private final String subscriber;
	private final String notifier;
	private final String eventPackage;
	private final String subscriptionId;
	
	private final Subscription.Event subscriptionEvent;
	private final Subscription.Status subscriptionStatus;
	
	private final String document;
	private final String contentType;
	private final String contentSubtype;
	
	public InternalNotifyEvent(String subscriber, String notifier, String eventPackage,
			String subscriptionId, Subscription.Event subscriptionEvent, Subscription.Status subscriptionStatus, String document, String contentType,
			String contentSubtype) {
		this.subscriber = subscriber;
		this.notifier = notifier;
		this.eventPackage = eventPackage;
		this.subscriptionId = subscriptionId;
		this.subscriptionEvent = subscriptionEvent;
		this.subscriptionStatus = subscriptionStatus;
		this.document = document;
		this.contentType = contentType;
		this.contentSubtype = contentSubtype;
	}

	public String getContentSubtype() {
		return contentSubtype;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public String getDocument() {
		return document;
	}
	
	public String getEventPackage() {
		return eventPackage;
	}

	public String getNotifier() {
		return notifier;
	}
	
	public String getSubscriber() {
		return subscriber;
	}
	
	public Subscription.Event getSubscriptionEvent() {
		return subscriptionEvent;
	}
	
	public String getSubscriptionId() {
		return subscriptionId;
	}
	
	public Subscription.Status getSubscriptionStatus() {
		return subscriptionStatus;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((InternalNotifyEvent)obj).eventId.equals(this.eventId);
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return eventId.hashCode();
	}
	
}
