/*
 * MsTerminationEvent.java
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
public interface MsTerminationEvent extends Serializable {
    public final static int TERMINATION_CREATED = 0;
    public final static int TERMINATION_ACTIVATED = 1;
    public final static int TERMINATION_DEACTIVATED = 2;
    
    public final static int CAUSE_NORMAL = 0;
        
    public MsTermination getSource();
    public int getEventID();
    public int getCause();
    public String getMessage();
}
