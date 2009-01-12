/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.dsp.audio.speex;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import static org.junit.Assert.*;

/**
 *
 * @author Oleg Kulikov
 */
public class CodecTest {

    private final static byte[] ENCODED_SILENCE_NB_Q03_MONO = {30, -99, 102, 0, 0, 103, 57, -56, 16, 51, -100, -28, 8, 25, -50, 114, 4, 12, -25, 57};
    private final static byte[] silenceOriginal = new byte[320];
    private final static byte[] silence = new byte[320];
    
    public CodecTest() {
    }            

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testCodec() {
        Buffer buffer = new Buffer();
        buffer.setData(silence);
        buffer.setOffset(0);
        buffer.setLength(silence.length);
        
        org.mobicents.media.server.spi.dsp.Codec compressor = new Encoder();
        compressor.process(buffer);
        
        byte[] res = (byte[]) buffer.getData();
        for (int i = 0; i < ENCODED_SILENCE_NB_Q03_MONO.length; i++) {
        	
            if (ENCODED_SILENCE_NB_Q03_MONO[i] != res[i]) {
                fail("mismatch found at " + i);
            }
        }
        org.mobicents.media.server.spi.dsp.Codec decompressor = new Decoder();
        decompressor.process(buffer);
      
        
        res = (byte[]) buffer.getData();
        for (int i = 0; i < silenceOriginal.length; i++) {
        	  	
            if (silenceOriginal[i] != res[i]) {
                fail("mismatch found at " + i);
            }
        }
    }

}