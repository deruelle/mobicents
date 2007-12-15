/*
 * Created on 25/Out/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ptin.xmas.app;

import javax.slee.profile.ProfileManagement;
import javax.slee.profile.ProfileVerificationException;

/**
 * @author Luis Teixeira
 *
 * 
 */
public abstract class DurationProfileManagementImpl implements ProfileManagement, DurationProfileCMP {
    public void profileInitialize() {
        setDuration(10);
    }
    
    public void profileLoad() {}
    public void profileStore() {}
    public void profileVerify() throws ProfileVerificationException {}    

}
