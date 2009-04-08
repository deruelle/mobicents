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

import java.util.Hashtable;
import org.apache.log4j.Logger;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.clock.Timer;
import org.mobicents.media.server.resource.Channel;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.resource.UnknownComponentException;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.events.RequestedEvent;
import org.mobicents.media.server.spi.events.RequestedSignal;

/**
 *
 * @author kulikov
 */
public class EndpointImpl implements Endpoint {

    private String localName;
    private Timer timer;
    
    private ComponentFactory sourceFactory;
    
    private Hashtable<String, ChannelFactory> rxChannelFactory;
    private Hashtable<String, ChannelFactory> txChannelFactory;
    
    private MediaSource source;
    private MediaSink sink;
    
    
    
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
    
    public void start() throws ResourceUnavailableException {
        source = (MediaSource) sourceFactory.newInstance("");
        logger.info("Started " + localName);
    }

    public void stop() {
        logger.info("Stopped " + localName);
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
    
    public Hashtable<String, ChannelFactory> getRxChannelFactory() {
        return rxChannelFactory;
    }
    
    public void setRxChannelFactory(Hashtable<String, ChannelFactory> rxChannelFactory) {
        this.rxChannelFactory = rxChannelFactory;
    }

    public Hashtable<String, ChannelFactory> getTxChannelFactory() {
        return txChannelFactory;
    }
    
    public void setTxChannelFactory(Hashtable<String, ChannelFactory> txChannelFactory) {
        this.txChannelFactory = txChannelFactory;
    }
    
    private Channel createRxChannel(String media) throws UnknownComponentException {
        Channel rxChannel = rxChannelFactory.get(media).newInstance();
        rxChannel.connect(source);
        return rxChannel;
    }
    
    private void dropRxChannel(String media, Channel channel) {
        channel.disconnect(source);
        rxChannelFactory.get(media).release(channel);
    }

    private Channel createTxChannel(String media) throws UnknownComponentException {
        Channel txChannel = txChannelFactory.get(media).newInstance();
        txChannel.connect(source);
        return txChannel;
    }
    
    private void dropTxChannel(String media, Channel channel) {
        channel.disconnect(source);
        txChannelFactory.get(media).release(channel);
    }
    
    public Connection createConnection(ConnectionMode mode) throws TooManyConnectionsException, ResourceUnavailableException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Connection createLocalConnection(ConnectionMode mode) throws TooManyConnectionsException, ResourceUnavailableException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteConnection(String connectionID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteAllConnections() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasConnections() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isInUse() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setInUse(boolean inUse) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(RequestedSignal[] signals, RequestedEvent[] events) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(RequestedSignal[] signals, RequestedEvent[] events, String connectionID) {
        throw new UnsupportedOperationException("Not supported yet.");
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

}
