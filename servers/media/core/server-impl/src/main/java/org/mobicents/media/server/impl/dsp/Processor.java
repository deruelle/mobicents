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

import java.io.IOException;
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
import org.mobicents.media.server.impl.rtp.SendStream;
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
    private HashMap<Format, Codec> selectedCodecs = new HashMap();

    public Processor() {
        input = new Input();
        output = new Output();
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
     * Configures codec map.
     * 
     * Keys of the codec map are input formats which can be transcoded 
     * to one of the output format using codec.
     * 
     * map ={Format[for each on input]--->Codec[output= one of the output formats]}
     */
    private void configure() {
        //analyze each input format
        //NOTE! As well as Processor works as "bridge" between consumer and 
        //source the formats supported by the consumer are holded by the 
        //processor's input instance (to which really is attached source output!)
        //and same for formats supported by source. 
        //1)[source]---(F1)--->(F2)---[consumer]
        //2)[source]---(F1)--->(F2)--{[Input][Ouput]}--(F1)--(F2)-->[consumer]
        for (Format f : output.baseFormats) {
            //if this input format is acceptable by consumer the transcoding is
            //not needed. Leaving this format as is and go the next format object.
            if (input.baseFormats.contains(f)) {
                continue;
            }

            //if we are here that it means that format f is not acceptable by the
            //consumer directly. We will try to find suitable codec and make an 
            //entry in the codec map
            for (Codec codec : codecs) {
                //get all formats supported by the codec 
                Format[] supported = codec.getSupportedInputFormats();

                //if it does not support currently analysed format go to 
                //the next codec
                if (!contains(supported, f)) {
                    continue;
                }

                //possible that this codec can perform required transcoding 
                //we are obtaining all formats to wich this codec can transform
                //the original format f
                Format[] out = codec.getSupportedOutputFormats(f);

                //try to search first common format in two lists: 
                //codec output formats and formats acceptable by consumer.
                //if such common format is found make an entry in the codec map
                for (Format fmt : input.baseFormats) {
                    if (contains(out, fmt)) {
                        selectedCodecs.put(f, codec);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Copy format objects from an array to array list.
     * Utility method.
     * 
     * @param src array to copy from
     * @param dest array list to copy to.
     */
    private void copy(Format[] src, ArrayList<Format> dest) {
        if (src != null) {
            for (int i = 0; i < src.length; i++) {
                dest.add(src[i]);
            }
        }
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
     * Constructs a list of formats to which can be transcoded original list.
     * 
     * @param baseFormats the original list of formats.
     * @return the list of possible transcoded formats.
     */
    protected ArrayList getCodecFormats(ArrayList<Format> baseFormats) {
        ArrayList encFormats = new ArrayList();
        for (Format fmt : baseFormats) {
            for (Codec codec : codecs) {
                if (contains(codec.getSupportedInputFormats(), fmt)) {
                    copy(codec.getSupportedOutputFormats(fmt), encFormats);
                }
            }
        }
        return encFormats;
    }

    /**
     * Draws codec map as String.
     * Used for debug and test perpouses.
     * 
     * @return the map as String.
     */
    public String showCodecMap() {
        StringBuffer s = new StringBuffer();
        Set<Format> formats = selectedCodecs.keySet();
        for (Format f : formats) {
            s.append(f.toString().toLowerCase());
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

        private ArrayList<Format> baseFormats;
        private ArrayList<Format> codedFormats;
        protected boolean connected = false;

        protected ArrayList<Format> getSinkFormats() {
            ArrayList list = new ArrayList();
            if (sink != null && sink.getFormats() != null) {
                Format[] fmts = sink.getFormats();
                for (int i = 0; i < fmts.length; i++) {
                    list.add(fmts[i]);
                }
            }
            return list;
        }

        @Override
        public void connect(MediaSink sink) throws IOException {
            super.connect(sink);
            input.baseFormats = this.getSinkFormats();
            input.codedFormats = getCodecFormats(input.baseFormats);
            connected = true;
            if (input.connected) {
                System.out.println("Configuring " + this + " on connection with " + sink);
                configure();
            }
        }

        public void start() {
        }

        public void stop() {
        }

        public Format[] getFormats() {
            if (baseFormats == null) {
                return null;
            }
            ArrayList<Format> commons = new ArrayList();

            commons.addAll(baseFormats);
            commons.addAll(codedFormats);

            Format[] res = new Format[commons.size()];
            commons.toArray(res);

            return res.length > 0 ? res : null;
        }

        /**
         * Transmits buffer to the output handler.
         * 
         * @param buffer the buffer to transmit
         */
        protected void transmit(Buffer buffer) {
            // Discard packet silently if output handler is not assigned yet
            if (sink == null) {
                return;
            }

            // perform transcoding if it is needed.
            // when processor is configured it creates a map of codecs where
            // for each input format stands a required codec if transcoding really
            // required
            
//            if (sink instanceof SendStream) {
//                System.out.println("Format=" + buffer.getFormat() + ", code=" + selectedCodecs.get(buffer.getFormat()));
//            }
            
            if (selectedCodecs.containsKey(buffer.getFormat())) {
                Codec codec = selectedCodecs.get(buffer.getFormat());
                codec.process(buffer);
            } else {
                //try to find codec
                for (Codec codec : codecs) {
                    //get all formats supported by the codec 
                    Format[] supported = codec.getSupportedInputFormats();

                    //if it does not support currently analysed format go to 
                    //the next codec
                    if (!contains(supported, buffer.getFormat())) {
                        continue;
                    }

                    //possible that this codec can perform required transcoding 
                    //we are obtaining all formats to wich this codec can transform
                    //the original format f
                    Format[] out = codec.getSupportedOutputFormats(buffer.getFormat());

                    //try to search first common format in two lists: 
                    //codec output formats and formats acceptable by consumer.
                    //if such common format is found make an entry in the codec map
                    for (Format fmt : input.baseFormats) {
                        if (contains(out, fmt)) {
                            selectedCodecs.put(buffer.getFormat(), codec);
                            codec.process(buffer);
                            break;
                        }
                    }
                }
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
            if (!sink.isAcceptable(buffer.getFormat())) {
                System.out.println("xxxxxxxxxxxxxx Discard");
                if (logger.isDebugEnabled()) {
                    logger.debug("xxx Discard " + buffer + ", not acceptable");
                }
                return;
            }

            //deliver packet to the consumer
            if (logger.isDebugEnabled()) {
                logger.debug("<-- Pish " + buffer + " to " + sink);
            }
            sink.receive(buffer);
        }
    }

    /**
     * Implements output of the processor.
     */
    private class Input extends AbstractSink {

        private ArrayList<Format> baseFormats;
        private ArrayList<Format> codedFormats;
        protected boolean connected = false;

        protected ArrayList<Format> getStreamFormats() {
            ArrayList list = new ArrayList();
            if (mediaStream != null && mediaStream.getFormats() != null) {
                Format[] fmts = mediaStream.getFormats();
                for (int i = 0; i < fmts.length; i++) {
                    list.add(fmts[i]);
                }
            }
            return list;
        }

        @Override
        public void connect(MediaSource stream) throws IOException {
            super.connect(stream);
            output.baseFormats = this.getStreamFormats();
            output.codedFormats = getCodecFormats(output.baseFormats);
            connected = true;
            if (output.connected) {
                System.out.println("Configuring " + this + " on connection with " + stream);
                configure();
            }
        }

        public boolean isAcceptable(Format format) {
            return true; //baseFormats.contains(format) || codedFormats.contains(format);
        }

        public void receive(Buffer buffer) {
            output.transmit(buffer);
        }

        public Format[] getFormats() {
            if (baseFormats != null) {
                ArrayList<Format> commons = new ArrayList();

                commons.addAll(baseFormats);
                commons.addAll(codedFormats);

                Format[] res = new Format[commons.size()];
                commons.toArray(res);

                return res.length > 0 ? res : null;
            } else {
                return null;
            }
        }
    }
}
