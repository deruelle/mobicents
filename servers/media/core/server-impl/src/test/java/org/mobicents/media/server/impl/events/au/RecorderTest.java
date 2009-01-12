/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.au;
import org.mobicents.media.Format;
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
	
	   private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
	    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
	    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
	    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
	            AudioFormat.LINEAR, 8000, 16, 1,
	            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
	    private final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, 8, 1);

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
        Recorder r = new Recorder("", "");
        AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR,8000, 16, 1,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
        assertEquals(4, r.getFormats().length);
        
        Format[] formats = r.getFormats();
        
        assertEquals(true, contains(formats, PCMA));
        assertEquals(true, contains(formats, PCMU));              
        assertEquals(true, contains(formats, LINEAR));       
        assertEquals(true, contains(formats, GSM));
    }
    
    private boolean contains(Format[] fmts, Format fmt) {
        for (int i = 0; i < fmts.length; i++) {
            if (fmts[i].matches(fmt)) {
                return true;
            }
        }
        return false;
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