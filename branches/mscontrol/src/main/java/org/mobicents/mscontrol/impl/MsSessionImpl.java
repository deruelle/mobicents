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

import java.rmi.server.UID;
import java.util.ArrayList;

import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsSessionEvent;
import org.mobicents.mscontrol.MsSessionListener;
import org.mobicents.media.msc.common.*;
import org.mobicents.media.msc.common.events.MsSessionEventID;
/**
 *
 * @author Oleg Kulikov
 */
public class MsSessionImpl implements MsSession {
    //unique identifier of the session
    private String id = (new UID()).toString();
    
    // provider managing this session
    protected MsProviderImpl provider;
    protected ArrayList <MsSessionListener> listeners = new ArrayList();
    
    // connections holders
    private ArrayList <MsLink> links = new ArrayList();
    private ArrayList <MsConnection> connections = new ArrayList();
    
    private MsSessionState state;
    
    /**
     * Creates a new instance of MsSessionImpl
     */
    public MsSessionImpl(MsProviderImpl provider) {
        this.provider = provider;
        this.listeners.addAll(provider.sessionListeners);
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
        MsLinkImpl link = new MsLinkImpl(this, mode);
        links.add(link);
        setState(MsSessionState.ACTIVE);
        return link;
    }
    
    /**
     * Releases reference on the specified link.
     *
     * @param the link for release reference.
     */
    protected synchronized void dropLink(MsLink link) {
        links.remove(link);
        if (links.size() == 0 && connections.size() == 0) {
            setState(MsSessionState.INVALID);
        }
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsSession#createNetworkConnection().
     */
    public synchronized MsConnection createNetworkConnection(String trunkName) {
        MsConnectionImpl connection = new MsConnectionImpl(this, trunkName);
        connections.add(connection);
        setState(MsSessionState.ACTIVE);
        return connection;
    }
    
    /**
     * Releases reference on the specified connection.
     *
     * @param the connection for release reference.
     */
    protected synchronized void drop(MsConnection connection) {
        connections.remove(connection);
        if (links.size() == 0 && connections.size() == 0) {
            setState(MsSessionState.INVALID);
        }
    }
    
    /**
     * Sends events related to this session.
     *
     * @param eventID the id of the event to be sent.
     */
    private void sendEvent(MsSessionEventID eventID) {
        MsSessionEventImpl evt = new MsSessionEventImpl(this, eventID);
        new Thread(evt).start();
    }
    
    /**
     * Modify state of the session.
     *
     * @param state the new value of the state.
     */
    private void setState(MsSessionState state) {
        if (this.state != state) {
            this.state = state; 
            switch (state) {
                case IDLE :
                    sendEvent(MsSessionEventID.SESSION_CREATED);
                    break;
                case ACTIVE :
                    sendEvent(MsSessionEventID.SESSION_ACTIVE);
                    break;
                case INVALID :
                    sendEvent(MsSessionEventID.SESSION_INVALID);
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
        listeners.add(listener);
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsSession#removeSessionListener(MsSessionListener).
     */
    public void removeSessionListener(MsSessionListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Return the string representation of the session.
     *
     * @return text view of the endpoint which includes unique identifier of 
     * the session.
     */
    public String toString() {
        return "MsSession[" + id + "]"; 
    }

}
