/*
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
package org.mobicents.media.server.impl.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.rtp.BufferFactory;
import org.mobicents.media.server.spi.Connection;

/**
 * Sends input signals into 
 * 
 * @author Oleg Kulikov
 */
public class Demultiplexer extends AbstractSource {

    private static final long serialVersionUID = -3391642385740571114L;
    private final static Format[] inputFormats = new Format[0];
    
    private Format[] outputFormats;
    
    private Input input = null;
    private Map<String, Output> branches = new ConcurrentHashMap<String, Output>();
    private volatile boolean started = false;
    private BufferFactory bufferFactory = null;

    public AbstractSink getInput() {
        return input;
    }

    public Demultiplexer(String name) {
        super(name);
        
        bufferFactory = new BufferFactory(10, name);
        input = new Input("Input." + name);
    }

    private String getIdentifier() {
        return getId();
    }
    
    @Override
    public void setConnection(Connection connection) {
        super.setConnection(connection);
        input.setConnection(connection);
    }
    
    public Format[] getFormats() {
        return input.getOtherPartyFormats();
    }

    @Override
    public void connect(MediaSink sink) {
        Output out = new Output("Output." + getName());
        branches.put(sink.getId(), out);
        out.connect(sink);
        this.reassemblyFormats();
    }

    @Override
    public void disconnect(MediaSink sink) {
        Output out = (Output) branches.remove(sink.getId());        
        out.disconnect(sink);
        this.reassemblyFormats();
    }

    /**
     * Reassemblies the list of used formats. This method is called each time
     * when connected/disconnected source
     */
    private void reassemblyFormats() {
        ArrayList list = new ArrayList();
        Collection<Output> outputs = branches.values();
        for (Output output : outputs) {
            Format[] fmts = output.getOtherPartyFormats();
            for (Format format : fmts) {
                if (!list.contains(format)) {
                    list.add(format);
                }
            }
        }

        outputFormats = new Format[list.size()];
        list.toArray(outputFormats);
    }
    
    

    public int getBranchCount() {
        return branches.size();
    }

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    
    
    private class Input extends AbstractSink {

        public Input(String name) {
            super(name);
        }

        public boolean isAcceptable(Format fmt) {
            for (int i = 0; i < outputFormats.length; i++) {
                if (outputFormats[i].matches(fmt)) {
                    return true;
                }
            }
            return false;
        }

        protected Format[] getOtherPartyFormats() {
            return otherParty != null ? otherParty.getFormats() : inputFormats;
        }

        public void receive(Buffer buffer) {
            if (!started) {
                return;
            }
            Collection<Output> streams = branches.values();
            for (Output stream : streams) {
                //stream.push((Buffer) buffer.clone());
                Buffer bufferNew = bufferFactory.allocate();
                bufferNew.copy(buffer);
                stream.push(bufferNew);
            }
            buffer.dispose();
        }

        public Format[] getFormats() {
            return inputFormats;
        }
    }

    private class Output extends AbstractSource {

        public Output(String parent) {
            super("Demultiplexer.Output:" + parent);
        }

        @Override
        public String getId() {
            return getIdentifier();
        }
        
        protected void push(Buffer buffer) {
            try {
                if (otherParty.isAcceptable(buffer.getFormat())) {
                    otherParty.receive(buffer);
                } else {
                    buffer.dispose();
                }
            } catch (NullPointerException e) {
                System.out.println("Error delivery");
            }
        }

        public void start() {
        }

        public void stop() {
        }

        public Format[] getOtherPartyFormats() {
            return otherParty != null ? otherParty.getFormats() : new Format[0];
        }
        
        public Format[] getFormats() {
            return input.getOtherPartyFormats();
        }

    }    
}
