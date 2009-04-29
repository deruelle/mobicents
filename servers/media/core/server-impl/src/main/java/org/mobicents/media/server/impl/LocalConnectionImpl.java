/*
 * RtpConnectionImpl.java
 *
 * Mobicents Media Gateway
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
package org.mobicents.media.server.impl;

import java.io.IOException;

import javax.sdp.SdpException;
import javax.sdp.SessionDescription;

import org.mobicents.media.Component;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * 
 * @author Oleg Kulikov
 */
public class LocalConnectionImpl extends BaseConnection {

    private SessionDescription localSDP;
    private SessionDescription remoteSDP;
    private LocalConnectionImpl otherConnection;

    /**
     * Creates a new instance of RtpConnectionImpl.
     * 
     * @param endpoint
     *            the endpoint executing this connection.
     * @param connectionID the unique identifier of the connection to be created
     * @param mode
     *            the mode of this connection.
     */
    public LocalConnectionImpl(Endpoint endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        super(endpoint, mode);
    }

    /**
     * Gets the reference to a connection joined with this one.
     * 
     * @return connection object or null if this connection not joined.
     */
    public Connection getOtherParty() {
        return this.otherConnection;
    }

    @Override
    public void setMode(ConnectionMode mode) {
        if (mode == ConnectionMode.RECV_ONLY) {
            endpoint.getPrimarySource(this).stop();
            if (otherConnection != null) {
                otherConnection.endpoint.getPrimarySource(otherConnection).start();
            }
        } else if (mode == ConnectionMode.SEND_ONLY) {
            endpoint.getPrimarySource(this).start();
            if (otherConnection != null) {
                otherConnection.endpoint.getPrimarySource(otherConnection).stop();
            }
        } else {
            endpoint.getPrimarySource(this).start();
            if (otherConnection != null) {
                otherConnection.endpoint.getPrimarySource(otherConnection).start();
            }
        }
        super.setMode(mode);
    }    
    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#getLocalDescriptor();
     */
    public String getLocalDescriptor() {
        return null;
    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#getRemoteDescriptor();
     */
    public String getRemoteDescriptor() {
        return remoteSDP != null ? remoteSDP.toString() : null;
    }

    /**
     * (Non-Javadoc).
     * 
     * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
     */
    public void setRemoteDescriptor(String descriptor) throws SdpException, IOException, ResourceUnavailableException {
        try {
            this.lockState();
            if (state != ConnectionState.HALF_OPEN && state != ConnectionState.OPEN) {
                throw new IllegalStateException("State is " + state);
            }
            if (state != ConnectionState.HALF_OPEN && state != ConnectionState.OPEN) {
                throw new IllegalStateException("State is " + state);
            }
            setState(ConnectionState.OPEN);
        } catch (InterruptedException e) {
//            logger.error("Failed to lock connection due to exception, possibly server is shutting down.");
            e.printStackTrace();
            //FIXME: baranowb: shouldnt we close here instead?
            throw new ResourceUnavailableException(e);
        } finally {
            this.releaseState();
        }
    }

    /**
     * (Non-Javadoc).
     * 
     * @throws InterruptedException
     * 
     * @see org.mobicents.media.server.spi.Connection#setRemoteDescriptor();
     */
    public void setOtherParty(Connection other) throws IOException {
        if (other == null) {
            throw new IllegalArgumentException("Other party can not be null");
        }
        if (state == ConnectionState.CLOSED) {
            throw new IllegalStateException("Connection is closed");
        }
        if (otherConnection == other) {
            return;
        }
        try {
            otherConnection = (LocalConnectionImpl) other;
            if (otherConnection.endpoint.getPrimarySink(otherConnection) != null) {
                endpoint.allocateMediaSources(this, otherConnection.endpoint.getFormats());
            }
            
            if (endpoint.getPrimarySink(this) != null) {
                otherConnection.endpoint.allocateMediaSources(other, endpoint.getPrimarySink(this).getFormats());
            }
            
            MediaSink sink1 = endpoint.getPrimarySink(this);
            MediaSource source1 = otherConnection.endpoint.getPrimarySource(otherConnection);
            
            if (sink1 != null && source1 != null) {
                sink1.connect(source1);
            }

            MediaSink sink2 = otherConnection.endpoint.getPrimarySink(otherConnection);
            MediaSource source2 = endpoint.getPrimarySource(this);
            
            if (sink2 != null && source2 != null) {
                sink2.connect(source2);
            }
            
            setMode(mode);
            setState(ConnectionState.OPEN);
            otherConnection.setState(ConnectionState.OPEN);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    @Override
    protected void close() {
        endpoint.connections.remove(id);
        if (otherConnection != null) {
            MediaSink sink = endpoint.getPrimarySink(this);
            MediaSource source = otherConnection.endpoint.getPrimarySource(otherConnection);

            if (sink != null && source != null) {
                source.disconnect(sink);
            }
            
            sink = otherConnection.endpoint.getPrimarySink(otherConnection);
            source = endpoint.getPrimarySource(this);
            
            if (sink != null && source != null) {
                sink.disconnect(source);
            }
            otherConnection = null;
        }
        super.close();
    }

    /**
     * Gets the text representation of the connection.
     * 
     * @return text representation of the connection.
     */
    @Override
    public String toString() {
        return "(LocalConnection, ID=" + id + ", endpoint=" + endpointName + ", state=" + state + ")";
    }

    public void error(Exception e) {
        endpoint.deleteConnection(id);
    }

    public String getOtherEnd() throws IllegalArgumentException {
        try {

            if (otherConnection != null) {
                return otherConnection.getEndpoint().getLocalName();
            }
        } catch (NullPointerException e) {
//            logger.error("Could not get other end", e);
        }
        return null;
    }

    public boolean isGatherStats() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setGatherStats(boolean gatherStats) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPacketsSent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPacketsReceived() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getOctetsReceived() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getOctetsSent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getInterArrivalJitter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPacketsLost() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addNotificationListener(NotificationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeNotificationListener(NotificationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Component getComponent(int resourceType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}


