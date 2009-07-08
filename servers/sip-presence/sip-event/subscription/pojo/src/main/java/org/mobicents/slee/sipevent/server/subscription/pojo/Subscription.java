package org.mobicents.slee.sipevent.server.subscription.pojo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
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
@Table(name = "MOBICENTS_SIPEVENT_SUBSCRIPTIONS")
@NamedQueries({
	@NamedQuery(name=Subscription.JPA_NAMED_QUERY_SELECT_SUBSCRIPTION_FROM_TIMERID,query="SELECT s FROM Subscription s WHERE s.timerID = :timerID"),
	@NamedQuery(name=Subscription.JPA_NAMED_QUERY_SELECT_SUBSCRIPTIONS_FROM_NOTIFIER_AND_EVENTPACKAGE,query="SELECT s FROM Subscription s WHERE s.notifier = :notifier AND s.key.eventPackage = :eventPackage"),
	@NamedQuery(name=Subscription.JPA_NAMED_QUERY_SELECT_SUBSCRIPTIONS_FROM_NOTIFIER,query="SELECT s FROM Subscription s WHERE s.notifier = :notifier"),
	@NamedQuery(name=Subscription.JPA_NAMED_QUERY_SELECT_SUBSCRIPTIONS_FROM_NOTIFIER_WITH_PARAMS,query="SELECT s FROM Subscription s WHERE s.notifier = :notifier and s.notifierParams = :notifierParams"),
	@NamedQuery(name=Subscription.JPA_NAMED_QUERY_SELECT_DIALOG_SUBSCRIPTIONS,query="SELECT s FROM Subscription s WHERE s.key.callId = :callId AND s.key.remoteTag = :remoteTag")
	})
