package org.mobicents.media.server.impl.resource.tone;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

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
	private ArrayList<int[]> s;

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
		timer = new TimerImpl();
		gen = new MultiFreqToneGeneratorImpl("Gen", timer);
		List<FrequencyBean> freq = new ArrayList<FrequencyBean>();

		FrequencyBean f1 = new FrequencyBean(1200, 700, 100);
		FrequencyBean f2 = new FrequencyBean(941, 2000, 100);
		FrequencyBean f3 = new FrequencyBean(1800, 2800, 100);
		FrequencyBean f4 = new FrequencyBean(700, 1600, 100);
		FrequencyBean f5 = new FrequencyBean(0, 0, 200);

		freq.add(f1);
		freq.add(f2);
		freq.add(f3);
		freq.add(f4);
		freq.add(f5);

		gen.setFreqBeanList(freq);

		gen.setVolume(0);

		det = new MultiFreqToneDetectorImpl("det");
		det.setFreqBean(f3);
		det.addListener(this);
		s = new ArrayList();

		p = new PlayerImpl("MulFreqtestPlayer");
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of getDigit method, of class InbandGeneratorImpl.
	 */
	@Test
	@SuppressWarnings("static-access")
	public void testDigit0() throws Exception {
		// gen.connect(p);
		gen.connect(det);
		// p.start();

		det.start();
		gen.start();

		Thread.currentThread().sleep(2000);
		gen.stop();
		det.stop();
		p.stop();

		Thread.currentThread().sleep(1000);
		assertTrue(verify(s, new int[] { 1800, 2800 }));
	}

	private boolean verify(ArrayList<int[]> ext, int[] F) {

		if (ext.size() != 1) {
			return false;
		}
		System.out.println("-----------------");
		int[] freqs = ext.get(0);
		for (int i =0; i<freqs.length; i++) {
			System.out.println("Expected Freq "+ F[i] + " Received "+ freqs[i]);
		}

		System.out.println("-----------------");
		boolean r = checkFreq(freqs, F);

		return r;
	}
	
    private boolean checkFreq(int[] ext, int[] F) {
    	 double error = 0;
        if (ext.length < F.length) {
            return false;
        }
        for (int i = 0; i < F.length; i++) {
        	//1.8% variation is ok
        	error = Math.abs(F[i]* 0.018);
        	System.out.println("error margin = "+ error);
            if (Math.abs(ext[i] - F[i]) > error) {
                return false;
            }
        }

        return true;
    }

	public void update(NotifyEvent event) {
		switch (event.getEventID()) {
		case MultiFreqToneEvent.MF_TONE_EVENTID:
			// System.out.print("received spectrum event = ");
			MultiFreqToneEvent evt = (MultiFreqToneEvent) event;
			// for(double d : evt.getSpectra()){
			// System.out.print("freq = "+ d);
			// }

			// somem(evt.getSpectra(), new int[] { 941, 1633 });
			s.add(evt.getFrequencies());
			break;
		}
	}
}
