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

import org.mobicents.media.server.impl.resource.cnf.AudioMixer;
import java.io.Serializable;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.MediaResource;
import org.mobicents.media.server.impl.resource.Multiplexer;
import org.mobicents.media.server.impl.dsp.Processor;
import org.mobicents.media.server.impl.events.announcement.AudioPlayer;
import org.mobicents.media.server.spi.NotificationListener;

/**
 *
 * @author kulikov
 */
public class TxChannel implements Serializable {
    
    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    private final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, 8, 1);
    private final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, 8, 1);
    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");
    
    private final static Format f1[] = new Format[]{PCMA, PCMU, SPEEX, G729, GSM, DTMF};
    private final static Format f2[] = new Format[]{LINEAR_AUDIO, DTMF};
    
    
    private transient Processor dsp;
    private Multiplexer mux;
    private AudioMixer mixer;
    private AudioPlayer player;
    
    private boolean active;
    private int index;
    
    
    public TxChannel(BaseEndpoint endpoint, int index) {
        this.index = index;
        
        dsp = new Processor("");
        mux = new Multiplexer("");
        mixer = new AudioMixer(endpoint, "tx-channel-" + index);
        player = new AudioPlayer(endpoint);
        
        mux.connect(mixer.getOutput());
        mixer.connect(player);
        
        dsp.getInput().connect(mux.getOutput());
        dsp.configure(f2, f1);
    }
    
    public int getIndex() {
        return index;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void attach(RxChannel rxChannel) {
        mixer.connect(rxChannel.demux);
    }
    
    public void deattach(RxChannel rxChannel) {
        mixer.disconnect(rxChannel.demux);
    }
    
    public void start() {
        active = true;
        mixer.start();
        dsp.getOutput().start();
        mux.getOutput().start();
    }
    
    public void stop() {
        active = false;
        dsp.getOutput().stop();
        mixer.stop();
        mux.getOutput().stop();
    }
    
    public void configure(Format[] f1, Format[] f2) {
        dsp.configure(f1, f2);
    }
    
    public void addListener(NotificationListener listener) {
        player.addListener(listener);
    }
    
    public void removeListener(NotificationListener listener) {
        player.removeListener(listener);
    }
    
    public void close() {
        dsp.getInput().disconnect(mux.getOutput());
        mux.disconnect(mixer.getOutput());
    }
    
    public MediaSource getSource() {
        return dsp.getOutput();
    }
    
    public MediaSource getMediaSource(MediaResource id) {
        if (id == MediaResource.AUDIO_PLAYER) {
            return player;
        }
        return null;
    }
}
