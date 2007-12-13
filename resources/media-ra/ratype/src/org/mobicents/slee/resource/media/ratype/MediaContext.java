/*
 * MediaContext.java
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

package org.mobicents.slee.resource.media.ratype;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Oleg Kulikov
 */
public interface MediaContext extends Serializable {   
    public final static int ANNOUNCEMENT = 1;
    public final static int IVR = 2;
    public final static int CONFERENCE = 3;
    
    /**
     * Returns unique identifier of this context.
     *
     * @return unique digiatl identifier.
     */
    public String getId();
        
    /**
     * Gets the cause of state transition.
     *
     * @return the integer identifier of the cause.
     */
   // public int getCause();
    
    /**
     * Join media resource through this context.
     * 
     * @param connection the connection object which reprsents media resource.
     * @throws IllegalStateException context is out of service.
     */
    public void add(MediaConnection connection) throws IllegalStateException;
    
    /**
     * Detach media resource from this context.
     * 
     * @param connection the connetion object which represents media resource 
     * been disconnected.
     * @throws IllegalStateException the context should be IN_SERVICE state.
     * @throws IllegalArgumentException the connection is not attached to this context.
     */
    public void subtract(MediaConnection connetion) throws IllegalStateException, IllegalArgumentException;

    /**
     * Creates connection between two contexts.
     *
     * @param context the second context.
     * @throws IllegalStateException the context should be IN_SERVICE state.
     */
    public void add(MediaContext context) throws IllegalStateException;
    
    /**
     * Drops connection between two contexts.
     *
     * @param context the second context.
     * @throws IllegalStateException the context should be IN_SERVICE state.
     * @throws IllegalArgumentException the contexts are not joined.
     */
    public void subtract(MediaContext context) throws IllegalStateException, IllegalArgumentException;

    /**
     * Gets collection of connections attached to this context.
     *
     * @return collection of attached connections.
     */
    public Collection getConnections();
    
}
