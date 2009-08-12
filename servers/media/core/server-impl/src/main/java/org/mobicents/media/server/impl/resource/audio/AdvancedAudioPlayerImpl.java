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

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.resource.audio.AdvancedAudioPlayer;

/**
 *
 * @author kulikov
 */
public class AdvancedAudioPlayerImpl extends AbstractSource implements AdvancedAudioPlayer {

    private AudioPlayerImpl audioPlayer;
    private TTSEngineImpl ttsEngine;
    
    private AbstractSource engine;
    
    public AdvancedAudioPlayerImpl(String name, Timer timer) {
        super(name);
        audioPlayer = new AudioPlayerImpl(name + "[AudioPlayer]", timer);
        ttsEngine = new TTSEngineImpl(name + "[TTS]");
        ttsEngine.setSyncSource(timer);
    }
    
    @Override
    public void start() {
        engine.start();
    }

    @Override
    public void stop() {
        engine.stop();
    }
    
    @Override
    public void setEndpoint(Endpoint endpoint) {
        audioPlayer.setEndpoint(endpoint);
        ttsEngine.setEndpoint(endpoint);
    }
    
    @Override
    public void setConnection(Connection connection) {
        audioPlayer.setConnection(connection);
        ttsEngine.setConnection(connection);
    }
    
    @Override
    public void evolve(Buffer buffer, long sequenceNumber) {
        engine.evolve(buffer, sequenceNumber);
    }

    public Format[] getFormats() {
        return engine.getFormats();
    }

    public void setText(String text) {
        engine = ttsEngine;
        ttsEngine.setText(text);
    }

    public void setURL(String url) {
        engine = audioPlayer;
        audioPlayer.setURL(url);
    }

    public String getURL() {
        return audioPlayer.getURL();
    }

    public void setVoice(String name) {
        ttsEngine.setVoice(name);
    }

}
