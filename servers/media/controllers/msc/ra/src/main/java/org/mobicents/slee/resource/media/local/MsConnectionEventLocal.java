/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.local;

import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionEventCause;
import org.mobicents.mscontrol.MsConnectionEventID;

/**
 *
 * @author Oleg Kulikov
 */
public class MsConnectionEventLocal implements MsConnectionEvent {

    private MsConnectionEvent evt;
    private MsConnection connection;
    private MsConnectionEventID eventID;
    private MsConnectionEventCause cause;
    private String msg;
    
    protected MsConnectionEventLocal(MsConnectionEvent evt, MsConnection connection) {
        this.evt = evt;
        this.eventID = evt.getEventID();
        this.cause = evt.getCause();
        this.connection = connection;
        this.msg = evt.getMessage();
    }
    
    protected MsConnectionEventLocal(MsConnectionEventID eventID, MsConnectionEventCause cause, MsConnection connection) {
        this.eventID = eventID;
        this.cause = cause;
        this.connection = connection;
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

}
