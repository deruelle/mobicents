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
package org.mobicents.media.server.impl.events.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.common.MediaResourceState;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.common.events.EventCause;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.impl.fft.SpectralAnalyser;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.spi.MediaSink;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceStateListener;
import org.mobicents.media.server.spi.UnknownMediaResourceException;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class TestPackage implements Serializable, ResourceStateListener {

    private BaseEndpoint endpoint;
    private Semaphore semaphore = new Semaphore(0);
    private boolean blocked = false;
    private Logger logger = Logger.getLogger(TestPackage.class);
    
    public void subscribe(EventID eventID, HashMap params,
    		Connection connection, NotificationListener listener) {
        SpectralAnalyser analyser = (SpectralAnalyser) endpoint.getResource(MediaResourceType.SPECTRUM_ANALYSER, connection.getId());
        if (analyser == null) {
            try {
                endpoint.configure(MediaResourceType.SPECTRUM_ANALYSER, connection, null);
            } catch (UnknownMediaResourceException e) {

            }
        }

        if (analyser.getState() != MediaResourceState.PREPARED) {
            try {
                MediaSink sink = (MediaSink) endpoint.getResource(MediaResourceType.AUDIO_SINK, connection.getId());
                sink.addStateListener(this);
                if (sink.getState() != MediaResourceState.PREPARED && sink.getState() != MediaResourceState.STARTED) {
                    try {
                        blocked = true;
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                    }
                }
                analyser.prepare(endpoint, sink.newBranch("DFT"));
            } catch (UnsupportedFormatException ex) {
                logger.error("Could not prepare Spectral analyser ", ex);
                NotifyEvent evt = new NotifyEvent(this, EventID.TEST_SPECTRA, EventCause.FACILITY_FAILURE, ex.getMessage());
                listener.update(evt);
                return;
            }
        }

    }

    public void onStateChange(MediaResource resource, MediaResourceState state) {
        if (state == MediaResourceState.PREPARED && blocked) {
            semaphore.release();
        }
    }
}
