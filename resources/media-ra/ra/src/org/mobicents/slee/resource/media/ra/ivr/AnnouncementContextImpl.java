/*
 * AnnouncementContextImpl.java
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

import EDU.oswego.cs.dl.util.concurrent.ReaderPreferenceReadWriteLock;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.media.Controller;
import javax.media.Format;
import javax.media.Manager;
import javax.media.NoDataSourceException;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.rtp.RTPControl;
import javax.media.rtp.SendStream;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ra.BaseMediaConnection;
import org.mobicents.slee.resource.media.ra.BaseMediaContext;
import org.mobicents.slee.resource.media.ra.MediaContextErrorEventImpl;
import org.mobicents.slee.resource.media.ra.MediaContextEventImpl;
import org.mobicents.slee.resource.media.ra.MediaResourceAdaptor;
import org.mobicents.slee.resource.media.ra.rtp.RtpMediaConnectionImpl;
import org.mobicents.slee.resource.media.ratype.AnnouncementContext;
import org.mobicents.slee.resource.media.ratype.Cause;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;
import EDU.oswego.cs.dl.util.concurrent.SyncList;
/**
 * Announcement context implementation.
 *
 * @author Oleg Kulikov
 */
public class AnnouncementContextImpl extends BaseMediaContext {

    protected boolean isPlaying = false;
    protected SyncList connections = new SyncList(new ArrayList(), new ReaderPreferenceReadWriteLock());
    
    private DataSource dataSource;
    protected Processor outputDsp;
    protected MediaResourceAdaptor ra;
    private URL url;
    private OutputProcessorStateListener outputDspStateListener;
    private Logger logger = Logger.getLogger(AnnouncementContextImpl.class);
    
    
    /** Creates a new instance of AnnouncementContextImpl */
    public AnnouncementContextImpl(MediaResourceAdaptor ra) {
        this.ra = ra;
    }
    
    /** 
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.MediaContext#play().
     */
    public void play(URL url) {
        this.url = url;
        new Thread(new StartPlayerTx()).start();
    }
    
    public void startPlayer() throws IllegalStateException {
        if (isPlaying) {
            throw new IllegalStateException("Context " + getId() + " is playing now");
        }
        
        try {
            dataSource = Manager.createDataSource(url);
        } catch (NoDataSourceException e) {
            notifyPlayerFailed(Cause.NO_DATASOURCE, e.getMessage());
            return;
        } catch (IOException e) {
            notifyPlayerFailed(Cause.IO_ERROR, e.getMessage());
            return;
        }
        
        if (outputDsp != null && outputDsp.getState() != Controller.Unrealized) {
            outputDsp.close();            
            outputDsp.deallocate();
        }
        
        outputDspStateListener = new OutputProcessorStateListener(this);
        try {
            outputDsp = Manager.createProcessor(dataSource);
            outputDsp.addControllerListener(outputDspStateListener);
            outputDsp.configure();
        } catch (NoProcessorException e) {
            notifyPlayerFailed(Cause.NO_PROCESSOR, e.getMessage());
        } catch (IOException e) {
            notifyPlayerFailed(Cause.IO_ERROR, e.getMessage());
        }  catch (Exception e) {
            notifyPlayerFailed(Cause.INTERNAL_SERVER_ERROR, e.getMessage());
        }     
    }
    
    
    /** 
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.MediaContext#stop().
     */
    public void stopPlayer() throws IllegalStateException {
        new Thread(new StopPlayerTx()).start();
    }
    
    private void stop() {
        if (outputDsp != null && outputDsp.getState() != Controller.Started) {
            outputDsp.stop();
        } else throw new IllegalStateException("Context is already stopped");
    }
    
    /** 
     * Relaize processor.
     *
     * @dsp processor to realize.
     */
    protected synchronized void realize(Processor dsp) {
        TrackControl track[] = dsp.getTrackControls();
        boolean encodingOk = false;
        
        // @todo create processor for each connection.
        BaseMediaConnection connection = (BaseMediaConnection) connections.get(0);
        DataSource dataSource = connection.getInputStream();
        
        Format format = new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1);
        RTPControl ctl = (RTPControl) dataSource.getControl("javax.media.rtp.RTPControl");
        
        if (ctl != null && ctl.getFormat() != null) {
            format = ctl.getFormat();
            logger.debug("(context id = " + getId() + ") set format :" + format);
        } else {
            logger.debug("(context id = " + getId() + ") set default format :" + format);
        }
        
        for (int i = 0; i < track.length; i++) {
            if (!encodingOk && track[i] instanceof FormatControl) {
                if (((FormatControl)track[i]).setFormat(format) == null) {
                    track[i].setEnabled(false);
                } else {
                    encodingOk = true;
                }
            } else {
                // we could not set this track to ulaw, so disable it
                track[i].setEnabled(false);
            }
        }
        
