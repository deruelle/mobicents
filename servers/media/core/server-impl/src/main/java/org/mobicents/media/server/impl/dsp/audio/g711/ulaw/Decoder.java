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
package org.mobicents.media.server.impl.dsp.audio.g711.ulaw;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 *
 * @author Oleg Kulikov
 */
public class Decoder implements Codec {

    private final static int cBias = 0x84;
    private int QUANT_MASK = 0xf;
    private final static int SEG_SHIFT = 4;
    private final static int SEG_MASK = 0x70;
    private final static int SIGN_BIT = 0x80;
    
    private static short muLawDecompressTable[] = new short[]{
        -32124, -31100, -30076, -29052, -28028, -27004, -25980, -24956,
        -23932, -22908, -21884, -20860, -19836, -18812, -17788, -16764,
        -15996, -15484, -14972, -14460, -13948, -13436, -12924, -12412,
        -11900, -11388, -10876, -10364, -9852, -9340, -8828, -8316,
        -7932, -7676, -7420, -7164, -6908, -6652, -6396, -6140,
        -5884, -5628, -5372, -5116, -4860, -4604, -4348, -4092,
        -3900, -3772, -3644, -3516, -3388, -3260, -3132, -3004,
        -2876, -2748, -2620, -2492, -2364, -2236, -2108, -1980,
        -1884, -1820, -1756, -1692, -1628, -1564, -1500, -1436,
        -1372, -1308, -1244, -1180, -1116, -1052, -988, -924,
        -876, -844, -812, -780, -748, -716, -684, -652,
        -620, -588, -556, -524, -492, -460, -428, -396,
        -372, -356, -340, -324, -308, -292, -276, -260,
        -244, -228, -212, -196, -180, -164, -148, -132,
        -120, -112, -104, -96, -88, -80, -72, -64,
        -56, -48, -40, -32, -24, -16, -8, 0,
        32124, 31100, 30076, 29052, 28028, 27004, 25980, 24956,
        23932, 22908, 21884, 20860, 19836, 18812, 17788, 16764,
        15996, 15484, 14972, 14460, 13948, 13436, 12924, 12412,
        11900, 11388, 10876, 10364, 9852, 9340, 8828, 8316,
        7932, 7676, 7420, 7164, 6908, 6652, 6396, 6140,
        5884, 5628, 5372, 5116, 4860, 4604, 4348, 4092,
        3900, 3772, 3644, 3516, 3388, 3260, 3132, 3004,
        2876, 2748, 2620, 2492, 2364, 2236, 2108, 1980,
        1884, 1820, 1756, 1692, 1628, 1564, 1500, 1436,
        1372, 1308, 1244, 1180, 1116, 1052, 988, 924,
        876, 844, 812, 780, 748, 716, 684, 652,
        620, 588, 556, 524, 492, 460, 428, 396,
        372, 356, 340, 324, 308, 292, 276, 260,
        244, 228, 212, 196, 180, 164, 148, 132,
        120, 112, 104, 96, 88, 80, 72, 64,
        56, 48, 40, 32, 24, 16, 8, 0
    };

    private byte[] temp = new byte[8192];
    
    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormat().
     */
    public Format getSupportedInputFormat() {
        return Codec.PCMU;
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
        byte[] data = (byte[]) buffer.getData();
        
        int len = process(data, buffer.getOffset(), buffer.getLength(), temp);
        System.arraycopy(temp, 0, (byte[])buffer.getData(), 0, len);
        
        buffer.setOffset(0);
        buffer.setFormat(Codec.LINEAR_AUDIO);
        buffer.setLength(len);
    }
    
    /**
     * Perform decopression
     * 
     * @param media the compressed media.
     * @param uncompressed media.
     */
    private int process(byte[] media, int offset, int len, byte[] res) {
        int j = 0;
        for (int i = 0; i < len; i++) {
            short s = muLawDecompressTable[media[i + offset] & 0xff];
            res[j++] = (byte) s;
            res[j++] = (byte) (s >> 8);
        }
        return 2*len;
    }

    /*
     * ulaw2linear() - Convert a u-law value to 16-bit linear PCM
     *
     * First, a biased linear code is derived from the code word. An unbiased
     * output can then be obtained by subtracting 33 from the biased code.
     *
     * Note that this function expects to be passed the complement of the
     * original code word. This is in keeping with ISDN conventions.
     */
    private short ulaw2linear(byte u_val) {
        int t;

        /* Complement to obtain normal u-law value. */
        u_val = (byte)~u_val;

        /*
         * Extract and bias the quantization bits. Then
         * shift up by the segment number and subtract out the bias.
         */
        t = ((u_val & QUANT_MASK) << 3) + cBias;
        t <<= (u_val & SEG_MASK) >> SEG_SHIFT;

        boolean s = (u_val & SIGN_BIT) == SIGN_BIT;
        return (short)(s ? (cBias - t) : (t - cBias));
    }
}
