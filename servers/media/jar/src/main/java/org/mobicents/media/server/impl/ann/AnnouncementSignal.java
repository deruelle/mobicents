/*
 * AnnouncementSignal.java
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

import java.util.Collection;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.Signal;
import org.mobicents.media.server.impl.jmf.player.AudioPlayer;
import org.mobicents.media.server.impl.jmf.player.PlayerEvent;
import org.mobicents.media.server.impl.jmf.player.PlayerListener;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.Announcement;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class AnnouncementSignal extends Signal implements PlayerListener {

    protected AnnEndpointImpl endpoint;
    
    private String[] params;
    private AudioPlayer player;
    private AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
    
    private Logger logger = Logger.getLogger(AnnouncementSignal.class);

    /**
     * Creates a new instance of AnnouncementSignal
     */
    public AnnouncementSignal(AnnEndpointImpl endpoint,
            NotificationListener listener, String params[]) {
        super(listener);
        
        this.endpoint = endpoint;
        this.params = params;
        
        player = new AudioPlayer(endpoint.getPacketizationPeriod());
        player.setFormat(audioFormat);
        player.addListener(this);
    }

    public void start() {
        try {
            PushBufferStream stream = player.start(params[0]);
            Collection <BaseConnection> list = endpoint.getConnections();
            for (BaseConnection connection : list) {
                LocalProxy resource = (LocalProxy) endpoint.getResource(
                        Endpoint.RESOURCE_AUDIO_SOURCE, connection.getId());
                resource.setInputStream(stream);
            }
        } catch (Exception e) {
        	logger.error("starting of AnnouncementSignal failed",e);
            NotifyEvent report = new NotifyEvent(endpoint,
                    Announcement.FAIL,
                    Announcement.CAUSE_FACILITY_FAILURE,
                    e.getMessage());
            this.sendEvent(report);
        }
    }

    public void stop() {
        logger.info("Terminating signal, endpoint= " + endpoint.getLocalName());
        if (player != null) {
            player.stop();
        }
    }

    public void update(PlayerEvent event) {
        switch (event.getEventID()) {
            case PlayerEvent.STARTED:
                logger.info("annoucement started, endpoint = " + endpoint.getLocalName());
                break;
            case PlayerEvent.END_OF_MEDIA:
                logger.info("annoucement complete, endpoint = " + endpoint.getLocalName());
                NotifyEvent report = new NotifyEvent(endpoint,
                        Announcement.COMPLETE,
                        Announcement.CAUSE_END_OF_MEDIA, null);
                sendEvent(report);
                break;
        }
    }
}
