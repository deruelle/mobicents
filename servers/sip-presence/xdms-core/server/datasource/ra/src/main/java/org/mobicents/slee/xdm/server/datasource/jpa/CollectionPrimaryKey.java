package org.mobicents.slee.xdm.server.datasource.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 
 * @author eduardomartins
 * 
 */

@Embeddable
public class CollectionPrimaryKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6638892043798746768L;

	@Column(name = "COLLECTION_NAME", nullable = false)
	private String collectionName;

	@ManyToOne(optional=false)
    @JoinColumn(name="APPUSAGE_ID", referencedColumnName = "APPUSAGE_ID")
    private AppUsage appUsage;

	public CollectionPrimaryKey() {
		// TODO Auto-generated constructor stub
	}
	
	public CollectionPrimaryKey(String collectionName, AppUsage appUsage) {
		setCollectionName(collectionName);
		setAppUsage(appUsage);
	}

	// -- GETTERS AND SETTERS

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public AppUsage getAppUsage() {
		return appUsage;
	}

	public void setAppUsage(AppUsage appUsage) {
		this.appUsage = appUsage;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			CollectionPrimaryKey other = (CollectionPrimaryKey) obj;
			return this.collectionName.equals(other.collectionName)
					&& this.appUsage.equals(other.appUsage);
		} else {
			return false;
		}
	}

	public int hashCode() {
		int result;
		result = collectionName.hashCode();
		result = 31 * result + appUsage.hashCode();
		return result;
	}

	public String toString() {
		return "CollectionPrimaryKey:collectionName=" + collectionName
				+ ",appUsage=" + appUsage;
	}

}