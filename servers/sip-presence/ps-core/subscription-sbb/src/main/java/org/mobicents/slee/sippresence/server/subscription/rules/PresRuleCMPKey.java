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
	
	private final String notifierWithoutParams;
	private final String notifierParams;
	
	public PresRuleCMPKey(String subscriber, String notifier, String eventPackage,String eventId) {
		super();
		this.subscriber = subscriber;
		this.notifier = notifier;
		int index = notifier.indexOf(';');
		if (index > 0) {
			notifierWithoutParams = notifier.substring(0,index);
			notifierParams = notifier.substring(index);
		}
		else {
			notifierWithoutParams = notifier;
			notifierParams = null;
		}
		this.eventPackage = eventPackage;
		if (eventId != null) {
			this.eventId = eventId;
		}
		else {
			this.eventId = "<";
		}
	}
	
	public String getEventId() {
		if (eventId == "<") {
			return null;
		}
		else {
			return eventId;
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
	
	public String getNotifierWithoutParams() {
		return notifierWithoutParams;
	}	
	
	public String getNotifierParams() {		
		return notifierParams;
	}
	
	@Override
	public int hashCode() {
		return ((subscriber.hashCode()*31+notifierWithoutParams.hashCode())*31 + eventPackage.hashCode())*31+eventId.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj.getClass() == this.getClass()) {
			PresRuleCMPKey other = (PresRuleCMPKey)obj;
			return this.subscriber.equals(other.subscriber) && this.notifierWithoutParams.equals(other.notifierWithoutParams) && this.eventPackage.equals(other.eventPackage) && this.eventId.equals(other.eventId);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "PresRuleCMPKey{ subscriber = "+subscriber+" , notifier = "+notifier+" , eventPackage = "+eventPackage+" , eventId = "+eventId+" }";
	}
}
