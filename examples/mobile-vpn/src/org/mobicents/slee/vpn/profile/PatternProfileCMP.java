/*
 * PatternCMP.java
 *
 * Created on 2 Èþíü 2006 ã., 10:27
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.mobicents.slee.vpn.profile;

import java.util.Date;
import javax.slee.Address;

/**
 *
 * @author mitrenko
 */
public interface PatternProfileCMP {
    
    public Address getAddress();
    public void setAddress(Address address);
    
    public Integer getDirection();
    public void setDirection(Integer direction);
    
    public Date getStartTime();
    public void setStartTime(Date time);
    
    public Date getFinishTime();
    public void setFinishTime(Date time);
    
    public Integer getDayOfWeek();
    public void setDayOfWeek(Integer day);
    
}
