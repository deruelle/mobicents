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
package org.mobicents.media.server.impl.jmf.recorder;

import org.mobicents.media.server.impl.ivr.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.packetrelay.LocalDataSource;

/**
 *
 * @author Oleg Kulikov
 */
public class Recorder {

    private Processor recorder;
    private DataSink dataSink;
    private String mediaType;
    private Format audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 8, 1, 
            AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED);
    
    private List<RecorderListener> listeners = new ArrayList();

    private Logger logger = Logger.getLogger(Recorder.class);

    public Recorder(String mediaType) {
        this.mediaType = FileTypeDescriptor.BASIC_AUDIO;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.server.spi.ivr.IVREndpoint#record(URL)
     */
    private void record(URL url, PushBufferStream stream) throws Exception {
        DataSource dataSource = new LocalDataSource(stream);
        FileTypeDescriptor ftd = new FileTypeDescriptor(mediaType);
        Format[] formats = new Format[]{audioFormat};

        ProcessorModel recorderModel = new ProcessorModel(dataSource, formats, ftd);
        recorder = Manager.createRealizedProcessor(recorderModel);
        //recorder.addControllerListener(new RecorderStateListener(this));

        if (logger.isDebugEnabled()) {
            logger.debug("Initialized Recorder[processor=" + recorder + "]");
        }

        MediaLocator file = new MediaLocator(url);
        dataSink = Manager.createDataSink(recorder.getDataOutput(), file);
        dataSink.open();

        if (logger.isDebugEnabled()) {
            logger.debug("Initialized Datasink[" + dataSink + "]");
        }

        recorder.start();
        if (logger.isDebugEnabled()) {
            logger.debug("Starting recorder[processor=" + recorder + "]");
        }

        dataSink.start();
        if (logger.isDebugEnabled()) {
            logger.debug("Started DataSink[" + dataSink + "]");
        }
    }

    public void start(String file, PushBufferStream stream) {
        try {
            URL url = new URL(file);
            record(url, stream);
            sendEvent(RecorderEvent.STARTED, "NORMAL");
        } catch (Exception e) {
            dispose();
            logger.error("Could not start recording", e);
            sendEvent(RecorderEvent.FACILITY_ERROR, e.getMessage());
        }
    }

    private void dispose() {
        if (dataSink != null) {
            try {
                dataSink.stop();
            } catch (IOException e) {
            }
            dataSink.close();
        }

        if (recorder != null) {
            recorder.stop();
            recorder.close();
        }
    }
    
    public void stop() {
        dispose();
        sendEvent(RecorderEvent.STOP_BY_REQUEST, "NORMAL");
    }

    protected synchronized void sendEvent(int eventID, String msg) {
        RecorderEvent evt = new RecorderEvent(this, eventID, msg);
        new Thread(new EventQueue(evt)).start();
    }

    public void addListener(RecorderListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RecorderListener listener) {
        listeners.remove(listener);
    }

    /**
     * Implements async event sender
     */
    private class EventQueue implements Runnable {

        private RecorderEvent evt;

        public EventQueue(RecorderEvent evt) {
            this.evt = evt;
        }

        public void run() {
            for (RecorderListener listener : listeners) {
                listener.update(evt);
            }
        }
    }
}
