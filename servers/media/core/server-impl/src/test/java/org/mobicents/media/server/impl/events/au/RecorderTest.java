/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.au;
import org.mobicents.media.server.impl.events.audio.Recorder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.format.AudioFormat;
import static org.junit.Assert.*;

/**
 *
 * @author Oleg Kulikov
 */
public class RecorderTest {

    private final static int F = 50;
    private final static int TEST_DURATION = 10000;
    private final static int ERROR = 5;
    
    public RecorderTest() {
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
    public void testGetFormats() {
        Recorder r = new Recorder("");
        AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR,8000, 16, 1,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
        assertEquals(1, r.getFormats().length);
        assertEquals(LINEAR, r.getFormats()[0]);
    }
    
    /**
     * Test of start method, of class Recorder.
     */
    @Test
    @SuppressWarnings("static-access")
    public void testRecorder() {
/*        Recorder recorder = new Recorder("wav");
        SineGenerator g = new SineGenerator(F);

        recorder.connect(g);
        g.start();
        
        recorder.start("recorder-test.wav");
        try {
            Thread.currentThread().sleep(TEST_DURATION);
        } catch (InterruptedException e) {
            fail("Interrupted");
            return;
        }
        g.stop();
        recorder.stop();
 */ 
    }

}