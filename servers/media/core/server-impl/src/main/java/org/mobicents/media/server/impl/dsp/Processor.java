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
package org.mobicents.media.server.impl.dsp;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.BaseComponent;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.dsp.SignalingProcessor;

/**
 * Implements DSP features.
 * 
 * Processor has input and output and is used to perform required 
 * transcoding if needed for packets followed from source to consumer. 
 * Processor is transparent for packets with format acceptable by consumer 
 * by default. 
 * 
 * @author Oleg Kulikov
 */
public class Processor extends BaseComponent implements SignalingProcessor {

    
    private ArrayList<Format> inputFormats = new ArrayList();
    private ArrayList<Format> outputFormats = new ArrayList();
    
    private Input input;
    private Output output;
    
    private transient ArrayList<Codec> codecs = new ArrayList();
    
    private Transformator transformator;
    private ArrayList<Transformator> map = new ArrayList();

    public Processor(String name) {
        super(name);
        input = new Input();
        output = new Output();
    }

    private void append(List<Format> list, Format fmt) {
        for (Format f : list) {
            if (f.matches(fmt)) return;
        }
        list.add(fmt);
    }
    
    private Format[] toArray(List<Format> list) {
        Format[] array = new Format[list.size()];
        list.toArray(array);
        return array;
    }
    
    protected void add(Codec codec) {
        codecs.add(codec);        
        append(inputFormats, codec.getSupportedInputFormat());
        append(outputFormats, codec.getSupportedOutputFormat());
    }
    
    /**
     * Gets the input for original media
     * 
     * @return media handler for input media.
     */
    public MediaSink getInput() {
        return input;
    }

    /**
     * Gets the output stream with transcoded media.
     * 
     * @return media stream.
     */
    public MediaSource getOutput() {
        return output;
    }

