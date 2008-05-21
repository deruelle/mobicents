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
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

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
    
    protected int packetPeriod;

    public AudioPlayer(int packetPeriod) {
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
        audioStream = new PushBufferAudioStream(this, AudioSystem.getAudioInputStream(url));
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
        PlayerEvent evt = new PlayerEvent(this, PlayerEvent.FACILITY_ERROR, e.getMessage());
        new Thread(new EventQueue(evt)).start();
    }
    
    /**
     * Called when player stopped.
     */
    protected void stopped() {
        PlayerEvent evt = new PlayerEvent(this, PlayerEvent.STOP_BY_REQUEST, null);
        new Thread(new EventQueue(evt)).start();
    }
    
    /**
     * Called when player started to transmitt audio.
     */
    protected void started() {
        PlayerEvent evt = new PlayerEvent(this, PlayerEvent.STARTED, null);
        new Thread(new EventQueue(evt)).start();
    }
    
    /**
     * Called when player reached end of audio stream.
     */
    protected void endOfMedia() {
//        audioStream.stop();
        PlayerEvent evt = new PlayerEvent(this, PlayerEvent.END_OF_MEDIA, null);
        new Thread(new EventQueue(evt)).start();
    }
    
    /**
     * Implements async event sender
     */
    private class EventQueue implements Runnable {
        private PlayerEvent evt;
        
        public EventQueue(PlayerEvent evt) {
            this.evt = evt;
        }
        
        public void run() {
            for (PlayerListener listener : listeners) {
                listener.update(evt);
            }
        }
    }
}
