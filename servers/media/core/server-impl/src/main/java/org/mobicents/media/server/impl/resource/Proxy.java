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
package org.mobicents.media.server.impl.resource;

import java.io.IOException;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.BaseComponent;

/**
 *
 * @author kulikov
 */
public class Proxy extends BaseComponent implements Inlet, Outlet {

    private Input input;
    private Output output;
    
    private Format[] formats;
    private Buffer buff;
    
    public Proxy(String name) {
        super(name);
        input = new Input(name);
        output = new Output(name);
    }
    
    public Format[] getFormats() {
        return formats;
    }
    
    public void setFormat(Format[] formats) {
        this.formats = formats;
    }
    
    public void start() {
        input.start();
        output.start();
    }

    public void stop() {
        input.stop();
        output.stop();
    }

    public MediaSink getInput() {
        return input;
    }

    public MediaSource getOutput() {
        return output;
    }

    private class Input extends AbstractSink {
        
        public Input(String name) {
            super("input." + name);
        }
        
        @Override
        public void onMediaTransfer(Buffer buffer) throws IOException {
            buff = buffer;
            output.run();
        }

        public Format[] getFormats() {
            return formats != null ? formats : output.getOtherPartyFormats();
        }
        
        public Format[] getOtherPartyFormats() {
            return otherParty != null ? otherParty.getFormats() : new Format[0];
        }

        public boolean isAcceptable(Format format) {
            if (formats != null) {
                for (Format fmt: formats) {
                    if (fmt.matches(format)) {
                        return true;
                    }
                }
                return false;
            }
            return output.isAcceptable(format);
        }
        
        @Override
        public String toString() {
            return "Proxy[" + getName() + "]";
        }
    }
        
    private class Output extends AbstractSource {

        public Output(String name) {
            super("output." + name);
        }
        
        @Override
        public void start() {
        }
        
        @Override
        public void stop() {
        }
        
        @Override
        public void evolve(Buffer buffer, long sequenceNumber) {
            buffer.copy(buff);
        }

        public boolean isAcceptable(Format format) {
            return otherParty != null && otherParty.isAcceptable(format);
        }
        
        public Format[] getOtherPartyFormats() {
            return otherParty != null ? otherParty.getFormats() : new Format[0];
        }
        
        public Format[] getFormats() {
            return formats != null? formats : input.getOtherPartyFormats();
        }
     
        @Override
        public String toString() {
            return "Proxy[" + getName() + "]";
        }
        
    }
}
