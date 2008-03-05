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
package org.mobicents.media.server.impl.ivr;

import java.net.URL;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.ann.AnnouncementSignal;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.AU;
import org.mobicents.media.server.spi.events.Announcement;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class PlayRecordSignal extends AnnouncementSignal {

    private IVREndpointImpl endpoint;
    private String[] params;
    private BaseConnection connection;
    private NotificationListener listener;

    public PlayRecordSignal(IVREndpointImpl endpoint,
            NotificationListener listener, String params[]) {
        super(endpoint, listener, params);
        this.listener = listener;
        connection = endpoint.getConnections().iterator().next();
    }

    @Override
    public void start() {
        String announcement = params[0];
        String recordURL = params[1];

        if (announcement != null) {
            try {
                endpoint.play(Announcement.PLAY, new String[]{announcement}, connection.getId(), listener, false);
            } catch (Exception e) {
                NotifyEvent report = new NotifyEvent(endpoint,
                        Announcement.FAIL,
                        Announcement.CAUSE_FACILITY_FAILURE,
                        e.getMessage());
                this.sendEvent(report);
            }
        }

        try {
            Recorder recorder = (Recorder) endpoint.getResource(Endpoint.RESOURCE_AUDIO_SOURCE, connection.getId());
            recorder.setURL(new URL(recordURL));
            recorder.start();
        } catch (Exception e) {
            NotifyEvent report = new NotifyEvent(endpoint,
                    AU.FAIL,
                    AU.CAUSE_FACILITY_FAILURE,
                    e.getMessage());
            this.sendEvent(report);
        }
    }

    @Override
    public void stop() {
        Recorder recorder = (Recorder) endpoint.getResource(Endpoint.RESOURCE_AUDIO_SOURCE, connection.getId());
        recorder.stop();
    }
}