    /**
     * Checks is format presented in the list.
     * 
     * @param fmts the list of formats to check
     * @param fmt the format instance to check.
     * @return true if fmt is in list of fmts.
     */
    private boolean contains(Format[] fmts, Format fmt) {
        for (int i = 0; i < fmts.length; i++) {
            if (fmts[i].matches(fmt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Implements output of the processor.
     */
    private class Output extends AbstractSource {
        //references to the format of last processed packet
        private Format format;
        protected boolean connected = false;
        protected int failDeliveryCount = 0;

        private boolean started = false;
        
        public Output() {
            super("Processor.Output");
        }

        @Override
        public void connect(MediaSink sink) {
            super.connect(sink);
            if (input.isConnected()) {
                configure(input.getOtherPartyFormats(), output.getOtherPartyFormats());
            }
        }

        protected boolean isConnected() {
            return otherParty!= null;
        }
        
        protected Format[] getOtherPartyFormats() {
            return otherParty.getFormats();
        }
        
        public void start() {            
            started = true;
        }

        public void stop() {
            started = false;
        }

        public Format[] getFormats() {
            return toArray(outputFormats);
        }

        /**
         * Transmits buffer to the output handler.
         * 
         * @param buffer the buffer to transmit
         */
        protected void transmit(Buffer buffer) {
            if (!started) {
            	buffer.dispose();
                return;
            }
            //Here we work in ReceiveStream.run method, which runs in local ReceiveStreamTimer
            // Discard packet silently if output handler is not assigned yet
            if (otherParty == null) {
            	buffer.dispose();
                return;
            }

            //compare format of the currently processing packet with last one
            //and if same use same codec also else reassign codec
            if (format == null || !format.equals(buffer.getFormat())) {
                //disable last used transformator
                transformator = null;
                //searcg for new one
                for (int i = 0; i < map.size(); i++) {
                    if (map.get(i).getFormat().equals(buffer.getFormat())) {
                        transformator = map.get(i);
                        format = buffer.getFormat();
                        break;
                    }
                }
            }
            
            if (transformator != null) {
                try {
                    transformator.process(buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            codec.process(buffer);

            // Codec can delay media transition if it has not enouph media
            // to perform its job. 
            // It means that Processor should check FLAGS after codec's 
            // work and discard packet if required
            if (buffer.getFlags() == Buffer.FLAG_DISCARD) {
            	buffer.dispose();
                return;
            }

            //may be a situation when original format can not be trancoded to 
            //one of the required output. In this case codec map will have no 
            //entry for this format. also codec may has no entry in case of when 
            //transcoding is not required. to differentiate these two cases check
            //if this format is acceptable by the consumer.

            //deliver packet to the consumer
            try {
                otherParty.receive(buffer);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * Implements output of the processor.
     */
    private class Input extends AbstractSink {

        public Input() {
            super("Processor.Input");
        }

        @Override
        public void connect(MediaSource source) {
            super.connect(source);
            if (output.isConnected()) {
                configure(input.getOtherPartyFormats(), output.getOtherPartyFormats());
            }
        }
        
        public boolean isAcceptable(Format format) {
            return true;
        }

        public void receive(Buffer buffer) {
            output.transmit(buffer);
        }

        protected boolean isConnected() {
            return otherParty!= null;
        }
        
        protected Format[] getOtherPartyFormats() {
            return otherParty.getFormats();
        }
        
        public Format[] getFormats() {
            return toArray(inputFormats);
        }
    }

    /**
     * Creates transformation for media from one format to another.
     * 
     * @param f1 the format of the input media
     * @param f2 the format of the output media.
     * @return true if this transformation available and false otherwise.
     */
    private boolean configure(Format f1, Format f2) {
        output.format = null;
        //looking for direct transformation
        for (int i = 0; i < codecs.size(); i++) {
            Codec c = codecs.get(i);
            if (c.getSupportedInputFormat().equals(f1) &&
                    c.getSupportedOutputFormat().equals(f2)) {
                map.add(new Transformator(f1, c, null));
                return true;
            }
        }

        //if there is no direct transformation then decompresson/compresson
        //algorithm should be applied
        Codec decoder = null;

        //looking for decoder
        for (int i = 0; i < codecs.size(); i++) {
            Codec c = codecs.get(i);
            if (c.getSupportedInputFormat().equals(f1) &&
                    c.getSupportedOutputFormat().getEncoding().equals(AudioFormat.LINEAR)) {
                decoder = c;
                break;
            }
        }

        if (decoder == null) {
            return false;
        }

        Codec encoder = null;
        //looking for encoder
        for (int i = 0; i < codecs.size(); i++) {
            Codec c = codecs.get(i);
            if (c.getSupportedInputFormat().getEncoding().equals(AudioFormat.LINEAR) &&
                    c.getSupportedOutputFormat().equals(f2)) {
                encoder = c;
                break;
            }
        }

        if (encoder == null) {
            return false;
        }

        map.add(new Transformator(f1, decoder, encoder));
        return true;
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.spi.dsp.SignalingProcessor.configure(Format[], Format[]).
     */
    public void configure(Format[] inputFormats, Format[] outputFormats) {
        map.clear();
        for (int i = 0; i < inputFormats.length; i++) {
            //if input format is same as one of the 
            //output we skip it
            if (contains(outputFormats, inputFormats[i])) {
                continue;
            }

            //we should find only one available transformation
            //so we break searching after first suitable match
            for (int j = 0; j < outputFormats.length; j++) {
                if (configure(inputFormats[i], outputFormats[j])) {
                    break;
                }
            }
        }
    }

    private class Transformator implements Serializable {

        private Format inputFormat;
        private Codec decoder;
        private Codec encoder;

        protected Transformator(Format inputFormat, Codec decoder, Codec encoder) {
            this.inputFormat = inputFormat;
            this.decoder = decoder;
            this.encoder = encoder;
        }

        protected Format getFormat() {
            return inputFormat;
        }

        protected void process(Buffer buffer) {
            if (decoder != null) {
                decoder.process(buffer);
            }
            if (encoder != null) {
                encoder.process(buffer);
            }
        }
    }

}
