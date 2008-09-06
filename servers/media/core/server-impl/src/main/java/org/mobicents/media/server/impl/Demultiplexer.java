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
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;

/**
 * Combines several signals for transmission over a single medium. 
 * A demultiplexor completes the process by separating multiplexed signals 
 * from a transmission line. Frequently a multiplexor and demultiplexor are 
 * combined into a single device capable of processing both outgoing and 
 * incoming signals. 
 * 
 * @author Oleg Kulikov
 */
public class Demultiplexer extends AbstractSource {

    private Input input = new Input();
    private HashMap<MediaSink, Output> branches = new HashMap();
    private final static ExecutorService threadPool = Executors.newCachedThreadPool();

    private Format[] formats;
    private transient Logger logger = Logger.getLogger(Demultiplexer.class);
    
    public AbstractSink getInput() {
        return input;
    }

    public Demultiplexer(Format[] formats) {
        this.formats = formats;
    }
    
    @Override
    public void connect(MediaSink sink) {
        synchronized (branches) {
            Output out = new Output();
            branches.put(sink, out);
            sink.connect(out);
        }
    }

    @Override
    public void disconnect(MediaSink sink) {
        synchronized (branches) {
            Output out = (Output) branches.remove(sink);
            if (out != null) {
                sink.disconnect(out);
            }
        }
    }

    public int getBranchCount() {
        return branches.size();
    }
    
    public void start() {
    }

    public void stop() {
    }

    private class Input extends AbstractSink {

        public boolean isAcceptable(Format fmt) {
            return true;
        }

        public void receive(Buffer buffer) {
            synchronized (branches) {
                boolean transffered = false;
                Collection<Output> streams = branches.values();
                for (Output stream : streams) {
                    transffered = true;
                    stream.push((Buffer) buffer.clone());
                    threadPool.submit(stream);
                }

                if (!transffered) {
                    CachedBuffersPool.release(buffer);
                }
            }
        }

        public Format[] getFormats() {
            //return mediaStream != null ?  mediaStream.getFormats() : null;
            return formats;
        }
    }

    private class Output extends AbstractSource implements Runnable {

        private ArrayList <Buffer> buffers = new ArrayList();
        
        protected void push(Buffer buffer) {
            synchronized(buffers) {
                buffers.add(buffer);
            }
        }

        protected boolean isAcceptable(Format fmt) {
            return sink != null ? sink.isAcceptable(fmt) : true;
        }
        
        public void start() {
        }

        public void stop() {
        }

        public void run() {
            if (sink != null && !buffers.isEmpty()) {
                Buffer buffer = buffers.remove(0);
                if (sink.isAcceptable(buffer.getFormat())) {
                    sink.receive(buffer);
                }
            }
        }

        public Format[] getFormats() {
            return input.getFormats();
        }
    }

    public Format[] getFormats() {
        //return input.getFormats();
        return formats;
    }
}
