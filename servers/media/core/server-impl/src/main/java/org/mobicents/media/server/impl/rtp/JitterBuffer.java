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
import java.util.Map;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
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

    private final static int STATE_WAITING = 3;
    private int maxSize;
    private int depth;
    private int period;
    private int jitter;    
    private BufferFactory bufferFactory = new BufferFactory(10, "ReceiveBuffer");
    private BufferConcurrentLinkedQueue<Buffer> queue = new BufferConcurrentLinkedQueue<Buffer>();
    private Map<Integer, Format> rtpMap;
    private volatile boolean ready = false;
    //private Buffer buffer;
    private long seq;

    /**
     * Creates new instance of jitter.
     * 
     * @param fmt
     *            the format of the received media
     * @param jitter
     *            the size of the jitter in milliseconds.
     */
    public JitterBuffer(int jitter, int period, Map<Integer, Format> rtpMap) {
        this.depth = jitter / period;
        this.maxSize = 4 * depth;
        this.period = period;
        this.jitter = jitter;
        this.rtpMap = rtpMap;
    }

    public int getJitter() {
        return jitter;
    }

    public void setPeriod(int period) {
        this.period = period;
        this.depth = jitter / period;
        maxSize = 4 * jitter / period;
    }

    public void write(byte[] data, int offset, int len) {
        Buffer buffer = bufferFactory.allocate();
        RtpHeader header = new RtpHeader();
        buffer.setHeader(header);

        int remainder = header.append(data, offset, len);
        offset = offset + (len - remainder);

        byte[] buff = (byte[]) buffer.getData();
        System.arraycopy(data, offset, buff, 0, remainder);
        buffer.setLength(remainder);

        int pt = header.getPayloadType();
        Format fmt = rtpMap.get(pt);

        buffer.setFormat(fmt);
        if (queue.size() < maxSize) {
            queue.offer(buffer);
        } else {
            Buffer b = queue.poll();
            b.dispose();
            queue.offer(buffer);
        }

        if (!ready && queue.size() > this.depth ) {
            ready = true;
        }        
    }

    public void reset() {
        queue.clear();
    }

    public Buffer read() {
        if (!ready) {
            return null;
        }

        if (!queue.isEmpty()) {
            Buffer buff = queue.poll();
            buff.setDuration(period);
            buff.setTimeStamp(period * seq);
            buff.setSequenceNumber(seq++);
            return buff;
        }
        return null;

    }
}
