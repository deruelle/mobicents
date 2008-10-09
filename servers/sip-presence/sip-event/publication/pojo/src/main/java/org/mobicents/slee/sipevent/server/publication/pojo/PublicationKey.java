package org.mobicents.slee.sipevent.server.publication.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author eduardomartins
 *
 */

@Embeddable
public class PublicationKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6638892043798746768L;
	
	@Column(name = "PK_ETAG", nullable = false)
    private String eTag;
    @Column(name = "PK_ENTITY", nullable = false)
    private String entity;
    @Column(name = "PK_EVENT_PACKAGE", nullable = false)
    private String eventPackage;
    
    public PublicationKey() {
    }

    public PublicationKey(String eTag, String entity, String eventPackage) {
        this.eTag = eTag;
        this.entity = entity;
        this.eventPackage = eventPackage;
    }

    public String getEntity() {
		return entity;
	}
    
    public void setEntity(String entity) {
		this.entity = entity;
	}
    
    public String getETag() {
		return eTag;
	}
    
    public void setETag(String eTag) {
		this.eTag = eTag;
	}
    
    public String getEventPackage() {
		return eventPackage;
	}
    
    public void setEventPackage(String eventPackage) {
		this.eventPackage = eventPackage;
	}
    
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == this.getClass()) {
            PublicationKey other = (PublicationKey) obj;
            return this.eTag.equals(other.eTag) && this.entity.equals(other.entity) && this.eventPackage.equals(other.eventPackage);
        }
        else {
            return false;
        }
    }

    public int hashCode() {
        int result;
        result = eTag.hashCode();
        result = 31 * result + eventPackage.hashCode();
        result = 31 * result + entity.hashCode();
        return result;
    }

    public String toString() {
        return "publicationKey: eTag="+eTag+",entity="+entity+",eventPackage="+eventPackage;
    }

}

