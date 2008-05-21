/*
 * MsPeerImpl.java
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

package org.mobicents.mscontrol.impl;

import org.mobicents.mscontrol.MsPeer;
import org.mobicents.mscontrol.MsProvider;

/**
 *
 * @author Oleg Kulikov
 */
public class MsPeerImpl implements MsPeer {
    
    /** Creates a new instance of MsPeerImpl */
    public MsPeerImpl() {
    }

    public MsProvider getProvider() {
        return new MsProviderImpl();
    }
    
}
