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
import org.mobicents.media.server.impl.clock.Quartz;

/**
 * Combines several signals for transmission over a single medium. A
 * demultiplexor completes the process by separating multiplexed signals from a
 * transmission line. Frequently a multiplexor and demultiplexor are combined
 * into a single device capable of processing both outgoing and incoming
 * signals.
 * <br>Multiplexer combines data and sends them, it is used as output for components.
 * @author Oleg Kulikov
 */
public class Multiplexer extends AbstractSink {

    private Format[] formats = null;
    private Format[] fmts = null;
    
    private HashMap<String, Input> inputs = new HashMap();
    private Output output;
    private int seq = 0;

    public Multiplexer() {
        super("Multiplexer");
        output = new Output();
    }

    public MediaSource getOutput() {
        return output;
    }

    public Format[] getFormats() {
        return output.sink != null ? output.sink.getFormats() : null;//fmts;
    }
    
    @Override
    public void connect(MediaSource source) {
        Input input = new Input();
        source.connect(input);
        inputs.put(((AbstractSource) source).getId(), input);
        reassemblyFormats();
    }

    @Override
    public void disconnect(MediaSource source) {
        Input input = inputs.remove(((AbstractSource) source).getId());
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
    }

    public boolean isAcceptable(Format fmt) {
        try {
            if (output.sink != null) {
                return output.sink.isAcceptable(fmt);
            }
        } catch (NullPointerException e) {
            return false;
        }

        return true;
    }

    public void receive(Buffer buffer) {
    }

    class Input extends AbstractSink {

        public Input() {
            super("Multiplexer.Input");
        }

        public boolean isAcceptable(Format fmt) {
            return true;
        }

        public void receive(Buffer buffer) {
            deliver(buffer);
        }

        public Format[] getFormats() {
            return output.sink != null ? output.sink.getFormats() : null;
        }
    }

    class Output extends AbstractSource {

        private boolean stopped = true;

        public Output() {
            super("Multiplexer.Output");
        }

        public void start() {
            stopped = false;
        }

        public void stop() {
            stopped = true;
            //timer.stop();
        }

        public Format[] getFormats() {
            return formats;
        }
    }

    public synchronized void deliver(Buffer buffer) {
        if (output != null && !output.stopped && output.sink != null ) {
            buffer.setSequenceNumber(seq);
            buffer.setTimeStamp(seq * Quartz.HEART_BEAT);
            output.sink.receive(buffer);
        }

        seq++;
    }

}

