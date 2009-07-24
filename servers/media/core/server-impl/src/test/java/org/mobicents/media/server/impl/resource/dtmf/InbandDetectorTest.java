package org.mobicents.media.server.impl.resource.dtmf;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author amit bhayani
 * 
 * 
 */
public class InbandDetectorTest {

    private volatile boolean receivedEvent = false;
    private volatile int count;
    
    Timer timer = null;
    private Semaphore semaphore;
    private InbandGeneratorImpl generator = null;
    private InbandDetectorImpl detector = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        receivedEvent = false;
        count = 0;
        
        semaphore = new Semaphore(0);
        timer = new TimerImpl();

        generator = new InbandGeneratorImpl("InbandDetectorTest", timer);
        detector = new InbandDetectorImpl("InbandDetectorTest");
    }

    @After
    public void tearDown() {
    }

    /**
     * Performs actual DTMF test.
     * 
     * @param tone the tone to generate.
     * @param duration duration of the tone to be generated.
     * @param eventID the expected eventID.
     */
    private void checkTone(String tone, int duration, int eventID) throws InterruptedException {
        generator.setDigit(tone);
        generator.setDuration(duration); // 100 ms
        generator.setVolume(0);
        
        DTMFListener listener = new DTMFListener(eventID);
        detector.addListener(listener);
        detector.connect(generator);

        generator.start();
        detector.start();

        semaphore.tryAcquire(500, TimeUnit.MILLISECONDS);

        generator.stop();
        detector.stop();
        assertEquals(true, receivedEvent);
        assertEquals(1, count);
    }

    @Test
    public void testDTMF0() throws InterruptedException {
        checkTone("0", 100, DtmfEvent.DTMF_0);
    }

    
    @Test
    public void testDTMF1() throws InterruptedException {
        checkTone("1", 100, DtmfEvent.DTMF_1);
    }

    @Test
    public void testDTMF2() throws InterruptedException {
        checkTone("2", 100, DtmfEvent.DTMF_2);
    }

    @Test
    public void testDTMF3() throws InterruptedException {
        checkTone("3", 100, DtmfEvent.DTMF_3);
    }

    @Test
    public void testDTMF4() throws InterruptedException {
        checkTone("4", 100, DtmfEvent.DTMF_4);
    }

    @Test
    public void testDTMF5() throws InterruptedException {
        checkTone("5", 100, DtmfEvent.DTMF_5);
    }

    @Test
    public void testDTMF6() throws InterruptedException {
        checkTone("6", 100, DtmfEvent.DTMF_6);
    }

    @Test
    public void testDTMF7() throws InterruptedException {
        checkTone("7", 100, DtmfEvent.DTMF_7);
    }

    @Test
    public void testDTMF8() throws InterruptedException {
        checkTone("8", 100, DtmfEvent.DTMF_8);
    }

    @Test
    public void testDTMF9() throws InterruptedException {
        checkTone("9", 100, DtmfEvent.DTMF_9);
    }

    @Test
    public void testDTMFA() throws InterruptedException {
        checkTone("A", 100, DtmfEvent.DTMF_A);
    }

    @Test
    public void testDTMFB() throws InterruptedException {
        checkTone("B", 100, DtmfEvent.DTMF_B);
    }

    @Test
    public void testDTMFC() throws InterruptedException {
        checkTone("C", 100, DtmfEvent.DTMF_C);
    }

    @Test
    public void testDTMFD() throws InterruptedException {
        checkTone("D", 100, DtmfEvent.DTMF_D);
    }

    @Test
    public void testDTMFSTAR() throws InterruptedException {
        checkTone("*", 100, DtmfEvent.DTMF_STAR);
    }

    @Test
    public void testDTMFHASH() throws InterruptedException {
        checkTone("#", 100, DtmfEvent.DTMF_HASH);
    }

    private class DTMFListener implements NotificationListener {

        int eventId = 0;

        public DTMFListener(int eventId) {
            this.eventId = eventId;
        }

        public void update(NotifyEvent event) {
            if (event.getEventID() == eventId) {
                receivedEvent = true;
                count++;
                semaphore.release();
            }
        }
    }
}
