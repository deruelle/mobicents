package org.mobicents.media.server.impl.rtp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 * 
 * 
 */
public class RtpFactoryReuseTest {

	private static final int HEART_BEAT = 20;

	private static final AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
	private static final AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);

	private final static int TEST_DURATION = 2;
	private final static double ERRORS = 5;

	private static Map<Integer, Format> formatMap = new HashMap<Integer, Format>();
	private RtpSocket serverSocket;
	private RtpSocket clientSocket;
	private InetAddress localAddress;
	private ArrayList<Buffer> packets = new ArrayList<Buffer>();
	private int errorCount;
	private int MAX_ERRORS;
	static {
		formatMap.put(0, PCMU);
		formatMap.put(8, PCMA);
	}

	private RtpFactory factory = null;

	private BufferFactory bufferFactory = new BufferFactory(10, "RtpFactoryReuseTest");
	private final long ssrc = System.currentTimeMillis();

	// @BeforeClass
	// public static void setUpClass() throws Exception {
	//
	// }
	//
	// @AfterClass
	// public static void tearDownClass() throws Exception {
	// }

	@Before
	public void setUp() throws Exception {
		try {
			// on windows getLocalHost returns what ever it finds first -
			// usually vpn if one has it.
			localAddress = InetAddress.getByName("localhost");
		} catch (Exception e) {
		}
		factory = new RtpFactory();
		factory.setFormatMap(formatMap);

		Timer timer = new TimerImpl();
		timer.setHeartBeat(HEART_BEAT);

		factory.setTimer(timer);
		factory.setJitter(80);
		factory.setBindAddress("localhost");
		factory.setPortRange("1281-1282");
		int ps = 1000 / HEART_BEAT;
		MAX_ERRORS = (int) Math.round(100.0 / ps * ERRORS);

	}

	@After
	public void tearDown() {
		// Dont close the Factory as it will stop the RtpSocket.readerThread and
		// RtpSocketTest will be screwed
		// factory.stop();
	}

	@Test
	public void getRTPSocketTransmitTwiceTest() {
		// This is twice RtpSocket test, to try out reusing of sockets.

		try {
			serverSocket = factory.getRTPSocket();
			clientSocket = factory.getRTPSocket();

			serverSocket.setPeer(localAddress, clientSocket.getLocalPort());
			clientSocket.setPeer(localAddress, serverSocket.getLocalPort());

			serverSocket.getReceiveStream().connect(new Receiver());
			serverSocket.getReceiveStream().start();

			Sender sender = new Sender();
			clientSocket.getSendStream().connect(sender);
			sender.start();

			try {
				Thread.currentThread().sleep(TEST_DURATION * 1000);
			} catch (Exception e) {
				fail(e.getMessage());
			}

			sender.stop();
			assertTrue((packets.size() > 0));
			int k = 0;
			for (int i = 0; i < packets.size(); i++) {
				if (packets.get(i) != null) {
					k = i;
					break;
				}
			}

			long exp = 1;
			for (int i = k; i < packets.size(); i++) {
				Buffer buffer = (Buffer) packets.get(i);
				byte[] data = (byte[]) buffer.getData();
				
				System.out.println(buffer.getFormat());

				if (!this.correctData(data, buffer.getOffset(), buffer.getLength())) {
					errorCount++;
				}
			}

			if (errorCount > MAX_ERRORS) {
				fail("Too many errors: " + errorCount + ", max=" + MAX_ERRORS);
			}
			System.out.println("Total errors: " + errorCount + ", max=" + MAX_ERRORS);

			serverSocket.release();
			clientSocket.release();

			errorCount = 0;
			this.packets = new ArrayList();
			// This is caused by sockets waiting for late packets.
			doWait();

			serverSocket = factory.getRTPSocket();
			clientSocket = factory.getRTPSocket();

			serverSocket.setPeer(localAddress, clientSocket.getLocalPort());
			clientSocket.setPeer(localAddress, serverSocket.getLocalPort());

			serverSocket.getReceiveStream().connect(new Receiver());
			serverSocket.getReceiveStream().start();

			sender = new Sender();
			clientSocket.getSendStream().connect(sender);
			sender.start();

			try {
				Thread.currentThread().sleep(TEST_DURATION * 1000);
			} catch (Exception e) {
				fail(e.getMessage());
			}

			sender.stop();

			serverSocket.release();
			clientSocket.release();

			assertTrue((packets.size() > 0));
			k = 0;
			for (int i = 0; i < packets.size(); i++) {
				if (packets.get(i) != null) {
					k = i;
					break;
				}
			}

			exp = 1;
			for (int i = k; i < packets.size(); i++) {
				Buffer buffer = (Buffer) packets.get(i);
				byte[] data = (byte[]) buffer.getData();
				System.out.println(buffer.getFormat());
				if (!this.correctData(data, buffer.getOffset(), buffer.getLength())) {
					errorCount++;
				}
			}

			if (errorCount > MAX_ERRORS) {
				fail("Too many errors: " + errorCount + ", max=" + MAX_ERRORS);
			}
			System.out.println("Total errors: " + errorCount + ", max=" + MAX_ERRORS);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed - was not able to obtain sockets, tranismit data, release and retransmit.");
		}
	}

	private boolean correctData(byte[] data, int offset, int length) {
		boolean result = true;
		if (length != 160) {
			return false;
		}
		int q = 0;
		for (int i = offset; i < length; i++) {
			if (q == 128) {
				q = 0;
			}
			int temp = (int) data[i];
			result = (result && (q == temp));
			q++;
		}
		return result;
	}

	private class Receiver extends AbstractSink {

		public Receiver() {
			super("RtpSocketImplTest.Receiver");
		}

		public void receive(Buffer buffer) {
			byte[] bb = (byte[]) buffer.getData();
			packets.add(buffer);
		}

		public Format[] getFormats() {
			return new Format[] { PCMA };
		}

		public boolean isAcceptable(Format format) {
			return format.matches(PCMA);
		}
	}

	private class Sender extends AbstractSource implements Runnable {

		private int seq = 0;
		private TimerImpl timer = new TimerImpl();
		private ScheduledFuture task = null;
		private boolean marker = true;

		public Sender() {
			super("RtpSocketImplTest.Source");
		}

		public void run() {
			seq++;
			byte[] data = fillByte(160);
			Buffer buffer = createBuffer(seq, data, marker);
			marker = false;

			if (otherParty != null) {
				otherParty.receive(buffer);
			}
		}

		public void start() {
			task = timer.synchronize(this);
		}

		public void stop() {
			task.cancel(false);
		}

		public Format[] getFormats() {
			return new Format[] { PCMA };
		}

		private byte[] fillByte(int capacity) {
			byte[] data = new byte[capacity];
			int q = 0;
			for (int i = 0; i < data.length; i++) {
				if (q == 128) {
					q = 0;
				}
				data[i] = (byte) q;
				q++;
			}
			return data;

		}

		private Buffer createBuffer(int seq, byte[] data, boolean marker) {
			Buffer buffer = bufferFactory.allocate(true);
			buffer.setFormat(Codec.PCMA);
			buffer.setSequenceNumber(seq);
			buffer.setTimeStamp(seq * 20);
			buffer.setData(data);
			buffer.setOffset(0);
			buffer.setLength(data.length);

			RtpHeader h = (RtpHeader) buffer.getHeader();
			h.init(marker, (byte) 8, (int) buffer.getSequenceNumber(), (int) buffer.getTimeStamp(), ssrc);
			return buffer;
		}
	}

	private void doWait() {
		try {
			Thread.currentThread().sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
