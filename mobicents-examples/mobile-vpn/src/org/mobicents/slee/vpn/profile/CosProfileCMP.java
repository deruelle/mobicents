/*
 * ClassOfServiceCMP.java
 *
 * Created on 2 Èþíü 2006 ã., 10:20
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.mobicents.slee.vpn.profile;

import java.util.Date;

import javax.slee.profile.ProfileID;

/**
 *
 * @author mitrenko
 */
public interface CosProfileCMP {
    public String getCosName();
    public void setCosName(String name);
    
    public ProfileID[] getAuthorizationPatterns();
    public void setAuthorizationPatterns(ProfileID[] patterns);
    
    public ProfileID[] getScreeningPatterns();
    public void setScreeningPatterns(ProfileID[] patterns);
    
    public ProfileID[] getRoutePatterns();
    public void setRoutePatterns(ProfileID[] patterns);
    
    public Date getRegistrationDate();
    public void setRegistrationDate(Date date);
    
    public Date getModificationDate();
    public void setModificationDate(Date date);   
    
}
