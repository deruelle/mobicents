/*
 * MsConnectionEventImpl.java
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

package org.mobicents.mscontrol.impl;

import java.io.Serializable;

import org.mobicents.media.msc.common.events.MsConnectionEventCause;
import org.mobicents.media.msc.common.events.MsConnectionEventID;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionListener;

/**
 *
 * @author Oleg Kulikov
 */
public class MsConnectionEventImpl implements MsConnectionEvent, Runnable {
    
    private MsConnectionImpl connection;
    private MsConnectionEventID eventID;
    private MsConnectionEventCause cause;
    private String msg;
    
    /** Creates a new instance of MsConnectionEventImpl */
    public MsConnectionEventImpl(MsConnectionImpl connection, MsConnectionEventID eventID, MsConnectionEventCause cause, String msg) {
        this.connection = connection;
        this.eventID = eventID;
    }

    public MsConnection getConnection() {
        return connection;
    }

    public MsConnectionEventID getEventID() {
        return eventID;
    }

    public MsConnectionEventCause getCause() {
        return cause;
    }
    
    public String getMessage() {
        return msg;
    }
    
    public void run() {
        for (MsConnectionListener listener : connection.listeners) {
            switch (eventID) {
                case CONNECTION_CREATED :
                    listener.connectionCreated(this);
                    break;
                case CONNECTION_MODIFIED :
                    listener.connectionModifed(this);
                    break;
                case CONNECTION_DELETED :
                    listener.connectionDeleted(this);
                    break;
                case TX_FAILED :
                    listener.txFailed(this);
                    break;
            }
        }
    }
    
}
