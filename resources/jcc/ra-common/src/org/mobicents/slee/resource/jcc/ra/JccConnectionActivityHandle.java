/*
 * File Name     : JccConnectionActivityHandle.java
 *
 * The Java Call Control RA
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

package org.mobicents.slee.resource.jcc.ra;

import javax.slee.resource.ActivityHandle;
import javax.csapi.cc.jcc.JccConnection;

/**
 * Represents the specific activity handle for Java Call Control RA.
 * This activity handle is bound to a connection wich is identified by 
 * JccConnection object.
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class JccConnectionActivityHandle implements ActivityHandle {
    
    private JccConnection connection;
    
    /** Creates a new instance of JccConnectionActivityHandle */
    public JccConnectionActivityHandle(JccConnection connection) {
        this.connection = connection;
    }
    
    public boolean equals(Object o) {
    	if (o != null && o.getClass() == this.getClass()) {
    		JccConnectionActivityHandle other = (JccConnectionActivityHandle)o;
			return other.connection == this.connection || other.connection.equals(this.connection);
		}
		else {
			return false;
		}
    }
       
    public int hashCode() {
        return connection.hashCode();
    }       
    
}
