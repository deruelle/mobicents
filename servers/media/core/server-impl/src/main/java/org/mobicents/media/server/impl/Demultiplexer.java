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
package org.mobicents.media.server.impl;

import java.util.ArrayList;
import java.util.Collection;

import java.util.concurrent.ConcurrentHashMap;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;

/**
 * Sends input signals into 
 * 
 * @author Oleg Kulikov
 */
public class Demultiplexer extends AbstractSource {

    /**
     * 
     */
    private static final long serialVersionUID = -3391642385740571114L;
    private Input input = null;
    private ConcurrentHashMap<String, Output> branches = new ConcurrentHashMap<String, Output>();
    private Format[] inputFormats;
    private Format[] formats;
    private boolean started = false;
    private String upperComponent = null;

    public AbstractSink getInput() {
        return input;
    }

    public Demultiplexer(Format[] formats) {
        super("Demultiplexer");
        this.inputFormats = formats;
        this.input = new Input(upperComponent);
        this.input.setWorkDataSink(this);
    }

    public void setFormats(Format[] formats) {
        this.inputFormats = formats;
    }
    
    public Format[] getFormats() {
        return input.mediaStream != null ? 
            input.mediaStream.getFormats() : null;
        
    }
    
    @Override
    public void connect(MediaSink sink) {
//        super.connect(sink);
        Output out = new Output("");
        branches.put(((AbstractSink) sink).getId(), out);
        out.connect(sink);
//        sink.connect(out);
//        this.reassemblyFormats();
    }

    @Override
    public void disconnect(MediaSink sink) {
        Output out = (Output) branches.remove(((AbstractSink) sink).getId());
        if (out != null) {
            sink.disconnect(out);
            out.dispose();
        }
//        super.disconnect(sink);
    }

    /**
     * Reassemblies the list of used formats. This method is called each time
     * when connected/disconnected source
     */
    private void reassemblyFormats() {
        ArrayList list = new ArrayList();
        Collection<Output> sinks = branches.values();
        for (Output output : sinks) {
            Format[] fmts = output.sink != null ? output.sink.getFormats() : null;
            if (fmts != null) {
                for (Format format : fmts) {
                    if (!list.contains(format)) {
                        list.add(format);
                    }
                }
            }
        }

        if (list.size() > 0) {
            formats = new Format[list.size()];
            list.toArray(formats);
        } else {
            formats = null;
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        this.branches.clear();
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

        public Input(String parent) {
            super("Demultiplexer.Input:" + parent);
        }

        public boolean isAcceptable(Format fmt) {
            return true;
        }

        public void receive(Buffer buffer) {
            if (!started) {
                return;
            }
            Collection<Output> streams = branches.values();
            for (Output stream : streams) {
                stream.push((Buffer) buffer.clone());
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

        protected void push(Buffer buffer) {
            try {
                if (sink.isAcceptable(buffer.getFormat())) {
                    sink.receive(buffer);
                }
            } catch (NullPointerException e) {
            }
        }

        protected boolean isAcceptable(Format fmt) {
            return sink != null ? sink.isAcceptable(fmt) : true;
        }

        public void start() {
        }

        public void stop() {
        }

        public Format[] getFormats() {
            return formats;
        }
    }

}
