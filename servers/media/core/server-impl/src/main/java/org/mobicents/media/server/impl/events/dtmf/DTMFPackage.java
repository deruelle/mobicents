/*
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
package org.mobicents.media.server.impl.events.dtmf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.EventID;

/**
 * 
 * @author Oleg Kulikov
 */
public class DTMFPackage implements Serializable {

    private BaseEndpoint endpoint;
    private Semaphore semaphore = new Semaphore(0);
    private boolean blocked = false;
    private Logger logger = Logger.getLogger(DTMFPackage.class);

    public DTMFPackage(Endpoint endpoint) {
        this.endpoint = (BaseEndpoint) endpoint;
    }

    public void subscribe(EventID eventID, HashMap params, String connectionID, NotificationListener listener) {
        /*
//        BaseDtmfDetector detector = (BaseDtmfDetector) this.endpoint.getResource(MediaResourceType.DTMF_DETECTOR,
//                connectionID);

//        Properties config = endpoint.getDefaultConfig(MediaResourceType.DTMF_DETECTOR);
//        DTMFType detectorMode = DTMFType.valueOf(config.getProperty("detector.mode"));

        if (params != null) {
            String mask = (String) params.get("dtmf.mask");
            if (mask != null) {
//                detector.setDtmfMask(mask);
            }
        }

//        if (detectorMode == DTMFType.INBAND) {
//            if (detector.getState() != MediaResourceState.PREPARED) {
                try {
//                    MediaSink sink = (MediaSink) endpoint.getResource(MediaResourceType.AUDIO_SINK, connectionID);
//                    sink.addStateListener(this);
//                    if (sink.getState() != MediaResourceState.PREPARED && sink.getState() != MediaResourceState.STARTED) {
                        try {
                            blocked = true;
                            semaphore.acquire();
                        } catch (InterruptedException e) {
                        }
//                    }
//                    detector.prepare(endpoint, sink.newBranch("DTMF"));
                } catch (UnsupportedFormatException ex) {
                    logger.error("Could not prepare DTMF detector", ex);
                    NotifyEvent evt = new NotifyEvent(this, EventID.DTMF, EventCause.FACILITY_FAILURE, ex.getMessage());
                    listener.update(evt);
                    return;
                }
            }
//        }

//        detector.start();
//        detector.addListener(listener);
         */ 
    }

}
