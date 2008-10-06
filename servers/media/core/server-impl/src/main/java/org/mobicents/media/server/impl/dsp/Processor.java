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
import java.util.HashMap;
import java.util.Set;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.dsp.Codec;

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
public class Processor implements Serializable {

    private Input input;
    private Output output;
    private final static ArrayList<Codec> codecs = new ArrayList();
    private transient Logger logger = Logger.getLogger(Processor.class);
    protected String resourceName = null;
    

    static {
        codecs.add(new org.mobicents.media.server.impl.dsp.audio.g711.alaw.Encoder());
        codecs.add(new org.mobicents.media.server.impl.dsp.audio.g711.alaw.Decoder());
        codecs.add(new org.mobicents.media.server.impl.dsp.audio.g711.ulaw.Encoder());
        codecs.add(new org.mobicents.media.server.impl.dsp.audio.g711.ulaw.Decoder());

        codecs.add(new org.mobicents.media.server.impl.dsp.audio.speex.Encoder());
        codecs.add(new org.mobicents.media.server.impl.dsp.audio.speex.Decoder());

        codecs.add(new org.mobicents.media.server.impl.dsp.audio.g729.Encoder());
        codecs.add(new org.mobicents.media.server.impl.dsp.audio.g729.Decoder());
    }
    ;
    private HashMap<String, Codec> selectedCodecs = new HashMap();

    public Processor(String resourceName) {
        input = new Input();
        output = new Output();
        this.resourceName = resourceName;
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
     * Draws codec map as String.
     * Used for debug and test perpouses.
     * 
     * @return the map as String.
     */
    public String showCodecMap() {
        StringBuffer s = new StringBuffer();
        Set<String> formats = selectedCodecs.keySet();
        for (String f : formats) {
            s.append(f.toLowerCase());
            s.append("-->");
            s.append(selectedCodecs.get(f).getClass().getCanonicalName());
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * Implements output of the processor.
     */
    private class Output extends AbstractSource {

        protected boolean connected = false;
        protected int failDeliveryCount = 0;

        @Override
        public void connect(MediaSink sink) {
            super.connect(sink);
            configure();
        }

        /**
         * Configures codec map.
         * 
         * Keys of the codec map are input formats which can be transcoded 
         * to one of the output format using codec.
         * 
         * map ={Format[for each on input]--->Codec[output= one of the output formats]}
         */
        private void configure() {
            //analyze each consumer format
            Format[] cunsumerFormats = sink.getFormats();
            ArrayList extFormats = new ArrayList();

            for (Format f : cunsumerFormats) {
                extFormats.add(f);
                for (Codec codec : codecs) {
                    //get all formats supported by the codec 
                    Format[] supported = codec.getSupportedOutputFormats();

                    //if it does not support currently analysed format go to 
                    //the next codec
                    if (!contains(supported, f)) {
                        continue;
                    }

                    //possible that this codec can perform required transcoding 
                    //we are obtaining all formats to wich this codec can transform
                    //the original format f
                    Format[] out = codec.getSupportedInputFormats(f);

                    for (Format of : out) {
                        extFormats.add(of);
                        selectedCodecs.put(of.toString(), codec);
                    }
                }
            }

            input.formats = new Format[extFormats.size()];
            extFormats.toArray(input.formats);
        }

        public void start() {
        }

        public void stop() {
        }

        public Format[] getFormats() {
            return null;
        }

        /**
         * Transmits buffer to the output handler.
         * 
         * @param buffer the buffer to transmit
         */
        protected void transmit(Buffer buffer) {

            //Here we work in ReceiveStream.run method, which runs in local ReceiveStreamTimer
            // Discard packet silently if output handler is not assigned yet

            if (super.sink == null || buffer == null || buffer.getFormat() == null) {
                return;
            }

            // perform transcoding if it is needed.
            // when processor is configured it creates a map of codecs where
            // for each input format stands a required codec if transcoding really
            // required
//         		   if ((sink instanceof SendStream)) {
//             		   System.out.println("f=" + buffer.getFormat() + " codec=" + selectedCodecs.get(buffer.getFormat().toString()));
//              	  System.out.println(showCodecMap());
//           	 }
            if (selectedCodecs.containsKey(buffer.getFormat().toString())) {
                Codec codec = selectedCodecs.get(buffer.getFormat().toString());
                codec.process(buffer);
            }


            // Codec can delay media transition if it has not enouph media
            // to perform its job. 
            // It means that Processor should check FLAGS after codec's 
            // work and discard packet if required
            if (buffer.getFlags() == Buffer.FLAG_DISCARD) {
                return;
            }

            //may be a situation when original format can not be trancoded to 
            //one of the required output. In this case codec map will have no 
            //entry for this format. also codec may has no entry in case of when 
            //transcoding is not required. to differentiate these two cases check
            //if this format is acceptable by the consumer.

            //deliver packet to the consumer
            sink.receive(buffer);

        }
    }

    /**
     * Implements output of the processor.
     */
    private class Input extends AbstractSink {

        protected Format[] formats;
        protected boolean connected = false;

        public boolean isAcceptable(Format format) {
            return contains(formats, format);
        }

        //FIXME: baranowb: why this is synced ?
        public synchronized void receive(Buffer buffer) {
            output.transmit(buffer);
        }

        public Format[] getFormats() {
            return formats;
        }
    }
}
