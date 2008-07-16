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
import org.mobicents.media.Format;

/**
 *
 * @author Oleg Kulikov
 */
public class DecoderTest {

    public final static byte[] ENCODED_SILENCE_NB_Q03_MONO = {30, -99, 102, 0, 0, 103, 57, -56, 16, 51, -100, -28, 8, 25, -50, 114, 4, 12, -25, 57};
    private Decoder decoder = new Decoder();
    
    public DecoderTest() {
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
     * Test of process method, of class Decoder.
     */
    @Test
    public void process() {
        System.out.println("process");
        byte[] res = decoder.process(ENCODED_SILENCE_NB_Q03_MONO);
        assertEquals(res.length, 320);
        for (int i = 0; i < res.length; i++) {
            System.out.println(res[i]);
        }
    }

}