/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.test.SineGenerator;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 * 
 * @author kulikov
 */
public class EndpointTransmittorTest {

	private final static int F = 50;
	private Timer timer;

	private EndpointTransmittor filler;
	private SineGenerator gen;
	private ExtSineGenerator extgen;
	private TestSink sink;

	private Semaphore semaphore = new Semaphore(0);

	private ArrayList<double[]> s = new ArrayList();
	private long maxDiff;
	private boolean signalDetected = true;
	private boolean outOfSeq;
	private int count = 0;
	private long wrongSeq;
	private long maxDifNo;

	private boolean silenceExpected = false;

	public EndpointTransmittorTest() {
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
                timer.start();
                
		gen = new SineGenerator("test.sine", timer);
		gen.setAmplitude(Short.MAX_VALUE);
		gen.setFrequency(F);

		extgen = new ExtSineGenerator("test.sine", timer);
		extgen.setAmplitude(Short.MAX_VALUE);
		extgen.setFrequency(F);
		extgen.setGenerationTime(3000);

		sink = new TestSink("test.sink");
		filler = new EndpointTransmittor("test.filler", timer);

	}

	@After
	public void tearDown() {
            timer.stop();
	}

	/**
	 * Test of getInput method, of class NoiseFiller.
	 */
	@Test
	public void testTransmission() throws Exception {
		gen.connect(filler.getInput());
		sink.connect(filler.getOutput());

		filler.getInput().start();
		filler.getOutput().start();

		gen.start();
		sink.start();

		semaphore.tryAcquire(5, TimeUnit.SECONDS);
		gen.stop();

		assertTrue("Interarrival interval to long", maxDiff < 60);
		assertTrue("Signal not detected", signalDetected);
		assertFalse("Out of sequence", outOfSeq);
		assertTrue("Signal too short", count > 200);

		signalDetected = false;
		count = 0;

		this.silenceExpected = true;

		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertTrue("Interarrival interval to long", maxDiff < 60);
		assertFalse("Signal detected", signalDetected);
		assertFalse("Out of sequence", outOfSeq);
		assertTrue("Signal too short", count > 200);

	}

	/**
	 * Test of getInput method, of class NoiseFiller.
	 */

	@Test
	public void testTransmission2() throws Exception {

		extgen.connect(filler.getInput());
		sink.connect(filler.getOutput());

		filler.getInput().start();
		filler.getOutput().start();

		extgen.start();
		sink.start();

		semaphore.tryAcquire(3, TimeUnit.SECONDS);
		extgen.stop();

		assertTrue("Interarrival interval to long", maxDiff < 60);
		assertTrue("Signal not detected", signalDetected);
		assertFalse("Out of sequence", outOfSeq);
		assertTrue("Signal too short", count > 140);

		signalDetected = false;
		count = 0;

		this.silenceExpected = true;

		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertTrue("Interarrival interval to long", maxDiff < 60);
		assertFalse("Signal detected", signalDetected);
		assertFalse("Out of sequence", outOfSeq);
		
		assertTrue("Signal too short", count > 200);
	}

	private class TestSink extends AbstractSink {

		private long timestamp;
		private long seq;

		public TestSink(String name) {
			super(name);
		}

		@Override
		public void onMediaTransfer(Buffer buffer) throws IOException {
			count++;
			long next = buffer.getTimeStamp();
			long seqNext = buffer.getSequenceNumber();

			if (count == 1) {
				timestamp = next;
				seq = seqNext;
				return;
			}

			long diff = Math.abs(next - timestamp);
			if (diff > maxDiff && !silenceExpected) {
				maxDiff = diff;
				maxDifNo = seqNext;
			}

			if (silenceExpected) {
				silenceExpected = false;
			}

			if (seqNext - seq != 1) {
				outOfSeq = true;
				wrongSeq = seq;
			}

			timestamp = next;
			seq = seqNext;

			byte[] data = (byte[]) buffer.getData();
			short[] signal = new short[buffer.getLength() / 2];
			int k = buffer.getOffset();
			for (int i = 0; i < signal.length; i++) {
				signal[i] = (short) ((data[k++] & 0xff) | (data[k++] << 8));
			}

			for (int i = 0; i < signal.length; i++) {
				if (signal[i] > 320) {
					signalDetected = true;
					return;
				}
			}

		}

		public Format[] getFormats() {
			return new Format[] { Codec.LINEAR_AUDIO };
		}

		public boolean isAcceptable(Format format) {
			return Codec.LINEAR_AUDIO.matches(format);
		}

	}

	private class ExtSineGenerator extends SineGenerator {

		private double time;

		private double generationTime;

		public ExtSineGenerator(String name, Timer timer) {
			super(name, timer);
		}

		public void setGenerationTime(double generationTime) {
			this.generationTime = generationTime;

		}

		@Override
		public void evolve(Buffer buffer, long timestamp, long seq) {
			super.evolve(buffer, timestamp, seq);
			time += ((double) getDuration()) / 1000.0;

			if (time >= (this.generationTime / 1000.0)) {
				buffer.setEOM(true);
			}
		}

	}
}