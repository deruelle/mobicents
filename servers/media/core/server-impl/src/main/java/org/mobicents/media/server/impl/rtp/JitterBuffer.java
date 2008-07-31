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

import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;

/**
 * Implements jitter buffer.
 *  
 * A jitter buffer temporarily stores arriving packets in order to minimize 
 * delay variations. If packets arrive too late then they are discarded. 
 * A jitter buffer may be mis-configured and be either too large or too small. 
 *
 * If a jitter buffer is too small then an excessive number of packets may be 
 * discarded, which can lead to call quality degradation. If a jitter buffer is 
 * too large then the additional delay can lead to conversational difficulty.
 *
 * A typical jitter buffer configuration is 30mS to 50mS in size. 
 * In the case of an adaptive jitter buffer then the maximum size may be set 
 * to 100-200mS. Note that if the jitter buffer size exceeds 100mS then the 
 * additional delay introduced can lead to conversational difficulty.
 *
 * @author Oleg Kulikov
 */
public class JitterBuffer {

    private byte[] buffer;
    private int pos = 0;
    private int threshold;
    private boolean ready = false;
    private Format fmt;
    private int jitter;
    private int seq = -1;
    private int period;
    private int packetSize;
    
    /**
     * Creates new instance of jitter.
     * 
     * @param fmt the format of the received media
     * @param jitter the size of the jitter in milliseconds.
     */
    public JitterBuffer(Format fmt, int jitter, int period) {
        this.fmt = fmt;
        setJitter(jitter);
        setPeriod(period);
    }

    /**
     * Gets the size of jitter.
     * 
     * @return the size of jitter in milliseconds
     */
    public int getJitter() {
        return jitter;
    }

    /**
     * Modify jitter's size.
     * 
     * @param size the new jitter's size in ms.
     */
    public void setJitter(int size) {
        this.jitter = size;
        this.threshold = getSizeInBytes(fmt, size);
        
        if (buffer == null) {
            buffer = threshold != 0 ? new byte[3 * this.threshold] : new byte[200];
        }
    }

    public void setPeriod(int period) {
        this.period = period;
        this.packetSize = getSizeInBytes(fmt, period);
    }
    
    public boolean isReady() {
        return pos > threshold;
    }
    
    private int getSizeInBytes(Format fmt, int size) {
        //samples per millisecond
        int s = (int) ((AudioFormat) fmt).getSampleRate() / 1000;
        int sampleSize = ((AudioFormat) fmt).getSampleSizeInBits() / 8;

        return sampleSize != 0 ? (int) s * size / sampleSize : 0;
    }

    public void push(int seq, byte[] data) {
        if (this.seq == seq) {
            //duplicate packet
            return;
        }
        
        this.seq = seq;
        
        synchronized (this) {
            if (pos + data.length > buffer.length) {
                return;
            }
//            while (pos + data.length > buffer.length) {
//                try {
//                    wait();
//                } catch (InterruptedException ex) {
//                }
//            }

            System.arraycopy(data, 0, buffer, pos, data.length);
            pos += data.length;

            ready = pos >= threshold;
            if (ready) {
                notify();
            }
        }
    }

    public byte[] next() throws InterruptedException {
        return packetSize != 0 ? next(this.packetSize) : next(this.pos);
    }

    public byte[] next(int count) throws InterruptedException {
        synchronized (this) {
            if (!ready) {
                wait(jitter);
            }

            byte[] data = null;
            int len = Math.min(count, pos);
            if (len > 0) {
                data = new byte[len];

                System.arraycopy(buffer, 0, data, 0, len);
                System.arraycopy(buffer, len, buffer, 0, buffer.length - len);
                pos -= len;

                notify();
            } else {
                data = new byte[count];
            }
            
            ready = false;
            return data;
        }
    }
}
