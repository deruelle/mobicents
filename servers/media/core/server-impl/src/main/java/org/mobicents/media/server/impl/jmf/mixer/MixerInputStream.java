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
package org.mobicents.media.server.impl.jmf.mixer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;

/**
 *
 * @author Oleg Kulikov
 */
public class MixerInputStream implements BufferTransferHandler {

    protected PushBufferStream stream;
    private Codec codec;
    private List<Buffer> buffers = Collections.synchronizedList(new ArrayList());
    private int jitter;
    private long duration;
    private AudioMixer mixer;
    
    private Logger logger = Logger.getLogger(MixerInputStream.class);
    
    public MixerInputStream(AudioMixer mixer, PushBufferStream stream, int jitter)
            throws UnsupportedFormatException {
        this.mixer = mixer;
        this.jitter = jitter;
        this.stream = stream;

        AudioFormat fmt = (AudioFormat) stream.getFormat();
        if (!fmt.matches(Codec.LINEAR_AUDIO)) {
            codec = CodecLocator.getCodec(fmt, Codec.LINEAR_AUDIO);
            if (codec == null) {
                throw new UnsupportedFormatException(fmt);
            }
        }

        stream.setTransferHandler(this);
    }

    public boolean isReady() {
        return !buffers.isEmpty();
    }

    /**
     * Gets the current jitter.
     * 
     * @return jitter value in milliseconds.
     */
    public long getJitter() {
        return duration;
    }

    /**
     * Provides transcoding of the input media buffer if necessary.
     * 
     * @param buffer the media buffer to transcode.
     */
    private void transcode(Buffer buffer) {
        if (codec != null) {
            codec.process(buffer);
        }
    }
    
    /**
     * This method is called by stream when new media is available.
     * 
     * @param stream the stream orinating the call.
     */
    public void transferData(PushBufferStream stream) {
        Buffer buffer = new Buffer();
        try {
            stream.read(buffer);
        } catch (IOException e) {
        }

        if (logger.isDebugEnabled()) {
            logger.debug(this + " <-- receive " + buffer.getLength() + " bytes packet, fmt=" + 
                    buffer.getFormat() + ", timestamp = " + buffer.getTimeStamp() + ", src=" + stream + ", dst=");
        }
        
        if (duration + buffer.getDuration() > jitter) {
            //silently discard packet
            return;
        }

        transcode(buffer);
        
        duration += buffer.getDuration();
        buffers.add(buffer);
    }

    /**
     * Reads media buffer from this stream with specified duration.
     * 
     * @param duration the duration of the requested buffer in milliseconds.
     * @return buffer which contains duration ms media for 8000Hz, 16bit, linear audio.
     */
    public byte[] read(int duration) {
        int size = 16 * duration;
        byte[] media = new byte[size];

        int count = 0;
        while (count < size && !buffers.isEmpty()) {
            Buffer buff = buffers.get(0);
            
            if (buff.isEOM() || buff.getData() == null) {
                buffers.remove(buff);
                this.duration -= buff.getDuration();
                break;
            }
            
            int len = Math.min(size - count, buff.getLength() - buff.getOffset());
            System.arraycopy(buff.getData(), buff.getOffset(), media, count, len);

            count += len;
            buff.setOffset(buff.getOffset() + len);

            if (buff.getOffset() == buff.getLength()) {
                buffers.remove(buff);
                this.duration -= buff.getDuration();
            }
        }

        return media;
    }
    
    @Override
    public String toString() {
        return mixer.toString();
    }
}
