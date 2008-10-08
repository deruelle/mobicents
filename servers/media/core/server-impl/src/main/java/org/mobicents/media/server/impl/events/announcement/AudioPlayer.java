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
package org.mobicents.media.server.impl.events.announcement;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat.Encoding;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.CachedBuffersPool;
import org.mobicents.media.server.impl.clock.Quartz;
import org.mobicents.media.server.impl.clock.Timer;
import org.mobicents.media.server.spi.events.pkg.Announcement;
import org.xiph.speex.spi.SpeexAudioFileReader;

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
    protected Timer timer;
    private boolean started;
    private String file;
    private long time;
    private transient Logger logger = Logger.getLogger(AudioPlayer.class);
    
    private int count = 0;
    //protected int packetPeriod;
    public AudioPlayer() {
    	super("AudioPlayer");
        this.timer = new Timer();
        timer.setListener(this);
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
            synchronized (this) {
                stream = AudioSystem.getAudioInputStream(url);
            }
        } catch (Exception e) {
            System.out.println("Error " + stream);
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
        //worker = new Thread(this);
        //worker.setName("Audio Player");
        //worker.setPriority(Thread.MAX_PRIORITY);
        //worker.start();
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
            started = false;
            this.stopped();
        }
    }

    /**
     * Called when player failed.
     */
    protected void failed(Exception e) {
        AnnEventImpl evt = new AnnEventImpl(Announcement.FAILED);
        this.sendEvent(evt);
    }

    /**
     * Called when player stopped.
     */
    protected void stopped() {
        AnnEventImpl evt = new AnnEventImpl(Announcement.COMPLETED);
        this.sendEvent(evt);
    }

    /**
     * Called when player started to transmitt audio.
     */
    protected void started() {
        AnnEventImpl evt = new AnnEventImpl(Announcement.STARTED);
        this.sendEvent(evt);
    }

    /**
     * Called when player reached end of audio stream.
     */
    protected void endOfMedia() {
        started = false;
        AnnEventImpl evt = new AnnEventImpl(Announcement.COMPLETED);
        this.sendEvent(evt);
    }

    private int readPacket(byte[] packet) throws IOException {
        int offset = 0;
        while (offset < packetSize) {
            int len = stream.read(packet, offset, packetSize - offset);
            if (len == -1) {
                return -1;
            }
            offset += len;
        }
        return packetSize;
    }

    private void doProcess() {
        byte[] packet = new byte[packetSize];
        try {
            //int len = stream.read(packet);
            int len = readPacket(packet);
//            long now = System.currentTimeMillis();
//            if (now - time > 25) {
//                System.out.println("Delay= " + (now - time));
//            }
//            time = now;
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
        } catch (Exception e) {
            timer.stop();
            started = false;
            e.printStackTrace();
            failed(e);
        }
    }

    @SuppressWarnings("static-access")
    public void run() {
/*        while (started) {
            if (count == 2) {
                count = 0;
                doProcess();
            } else {
                count++;
                try {
                    Thread.currentThread().sleep(10);
                } catch (InterruptedException e) {
                    started = false;
                }
            }

        }
 */
        doProcess();
    }

    public Format[] getFormats() {
        return formats;
    }
}
