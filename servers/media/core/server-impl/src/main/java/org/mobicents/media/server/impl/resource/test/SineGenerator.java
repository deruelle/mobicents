/*
 * Generator.java
 *
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
package org.mobicents.media.server.impl.resource.test;

import java.util.concurrent.ScheduledFuture;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;

/**
 *
 * @author Oleg Kulikov
 */
public class SineGenerator extends AbstractSource implements Runnable {

    private byte[] data;
    
    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    private final static Format formats[] = new Format[] {LINEAR_AUDIO};
    
    private Timer timer;
    private ScheduledFuture worker;
    
    private int sizeInBytes;
    private int offset;
    private int seq;
    
    private int f;
    private short A = Short.MAX_VALUE;
    
    /** Creates a new instance of Generator */
    public SineGenerator(Endpoint endpoint, String name) {
    	super(name);
        this.timer = endpoint.getTimer();
    }

    public void setAmplitude(short A) {
        this.A = A;
    }
    
    public short getAmplitude() {
        return A;
    }
    
    public void setFrequency(int f) {
        this.f = f;
    }
    
    public int getFrequency() {
        return f;
    }
    
    public void start() {
        if (worker != null && !worker.isCancelled()) {
            worker.cancel(true);
        }
        
        data = new byte[(int)
                LINEAR_AUDIO.getSampleRate() * 
                LINEAR_AUDIO.getSampleSizeInBits()/8];

        sizeInBytes = (int) (LINEAR_AUDIO.getSampleRate() * 
                (LINEAR_AUDIO.getSampleSizeInBits() / 8)/1000 * 20); // Duration
        
        int len = data.length / 2;
        int k = 0;

        for (int i = 0; i < len; i++) {
            short s = (short) (A* Math.sin(2 * Math.PI * f * i / len));
            data[k++] = (byte) s;
            data[k++] = (byte) (s >> 8);
        }

        worker = timer.synchronize(this);
    }

    public void stop() {
        if (worker != null && !worker.isCancelled()) {
            worker.cancel(true);
        }
    }

    public void run() {
        byte[] media = new byte[sizeInBytes];

        int count = Math.min(data.length - offset, sizeInBytes);
        System.arraycopy(data, offset, media, 0, count);
        offset += count;
        if (offset == data.length) {
            offset = 0;
        }
        
        Buffer buffer = new Buffer();        
        buffer.setOffset(0);
        buffer.setLength(media.length);
        buffer.setSequenceNumber(seq);
        buffer.setDuration(timer.getHeartBeat());
        buffer.setTimeStamp(seq * timer.getHeartBeat()); 
        buffer.setData(media);
        buffer.setFormat(LINEAR_AUDIO);
        seq++;
        
        if (otherParty != null) {
            otherParty.receive(buffer);
        }
    }

    public Format[] getFormats() {
        return formats;
    }

    public void started() {
    }

    public void ended() {
    }

    
}