        if (!encodingOk) {
            notifyPlayerFailed(Cause.NO_FORMAT, "");
            return;
        }
        
        dsp.realize();
    }
    
    /** 
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.MediaContext#attach(MediaConnection).
     */
    public synchronized void add(MediaConnection connection) throws IllegalStateException {
        System.out.println("**** ADDED CONNECTION ****");
        connections.add(connection);
        ((BaseMediaConnection)connection).setMediaContext(this);
    }
    
    /** 
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.MediaContext#detach(MediaConnection).
     */
    public synchronized void subtract(MediaConnection connection) throws IllegalStateException, IllegalArgumentException {
        if (!connections.remove(connection)) {
            throw new IllegalArgumentException("Unknown connection: "+ connection);
        }

        //((BaseMediaConnection)connection).setOutputStream(null);
        ((BaseMediaConnection)connection).setMediaContext(null);
        
        if (connections.size() == 0) {
            release();
        }
        
    }
    
    /** 
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.MediaContext#attach(MediaContext).
     */
    public void add(MediaContext context) throws IllegalStateException {
    }
    
    /** 
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.MediaContext#attach(MediaContext).
     */
    public void subtract(MediaContext context) throws IllegalStateException, 
            IllegalArgumentException {
        
    }

    /** 
     * (Non Java-doc).
     *
     * @see org.mobicents.slee.resource.media.ratype.MediaContext#getConnections().
     */
    public Collection getConnections() {
        return connections;
    }
    
    /**
     * Starts outgoing media transmission.
     */
    protected synchronized void startSendStream() {
        DataSource dataSource = outputDsp.getDataOutput();
        try {
            outputDsp.start();
            dataSource.start();
        } catch (IOException e) {
            notifyPlayerFailed(Cause.IO_ERROR, e.getMessage());
            return;
        }
        
        logger.debug("(context id = " + getId() + ") started :" + dataSource);
        
        int count = connections.size();
        for (int i = 0; i < count; i++) {
            if (logger.isDebugEnabled()) {
                logger.debug("(context id = " + getId() + ") set stream for connection :" + connections.get(i));
            }
            BaseMediaConnection connection = (BaseMediaConnection) connections.get(i);
            if (connection.getState() == MediaConnection.STATE_CONNECTED) {
                ((RtpMediaConnectionImpl) connections.get(i)).setOutputStream(dataSource);
            }
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("(context id = " + getId() + ") start send stream");
        }
    }

    /**
     * Release all allocated resources.
     */
    protected void release() {
        //if output processor wasn't created nothing to do here'
        if (outputDsp == null) {
            return;
        }
        
        //closing output processor.
        try {
            outputDsp.removeControllerListener(outputDspStateListener);
            outputDsp.stop();
            outputDsp.close();
            outputDsp.deallocate();
            if (logger.isDebugEnabled()) {
                logger.debug("(context id = " + getId() + ") released gracefully");
            }
        } finally {
            ra.endingMediaContextActivity(this);
        }
    }
    
    /**
     * Notify the resource adaptor that player is failed.
     *
     * @param cause the cause code of the failure.
     * @param msg the message explaining details of the failure.
     */
    protected void notifyPlayerFailed(int cause, String msg) {
        MediaContextEvent evt = new MediaContextErrorEventImpl(this, cause, msg);
        ra.firePlayerFailedEvent(evt);
        logger.info("{context id = " + getId() + "} player failed: " + msg);
    }

    /**
     * Notify the resource adaptor that is player started successfully.
     */
    protected void notifyPlayerStarted() {
        isPlaying = true;
        logger.info("{context id = " + getId() + "} player started");
        MediaContextEvent evt = new MediaContextEventImpl(this, Cause.NORMAL);
        ra.firePlayerStartedEvent(evt);
    }

    /**
     * Notify the resource adaptor that player is stopped successfully.
     */
    protected void notifyPlayerStopped() {
        isPlaying = false;
        logger.info("{context id = " + getId() + "} player stopped");
        MediaContextEvent evt = new MediaContextEventImpl(this, Cause.NORMAL);
        ra.firePlayerStoppedEvent(evt);
    }
        
    /**
     * Indicates the state of the player.
     *
     * @return true if player is started and false otherwise.
     */
    public boolean isPlaying() {
        return isPlaying;
    }
    
    private class StartPlayerTx implements Runnable {
        public void run() {
            startPlayer();
        }
    }
    
    private class StopPlayerTx implements Runnable {
        public void run() {
            stop();
        }
    }    
}
