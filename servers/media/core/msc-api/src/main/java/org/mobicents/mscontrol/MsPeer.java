/*
 * MsPeer.java
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

/**
 * Used to get the instance of {@link MsProvider} class
 * @author Oleg Kulikov
 */
public interface MsPeer {
    /**
     * Returns an instance of a Provider object given a string argument which 
     * contains the desired service name.
     *
     * @return An instance of the MsProvider object. 
     */
    public MsProvider getProvider();
}
