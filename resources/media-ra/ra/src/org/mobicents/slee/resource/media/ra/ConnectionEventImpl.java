/*
 * ConnectionEventImpl.java
 *
 * The Simple Media API RA
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

package org.mobicents.slee.resource.media.ra;

import org.mobicents.slee.resource.media.ratype.ConnectionEvent;
import org.mobicents.slee.resource.media.ratype.MediaConnection;

/**
 * Implements Connection event object.
 *
 * @author Oleg Kulikov
 */
public class ConnectionEventImpl implements ConnectionEvent {
    
    private MediaConnection connection;
    private int cause;
    
    /** 
     * Creates a new instance of ConnectionEventImpl.
     *
     * @param connection the connection to which this event relates.
     * @param cause the constant which identify the cause of this event. 
     */
    public ConnectionEventImpl(MediaConnection connection, int cause) {
        this.connection = connection;
        this.cause = cause;
    }

    /**
     * Gets connection related to event.
     *
     * @return the connection object.
     */
    public MediaConnection getConnection() {
        return connection;
    }

    /**
     * Gets the reason of this event.
     *
     * @return integer identifier of the reason.
     */
    public int getCause() {
        return cause;
    }
    
}
