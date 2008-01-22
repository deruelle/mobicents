/*
 * MsConnectionImpl.java
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

package org.mobicents.mscontrol.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.naming.NamingException;
import javax.sdp.SdpException;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.mscontrol.MsConnectionEvent;

import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsSession;

/**
 *
 * @author Oleg Kulikov
 */
public class MsConnectionImpl implements MsConnection {
    
    private String id = Long.toHexString(System.currentTimeMillis());
    
    private String localSdp;
    private String remoteSdp;
    
    private MsSessionImpl session;
    private String endpointName;
    
    private Connection connection;
    private Endpoint endpoint;
    
    protected ArrayList <MsConnectionListener> listeners = new ArrayList();
    private transient Logger logger = Logger.getLogger(MsConnectionImpl.class);
    
    private int mode = Connection.MODE_SEND_RECV;
    
    /**
     * Creates a new instance of MsConnectionImpl
     *
     * @params session the session object to which this connections belongs.
     * @param endpointName the name of the endpoint.
     */
    public MsConnectionImpl(MsSessionImpl session, String endpointName) {
        this.session = session;
        this.endpointName = endpointName;
        listeners.addAll(session.provider.connectionListeners);
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsConnection#getSession();
     */
    public MsSession getSession() {
        return session;
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsConnection#getLocalDescriptor();
     */
    public String getLocalDescriptor() {
        return connection != null ? connection.getLocalDescriptor() : null;
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsConnection#getLocalDescriptor();
     */
    public String getRemoteDescriptor(){
        return connection != null ? connection.getRemoteDescriptor() : null;
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsConnection#getEndpoint();
     */
    public String getEndpoint() {
        return endpoint != null ? endpoint.getLocalName() : null;
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsConnection#addConectionListener(MsConnectionListener);
     */
    public void addConnectionListener(MsConnectionListener listener) {
        listeners.add(listener);
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsConnection#removeConectionListener(MsConnectionListener);
     */
    public void removeConnectionListener(MsConnectionListener listener) {
        listeners.remove(listener);
    }
    
    public Collection getConnectionListeners() {
        return listeners;
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsConnection#modify();
     */
    public void modify(String localDesc, String remoteDesc) {
        this.remoteSdp = remoteDesc;
        Runnable tx = endpoint == null ? new CreateTx(this) : new ModifyTx(this);
        new Thread(tx).start();
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsConnection#release();
     */
    public void release() {
        if (endpoint != null) {
            Runnable tx = new DeleteTx();
            new Thread(tx).start();
        }
    }
    
    private synchronized void sendEvent(int eventID, int cause, String msg) {
        MsConnectionEventImpl evt = new MsConnectionEventImpl(this, eventID, cause, msg);
        new Thread(evt).start();
    }
    
    
    public String toString() {
        return "MsConnection[" + id + "]";
    }
    
    
    private class CreateTx implements Runnable  {
        private MsConnectionImpl localConnection;
        
        public CreateTx(MsConnectionImpl localConnection) {
            this.localConnection = localConnection;
        }
        
        private void execute() {
            try {
                endpoint = EndpointQuery.lookup(endpointName);
                logger.debug("Media server returns endpoint: " + endpoint.getLocalName());
            } catch (NamingException ex) {
                logger.warn("TX Failed", ex);
                sendEvent(MsConnectionEvent.TX_FAILED,
                        MsConnectionEvent.CAUSE_FACILITY_FAILURE,
                        ex.getMessage());
                return;
            } catch (ResourceUnavailableException ex) {
                logger.warn("TX Failed", ex);
                sendEvent(MsConnectionEvent.TX_FAILED,
                        MsConnectionEvent.CAUSE_RESOURCE_UNAVAILABLE,
                        ex.getMessage());
                return;
            }
            
            try {
                connection = endpoint.createConnection(Connection.MODE_SEND_RECV);
                logger.debug("Media server creates connection : id = " + connection.getId());
            } catch (TooManyConnectionsException ex) {
                logger.warn("TX Failed", ex);
                sendEvent(MsConnectionEvent.TX_FAILED,
                        MsConnectionEvent.CAUSE_FACILITY_FAILURE,
                        ex.getMessage());
                return;
            } catch (ResourceUnavailableException ex) {
                logger.warn("TX Failed", ex);
                sendEvent(MsConnectionEvent.TX_FAILED,
                        MsConnectionEvent.CAUSE_RESOURCE_UNAVAILABLE,
                        ex.getMessage());
                return;
            }
            
            if (remoteSdp != null) {
                try {
                    connection.setRemoteDescriptor(remoteSdp);
                    logger.debug("Updated remote descriptor");
                } catch (SdpException ex) {
                    endpoint.deleteConnection(connection.getId());
                    logger.warn("TX Failed", ex);
                    sendEvent(MsConnectionEvent.TX_FAILED,
                            MsConnectionEvent.CAUSE_REMOTE_SDP_INVALID,
                            ex.getMessage());
                    return;
                } catch (IOException ex) {
                    endpoint.deleteConnection(connection.getId());
                    logger.warn("TX Failed", ex);
                    sendEvent(MsConnectionEvent.TX_FAILED,
                            MsConnectionEvent.CAUSE_FACILITY_FAILURE,
                            ex.getMessage());
                    return;
                }
            }
            
            sendEvent(MsConnectionEvent.CONNECTION_CREATED, MsConnectionEvent.CAUSE_NORMAL, null);
        }
        
        public void run() {
            NDC.push("CREATE TX ID = " + id);
            try {
                Thread.currentThread().sleep(500);
                execute();
            } catch (InterruptedException e){
            } finally {
                NDC.pop();
                NDC.remove();
            }
        }
    }
    
    private class ModifyTx implements Runnable  {
        private MsConnectionImpl localConnection;
        
        public ModifyTx(MsConnectionImpl localConnection) {
            this.localConnection = localConnection;
        }
        
        private void execute() {
            if (remoteSdp != null) {
                try {
                    connection.setRemoteDescriptor(remoteSdp);
                    logger.debug("Updated remote descriptor");
                } catch (SdpException ex) {
                    logger.warn("TX Failed", ex);
                    sendEvent(MsConnectionEvent.TX_FAILED,
                            MsConnectionEvent.CAUSE_REMOTE_SDP_INVALID,
                            ex.getMessage());
                } catch (IOException ex) {
                    logger.warn("TX Failed", ex);
                    sendEvent(MsConnectionEvent.TX_FAILED,
                            MsConnectionEvent.CAUSE_FACILITY_FAILURE,
                            ex.getMessage());
                }
            }
            sendEvent(MsConnectionEvent.CONNECTION_MODIFIED, MsConnectionEvent.CAUSE_NORMAL, null);
        }
        
        public void run() {
            NDC.push("MODIFY TX ID = " + id);
            try {
                Thread.currentThread().sleep(500);
                execute();
            } catch (InterruptedException e){
            } finally {
                NDC.pop();
                NDC.remove();
            }
        }
    }
    
    private class DeleteTx implements Runnable  {
        public void run() {
            NDC.push("DELETE TX = " + id);
            try {
                if (connection != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Deleting connection " + this);
                    }
                    endpoint.deleteConnection(connection.getId());
                    sendEvent(MsConnectionEvent.CONNECTION_DELETED, MsConnectionEvent.CAUSE_NORMAL, null);
                }
            } finally {
                NDC.pop();
                NDC.remove();
            }
        }
    }
}
