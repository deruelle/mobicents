package org.mobicents.media.server.impl.rtp;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.RtpHeader;
import org.mobicents.media.server.spi.dsp.Codec;
import static org.junit.Assert.*;

/**
 * 
 * @author amit bhayani
 * 
 */
public class JitterBufferTest {

	private AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);

	private int period = 20;
	private int jitter = 40;
	private JitterBuffer jitterBuffer;
	private BufferFactory bufferFactory = new BufferFactory(10, "ReceiverBuffer");
	private ArrayList packets = new ArrayList();
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
		rtpMap.put(8, PCMA);
		jitterBuffer = new JitterBuffer(jitter, period, rtpMap);
	}

	@After
	public void tearDown() {
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

	private void printData(byte[] data, int offSet, int length) {
		System.out.println("---------------");
		for (int i = offSet; i < length; i++) {
			int temp = (int) data[i];
			System.out.print(temp + " ");
		}
		System.out.println("");
	}

	@Test
	public void testAccuracy() {

		/***********************************************************************
		 * Fill Buffer 1
		 **********************************************************************/
		int seq = 1;
		byte[] data = fillByte(160);

		Buffer buffer = createBuffer(seq, data, true);
		RtpHeader h = (RtpHeader) buffer.getHeader();
		byte[] headerByte = h.toByteArray();

		int len = headerByte.length + buffer.getLength();

		byte[] senderBuffer1 = new byte[len];

		// combine RTP header and payload
		System.arraycopy(headerByte, 0, senderBuffer1, 0, headerByte.length);
		System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer1, headerByte.length, buffer.getLength());

		jitterBuffer.write(senderBuffer1);

		Buffer readbuffer = jitterBuffer.read();
		if (readbuffer != null) {
			fail("Buffer is new and not full yet. But it allow to read packets");
		}

		/***********************************************************************
		 * Fill Buffer 2
		 **********************************************************************/
		seq = 2;
		data = fillByte(160);

		buffer = createBuffer(seq, data, false);
		h = (RtpHeader) buffer.getHeader();
		headerByte = h.toByteArray();
		len = headerByte.length + buffer.getLength();

		byte[] senderBuffer2 = new byte[len];

		// combine RTP header and payload
		System.arraycopy(headerByte, 0, senderBuffer2, 0, headerByte.length);
		System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer2, headerByte.length, buffer.getLength());

		jitterBuffer.write(senderBuffer2);

		/***********************************************************************
		 * Read Buffer 1
		 **********************************************************************/
		Buffer readbuffer1 = jitterBuffer.read();
		if (readbuffer1 == null) {
			fail("Buffer is new and not empty but it doesn't allow to read packets");
		}
		h = (RtpHeader) readbuffer1.getHeader();
		assertEquals(1, h.getSeqNumber());

		printData((byte[]) readbuffer1.getData(), readbuffer1.getOffset(), readbuffer1.getLength());

		/***********************************************************************
		 * Fill Buffer 3 - first half
		 **********************************************************************/
		data = fillByte(80);
		seq = 3;

		buffer = createBuffer(seq, data, false);
		h = (RtpHeader) buffer.getHeader();
		headerByte = h.toByteArray();
		len = headerByte.length + buffer.getLength();

		byte[] senderBuffer3 = new byte[len];

		// combine RTP header and payload
		System.arraycopy(headerByte, 0, senderBuffer3, 0, headerByte.length);
		System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer3, headerByte.length, buffer.getLength());

		jitterBuffer.write(senderBuffer3);

		/***********************************************************************
		 * Fill Buffer 3 - second half
		 **********************************************************************/
		data = fillByte(80);
		jitterBuffer.write(data);

		/***********************************************************************
		 * Read Buffer 2
		 **********************************************************************/
		Buffer readbuffer2 = jitterBuffer.read();
		if (readbuffer2 == null) {
			fail("Buffer is new and not empty but it doesn't allow to read packets");
		}
		h = (RtpHeader) readbuffer2.getHeader();
		assertEquals(2, h.getSeqNumber());

		printData((byte[]) readbuffer2.getData(), readbuffer2.getOffset(), readbuffer2.getLength());

		/***********************************************************************
		 * Read Buffer 3
		 **********************************************************************/
		Buffer readbuffer3 = jitterBuffer.read();
		if (readbuffer3 == null) {
			fail("Buffer is new and not empty but it doesn't allow to read packets");
		}
		h = (RtpHeader) readbuffer3.getHeader();
		assertEquals(3, h.getSeqNumber());

		byte[] testData = (byte[]) readbuffer3.getData();

		assertEquals(160, readbuffer3.getLength());

		printData((byte[]) readbuffer3.getData(), readbuffer3.getOffset(), readbuffer3.getLength());
	}

	@Test
	public void testOverflow() {
		/***********************************************************************
		 * Fill Buffer 1
		 **********************************************************************/
		int seq = 1;
		byte[] data = fillByte(160);

		Buffer buffer = createBuffer(seq, data, true);
		RtpHeader h = (RtpHeader) buffer.getHeader();
		byte[] headerByte = h.toByteArray();

		int len = headerByte.length + buffer.getLength();

		byte[] senderBuffer1 = new byte[len];

		// combine RTP header and payload
		System.arraycopy(headerByte, 0, senderBuffer1, 0, headerByte.length);
		System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer1, headerByte.length, buffer.getLength());

		jitterBuffer.write(senderBuffer1);

		/***********************************************************************
		 * Fill Buffer 2
		 **********************************************************************/
		seq = 2;
		data = fillByte(160);

		buffer = createBuffer(seq, data, false);
		h = (RtpHeader) buffer.getHeader();
		headerByte = h.toByteArray();
		len = headerByte.length + buffer.getLength();

		byte[] senderBuffer2 = new byte[len];

		// combine RTP header and payload
		System.arraycopy(headerByte, 0, senderBuffer2, 0, headerByte.length);
		System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer2, headerByte.length, buffer.getLength());

		jitterBuffer.write(senderBuffer2);

		/***********************************************************************
		 * Fill Buffer 3
		 **********************************************************************/
		data = fillByte(160);
		seq = 3;

		buffer = createBuffer(seq, data, false);
		h = (RtpHeader) buffer.getHeader();
		headerByte = h.toByteArray();
		len = headerByte.length + buffer.getLength();

		byte[] senderBuffer3 = new byte[len];

		// combine RTP header and payload
		System.arraycopy(headerByte, 0, senderBuffer3, 0, headerByte.length);
		System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer3, headerByte.length, buffer.getLength());

		jitterBuffer.write(senderBuffer3);

		/***********************************************************************
		 * Fill Buffer 4
		 **********************************************************************/
		data = fillByte(160);
		seq = 4;

		buffer = createBuffer(seq, data, false);
		h = (RtpHeader) buffer.getHeader();
		headerByte = h.toByteArray();
		len = headerByte.length + buffer.getLength();

		byte[] senderBuffer4 = new byte[len];

		// combine RTP header and payload
		System.arraycopy(headerByte, 0, senderBuffer4, 0, headerByte.length);
		System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer4, headerByte.length, buffer.getLength());

		jitterBuffer.write(senderBuffer4);

		/***********************************************************************
		 * Fill Buffer 5
		 **********************************************************************/
		data = fillByte(160);
		seq = 5;

		buffer = createBuffer(seq, data, false);
		h = (RtpHeader) buffer.getHeader();
		headerByte = h.toByteArray();
		len = headerByte.length + buffer.getLength();

		byte[] senderBuffer5 = new byte[len];

		// combine RTP header and payload
		System.arraycopy(headerByte, 0, senderBuffer5, 0, headerByte.length);
		System.arraycopy((byte[]) buffer.getData(), 0, senderBuffer5, headerByte.length, buffer.getLength());

		jitterBuffer.write(senderBuffer5);

		buffer = jitterBuffer.read();
		h = (RtpHeader) buffer.getHeader();
		assertEquals(1, h.getSeqNumber());

		buffer = jitterBuffer.read();
		h = (RtpHeader) buffer.getHeader();
		assertEquals(2, h.getSeqNumber());

		buffer = jitterBuffer.read();
		h = (RtpHeader) buffer.getHeader();
		assertEquals(3, h.getSeqNumber());

		buffer = jitterBuffer.read();
		h = (RtpHeader) buffer.getHeader();
		assertEquals(4, h.getSeqNumber());

		buffer = jitterBuffer.read();
		assertNull(buffer);

	}
}
