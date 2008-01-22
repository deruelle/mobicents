/*
 * MsTerminationImpl.java
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
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.mscontrol.MsJoinException;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsResource;
import org.mobicents.mscontrol.MsSignalDetector;
import org.mobicents.mscontrol.MsSignalGenerator;
import org.mobicents.mscontrol.MsTermination;
import org.mobicents.mscontrol.MsTerminationEvent;
import org.mobicents.mscontrol.MsTerminationListener;

/**
 *
 * @author Oleg Kulikov
 */
public class MsTerminationImpl implements MsTermination {
    
    private final String id = Long.toHexString(System.currentTimeMillis());
    
    private int state;
    private MsLinkImpl link;
    
    private Endpoint endpoint;
    protected Connection connection;
    
    private ArrayList <MsTerminationListener> listeners = new ArrayList();
    private Logger logger = Logger.getLogger(MsTerminationImpl.class);
    
    public MsTerminationImpl(MsLinkImpl link) {
        this.link = link;
        synchronized (listeners) {
            this.listeners.addAll(link.termListeners);
        }
        this.termCreated(MsTerminationEvent.CAUSE_NORMAL);
    }
    
    public MsLink getLink() {
        return link;
    }
    
    public int getState() {
        return state;
    }
    
    public String getEndpoint() {
        if (state == MsTermination.ACTIVE) {
            return endpoint.getLocalName();
        } else throw new IllegalStateException("Termination is not active");
    }
    
    public void plug(String endpointName, int mode) {
        if (logger.isDebugEnabled()) {
            logger.debug(this + " pluging to " + endpointName);
        }
        new Thread(new PlugTx(endpointName, mode)).start();
    }
    
    public void unplug() {
        if (state == MsTermination.ACTIVE) {
            if (logger.isDebugEnabled()) {
                logger.debug(this + " unplug from " + endpoint.getLocalName());
            }
            new Thread(new UnplugTx()).start();
        } else {
            throw new IllegalStateException("Termination is INAVCTIVE");
        }
    }
    
    public void addListener(MsTerminationListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(MsTerminationListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
    
    private synchronized void termCreated(int cause) {
        this.state = MsTermination.IDLE;
        MsTerminationEventImpl evt = new MsTerminationEventImpl(this,
                MsTerminationEvent.TERMINATION_CREATED, cause);
        synchronized (listeners) {
            for (MsTerminationListener listener : listeners) {
                listener.terminationCreated(evt);
            }
        }
    }
    
    private synchronized void termActivated(int cause) {
        this.state = MsTermination.ACTIVE;
        MsTerminationEventImpl evt = new MsTerminationEventImpl(this,
                MsTerminationEvent.TERMINATION_ACTIVATED, cause);
        synchronized (listeners) {
            for (MsTerminationListener listener : listeners) {
                listener.terminationActivated(evt);
            }
        }
    }
    
    private synchronized void termDeactivated(int cause, String msg) {
        this.state = MsTermination.INVALID;
        MsTerminationEventImpl evt = new MsTerminationEventImpl(this,
                MsTerminationEvent.TERMINATION_DEACTIVATED, cause, msg);
        for (MsTerminationListener listener : listeners) {
            listener.terminationDeactivated(evt);
        }
    }
    
    public String toString() {
        return "MsTermination{" + id +  "}";
    }
    
    private class PlugTx implements Runnable {
        private String endpointName;
        private int mode;
        
        public PlugTx(String endpointName, int mode) {
            this.endpointName = endpointName;
            this.mode = mode;
        }
        
        public void run() {
            try {
                endpoint = EndpointQuery.lookup(endpointName);
                if (logger.isDebugEnabled()) {
                    logger.debug("Allocated endpoint: " + endpoint);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            try {
                connection = endpoint.createConnection(mode);
                if (logger.isDebugEnabled()) {
                    logger.debug("Created connection: " + connection);
                }
            } catch (TooManyConnectionsException ex) {
                ex.printStackTrace();
            } catch (ResourceUnavailableException ex) {
                ex.printStackTrace();
            }
            termActivated(MsTerminationEvent.CAUSE_NORMAL);
        }
    }
    
    private class UnplugTx implements Runnable {
        public void run() {
            endpoint.deleteConnection(connection.getId());
            termActivated(MsTerminationEvent.CAUSE_NORMAL);
        }
    }
}
