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
package org.mobicents.media.server.impl.events.ann;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.Utils;
import org.mobicents.media.server.impl.common.events.PlayerEventType;
import org.xiph.speex.spi.SpeexAudioFileReader;

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import javax.sound.sampled.AudioFormat.Encoding;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.CachedBuffersPool;
import org.mobicents.media.server.impl.clock.Quartz;
import org.mobicents.media.server.impl.clock.Timer;

/**
 *
 * @author Oleg Kulikov
 */
public class AudioPlayer extends AbstractSource implements Runnable {

    protected final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    protected AudioFormat format = LINEAR;
    protected final static Format[] formats = new Format[]{LINEAR};
    private AudioInputStream stream = null;
    private int packetSize;
    private long seq = 0;
    private List<PlayerListener> listeners = Collections.synchronizedList(new ArrayList());
    protected Timer timer;
    private boolean started;
    private String file;
    private QueuedExecutor eventService = new QueuedExecutor();
    private transient Logger logger = Logger.getLogger(AudioPlayer.class);
    //protected int packetPeriod;
    public AudioPlayer() {
        this.timer = new Timer();
        timer.setListener(this);
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

    public void setFile(String file) {
        this.file = file;
    }

    public void start() {
        this.start(file);
    }

    /**
     * Starts playing audio from specified file.
     * 
     * 
     * @param file the string url which points to a file to be played.     * 
     * 
     * @throws java.net.MalformedURLException
     * @throws java.io.IOException
     * @throws javax.media.NoDataSourceException
     * @throws javax.media.NoProcessorException
     * @throws javax.media.CannotRealizeException
     */
    public void start(String file) {

        URL url = null;
        try {
            url = new URL(file);
        } catch (IOException e) {
            this.failed(e);
            return;
        }

        //speex support
        try {
            stream = AudioSystem.getAudioInputStream(url);
        } catch (Exception e) {
            this.failed(e);
            return;
        }

        if (stream == null) {
            SpeexAudioFileReader speexAudioFileReader = new SpeexAudioFileReader();
            try {
                stream = speexAudioFileReader.getAudioInputStream(url);
            } catch (Exception e) {
                this.failed(e);
                return;
            }
        }

        AudioFormat fmt = new AudioFormat(getEncoding(stream.getFormat().getEncoding()), stream.getFormat().getFrameRate(), stream.getFormat().getFrameSize() * 8, stream.getFormat().getChannels());
        packetSize = (int) ((fmt.getSampleRate() / 1000) * (fmt.getSampleSizeInBits() / 8) * Quartz.HEART_BEAT);

        this.timer.start();
        this.started = true;
        this.started();
    }

    private String getEncoding(Encoding encoding) {
        if (encoding == Encoding.ALAW) {
            return "ALAW";
        } else if (encoding == Encoding.ULAW) {
            return "ULAW";
        } else if (encoding == Encoding.PCM_SIGNED) {
            return "LINEAR";
        } else if (encoding == Encoding.PCM_UNSIGNED) {
            return "LINEAR";
        } else {
            return null;
        }
    }

    /**
     * Terminates player.
     */
    public void stop() {
        timer.stop();
        if (started) {
            this.stopped();
            started = false;
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
        started = false;
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

    public void run() {
        byte[] packet = new byte[packetSize];
        try {
            int len = stream.read(packet);
            if (len == -1) {
                timer.stop();
                if (started) {
                    this.endOfMedia();
                }
            } else {
                Buffer buffer = CachedBuffersPool.allocate();
                buffer.setData(packet);
                buffer.setDuration(Quartz.HEART_BEAT);
                buffer.setLength(len);
                buffer.setOffset(0);
                buffer.setFormat(format);
                buffer.setTimeStamp(seq * Quartz.HEART_BEAT);
                buffer.setEOM(false);
                buffer.setSequenceNumber(seq++);

                if (sink != null) {
                    sink.receive(buffer);
                }

            }
        } catch (IOException e) {
            timer.stop();
            started = false;
            e.printStackTrace();
            failed(e);
        }
    }

    public Format[] getFormats() {
        return formats;
    }
}
