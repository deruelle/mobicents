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
import java.util.concurrent.ConcurrentLinkedQueue;
import org.mobicents.media.Buffer;

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
 */
public class JitterBuffer implements Serializable {

	private volatile boolean ready = false;
	private int jitter;
	private int seq = 0;
	private int period;
	private ConcurrentLinkedQueue<Buffer> queue = new ConcurrentLinkedQueue<Buffer>();
	private int maxSize;

	/**
	 * Creates new instance of jitter.
	 * 
	 * @param fmt
	 *            the format of the received media
	 * @param jitter
	 *            the size of the jitter in milliseconds.
	 */
	public JitterBuffer(int jitter, int period) {
		this.maxSize = 2 * jitter / period;
		this.period = period;
	}

	/**
	 * Gets the size of jitter.
	 * 
	 * @return the size of jitter in milliseconds
	 */
	public int getJitter() {
		return jitter;
	}

	public void setPeriod(int period) {
		this.period = period;
		maxSize = jitter / period;
	}

	public void write(Buffer buffer) {
		if (queue.size() == this.maxSize) {
			Buffer tmpbuffer = queue.remove();
			tmpbuffer.dispose();			
		}
		queue.offer(buffer);
		if (!ready && queue.size() >= this.maxSize / 2) {
			ready = true;
		}
	}

	public void reset() {
		queue.clear();
	}

	/**
	 * Reads media packet from jitter buffer.
	 * 
	 * @return media packet.
	 */
	public Buffer read() {
		if (!ready) {
			return null;
		}

		Buffer buff = null;
		if (!queue.isEmpty()) {
			buff = queue.poll();

			if (buff != null) {
				buff.setSequenceNumber(seq);
				buff.setTimeStamp(seq * period);
				buff.setDuration(period);
				buff.setOffset(0);
				seq++;
			}
		}
		return buff;
	}
}
