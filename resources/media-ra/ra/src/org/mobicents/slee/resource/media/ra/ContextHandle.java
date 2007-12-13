/*
 * ContextHandle.java
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

package org.mobicents.slee.resource.media.ra;

import javax.slee.resource.ActivityHandle;

/**
 * Implements handler for Context activity.
 *
 * @author Oleg Kulikov
 */
public class ContextHandle implements ActivityHandle {
    private String handle;
    
    /** Creates a new instance of ContextHandle */
    public ContextHandle(String id) {
        this.handle = id;
    }
    
    // ActivityHandle interface
    public boolean equals(Object o) {
    	if (o != null && o.getClass() == this.getClass()) {
			return ((ContextHandle)o).handle.equals(this.handle);
		}
		else {
			return false;
		}
    }
       
    // ActivityHandle interface    
    public int hashCode() {
        return handle.hashCode();
    }    
    
}
