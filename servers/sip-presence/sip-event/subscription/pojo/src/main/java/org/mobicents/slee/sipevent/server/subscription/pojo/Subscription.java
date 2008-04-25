package org.mobicents.slee.sipevent.server.subscription.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.slee.facilities.TimerID;

/**
 * 	Subscription: A subscription is a set of application state associated
 *     with a dialog.  This application state includes a pointer to the
 *     associated dialog, the event package name, and possibly an
 *     identification token.  Event packages will define additional
 *     subscription state information.  By definition, subscriptions
 *     exist in both a subscriber and a notifier.
 *  
 *     This class is JPA pojo for a subscription.
 *     
 * @author eduardomartins
 *
 */
@Entity
@Table(name = "SUBSCRIPTIONS")
public class Subscription implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8020033417766370446L;

	/**
	 * the subscription key
	 */   
	@EmbeddedId
	protected SubscriptionKey subscriptionKey;
		
	/**
	 * the subscriber
	 */
	@Column(name = "SUBSCRIBER",nullable=false)
	private String subscriber;
	
	
	/**
	 * the notifier
	 */
	@Column(name = "NOTIFER",nullable=false)
	private String notifier;
		
	/**
	 * the current status of the subscription
	 */
	public enum Status {
		ACTIVE, 
		PENDING, 
		WAITING, 
		TERMINATED;
	}
	@Column(name = "STATUS", nullable = false)
	private Status status;
	
	/**
	 * the date when this subscription was created
	 */
	@Column(name = "CREATION_DATE", nullable = false)
	private long creationDate; // subscription's date of creation

	/**
	 * the last time this subscription was refreshed
	 */
	@Column(name = "LAST_REFRESH_DATE", nullable = false)
	private long lastRefreshDate; // last time this subscription was refreshed

	/**
	 * seconds to expire the subscription, after creation/last refresh
	 */
	@Column(name = "EXPIRES", nullable = false)
	private int expires; // seconds to expire

	/**
	 * display name of the subscriber
	 */

	@Column(name = "SUBSCRIBER_DISPLAY_NAME", nullable = true)
	private String subscriberDisplayName; // the display name of the subscriber in a sip header
	
	/**
	 * last event that occurred in the subscription
	 */
	public enum Event {
		NORESOURCE,
		REJECTED,
		DEACTIVATED,
		PROBATION,
		TIMEOUT,
		APPROVED,
		GIVEUP
	}
	@Column(name = "LAST_EVENT", nullable = true)
	private Event lastEvent;
	
	/**
	 * the id of the SLEE timer associated with this subscription
	 */
	@Column(name = "TIMER_ID", nullable = true)
	private TimerID timerID;
	
	/**
	 * the version of the last content, this only applies to WInfo subscriptions
	 */
	@Column(name = "VERSION", nullable = false)
	private int version;
	
	public Subscription() {
		// TODO Auto-generated constructor stub
	}
	
	public Subscription(SubscriptionKey key, String subscriber, String notifier, Status status, String subscriberDisplayName, int expires) {
		this.subscriptionKey = key;
		this.subscriber = subscriber;
		this.notifier = notifier;
		this.status = status;
		this.creationDate = System.currentTimeMillis();
		this.lastRefreshDate = creationDate;		
		this.expires = expires;
		this.subscriberDisplayName = subscriberDisplayName;
		this.version = 0;
	}
	
	public int getRemainingExpires() {
		long remainingExpires = expires - (System.currentTimeMillis()-lastRefreshDate) / 1000;		
		if (expires < 0) {
			return 0;
		}
		else {
			return (int) remainingExpires;
		}
	}

	public int getSubscriptionDuration() {
		return (int) ((System.currentTimeMillis() - creationDate) / 1000);
	}
	
	public void refresh(int expires) {
		lastRefreshDate = System.currentTimeMillis();
		this.expires = expires;
	}
	
	public boolean changeStatus(Event event) {

		// implements subscription state machine
		Status oldStatus = status;
		if (status == Status.ACTIVE) {
			if (event == Event.NORESOURCE || event == Event.REJECTED || event == Event.DEACTIVATED || event == Event.PROBATION || event == Event.TIMEOUT) {
				status = Status.TERMINATED;
			}
		}

		else if (status == Status.PENDING) {
			if (event == Event.APPROVED) {
				status = Status.ACTIVE;
			}
			else if (event == Event.TIMEOUT) {
				status = Status.WAITING;
			}
			else if (event == Event.NORESOURCE || event == Event.REJECTED || event == Event.DEACTIVATED || event == Event.PROBATION || event == Event.GIVEUP) {
				status = Status.TERMINATED;
			}
		}		

		else if (status == Status.WAITING) {
			if (event == Event.NORESOURCE || event == Event.REJECTED || event == Event.GIVEUP || event == Event.APPROVED) {
				status = Status.TERMINATED;
			}
		}

		if (status != oldStatus) {
			lastEvent = event;
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return subscriptionKey.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((Subscription)obj).subscriptionKey.equals(this.subscriptionKey);
		}
		else {
			return false;
		}
	}

	// -- GETTERS AND SETTERS
	
	public SubscriptionKey getSubscriptionKey() {
		return subscriptionKey;
	}

	public void setSubscriptionKey(SubscriptionKey key) {
		this.subscriptionKey = key;
	}

	public String getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}

	public String getNotifier() {
		return notifier;
	}

	public void setNotifier(String notifier) {
		this.notifier = notifier;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public long getLastRefreshDate() {
		return lastRefreshDate;
	}

	public void setLastRefreshDate(long lastRefreshDate) {
		this.lastRefreshDate = lastRefreshDate;
	}

	public int getExpires() {
		return expires;
	}

	public void setExpires(int expires) {
		this.expires = expires;
	}

	public String getSubscriberDisplayName() {
		return subscriberDisplayName;
	}

	public void setSubscriberDisplayName(String subscriberDisplayName) {
		this.subscriberDisplayName = subscriberDisplayName;
	}

	public Event getLastEvent() {
		return lastEvent;
	}

	public void setLastEvent(Event lastEvent) {
		this.lastEvent = lastEvent;
	}

	public TimerID getTimerID() {
		return timerID;
	}

	public void setTimerID(TimerID timerID) {
		this.timerID = timerID;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public void incrementVersion() {
		this.version++;
	}
}
