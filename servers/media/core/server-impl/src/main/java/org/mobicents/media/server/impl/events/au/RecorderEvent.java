/*
 * Mobicents Media Gateway
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

package org.mobicents.media.server.impl.events.au;

import java.io.Serializable;

import org.mobicents.media.server.impl.common.events.RecorderEventType;

/**
 * Implements media recorder.
 * 
 * @author Oleg Kulikov
 */
public class RecorderEvent implements Serializable {
    

    private RecorderEventType type;
    private Recorder source;
    private String msg;
    
    public RecorderEvent(Recorder source, RecorderEventType type, String msg) {
        this.source = source;
        this.type = type;
        this.msg = msg;
    }
    
    public Recorder getSource() {
        return source;
    }
    
    public RecorderEventType getEventType() {
        return type;
    }
    
    public String getMessage() {
        return msg;
    }
    
}
