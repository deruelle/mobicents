package org.mobicents.media.server.impl.resource.dtmf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.dtmf.InbandDetectorImpl;
import org.mobicents.media.server.impl.resource.dtmf.InbandGeneratorImpl;
import org.mobicents.media.server.spi.Endpoint;
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

	Logger logger = Logger.getLogger(InbandDetectorTest.class);

	private volatile boolean receivedEvent = false;
	Timer timer = null;
	Endpoint endpoint = null;
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

		semaphore = new Semaphore(0);

		timer = new TimerImpl();

		endpoint = new EndpointImpl();
		endpoint.setTimer(timer);

		generator = new InbandGeneratorImpl("InbandDetectorTest");
		generator.setEndpoint(endpoint);
		generator.init();

		detector = new InbandDetectorImpl("InbandDetectorTest");
		detector.init();

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testInitializeException() {
		InbandDetectorImpl detector1 = new InbandDetectorImpl("InbandDetectorTest");
		try {
			detector1.connect(generator);
			fail("IllegalStateException not thrown");
		} catch (IllegalStateException e) {
			logger.debug("Expected exception ", e);
		}
	}

	@Test
	public void testDTMF0() throws InterruptedException {

		generator.setDigit("0");
		generator.setDuration(80); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_0);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(500, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	/**
	 * This test will also fail as Detector duration is 120ms while Generator
	 * duration is just 100ms. Hence all the data sent by Generator is still not
	 * sufficient by Detector to detect DTMF
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testDTMF0Fail1() throws InterruptedException {

		generator.setDigit("0");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_0);

		// Duration changed at run-time. The InbandDetector needs to be
		// initialized again
		detector.setDuration(120);
		detector.init();

		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(500, TimeUnit.MILLISECONDS);
		assertEquals(false, receivedEvent);

	}

	@Test
	public void testDTMF1() throws InterruptedException {

		generator.setDigit("1");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_1);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF2() throws InterruptedException {

		generator.setDigit("2");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_2);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF3() throws InterruptedException {

		generator.setDigit("3");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_3);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF4() throws InterruptedException {

		generator.setDigit("4");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_4);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF5() throws InterruptedException {

		generator.setDigit("5");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_5);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF6() throws InterruptedException {

		generator.setDigit("6");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_6);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF7() throws InterruptedException {

		generator.setDigit("7");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_7);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF8() throws InterruptedException {

		generator.setDigit("8");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_8);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF9() throws InterruptedException {

		generator.setDigit("9");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_9);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMFA() throws InterruptedException {

		generator.setDigit("A");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_A);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMFB() throws InterruptedException {

		generator.setDigit("B");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_B);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMFC() throws InterruptedException {

		generator.setDigit("C");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_C);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMFD() throws InterruptedException {

		generator.setDigit("D");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_D);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMFSTAR() throws InterruptedException {

		generator.setDigit("*");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_STAR);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMFHASH() throws InterruptedException {

		generator.setDigit("#");
		generator.setDuration(100); // 100 ms

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_HASH);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

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
