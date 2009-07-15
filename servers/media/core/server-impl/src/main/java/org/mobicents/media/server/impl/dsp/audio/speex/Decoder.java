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
package org.mobicents.media.server.impl.dsp.audio.speex;

import java.io.StreamCorruptedException;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.spi.dsp.Codec;
import org.xiph.speex.SpeexDecoder;

/**
 * Implements Speex narrow band, 8kHz decompressor.
 * 
 * @author Amit Bhayani
 * @author Oleg Kulikov
 */
public class Decoder implements Codec {

    private final static int MODE_NB = 0;
    private final static boolean ENHANCED = false;
    private final static int SAMPLE_RATE = 8000;
    private final static int CHANNELS = 1;
    private SpeexDecoder decoder = new SpeexDecoder();

    public Decoder() {
        decoder.init(MODE_NB, SAMPLE_RATE, CHANNELS, ENHANCED);
    }

    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormat().
     */
    public Format getSupportedInputFormat() {
        return Codec.SPEEX;
    }

    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormat().
     */
    public Format getSupportedOutputFormat() {
        return Codec.LINEAR_AUDIO;
    }

    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#process(Buffer).
     */
    public void process(Buffer buffer) {
        int len = process((byte[]) buffer.getData(), buffer.getOffset(), buffer.getLength(), (byte[]) buffer.getData());
        
        buffer.setOffset(0);
        buffer.setFormat(Codec.LINEAR_AUDIO);
        buffer.setLength(len);
    }
    
    /**
     * Perform decompression.
     * 
     * @param media input compressed speech.
     * @return uncompressed speech.
     */
    private int process(byte[] media, int offset, int len, byte[] res) {
        try {
            decoder.processData(media, offset, len);
            int size = decoder.getProcessedDataByteSize();
            decoder.getProcessedData(res, 0);
            return size;
        } catch (StreamCorruptedException e) {
            return 0;
        }
    }
}
