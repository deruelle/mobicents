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

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import java.rmi.server.UID;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEventCause;
import org.mobicents.mscontrol.MsLinkEventID;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsLinkMode;
import org.mobicents.mscontrol.MsLinkState;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.impl.events.EventParser;

/**
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public class MsLinkImpl implements MsLink, ConnectionListener, NotificationListener {

    /**
     * 
     */
    private static final long serialVersionUID = 6373269860176309745L;
    private final String id = (new UID()).toString();
    protected MsSessionImpl session;
    private MsLinkMode mode;
    private MsLinkState state;
    private Connection[] connections = new Connection[2];
    private Logger logger = Logger.getLogger(MsLinkImpl.class);
    private MsEndpointImpl endpoints[] = new MsEndpointImpl[2];
    private QueuedExecutor eventQueue = new QueuedExecutor();
    private EventParser eventParser = new EventParser();
    private int permits = 0;

    private PendingQueue[] pendingQueue = new PendingQueue[2];
    
    public String getId() {
        return id;
    }

    /** Creates a new instance of MsLink */
    public MsLinkImpl(MsSessionImpl session, MsLinkMode mode) {
        this.session = session;
        this.mode = mode;
        setState(MsLinkState.IDLE, MsLinkEventCause.NORMAL);
    }

    public MsSession getSession() {
        return session;
    }

    public MsLinkState getState() {
        return this.state;
    }

    private void setState(MsLinkState state, MsLinkEventCause cause) {
        this.state = state;
        switch (state) {
            case IDLE:
                sendEvent(MsLinkEventID.LINK_CREATED, cause, null);
                break;
            case CONNECTED:
                sendEvent(MsLinkEventID.LINK_CONNECTED, cause, null);
                break;
            case FAILED:
                sendEvent(MsLinkEventID.LINK_FAILED, cause, null);
                break;
            case DISCONNECTED:
                session.removeLink(this);
                sendEvent(MsLinkEventID.LINK_DISCONNECTED, cause, null);
                break;
        }
    }

    protected String getConnectionID(String endpointName) {
        if (endpoints != null) {
            if (endpoints[0].server.getLocalName().matches(endpointName)) {
                return connections[0].getId();
            } else if (endpoints[1].server.getLocalName().matches(endpointName)) {
                return connections[1].getId();
            }
        }
        return null;
    }

    private void sendEvent(MsLinkEventID eventID, MsLinkEventCause cause, String msg) {
        MsLinkEventImpl evt = new MsLinkEventImpl(this, eventID, cause, msg);
        try {
            eventQueue.execute(evt);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Joins specified endpoints.
     * 
     * @param a the name of the first endpoint.
     * @param b the name of the second endpoint.
     */
    public void join(String a, String b) {
        Runnable tx = new JoinTx(this, a, b);
        MsProviderImpl.submit(tx);
    }

    public MsEndpoint[] getEndpoints() {
        return endpoints;
    }

    public void addLinkListener(MsLinkListener listener) {
        session.provider.linkListeners.add(listener);
    }

    public void removeLinkListener(MsLinkListener listener) {
        session.provider.linkListeners.remove(listener);
    }

    /**
     * Drops this link
     */
    public void release() {
        DropTx tx = new DropTx();
        MsProviderImpl.submit(tx);
    }

    private PendingQueue getQueue(String endpointName) {
        if (endpoints[0].getLocalName().equals(endpointName)) {
            return pendingQueue[0];
        }
        return pendingQueue[1];        
    }
    
    public void append(MsRequestedSignal requestedSignal, String endpointName) {
        getQueue(endpointName).append(requestedSignal);
    }
    
    public void append(MsRequestedEvent requestedEvent, String endpointName) {
        getQueue(endpointName).append(requestedEvent);
    }
    
    @Override
    public String toString() {
        return id;
    }

    private class JoinTx implements Runnable {

        public String epnA;
        public String epnB;
        private MsLinkImpl link;

        public JoinTx(MsLinkImpl link, String epnA, String epnB) {
            this.link = link;
            this.epnA = epnA;
            this.epnB = epnB;
        }

        private Connection createConnection(String epn, int end)
                throws NamingException, ResourceUnavailableException, TooManyConnectionsException {
            Endpoint endpoint = EndpointQuery.lookup(epn);
            return endpoint.createLocalConnection(getMode(end));
        }

        public void run() {
            try {
                connections[0] = createConnection(epnA, 0);
                connections[0].addListener(link);

                endpoints[0] = new MsEndpointImpl(connections[0].getEndpoint());
                pendingQueue[0] = new PendingQueue(connections[0].getEndpoint(), connections[0].getId());

                connections[1] = createConnection(epnB, 1);
                connections[1].addListener(link);

                endpoints[1] = new MsEndpointImpl(connections[1].getEndpoint());
                pendingQueue[1] = new PendingQueue(connections[1].getEndpoint(), connections[1].getId());

                connections[0].setOtherParty(connections[1]);
            } catch (NamingException e) {
                e.printStackTrace();
                setState(MsLinkState.FAILED, MsLinkEventCause.ENDPOINT_UNKNOWN);
            } catch (ResourceUnavailableException e) {
                e.printStackTrace();
                setState(MsLinkState.FAILED, MsLinkEventCause.RESOURCE_UNAVAILABLE);
            } catch (TooManyConnectionsException e) {
                e.printStackTrace();
                setState(MsLinkState.FAILED, MsLinkEventCause.RESOURCE_UNAVAILABLE);
            } catch (Exception e) {
                e.printStackTrace();
                setState(MsLinkState.FAILED, MsLinkEventCause.FACILITY_FAILURE);
            }
        }
    }

    private class DropTx implements Runnable {

        public void run() {
            if (connections[1] != null) {
                connections[1].getEndpoint().deleteConnection(connections[1].getId());
            }
            if (connections[0] != null) {
                connections[0].getEndpoint().deleteConnection(connections[0].getId());
            }
        }
    }

    private ConnectionMode getMode(int end) {
        switch (mode) {
            case FULL_DUPLEX:
                return ConnectionMode.SEND_RECV;
            case HALF_DUPLEX:
                return end == 0 ? ConnectionMode.SEND_ONLY : ConnectionMode.RECV_ONLY;
        }
        return null;
    }

    public void onStateChange(Connection connection, ConnectionState oldState) {
        switch (state) {
            case IDLE:
                switch (connection.getState()) {
                    case OPEN:
                        permits++;
                        if (permits == 2) {
                            setState(MsLinkState.CONNECTED, MsLinkEventCause.NORMAL);
                        }
                        break;
                    case CLOSED:
                        setState(MsLinkState.DISCONNECTED, MsLinkEventCause.NORMAL);
                }
                break;

            case CONNECTED:
                switch (connection.getState()) {
                    case HALF_OPEN:
                    case CLOSED:
                        permits--;
                        if (permits == 0) {
                            setState(MsLinkState.DISCONNECTED, MsLinkEventCause.NORMAL);
                        }
                }
        }
    }

    public void update(NotifyEvent event) {
        MsNotifyEvent evt = eventParser.parse(this, event);
        for (MsNotificationListener listener : session.provider.eventListeners) {
            listener.update(evt);
        }
    }
}
