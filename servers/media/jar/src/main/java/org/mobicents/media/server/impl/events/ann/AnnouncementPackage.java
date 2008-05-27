/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.ann;

import java.io.Serializable;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.Signal;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.common.events.EventCause;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.impl.jmf.player.AudioPlayer;
import org.mobicents.media.server.impl.jmf.player.PlayerEvent;
import org.mobicents.media.server.impl.jmf.player.PlayerListener;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaSource;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class AnnouncementPackage implements Serializable {
    private BaseEndpoint endpoint;
    private Logger logger = Logger.getLogger(AnnouncementPackage.class);
    
    public AnnouncementPackage(Endpoint endpoint) {
        this.endpoint = (BaseEndpoint) endpoint;
    }

    public Signal play(EventID signalID, HashMap params, String connectionID, NotificationListener listener) {
        if (signalID == EventID.PLAY) {
            Signal signal = new AnnouncementSignal(endpoint, connectionID, 
                    (String)params.get("announcement.url"), listener);
            signal.start();
            return signal;
        }
        return null;
    }
}

class AnnouncementSignal extends Signal implements PlayerListener {

    private BaseEndpoint endpoint;
    private AudioPlayer player;
    
    private String connectionID;
    private String url;
    
    private AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);    
    private Logger logger = Logger.getLogger(AnnouncementSignal.class);
    
    public AnnouncementSignal(Endpoint endpoint, String connectionID, 
            String url, NotificationListener listener) {
        super(listener);
        
        this.endpoint = (BaseEndpoint) endpoint;
        this.url = url;
        this.connectionID = connectionID;
        
        player = new AudioPlayer(endpoint.getPacketizationPeriod());
        player.setFormat(audioFormat);
        player.addListener(this);
    }
    
    @Override
    public void start() {
        try {
            PushBufferStream stream = (PushBufferStream) player.start(url);
            MediaSource mediaSource = (MediaSource) endpoint.getResource(
                		MediaResourceType.AUDIO_SOURCE, connectionID);
            mediaSource.add("Announcement", stream);
        } catch (Exception e) {
            logger.error("Could not start announcement signal", e);
            NotifyEvent report = new NotifyEvent(endpoint,
                    EventID.FAIL,
                    EventCause.FACILITY_FAILURE,
                    e.getMessage());
            this.sendEvent(report);
        }
    }

    @Override
    public void stop() {
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