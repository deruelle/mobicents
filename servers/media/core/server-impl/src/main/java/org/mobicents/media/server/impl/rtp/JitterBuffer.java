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
 * @author amit bhayani
 */
public class JitterBuffer implements Serializable {

    private int maxSize;
    private int depth;
    
    private int period;
    private int jitter;
    
    private BufferConcurrentLinkedQueue<RtpPacket> queue = new BufferConcurrentLinkedQueue();    
    private volatile boolean ready = false;

    
    /**
     * Creates new instance of jitter.
     * 
     * @param fmt
     *            the format of the received media
     * @param jitter
     *            the size of the jitter in milliseconds.
     */
    public JitterBuffer(int jitter, int period) {
        this.depth = jitter /period;
        this.maxSize = 4 * depth;
        this.period = period;
        this.jitter = jitter;
    }

    public int getJitter() {
        return jitter;
    }

    public void setPeriod(int period) {
        this.period = period;
        this.depth = jitter /period;
        maxSize = 4* jitter / period;
    }

    public void write(RtpPacket rtpPacket) {
        if (queue.size() < maxSize) {
            queue.offer(rtpPacket);
        }
        if (!ready && queue.size() > this.depth ) {
            ready = true;
        }
    }
    
    public void reset() {
        queue.clear();
    }

    public RtpPacket read() {
        return ready && !queue.isEmpty() ? queue.poll() : null;
    }
}
