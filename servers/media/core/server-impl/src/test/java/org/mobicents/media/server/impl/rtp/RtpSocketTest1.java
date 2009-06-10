package org.mobicents.media.server.impl.rtp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

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
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.dsp.Codec;


/**
 * 
 * @author amit bhayani
 * 
 */
public class RtpSocketTest1 {

	private final static int TEST_DURATION = 2;
	private final static double ERRORS = 5;
	private int MAX_ERRORS;
	private int HEART_BEAT = 20;

	private RtpFactory rtpfactory = null;

	private RtpSocket serverSocket;
	private RtpSocket clientSocket;
	// private int period = 20;
	// private int jitter = 40;
	private InetAddress localAddress;
	private AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);

	private BufferFactory bufferFactory = new BufferFactory(10, "RtpSocketTest");
	private final long ssrc = System.currentTimeMillis();

	private ArrayList<Buffer> packets = new ArrayList();
	private int errorCount;

	private TimerImpl timer = null;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		int ps = 1000 / HEART_BEAT;
		MAX_ERRORS = (int) Math.round(100.0 / ps * ERRORS);

		timer = new TimerImpl();
		timer.setHeartBeat(HEART_BEAT);

		rtpfactory = new RtpFactory();

		try {
			localAddress = InetAddress.getLocalHost();
		} catch (Exception e) {
		}
		HashMap<Integer, Format> rtpMap = new HashMap();
		rtpMap.put(97, AVProfile.SPEEX);

		serverSocket = new RtpSocket(timer, rtpMap, rtpfactory);
		clientSocket = new RtpSocket(timer, rtpMap, rtpfactory);
	}

//	@Test
	@SuppressWarnings("static-access")
	public void testTransmission() throws Exception {
		int p1 = serverSocket.init(localAddress, 1024, 65535);
		int p2 = clientSocket.init(localAddress, 1024, 65535);

		// serverSocket.addFormat(1, PCMA);
		// clientSocket.addFormat(1, PCMA);

		serverSocket.setPeer(localAddress, p2);
		clientSocket.setPeer(localAddress, p1);

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

		serverSocket.close();
		clientSocket.close();

		assertTrue((packets.size() > 0));
		for (int i = 1; i < packets.size(); i++) {
			Buffer buffer = (Buffer) packets.get(i);
			RtpHeader header = (RtpHeader)buffer.getHeader();
			assertEquals(i, header.getSeqNumber());
			assertEquals(97, header.getPayloadType());			

                        int d1 = getPayload(buffer);
                        int d2 = getPayload(packets.get(i -1));
                        
                        assertEquals(1, (d1 - d2));

		}

		if (errorCount > MAX_ERRORS) {
			fail("Too many errors: " + errorCount + ", max=" + MAX_ERRORS);
		}
	}
	

        private int getPayload(Buffer buffer) {
        	byte[] data = (byte[]) buffer.getData();
                return data[0];
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
//                    System.out.println("Receive " + buffer + ", data = " + ((byte[])buffer.getData())[0]);
			byte[] bb = (byte[]) buffer.getData();
			packets.add(buffer);
		}

		public Format[] getFormats() {
			return new Format[] {AVProfile.SPEEX };
		}

		public boolean isAcceptable(Format format) {
			return format.matches(AVProfile.SPEEX);
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
			byte[] data = fillByte(20);
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
				data[i] = (byte) seq;
			}
			return data;

		}

		private Buffer createBuffer(int seq, byte[] data, boolean marker) {
			Buffer buffer = bufferFactory.allocate(true);
			buffer.setFormat(AVProfile.SPEEX);
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
}
