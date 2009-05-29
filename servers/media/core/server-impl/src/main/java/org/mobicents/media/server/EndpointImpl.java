/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;
import javax.sdp.SdpFactory;
import org.apache.log4j.Logger;
import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.resource.Channel;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.resource.UnknownComponentException;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.TooManyConnectionsException;

/**
 *
 * @author kulikov
 */
public class EndpointImpl implements Endpoint {

    private String localName;
    private boolean isInUse = false;
    private Timer timer;
    private ComponentFactory sourceFactory;
    private ComponentFactory sinkFactory;
    
    private ChannelFactory rxChannelFactory;
    private ChannelFactory txChannelFactory;
    
    private Hashtable<String, RtpFactory> rtpFactory;
    private MediaSource source;
    private MediaSink sink;
    
    /** The list of indexes available for connection enumeration within endpoint */
    private ArrayList<Integer> index = new ArrayList();
    /** The last generated connection's index*/
    private int lastIndex = -1;
    /** Holder for created connections */
    protected transient HashMap<String, Connection> connections = new HashMap();
    protected ReentrantLock state = new ReentrantLock();
    
    private SdpFactory sdpFactory = SdpFactory.getInstance();
    
    private static final Logger logger = Logger.getLogger(EndpointImpl.class);

    public EndpointImpl() {
    }

    public EndpointImpl(String localName) {
        this.localName = localName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public MediaSink getSink() {
        return sink;
    }
    
    public MediaSource getSource() {
        return source;
    }
    
    /**
     * Calculates index of the new connection.
     * 
     * The connection uses this method to ask endpoint for new lowerest index.
     * The index is unique withing endpoint but it is not used as connection 
     * identifier outside of the endpoint.
     * 
     * @return the lowerest available integer value.
     */
    protected int getIndex() {
        return index.isEmpty() ? ++lastIndex : index.remove(0);
    }

    public void start() throws ResourceUnavailableException {
        if (sourceFactory != null) {
            source = (MediaSource) sourceFactory.newInstance(this);
        }
        if (sinkFactory != null) {
            sink = (MediaSink) sinkFactory.newInstance(this);
        }
        
        logger.info("Started " + localName);
    }

    public void stop() {
        logger.info("Stopped " + localName);
    }

    protected SdpFactory getSdpFactory() {
        return sdpFactory;
    }
    
    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void setSourceFactory(ComponentFactory sourceFactory) {
        this.sourceFactory = sourceFactory;
    }

    public ComponentFactory getSourceFactory() {
        return sourceFactory;
    }

    public void setSinkFactory(ComponentFactory sinkFactory) {
        this.sinkFactory = sinkFactory;
    }

    public ComponentFactory getSinkFactory() {
        return sinkFactory;
    }

    public ChannelFactory getRxChannelFactory() {
        return rxChannelFactory;
    }

    public void setRxChannelFactory(ChannelFactory rxChannelFactory) {
        this.rxChannelFactory = rxChannelFactory;
    }
    
    public void setRtpFactory(Hashtable<String, RtpFactory> rtpFactory) {
        this.rtpFactory = rtpFactory;
    }
    
    public Hashtable<String, RtpFactory> getRtpFactory() {
        return this.rtpFactory;
    }
    

    public ChannelFactory getTxChannelFactory() {
        return txChannelFactory;
    }

    public void setTxChannelFactory(ChannelFactory txChannelFactory) {
        this.txChannelFactory = txChannelFactory;
    }

    protected Channel createRxChannel(Connection connection) throws UnknownComponentException {
        if (rxChannelFactory != null && sink != null) {
            Channel rxChannel = rxChannelFactory.newInstance(this);
            rxChannel.setConnection(connection);
            rxChannel.connect(sink);
            return rxChannel;
        } else return null;
    }

    protected void releaseRxChannel(Channel channel) {
        if (channel != null && sink != null) {
            channel.disconnect(sink);
            rxChannelFactory.release(channel);
        }
    }

    protected Channel createTxChannel(Connection connection) throws UnknownComponentException {
        if (txChannelFactory != null && source != null) {
            Channel txChannel = txChannelFactory.newInstance(this);
            txChannel.setConnection(connection);
            txChannel.connect(source);
            return txChannel;
        } else return null;
    }

    protected void releaseTxChannel(Channel channel) {
        if (channel != null && source != null) {
            channel.disconnect(source);
            txChannelFactory.release(channel);
        }
    }

    public Collection<Connection> getConnections() {
        return connections.values();
    }
    
    public Connection createConnection(ConnectionMode mode) throws TooManyConnectionsException, ResourceUnavailableException {
        state.lock();
        try {
            RtpConnectionImpl connection = new RtpConnectionImpl(this, mode);
            connections.put(connection.getId(), connection);
            this.isInUse = true;
            return connection;
        } catch (Exception e) {
            logger.error("Could not create RTP connection", e);
            throw new ResourceUnavailableException(e.getMessage());
        } finally {
            state.unlock();
        }
    }

    public Connection createLocalConnection(ConnectionMode mode) throws TooManyConnectionsException, ResourceUnavailableException {
        state.lock();
        try {
            LocalConnectionImpl connection = new LocalConnectionImpl(this, mode);
            connections.put(connection.getId(), connection);
            this.isInUse = true;
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Could not create RTP connection", e);
            throw new ResourceUnavailableException(e.getMessage());
        } finally {
            state.unlock();
        }
    }

    public void deleteConnection(String connectionID) {
        state.lock();
        try {
            ConnectionImpl connection = (ConnectionImpl) connections.remove(connectionID);
            if (connection != null) {
                connection.close();
                index.add(connection.getIndex());
            }
            isInUse = connections.size() > 0;
        } finally {
            state.unlock();
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.Endpoint#deleteAllConnections();
     */
    public void deleteAllConnections() {
        state.lock();
        try {
            ConnectionImpl[] list = new ConnectionImpl[connections.size()];
            connections.values().toArray(list);
            for (int i = 0; i < list.length; i++) {
                deleteConnection(list[i].getId());
            }
        } finally {
            state.unlock();
        }
    }

    public boolean hasConnections() {
        return !connections.isEmpty();
    }

    public boolean isInUse() {
        return this.isInUse;
    }

    public void setInUse(boolean inUse) {
        this.isInUse = inUse;
    }


    public void addNotificationListener(NotificationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeNotificationListener(NotificationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addConnectionListener(ConnectionListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeConnectionListener(ConnectionListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getSupportedPackages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Connection getConnection(String connectionID) {
        return connections.get(connectionID);
    }

    public Component getComponent(String name) {
        if (source != null && source.getName().matches(name)) {
            return source;
        } else if (sink != null && sink.getName().matches(name)) {
            return sink;
        } 
        return null;
    }
    
    public Component getComponent(int resourceID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
