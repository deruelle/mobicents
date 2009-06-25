package org.mobicents.slee.enabler.userprofile;

import javax.slee.SbbLocalObject;

/**
 * 
 * @author martins
 * 
 */
public interface UserProfileControlSbbLocalObject extends SbbLocalObject {

	/**
	 * 
	 * @param username
	 * @param realm
	 * @return
	 */
	public UserProfile find(String username, String realm);
	
}
