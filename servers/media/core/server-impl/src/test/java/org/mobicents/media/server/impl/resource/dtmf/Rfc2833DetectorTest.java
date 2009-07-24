package org.mobicents.media.server.impl.resource.dtmf;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author amit bhayani
 * 
 */
public class Rfc2833DetectorTest {

    private volatile boolean receivedEvent = false;
    Timer timer = null;
    Endpoint endpoint = null;
    Rfc2833GeneratorImpl generator = null;
    Rfc2833DetectorImpl detector = null;
    private Semaphore semaphore;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        receivedEvent = false;
        semaphore = new Semaphore(0);

        timer = new TimerImpl();

        endpoint = new EndpointImpl();
        endpoint.setTimer(timer);

        generator = new Rfc2833GeneratorImpl("Rfc2833DetectorTest", timer);
        generator.setDuration(100); // 100 ms
        generator.setVolume(10);
        generator.setEndpoint(endpoint);

        detector = new Rfc2833DetectorImpl("Rfc2833DetectorTest");

    }

    @After
    public void tearDown() {
    }

    private void checkDigit(String digit, int eventID) throws InterruptedException {
        generator.setDigit(digit);

        DTMFListener listener = new DTMFListener(eventID);
        detector.addListener(listener);
        detector.connect(generator);

        generator.start();
        detector.start();
        semaphore.tryAcquire(120, TimeUnit.MILLISECONDS);
        assertEquals(true, receivedEvent);
    }

    @Test
    public void testDTMF0() throws InterruptedException {
        checkDigit("0", DtmfEvent.DTMF_0);
    }

    @Test
    public void testDTMF1() throws InterruptedException {
        checkDigit("1", DtmfEvent.DTMF_1);
    }

    @Test
    public void testDTMF2() throws InterruptedException {
        checkDigit("2", DtmfEvent.DTMF_2);
    }

    @Test
    public void testDTMF3() throws InterruptedException {
        checkDigit("3", DtmfEvent.DTMF_3);
    }

    @Test
    public void testDTMF4() throws InterruptedException {
        checkDigit("4", DtmfEvent.DTMF_4);
    }

    @Test
    public void testDTMF5() throws InterruptedException {
        checkDigit("5", DtmfEvent.DTMF_5);
    }

    @Test
    public void testDTMF6() throws InterruptedException {
        checkDigit("6", DtmfEvent.DTMF_6);
    }

    @Test
    public void testDTMF7() throws InterruptedException {
        checkDigit("7", DtmfEvent.DTMF_7);
    }

    @Test
    public void testDTMF8() throws InterruptedException {
        checkDigit("8", DtmfEvent.DTMF_8);
    }

    @Test
    public void testDTMF9() throws InterruptedException {
        checkDigit("9", DtmfEvent.DTMF_9);
    }

    @Test
    public void testDTMFA() throws InterruptedException {
        checkDigit("A", DtmfEvent.DTMF_A);
    }

    @Test
    public void testDTMFB() throws InterruptedException {
        checkDigit("B", DtmfEvent.DTMF_B);
    }

    @Test
    public void testDTMFC() throws InterruptedException {
        checkDigit("C", DtmfEvent.DTMF_C);
    }

    @Test
    public void testDTMFD() throws InterruptedException {
        checkDigit("D", DtmfEvent.DTMF_D);
    }

    @Test
    public void testDTMFSTAR() throws InterruptedException {
        checkDigit("*", DtmfEvent.DTMF_STAR);
    }

    @Test
    public void testDTMFHASH() throws InterruptedException {
        checkDigit("#", DtmfEvent.DTMF_HASH);
    }

    private class DTMFListener implements NotificationListener {

        int eventId = 0;

        public DTMFListener(int eventId) {
            this.eventId = eventId;
        }

        public void update(NotifyEvent event) {
            if (event.getEventID() == eventId) {
                receivedEvent = true;
                semaphore.release();
            }
        }
    }
}
