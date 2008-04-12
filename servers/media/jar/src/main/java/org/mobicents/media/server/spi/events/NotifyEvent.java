/*
 * NotifyEvent.java
 *
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

package org.mobicents.media.server.spi.events;

import java.io.Serializable;

/**
 *
 * @author Oleg Kulikov
 */
public class NotifyEvent implements Serializable {
    private int id;
    private int cause;
    private Object source;
    private String message;
    
    public NotifyEvent(Object source, int id, int cause, String message) {
        this.id = id;
        this.source = source;
        this.cause = cause;
        this.message = message;
    }
    
    public int getID() {
        return id;
    }
    
    public Object getSource() {
        return source;
    }
    
    public int getCause() {
        return cause;
    }
    
    public String getMessage() {
        return message;
    }
    
}
