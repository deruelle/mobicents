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
package org.mobicents.media.server.impl.resource.prelay;

import java.io.IOException;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.ConnectionImpl;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSinkSet;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.AbstractSourceSet;
import org.mobicents.media.server.impl.BaseComponent;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.ResourceGroup;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 *
 * @author kulikov
 */
public class Bridge extends BaseComponent implements ResourceGroup {
    protected final static Format DEFAULT_FORMAT[] = new Format[]{
        AVProfile.PCMA, AVProfile.PCMU, AVProfile.SPEEX, 
        AVProfile.GSM, AVProfile.G729, AVProfile.DTMF, Codec.LINEAR_AUDIO
    };
    
    protected PacketRelay[] proxies = new PacketRelay[2];
    private EndpointImpl endpoint;
    
    private Input input;
    private Output output;
        
    public Bridge(String name) {
        super(name);
        this.endpoint = (EndpointImpl) endpoint;
        
        proxies[0] = new PacketRelay("PacketRelay-0");
        proxies[1] = new PacketRelay("PacketRelay-1");
        
        proxies[0].getInput().start();
        proxies[0].getOutput().start();

        proxies[1].getInput().start();
        proxies[1].getOutput().start();   
        
        input = new Input("PacketRelay[Input]");
        output = new Output("PacketRelay[Output]");
    }
    
    public MediaSource getSource() {
        return output;
    }
    
    public MediaSink getSink() {
        return input;
    }

    public void start() {
        proxies[0].start();
        proxies[1].start();
    }

    public void stop() {
        proxies[0].stop();
        proxies[1].stop();
    }
    
    private class Input extends AbstractSinkSet {

        public Input(String name) {
            super(name);
        }
        
        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        public Format[] getFormats() {
            return null;
        }

        public boolean isAcceptable(Format format) {
            return false;
        }

        @Override
        public void onMediaTransfer(Buffer buffer) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AbstractSink createSink(MediaSource otherParty) {
            ConnectionImpl connection = (ConnectionImpl) otherParty.getConnection();
            int idx = connection.getIndex();
            return (AbstractSink) proxies[idx].getInput();
        }

        @Override
        public void destroySink(AbstractSink sink) {
        }

        
    }
    
    private class Output extends AbstractSourceSet {

        public Output(String name) {
            super(name);
        }
        

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void evolve(Buffer buffer, long sequenceNumber) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Format[] getFormats() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AbstractSource createSource(MediaSink otherParty) {
            ConnectionImpl connection = (ConnectionImpl) otherParty.getConnection();
            int idx = Math.abs(connection.getIndex() - 1);
            return (AbstractSource) proxies[idx].getOutput();
        }

        @Override
        public void destroySource(AbstractSource source) {
        }
        
    }

}
