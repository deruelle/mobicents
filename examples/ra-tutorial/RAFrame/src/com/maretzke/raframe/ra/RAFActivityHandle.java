/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: RAFActivityHandle.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 3rd August 2005, 14:09
 * Version: 1.0
 */
package com.maretzke.raframe.ra;

import javax.slee.resource.ActivityHandle;
import org.apache.log4j.Logger;

/**
 * RAFActivityHandle represents the specific activity handle for RAFrame resource
 * adaptor. According to JSLEE 1.1 specification, "an activity handle parameter is
 * a distributable reference to an activity. The activity handle is used by the SLEE
 * to determine the activity context that represents the underlying activity in the 
 * resource for an event to be delivered on. The resource adaptor defines the activity
 * handle and activity assocation however this association must be a 1:1 relationship."
 * <br>
 * In the case of RAFrame, the activity handle is bound to the "dialog" which is indicated
 * by the identifier (id) in incoming protocol messages. So, the id itself is the activity
 * handle and is wrapped and represented through RAFActivityHandle. 
 * <br>
 * For further information, please refer to JSLEE v1.1 Specification, Early Draft 
 * Review Page 314.
 *
 * @author Michael Maretzke
 */
public class RAFActivityHandle implements ActivityHandle {    
    private static Logger logger = Logger.getLogger(RAFActivityHandle.class);
    private String handle = null;
    
    public RAFActivityHandle(String id) {
        logger.debug("RAFActivityHanlde(" + id + ") called.");
        this.handle = id;
    }
    
    // ActivityHandle interface
    public boolean equals(Object obj) {
        if (obj instanceof RAFActivityHandle) {
            return handle.equals(((RAFActivityHandle)obj).handle);
        } 
        else return false;
    }
       
    // ActivityHandle interface    
    public int hashCode() {
        return handle.hashCode();
    }       
}
