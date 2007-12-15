package org.mobicents.slee.examples.callforwardblock.profile;

import javax.slee.profile.ProfileManagement;
import javax.slee.profile.ProfileVerificationException;

/**
 * Profile Management implementation class.
 */

public abstract class CallForwardBlockProfileManagementImpl implements ProfileManagement, CallForwardBlockProfileCMP {

	/**
	 * Initialize the profile with its default values.
	 */

    public void profileInitialize() {
    }

    public void profileLoad() {}
    public void profileStore() {}

	/**
	 * Verify the profile's CMP field settings.
	 * @throws ProfileVerificationException if any CMP field contains an invalid value
     */
     
    public void profileVerify() throws ProfileVerificationException {
    }
}
