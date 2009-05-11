package org.mobicents.media.server.impl.events.dtmf;

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
import org.mobicents.media.server.impl.resource.dtmf.Rfc2833DetectorImpl;
import org.mobicents.media.server.impl.resource.dtmf.Rfc2833GeneratorImpl;
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

		generator = new Rfc2833GeneratorImpl("Rfc2833DetectorTest");
		generator.setDuration(100); // 100 ms
		generator.setVolume(10);
		generator.setEndpoint(endpoint);

		detector = new Rfc2833DetectorImpl("Rfc2833DetectorTest");

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testDTMF0() throws InterruptedException {

		generator.setDigit("0");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_0);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(120, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF1() throws InterruptedException {

		generator.setDigit("1");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_1);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF2() throws InterruptedException {

		generator.setDigit("2");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_2);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF3() throws InterruptedException {

		generator.setDigit("3");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_3);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF4() throws InterruptedException {

		generator.setDigit("4");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_4);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF5() throws InterruptedException {

		generator.setDigit("5");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_5);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF6() throws InterruptedException {

		generator.setDigit("6");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_6);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF7() throws InterruptedException {

		generator.setDigit("7");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_7);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF8() throws InterruptedException {

		generator.setDigit("8");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_8);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMF9() throws InterruptedException {

		generator.setDigit("9");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_9);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMFA() throws InterruptedException {

		generator.setDigit("A");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_A);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMFB() throws InterruptedException {

		generator.setDigit("B");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_B);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMFC() throws InterruptedException {

		generator.setDigit("C");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_C);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}

	@Test
	public void testDTMFD() throws InterruptedException {

		generator.setDigit("D");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_D);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}
	
	@Test
	public void testDTMFSTAR() throws InterruptedException {

		generator.setDigit("*");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_STAR);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
		assertEquals(true, receivedEvent);

	}
	
	@Test
	public void testDTMFHASH() throws InterruptedException {

		generator.setDigit("#");

		DTMFListener listener = new DTMFListener(DtmfEvent.DTMF_HASH);
		detector.addListener(listener);
		detector.connect(generator);

		generator.start();

		semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
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
