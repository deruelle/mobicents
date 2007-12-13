/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.resource.media.ra;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.media.CannotRealizeException;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.NoProcessorException;
import javax.media.NotRealizedError;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.rtp.RTPControl;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.RemotePayloadChangeEvent;
import org.mobicents.slee.resource.media.ratype.MediaSession;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.events.EndMediaStreamEvent;


/**
 *
 * Callback class, which receives events from an active media session
 * and reacts accordingly.
 * TODO: the logic needs to be cleaned up and documented better
 *
 * @author torosvi
 * @author Ivelin Ivanov
 * @author Oleg Kulikov
 */
public class MediaListener implements ControllerListener, ReceiveStreamListener {
    
    private static Logger logger = Logger.getLogger(MediaListener.class);
    
    private MediaSessionImpl mediaSession;
    private EventListener listener;
    
    // CONTROLER LISTENER
    private Processor processor = null;
    private Integer stateLock = new Integer(0);
    private boolean failed = false;
    private boolean start = false;
    private boolean stopped = false;
    private boolean processorStopped = false;
    
    // RECEIVE STREAM LISTENER
    private DataSink dataSink;
    private Processor recordProcessor = null;
    private DataSourceHandler dsHandler = null;
    private DataSource inboundMediaSource;
    private boolean dtmf = false;
    private Format remoteFormat;
    private URL fileRcv;
    
    // Contructor
    public MediaListener(MediaSessionImpl mediaSession, boolean dtmf) {
        this.mediaSession = mediaSession;
        this.dtmf = dtmf;
    }
    
    /**
     * ControllerListener
     */
    public void controllerUpdate(ControllerEvent ce) {
        
        // If there was an error during configure or
        // realize, the processor will be closed
        if (ce instanceof ControllerClosedEvent)
            setFailed();
        
        // All controller events, send a notification
        // to the waiting thread in waitForState method.
        if (ce instanceof ControllerEvent) {
            synchronized (getStateLock()) {
                getStateLock().notifyAll();
            }
            if (processor.getState() == Processor.Started) {
                start = true;
            }
        }
        
        if (start == true && processor.getState()
        < Processor.Started) {
            start = false;
            processor.removeControllerListener(this);
            logger.info("...transmission ended.");
            
            setProcessorStopped(true);
            
            EndMediaStreamEvent event = new EndMediaStreamEvent();
            
            if (getStopped()) {
                // The Media Stream has been forced to achieve the end
                setStopped(false);
                event.setForcedEnd(true);
            }
            
            listener.onEvent(event, mediaSession);
        }
    }
    
    public synchronized boolean waitForState(Processor p, int state) {
        this.processor = p;
        p.addControllerListener(this);
        failed = false;
        
        // Call the required method on the processor
        if (state == Processor.Configured) {
            p.configure();
        } else if (state == Processor.Realized) {
            p.realize();
        }
        
        // Wait until we get an event that confirms the
        // success of the method, or a failure event.
        // See StateListener inner class
        while (p.getState() < state && !failed) {
            synchronized (getStateLock()) {
                try {
                    getStateLock().wait();
                } catch (InterruptedException ie) {
                    return false;
                }
            }
        }
        
        if (failed)
            return false;
        else
            return true;
    }
    
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    public boolean getStopped() {
        return stopped;
    }
    
    public void setProcessorStopped(boolean processorStopped) {
        this.processorStopped = processorStopped;
    }
    public boolean getProcessorStopped() {
        return processorStopped;
    }
    
    public void setFailed() {
        failed = true;
    }
    public boolean getFailed() {
        return failed;
    }
    
    public Integer getStateLock() {
        return stateLock;
    }
    
    /**
     * ReceiveStreamListener
     */
    public synchronized void update(ReceiveStreamEvent evt) {
        
        ReceiveStream stream = null;
        if (evt instanceof RemotePayloadChangeEvent) {
            logger.info("  - Received an RTP PayloadChangeEvent.");
            stream = evt.getReceiveStream();
            inboundMediaSource = stream.getDataSource();
            
            if (dtmf)
                checkingDTMF();
        }
        
        else if (evt instanceof NewReceiveStreamEvent) {
            logger.info("  - Received an RTP NewReceiveStreamEvent.");
            stream = ((NewReceiveStreamEvent)evt).getReceiveStream();
            //mediaSession.inboundMediaSource = stream.getDataSource();
            inboundMediaSource = stream.getDataSource();
            
            if (dtmf)
                checkingDTMF();
        }
        
       // if (inboundMediaSource != null) mediaSession.inboundStreamInitialized();
    }
    
    public void checkingDTMF() {
        if (inboundMediaSource != null) {
            String format = "UNKNOWN";
            
            // Find out the formats.
            RTPControl ctl = (RTPControl)inboundMediaSource.getControl("javax.media.rtp.RTPControl");
            
            if ((ctl != null) && (ctl.getFormat() != null))
                format = ctl.getFormat().toString();
            
            if (format.equals("DTMF")) {
                DataSourceHandler handler = new DataSourceHandler(listener, mediaSession);
                
                if (dsHandler != null) {
                    dsHandler.stop();
                }
                
                dsHandler = handler;
                
                try {
                    dsHandler.setSource(inboundMediaSource);
                    
                } catch (IncompatibleSourceException e) {
                    logger.error("Cannot handle the output DataSource from the processor: " + inboundMediaSource, e);
                }
                
                dsHandler.start();
            }
        }
    }
    
    public void addListener(EventListener listener) {
        this.listener = listener;
    }
    
    public void prepareRecording(Format remoteFormat, URL fileRcv) {
        this.remoteFormat = remoteFormat;
        this.fileRcv = fileRcv;
    }
    
    public void startRecording() {
        try {
            FileTypeDescriptor ftd= new FileTypeDescriptor(FileTypeDescriptor.BASIC_AUDIO);
            Format [] formats = new Format[] {remoteFormat};
            
            Vector rcvStreams = mediaSession.rtpSessionManager.getReceiveStreams();
            if (rcvStreams.size() > 0) {
                inboundMediaSource = ((ReceiveStream) (rcvStreams.get(0))).getDataSource();
            }
            
            // ds should not be null if the media control state machine is correctly implemented
            if (inboundMediaSource == null) throw new IllegalStateException("The inbound media stream cannot be null when recording begins.");
            
            ProcessorModel processorModel = new ProcessorModel(inboundMediaSource, formats, ftd);
            recordProcessor = Manager.createRealizedProcessor(processorModel);
            MediaLocator file = new MediaLocator(fileRcv);
            dataSink = Manager.createDataSink(recordProcessor.getDataOutput(), file);
            
            dataSink.open();
            recordProcessor.start();
            dataSink.start();
            
        } catch (NoProcessorException e) {
            logger.error(e.getMessage(), e);
        } catch (CannotRealizeException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (NoDataSinkException e) {
            logger.error(e.getMessage(), e);
        } catch (NotRealizedError e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public void stopRecording() {
        
        if ((recordProcessor != null) && (dataSink != null)) {
            recordProcessor.stop();
            recordProcessor.close();
            dataSink.close();
            logger.info("########## RECORDING STOPPED ##########");
        }
    }
}