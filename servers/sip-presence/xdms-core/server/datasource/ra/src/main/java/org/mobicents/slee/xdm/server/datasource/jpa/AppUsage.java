package org.mobicents.slee.xdm.server.datasource.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *  
 *     
 * @author eduardomartins
 *
 */
@Entity
@Table(name = "XDM_DATASOURCE_APPUSAGES")
@NamedQueries({	
	@NamedQuery(name="selectAppUsages",query="SELECT x FROM AppUsage x"),
	@NamedQuery(name="selectAppUsageFromKey",query="SELECT x FROM AppUsage x WHERE x.id=:id"),
	@NamedQuery(name="deleteAppUsageFromKey",query="DELETE FROM AppUsage x WHERE x.id = :auid")
})
public class AppUsage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8277363541216110537L;
	/**
	 * the key
	 */   
	@Id
	@Column(name = "APPUSAGE_ID", nullable = false)
	private String id;
	
	public AppUsage() {
		// TODO Auto-generated constructor stub
	}
	
	public AppUsage(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((AppUsage)obj).id.equals(this.id);
		}
		else {
			return false;
		}
	}

	// -- GETTERS AND SETTERS
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
