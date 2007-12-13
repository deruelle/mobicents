/*
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
 *
 *
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package org.mobicents.slee.resource.media.ra.conf;

import java.io.IOException;
import javax.media.Format;
import javax.media.Manager;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.rtp.RTPControl;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ra.BaseMediaConnection;

/**
 *
 * @author $Author: kulikoff $
 * @version $Revision: 1.3 $
 */
public class ConfParty {
    
    private ConfMediaContextImpl context;
    protected BaseMediaConnection connection;
    
    protected Processor inputDsp;
    protected Processor outputDsp;
    
    private Logger logger = Logger.getLogger(ConfParty.class);
    
    /** Creates a new instance of ConfParty */
    public ConfParty(ConfMediaContextImpl context, BaseMediaConnection connection) {
        this.context = context;
        this.connection = connection;
    }
    
    public String getId() {
        return connection.getId();
    }
    
    protected void transcodeInputStream() {
        try {
            connection.getInputStream().start();
            inputDsp = Manager.createProcessor(connection.getInputStream());
            inputDsp.addControllerListener(new InputProcessorStateListener(this));
            inputDsp.configure();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoProcessorException e) {
            e.printStackTrace();
        }
    }
    
    protected DataSource getInputStream() {
        inputDsp.start();
        try {
            inputDsp.getDataOutput().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputDsp.getDataOutput();
    }
    
    protected void setOutputStream(DataSource dataSource) {
        try {
            dataSource.start();
            outputDsp = Manager.createProcessor(dataSource);
            outputDsp.addControllerListener(new OutputProcessorStateListener(this));
            outputDsp.configure();
            if (logger.isDebugEnabled()) {
                logger.debug("Configuring output processor " + outputDsp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoProcessorException e) {
            e.printStackTrace();
        }
    }
    
    protected void startSendStream() {
        if (logger.isDebugEnabled()) {
            logger.debug("Set output source: " + outputDsp.getDataOutput());
        }
        connection.setOutputStream(outputDsp.getDataOutput());
    }
    
    /**
     * Relaize processor.
     *
     * @dsp processor to realize.
     */
    protected synchronized void realize(Processor dsp, Format format) {
        TrackControl track[] = dsp.getTrackControls();
        boolean encodingOk = false;
        
        // @todo create processor for each connection.
        DataSource dataSource = connection.getInputStream();
        if (format == null) {
            format = new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1);
            RTPControl ctl = (RTPControl) dataSource.getControl("javax.media.rtp.RTPControl");
            
            if (ctl != null && ctl.getFormat() != null) {
                format = ctl.getFormat();
                logger.debug("(party id = " + getId() + ") set format :" + format);
            } else {
                logger.debug("(party id = " + getId() + ") set default format :" + format);
            }
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
            //notifyPlayerFailed(Cause.NO_FORMAT, "");
            return;
        }
        
        dsp.realize();
    }
    
    protected void mux() {
        inputDsp.start();
        try {
            inputDsp.getDataOutput().start();
            context.mux();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
