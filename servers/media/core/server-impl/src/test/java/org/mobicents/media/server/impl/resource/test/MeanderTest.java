/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author kulikov
 */
public class MeanderTest implements NotificationListener {

    public final static int TEST_DURATION = 10;
    
    public final static short A = 100;
    public final static double T = 0.1;
    
    private Timer timer;
    
    private MeanderGenerator gen;
    private MeanderDetector det;
    
    private int outOfSeq;
    private int evtCount;
    private boolean fmtMissmatch = false;
    
    public MeanderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        timer = new TimerImpl();
        
        gen = new MeanderGenerator("test-gen", timer);
        gen.setAmplitude(A);
        gen.setPeriod(T);
        
        det = new MeanderDetector("test-det");
        det.setAmplitude(A);
        det.setPeriod(T);
        det.addListener(this); 
        
        timer.start();
    }

    @After
    public void tearDown() {
        timer.stop();
    }

    /**
     * Test of isAcceptable method, of class MeanderDetector.
     */
    @Test
    @SuppressWarnings("static-access")
    public void testGenerationDetection() throws Exception {
        gen.connect(det);
        det.start();
        gen.start();
        
        Thread.currentThread().sleep(TEST_DURATION * 1000);
        assertEquals(0, outOfSeq); 
        
        int count = (int)(TEST_DURATION /T/2);
        int diff = Math.abs(count - evtCount);
        
        assertTrue("Signal not detected", diff < 2);
        assertFalse("Format missmatch detected", this.fmtMissmatch);
    }

    public void update(NotifyEvent event) {
        if (event.getEventID() == MeanderEvent.EVENT_MEANDER) {
            evtCount++;
        } else if (event.getEventID() == MeanderEvent.EVENT_OUT_OF_SEQUENCE){
            outOfSeq++;
        } else if (event.getEventID() == MeanderEvent.EVENT_FORMAT_MISSMATCH) {
            fmtMissmatch = true;
        }
    }

}