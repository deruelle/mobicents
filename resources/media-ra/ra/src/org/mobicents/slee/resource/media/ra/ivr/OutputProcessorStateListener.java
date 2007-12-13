/*
 * OutputProcessorStateListener.java
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

import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Processor;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ratype.Cause;
import org.mobicents.slee.resource.media.ratype.MediaContext;

/**
 *
 * @author Oleg Kulikov
 */
public class OutputProcessorStateListener implements ControllerListener {
    
    private AnnouncementContextImpl context;
    private Logger logger = Logger.getLogger(OutputProcessorStateListener.class);
    
    /** Creates a new instance of OutputProcessorStateListener */
    public OutputProcessorStateListener(AnnouncementContextImpl context) {
        this.context = context;
    }
    
    public void controllerUpdate(ControllerEvent evt) {
        if (logger.isDebugEnabled()) {
            logger.debug("(context id = " + context.getId() + ") evt=" + evt);
        }
        
        if (evt instanceof javax.media.ConfigureCompleteEvent) {
            logger.debug("(context id = " + context.getId() + ") Realize transmitter processor");
            context.realize(context.outputDsp);
        } else if (evt instanceof javax.media.RealizeCompleteEvent) {
            logger.debug("(context id = " + context.getId() + ") Starting transmitter processor");
            context.startSendStream();
        } if (evt instanceof javax.media.StartEvent) {
            context.notifyPlayerStarted();
        } else if (evt instanceof javax.media.EndOfMediaEvent) {
            //An EndOfMediaEvent indicates that the Controller has reached
            //the end of its media and is stopping.
            context.notifyPlayerStopped();
        } else if (evt instanceof javax.media.ResourceUnavailableEvent) {
            //A ResourceUnavailableEvent indicates that a Controller was unable
            //to allocate a resource that it requires for operation.
            context.notifyPlayerFailed(Cause.RESOURCE_UNAVAILABLE, 
                    ((javax.media.ResourceUnavailableEvent)evt).getMessage());
        } else if (evt instanceof javax.media.DataLostErrorEvent) {
            //A DataLostErrorEvent is posted when a Controller has lost data.
            context.notifyPlayerFailed(Cause.DATA_LOST, 
                    ((javax.media.DataLostErrorEvent)evt).getMessage());
        } else if (evt instanceof javax.media.ConnectionErrorEvent) {
            //A ConnectionErrorEvent is posted when an error occurs within
            //a DataSource when obtaining data or communicating with a client.
            context.notifyPlayerFailed(Cause.CONNECTION_ERROR, 
                    ((javax.media.ConnectionErrorEvent)evt).getMessage());
        } else if (evt instanceof javax.media.ControllerErrorEvent) {
            //A ControllerErrorEvent describes an event that is generated when
            //an error condition occurs that will cause a Controller to cease
            //functioning. Events should only subclass from ControllerErrorEvent
            //if the error being reported will result in catastrophic failure
            //if action is not taken, or if the Controller has already failed.
            //A ControllerErrorEvent indicates that the Controller is closed.
            context.notifyPlayerFailed(Cause.INTERNAL_SERVER_ERROR, 
                    ((javax.media.ControllerErrorEvent)evt).getMessage());
        } else if (evt instanceof javax.media.ControllerClosedEvent && context.isPlaying()) {
            context.notifyPlayerFailed(Cause.INTERNAL_SERVER_ERROR, 
                    ((javax.media.ControllerClosedEvent)evt).getMessage());
        }
    }
    
}
