/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.audio;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Format;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;

/**
 *
 * @author kulikov
 */
public class AudioPlayerTest {

    private final static Format[] formats = new Format[]{
        AVProfile.L16_MONO,
        AVProfile.L16_STEREO,
        AVProfile.PCMA,
        AVProfile.PCMU,
        AVProfile.SPEEX,
        AVProfile.GSM
    };
    private AudioPlayer player;
    
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
        TimerImpl timer = new TimerImpl();
        EndpointImpl endpoint = new EndpointImpl();
        endpoint.setTimer(timer);
        player = new AudioPlayer(endpoint, "");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSupportedFormats() {
        Format[] supported = player.getFormats();
        assertEquals(formats.length, supported.length);
        for (int i = 0; i < supported.length; i++) {
            boolean found = false;
            for (int j = 0; j < formats.length; j++) {
                if (supported[i].equals(formats[j])) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("Not found " + supported[i]);
            }
        }
    }
}