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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
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
	// private ConcurrentLinkedQueue<byte[]> queue = new
	// ConcurrentLinkedQueue<byte[]>();
	private BufferConcurrentLinkedQueue<byte[]> queue = new BufferConcurrentLinkedQueue<byte[]>();

	private BufferFactory bufferFactory = new BufferFactory(10, "ReceiverBuffer");

	private int payloadType = -1;

	private Format format;

	Map<Integer, Format> rtpMap;

	private int packetSize = -1;

	private volatile boolean ready = false;

	private static final int MAX_ROUNDS = 3;

	private volatile transient Buffer currentBuffer = bufferFactory.allocate(true);
	private volatile transient boolean initializedHeader = false;

	private volatile transient ByteArrayOutputStream payloadDataStream = new ByteArrayOutputStream();

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
		} else if ((format.matches(Codec.PCMA)) || (format.matches(Codec.PCMU)) || (format.matches(Codec.SPEEX)) || (format.matches(Codec.G729))) {
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

			try {
				// here we can have full, header, part header, part herader with
				// data, etc
				Buffer toReturn = null;
				RtpHeader header = (RtpHeader) this.currentBuffer.getHeader();
				if (!this.initializedHeader) {

					// we get two while loops,
					while (!this.initializedHeader) {
						// here, if there is something in queue we trye to
						// create header, if there is nothing, we return,
						// while is because we can header in more than one chunk
						// :)
						int leftOverBytes = -1;
						byte[] presumableHeaderData = null;

						while (leftOverBytes <0) {
							presumableHeaderData = queue.poll();
							if (presumableHeaderData == null) {
								// just in case:
								return null;
							}
							leftOverBytes = header.initPartialy(presumableHeaderData);
						}
						this.initializedHeader = true;
						// now we have some bytes left.
						if (leftOverBytes > 0 && presumableHeaderData != null)
							storeLeftOver(presumableHeaderData, leftOverBytes);

					}

					toReturn = initBufferPayload(header);
				} else {
					// we want buffer to return
					toReturn = initBufferPayload(header);
					
				}

				return toReturn;

			} catch (Exception e) {
				logger.error("The JitterBuffer read() is failing " + e);
			}
		} // end of if
		return null;

	}

	private Buffer initBufferPayload(RtpHeader header) throws IOException{
		// Here we must init payload, we return ready buffer and allocate
		// another.

		if (payloadType != header.getPayloadType()) {
			payloadType = header.getPayloadType();
			format = rtpMap.get(payloadType);
			if (format == null) {
				logger.error("There is no Format defined for PayloadType = " + payloadType + " returning null from JitterBuffer.read()");
				
				cleanBuffers();
				return null;

			}
		}
			packetSize = getPacketSize(format);
			int loadedData = this.payloadDataStream.size();

			while (this.payloadDataStream.size() != packetSize) {
				byte[] dataToStream = queue.poll();
				if (dataToStream == null) {
					return null;
				}

				if (loadedData + dataToStream.length > packetSize) {
					// here we might want to start init of another buffer, but
					// for now its ok. lets see results first.
					storeLeftOver(dataToStream, loadedData + dataToStream.length - packetSize);
					byte[] tmp = new byte[packetSize-loadedData];
					System.arraycopy(dataToStream, 0, tmp, 0, tmp.length);
					dataToStream = tmp;
				}

				loadedData += dataToStream.length;
				this.payloadDataStream.write(dataToStream);

				// we are ready :)
				if (loadedData == packetSize) {
					Buffer toReturn = this.currentBuffer;
					this.currentBuffer = this.bufferFactory.allocate(true);
					toReturn.setData(this.payloadDataStream.toByteArray());
					this.payloadDataStream.reset();
					this.initializedHeader = false;
					toReturn.setLength(packetSize);
					toReturn.setFormat(format);
					return toReturn;
				}
			}
		

		return null;
	}

	private void cleanBuffers() {
		this.currentBuffer.dispose();
		this.currentBuffer = bufferFactory.allocate(true);
		this.payloadDataStream.reset();
	}

	private void storeLeftOver(byte[] presumableHeaderData, int leftOverBytes) {
		byte[] leftOver = new byte[leftOverBytes];
		System.arraycopy(presumableHeaderData, presumableHeaderData.length - leftOverBytes, leftOver, 0, leftOverBytes);
		this.queue.storeAtHead(leftOver);
	}
}
