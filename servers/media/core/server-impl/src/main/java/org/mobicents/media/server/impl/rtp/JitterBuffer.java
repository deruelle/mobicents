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

    private final static int STATE_WRITTING_HEADER = 0;
    private final static int STATE_WRITTING_PAYLOAD = 1;
    private final static int STATE_WAITING = 3;
    
    private int maxSize;
    private int depth;
    
    private int period;
    private int jitter;
    
    // private ConcurrentLinkedQueue<byte[]> queue = new
    // ConcurrentLinkedQueue<byte[]>();
    private BufferConcurrentLinkedQueue<Buffer> queue = new BufferConcurrentLinkedQueue<Buffer>();
    private BufferFactory bufferFactory = new BufferFactory(10, "ReceiverBuffer");
    
    private Map<Integer, Format> rtpMap;
    private volatile boolean ready = false;

    private Buffer buffer;
    private int state = STATE_WAITING;
    private int pSize;
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
        this.depth = jitter /period;
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
        this.depth = jitter /period;
        maxSize = 4* jitter / period;
    }

    public void write(byte[] data, int offset, int len) {
        switch (state) {
            //at this state we are awaiting for first byte of RTP packet
            //upon receive packet allocate new buffer, create RTPHeader 
            //instance and fill header with received data
            case STATE_WAITING:
                buffer = bufferFactory.allocate();
                buffer.setHeader(new RtpHeader());
                state = STATE_WRITTING_HEADER;
            //populating header    
            case STATE_WRITTING_HEADER:
                RtpHeader header = (RtpHeader) buffer.getHeader();
                int remainder = header.append(data, offset, len);

                if (header.isFilled()) {
                    state = STATE_WRITTING_PAYLOAD;
                }

                if (remainder == 0) {
                    break;
                }

                offset = offset + (len - remainder);
                len = remainder;

                int pt = ((RtpHeader) buffer.getHeader()).getPayloadType();
                Format fmt = rtpMap.get(pt);

                if (fmt == null) {
                    state = STATE_WAITING;
                    buffer.dispose();
                    return;
                }

                buffer.setFormat(fmt);
                pSize = this.getPacketSize(fmt);
                state = STATE_WRITTING_PAYLOAD;
            case STATE_WRITTING_PAYLOAD:
                byte[] buff = (byte[]) buffer.getData();
                int bcount = Math.min(len, pSize - buffer.getLength());

                int pos = buffer.getOffset() + buffer.getLength();
                int length = buffer.getLength() + bcount;

                System.arraycopy(data, offset, buff, pos, bcount);
                buffer.setLength(length);

                if (length == pSize) {
                    if (queue.size() < maxSize) {
                        queue.offer(buffer);
                    }
                    state = STATE_WAITING;
                }

                remainder = len - bcount;
                if (remainder > 0) {
                    offset += bcount;
                    len = remainder;
                    write(data, offset, len);
                }
                break;
        }
        
        if (!ready && queue.size() > this.depth ) {
            ready = true;
        }
    }

    public void reset() {
        queue.clear();
    }

    private int getPacketSize(Format format) {
        int packetSize = 160;
        if ((format.matches(AVProfile.PCMA)) || (format.matches(AVProfile.PCMU)) || (format.matches(Codec.SPEEX)) || (format.matches(AVProfile.G729))) {
            packetSize = 160;
        } else if (format.matches(AVProfile.GSM)) {
            packetSize = 33;
        } else if (format.matches(AVProfile.L16_MONO)) {
            packetSize = 1764;
        } else if (format.matches(AVProfile.L16_STEREO)) {
            packetSize = 3528;
        }
        return packetSize;
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
