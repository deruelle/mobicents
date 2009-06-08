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

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 *
 * @author kulikov
 */
public class Proxy {

    private final static Format[] DEFAULT_FORMATS = new Format[]{
        AVProfile.PCMA, AVProfile.PCMU, AVProfile.SPEEX, 
        AVProfile.GSM, AVProfile.G729, AVProfile.DTMF, Codec.LINEAR_AUDIO
    };
    
    private Input input;
    private Output output;
    
    private String connectionID;
    private boolean isSinkConnected;
    private boolean isSourceConnected;
    
    public Proxy(String name) {
        input = new Input(name +".input");
        output = new Output(name + ".output");
    }
    
    public MediaSink getInput() {
        return input;
    }
    
    public MediaSource getOutput() {
        return output;
    }
    
    public String getConnectionID() {
        return connectionID;
    }
    
    public void setConnectionID(String connectionID) {
        this.connectionID = connectionID;
    }

    public boolean isIsSinkConnected() {
        return isSinkConnected;
    }

    public boolean isIsSourceConnected() {
        return isSourceConnected;
    }
    
    public boolean isConnectedTo(MediaSink sink) {
        return output.isConnectedTo(sink);
    }
    
    public boolean isConnectedTo(MediaSource source) {
        return input.isConnectedTo(source);
    }
    
    private class Input extends AbstractSink {
        
        public Input(String name) {
            super(name);
        }

        @Override
        public void connect(MediaSource source) {
            super.connect(source);
            isSourceConnected = true;
        }
        

        @Override
        public void disconnect(MediaSource source) {
            super.disconnect(source);
            if (!output.isConnected()) {
                connectionID = null;
            }
            isSourceConnected = false;
        }
        
        public boolean isConnectedTo(MediaSource source) {
            return otherParty == source;
        }
        
        public Format[] getOtherPartyFormats() {
            return otherParty.getFormats();
        }
        
        public Format[] getFormats() {
            Format[] formats = output.isConnected() ? output.getOtherPartyFormats() : null;
            return formats != null && formats.length > 0 ? formats  : DEFAULT_FORMATS;
        }

        public boolean isAcceptable(Format format) {
            return output.isConnected() ? output.isAcceptable(format) : true;
        }

        public void receive(Buffer buffer) {
            if (output.isConnected()) {
                output.delivery(buffer);
            }
        }
        
    }
    
    private class Output extends AbstractSource {

        public Output(String name) {
            super(name);
        }
        
        public void start() {
        }

        public void stop() {
        }

        @Override
        public void connect(MediaSink sink) {
            super.connect(sink);
            isSinkConnected = true;
        }
        

        @Override
        public void disconnect(MediaSink sink) {
            super.disconnect(sink);
            if (!input.isConnected()) {
                connectionID = null;
            }
            isSinkConnected = false;
        }

        public boolean isConnectedTo(MediaSink sink) {
            return otherParty == sink;
        }
        
        public Format[] getOtherPartyFormats() {
            return otherParty.getFormats();
        }
        
        public boolean isAcceptable(Format fmt) {
            return otherParty.isAcceptable(fmt);
        }
        
        public Format[] getFormats() {
            Format[] formats = input.isConnected() ? input.getOtherPartyFormats() : null;
            return formats != null && formats.length > 0 ? formats  : DEFAULT_FORMATS;
        }
        
        public void delivery(Buffer buffer)  {
            if (otherParty != null && otherParty.isAcceptable(buffer.getFormat())) {
                otherParty.receive(buffer);
            }
        }
    }
}
