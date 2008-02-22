/*
 * CallHandle.java
 *
 * Created on 26 Март 2007 г., 10:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.mgcp.ra;

import jain.protocol.ip.mgcp.message.parms.CallIdentifier;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import javax.slee.resource.ActivityHandle;

/**
 *
 * @author Oleg Kulikov
 */
public class CallHandle implements ActivityHandle {
    
    private CallIdentifier callID;
    private ConcurrentHashMap connections = new ConcurrentHashMap();
    
    /**
     * Creates a new instance of CallHandle
     */
    public CallHandle(CallIdentifier callID) {
        this.callID = callID;
    }

    public String getId() {
        return callID.toString();
    }
    
    public void add(ConnectionHandle handle) {
        connections.put(handle.getId(), handle);
    }
    
    public void remove(ConnectionHandle handle) {
        connections.remove(handle.toString());
    }
    
    public int getConnectionCount() {
        return connections.size();
    }
    
    public Collection getConnectionHandlers () {
        return connections.values();
    }
    
    public boolean equals(Object other) {
        return other instanceof CallHandle && 
                ((CallHandle) other).callID.toString().equals(callID);
    }
    
    public int hasCode() {
        return callID.toString().hashCode();
    }
    
}
