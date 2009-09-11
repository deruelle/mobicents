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
package org.mobicents.media.server.impl.resource.audio;

import javax.sound.sampled.AudioFormat;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.resource.TTSEngine;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author kulikov
 */
public class TTSEngineImpl extends AbstractSource implements TTSEngine {

    private String text;
    private String url;
    
    private Format FORMATS[] = new Format[]{Codec.LINEAR_AUDIO};
    private Voice voice;
    private String voiceName;
    
    private boolean isReady = false;
    private int offset;
    private int length;
    
    private byte[] localBuff;
    private int pSize = 320;
    
    private long timestamp;
    
    public TTSEngineImpl(String name) {
        super(name);
    }

    public void setVoice(String name) {
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(voiceName);
        voice.allocate();
        voice.setAudioPlayer(new TTSAudioStream());
    }

    public String getVoice() {
        return voice != null? voice.getName() : null;
    }
    
    public void setURL(String url) {
        this.url = url;
    }
    
    public String getURL() {
        return url;
    }
    
    @Override
    public void beforeStart() {
        isReady = false;
        if (url != null) {
            try {
                URL uri = new URL(url);
                URLConnection connection = uri.openConnection();
                voice.speak(connection.getInputStream());
            } catch (IOException e) {
                failed(NotifyEvent.START_FAILED, e);
            }
            
        } else {
            voice.speak(text);
        }
    }

    @Override
    public void evolve(Buffer buffer, long timestamp, long sequenceNumber) {
        if (isReady) {
            buffer.setSequenceNumber(sequenceNumber);
            buffer.setFormat(Codec.LINEAR_AUDIO);
            buffer.setDuration(getDuration());
            buffer.setTimeStamp(getSyncSource().getTimestamp());
            buffer.setDiscard(false);
            
            byte[] data = (byte[]) buffer.getData();
            int len = Math.min(pSize, length - offset);
            
            System.arraycopy(localBuff, offset, data, 0, len);
            offset += len;
            
            buffer.setOffset(0);
            buffer.setLength(len);
            buffer.setEOM(offset == length);            
        } else {
            buffer.setDiscard(true);
        }
    }

    public Format[] getFormats() {
        return FORMATS;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
    private class TTSAudioStream implements com.sun.speech.freetts.audio.AudioPlayer {

        private AudioFormat fmt;
        private float volume;
        
        public void setAudioFormat(AudioFormat fmt) {
            this.fmt = fmt;
        }

        public AudioFormat getAudioFormat() {
            return fmt;
        }

        public void pause() {
        }

        public void resume() {
        }

        public void reset() {
            offset = 0;
            isReady = false;
        }

        public boolean drain() {
            return true;
        }

        public void begin(int size) {
            localBuff = new byte[size];
        }

        public boolean end() {
            length = offset;
            offset = 0;
            isReady = true;
            return true;
        }

        public void cancel() {
        }

        public void close() {
        }

        public float getVolume() {
            return volume;
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }

        public long getTime() {
            return 0;
        }

        public void resetTime() {
        }

        public void startFirstSampleTimer() {
        }

        public boolean write(byte[] buff) {
            return write(buff, 0, buff.length);
        }

        public boolean write(byte[] buff, int off, int len) {
            System.arraycopy(buff, off, localBuff, offset, len);
            offset += len;
            return true;
        }

        public void showMetrics() {
        }

    }
}
