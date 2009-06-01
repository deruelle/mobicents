package org.mobicents.media.server.impl.rtp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.resource.DtmfDetector;

/**
 * 
 * @author amit bhayani
 * 
 */
public class JitterBufferTest {

	private int period = 20;
	private int jitter = 40;
	private JitterBuffer jitterBuffer;
	private BufferFactory bufferFactory = new BufferFactory(10, "JitterBufferTest");
	private ArrayList packets = new ArrayList();
	private final long ssrc = System.currentTimeMillis();

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

	private Buffer generateRfc2833Buffer(boolean endOfEvent, boolean marker, int seq, byte digit, int volume) {
		Buffer buffer = bufferFactory.allocate();

		RtpHeader rtpHeader = new RtpHeader();
		rtpHeader.init(marker, (byte) 101, seq, (int) seq * 20, ssrc);
		buffer.setHeader(rtpHeader);

		byte[] data = (byte[]) buffer.getData();
		data[0] = digit;
		data[1] = endOfEvent ? (byte) (volume | 0x80) : (byte) (volume & 0x7f);

		buffer.setOffset(0);
		buffer.setLength(4);
		buffer.setFormat(AVProfile.DTMF);
		buffer.setSequenceNumber(seq);
		buffer.setTimeStamp(seq * 20);

		return buffer;

	}

	@Test
	public void testRFC2833Reading() throws Exception {

		int seq = 0;

		byte digit = (byte) 9;
		int volume = 10;
		boolean endOfEvent = false;

		Buffer b0 = generateRfc2833Buffer(endOfEvent, true, seq, digit, volume);
		seq++;
		Buffer b1 = generateRfc2833Buffer(endOfEvent, false, seq, digit, volume);
		seq++;
		Buffer b2 = generateRfc2833Buffer(endOfEvent, false, seq, digit, volume);
		seq++;

		write(jitterBuffer, createBuffer(b0), 16);
		write(jitterBuffer, createBuffer(b1), 16);
		write(jitterBuffer, createBuffer(b2), 16);

		Buffer buff = jitterBuffer.read();
		assertTrue(buff != null);

		RtpHeader head = (RtpHeader) buff.getHeader();
		assertTrue(head.getMarker());
		byte[] data = (byte[]) buff.getData();

		String recdDigit = DtmfDetector.TONE[data[0]];
		assertEquals("9", recdDigit);
		boolean end = (data[1] & 0x80) != 0;
		assertEquals(false, end);
		
		endOfEvent = true;
		Buffer b3 = generateRfc2833Buffer(endOfEvent, false, seq, digit, volume);
		seq++;
		write(jitterBuffer, createBuffer(b3), 16);
		
		buff = jitterBuffer.read();
		assertTrue(buff != null);
		
		buff = jitterBuffer.read();
		assertTrue(buff != null);
		
		buff = jitterBuffer.read();
		assertTrue(buff != null);
		
		head = (RtpHeader) buff.getHeader();
		assertTrue(!head.getMarker());
		data = (byte[]) buff.getData();

		recdDigit = DtmfDetector.TONE[data[0]];
		assertEquals("9", recdDigit);
		end = (data[1] & 0x80) != 0;
		assertEquals(true, end);

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

}
