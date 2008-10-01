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
    private MsSession session;
    
    protected MsSessionEventLocal(MsSessionEvent evt, MsSession session) {
        this.evt = evt;
        this.session = session;
    }
    
    public MsSession getSource() {
        return session;
    }

    public MsSessionEventID getEventID() {
        return evt.getEventID();
    }

    public MsSessionEventCause getEventCause() {
        return evt.getEventCause();
    }

    public Object getCauseObject() {
        return evt.getCauseObject();
    }

}
