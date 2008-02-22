/*
 * TransactionHandle.java
 *
 * Media Gateway Control Protocol (MGCP) Resource Adaptor.
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

package org.mobicents.slee.resource.mgcp.ra;

import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import javax.slee.resource.ActivityHandle;

/**
 *
 * @author Oleg Kulikov
 */
public class TransactionHandle implements ActivityHandle {
    
    private int id;
    private CallIdentifier callID;
    private ConnectionIdentifier connectionID;
    
    /** Creates a new instance of TransactionHandle */
    public TransactionHandle(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public void setCallID(CallIdentifier callID) {
        this.callID = callID;
    }

    public CallIdentifier getCallID() {
        return callID;
    }
    
    public void setConnectionIdentifier(ConnectionIdentifier connectionID) {
        this.connectionID = connectionID;
    }
    
    public ConnectionIdentifier getConnectionID() {
        return connectionID;
    }
    
    public boolean equals(Object other) {
        return other instanceof TransactionHandle &&
                ((TransactionHandle) other).id == id;
    }
    
    public int hashCode() {
        return new Integer(id).hashCode();
    }
}
