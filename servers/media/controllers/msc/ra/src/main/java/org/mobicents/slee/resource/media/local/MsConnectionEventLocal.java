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
    
    protected MsConnectionEventLocal(MsConnectionEvent evt, MsConnection connection) {
        this.evt = evt;
        this.connection = connection;
    }
    
    public MsConnection getConnection() {
        return connection;
    }

    public MsConnectionEventID getEventID() {
        return evt.getEventID();
    }

    public MsConnectionEventCause getCause() {
        return evt.getCause();
    }

    public String getMessage() {
        return evt.getMessage();
    }

}
