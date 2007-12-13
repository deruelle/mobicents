/*
 * IVRContextImpl.java
 *
 * The Simple Media API RA
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

package org.mobicents.slee.resource.media.ra.ivr;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.media.CannotRealizeException;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.NoProcessorException;
import javax.media.NotRealizedError;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ra.*;
import org.mobicents.slee.resource.media.ratype.Cause;
import org.mobicents.slee.resource.media.ratype.IVRContext;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;

/**
 * Implements IVR media context.
 *
 * IVR allows to play announcement from specified URL and record audio to
 * specified URL.
 *
 *
 * @author Oleg Kulikov
 */
public class IVRContextImpl extends AnnouncementContextImpl implements IVRContext {
    
    protected boolean isRecording = false;
    
    private DataSource dataSource;
    private DataSink dataSink;
    protected Processor inputDsp;
    private MediaLocator file;
    
    private URL url;
    
    private Logger logger = Logger.getLogger(IVRContextImpl.class);
    
    /**
     * Creates a new instance of IVRContextImpl
     */
    public IVRContextImpl(MediaResourceAdaptor ra) {
        super(ra);
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.IVRContext#record().
     */
    public void record(URL url) throws IllegalStateException {
        this.url = url;
        new Thread(new StartRecorderTx(this)).start();
    }
    
    protected void release() {
        try {
            stopRecorder();
            if (logger.isDebugEnabled()) {
                logger.debug("(context id = " + getId() + ") stop recording");
            }
        } finally {
            super.release();
        }
    }
    
    public void add(MediaContext context) throws IllegalStateException {
    }
    
    public void subtract(MediaContext context) throws IllegalStateException, IllegalArgumentException {
    }
    
    /**
     * Starts datasink.
     */
    protected void startDatasink() {
        try {
            dataSink = Manager.createDataSink(inputDsp.getDataOutput(), file);
            if (logger.isDebugEnabled()) {
                logger.debug("(context id = " + getId() + ") created datasink " + dataSink);
            }
        } catch (NoDataSinkException e) {
            notifyRecorderFailed(Cause.NO_PROCESSOR, e.getMessage());
            return;
        }
        
        try {
            dataSink.open();
            if (logger.isDebugEnabled()) {
                logger.debug("(context id = " + getId() + ") opened datasink " + dataSink);
            }
        } catch (SecurityException e) {
            notifyRecorderFailed(Cause.INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        } catch (IOException e) {
            notifyRecorderFailed(Cause.IO_ERROR, e.getMessage());
            return;
        }
        
        try {
            dataSink.start();
            if (logger.isDebugEnabled()) {
                logger.debug("(context id = " + getId() + ") started datasink " + dataSink);
            }
        } catch (IOException e) {
            notifyRecorderFailed(Cause.IO_ERROR, e.getMessage());
        }
        
    }
    
    /**
     * Notify the resource adaptor that is player started successfully.
     */
    protected void notifyRecorderStarted() {
        isRecording = true;
        MediaContextEvent evt = new MediaContextEventImpl(this, Cause.NORMAL);
        ra.fireRecorderStartedEvent(evt);
    }
    
    /**
     * Notify the resource adaptor that player is stopped successfully.
     */
    protected void notifyRecorderStopped() {
        isRecording = false;
        MediaContextEvent evt = new MediaContextEventImpl(this, Cause.NORMAL);
        ra.fireRecorderStoppedEvent(evt);
    }
    
    /**
     * Notify that recorder fails.
     *
     * @param cause the cause code of the failure.
     * @param msg the message which explains the reason of the failure.
     */
    protected void notifyRecorderFailed(int cause, String msg) {
        logger.error("(context id = " + getId() + ") failed, caused by " + msg);
        MediaContextEvent evt = new MediaContextErrorEventImpl(this, cause, msg);
        ra.fireRecorderFailedEvent(evt);
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.MediaContext#stopRecorder().
     */
    public void stopRecorder() {
        new Thread(new StopRecorderTx()).start();
    }
    
    /**
     * Indicates current state of the recorder.
     *
     * @return true if the recorder performs recording now or false otherwise.
     */
    public boolean isRecording() {
        return isRecording;
    }
    
    private class StartRecorderTx implements Runnable {
        private IVRContextImpl context;
        
        public StartRecorderTx(IVRContextImpl context) {
            this.context = context;
        }
        
        public void run() {
            // if the file to record to is already exists the FileNotFoundException may
            // be sometimes thrown at datasink openeing. The google says that it
            // happens only on windows machines.
            //To avoid this problem recreate file mannualy
            
            try {
                FileTypeDescriptor ftd= new FileTypeDescriptor(FileTypeDescriptor.BASIC_AUDIO);
                Format [] formats = new Format[] {new AudioFormat(AudioFormat.ULAW, 8000, 8, 1)};
                
                BaseMediaConnection connection = (BaseMediaConnection) connections.get(0);
                
                // ds should not be null if the media control state machine is correctly implemented
                if (connection.getInputStream() == null) throw new IllegalStateException("The inbound media stream cannot be null when recording begins.");
                
                ProcessorModel processorModel = new ProcessorModel(connection.getInputStream(), formats, ftd);
                inputDsp = Manager.createRealizedProcessor(processorModel);
                inputDsp.addControllerListener(new InputProcessorStateListener(context));
                logger.debug("Created realized processor");
                
                MediaLocator file = new MediaLocator(url);
                dataSink = Manager.createDataSink(inputDsp.getDataOutput(), file);
                logger.debug("Created Datasink");
                
                dataSink.open();
                logger.debug("Datasink opened");
                inputDsp.start();
                logger.debug("processor started");
                dataSink.start();
                logger.debug("datasink started");
                
                logger.debug("Recorder started");
                notifyRecorderStarted();
            } catch (NoProcessorException e) {
                notifyRecorderFailed(Cause.NO_PROCESSOR, e.getMessage());
            } catch (CannotRealizeException e) {
                notifyRecorderFailed(Cause.INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException e) {
                notifyRecorderFailed(Cause.IO_ERROR, e.getMessage());
            } catch (NoDataSinkException e) {
                notifyRecorderFailed(Cause.NO_DATASOURCE, e.getMessage());
            } catch (NotRealizedError e) {
                notifyRecorderFailed(Cause.INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (Exception e) {
                notifyRecorderFailed(Cause.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
            }
        }
    }
    
    private class StopRecorderTx implements Runnable {
        public void run() {
            //close data sink
            if (dataSink != null) {
                try {
                    dataSink.stop();
                    dataSink.close();
                } catch (IOException e) {
                    logger.warn("Could not gracefully close datasink", e);
                }
            }
            
            //closes inputprocessors imidiately
            if (inputDsp != null) {
                inputDsp.stop();
                inputDsp.close();
            }
            
            notifyRecorderStopped();
        }
    }
    
}
