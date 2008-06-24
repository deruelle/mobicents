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
package org.mobicents.media.server.impl.ivr;

import org.mobicents.media.server.impl.ann.*;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.Signal;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.common.events.EventCause;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.impl.jmf.player.AudioPlayer;
import org.mobicents.media.server.impl.jmf.player.PlayerEvent;
import org.mobicents.media.server.impl.jmf.player.PlayerListener;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.impl.common.events.*;
import org.mobicents.media.server.impl.test.audio.SineStream;

/**
 *
 * @author Oleg Kulikov
 */
public class SineSignal extends Signal {

    protected AnnEndpointImpl endpoint;
    
    private String[] params;
    private SineStream player;
    private AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
    
    private Logger logger = Logger.getLogger(SineSignal.class);

    /**
     * Creates a new instance of AnnouncementSignal
     */
    public SineSignal(AnnEndpointImpl endpoint,
            NotificationListener listener, String params[]) {
        super(listener);
        
        this.endpoint = endpoint;
        this.params = params;
        
        player = new SineStream(50, endpoint.getPacketizationPeriod(), false);
    }

    public void start() {
        try {
            player.start();
            Collection <BaseConnection> list = endpoint.getConnections();
            for (BaseConnection connection : list) {
                LocalProxy resource = (LocalProxy) endpoint.getResource(
                		MediaResourceType.AUDIO_SOURCE, connection.getId());
                resource.setInputStream(player);
            }
        } catch (Exception e) {
            logger.error("Could not start announcement signal", e);
            NotifyEvent report = new NotifyEvent(endpoint,
                    EventID.FAIL,
                    EventCause.FACILITY_FAILURE,
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
        switch (event.getEventType()) {
            case STARTED:
                logger.info("annoucement started, endpoint = " + endpoint.getLocalName());
                break;
            case END_OF_MEDIA:
                logger.info("annoucement complete, endpoint = " + endpoint.getLocalName());
                NotifyEvent report = new NotifyEvent(endpoint,
                        EventID.COMPLETE,
                        EventCause.END_OF_MEDIA, null);
                sendEvent(report);
                break;
        }
    }
}
