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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat.Encoding;

import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.BaseEndpoint;

import org.xiph.speex.spi.SpeexAudioFileReader;
import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 */
public class AudioPlayer extends AbstractSource {

    private final static int MAX_ERRORS = 5;
    /** supported formats definition */
    private final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, 8, 1);
    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    private final static Format[] formats = new Format[]{LINEAR, PCMA, PCMU, SPEEX, GSM};
    /** format of the file */
    private AudioFormat format;
    /** audio stream */
    private transient AudioInputStream stream = null;
    /** the size of the packet in bytes */
    private int packetSize;
    /** packetization period in millisconds  */
    private int period = 20;
    /** sequence number of the packet */
    private long seq = 0;
    /** Timer     */
    private transient ScheduledExecutorService timer;
    private transient Future worker;
    /** Command executor service used for async command executions */
    private transient ExecutorService executor;
    private transient ThreadFactory threadFactory;
    /** Name (path) of the file to play */
    private String file;
    /** Flag indicating end of media */
    private volatile boolean eom = false;
    /** The countor for errors occured during processing */
    private int errorCount;
    private volatile boolean started = false;
    private BufferFactory bufferFactory = null;
    private transient Logger logger = Logger.getLogger(AudioPlayer.class);

    public AudioPlayer(BaseEndpoint endpoint) {
        super("AudioPlayer[" + endpoint.getLocalName() + "]");
        bufferFactory = new BufferFactory(10, "AudioPlayer[" + endpoint.getLocalName() + "]");
        this.timer = endpoint.getTransmittorThread();
        threadFactory = new ThreadFactoryImpl("AudioPlayerCommand[" + endpoint.getLocalName() + "]");
        executor = Executors.newSingleThreadExecutor(threadFactory);
    }

    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Sarts playback. Executes asynchronously.
     */
    public void start() {
        if (file != null) {
            executor.submit(new StartCommand());
        }
    }

    /**
     * Terminates player. Methods executes asynchronously.
     */
    public void stop() {
        executor.submit(new StopCommand());
    }

    /**
     * Gets the format of specified stream.
     * 
     * @param stream the stream to obtain format.
     * @return the format object.
     */
    private AudioFormat getFormat(AudioInputStream stream) {
        Encoding encoding = stream.getFormat().getEncoding();
        if (encoding == Encoding.ALAW) {
            return new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
        } else if (encoding == Encoding.ULAW) {
            return new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
        } else if (encoding == Encoding.PCM_SIGNED) {
            return LINEAR;
        } else if (encoding == Encoding.PCM_UNSIGNED) {
            return new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.UNSIGNED);
        } else {
            return null;
        }
    }

    /**
     * Calculates size of packets for the currently opened stream.
     * @return the size of packets in bytes;
     */
    private int getPacketSize() {
        return format.getEncoding().equals(AudioFormat.LINEAR) ? 320 : 160;
    }

    /**
     * Called when player failed.
     */
    protected void failed(Exception e) {
//        AnnEventImpl evt = new AnnEventImpl(Announcement.FAILED);
//        this.sendEvent(evt);
    }

    /**
     * Called when player started to transmitt audio.
     */
    public void started() {
//        AnnEventImpl evt = new AnnEventImpl(Announcement.STARTED);
//        this.sendEvent(evt);
    }

    /**
     * Called when player reached end of audio stream.
     */
    protected void endOfMedia() {
//        AnnEventImpl evt = new AnnEventImpl(Announcement.COMPLETED);
//        this.sendEvent(evt);
    }

    /**
     * Reads packet from currently opened stream.
     * 
     * @param packet the packet to read
     * @param offset the offset from which new data will be inserted
     * @return the number of actualy read bytes.
     * @throws java.io.IOException 
     */
    private int readPacket(byte[] packet, int offset) throws IOException {
        int length = 0;
        while (length < packetSize) {
            int len = stream.read(packet, offset + length, packetSize - length);
            if (len == -1) {
                return length;
            }
            length += len;
        }
        return length;
    }

    /**
     * Reads packet from currently opened stream into specified buffer.
     * 
     * @param buffer the buffer object to insert data to
     * @throws java.io.IOException
     */
    private void readPacket(Buffer buffer) throws IOException {
        buffer.setLength(readPacket((byte[]) buffer.getData(), buffer.getOffset()));
    }

    /**
     * Perform padding buffer with zeros to aling length.
     * 
     * @param buffer the buffer for padding.
     */
    private void padding(Buffer buffer) {
        int count = packetSize - buffer.getLength();
        byte[] data = (byte[]) buffer.getData();
        int offset = buffer.getOffset() + buffer.getLength();
        for (int i = 0; i <
                count; i++) {
            data[i + offset] = 0;
        }

        buffer.setLength(packetSize);
    }

    /**
     * Closes audio stream
     */
    private void closeAudioStream() {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
        }
    }

    private void doProcess1() {
        Buffer buffer = bufferFactory.allocate();
        try {
            readPacket(buffer);
        } catch (IOException e) {
            failed(e);
            worker.cancel(true);
            return;
        }

        if (buffer.getLength() == 0) {
            eom = true;
        } else if (buffer.getLength() < packetSize) {
            padding(buffer);
        }

        buffer.setDuration(period);
        buffer.setFormat(format);
        buffer.setTimeStamp(seq * period);
        buffer.setEOM(eom);
        buffer.setSequenceNumber(seq++);

        try {
            otherParty.receive(buffer);
            errorCount = 0;
            if (eom) {
                worker.cancel(true);
                started = false;
                ended();
            }
        } catch (Exception e) {
            errorCount++;
            if (errorCount == MAX_ERRORS) {
                failed(e);
                worker.cancel(true);
            }
        }
    }

    public Format[] getFormats() {
        return formats;
    }

    public void ended() {
        closeAudioStream();
//        AnnEventImpl evt = new AnnEventImpl(Announcement.COMPLETED);
//        this.sendEvent(evt);
    }

    private class Player implements Runnable {

        public void run() {
            doProcess1();
        }
    }

    private class StartCommand implements Runnable {

        @SuppressWarnings("static-access")
        public void run() {
            if (started) {
                return;
            }
            closeAudioStream();
            seq = 0;
            try {
                if (file.endsWith("spx")) {
                    stream = new SpeexAudioFileReader().getAudioInputStream(new URL(file));
                } else {
                    stream = AudioSystem.getAudioInputStream(new URL(file));
                }
                format = getFormat(stream);
                if (format == null) {
                    throw new IOException("Unsupported format: " + stream.getFormat());
                }
                packetSize = getPacketSize();
                eom = false;

                if (Thread.currentThread().interrupted()) {
                    return;
                }
                worker = timer.scheduleAtFixedRate(new Player(), 0, period, TimeUnit.MILLISECONDS);
                started = true;
                started();
            } catch (Exception e) {
                logger.error("Exception in file " + file, e);
                failed(e);
            }
        }
    }

    private class StopCommand implements Runnable {

        @SuppressWarnings("static-access")
        public void run() {
            if (worker != null && started) {
                worker.cancel(true);
                started = false;
            }
            closeAudioStream();
        }
    }

    private class ThreadFactoryImpl implements ThreadFactory {

        private String name;

        public ThreadFactoryImpl(String name) {
            this.name = name;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, name);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    }
}
