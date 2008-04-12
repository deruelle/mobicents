/*
 * MsTerminationEventImpl.java
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

import org.mobicents.mscontrol.MsTermination;
import org.mobicents.mscontrol.MsTerminationEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class MsTerminationEventImpl implements MsTerminationEvent {
    
    private MsTerminationImpl source;
    private int eventID;
    private int cause;
    private String msg;
    
    /** Creates a new instance of MsTerminationEventImpl */
    public MsTerminationEventImpl(MsTerminationImpl source, int eventID, int cause) {
        this.source = source;
        this.eventID = eventID;
        this.cause = cause;
    }
    
    public MsTerminationEventImpl(MsTerminationImpl source, int eventID, int cause, String msg) {
        this.source = source;
        this.eventID = eventID;
        this.cause = cause;
        this.msg = msg;
    }

    public MsTermination getSource() {
        return source;
    }

    public int getEventID() {
        return eventID;
    }

    public int getCause() {
        return cause;
    }

    public String getMessage() {
        return msg;
    }
    
}
