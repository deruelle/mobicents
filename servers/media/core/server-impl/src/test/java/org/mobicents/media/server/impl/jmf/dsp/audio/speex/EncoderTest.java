/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.jmf.dsp.audio.speex;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Oleg Kulikov
 */
public class EncoderTest {

    public final static byte[] ENCODED_SILENCE_NB_Q03_MONO = {30, -99, 102, 0, 0, 103, 57, -56, 16, 51, -100, -28, 8, 25, -50, 114, 4, 12, -25, 57};
    
    private Encoder encoder = new Encoder();
    private byte[] silence = new byte[320];

    public EncoderTest() {
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

    /**
     * Test of process method, of class Encoder.
     */
    @Test
    public void process() {
        System.out.println("process");
        byte[] res = encoder.process(silence);
    
        for (int i = 0; i < res.length; i++) {
            if (res[i] != ENCODED_SILENCE_NB_Q03_MONO[i]) {
                fail("Missmatch on byte " + i);
            }
        }
    }

}