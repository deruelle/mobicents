/*
 * DtmfSbb.java
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

package org.mobicents.examples.media;

import java.net.URL;
import java.util.HashMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.facilities.ActivityContextNamingFacility;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ratype.Cause;
import org.mobicents.slee.resource.media.ratype.ConnectionEvent;
import org.mobicents.slee.resource.media.ratype.IVRContext;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;
import org.mobicents.slee.resource.media.ratype.MediaProvider;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class DtmfSbb implements Sbb {
    
    private SbbContext sbbContext;
    
    private MediaProvider mediaProvider;
    private MediaRaActivityContextInterfaceFactory mediaAcif;
    
    private Logger logger = Logger.getLogger(DtmfSbb.class);
    
    private ActivityContextNamingFacility namingFacility;
    private HashMap messages = new HashMap();
    
    private boolean enableRecording = false;
    private URL recordFile;
    
    /** Creates a new instance of DtmfSbb */
    public DtmfSbb() {
    }
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        logger.info("Set sbbContext: SbbID=" + sbbContext.getSbb());
        
        try {
            Context ctx = (Context) new InitialContext().lookup("java:comp/env");
            namingFacility = (ActivityContextNamingFacility) ctx.lookup("slee/facilities/activitycontextnaming");
            
            //initilize Media API
            mediaProvider = (MediaProvider) ctx.lookup("slee/resources/media/1.0/provider");
            mediaAcif = (MediaRaActivityContextInterfaceFactory)ctx.lookup("slee/resources/media/1.0/acifactory");
        } catch (Exception ne) {
            logger.error("Could not set SBB context:", ne);
        }
        
        try {
            recordFile = new URL("file:/c:/record.wav");
        } catch (Exception e) {
            logger.error("Could not create file for recording.", e);
        }
        
        initVoiceMessages();
    }
    
    public void onDtmfEvent(ConnectionEvent event, ActivityContextInterface aci) {
        MediaConnection connection = event.getConnection();
        IVRContext mediaContext = (IVRContext) connection.getMediaContext();
        
        logger.info("DTMF detected on connection " + connection);
        
        URL message = (URL) messages.get(new Integer(event.getCause()));
        mediaContext.play(message);
        
        enableRecording = event.getCause() - ((int)(event.getCause() / 2)) * 2 == 0;
        logger.info("Enable recording is " + enableRecording);
    }
    
    public void onPlayerStopped(MediaContextEvent evt, ActivityContextInterface aci) {
        IVRContext ivr = (IVRContext) evt.getMediaContext();
        logger.info("Context " +  ivr.getId() + " ispaying= " + ivr.isPlaying() + " stopped, cause " + evt.getCause());
        if (enableRecording) {
            if (!ivr.isRecording()) {
                logger.info("Context " +  ivr.getId() + " start recorder ");
                ivr.record(recordFile);
            }
        } else {
            if (ivr.isRecording()) ivr.stopRecorder();
        }
    }
    
    private void initVoiceMessages() {
        try {
            messages.put(new Integer(Cause.DTMF_0), new URL("file:/C:/sounds/dtmf0.wav"));
            messages.put(new Integer(Cause.DTMF_1), new URL("file:/C:/sounds/dtmf1.wav"));
            messages.put(new Integer(Cause.DTMF_2), new URL("file:/C:/sounds/dtmf2.wav"));
            messages.put(new Integer(Cause.DTMF_3), new URL("file:/C:/sounds/dtmf3.wav"));
            messages.put(new Integer(Cause.DTMF_4), new URL("file:/C:/sounds/dtmf4.wav"));
            messages.put(new Integer(Cause.DTMF_5), new URL("file:/C:/sounds/dtmf5.wav"));
            messages.put(new Integer(Cause.DTMF_6), new URL("file:/C:/sounds/dtmf6.wav"));
            messages.put(new Integer(Cause.DTMF_7), new URL("file:/C:/sounds/dtmf7.wav"));
            messages.put(new Integer(Cause.DTMF_8), new URL("file:/C:/sounds/dtmf8.wav"));
            messages.put(new Integer(Cause.DTMF_9), new URL("file:/C:/sounds/dtmf9.wav"));
        } catch (Exception e) {
            logger.error("Unable load void messages", e);
        }
    }
    
    
    public void unsetSbbContext() {
    }
    
    public void sbbCreate() throws CreateException {
    }
    
    public void sbbPostCreate() throws CreateException {
    }
    
    public void sbbActivate() {
    }
    
    public void sbbPassivate() {
    }
    
    public void sbbLoad() {
    }
    
    public void sbbStore() {
    }
    
    public void sbbRemove() {
    }
    
    public void sbbExceptionThrown(Exception exception, Object object, ActivityContextInterface activityContextInterface) {
    }
    
    public void sbbRolledBack(RolledBackContext rolledBackContext) {
    }
    
}
