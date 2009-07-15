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

/**
 * Implements G.711 A-Law decompressor.
 * 
 * @author Oleg Kulikov
 */
public class Decoder implements Codec {
    /** decompress table constants */
    private static short aLawDecompressTable[] = new short[]{
        -5504, -5248, -6016, -5760, -4480, -4224, -4992, -4736,
        -7552, -7296, -8064, -7808, -6528, -6272, -7040, -6784,
        -2752, -2624, -3008, -2880, -2240, -2112, -2496, -2368,
        -3776, -3648, -4032, -3904, -3264, -3136, -3520, -3392,
        -22016, -20992, -24064, -23040, -17920, -16896, -19968, -18944,
        -30208, -29184, -32256, -31232, -26112, -25088, -28160, -27136,
        -11008, -10496, -12032, -11520, -8960, -8448, -9984, -9472,
        -15104, -14592, -16128, -15616, -13056, -12544, -14080, -13568,
        -344, -328, -376, -360, -280, -264, -312, -296,
        -472, -456, -504, -488, -408, -392, -440, -424,
        -88, -72, -120, -104, -24, -8, -56, -40,
        -216, -200, -248, -232, -152, -136, -184, -168,
        -1376, -1312, -1504, -1440, -1120, -1056, -1248, -1184,
        -1888, -1824, -2016, -1952, -1632, -1568, -1760, -1696,
        -688, -656, -752, -720, -560, -528, -624, -592,
        -944, -912, -1008, -976, -816, -784, -880, -848,
        5504, 5248, 6016, 5760, 4480, 4224, 4992, 4736,
        7552, 7296, 8064, 7808, 6528, 6272, 7040, 6784,
        2752, 2624, 3008, 2880, 2240, 2112, 2496, 2368,
        3776, 3648, 4032, 3904, 3264, 3136, 3520, 3392,
        22016, 20992, 24064, 23040, 17920, 16896, 19968, 18944,
        30208, 29184, 32256, 31232, 26112, 25088, 28160, 27136,
        11008, 10496, 12032, 11520, 8960, 8448, 9984, 9472,
        15104, 14592, 16128, 15616, 13056, 12544, 14080, 13568,
        344, 328, 376, 360, 280, 264, 312, 296,
        472, 456, 504, 488, 408, 392, 440, 424,
        88, 72, 120, 104, 24, 8, 56, 40,
        216, 200, 248, 232, 152, 136, 184, 168,
        1376, 1312, 1504, 1440, 1120, 1056, 1248, 1184,
        1888, 1824, 2016, 1952, 1632, 1568, 1760, 1696,
        688, 656, 752, 720, 560, 528, 624, 592,
        944, 912, 1008, 976, 816, 784, 880, 848
    };
    private byte[] temp = new byte[1200];

    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormat().
     */
    public Format getSupportedInputFormat() {
        return Codec.PCMA;
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
        int len = process((byte[]) buffer.getData(), buffer.getOffset(), buffer.getLength(), temp);
        System.arraycopy(temp, 0, (byte[])buffer.getData(), 0, len);
        buffer.setOffset(0);
        buffer.setLength(len);
        buffer.setFormat(Codec.LINEAR_AUDIO);
    }
    
    /**
     * Perform decompression using A-law.
     * 
     * @param media the input compressed media
     * @return the output decompressed media.
     */
    private int process(byte[] src, int offset, int len, byte[] res) {
        int j = 0;
        for (int i = 0; i < len; i++) {
            short s = aLawDecompressTable[src[i + offset] & 0xff];
            res[j++] = (byte) s;
            res[j++] = (byte) (s >> 8);
        }
        return 2 * len;
    }    

}
