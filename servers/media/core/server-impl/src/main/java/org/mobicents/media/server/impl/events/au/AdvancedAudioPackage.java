/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.events.au;

import java.io.Serializable;

import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.events.EventDetector;
import org.mobicents.media.server.spi.events.EventPackage;
import org.mobicents.media.server.spi.events.Options;
import org.mobicents.media.server.spi.events.Signal;

/**
 * 
 * @author Oleg Kulikov
 */
public class AdvancedAudioPackage implements EventPackage {

    private BaseEndpoint endpoint;

    public AdvancedAudioPackage(Endpoint endpoint) {
        this.endpoint = (BaseEndpoint) endpoint;
    }

    public Signal getSignal(String signalID, Options options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EventDetector getDetector(String signalID, Options options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

/*    public Signal play(EventID signalID, HashMap params, String connectionID, NotificationListener listener,
            boolean startRecordingImmediately) {
        if (signalID == EventID.PLAY_RECORD) {
            Signal signal = new PlayRecordSignal(endpoint, connectionID, (String) params.get("announcement.url"),
                    (String) params.get("record.url"), listener, startRecordingImmediately);
            signal.start();
            return signal;
        }
        return null;
    }
*/
  /*  class PlayRecordSignal extends Signal implements PlayerListener, RecorderListener {

        private BaseEndpoint endpoint;
        private AudioPlayer player;
        private Recorder recorder;
        private String connectionID;
        private String announcementURL;
        private String recorderURL;
        private boolean startRecordingImmediately = false;
        private AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
        private Logger logger = Logger.getLogger(PlayRecordSignal.class);

        public PlayRecordSignal(Endpoint endpoint, String connectionID, String announcementURL, String recorderURL,
                NotificationListener listener, boolean startRecordingImmediately) {
            super(listener);

            this.endpoint = (BaseEndpoint) endpoint;
            this.announcementURL = announcementURL;
            this.recorderURL = recorderURL;
            this.startRecordingImmediately = startRecordingImmediately;
            this.connectionID = connectionID;

            player = new AudioPlayer();
//            player.setFormat(audioFormat);
            player.addListener(this);

            recorder = new Recorder(null);
            recorder.addListener(this);
        }

        @Override
        public void start() {
            try {
//                PushBufferStream stream = (PushBufferStream) player.start(announcementURL);
//                MediaStream stream =  player.startPlayer(announcementURL);
//                MediaSource mediaSource = (MediaSource) endpoint.getResource(MediaResourceType.AUDIO_SOURCE,
//                        connectionID);
//                mediaSource.add("Announcement", stream);
            } catch (Exception e) {
                logger.error("Could not start announcement signal", e);
                NotifyEvent report = new NotifyEvent(endpoint, EventID.FAIL, EventCause.FACILITY_FAILURE, e.getMessage());
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
                    logger.info("PlayRecordSignal. Annoucement started, endpoint = " + endpoint.getLocalName());
                    if (startRecordingImmediately) {
//                        MediaSink mediaSink = (MediaSink) endpoint.getResource(MediaResourceType.AUDIO_SINK, connectionID);
//                        PushBufferStream stream = mediaSink.newBranch("Recorder:" + connectionID);
//                        recorder.start(recorderURL, stream);
                    }
                    break;
                case END_OF_MEDIA:
                    logger.info("PlayRecordSignal. Annoucement complete, endpoint = " + endpoint.getLocalName());
                    if (!startRecordingImmediately) {
//                        MediaSink mediaSink = (MediaSink) endpoint.getResource(MediaResourceType.AUDIO_SINK, connectionID);
//                        PushBufferStream stream = mediaSink.newBranch("Recorder:" + connectionID);
//                        recorder.start(recorderURL, stream);
                    }
                    NotifyEvent report = new NotifyEvent(endpoint, EventID.COMPLETE, EventCause.END_OF_MEDIA, null);
                    sendEvent(report);
                    break;
            }
        }

        public void update(RecorderEvent event) {
            switch (event.getEventType()) {
                case STARTED:
                    logger.info("PlayRecordSignal. Recorder started, endpoint = " + endpoint.getLocalName());
                    break;
                case STOP_BY_REQUEST:
                    logger.info("PlayRecordSignal. Rrecorder stopped, endpoint = " + endpoint.getLocalName());
//                    MediaSink mediaSink = (MediaSink) endpoint.getResource(MediaResourceType.AUDIO_SINK, connectionID);
//                    mediaSink.remove("Recorder:" + connectionID);
                    NotifyEvent report = new NotifyEvent(endpoint, EventID.COMPLETE, EventCause.END_OF_MEDIA, null);
                    sendEvent(report);
                    break;
            }
        }
    }
   */ 
}
