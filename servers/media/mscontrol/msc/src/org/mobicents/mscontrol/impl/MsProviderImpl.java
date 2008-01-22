/*
 * MsProviderImpl.java
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsJoinException;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsResourceListener;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsSessionListener;
import org.mobicents.mscontrol.MsSignalDetector;
import org.mobicents.mscontrol.MsSignalGenerator;
import org.mobicents.mscontrol.MsTermination;
import org.mobicents.mscontrol.MsTerminationListener;

/**
 *
 * @author Oleg Kulikov
 */
public class MsProviderImpl implements MsProvider, Serializable {
    
    protected ArrayList <MsSessionListener> sessionListeners = new ArrayList();
    protected ArrayList <MsConnectionListener> connectionListeners = new ArrayList();
    protected ArrayList <MsResourceListener> resourceListeners = new ArrayList();
    protected ArrayList <MsTerminationListener> terminationListeners = new ArrayList();
    protected ArrayList <MsLinkListener> linkListeners = new ArrayList();
    
    private ArrayList <MsSessionImpl> calls = new ArrayList();
    
    /** Creates a new instance of MsProviderImpl */
    public MsProviderImpl() {
    }

    public MsSession createSession() {
        MsSessionImpl call = new MsSessionImpl(this);
        calls.add(call);
        return call;
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsProvider#addSessionListener(MsSessionListener).
     */
    public void addSessionListener(MsSessionListener listener) {
        sessionListeners.add(listener);
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.mscontrol.MsProvider#removeSessionListener(MsSessionListener).
     */
    public void removeSessionListener(MsSessionListener listener) {
        sessionListeners.remove(listener);
    }
    
    public void addResourceListener(MsResourceListener listener) {
        resourceListeners.add(listener);
    }
    
    public void removeResourceListener(MsResourceListener listener) {
        resourceListeners.remove(listener);
    }

    
    public void addConnectionListener(MsConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(MsConnectionListener listener) {
        connectionListeners.remove(listener);
    }
    
    public Collection getConnectionListeners() {
        return connectionListeners;
    }
    
    /**
     * Add a termination listener to all terminations.
     *
     * @param MsTerminationListener object that receives the specified events.
     */
    public void addTerminationListener(MsTerminationListener listener) {
        terminationListeners.add(listener);
    }

    /**
     * Removes termination listener
     *
     * @param MsTerminationListener object that receives the specified events.
     */
    public void removeTerminationListener(MsTerminationListener listener) {
        terminationListeners.remove(listener);
    }

    /**
     * Add a termination listener to all terminations.
     *
     * @param MsLinkListener object that receives the specified events.
     */
    public void addLinkListener(MsLinkListener listener) {
        linkListeners.add(listener);
    }

    /**
     * Removes termination listener
     *
     * @param MsLinkListener object that receives the specified events.
     */
    public void removeLinkListener(MsLinkListener listener) {
        linkListeners.remove(listener);
    }
    
    public MsSignalGenerator getSignalGenerator(String endpointName) {
        return new MsSignalGeneratorImpl(this, endpointName);
    }
    
    public MsSignalDetector getSignalDetector(String endpointName) {
        return new MsSignalDetectorImpl(this, endpointName);
    }
    
}
