/*
 * VpnProfile.java
 *
 * Created on 2 Èþíü 2006 ã., 9:54
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
public interface VpnProfileCMP {
    
    public String getId();
    public void setId(String id);
    
    public String getOwnerName();
    public void setOwnerName(String owner);
    
    public ProfileID[] getVirtualOnNetPatterns();
    public void setVirtualOnNetPatterns(ProfileID[] patterns);

    public ProfileID[] getMembers();
    public void setMembers(ProfileID[] members);
    
    public ProfileID[] getClasses();
    public void setClasses(ProfileID[] classes);
    
    public Boolean getPrefixFlag();
    public void setPrefixFlag(Boolean flag);
    
    public Date getRegistrationDate();
    public void setRegistrationDate(Date date);
    
    public Date getModificationDate();
    public void setModificationDate(Date date);   
        
}
