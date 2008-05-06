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
	@Column(name = "PK_DIALOG_ID", nullable = false)
    private String dialogId;
    @Column(name = "PK_EVENT_PACKAGE", nullable = false)
    private String eventPackage;
    @Column(name = "PK_EVENT_ID", nullable = false)
    private String eventId;

    public SubscriptionKey() {
    }

    public SubscriptionKey(String dialogId, String eventPackage, String eventId) {
        this.dialogId = dialogId;
        this.eventPackage = eventPackage;
        setEventId(eventId);
    }

    public String getDialogId() {
		return dialogId;
	}
    
    public void setDialogId(String dialogId) {
		this.dialogId = dialogId;
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
            return this.dialogId.equals(other.dialogId) &&
                    this.eventPackage.equals(other.eventPackage) &&
                    this.eventId.equals(other.eventId);
        }
        else {
            return false;
        }
    }

    public int hashCode() {
        int result;
        result = dialogId.hashCode();
        result = 31 * result + eventPackage.hashCode();
        result = 31 * result + eventId.hashCode();
        return result;
    }

    public String toString() {
        return "subscriptionKey:dialogId="+dialogId+",eventPackage="+eventPackage+",eventId="+eventId;
    }

}

