/*
 * ConnectionHandle.java
 *
 * Created on 26 Март 2007 г., 10:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.mgcp.ra;

import javax.slee.resource.ActivityHandle;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;

/**
 *
 * @author Oleg Kulikov
 */
public class ConnectionHandle implements ActivityHandle {
    
    private ConnectionIdentifier connectionID;
    
    /**
     * Creates a new instance of CallHandle
     */
    public ConnectionHandle(ConnectionIdentifier connectionID) {
        this.connectionID = connectionID;
    }
    
    public String getId() {
        return connectionID.toString();
    }
    
    public boolean equals(Object other) {
        return other instanceof CallHandle && 
                ((ConnectionHandle) other).connectionID.toString().equals(connectionID);
    }
    
    public int hasCode() {
        return connectionID.toString().hashCode();
    }
    
}
