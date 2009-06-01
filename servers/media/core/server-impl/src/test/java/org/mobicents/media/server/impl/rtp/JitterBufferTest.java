package org.mobicents.media.server.impl.rtp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.dtmf.Rfc2833GeneratorImpl;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.resource.DtmfGenerator;

/**
 * 
 * @author amit bhayani
 * 
 */
public class JitterBufferTest {

	private int period = 20;
	private int jitter = 40;
	private JitterBuffer jitterBuffer;
	private BufferFactory bufferFactory = new BufferFactory(10, "ReceiverBuffer");
	private ArrayList packets = new ArrayList();
	private final long ssrc = System.currentTimeMillis();

	private List<Buffer> list = null;

	private Semaphore semaphore;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		HashMap<Integer, Format> rtpMap = new HashMap();
		rtpMap.put(8, AVProfile.PCMA);
		rtpMap.put(101, AVProfile.DTMF);
		jitterBuffer = new JitterBuffer(jitter, period, rtpMap);

		list = new ArrayList<Buffer>();
		semaphore = new Semaphore(0);
	}

	@After
	public void tearDown() {
	}

	private byte[] createBuffer(int seq) {
		RtpHeader header = new RtpHeader();
		header.init(false, (byte) 8, (int) seq, (int) (seq * 160), ssrc);
		byte[] h = header.toByteArray();

		byte[] res = new byte[172];
		System.arraycopy(h, 0, res, 0, 12);
		return res;
	}

	private byte[] createBuffer(Buffer buffer) {
		RtpHeader h = (RtpHeader) buffer.getHeader();
		byte[] headerByte = h.toByteArray();

		int len = headerByte.length + buffer.getLength();

		byte[] senderBuffer = new byte[len];

		// combine RTP header and payload
		System.arraycopy(headerByte, 0, senderBuffer, 0, headerByte.length);
		System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer, headerByte.length, buffer.getLength());
		return senderBuffer;
	}

	@Test
	public void testAccuracy() {

		/***********************************************************************
		 * Fill Buffer 1
		 **********************************************************************/
		jitterBuffer.write(createBuffer(1), 0, 172);
		Buffer buff = jitterBuffer.read();
		assertEquals("Jitter Buffer not full yet", null, buff);

		jitterBuffer.write(createBuffer(2), 0, 172);
		buff = jitterBuffer.read();
		assertEquals("Jitter Buffer not full yet", null, buff);

		jitterBuffer.write(createBuffer(3), 0, 172);
		buff = jitterBuffer.read();
		assertTrue("Failed to match binary representation.", buff != null);

	}

	@Test
	public void testRFC2833Reading() throws Exception {
		Timer timer = new TimerImpl();

		Endpoint endpoint = new EndpointImpl();
		endpoint.setTimer(timer);

		DtmfGenerator generator = new Rfc2833GeneratorImpl("JitterBufferTest");

		generator.connect(new TestSink("TestSink"));
		generator.setDuration(100); // 100 ms
		generator.setVolume(10);
		generator.setDigit("9");
		generator.setEndpoint(endpoint);

		generator.fireDtmf();

		semaphore.tryAcquire(150, TimeUnit.MILLISECONDS);

		assertEquals(7, list.size());

		write(jitterBuffer, createBuffer(list.get(0)), 16);
		write(jitterBuffer, createBuffer(list.get(1)), 16);
		write(jitterBuffer, createBuffer(list.get(2)), 16);

		Buffer buff = jitterBuffer.read();
		assertTrue(buff != null);
		
		RtpHeader head = (RtpHeader)buff.getHeader();
		assertTrue(head.getMarker());

	}

	@Test
	public void testAsyncWritting() {
		byte[] b1 = createBuffer(1);
		byte[] b2 = createBuffer(2);
		byte[] b3 = createBuffer(3);

		write(jitterBuffer, b1, 172);
		Buffer buff = jitterBuffer.read();
		assertEquals("Jitter Buffer not full yet", null, buff);

		write(jitterBuffer, b2, 172);
		buff = jitterBuffer.read();
		assertEquals("Jitter Buffer not full yet", null, buff);

		write(jitterBuffer, b3, 172);
		buff = jitterBuffer.read();
		assertTrue("Failed to match binary representation.", buff != null);

		assertEquals("Seq number problem", 0, buff.getSequenceNumber());
	}

	private void write(JitterBuffer jb, byte[] data, int remainder) {
		int offset = 0;
		// int remainder = 172;

		while (remainder > 0) {
			int len = Math.min(10, remainder);
			jb.write(data, offset, len);
			remainder -= len;
			offset += len;
		}
	}

	private class TestSink extends AbstractSink {

		private long lastDuration = 0;
		private long lastSeqNo = 0;
		private long timeStamp = 0;

		private int packetsReceived = 0;

		private TestSink(String name) {
			super(name);
		}

		public Format[] getFormats() {
			return new Format[0];
		}

		public boolean isAcceptable(Format format) {
			return true;
		}

		public void receive(Buffer buffer) {
			
			
			RtpHeader header = (RtpHeader)buffer.getHeader();
			
			System.out.println(buffer +" \n " + header.getMarker());
			
			header.init(header.getMarker(), (byte) 101, header.getSeqNumber(), (int) buffer.getTimeStamp(), buffer.getSequenceNumber());
			buffer.setHeader(header);
			
			list.add(buffer);
		}
	}
}
