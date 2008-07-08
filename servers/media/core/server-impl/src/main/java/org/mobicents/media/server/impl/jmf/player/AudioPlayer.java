/*
 * BaseConnection.java
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

package org.mobicents.media.server.impl.jmf.player;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.Utils;
import org.mobicents.media.server.impl.common.events.PlayerEventType;
import org.xiph.speex.spi.SpeexAudioFileReader;

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

/**
 *
 * @author Oleg Kulikov
 */
public class AudioPlayer {
    private final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    
    protected AudioFormat format = LINEAR;
    private List<PlayerListener> listeners = Collections.synchronizedList(new ArrayList());
    private PushBufferAudioStream audioStream;
    
    protected Timer timer;
    private QueuedExecutor eventService = new QueuedExecutor();
    
    protected int packetPeriod;
    
    public AudioPlayer(Timer timer, int packetPeriod) {
        this.timer = timer;
        this.packetPeriod = packetPeriod;
    }
    
    /**
     * Gets the audio format of the output media.
     * The default output format is 8000Hz, 16bit linear audio,
     * 
     * @return the object describing audio format.
     */
    public AudioFormat getFormat() {
        return format;
    }
    
    /**
     * Modifies format of the output audio signal.
     * The modified format will be applied for next signals.
     * 
     * @param format the new format of the output media signal.
     */
    public void setFormat(AudioFormat format) {
        this.format = format;
    }
        
    /**
     * Adds player state listener.
     * 
     * @param listener instance of the listener to be added.
     */
    public void addListener(PlayerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes player state listener.
     * 
     * @param listener instance of the listener to be removed.
     */
    public void removeListener(PlayerListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Starts playing audio from specified file.
     * 
     * 
     * @param file the string url which points to a file to be played.     * 
     * @return the output stream object.
     * 
     * @throws java.net.MalformedURLException
     * @throws java.io.IOException
     * @throws javax.media.NoDataSourceException
     * @throws javax.media.NoProcessorException
     * @throws javax.media.CannotRealizeException
     */
    public PushBufferStream start(String file) throws MalformedURLException, 
            IOException, UnsupportedAudioFileException, UnsupportedFormatException {
        
        URL url = new URL(file);        
        
        AudioInputStream stream = null;
        
        //speex support
        try{
        	stream = AudioSystem.getAudioInputStream(url);
        } catch(UnsupportedAudioFileException unSupAudio){
        	unSupAudio.printStackTrace();
        }
        
        if(stream == null ){
        	SpeexAudioFileReader speexAudioFileReader = new SpeexAudioFileReader();
        	stream = speexAudioFileReader.getAudioInputStream(url);
        }
        
        audioStream = new PushBufferAudioStream(this, stream);
        audioStream.start();
        
        return audioStream;
    }
    
    /**
     * Terminates player.
     */
    public void stop() {
        if (audioStream != null) {
            audioStream.stop();
        }
    }

    /**
     * Called when player failed.
     */
    protected void failed(Exception e) {
        PlayerEvent evt = new PlayerEvent(this, PlayerEventType.FACILITY_ERROR, Utils.doMessage(e));
        try {
            eventService.execute(new EventHandler(evt));
        } catch (InterruptedException ex) {
        }
    }
    
    /**
     * Called when player stopped.
     */
    protected void stopped() {
        PlayerEvent evt = new PlayerEvent(this, PlayerEventType.STOP_BY_REQUEST, null);
        try {
            eventService.execute(new EventHandler(evt));
        } catch (InterruptedException ex) {
        }
    }
    
    /**
     * Called when player started to transmitt audio.
     */
    protected void started() {
        PlayerEvent evt = new PlayerEvent(this, PlayerEventType.STARTED, null);
        try {
            eventService.execute(new EventHandler(evt));
        } catch (InterruptedException ex) {
        }
    }
    
    /**
     * Called when player reached end of audio stream.
     */
    protected void endOfMedia() {
        PlayerEvent evt = new PlayerEvent(this, PlayerEventType.END_OF_MEDIA, null);
        try {
            eventService.execute(new EventHandler(evt));
        } catch (InterruptedException ex) {
        }
    }
    
    /**
     * Implements async event sender
     */
    private class EventHandler implements Runnable {
        private PlayerEvent evt;
        
        public EventHandler(PlayerEvent evt) {
            this.evt = evt;
        }
        
        public void run() {
            for (PlayerListener listener : listeners) {
                listener.update(evt);
            }
        }
    }
}
