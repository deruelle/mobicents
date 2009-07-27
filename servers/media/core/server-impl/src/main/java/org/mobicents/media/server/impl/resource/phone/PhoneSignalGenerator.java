/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.impl.resource.phone;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.Timer;

/**
 * Generates sine wave signal with specified Amplitude and frequence.
 *
 * The format of output signal is Linear, 16bit, 8kHz.
 * 
 * @author Oleg Kulikov
 */
public class PhoneSignalGenerator extends AbstractSource  {

    
    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    private final static Format FORMAT[] = new Format[] {LINEAR_AUDIO};
    
    private int[] f;
    private short A = Short.MAX_VALUE;
    
    private double dt;
    private int pSize;
    
    private double time;
    private double elapsed;
    private double duration;
    private double value = 1;
    
    private int[] T;
    
    public PhoneSignalGenerator(String name, Timer timer) {
        super(name);
        setSyncSource(timer);
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
    
    public void setFrequency(int[] f) {
        this.f = f;
    }
    
    public int[] getFrequency() {
        return f;
    }
    
    public void setPeriods(int[] T) {
        this.T = T;
        duration = T[0];
    }
    
    public int[] getPeriods() {
        return T;
    }
    
    private short getValue(double t) {
        elapsed += dt;
        if (elapsed > duration) {
            if (value == 0) {
                value = 1;
                duration = T[0];
            } else {
                value = 0;
                duration = T[1];
            }
            elapsed = 0;
        }
        if (value == 0) {
            return 0;
        }
        
        double v = 0;
        for (int i = 0; i < f.length; i++) {
            v += Math.sin(2 * Math.PI * f[i] * t);
        }
        return (short)(v * A);
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
