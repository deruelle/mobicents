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
package org.mobicents.media.server.impl.dsp.audio.g711.alaw;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.dsp.SignalingProcessor;

/**
 * Implements G.711 A-law compressor.
 * 
 * @author Oleg Kulikov
 */
public class Encoder implements Codec {

    private final static int cClip = 32635;
    private static byte aLawCompressTable[] = new byte[]{
        1, 1, 2, 2, 3, 3, 3, 3,
        4, 4, 4, 4, 4, 4, 4, 4,
        5, 5, 5, 5, 5, 5, 5, 5,
        5, 5, 5, 5, 5, 5, 5, 5,
        6, 6, 6, 6, 6, 6, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6,
        7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 7, 7, 7, 7, 7, 7
    };

    private byte[] temp = new byte[1200];
    private SignalingProcessor dsp;
    
    public void setProc(SignalingProcessor dsp) {
        this.dsp = dsp;
    }
    
    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormat().
     */
    public Format getSupportedInputFormat() {
        return Codec.LINEAR_AUDIO;
    }
    
    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormats().
     */
    public Format getSupportedOutputFormat() {
        return Codec.PCMA;
    }

    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#process(Buffer).
     */
    public void process(Buffer buffer) {
        int len = process((byte[]) buffer.getData(), buffer.getOffset(), buffer.getLength(), temp);
        System.arraycopy(temp, 0, (byte[])buffer.getData(), 0, len);
        buffer.setOffset(0);
        buffer.setLength(len);
        buffer.setFormat(PCMA);
    }
    
    private int process(byte[] src, int offset, int len, byte[] res) {
        int j = offset;
        int count = len / 2;
        short sample = 0;
        
        for (int i = 0; i < count; i++) {
            sample = (short) (((src[j++] & 0xff) | (src[j++]) << 8));
            res[i] = linearToALawSample(sample);
        }
        return count;
    }
    
    /**
     * Perform compression using A-law.
     * 
     * @param media the input uncompressed media
     * @return the output compressed media.
     */
    private byte[] process(byte[] media) {
        byte[] compressed = new byte[media.length / 2];
        
        int j = 0;
        for (int i = 0; i < compressed.length; i++) {
            short sample = (short) (((media[j++] & 0xff) | (media[j++]) << 8));
            compressed[i] = linearToALawSample(sample);
        }
        return compressed;
    }
    
    /**
     * Compress 16bit value to 8bit value
     * 
     * @param sample 16-bit sample
     * @return compressed 8-bit value.
     */
    private byte linearToALawSample(short sample) {
        int sign;
        int exponent;
        int mantissa;
        int s;
        
        sign = ((~sample) >> 8) & 0x80;
        if (!(sign == 0x80)) {
            sample = (short) -sample;
        }
        if (sample > cClip) {
            sample = cClip;
        }
        if (sample >= 256) {
            exponent = (int) aLawCompressTable[(sample >> 8) & 0x7F];
            mantissa = (sample >> (exponent + 3)) & 0x0F;
            s = (exponent << 4) | mantissa;
        } else {
            s = sample >> 4;
        }
        s ^= (sign ^ 0x55);
        return (byte)s;
    }
}
