/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.slee.resource.media.local;

import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsConnectionMode;
import org.mobicents.mscontrol.MsConnectionState;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsSession;

/**
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public class MsConnectionLocal implements MsConnection {

    private MsSessionLocal session;
    protected MsConnection connection;

    protected MsConnectionLocal(MsSessionLocal session, MsConnection connection) {
        this.session = session;
        this.connection = connection;
    }

    public String getId() {
        return connection.getId();
    }

    public MsSession getSession() {
        return session;
    }

    public MsConnectionState getState() {
        return connection.getState();
    }

    public String getLocalDescriptor() {
        return connection.getLocalDescriptor();
    }

    public String getRemoteDescriptor() {
        return connection.getRemoteDescriptor();
    }

    public MsEndpoint getEndpoint() {
        return new MsEndpointLocal(connection.getEndpoint());
    }

    public void addConnectionListener(MsConnectionListener listener) {
        throw new SecurityException("addConnectionListener is unsupported. Use event handlers of SBB");
    }

    public void removeConnectionListener(MsConnectionListener listener) {
        throw new SecurityException("removeConnectionListener is unsupported.");
    }

    public void modify(String localDesc, String remoteDesc) {
        connection.modify(localDesc, remoteDesc);
    }

    public void release() {
        connection.release();
    }

    @Override
    public String toString() {
        return connection.toString();
    }

    public void addNotificationListener(MsNotificationListener listener) {
        throw new SecurityException("method is unsupported.");
    }

    public void removeNotificationListener(MsNotificationListener listener) {
        throw new SecurityException("method is unsupported.");
    }

    public void setMode(MsConnectionMode mode) {
        connection.setMode(mode);
    }
    
    public MsConnectionMode getMode() {
        return connection.getMode();
    }
}
