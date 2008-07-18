/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.codec.alaw.test;

import org.mobicents.media.server.impl.jmf.dsp.audio.ulaw.*;
import junit.framework.TestCase;
import org.mobicents.media.Buffer;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;
import org.mobicents.media.server.impl.jmf.dsp.Codec;

/**
 *
 * @author Oleg Kulikov
 */
public class CodecTest extends TestCase {

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
    
    private byte[] src = new byte[512];
    
    public CodecTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        int k = 0;
        for (int i = 0; i < 256; i++) {
            short s = aLawDecompressTable[i];
            src[k++] = (byte)(s);
            src[k++] = (byte)(s >> 8);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    /**
     * Test of process method, of class Decoder.
     */
    public void testCodec() {
        Buffer buffer = new Buffer();
        buffer.setData(src);
        buffer.setOffset(0);
        buffer.setLength(src.length);
        
        Codec compressor = CodecLocator.getCodec(Codec.LINEAR_AUDIO, Codec.PCMA);
        compressor.process(buffer);
        
        Codec decompressor = CodecLocator.getCodec(Codec.PCMA, Codec.LINEAR_AUDIO);
        decompressor.process(buffer);
        
        byte[] res = (byte[]) buffer.getData();
        for (int i = 0; i < src.length; i++) {
            if (src[i] != res[i]) {
                fail("mismatch found at " + i);
            }
        }
    }

}
