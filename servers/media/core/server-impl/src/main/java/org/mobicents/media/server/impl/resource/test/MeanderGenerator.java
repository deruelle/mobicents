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

package org.mobicents.media.server.impl.resource.test;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.SyncSource;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 *
 * @author kulikov
 */
public class MeanderGenerator extends AbstractSource implements Runnable{

    private final static Format[] FORMATS = new Format[]{Codec.LINEAR_AUDIO};
    
    private double time;
    
    private double dt;
    private int pSize;
    
    private double T;
    private short A;
    
    public MeanderGenerator(String name, SyncSource syncSource) {
        super(name);
        setSyncSource(syncSource);
        
        //number of seconds covered by one sample
        dt = 1/Codec.LINEAR_AUDIO.getSampleRate();
    }
    
    public void setPeriod(double T) {
        this.T = T/2;
    }
    
    public void setAmplitude(short A) {
        this.A = A;
    }
    
    public Format[] getFormats() {
        return FORMATS;
    }

    private short getValue(double t) {
        return ((long)Math.floor(t/T)) % 2 == 0 ? A : 0;
    }
    
    public void evolve(Buffer buffer, long timestamp, long seq) {
        byte[] data = (byte[])buffer.getData();
        
        int k = 0;
        
        //packet size in samples
        pSize = (int)((double)getDuration()/1000.0/dt);
        for (int i = 0; i < pSize; i++) {
            short v = getValue(time + dt * i);
            data[k++] = (byte) v;
            data[k++] = (byte) (v >> 8);
        }
        
        buffer.setFormat(Codec.LINEAR_AUDIO);
        buffer.setSequenceNumber(seq++);
        buffer.setTimeStamp(getSyncSource().getTimestamp());
        buffer.setDuration(getDuration());
        buffer.setOffset(0);
        buffer.setLength(2* pSize);
        
        time += ((double)getDuration())/1000.0;
    }
}
