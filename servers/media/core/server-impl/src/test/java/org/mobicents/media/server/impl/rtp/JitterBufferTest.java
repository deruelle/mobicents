package org.mobicents.media.server.impl.rtp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;

/**
 * 
 * @author amit bhayani
 * 
 */
public class JitterBufferTest {

	private int period = 20;
	private int jitter = 40;
	private JitterBuffer jitterBuffer;
	private final long ssrc = System.currentTimeMillis();


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

		// RFC2833 DtmfEvent for 9, marker=true, endOfEvent = false, volume =
		// 10, seq = 0, ts = 0
		byte[] by0 = new byte[] { -128, -27, 0, 0, 0, 0, 0, 0, -101, 29, -126, 82, 9, 10, 0, 0 };
		// RFC2833 DtmfEvent for 9, marker=false, endOfEvent = false, volume =
		// 10, seq = 1, ts = 20
		byte[] by1 = new byte[] { -128, 101, 0, 1, 0, 0, 0, 20, -101, 29, -126, 82, 9, 10, 0, 0 };

		// RFC2833 DtmfEvent for 9, marker=false, endOfEvent = false, volume =
		// 10, seq = 2, ts = 40
		byte[] by2 = new byte[] { -128, 101, 0, 2, 0, 0, 0, 40, -101, 29, -126, 82, 9, 10, 0, 0 };

		// RFC2833 DtmfEvent for 9, marker=false, endOfEvent = true, volume =
		// 10, seq = 3, ts = 60
		byte[] by3 = new byte[] { -128, 101, 0, 3, 0, 0, 0, 60, -101, 32, -51, -92, 9, -118, 0, 0 };
		write(jitterBuffer, by0, 16);
		write(jitterBuffer, by1, 16);
		write(jitterBuffer, by2, 16);

		Buffer buff = jitterBuffer.read();
		assertTrue(buff != null);
		assertEquals(AVProfile.DTMF, buff.getFormat());

		write(jitterBuffer, by3, 16);

		buff = jitterBuffer.read();
		assertTrue(buff != null);

		buff = jitterBuffer.read();
		assertTrue(buff != null);

		buff = jitterBuffer.read();
		assertTrue(buff != null);
		assertEquals(AVProfile.DTMF, buff.getFormat());

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

//		while (remainder > 0) {
//			int len = Math.min(10, remainder);
//			jb.write(data, offset, len);
//			remainder -= len;
//			offset += len;
//		}
		jb.write(data, offset, remainder);
	}

}
