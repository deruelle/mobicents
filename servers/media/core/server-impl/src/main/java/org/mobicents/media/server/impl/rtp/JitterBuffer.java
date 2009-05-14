/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.rtp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.Format;
import org.mobicents.media.server.RtpHeader;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 * Implements jitter buffer.
 * 
 * A jitter buffer temporarily stores arriving packets in order to minimize
 * delay variations. If packets arrive too late then they are discarded. A
 * jitter buffer may be mis-configured and be either too large or too small.
 * 
 * If a jitter buffer is too small then an excessive number of packets may be
 * discarded, which can lead to call quality degradation. If a jitter buffer is
 * too large then the additional delay can lead to conversational difficulty.
 * 
 * A typical jitter buffer configuration is 30mS to 50mS in size. In the case of
 * an adaptive jitter buffer then the maximum size may be set to 100-200mS. Note
 * that if the jitter buffer size exceeds 100mS then the additional delay
 * introduced can lead to conversational difficulty.
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public class JitterBuffer implements Serializable {

	private static final Logger logger = Logger.getLogger(JitterBuffer.class);

	private int maxSize;
	private int period;
	private int jitter;
	private ConcurrentLinkedQueue<byte[]> queue = new ConcurrentLinkedQueue<byte[]>();

	private BufferFactory bufferFactory = new BufferFactory(10, "ReceiverBuffer");

	private int payloadType = -1;

	private Format format;

	Map<Integer, Format> rtpMap;

	private int packetSize = -1;

	private volatile boolean ready = false;

	private static final int MAX_ROUNDS = 3;

	private volatile byte[] leftOver = null;

	List<byte[]> rawData = new ArrayList<byte[]>();
	byte[] data = null;
	int dataLength = 0;

	private JitterBuffer() {

	}

	/**
	 * Creates new instance of jitter.
	 * 
	 * @param fmt
	 *            the format of the received media
	 * @param jitter
	 *            the size of the jitter in milliseconds.
	 */
	public JitterBuffer(int jitter, int period, Map<Integer, Format> rtpMap) {
		this.maxSize = 2 * jitter / period;
		this.period = period;
		this.jitter = jitter;
		this.rtpMap = rtpMap;
	}

	public int getJitter() {
		return jitter;
	}

	public void setPeriod(int period) {
		this.period = period;
		maxSize = jitter / period;
	}

	public void write(byte[] data) {
		if (queue.size() < this.maxSize) {
			queue.offer(data);
		}

		if (!ready && queue.size() >= this.maxSize / 2) {
			ready = true;
		}
	}

	public void reset() {
		queue.clear();
	}

	private int getPacketSize(Format format) {
		int packetSize = 160;
		if (format.matches(Codec.LINEAR_AUDIO)) {
			packetSize = 320;
		} else if ((format.matches(Codec.PCMA)) || (format.matches(Codec.PCMU)) || (format.matches(Codec.SPEEX))
				|| (format.matches(Codec.G729))) {
			packetSize = 160;
		} else if (format.matches(Codec.GSM)) {
			packetSize = 33;
		} else if (format.matches(Codec.L16_MONO)) {
			packetSize = 1764;
		} else if (format.matches(Codec.L16_STEREO)) {
			packetSize = 3528;
		}
		return packetSize;
	}


	private byte[] concatByteList(List<byte[]> byteArrList, int totalLen) {
		byte[] concatedData = new byte[totalLen];
		int offset = 0;
		for (byte[] b : byteArrList) {
			int t = b.length;
			System.arraycopy(b, 0, concatedData, offset, t);
			offset += t;
		}
		return concatedData;
	}

	// TODO : Profile this. Optimization needed
	public Buffer read() {
		if (!ready) {
			return null;
		}

		if (!queue.isEmpty()) {

			rawData.clear();
			data = null;
			dataLength = 0;

			try {

				while (dataLength < 12) {

					if (leftOver != null) {
						data = leftOver;
						leftOver = null;
					} else {
						data = queue.poll();
						if (data == null) {
							leftOver = concatByteList(rawData, dataLength);
							return null;
						}
					}

					dataLength += data.length;
					rawData.add(data);
				}

				data = concatByteList(rawData, dataLength);

				// data = queue.poll();

				int dataCount = data.length - 12;

				// allocating media buffer using factory
				Buffer buffer = bufferFactory.allocate(true);
				RtpHeader header = (RtpHeader) buffer.getHeader();
				header.init(data);

				// assign format.
				// if payload not changed use the already known format
				if (payloadType != header.getPayloadType()) {
					payloadType = header.getPayloadType();
					format = rtpMap.get(payloadType);
					if (format == null) {
						logger.error("There is no Format defined for PayloadType = " + payloadType
								+ " returning null from JitterBuffer.read()");
						buffer.dispose();
						return null;

					}
					packetSize = getPacketSize(format);
				}

				// the length of the payload is total length of the
				// datagram except RTP header which has 12 bytes in length
				int legth = Math.min(dataCount, packetSize);
				System.arraycopy(data, 12, (byte[]) buffer.getData(), 0, legth);

				if (dataCount > packetSize) {
					int willRemain = dataCount - packetSize;
					leftOver = new byte[willRemain];
					System.arraycopy(data, packetSize, leftOver, 0, willRemain);

				} else {

					while ((dataCount < packetSize)) {
						byte[] newData = queue.poll();

						if (newData == null) {
							leftOver = data;
							buffer.dispose();
							return null;
						}

						int newDataLen = newData.length;
						if (dataCount + newDataLen > packetSize) {

							int willConsume = packetSize - dataCount;
							int willRemain = newDataLen - willConsume;

							leftOver = new byte[willRemain];

							System.arraycopy(newData, willConsume, leftOver, 0, willRemain);
							newDataLen = willConsume;
						}

						byte[] oldData = (byte[]) buffer.getData();
						System.arraycopy(newData, 0, oldData, dataCount, newDataLen);
						dataCount += newDataLen;
					}
				}				

				buffer.setLength(packetSize);
				buffer.setFormat(format);
				// receiveStream.push(buffer);
				return buffer;
			} catch (Exception e) {
				logger.error("The JitterBuffer read() is failing " + e);
			}
		} // end of if
		return null;

	}
}
