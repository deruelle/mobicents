/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.ann;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.format.AudioFormat;

/**
 *
 * @author Oleg Kulikov
 */
public class AudioPlayerTest {

    public AudioPlayerTest() {
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
     * Test of getFormats method, of class AudioPlayer.
     */
    @Test
    public void testGetFormats() {
        AudioPlayer p = new AudioPlayer();
        AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR,8000, 16, 1,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
        assertEquals(1, p.getFormats().length);
        assertEquals(LINEAR, p.getFormats()[0]);
    }

}