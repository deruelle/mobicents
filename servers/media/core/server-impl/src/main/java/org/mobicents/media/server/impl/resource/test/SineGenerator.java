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

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;

/**
 * Generates sine wave signal with specified Amplitude and frequence.
 *
 * The format of output signal is Linear, 16bit, 8kHz.
 * 
 * @author Oleg Kulikov
 */
public class SineGenerator extends AbstractSource implements Runnable {

    
    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    private final static Format FORMAT[] = new Format[] {LINEAR_AUDIO};
    
    private int f;
    private short A = Short.MAX_VALUE;
    
    private double dt;
    private int pSize;
    
    private double time;
    
    public SineGenerator(String name, Timer timer) {
        super(name);
        setSyncSource(timer);
        init();
    }
    
    /** Creates a new instance of Generator */
    public SineGenerator(Endpoint endpoint, String name) {
    	super(name);
        setSyncSource(endpoint.getTimer());
        init();
    }

    private void init() {
        //number of seconds covered by one sample
        dt = 1/LINEAR_AUDIO.getSampleRate();
        //packet size in samples
        pSize = (int)((double)getSyncSource().getHeartBeat()/1000.0/dt);
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
    
    private short getValue(double t) {
        return (short) (A* Math.sin(2 * Math.PI * f * t));
    }

    public void evolve(Buffer buffer, long seq) {
        byte[] data = (byte[])buffer.getData();
        
        int k = 0;
        
        for (int i = 0; i < pSize; i++) {
            short v = getValue(time + dt * i);
            data[k++] = (byte) v;
            data[k++] = (byte) (v >> 8);
        }
        
        buffer.setFormat(LINEAR_AUDIO);
        buffer.setSequenceNumber(seq);
        buffer.setTimeStamp(getSyncSource().getTimestamp());
        buffer.setDuration(getSyncSource().getHeartBeat());
        buffer.setOffset(0);
        buffer.setLength(2*pSize);
        
        time += ((double)getSyncSource().getHeartBeat())/1000.0;
    }

    public Format[] getFormats() {
        return FORMAT;
    }

}
