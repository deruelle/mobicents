/*
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
package org.mobicents.media.server.impl.jmf.mixer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 */
public class AudioMixer implements Serializable {
    
    private final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    
    private static int JITTER = 100;
    
    private Timer timer;
    private TimerTask mixer;
    
    private MixerOutputStream outputStream;
    private List<MixerInputStream> buffers = Collections.synchronizedList(new ArrayList());
    private boolean started = false;
    
    private AudioFormat fmt = LINEAR;
    
    private int packetSize;
    private int packetPeriod;
    
    private Logger logger = Logger.getLogger(AudioMixer.class);
    
    /** 
     * Creates a new instance of AudioMixer.
     * 
     * @param packetPeriod packetization period in milliseconds. 
     */
    public AudioMixer(int packetPeriod) {
        this.packetPeriod = packetPeriod;
        try {
            this.init();
        } catch (UnsupportedFormatException e) {
        }
    }

    /** 
     * Creates a new instance of AudioMixer.
     * 
     * @param packetPeriod packetization period in milliseconds. 
     * @param fmt format of the output stream.
     */
    public AudioMixer(int packetPeriod, AudioFormat fmt) throws UnsupportedFormatException {
        this.packetPeriod = packetPeriod;
        this.fmt = fmt;
        this.init();
    }
    
    /**
     * Initializes audio mixer.
     * 
     * @throws javax.media.format.UnsupportedFormatException
     */
    private void init() throws UnsupportedFormatException {
        this.packetSize = 16 * packetPeriod;        
        outputStream = new MixerOutputStream(fmt, packetPeriod);
    }
    
    public Timer getTimer() {
        return timer;
    }
    
    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    
    /**
     * Gets the numbers of input streams used for mixing.
     * 
     * @return the numbers of streams.
     */
    public int size() {
        return buffers.size();
    }

    /**
     * Adds specified stream to this mixer.
     * 
     * @param stream the stream for mixing.
     * @throws UnsupportedFormatException if format of the specified stream
     * can not be mixed or decoded.
     */
    public synchronized void addInputStream(PushBufferStream stream) throws UnsupportedFormatException {
        MixerInputStream buffer = new MixerInputStream(stream, JITTER);
        buffers.add(buffer);
    }

    /**
     * Remove specified stream from mixer.
     * 
     * @param stream the stream to be removed.
     */
    public synchronized void removeInputStream(PushBufferStream stream) {
        MixerInputStream buff = null;
        for (MixerInputStream buffer : buffers) {
            if (buffer.stream == stream) {
                buff = buffer;
                break;
            }
        }

        if (buff != null) {
            buffers.remove(buff);
        }
    }

    /**
     * Gets the output stream which is a result of mixing input streams.
     * 
     * @return the result of the mixing of input streams.
     */
    public PushBufferStream getOutputStream() {
        return outputStream;
    }

    /**
     * Starts mixer.
     */
    public void start() {
        if (!started) {
            started = true;
            mixer = new Mixer();
            if (timer == null) {
                timer = new Timer();
            }
            timer.scheduleAtFixedRate(mixer, packetPeriod, packetPeriod);
        }
    }

    /**
     * Terminates mixer.
     */
    public void stop() {
        if (started) {
            started = false;
            mixer.cancel();
            timer.purge();
        }
    }
    
    /**
     * Implements mixing procedure for a buffer of audio.
     */
    private class Mixer extends TimerTask {
        public void run() {
            byte[] data = new byte[packetSize];

            ArrayList<byte[]> frames = new ArrayList();
            for (MixerInputStream buffer : buffers) {
                if (buffer.isReady()) {
                    frames.add(buffer.read(packetPeriod));
                } 
            }

            int l = 0;

            for (int i = 0; i < packetSize; i += 2) {
                short s = 0;
                for (byte[] frame : frames) {
                    if ((i + 1) < frame.length) {
                        s += (short) (((frame[i]) << 8) | (frame[i + 1] & 0xff));
                    }
                }
                data[l++] = (byte) (s >> 8);
                data[l++] = (byte) (s);
            }

            outputStream.push(data);
        }
    }
}
