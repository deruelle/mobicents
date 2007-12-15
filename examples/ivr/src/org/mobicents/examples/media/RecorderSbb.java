/*
 * RecorderSbb.java
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
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ratype.IVRContext;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class RecorderSbb implements Sbb {
    
    private Logger logger = Logger.getLogger(RecorderSbb.class);
    
    private URL startMessage;
    private URL stopMessage;
    
    public void onRecorderStarted(MediaContextEvent evt, ActivityContextInterface aci) {
        logger.info("Start recording....");
        try {
            ((IVRContext)evt.getMediaContext()).play(startMessage);
        } catch (Exception e) {
            logger.error("ContextID= " + evt.getMediaContext().getId(),e);
        }
    }
    
    public void onRecorderStopped(MediaContextEvent evt, ActivityContextInterface aci) {
        logger.info("Stop recording");
        ((IVRContext)evt.getMediaContext()).play(stopMessage);
    }
    
    /** Creates a new instance of RecorderSbb */
    public RecorderSbb() {
    }

    public void setSbbContext(SbbContext sbbContext) {
        try {
            startMessage = new URL("file:/sounds/startrecord.wav");
            stopMessage = new URL("file:/sounds/stoprecord.wav");
        } catch (Exception e) {
            logger.error("Could not load voice messages.", e);
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
