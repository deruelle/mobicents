/*
 * InputProcessorStateListener.java
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
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Processor;
import javax.media.rtp.event.RemotePayloadChangeEvent;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ratype.Cause;

/**
 *
 * @author Oleg Kulikov
 */
public class InputProcessorStateListener implements ControllerListener {
    
    private IVRContextImpl context;
    private Logger logger = Logger.getLogger(InputProcessorStateListener.class);
    
    /** Creates a new instance of InputProcessorStateListener */
    public InputProcessorStateListener(IVRContextImpl context) {
        this.context = context;
    }

    public void controllerUpdate(ControllerEvent evt) {
        logger.debug("(context id = " + context.getId() + ") evt=" + evt);
        if (evt instanceof javax.media.ConfigureCompleteEvent) {
            logger.debug("(context id = " + context.getId() + ") Realize receiver processor");
            //context.realize(context.inputDsp);
        } else if (evt instanceof javax.media.RealizeCompleteEvent) {
            logger.debug("(context id = " + context.getId() + ") Starting receiver processor");
            //context.startDatasink();
        } if (evt instanceof javax.media.StartEvent) {
            //context.isPlaying = true;
            System.out.println("************ START INPUT PROCCESOR***********");
        } else if (evt instanceof javax.media.EndOfMediaEvent) {
            //An EndOfMediaEvent indicates that the Controller has reached
            //the end of its media and is stopping.
            //context.isPlaying = false;
            System.out.println("************ END OF INPUT DATA ***********");
        } else if (evt instanceof javax.media.ResourceUnavailableEvent) {
            //A ResourceUnavailableEvent indicates that a Controller was unable
            //to allocate a resource that it requires for operation.
            context.notifyRecorderFailed(Cause.RESOURCE_UNAVAILABLE, 
                    ((javax.media.ResourceUnavailableEvent)evt).getMessage());
        } else if (evt instanceof javax.media.DataLostErrorEvent) {
            //A DataLostErrorEvent is posted when a Controller has lost data.
            context.notifyRecorderFailed(Cause.DATA_LOST, 
                    ((javax.media.DataLostErrorEvent)evt).getMessage());
        } else if (evt instanceof javax.media.ConnectionErrorEvent) {
            //A ConnectionErrorEvent is posted when an error occurs within
            //a DataSource when obtaining data or communicating with a client.
            context.notifyRecorderFailed(Cause.CONNECTION_ERROR, 
                    ((javax.media.ConnectionErrorEvent)evt).getMessage());
        } else if (evt instanceof javax.media.ControllerErrorEvent) {
            //A ControllerErrorEvent describes an event that is generated when
            //an error condition occurs that will cause a Controller to cease
            //functioning. Events should only subclass from ControllerErrorEvent
            //if the error being reported will result in catastrophic failure
            //if action is not taken, or if the Controller has already failed.
            //A ControllerErrorEvent indicates that the Controller is closed.
            context.notifyRecorderFailed(Cause.INTERNAL_SERVER_ERROR, 
                    ((javax.media.ControllerErrorEvent)evt).getMessage());
        } else if (evt instanceof javax.media.ControllerClosedEvent) {
            //context.notifyRecorderFailed(Cause.INTERNAL_SERVER_ERROR, 
            //        ((javax.media.ControllerClosedEvent)evt).getMessage());
        }
    }
    
}
