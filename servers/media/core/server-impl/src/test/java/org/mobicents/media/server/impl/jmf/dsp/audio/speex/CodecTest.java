/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.jmf.dsp.audio.speex;

import junit.framework.TestCase;
import org.mobicents.media.Buffer;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;

/**
 *
 * @author Oleg Kulikov
 */
public class CodecTest extends TestCase {

    private final static byte[] ENCODED_SILENCE_NB_Q03_MONO = {30, -99, 102, 0, 0, 103, 57, -56, 16, 51, -100, -28, 8, 25, -50, 114, 4, 12, -25, 57};
    private final static byte[] silence = new byte[320];
    public CodecTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
        buffer.setData(silence);
        buffer.setOffset(0);
        buffer.setLength(silence.length);
        
        Codec compressor = CodecLocator.getCodec(Codec.LINEAR_AUDIO, Codec.SPEEX);
        compressor.process(buffer);
        
        byte[] res = (byte[]) buffer.getData();
        for (int i = 0; i < ENCODED_SILENCE_NB_Q03_MONO.length; i++) {
            if (ENCODED_SILENCE_NB_Q03_MONO[i] != res[i]) {
                fail("mismatch found at " + i);
            }
        }
        Codec decompressor = CodecLocator.getCodec(Codec.SPEEX, Codec.LINEAR_AUDIO);
        decompressor.process(buffer);
        
        res = (byte[]) buffer.getData();
        for (int i = 0; i < silence.length; i++) {
            if (silence[i] != res[i]) {
                fail("mismatch found at " + i);
            }
        }
    }
    
}
