/*
 * MsSessionEvent.java
 *
 * The Simple Media Server Control API
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

package org.mobicents.mscontrol;

import java.io.Serializable;

/**
 * This is the interface for all MsSession-related events.
 *
 * @author Oleg Kulikov
 */
public interface MsSessionEvent extends Serializable {
    //indicates that the MsSession object has been created and is in the MsSession.IDLE state.
    public final static int SESSION_CREATED = 0;
    //indicates that the state of the MsSession object has changed to MsSession.ACTIVE.
    public final static int SESSION_ACTIVE = 1;
    //indicates that the state of the MsSession object has changed to MsSession.INVALID
    public final static int SESSION_INVALID = 2;
    
    /**
     * Returns the MsSession object associated with this event.
     *
     * @return the MsSession object associated with this event.
     */
    public MsSession getSource();
    
    /**
     * Returns the id of event. 
     *
     * @return the id of event
     */
    public int getEventID();
}
