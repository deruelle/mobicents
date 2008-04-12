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

import java.util.ArrayList;
import java.rmi.server.UID;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsProvider;

import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsSessionEvent;
import org.mobicents.mscontrol.MsSessionListener;
import org.mobicents.mscontrol.MsTerminationListener;

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
    
    private int state;
    
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
    public int getState() {
        return state;
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsSession#getProvider().
     */
    public synchronized MsLink createLink(int mode) {
        MsLinkImpl link = new MsLinkImpl(this, mode);
        links.add(link);
        setState(MsSession.ACTIVE);
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
            setState(MsSession.INVALID);
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
        setState(MsSession.ACTIVE);
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
            setState(MsSession.INVALID);
        }
    }
    
    /**
     * Sends events related to this session.
     *
     * @param eventID the id of the event to be sent.
     */
    private void sendEvent(int eventID) {
        MsSessionEventImpl evt = new MsSessionEventImpl(this, eventID);
        new Thread(evt).start();
    }
    
    /**
     * Modify state of the session.
     *
     * @param state the new value of the state.
     */
    private void setState(int state) {
        if (this.state != state) {
            this.state = state; 
            switch (state) {
                case MsSession.IDLE :
                    sendEvent(MsSessionEvent.SESSION_CREATED);
                    break;
                case MsSession.ACTIVE :
                    sendEvent(MsSessionEvent.SESSION_ACTIVE);
                    break;
                case MsSession.INVALID :
                    sendEvent(MsSessionEvent.SESSION_INVALID);
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
