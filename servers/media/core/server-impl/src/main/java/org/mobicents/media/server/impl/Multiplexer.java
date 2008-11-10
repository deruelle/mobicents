/**
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
import org.mobicents.media.MediaSource;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.clock.Quartz;
import org.mobicents.media.server.impl.clock.Timer;
import org.mobicents.media.server.impl.clock.TimerTask;

/**
 * Combines several signals for transmission over a single medium. A
 * demultiplexor completes the process by separating multiplexed signals from a
 * transmission line. Frequently a multiplexor and demultiplexor are combined
 * into a single device capable of processing both outgoing and incoming
 * signals.
 * 
 * @author Oleg Kulikov
 */
public class Multiplexer extends AbstractSink implements TimerTask {

    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW,
            8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW,
            8000, 8, 1);
    private final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1, AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    private final static AudioFormat DTMF = new AudioFormat(
            "telephone-event/8000");
    private Format[] formats = null;
    private HashMap<String, Input> inputs = new HashMap();
    private Output output;
    private ArrayList<Buffer> packets = new ArrayList();
    private Timer timer;
    private int seq = 0;

    

    public Multiplexer() {
    	super("Multiplexer");
        output = new Output();
        timer = new Timer();
        timer.setListener(this);
    }

    public MediaSource getOutput() {
        return output;
    }

    public Format[] getFormats() {
        return output.sink != null ? output.sink.getFormats() : null;
    }

    private void print(Format[] fmts) {
        if (fmts != null) {
            for (Format f : fmts) {
                System.out.println(f);
            }
        } else {
            System.out.println("NULL");
        }
    }

    @Override
    public void connect(MediaSource source) {
        Input input = new Input();
        // input.connect(source);
        source.connect(input);
        inputs.put(((AbstractSource)source).getId(), input);
        reassemblyFormats();
    }

    @Override
    public void disconnect(MediaSource source) {
        Input input = inputs.remove(((AbstractSource)source).getId());
        if (input != null) {
            source.disconnect(input);
            input.dispose();
            reassemblyFormats();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        inputs.clear();
    }
    /**
     * Reassemblies the list of used formats. This method is called each time
     * when connected/disconnected source
     */
    private void reassemblyFormats() {
        ArrayList list = new ArrayList();
        Collection<Input> sources = inputs.values();
        for (Input input : sources) {
            Format[] fmts = input.mediaStream != null ? input.mediaStream.getFormats() : null;
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
    // commonFormats = (Format[]) list.toArray();
    }

    public boolean isAcceptable(Format fmt) {
        try {
            if (output.sink != null) {
                return output.sink.isAcceptable(fmt);
            }
        } catch (NullPointerException e) {
        	logger.error("Format not supported", e);
            return false;
        }

        return true;
    }

    public void receive(Buffer buffer) {
    }

    class Input extends AbstractSink {
    	
    	public Input(){
    		super("Multiplexer.Input");
    	}

        public boolean isAcceptable(Format fmt) {
            return true;
        }

        public void receive(Buffer buffer) {
            deliver(buffer);
            //synchronized (packets) {
                // if (packets.size() > 2 * inputs.size()) {
                // packets.remove(0);
                // }
                // System.out.println("Append : " + buffer.getFormat());
//                packets.add(buffer);
//            }
        }

        public Format[] getFormats() {
            return output.sink != null ? output.sink.getFormats() : null;
        }
    }

    class Output extends AbstractSource {
    	
        private boolean stopped = true;
        
       	public Output(){
    		super("Multiplexer.Output");
    	}

        public void start() {
            stopped = false;
        }

        public void stop() {
            stopped = true;
        }

        public Format[] getFormats() {
            return formats;
        }
    }

    public synchronized void deliver(Buffer buffer) {
        //System.out.println("Recv p=" + buffer.getSequenceNumber() + ", stopped=" + output.stopped);
        if (output != null && !output.stopped && output.sink != null) {
            buffer.setSequenceNumber(seq);
            buffer.setTimeStamp(seq * Quartz.HEART_BEAT);
            output.sink.receive(buffer);
        }
        seq++;
    }

    public void run() {
        synchronized (packets) {
            if (packets.size() > 0) {
                Buffer buffer = packets.remove(0);
                buffer.setSequenceNumber(seq);
                buffer.setTimeStamp(seq * Quartz.HEART_BEAT);
//                    if (output != null && output.sink != null &&
//                            output.sink.isAcceptable(buffer.getFormat())) {
//                        output.sink.receive(buffer);
//                    }
                if (output != null && output.sink != null) {
                    output.sink.receive(buffer);
                }
                seq++;
            }
        }
    }

    public void started() {
    }

    public void ended() {
    }
}

