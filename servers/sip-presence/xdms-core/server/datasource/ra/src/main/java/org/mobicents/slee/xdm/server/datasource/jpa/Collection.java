package org.mobicents.slee.xdm.server.datasource.jpa;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *     
 * @author eduardomartins
 *
 */
@Entity
@Table(name = "XDM_DATASOURCE_COLLECTIONS")
@NamedQueries({
	@NamedQuery(name="selectCollectionFromKey",query="SELECT c FROM Collection c WHERE c.key.collectionName = :collectionName AND c.key.appUsage.id = :auid"),
	@NamedQuery(name="selectCollectionsFromAppUsage",query="SELECT x FROM Collection x WHERE x.key.appUsage.id = :auid"),
	@NamedQuery(name="selectCollectionsFromAppUsageAndExpression",query="SELECT x FROM Collection x WHERE x.key.appUsage.id = :auid AND x.key.collectionName LIKE ':expression'"),
	@NamedQuery(name="deleteCollectionFromKey",query="DELETE FROM Collection x WHERE x.key.appUsage.id = :auid AND x.key.collectionName = :collectionName"),
	@NamedQuery(name="deleteCollectionsFromAppUsage",query="DELETE FROM Collection x WHERE x.key.appUsage.id = :auid")
	})
public class Collection implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3697052553779974529L;

	@EmbeddedId
	protected CollectionPrimaryKey key;
	
	public Collection() {
		// TODO Auto-generated constructor stub
	}
	
	public Collection(String auid, String collectionName) {
		setKey(new CollectionPrimaryKey(collectionName,new AppUsage(auid)));
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			Collection other = (Collection) obj;
			return other.key.equals(this.key);
		}
		else {
			return false;
		}
	}

	// -- GETTERS AND SETTERS
	
	public CollectionPrimaryKey getKey() {
		return key;
	}

	public void setKey(CollectionPrimaryKey key) {
		this.key = key;
	}
	
	
}