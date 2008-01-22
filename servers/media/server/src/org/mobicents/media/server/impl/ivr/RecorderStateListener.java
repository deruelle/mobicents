/*
 * RecorderStateListener.java
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

package org.mobicents.media.server.impl.ivr;

import javax.media.ConnectionErrorEvent;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataLostErrorEvent;
import javax.media.ResourceUnavailableEvent;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

/**
 *
 * @author Oleg Kulikov
 */
public class RecorderStateListener implements ControllerListener {
    
    private Recorder recorder;
    private transient Logger logger = Logger.getLogger(RecorderStateListener.class);
    
    /** Creates a new instance of RecorderStateListener */
    public RecorderStateListener(Recorder recorder) {
        this.recorder = recorder;
    }
    
    public void controllerUpdate(ControllerEvent evt) {
        NDC.push(recorder.endpoint.getLocalName());
        
        if (logger.isDebugEnabled()) {
            logger.debug("evt=" + evt);
        }
        
        if (evt instanceof javax.media.ConfigureCompleteEvent) {
        } else if (evt instanceof javax.media.RealizeCompleteEvent) {
            //context.startSendStream();
        } if (evt instanceof javax.media.StartEvent) {
//            recorder.endpoint.sendEvent(new NotifyEvent(
//                    NotifyEvent.CONTROLLER_STARTED, recorder.endpoint, null));
        } else if (evt instanceof javax.media.EndOfMediaEvent) {
            //An EndOfMediaEvent indicates that the Controller has reached
            //the end of its media and is stopping.
            // endpoint.recorderStopped();
        } else if (evt instanceof javax.media.ResourceUnavailableEvent) {
            //A ResourceUnavailableEvent indicates that a Controller was unable
            //to allocate a resource that it requires for operation.
            // endpoint.recorderFailed(new ResourceUnavailableException());
            ResourceUnavailableEvent error = (ResourceUnavailableEvent) evt;
//            recorder.endpoint.sendEvent(new NotifyEvent(
//                    NotifyEvent.ERROR, recorder.endpoint, error.getMessage()));
        } else if (evt instanceof javax.media.DataLostErrorEvent) {
            //A DataLostErrorEvent is posted when a Controller has lost data.
            DataLostErrorEvent error = (DataLostErrorEvent) evt;
//            recorder.endpoint.sendEvent(new NotifyEvent(
//                    NotifyEvent.ERROR, recorder.endpoint, error.getMessage()));
        } else if (evt instanceof javax.media.ConnectionErrorEvent) {
            //A ConnectionErrorEvent is posted when an error occurs within
            //a DataSource when obtaining data or communicating with a client.
            ConnectionErrorEvent error = (ConnectionErrorEvent) evt;
//            recorder.endpoint.sendEvent(new NotifyEvent(
//                    NotifyEvent.ERROR, recorder.endpoint, error.getMessage()));
        } else if (evt instanceof javax.media.ControllerErrorEvent) {
            //A ControllerErrorEvent describes an event that is generated when
            //an error condition occurs that will cause a Controller to cease
            //functioning. Events should only subclass from ControllerErrorEvent
            //if the error being reported will result in catastrophic failure
            //if action is not taken, or if the Controller has already failed.
            //A ControllerErrorEvent indicates that the Controller is closed.
            ControllerErrorEvent error = (ControllerErrorEvent) evt;
//            recorder.endpoint.sendEvent(new NotifyEvent(
//                    NotifyEvent.ERROR, recorder.endpoint, error.getMessage()));
        } else if (evt instanceof javax.media.ControllerClosedEvent) {
            ControllerClosedEvent cause = (ControllerClosedEvent) evt;
//            recorder.endpoint.sendEvent(new NotifyEvent(
//                    NotifyEvent.CONTROLLER_STOPPED, recorder.endpoint, cause.getMessage()));
        }
        NDC.pop();
    }
    
}
