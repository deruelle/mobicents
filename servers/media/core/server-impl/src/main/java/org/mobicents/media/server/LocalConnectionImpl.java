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

import java.io.IOException;
import javax.sdp.SdpException;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author kulikov
 */
public class LocalConnectionImpl extends ConnectionImpl {

    private LocalConnectionImpl otherConnection;

    public LocalConnectionImpl(EndpointImpl endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        super(endpoint, mode);
        try {
            txChannel = endpoint.createTxChannel(this);
            rxChannel = endpoint.createRxChannel(this);
        } catch (Exception e) {
            throw new ResourceUnavailableException(e);
        }
        
        setMode(mode);
    }
    
    public String getLocalDescriptor() {
        return null;
    }

    public String getRemoteDescriptor() {
        return null;
    }

    public void setRemoteDescriptor(String descriptor) throws SdpException, IOException, ResourceUnavailableException {
        
    }
    
    public void setOtherParty(Connection other) throws IOException {
        //hold reference for each other
        this.otherConnection = (LocalConnectionImpl) other;
        otherConnection.otherConnection = this;

        //join channels
        if (txChannel != null && otherConnection.rxChannel != null) {
            txChannel.connect(otherConnection.rxChannel);
        }
        
        if (rxChannel != null && otherConnection.txChannel != null) {
            otherConnection.txChannel.connect(rxChannel);
        }
    }

    @Override
    protected void close() {
        if (this.otherConnection == null) {
            return;
        }
        
        if (txChannel != null && otherConnection.rxChannel != null) {
            txChannel.disconnect(otherConnection.rxChannel);
        }
        
        if (rxChannel != null && otherConnection.txChannel != null) {
            rxChannel.disconnect(otherConnection.txChannel);
        }
        
        
        super.close();
    }
    
    public long getPacketsReceived(String media) {
        return rxChannel != null ? rxChannel.getPacketsTransmitted() : 0;
    }
    
    public long getPacketsTransmitted(String media) {
        return txChannel != null ? txChannel.getPacketsTransmitted() : 0;
    }
    
}
