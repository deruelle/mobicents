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

import org.mobicents.media.Component;
import org.mobicents.media.server.resource.Channel;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author kulikov
 */
public abstract class ConnectionImpl implements Connection {

    private static int GEN = 1;
    private String id;
    private int index;
    
    private EndpointImpl endpoint;
    private ConnectionState state = ConnectionState.NULL;
    private ConnectionMode mode;
    
    protected Channel txChannel;
    protected Channel rxChannel;
    
    
    protected ConcurrentHashMap connectionListeners = new ConcurrentHashMap();
    
    public ConnectionImpl(EndpointImpl endpoint, ConnectionMode mode) {
        this.id = genID();
        this.endpoint = endpoint;
        this.mode = mode;
        this.index = endpoint.getIndex();
    }
    
    /**
     * Generates unique identifier for this connection.
     * 
     * @return hex view of the unique integer.
     */
    private String genID() {
        GEN++;
        if (GEN ==Integer.MAX_VALUE) {
            GEN = 1;
        }
        return Integer.toHexString(GEN);
//        return (new UID()).toString();
    }
    
    public String getId() {
       return this.id;
    }

    public int getIndex() {
        return index;
    }
    
    public ConnectionState getState() {
        return state;
    }

    protected void setState(ConnectionState state) {
    	ConnectionState oldState = this.state;
        this.state = state;
        
        if(oldState!=this.state)
        {
        	//FIXME: does this require sync, maybe we should add here tasks?
        	for(Object ocl: this.connectionListeners.keySet())
        	{
        		ConnectionListener cl = (ConnectionListener) ocl;
        		cl.onStateChange(this, oldState);
        	}
        }
    }
    
    public int getLifeTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLifeTime(int lifeTime) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ConnectionMode getMode() {
        return mode;
    }

    public void setMode(ConnectionMode mode) {
        this.mode = mode;
        if (mode == ConnectionMode.SEND_RECV) {
            if (txChannel != null) txChannel.start();
            if (rxChannel != null) rxChannel.start();
        } else if (mode == ConnectionMode.SEND_ONLY) {
            if (txChannel != null) txChannel.start();
            if (rxChannel != null) rxChannel.stop();
        } else if (mode == ConnectionMode.RECV_ONLY) {
            if (txChannel != null) txChannel.stop();
            if (rxChannel != null) rxChannel.start();
        }
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }


    public void addListener(ConnectionListener listener) {
        System.out.println("ADDED LISTENER" + listener);
        this.connectionListeners.put(listener, listener);
    }

    public void removeListener(ConnectionListener listener) {
        this.connectionListeners.remove(listener);
    }

    public Component getComponent(int resourceID) {
        return null;
    }
    
    public Component getComponent(String name, int chanID) {
        Component c = null;
        switch (chanID) {
            case Connection.CHANNEL_RX :
                return rxChannel != null ? rxChannel.getComponent(name) : null;
            case Connection.CHANNEL_TX :
                return txChannel != null ? txChannel.getComponent(name) : null;
            default :
                return null;
        }
    }
    
    public void addNotificationListener(NotificationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeNotificationListener(NotificationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
        
    
    protected void close() {
       ((EndpointImpl) getEndpoint()).releaseRxChannel(rxChannel);
       ((EndpointImpl) getEndpoint()).releaseTxChannel(txChannel);
       
       rxChannel = null;
       txChannel = null;
       
        setState(ConnectionState.CLOSED);
    }
}