public class Subscription implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8020033417766370446L;
	
	private static final String JPA_NAMED_QUERY_PREFIX = "MSPS_NQUERY_";
	public static final String JPA_NAMED_QUERY_SELECT_SUBSCRIPTION_FROM_TIMERID = JPA_NAMED_QUERY_PREFIX + "selectSubscriptionFromTimerID";
	public static final String JPA_NAMED_QUERY_SELECT_SUBSCRIPTIONS_FROM_NOTIFIER_AND_EVENTPACKAGE = JPA_NAMED_QUERY_PREFIX + "selectSubscriptionsFromNotifierAndEventPackage";
	public static final String JPA_NAMED_QUERY_SELECT_SUBSCRIPTIONS_FROM_NOTIFIER = JPA_NAMED_QUERY_PREFIX + "selectSubscriptionsFromNotifier";
	public static final String JPA_NAMED_QUERY_SELECT_SUBSCRIPTIONS_FROM_NOTIFIER_WITH_PARAMS = JPA_NAMED_QUERY_PREFIX + "selectSubscriptionsFromNotifierWithParams";
	public static final String JPA_NAMED_QUERY_SELECT_DIALOG_SUBSCRIPTIONS = JPA_NAMED_QUERY_PREFIX + "selectDialogSubscriptions";
	
	/**
	 * the subscription key
	 */   
	@EmbeddedId
	protected SubscriptionKey key;
		
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
	 * the notifier uri params
	 */
	@Column(name = "NOTIFER_PARAMS",nullable=true)
	private String notifierParams;
	
	/**
	 * the current status of the subscription
	 */
	public enum Status {
		active, 
		pending, 
		waiting, 
		terminated;
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
		noresource,
		rejected,
		deactivated,
		probation,
		timeout,
		approved,
		giveup,
		subscribe
	}
	@Column(name = "LAST_EVENT", nullable = true)
	private Event lastEvent;
	
	/**
	 * the id of the SLEE timer associated with this subscription
	 */
	@Column(name = "TIMER_ID", nullable = true)
	private TimerID timerID;
	
	/**
	 * the version of the last content, this only applies to WInfo or RLS subscriptions
	 */
	@Column(name = "VERSION", nullable = false)
	private int version;
	
	/**
	 * if true the subscription if for a resource list
	 */
	@Column(name = "RESOURCE_LIST", nullable = false)
	private Boolean resourceList;
	
	/**
	 * the entity manager used to load and persist this subscription
	 */
	@Transient
	private EntityManager entityManager; 
	
	/**
	 * notifier uri with params
	 */
	@Transient
	private String notifierWithParams;
	
	/**
	 * 
	 */
	public Subscription() {	}
	
	/**
	 * 
	 * @param key
	 * @param subscriber
	 * @param notifier
	 * @param status
	 * @param subscriberDisplayName
	 * @param expires
	 * @param resourceList
	 */
	public Subscription(SubscriptionKey key, String subscriber, String notifier, Status status, String subscriberDisplayName, int expires, boolean resourceList) {
		this.key = key;
		this.subscriber = subscriber;
		int index = notifier.indexOf(';');
		this.notifierWithParams = notifier;
		if (index > 0) {
			this.notifier = notifier.substring(0,index);
			this.notifierParams = notifier.substring(index);			
		}
		else {
			this.notifier = notifier;
			this.notifierParams = null;
		}
		this.status = status;
		if (status.equals(Status.active)){
			this.lastEvent = Event.approved;
		}
		else if (status.equals(Status.pending)) {
			this.lastEvent = Event.subscribe;
		}
		this.creationDate = System.currentTimeMillis();
		this.lastRefreshDate = creationDate;		
		this.expires = expires;
		this.subscriberDisplayName = subscriberDisplayName;
		this.version = 0;
		this.resourceList = Boolean.valueOf(resourceList);
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
		if (status == Status.active) {
			if (event == Event.noresource || event == Event.rejected || event == Event.deactivated || event == Event.probation || event == Event.timeout) {
				status = Status.terminated;
			}
		}

		else if (status == Status.pending) {
			if (event == Event.approved) {
				status = Status.active;
			}
			else if (event == Event.timeout) {
				status = Status.waiting;
			}
			else if (event == Event.noresource || event == Event.rejected || event == Event.deactivated || event == Event.probation || event == Event.giveup) {
				status = Status.terminated;
			}
		}		

		else if (status == Status.waiting) {
			if (event == Event.noresource || event == Event.rejected || event == Event.giveup || event == Event.approved) {
				status = Status.terminated;
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
		return key.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((Subscription)obj).key.equals(this.key);
		}
		else {
			return false;
		}
	}

	// -- GETTERS AND SETTERS
	
	public SubscriptionKey getKey() {
		return key;
	}

	public void setKey(SubscriptionKey key) {
		this.key = key;
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

	public String getNotifierParams() {
		return notifierParams;
	}
	
	public void setNotifierParams(String notifierParams) {
		this.notifierParams = notifierParams;
	}
	
	public String getNotifierWithParams() {
		if (notifierWithParams == null) {
			if (notifierParams == null) {
				notifierWithParams = notifier;
			}
			else {
				notifierWithParams = notifier + notifierParams;
			}
		}
		return notifierWithParams;
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
	
	public Boolean getResourceList() {
		return resourceList;
	}
	
	public void setResourceList(Boolean resourceList) {
		this.resourceList = resourceList;
	}
	
	@Override
	public String toString() {
		return "subscription: subscriber="+subscriber+",notifier="+notifier+",resourceList="+resourceList+",eventPackage="+key.getEventPackage()+",eventId="+key.getRealEventId()+",status="+status;
	}
	
	/*
	 * loads subscription pojo from persistence
	 */
	public static Subscription getSubscription(EntityManager entityManager,
			String callId, String remoteTag, String eventPackage, String eventId) {
		return (Subscription) entityManager.find(Subscription.class,
				new SubscriptionKey(callId, remoteTag, eventPackage, eventId));
	}
	
	/**
	 * retrieves subscriptions associated with the specified dialog
	 * @param entityManager
	 * @param dialog
	 * @return
	 */
	public static List getDialogSubscriptions(EntityManager entityManager, String callId, String remoteTag) {
		return entityManager.createNamedQuery(JPA_NAMED_QUERY_SELECT_DIALOG_SUBSCRIPTIONS)
		.setParameter("callId",callId)
		.setParameter("remoteTag",remoteTag)
		.getResultList();
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}