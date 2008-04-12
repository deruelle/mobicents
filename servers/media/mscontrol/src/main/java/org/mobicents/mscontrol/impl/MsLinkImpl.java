/*
 * MsLink.java
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

import java.io.IOException;
import java.util.ArrayList;
import java.rmi.server.UID;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.MsTermination;
import org.mobicents.mscontrol.MsTerminationEvent;
import org.mobicents.mscontrol.MsTerminationListener;

/**
 *
 * @author Oleg Kulikov
 */
public class MsLinkImpl implements MsLink {
    
    private final String id = (new UID()).toString();
    
    private MsSessionImpl session;
    private int mode;
    
    protected ArrayList <MsTerminationListener> termListeners = new ArrayList();
    protected ArrayList <MsLinkListener> linkListeners = new ArrayList();
    
    private Connection[] connections = new Connection[2];
    private Logger logger = Logger.getLogger(MsLinkImpl.class);
    
    private String endpoints[] = new String[2];
    
    public String getId() {
        return id;
    }
    
    /** Creates a new instance of MsLink */
    public MsLinkImpl(MsSessionImpl session, int mode) {
        this.session = session;
        this.linkListeners.addAll(session.provider.linkListeners);
    }
    
    public MsSession getSession() {
        return session;
    }
    
    private int getMode(int end) {
        switch (mode) {
            case MsLink.MODE_FULL_DUPLEX :
                return Connection.MODE_SEND_RECV;
            case MsLink.MODE_HALF_DUPLEX :
                return end == 0 ?
                    Connection.MODE_SEND_ONLY :
                    Connection.MODE_RECV_ONLY;
        }
        return -1;
    }
    
    private void sendEvent(int eventID, int cause, String msg) {
        MsLinkEventImpl evt = new MsLinkEventImpl(this, eventID, cause, msg);
        new Thread(evt).start();
    }
    
    /**
     * Joins specified endpoints.
     *
     * @param a the name of the first endpoint.
     * @param b the name of the second endpoint.
     */
    public void join(String a, String b) {
        new Thread(new JoinTx(a,b)).start();
    }
    
    public String[] getEndpoints() {
        return endpoints;
    }
    
    /**
     * Drops this link
     */
    public void release() {
        new Thread(new DropTx()).start();
    }
    
    public String toString() {
        return "MsLink{" + id + "}";
    }
    
    private class JoinTx implements Runnable {
        public String epnA;
        public String epnB;
        
        public JoinTx(String epnA, String epnB) {
            this.epnA = epnA;
            this.epnB = epnB;
        }
        
        private Connection createConnection(String epn) throws NamingException,
                ResourceUnavailableException, TooManyConnectionsException {
            Endpoint endpoint = EndpointQuery.lookup(epn);
            return endpoint.createConnection(mode);
        }
        
        private void execute() {
            try {
                connections[0] = createConnection(epnA);
                endpoints[0] = connections[0].getEndpoint().getLocalName();
                logger.debug("Created local connection: " + connections[0].getId());
            } catch (NamingException e) {
                logger.warn("TX Failed", e);
                sendEvent(MsLinkEvent.LINK_FAILED,
                        MsLinkEvent.CAUSE_FACILITY_FAILURE, e.getMessage());
                return;
            } catch (ResourceUnavailableException e) {
                logger.warn("TX Failed", e);
                sendEvent(MsLinkEvent.LINK_FAILED,
                        MsLinkEvent.CAUSE_ENDPOINT_UNKNOWN, e.getMessage());
                return;
            } catch (TooManyConnectionsException e) {
                logger.warn("TX Failed", e);
                sendEvent(MsLinkEvent.LINK_FAILED,
                        MsLinkEvent.CAUSE_RESOURCE_UNAVAILABLE, e.getMessage());
                return;
            }
            
            try {
                connections[1] = createConnection(epnB);
                endpoints[1] = connections[1].getEndpoint().getLocalName();
                logger.debug("Created local connection: " + connections[1].getId());
            } catch (NamingException e) {
                logger.warn("TX Failed", e);
                connections[0].getEndpoint().deleteConnection(connections[0].getId());
                sendEvent(MsLinkEvent.LINK_FAILED,
                        MsLinkEvent.CAUSE_FACILITY_FAILURE, e.getMessage());
                return;
            } catch (ResourceUnavailableException e) {
                connections[0].getEndpoint().deleteConnection(connections[0].getId());
                logger.warn("TX Failed", e);
                sendEvent(MsLinkEvent.LINK_FAILED,
                        MsLinkEvent.CAUSE_ENDPOINT_UNKNOWN, e.getMessage());
                return;
            } catch (TooManyConnectionsException e) {
                connections[0].getEndpoint().deleteConnection(connections[0].getId());
                logger.warn("TX Failed", e);
                sendEvent(MsLinkEvent.LINK_FAILED,
                        MsLinkEvent.CAUSE_RESOURCE_UNAVAILABLE, e.getMessage());
                return;
            }
            
            try {
                connections[0].setOtherParty(connections[1]);
                connections[1].setOtherParty(connections[0]);
                logger.debug("Join connections [" + connections[0].getId() +
                        "," + connections[0].getId());
                sendEvent(MsLinkEvent.LINK_JOINED, MsLinkEvent.CAUSE_NORMAL, null);
            } catch (IOException e) {
                logger.error("Could not join connections", e);
                connections[1].getEndpoint().deleteConnection(connections[1].getId());
                connections[0].getEndpoint().deleteConnection(connections[0].getId());
                sendEvent(MsLinkEvent.LINK_FAILED,
                        MsLinkEvent.CAUSE_FACILITY_FAILURE, e.getMessage());
            }
        }
        
        public void run() {
            NDC.push("JOIN TX ID = " + id);
            try {
                execute();
            } finally {
                NDC.pop();
                NDC.remove();
            }
        }
        
    }
    
    private class DropTx implements Runnable {
        public void execute(){
            logger.debug("Releasing connection " + connections[1]);
            connections[1].getEndpoint().deleteConnection(connections[1].getId());
            logger.debug("Releasing connection " + connections[0]);
            connections[0].getEndpoint().deleteConnection(connections[0].getId());
            sendEvent(MsLinkEvent.LINK_DROPPED,
                    MsLinkEvent.CAUSE_NORMAL, null);
        }
        
        public void run() {
            NDC.push("RELEASE TX ID = " + id);
            try {
                execute();
            } finally {
                NDC.pop();
                NDC.remove();
            }
        }
    }
}
