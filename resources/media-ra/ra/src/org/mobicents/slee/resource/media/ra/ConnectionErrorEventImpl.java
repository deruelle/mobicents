/*
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

package org.mobicents.slee.resource.media.ra;

import org.mobicents.slee.resource.media.ratype.ConnectionErrorEvent;
import org.mobicents.slee.resource.media.ratype.MediaConnection;

/**
 *
 * @author Oleg Kulikov
 */
public class ConnectionErrorEventImpl extends ConnectionEventImpl 
        implements ConnectionErrorEvent {
    private String message;
    /** Creates a new instance of ConnectionErrorEventImpl */
    public ConnectionErrorEventImpl(MediaConnection connection, int cause, String message) {
        super(connection, cause);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
