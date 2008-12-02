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
    private Input input =null;
    private HashMap<String, Output> branches = new HashMap<String, Output>();
    //private final static ExecutorService demuxThreadPool = Executors.newCachedThreadPool(new Demultiplexer.ThreadFactoryImpl());
    private Format[] formats;
    private boolean started = false;
    private String upperComponent=null;
    public AbstractSink getInput() {
        return input;
    }

    public Demultiplexer(Format[] formats) {
        super("Demultiplexer");
        this.formats = formats;
        this.input=new Input(upperComponent);
        this.input.setWorkDataSink(this);
   

    }

    @Override
    public void connect(MediaSink sink) {
   
        synchronized (branches) {
            Output out = new Output(upperComponent);
            branches.put(((AbstractSink) sink).getId(), out);
            sink.connect(out);
        }
    }

    @Override
    public void disconnect(MediaSink sink) {
        synchronized (branches) {
            Output out = (Output) branches.remove(((AbstractSink) sink).getId());
            if (out != null) {
                sink.disconnect(out);
                out.dispose();
            }
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
            super("Demultiplexer.Input:"+parent);
        }

        public boolean isAcceptable(Format fmt) {
            return true;
        }

        public void receive(Buffer buffer) {
            if (!started) {
                return;
            }
            synchronized (branches) {
                boolean transffered = false;
                Collection<Output> streams = branches.values();
                for (Output stream : streams) {
                    transffered = true;
                    stream.push((Buffer) buffer.clone());
                    stream.run();
                //demuxThreadPool.submit(stream);
                }
                if(transffered)
                {
                	try{
                		super.octetsReceived(buffer.getLength());
                	}catch(Exception e)
                	{
                		
                	}
                }
                if (!transffered) {
                    CachedBuffersPool.release(buffer);
                }
                
                
            }
        }

        public Format[] getFormats() {
            // return mediaStream != null ? mediaStream.getFormats() : null;
            return formats;
        }
    }

    private class Output extends AbstractSource implements Runnable {

        public Output(String parent) {
            super("Demultiplexer.Output:"+parent);
        }
        private ArrayList<Buffer> buffers = new ArrayList();

        protected void push(Buffer buffer) {
            synchronized (buffers) {
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
        // return input.getFormats();
        return formats;
    }
}
