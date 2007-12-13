/*
 * File Name     : JccConnectionEventImpl.java
 *
 * The Java Call Control API for SIP
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

package org.mobicents.jcc.sip;

import javax.csapi.cc.jcc.JccConnectionEvent;
import javax.csapi.cc.jcc.JccConnection;
import javax.csapi.cc.jcc.JccCall;

/**
 * Implements JccConnectionEvent interface.
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class JccConnectionEventImpl implements JccConnectionEvent {
    
    int id;
    private JccConnection connection;
    int cause;
    
    /** 
     * Creates a new instance of JccConnectionEventImpl.
     *
     * @param id the id of this event.
     * @param connection the JccConnection associated with this event 
     * @param cause  the cause associated with this event.
     */
    public JccConnectionEventImpl(int id, JccConnection connection, int cause) {
        this.id = id;
        this.connection = connection;
        this.cause = cause;
    }

    /**
     * (Non Java-doc).
     * @see javax.csapi.cc.jcc.JccConnectionEvent#getCall().
     */
    public JccCall getCall() {
        return connection.getCall();
    }

    /**
     * (Non Java-doc).
     * @see javax.csapi.cc.jcc.JccConnectionEvent#getCause().
     */
    public int getCause() {
        return cause;
    }

    /**
     * (Non Java-doc).
     * @see javax.csapi.cc.jcc.JccConnectionEvent#getConnection().
     */
    public JccConnection getConnection() {
        return connection;
    }

    /**
     * (Non Java-doc).
     * @see javax.csapi.cc.jcc.JccConnectionEvent#getID().
     */
    public int getID() {
        return id;
    }

    /**
     * (Non Java-doc).
     * @see javax.csapi.cc.jcc.JccConnectionEvent#getSource().
     */
    public Object getSource() {
        return connection;
    }
    
    public String toString() {
        switch (id) {
            case JccConnectionEvent.CONNECTION_CREATED :
                return "CONNECTION_CREATED";
            case JccConnectionEvent.CONNECTION_ADDRESS_ANALYZE :
                return "CONNECTION_ADDRESS_ANALYZE";
            case JccConnectionEvent.CONNECTION_ADDRESS_COLLECT :
                return "CONNECTION_ADDRESS_COLLECT";
            case JccConnectionEvent.CONNECTION_ALERTING :
                return "CONNECTION_ALERTING";
            case JccConnectionEvent.CONNECTION_AUTHORIZE_CALL_ATTEMPT :
                return "CONNECTION_AUTHORIZE_CALL_ATTEMPT";
            case JccConnectionEvent.CONNECTION_CALL_DELIVERY :
                return "CONNECTION_CALL_DELIVERY";
            case JccConnectionEvent.CONNECTION_CONNECTED :
                return "CONNECTION_CONNECTED";
            case JccConnectionEvent.CONNECTION_DISCONNECTED :
                return "CONNECTION_DISCONNECTED";
            case JccConnectionEvent.CONNECTION_FAILED :
                return "CONNECTION_FAILED";
            case JccConnectionEvent.CONNECTION_MID_CALL :
                return "CONNECTION_MID_CALL";
            default : return "UNKNOWN";
        }
    }
}
