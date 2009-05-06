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

package org.mobicents.media.server.impl.enp.cnf;

import java.io.Serializable;

import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.MediaResource;
import org.mobicents.media.server.impl.dsp.Processor;
import org.mobicents.media.server.impl.resource.Demultiplexer;
import org.mobicents.media.server.spi.NotificationListener;

/**
 *
 * @author kulikov
 */
public class RxChannel implements Serializable {

    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    private final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, 8, 1);
    private final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, 8, 1);
    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");
    
    private final static Format f1[] = new Format[]{LINEAR_AUDIO, PCMA, PCMU, SPEEX, G729, GSM, DTMF};
    private final static Format f2[] = new Format[]{LINEAR_AUDIO, DTMF};
    
    private transient Processor dsp;
   // private DtmfDetector dtmfDet;
    protected Demultiplexer demux;
    
    private boolean active;
    private int index;
    
    public RxChannel(int index) {
        this.index = index;
        dsp = new Processor("rx-channel-" + index);
        demux = new Demultiplexer("Demultiplexer rx-channel-" + index);
        dsp.getOutput().connect(demux.getInput());
        dsp.configure(f1, f2);
        
//        dtmfDet = new DtmfDetector("");
//        dtmfDet.connect(demux);
        //demux.connect(dtmfDet);
    }
    
    public int getIndex() {
        return index;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public MediaSink getInput() {
        return dsp.getInput();
    }
    
    public void start() {
        active = true;
        dsp.getOutput().start();
        demux.start();
    }
    
    public void stop() {
        active = false;
        dsp.getOutput().stop();
        demux.stop();
    }
    
    public void addListener(NotificationListener listener) {
        //dtmfDet.addListener(listener);
    }
    
    public void removeListener(NotificationListener listener) {
        //dtmfDet.removeListener(listener);
    }
    
    public MediaSink getMediaSink(MediaResource id) {
//        if (id == MediaResource.DTMF_DETECTOR) {
//            return dtmfDet;
//        }
        return null;
    }
    
    public void close() {
        dsp.getOutput().disconnect(demux.getInput());
    }
}
