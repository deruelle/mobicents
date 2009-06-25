package org.mobicents.slee.enabler.userprofile.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author eduardomartins
 * 
 */

@Embeddable
public class UserProfilePrimaryKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6638892043798746768L;

	@Column(name = "USERNAME", nullable = false)
	private String username;

	@Column(name = "REALM", nullable = false)
	private String realm;
	
	public UserProfilePrimaryKey() {
		// TODO Auto-generated constructor stub
	}
	
	public UserProfilePrimaryKey(String username, String domain) {
		setUsername(username);
		setRealm(domain);
	}

	// -- GETTERS AND SETTERS

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			UserProfilePrimaryKey other = (UserProfilePrimaryKey) obj;
			return this.username.equals(other.username)
					&& this.realm.equals(other.realm);
		} else {
			return false;
		}
	}

	public int hashCode() {
		int result;
		result = username.hashCode();
		result = 31 * result + realm.hashCode();
		return result;
	}

	public String toString() {
		return "UserProfilePrimaryKey: username = " + username
				+ " , realm = " + realm;
	}

}