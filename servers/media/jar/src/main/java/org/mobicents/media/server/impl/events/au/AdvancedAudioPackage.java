/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.events.au;

import java.io.Serializable;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.Signal;
import org.mobicents.media.server.impl.ann.AnnouncementSignal;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.common.events.EventCause;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.impl.jmf.player.AudioPlayer;
import org.mobicents.media.server.impl.jmf.player.PlayerEvent;
import org.mobicents.media.server.impl.jmf.player.PlayerListener;
import org.mobicents.media.server.impl.jmf.recorder.Recorder;
import org.mobicents.media.server.impl.jmf.recorder.RecorderEvent;
import org.mobicents.media.server.impl.jmf.recorder.RecorderListener;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaSink;
import org.mobicents.media.server.spi.MediaSource;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class AdvancedAudioPackage implements Serializable {

    private BaseEndpoint endpoint;
    
    public AdvancedAudioPackage(Endpoint endpoint) {
        this.endpoint = (BaseEndpoint) endpoint;
    }

    public Signal play(EventID signalID, HashMap params, String connectionID, NotificationListener listener) {
        if (signalID == EventID.PLAY_RECORD) {
            Signal signal = new PlayRecordSignal(endpoint, connectionID,
                    (String) params.get("announcement.url"),
                    (String) params.get("record.url"),
                    listener);
            signal.start();
            return signal;
        }
        return null;
    }

    class PlayRecordSignal extends Signal implements PlayerListener, RecorderListener {

        private BaseEndpoint endpoint;
        private AudioPlayer player;
        private Recorder recorder;
        private String connectionID;
        private String announcementURL;
        private String recorderURL;
        private AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
        private Logger logger = Logger.getLogger(AnnouncementSignal.class);

        public PlayRecordSignal(Endpoint endpoint, String connectionID,
                String announcementURL, String recorderURL, NotificationListener listener) {
            super(listener);

            this.endpoint = (BaseEndpoint) endpoint;
            this.announcementURL = announcementURL;
            this.recorderURL = recorderURL;
            this.connectionID = connectionID;

            player = new AudioPlayer(
                    ((BaseEndpoint) endpoint).getTimer(),
                    endpoint.getPacketizationPeriod());
            player.setFormat(audioFormat);
            player.addListener(this);

            recorder = new Recorder(null);
            recorder.addListener(this);
        }

        @Override
        public void start() {
            try {
                PushBufferStream stream = (PushBufferStream) player.start(announcementURL);
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
            if (recorder != null) {
                recorder.stop();
            }
        }

        public void update(PlayerEvent event) {
            switch (event.getEventType()) {
                case STARTED:
                    logger.info("annoucement started, endpoint = " + endpoint.getLocalName());
                    break;
                case END_OF_MEDIA:
                    logger.info("annoucement complete, endpoint = " + endpoint.getLocalName());
                    MediaSink mediaSink = (MediaSink) endpoint.getResource(
                            MediaResourceType.AUDIO_SINK, connectionID);
                    PushBufferStream stream = mediaSink.newBranch("Recorder:" + connectionID);
                    recorder.start(recorderURL, stream);
                    break;
            }
        }

        public void update(RecorderEvent event) {
            switch (event.getEventType()) {
                case STARTED:
                    logger.info("recorder started, endpoint = " + endpoint.getLocalName());
                    break;
                case STOP_BY_REQUEST:
                    logger.info("recorder stopped, endpoint = " + endpoint.getLocalName());
                    MediaSink mediaSink = (MediaSink) endpoint.getResource(
                            MediaResourceType.AUDIO_SINK, connectionID);
                    mediaSink.remove("Recorder:" + connectionID);
                    NotifyEvent report = new NotifyEvent(endpoint,
                            EventID.COMPLETE,
                            EventCause.END_OF_MEDIA, null);
                    sendEvent(report);
                    break;
            }
        }
    }
}