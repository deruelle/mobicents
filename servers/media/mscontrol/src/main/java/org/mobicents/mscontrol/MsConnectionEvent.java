/*
 * MsConnectionEvent.java
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

package org.mobicents.mscontrol;

import java.io.Serializable;

/**
 *
 * @author Oleg Kulikov
 */
public interface MsConnectionEvent extends Serializable {
    
    public final static int CONNECTION_CREATED = 0;
    public final static int CONNECTION_MODIFIED = 1;
    public final static int CONNECTION_DELETED = 2;
    public final static int TX_FAILED = 3;
    
    public final static int CONNECTION_CONNECTED = 2;
    public final static int CONNECTION_FAILED = 3;
    public final static int CONNECTION_DISCONNECTED = 4;
    
    public final static int CAUSE_NORMAL = 0;
    public final static int CAUSE_FACILITY_FAILURE = 1;
    public final static int CAUSE_ENDPOINT_UNKNOWN = 2;
    public final static int CAUSE_RESOURCE_UNAVAILABLE = 3;
    public final static int CAUSE_REMOTE_SDP_INVALID = 4;
    public final static int CAUSE_REMOTE_SDP_MISSING = 4;
    
    public MsConnection getConnection();
    public int getEventID();
    public int getCause();
    public String getMessage();
}
