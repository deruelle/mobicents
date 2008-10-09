/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.clock.Quartz;
import org.mobicents.media.server.impl.clock.Timer;

/**
 * 
 * @author Oleg Kulikov
 */
public class MultiplexerTest {

	private final static int TEST_DURATION = 20;
	private Multiplexer mux;
	private ArrayList packets = new ArrayList();
	private int count1;
	private int count2;

	public MultiplexerTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		mux = new Multiplexer();
		packets = new ArrayList();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGetOutputFormats() {
		Format[] formats = mux.getOutput().getFormats();
		if (formats != null) {
			fail("Mux output is in the air");
		}

		Source1 s1 = new Source1();
		Source2 s2 = new Source2();

		mux.connect(s1);
		mux.connect(s2);

		AudioFormat f1 = new AudioFormat("F1");
		AudioFormat f2 = new AudioFormat("F2");

		formats = mux.getOutput().getFormats();

		assertEquals(true, hasFormat(f1, formats));
		assertEquals(true, hasFormat(f2, formats));
		assertEquals(2, formats.length);

		assertEquals(null, mux.getFormats());

		Sink sink = new Sink();
		try {
			mux.getOutput().connect(sink);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		formats = mux.getFormats();

		assertEquals(true, hasFormat(f1, formats));
		assertEquals(true, hasFormat(f2, formats));
		assertEquals(2, formats.length);

	}

	private boolean hasFormat(AudioFormat f, Format[] formats) {
		if (formats == null) {
			return false;
		}

		for (Format format : formats) {
			if (f.matches(format)) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void testMux() {
		progress(TEST_DURATION);
		checkMux();
	}

	@Test
	public void testSeq() {
		progress(TEST_DURATION);
		checkSeq();
	}

	private void checkMux() {
		int c1 = getPacketNum(new AudioFormat("F1"));
		int c2 = getPacketNum(new AudioFormat("F2"));

		System.out.println("Expected " + count1 + ", received " + c1);
		if (Math.abs(c1 - count1) > 2) {
			fail("Received packets for format F1 " + c1 + ", expected=" + count1);
		}
		if (Math.abs(c2 - count2) > 2) {
			fail("Received packets for format F2 " + c1 + ", expected=" + count1);
		}
	}

	private void checkSeq() {
		for (int i = 0; i < packets.size() - 1; i++) {
			Buffer b1 = (Buffer) packets.get(i);
			Buffer b2 = (Buffer) packets.get(i + 1);
			if ((b2.getSequenceNumber() - b1.getSequenceNumber()) != 1) {
				fail("Wrong seq");
			}
		}
	}

	private int getPacketNum(AudioFormat fmt) {
		int count = 0;
		for (int i = 0; i < packets.size(); i++) {
			Buffer b = (Buffer) packets.get(i);
			if (fmt.matches(b.getFormat())) {
				count++;
			}
		}
		return count;
	}

	@SuppressWarnings("static-access")
	private void progress(int duration) {
		Source1 s1 = new Source1();
		Source2 s2 = new Source2();

		s1.start();
		s2.start();

		mux.connect(s1);

		mux.connect(s2);

		mux.getOutput().start();
		mux.getOutput().connect(new Sink());

		try {
			Thread.currentThread().sleep(duration * 1000);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		s1.stop();
		s2.stop();
		mux.getOutput().stop();
	}

	private class Source1 extends AbstractSource implements Runnable {

		private Timer timer = new Timer();
		private int seq;
		private AudioFormat f = new AudioFormat("F1");

		public Source1() {
			super("MultiplexerTest.Source1");
			timer.setListener(this);
		}

		public void start() {
			timer.start();
		}

		public void stop() {
			timer.stop();
		}

		public void run() {
			Buffer buffer = new Buffer();
			buffer.setSequenceNumber(seq);
			buffer.setTimeStamp(seq * Quartz.HEART_BEAT);
			buffer.setFormat(f);

			if (sink != null) {
				sink.receive(buffer);
				count1++;
			}
		}

		public Format[] getFormats() {
			return new Format[] { f };
		}
	}

	private class Source2 extends AbstractSource implements Runnable {

		private Timer timer = new Timer();
		private int seq;
		private AudioFormat f = new AudioFormat("F2");

		public Source2() {
			super("MultiplexerTest.Source2");
			timer.setListener(this);
		}

		public Format[] getFormats() {
			return new Format[] { f };
		}

		public void start() {
			timer.start();
		}

		public void stop() {
			timer.stop();
		}

		public void run() {
			Buffer buffer = new Buffer();
			buffer.setSequenceNumber(seq);
			buffer.setTimeStamp(seq * Quartz.HEART_BEAT);
			buffer.setFormat(f);

			if (sink != null) {
				sink.receive(buffer);
			}
			count2++;
		}
	}

	private class Sink extends AbstractSink {

		public Sink() {
			super("MultiplexerTest.Sink");
		}

		private AudioFormat f1 = new AudioFormat("F1");
		private AudioFormat f2 = new AudioFormat("F2");

		public void receive(Buffer buffer) {
			packets.add(buffer);
		}

		public Format[] getFormats() {
			return new Format[] { f1, f2 };
		}

		public boolean isAcceptable(Format format) {
			return format.matches(f1) || format.matches(f2);
		}
	}
}