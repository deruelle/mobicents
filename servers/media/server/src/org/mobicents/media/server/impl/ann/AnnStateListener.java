/*
 * AnnStateListener.java
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
package org.mobicents.media.server.impl.ann;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;


import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 */
public class AnnStateListener implements ControllerListener {

    private AnnouncementSignal signal;
    private transient Logger logger = Logger.getLogger(AnnStateListener.class);

    /**
     * Creates a new instance of AnnStateListener
     */
    public AnnStateListener(AnnouncementSignal player) {
        this.signal = player;
    }

    public void controllerUpdate(ControllerEvent evt) {
        logger.info("evt=" + evt);
        if (evt instanceof javax.media.ConfigureCompleteEvent) {
        } else if (evt instanceof javax.media.RealizeCompleteEvent) {
        } else if (evt instanceof javax.media.StartEvent) {
        } else if (evt instanceof javax.media.EndOfMediaEvent) {
            //signal.stop();
        } 
    }
}
