package org.mobicents.media.server.impl.resource.tone;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.audio.soundcard.PlayerImpl;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.resource.FrequencyBean;

/**
 * 
 * @author amit.bhayani
 * 
 */
public class MultiFrequencyToneGeneratorImplTest implements NotificationListener {

	private final static int FREQ_ERROR = 5;
	private int MAX_ERRORS = 1;

	private Timer timer;
	private MultiFreqToneGeneratorImpl gen;
	private MultiFreqToneDetectorImpl det;
	private Semaphore semaphore;

	private boolean eventDetcted = false;

	PlayerImpl p;

	public MultiFrequencyToneGeneratorImplTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		semaphore = new Semaphore(0);
		eventDetcted = false;

		timer = new TimerImpl();
                timer.start();
                
		gen = new MultiFreqToneGeneratorImpl("Gen", timer);
		List<FrequencyBean> freq = new ArrayList<FrequencyBean>();

		FrequencyBean f1 = new FrequencyBean(1200, 700, 100);
		FrequencyBean f2 = new FrequencyBean(941, 2000, 100);
		FrequencyBean f3 = new FrequencyBean(1800, 2800, 100);
		FrequencyBean f4 = new FrequencyBean(700, 1600, 100);
		FrequencyBean f5 = new FrequencyBean(0, 0, 100);

		freq.add(f1);
		freq.add(f2);
		freq.add(f3);
		freq.add(f4);
		freq.add(f5);

		gen.setFreqBeanList(freq);

		det = new MultiFreqToneDetectorImpl("det");
		det.setFreqBean(f3);
		det.setVolume(-30);
		det.addListener(this);

		p = new PlayerImpl("MulFreqtestPlayer");
	}

	@After
	public void tearDown() {
            timer.stop();
	}

	/**
	 * Test of getDigit method, of class InbandGeneratorImpl.
	 */
	@Test
	@SuppressWarnings("static-access")
	public void testDigit0() throws Exception {
		//gen.connect(p);
		gen.connect(det);
		//p.start();

		det.start();
		gen.start();

		semaphore.tryAcquire(2, TimeUnit.SECONDS);

		assertTrue("MultiFreqToneEvent detected ", eventDetcted);
		gen.stop();
		det.stop();
		p.stop();

		semaphore.tryAcquire(1, TimeUnit.SECONDS);

	}

	public void update(NotifyEvent event) {
		switch (event.getEventID()) {
		case MultiFreqToneEvent.MF_TONE_EVENTID:

			MultiFreqToneEvent evt = (MultiFreqToneEvent) event;
			System.out.print("received MultiFreqToneEvent event = " + evt);
			eventDetcted = true;
			semaphore.release();

			break;
		}
	}
}
