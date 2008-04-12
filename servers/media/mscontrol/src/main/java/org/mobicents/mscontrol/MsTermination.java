/*
 * MsTermination.java
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

package org.mobicents.mscontrol;

import java.io.Serializable;

/**
 *
 * @author Oleg Kulikov
 */
public interface MsTermination extends Serializable {
    public final static int IDLE = 0;
    public final static int ACTIVE = 1;
    public final static int INVALID = 2;
    
    public final static int MODE_RECV_ONLY = 1;
    public final static int MODE_SEND_ONLY = 2;
    public final static int MODE_SEND_RECV = 3;
        
    public MsLink getLink();
    public int getState();
    
    public String getEndpoint();
    
    public void addListener(MsTerminationListener listener);
    public void removeListener(MsTerminationListener listener);
    
    public void plug(String endpointName, int mode);
    public void unplug();
}
