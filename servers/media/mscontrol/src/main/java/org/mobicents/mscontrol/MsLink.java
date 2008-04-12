/*
 * MsLink.java
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
public interface MsLink extends Serializable {
    
    public final static int MODE_HALF_DUPLEX = 1;
    public final static int MODE_FULL_DUPLEX = 2;
    
    /**
     * Gets the session to which this links belongs
     *
     * @return the session object.
     */
    public MsSession getSession();
        
    /**
     * Joins specified endpoints.
     *
     * @param a the name of the first endpoint.
     * @param b the name of the second endpoint.
     */
    public void join(String a, String b);
    
    public String[] getEndpoints();
    
    /**
     * Drops this link
     */ 
    public void release();
}
