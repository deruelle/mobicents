/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.media.server.spi.events;

import org.mobicents.media.MediaSink;
import org.mobicents.media.server.spi.NotificationListener;

/**
 * The concept of events and signals is central to the Media Server.  
 * A Call Controller may ask to be notified about certain events occurring in an 
 * endpoint  (e.g., off-hook events) or may also request certain signals to be 
 * applied to an endpoint (e.g., dial-tone).
 * 
 * 
 * @author Oleg Kulikov
 */
public interface EventDetector extends MediaSink {
    /**
     * Gets the unique identifier of the event.
     * 
     * @return the unique identifier.
     */
    public String getID();
    /**
     * Gets value of parameter of this event.
     * 
     * @param name the name of the parameter.
     * @return the value of the paremeter as java.lang.Object.
     */
    public Object getParameter(String name);
    
    /**
     * Modify parameter's value.
     * 
     * @param name the name of the parameter
     * @param value the value of the parameter.
     */
    public void setParameter(String name, Object value);    
    
    public void addListener(NotificationListener listener);
    public void removeListener(NotificationListener listener);
}
