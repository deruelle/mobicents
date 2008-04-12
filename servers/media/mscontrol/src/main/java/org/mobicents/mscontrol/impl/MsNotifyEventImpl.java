/*
 * MsNotifyEventImpl.java
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

import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsResource;

/**
 *
 * @author Oleg Kulikov
 */
public class MsNotifyEventImpl implements MsNotifyEvent {
    
    private MsResource source;
    private int id;
    private int cause;
    private String message;
    
    /** Creates a new instance of MsNotifyEventImpl */
    public MsNotifyEventImpl(MsResource source, int id,int cause, String message) {
        this.source = source;
        this.id = id;
        this.cause = cause;
        this.message = message;
    }

    public MsResource getSource() {
        return source;
    }
    
    public int getEventID() {
        return id;
    }

    public int getCause() {
        return cause;
    }

    public String getMessage() {
        return message;
    }
    
}
