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
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author kulikov
 */
public class LocalConnectionImpl extends ConnectionImpl {

    private LocalConnectionImpl otherConnection;
    private Input rxInput, txInput;
    private Output rxOutput, txOutput;
    
    public LocalConnectionImpl(EndpointImpl endpoint, ConnectionMode mode) throws ResourceUnavailableException {
        super(endpoint, mode);
        try {
            txChannel = endpoint.createTxChannel(this);
            rxChannel = endpoint.createRxChannel(this);
        } catch (Exception e) {
            throw new ResourceUnavailableException(e);
        }
        
        rxOutput = new Output("local.connection.rxOutput");
        txOutput = new Output("local.connection.txOutput");
        
        rxOutput.setConnection(this);
        txOutput.setConnection(this);
        
        rxInput = new Input("local.connection.rxInput", rxOutput);
        txInput = new Input("local.connection.txInput", txOutput);   
        
        rxInput.setConnection(this);
        txInput.setConnection(this);
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
            txChannel.connect(txInput);
            otherConnection.rxChannel.connect(txOutput);
        }
        
        if (rxChannel != null && otherConnection.txChannel != null) {
            rxChannel.connect(rxOutput);
            otherConnection.txChannel.connect(rxInput);
        }
    }

    @Override
    protected void close() {
        if (this.otherConnection == null) {
            return;
        }
        
        if (txChannel != null && otherConnection.rxChannel != null) {
            txChannel.disconnect(txInput);
            otherConnection.rxChannel.disconnect(txOutput);
            txChannel.setConnection(null);
        }
        
        if (rxChannel != null && otherConnection.txChannel != null) {
            rxChannel.disconnect(rxOutput);
            otherConnection.txChannel.disconnect(rxInput);
            rxChannel.setConnection(null);
        }
        
        otherConnection.otherConnection = null;
        otherConnection = null;
        
        txInput.setConnection(null);
        rxInput.setConnection(null);
        
        txOutput.setConnection(null);
        rxOutput.setConnection(null);
        
        super.close();
    }
    
    private class Output extends AbstractSource {

        public Output(String name) {
            super(name);
        }
        
        public void start() {
        }

        public void stop() {
        }

        public Format[] getFormats() {
            return otherParty != null ? otherParty.getFormats() : new Format[0];
        }
        
        protected boolean isAcceptable(Format fmt) {
            return otherParty != null && otherParty.isAcceptable(fmt);
        }
        
        public void delivery(Buffer buffer) {
            if (isAcceptable(buffer.getFormat())) {
                otherParty.receive(buffer);
            }
        }
    }
    
    private class Input extends AbstractSink {

        private Output output;
        private Format fmt;
        private boolean isAcceptable;
        
        public Input(String name, Output output) {
            super(name);
            this.output = output;
        }
        
        public Format[] getFormats() {
            return output.getFormats();
        }

        public boolean isAcceptable(Format format) {
            return output.isAcceptable(format);
        }

        public void receive(Buffer buffer) {
            output.delivery(buffer);
        }
        
    }
}
