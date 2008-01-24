/*
 * CallSbb.java
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

package org.mobicents.examples.media.loop;

import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import org.mobicents.examples.media.BaseDialogSbb;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsSession;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class LoopDemoSbb extends BaseDialogSbb {

    public final static String LOOP_ENDPOINT = "";
    @Override
    public String getWelcomeMessage() {
        return "file://c:/sounds/welcome.wav";
    }

    public void onAnnouncementComplete(MsNotifyEvent evt, ActivityContextInterface aci) {
        join(LOOP_ENDPOINT);
    }
    
    public void unsetSbbContext() {        
    }

    public void sbbCreate() throws CreateException {       
    }

    public void sbbPostCreate() throws CreateException {        
    }

    public void sbbActivate() {        
    }

    public void sbbPassivate() {        
    }

    public void sbbLoad() {        
    }

    public void sbbStore() {        
    }

    public void sbbRemove() {        
    }

    public void sbbExceptionThrown(Exception arg0, Object arg1, ActivityContextInterface arg2) {        
    }

    public void sbbRolledBack(RolledBackContext arg0) {        
    }

}
