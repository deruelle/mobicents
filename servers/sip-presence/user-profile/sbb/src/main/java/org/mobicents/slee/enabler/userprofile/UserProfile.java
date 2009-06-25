package org.mobicents.slee.enabler.userprofile;

public class UserProfile {

	private final org.mobicents.slee.enabler.userprofile.jpa.UserProfile userProfile;

	public UserProfile(org.mobicents.slee.enabler.userprofile.jpa.UserProfile userProfile) {
		if (userProfile == null) {
			throw new NullPointerException();
		}
		this.userProfile = userProfile;
	}
	
	public String getUsername() {
		return userProfile.getKey().getUsername();
	}
	
	public String getRealm() {
		return userProfile.getKey().getRealm();
	}
	
	public String getPassword() {
		return userProfile.getPassword();
	}
	
}
