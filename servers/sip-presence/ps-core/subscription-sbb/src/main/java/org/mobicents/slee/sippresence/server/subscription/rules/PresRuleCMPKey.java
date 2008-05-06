package org.mobicents.slee.sippresence.server.subscription.rules;

import java.io.Serializable;

public class PresRuleCMPKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1212963002333839692L;
	
	private final String subscriber;
	private final String notifier;
	private final String eventPackage;
	private final String eventId;
	
	public PresRuleCMPKey(String subscriber, String notifier, String eventPackage,String eventId) {
		super();
		this.subscriber = subscriber;
		this.notifier = notifier;
		this.eventPackage = eventPackage;
		if (eventId != null) {
			this.eventId = eventId;
		}
		else {
			this.eventId = "<";
		}
	}
	
	public String getEventPackage() {
		return eventPackage;
	}
	
	public String getSubscriber() {
		return subscriber;
	}
	
	public String getNotifier() {
		return notifier;
	}
	
	@Override
	public int hashCode() {
		return ((subscriber.hashCode()*31+notifier.hashCode())*31 + eventPackage.hashCode())*31+eventId.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj.getClass() == this.getClass()) {
			PresRuleCMPKey other = (PresRuleCMPKey)obj;
			return this.subscriber.equals(other.subscriber) && this.notifier.equals(other.notifier) && this.eventPackage.equals(other.eventPackage) && this.eventId.equals(other.eventId);
		}
		else {
			return false;
		}
	}
}
