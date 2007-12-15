/*
 * MemberProfile.java
 *
 * Created on 2 Èþíü 2006 ã., 10:06
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.mobicents.slee.vpn.profile;

import java.util.Date;

import javax.slee.Address;
import javax.slee.profile.ProfileID;

/**
 *
 * @author mitrenko
 */
public interface MemberProfileCMP {
    
    public Address getAddress();
    public void setAddress(Address address);
    
    public ProfileID getCos();
    public void setCos(ProfileID cos);
    
    public ProfileID[] getBlackList();
    public void setBlackList(ProfileID[] list);
    
    public ProfileID[] getWhiteList();
    public void setWhiteList(ProfileID[] list);
    
    public Boolean getEscCodeFlag();
    public void setEscCodeFlag(Boolean flag);
    
    public String getNetworkID();
    public void setNetworkID(String id);
    
    public Date getRegistrationDate();
    public void setRegistrationDate(Date date);
    
    public Date getModificationDate();
    public void setModificationDate(Date date);   
    
}
