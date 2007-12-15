/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/

package org.mobicents.conference;

import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import java.util.concurrent.ConcurrentHashMap;
import javax.sip.RequestEvent;
import javax.slee.ActivityContextInterface;

/**
 *
 * @author Oleg Kulikov
 */
public interface ConfSbbActivityContextInterface extends ActivityContextInterface {
    public RequestEvent getRequestEvent();
    public void setRequestEvent(RequestEvent evt);

    public String getConfName();
    public void setConfName(String confName);
    
    public ConfSbbActivityContextInterface getConfActivityContextInterface();
    public void setConfActivityContextInterface(ConfSbbActivityContextInterface aci);
    
    public int getConnectionCount();
    public void setConnectionCount(int count);
    
    public EndpointIdentifier getEndpoint();
    public void setEndpoint(EndpointIdentifier endpointID);
    
    public CallIdentifier getCallID();
    public void setCallID(CallIdentifier callID);

    public ConnectionIdentifier getConnectionID();
    public void setConnectionID(ConnectionIdentifier connectionID);
    
    public ConcurrentHashMap getConnections();
    public void setConnections(ConcurrentHashMap connections);
}
