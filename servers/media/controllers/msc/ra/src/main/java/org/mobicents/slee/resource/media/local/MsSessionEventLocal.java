/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.local;

import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSessionEvent;
import org.mobicents.mscontrol.MsSessionEventCause;
import org.mobicents.mscontrol.MsSessionEventID;

/**
 *
 * @author Oleg Kulikov
 */
public class MsSessionEventLocal implements MsSessionEvent {

    private MsSessionEvent evt;
    private MsSessionEventID eventID;
    private MsSessionEventCause cause;
    private MsSession session;
    
    protected MsSessionEventLocal(MsSessionEvent evt, MsSession session) {
        this.evt = evt;
        this.eventID = evt.getEventID();
        this.cause = evt.getEventCause();
        this.session = session;
    }
    
    protected MsSessionEventLocal(MsSessionEventID eventID, MsSessionEventCause cause, MsSession session) {
        this.eventID = eventID;
        this.cause = cause;
        this.session = session;
    }
    
    public MsSession getSource() {
        return session;
    }

    public MsSessionEventID getEventID() {
        return eventID;
    }

    public MsSessionEventCause getEventCause() {
        return cause;
    }

    public Object getCauseObject() {
        return evt.getCauseObject();
    }

}
