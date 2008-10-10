package org.mobicents.slee.sipevent.server.subscription.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author eduardomartins
 *
 */

@Embeddable
public class SubscriptionKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6638892043798746768L;
	
	public static final String NO_CALL_ID = " ";
	public static final String NO_REMOTE_TAG = " ";
	
	@Column(name = "PK_CALL_ID", nullable = false)
    private String callId;
	@Column(name = "PK_REMOTE_TAG", nullable = false)
    private String remoteTag;
	@Column(name = "PK_EVENT_PACKAGE", nullable = false)
    private String eventPackage;
    @Column(name = "PK_EVENT_ID", nullable = false)
    private String eventId;

    public SubscriptionKey() {
    }

    public SubscriptionKey(String callId, String remoteTag, String eventPackage, String eventId) {
        
    	this.callId = callId;
    	this.remoteTag = remoteTag;
        this.eventPackage = eventPackage;
        setEventId(eventId);
    }

    public String getCallId() {
		return callId;
	}
    
    public String getRemoteTag() {
		return remoteTag;
	}
    
    public void setCallId(String callId) {
		this.callId = callId;
	}
    
    public void setRemoteTag(String remoteTag) {
		this.remoteTag = remoteTag;
	}
    
    /**
     * Returns the persisted event id, which may be not the real one.
     * @return
     */
    public String getEventId() {
    	return eventId;
	}
    
    public void setEventId(String eventId) {
    	if (eventId == null) {
        	this.eventId = "\"";
        }
        else {
          this.eventId = eventId;
        }
	}
    
    /**
     * Returns the real event id, which may not be the one persisted.
     * @return
     */
    public String getRealEventId() {
    	if (eventId.equals("\"")) {
        	return null;
        }
        else {
          return eventId;
        }
    }
    
    public static String getEventIdPersisted(String realEventId) {
    	if (realEventId != null) {
    		return realEventId;
    	}
    	else {
    		return "\"";
    	}
    }
    
    public String getEventPackage() {
		return eventPackage;
	}
    
    public void setEventPackage(String eventPackage) {
		this.eventPackage = eventPackage;
	}
    
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == this.getClass()) {
        	SubscriptionKey other = (SubscriptionKey) obj;
            return this.callId.equals(other.callId) &&
            	this.remoteTag.equals(other.remoteTag) &&    
            	this.eventPackage.equals(other.eventPackage) &&
                    this.eventId.equals(other.eventId);
        }
        else {
            return false;
        }
    }

    public int hashCode() {
        int result;
        result = callId.hashCode();
        result = 31 * result + remoteTag.hashCode();
        result = 31 * result + eventPackage.hashCode();
        result = 31 * result + eventId.hashCode();
        return result;
    }

    private transient String toString = null;
    public String toString() {
    	if (toString == null) {
    		toString = "SubscriptionKey:callId="+callId+",remoteTag="+remoteTag+",eventPackage="+eventPackage+",eventId="+eventId;
    	}
        return toString; 
    }

    public boolean isInternalSubscription() {
    	// no need to test both call id and remote tag
		return callId.equals(SubscriptionKey.NO_CALL_ID);
	}
}