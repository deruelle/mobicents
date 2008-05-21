/*
 * MsSessionEventImpl.java
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

package org.mobicents.mscontrol.impl;

import org.mobicents.media.msc.common.events.MsSessionEventID;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSessionEvent;
import org.mobicents.mscontrol.MsSessionListener;

/**
 * Implementation for MsSessionEvent interface.
 *
 * @author Oleg Kulikov
 */
public class MsSessionEventImpl implements MsSessionEvent, Runnable  {
    
    private MsSessionImpl session;
    private MsSessionEventID eventID;
    
    /** Creates a new instance of MsSessionEventImpl */
    public MsSessionEventImpl(MsSessionImpl session, MsSessionEventID eventID) {
        this.session = session;
        this.eventID = eventID;
    }

    public MsSession getSource() {
        return session;
    }

    public MsSessionEventID getEventID() {
        return eventID;
    }
    
    public void run() {
        for (MsSessionListener listener: session.listeners) {
            switch (eventID) {
                case SESSION_CREATED :
                    listener.sessionCreated(this);
                    break;
                case SESSION_ACTIVE :
                    listener.sessionActive(this);
                    break;
                case SESSION_INVALID :
                    listener.sessionInvalid(this);
                    break;
            }
        }
    }
}
