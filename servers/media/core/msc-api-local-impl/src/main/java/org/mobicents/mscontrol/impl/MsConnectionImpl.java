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
import java.rmi.server.UID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.naming.NamingException;
import javax.sdp.SdpException;

import org.apache.log4j.Logger;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEventCause;
import org.mobicents.mscontrol.MsConnectionEventID;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsConnectionMode;
import org.mobicents.mscontrol.MsConnectionState;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.impl.events.EventParser;

/**
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public class MsConnectionImpl implements MsConnection, ConnectionListener, NotificationListener {

    /**
     * 
     */
    private static final long serialVersionUID = 7869810097365002045L;
    private transient Logger logger = Logger.getLogger(MsConnectionImpl.class);
    private String id = (new UID()).toString();
    private MsConnectionState state;
    private MsConnectionMode mode = MsConnectionMode.SEND_RECV;
    private String remoteSdp;
    protected MsSessionImpl session;
    private String endpointName;
    protected Connection connection;
    private MsEndpointImpl endpoint;
    protected CopyOnWriteArrayList<MsNotificationListener> connLocalNotificationListeners = new CopyOnWriteArrayList<MsNotificationListener>();
    protected CopyOnWriteArrayList<MsConnectionListener> connLocalConnectionListeners = new CopyOnWriteArrayList<MsConnectionListener>();
    private EventParser eventParser = new EventParser();

    /**
     * Creates a new instance of MsConnectionImpl
     * 
     * @params session the session object to which this connections belongs.
     * @param endpointName
     *            the name of the endpoint.
     */
    public MsConnectionImpl(MsSessionImpl session, String endpointName) {
        this.session = session;
        this.endpointName = endpointName;

        setState(MsConnectionState.IDLE, MsConnectionEventCause.NORMAL);

    }

    public String getId() {
        return this.id;
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
    public String getRemoteDescriptor() {
        return connection != null ? connection.getRemoteDescriptor() : null;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsConnection#getEndpoint();
     */
    public MsEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsConnection#addConectionListener(MsConnectionListener);
     */
    public void addConnectionListener(MsConnectionListener listener) {
        connLocalConnectionListeners.add(listener);
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsConnection#removeConectionListener(MsConnectionListener);
     */
    public void removeConnectionListener(MsConnectionListener listener) {
        connLocalConnectionListeners.remove(listener);
    }

    public void addNotificationListener(MsNotificationListener listener) {
        connLocalNotificationListeners.add(listener);
    }

    public void removeNotificationListener(MsNotificationListener listener) {
        connLocalNotificationListeners.remove(listener);
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsConnection#modify();
     */
    public void modify(String localDesc, String remoteDesc) {
        this.remoteSdp = remoteDesc;
        Runnable tx = endpoint == null ? new CreateTx(this) : new ModifyTx(this);
        MsProviderImpl.submit(tx);
    }

    public MsConnectionMode getMode() {
        return mode;
    }
    
    public void setMode(MsConnectionMode mode) {
        this.mode = mode;
        if (state != MsConnectionState.IDLE) {
            MsProviderImpl.submit(new ModifyModeTx(this));
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsConnection#release();
     */
    public void release() {
        if (endpoint != null) {
            Runnable tx = new DeleteTx();
            MsProviderImpl.submit(tx);
        }
    }

    private synchronized void sendEvent(MsConnectionEventID eventID, MsConnectionEventCause cause, String msg) {
        MsConnectionEventImpl evt = new MsConnectionEventImpl(this, eventID, cause, msg);
        MsProviderImpl.sendEvent(evt);
    }

    public MsConnectionState getState() {
        return state;
    }

    private void setState(MsConnectionState state, MsConnectionEventCause cause) {
        this.state = state;
        switch (state) {
            case IDLE:
                sendEvent(MsConnectionEventID.CONNECTION_CREATED, cause, null);
                break;
            case HALF_OPEN:
                sendEvent(MsConnectionEventID.CONNECTION_HALF_OPEN, cause, null);
                break;
            case OPEN:
                sendEvent(MsConnectionEventID.CONNECTION_OPEN, cause, null);
                break;
            case FAILED:
                // send event and immediately transit to CLOSED state
                sendEvent(MsConnectionEventID.CONNECTION_FAILED, cause, null);
                setState(MsConnectionState.CLOSED, cause);
                break;
            case CLOSED:                
                sendEvent(MsConnectionEventID.CONNECTION_DISCONNECTED, cause, null);
                session.removeConnection(this);
        }
    }

    @Override
    public String toString() {
        return id;
    }

    private class CreateTx implements Runnable {

        private MsConnectionImpl localConnection;

        public CreateTx(MsConnectionImpl localConnection) {
            this.localConnection = localConnection;
        }

        public void run() {
            try {
                endpoint = new MsEndpointImpl(EndpointQuery.lookup(endpointName), session.getProvider());
                endpointName = endpoint.server.getLocalName();

                logger.debug("Media server returns endpoint: " + endpoint.server.getLocalName());
                endpoint.server.addConnectionListener(localConnection);
                connection = endpoint.server.createConnection(getConnectionMode(mode));
                setState(MsConnectionState.HALF_OPEN, MsConnectionEventCause.NORMAL);
                //connection.addListener(localConnection);
                if (remoteSdp != null) {
                    connection.setRemoteDescriptor(remoteSdp);
                }
            } catch (NamingException e) {
                logger.error("Creation of Connection failed for Endpoint " + endpointName, e);
                setState(MsConnectionState.FAILED, MsConnectionEventCause.ENDPOINT_UNKNOWN);
            } catch (ResourceUnavailableException e) {
                logger.error("Creation of Connection failed for Endpoint " + endpointName, e);
                setState(MsConnectionState.FAILED, MsConnectionEventCause.FACILITY_FAILURE);
            } catch (TooManyConnectionsException e) {
                logger.error("Creation of Connection failed for Endpoint " + endpointName, e);
                setState(MsConnectionState.FAILED, MsConnectionEventCause.FACILITY_FAILURE);
            } catch (SdpException e) {
                logger.error("Creation of Connection failed for Endpoint " + endpointName, e);
                setState(MsConnectionState.FAILED, MsConnectionEventCause.REMOTE_SDP_INVALID);
            } catch (IOException e) {
                logger.error("Creation of Connection failed for Endpoint " + endpointName, e);
                setState(MsConnectionState.FAILED, MsConnectionEventCause.FACILITY_FAILURE);
            } catch (Exception e) {
                logger.error("Creation of Connection failed for Endpoint " + endpointName, e);
                setState(MsConnectionState.FAILED, MsConnectionEventCause.FACILITY_FAILURE);
            }
        }
    }

    private class ModifyTx implements Runnable {

        private MsConnectionImpl localConnection;

        public ModifyTx(MsConnectionImpl localConnection) {
            this.localConnection = localConnection;
        }

        public void run() {
            if (remoteSdp != null) {
                try {
                    connection.setRemoteDescriptor(remoteSdp);
                } catch (SdpException ex) {
                    logger.error("Setting remote SDP failed", ex);
                    setState(MsConnectionState.FAILED, MsConnectionEventCause.REMOTE_SDP_INVALID);
                } catch (IOException ex) {
                    logger.error("Setting remote SDP failed", ex);
                    setState(MsConnectionState.FAILED, MsConnectionEventCause.FACILITY_FAILURE);
                } catch (ResourceUnavailableException ex) {
                    logger.error("Setting remote SDP failed", ex);
                    setState(MsConnectionState.FAILED, MsConnectionEventCause.REMOTE_SDP_INVALID);
                }
            }
        }
    }

    private class ModifyModeTx implements Runnable {

        private MsConnectionImpl localConnection;

        public ModifyModeTx(MsConnectionImpl localConnection) {
            this.localConnection = localConnection;
        }

        public void run() {
            connection.setMode(getConnectionMode(mode));
        }
    }

    private class DeleteTx implements Runnable {

        public void run() {
            if (connection != null) {
                endpoint.server.deleteConnection(connection.getId());
            }
        }
    }

    public void update(NotifyEvent event) {
        MsNotifyEvent evt = eventParser.parse(this, event);
        for (MsNotificationListener listener : session.provider.eventListeners) {
            listener.update(evt);
        }
        for (MsNotificationListener listener : connLocalNotificationListeners) {
            listener.update(evt);
        }
    }

    public void onStateChange(Connection connection, ConnectionState oldState) {
        if (this.connection != connection) {
            return;
        }
        switch (connection.getState()) {
            case NULL:
                setState(MsConnectionState.IDLE, MsConnectionEventCause.NORMAL);
                break;
            case HALF_OPEN:
                setState(MsConnectionState.HALF_OPEN, MsConnectionEventCause.NORMAL);
                break;
            case OPEN:
                setState(MsConnectionState.OPEN, MsConnectionEventCause.NORMAL);
                break;
            case CLOSED:
                setState(MsConnectionState.CLOSED, MsConnectionEventCause.NORMAL);
                break;
        }
    }

    public void onModeChange(Connection connection, ConnectionMode oldMode) {
        if (this.connection != connection) {
            return;
        }
        if (connection.getMode() == ConnectionMode.RECV_ONLY) {
            sendEvent(MsConnectionEventID.MODE_RECV_ONLY, MsConnectionEventCause.NORMAL, "");
        } else if (connection.getMode() == ConnectionMode.SEND_ONLY) {
            sendEvent(MsConnectionEventID.MODE_SEND_ONLY, MsConnectionEventCause.NORMAL, "");
        } else {
            sendEvent(MsConnectionEventID.MODE_SEND_RECV, MsConnectionEventCause.NORMAL, "");
        }
    }

    private ConnectionMode getConnectionMode(MsConnectionMode mode) {
        if (mode == MsConnectionMode.RECV_ONLY) {
            return ConnectionMode.RECV_ONLY;
        } else if (mode == MsConnectionMode.SEND_ONLY) {
            return ConnectionMode.SEND_ONLY;
        } else {
            return ConnectionMode.SEND_RECV;
        }
    }
}
