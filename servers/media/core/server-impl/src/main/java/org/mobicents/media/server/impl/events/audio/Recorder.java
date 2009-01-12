/*
 * Recorder.java
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
package org.mobicents.media.server.impl.events.audio;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSink;

import org.mobicents.media.server.spi.Endpoint;

/**
 * 
 * @author Oleg Kulikov
 */
public class Recorder extends AbstractSink {

    private transient Logger logger = Logger.getLogger(Recorder.class);
    private int recordTime = 60;
    private Format format;
    private String recordDir = "";
    private FileOutputStream file;
    private Thread recorderThread = null;
    private RecorderStream recorderStream;
    private volatile boolean started = false;

    // private RecorderRunnable runner=null;
    public Recorder(String name) {
        super(name);
    }

    public Recorder(String mediaType, String recordDir) {
        super("Recorder");
        this.recordDir = recordDir;
    }

    public Recorder(AudioFileFormat.Type mediaType, int recordTime, String recordDir) {
        super("Recorder");
        this.recordTime = recordTime;
        this.recordDir = recordDir;
    }
    
    public void setRecordTime(int recordTime){
    	this.recordTime = recordTime;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.server.spi.ivr.IVREndpoint#record(URL)
     */
    private void record(String uri) throws Exception {
        if (recorderThread != null) {
            dispose();
        }

        recorderStream = new RecorderStream();
        javax.sound.sampled.AudioFormat fmt = new javax.sound.sampled.AudioFormat(8000, 16, 1, true, false);
        AudioInputStream audioStream = new AudioInputStream(recorderStream, fmt, 8000 * recordTime);
        // AudioInputStream audioStream =
        // AudioSystem.getAudioInputStream(recorderStream);
        int index = uri.lastIndexOf("/");
        if (index > 0) {
            String folderStructure = uri.substring(0, index);

            java.io.File file = new java.io.File(new StringBuffer(recordDir).append("/").append(folderStructure).toString());
            boolean fileCreationSuccess = file.mkdirs();
        }
        uri = recordDir + "/" + uri;
        if (logger.isDebugEnabled()) {
            logger.debug("RECORDING TO " + uri);
        }
        file = new FileOutputStream(uri);
        this.recorderThread = new Thread(new RecorderRunnable(audioStream));
        this.recorderThread.start();
    }

    public void start(String file) {
        try {
            started = true;
            record(file);
        // sendEvent(RecorderEventType.STARTED, "NORMAL");
        } catch (Exception e) {
            started = false;
            release();
            logger.error("Could not start recording", e);
        // sendEvent(RecorderEventType.FACILITY_ERROR, e.getMessage());
        }
    }

    private void release() {
        try {
            if (file != null) {
                file.flush();
                file.close();
            }

            if (recorderThread != null) {
                this.recorderThread = null;
            }
        // this.runner=null;
        } catch (Exception e) {
            logger.error("Could not close recorder file", e);
        // sendEvent(RecorderEventType.FACILITY_ERROR, e.getMessage());
        }
    }

    public void stop() {
        started = false;
        release();
    }

    private class RecorderRunnable implements Runnable {

        AudioInputStream audioStream = null;

        public RecorderRunnable(AudioInputStream audioStream) {
            this.audioStream = audioStream;
        }

        public void run() {
            try {
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, file);
            } catch (IOException e) {
                if (started) {
                    logger.error("Audio stream write error", e);
                    // sendEvent(RecorderEventType.FACILITY_ERROR, e.getMessage());
                    dispose();
                }
            }

        }
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.MediaSink.isAcceptable(Format).
     */
    public boolean isAcceptable(Format fmt) {
        return true;
    }

    public void receive(Buffer buffer) {
        if (recorderStream == null) {
            return;
        }

        recorderStream.buffers.add(buffer);
        recorderStream.available += (buffer.getLength() - buffer.getOffset());

        if (recorderStream.blocked) {
            recorderStream.blocked = false;
            recorderStream.semaphore.release();
        }
    }

    public Format[] getFormats() {
        return new Format[]{Endpoint.LINEAR_AUDIO, Endpoint.GSM, Endpoint.PCMA, Endpoint.PCMU};
    }
}
