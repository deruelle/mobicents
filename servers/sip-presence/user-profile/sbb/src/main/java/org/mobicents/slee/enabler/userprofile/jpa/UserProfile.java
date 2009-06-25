package org.mobicents.slee.enabler.userprofile.jpa;

import java.io.Serializable;

import javax.persistence.Column;
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
@Table(name = "MOBICENTS_SLEE_ENABLER_USERPROFILES")
@NamedQueries({
	@NamedQuery(name=UserProfile.JPA_NAMED_QUERY_SELECT_ALL_USERPROFILES,query="SELECT p FROM UserProfile p")
	})
public class UserProfile implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3697052553779974529L;

	private static final String JPA_NAMED_QUERY_PREFIX = "MSPS_UP_NQUERY_";
	public static final String JPA_NAMED_QUERY_SELECT_ALL_USERPROFILES = JPA_NAMED_QUERY_PREFIX + "selectAllUserProfiles";
	
	@EmbeddedId
	protected UserProfilePrimaryKey key;
	
	/**
	 * the user password
	 */
	@Column(name = "PASSWORD", nullable = true)
	private String password;
	
	public UserProfile() {
		// TODO Auto-generated constructor stub
	}
	
	public UserProfile(String username, String realm) {
		setKey(new UserProfilePrimaryKey(username,realm));
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			UserProfile other = (UserProfile) obj;
			return other.key.equals(this.key);
		}
		else {
			return false;
		}
	}

	// -- GETTERS AND SETTERS
	
	public UserProfilePrimaryKey getKey() {
		return key;
	}

	public void setKey(UserProfilePrimaryKey key) {
		this.key = key;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}