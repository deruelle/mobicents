/*
 * MsSessionImpl.java
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

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;

import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkMode;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSessionEventCause;
import org.mobicents.mscontrol.MsSessionEventID;
import org.mobicents.mscontrol.MsSessionListener;
import org.mobicents.mscontrol.MsSessionState;

/**
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public class MsSessionImpl implements MsSession {

    /**
     * 
     */
    private static final long serialVersionUID = 1376371530688007623L;    // unique identifier of the session
    private String id = (new UID()).toString();
    protected MsProviderImpl provider;
    protected ArrayList<MsLink> links = new ArrayList<MsLink>();
    protected ArrayList<MsConnection> connections = new ArrayList<MsConnection>();
    private MsSessionState state;

    protected QueuedExecutor eventQueue = new QueuedExecutor();
    
    /**
     * Creates a new instance of MsSessionImpl
     */
    public MsSessionImpl(MsProviderImpl provider) {
        this.provider = provider;
        setState(MsSessionState.IDLE, MsSessionEventCause.SESSION_CREATED, this);
    }

    public String getId() {
        return this.id;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsSession#getProvider().
     */
    public MsProvider getProvider() {
        return provider;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsSession#getState().
     */
    public MsSessionState getState() {
        return state;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsSession#getProvider().
     */
    public synchronized MsLink createLink(MsLinkMode mode) {
        MsLink link = new MsLinkImpl(this, mode);
        links.add(link);

        setState(MsSessionState.ACTIVE, MsSessionEventCause.LINK_CREATED, link);
        return link;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsSession#createNetworkConnection().
     */
    public synchronized MsConnection createNetworkConnection(String trunkName) {
        MsConnection connection = new MsConnectionImpl(this, trunkName);
        connections.add(connection);

        setState(MsSessionState.ACTIVE, MsSessionEventCause.CONNECTION_CREATED, connection);
        return connection;
    }

    /**
     * Sends events related to this session.
     * 
     * @param eventID
     *            the id of the event to be sent.
     */
    private void sendEvent(MsSessionEventID eventID, MsSessionEventCause eventCause, Object causeObject) {
        MsSessionEventImpl evt = new MsSessionEventImpl(this, eventID, eventCause, causeObject);
        try {
            eventQueue.execute(evt);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Modify state of the session.
     * 
     * @param state
     *            the new value of the state.
     */
    private void setState(MsSessionState state, MsSessionEventCause eventCause, Object causeObject) {
        if (this.state != state) {
            this.state = state;
            switch (state) {
                case IDLE:
                    sendEvent(MsSessionEventID.SESSION_CREATED, eventCause, causeObject);
                    break;
                case ACTIVE:
                    sendEvent(MsSessionEventID.SESSION_ACTIVE, eventCause, causeObject);
                    break;
                case INVALID:
                    provider.sessions.remove(this);
                    sendEvent(MsSessionEventID.SESSION_INVALID, eventCause, causeObject);
                    break;
            }
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsSession#addSessionListener(MsSessionListener).
     */
    public void addSessionListener(MsSessionListener listener) {
        provider.sessionListeners.add(listener);
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.mscontrol.MsSession#removeSessionListener(MsSessionListener).
     */
    public void removeSessionListener(MsSessionListener listener) {
        provider.sessionListeners.remove(listener);
    }

    public List<MsConnection> getConnections() {
        return this.connections;
    }
    
    protected void removeConnection(MsConnection msConnection) {
        connections.remove(msConnection);
        if (connections.isEmpty() && links.isEmpty()) {
            setState(MsSessionState.INVALID, MsSessionEventCause.CONNECTION_DROPPED, null);
        }
    }
    
    protected void removeLink(MsLink link) {
        links.remove(link);
        if (connections.isEmpty() && links.isEmpty()) {
            setState(MsSessionState.INVALID, MsSessionEventCause.CONNECTION_DROPPED, null);
        }
    }
    /**
     * Return the string representation of the session.
     * 
     * @return text view of the endpoint which includes unique identifier of the
     *         session.
     */
    @Override
    public String toString() {
        return "MsSessionImpl[" + id + "]";
    }

}
