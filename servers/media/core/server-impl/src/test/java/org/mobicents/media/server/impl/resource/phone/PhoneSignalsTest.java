/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.phone;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
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
public class PhoneSignalsTest implements NotificationListener {

    private Timer timer;
    private PhoneSignalGenerator gen;
    private PhoneSignalDetector det;
    
    private Semaphore semaphore = new Semaphore(0);
    private boolean evtDetected;
    
    public PhoneSignalsTest() {
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
        
        short A = Short.MAX_VALUE /2;
        
        int[] F = new int[]{100, 200};
        int[] T = new int[]{1,1};
        
        gen = new PhoneSignalGenerator("phone.gen", timer);
        gen.setAmplitude((short)320);
        gen.setFrequency(F);
        gen.setPeriods(T);
        
        det = new PhoneSignalDetector("phone.detector");
        det.setFrequency(F);
        det.setPeriods(T);
        det.setVolume(-30);
        det.setEventID(50);
        det.addListener(this);
        gen.connect(det);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setPeriods method, of class PhoneSignalDetector.
     */
    @Test
    public void testSetPeriods() throws Exception {
        det.start();
        gen.start();
        
        semaphore.tryAcquire(10, TimeUnit.SECONDS);
        
        assertTrue("Event not detected", evtDetected);
    }


    public void update(NotifyEvent event) {
        if (event.getEventID() == 50) {
            evtDetected = true;
            semaphore.release();
        }
    }

}