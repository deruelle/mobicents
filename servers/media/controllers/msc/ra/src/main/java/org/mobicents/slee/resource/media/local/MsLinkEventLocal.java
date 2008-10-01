/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.local;

import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkEventCause;
import org.mobicents.mscontrol.MsLinkEventID;

/**
 *
 * @author Oleg Kulikov
 */
public class MsLinkEventLocal implements MsLinkEvent {

    private MsLinkEvent evt;
    private MsLink link;
    
    protected MsLinkEventLocal(MsLinkEvent evt, MsLink link) {
        this.evt = evt;
        this.link = link;
    }
    
    public MsLink getSource() {
        return link;
    }

    public MsLinkEventID getEventID() {
        return evt.getEventID();
    }

    public MsLinkEventCause getCause() {
        return evt.getCause();
    }

    public String getMessage() {
        return evt.getMessage();
    }

}
