/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.audio;

import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.MediaResource;

/**
 *
 * @author Oleg Kulikov
 */
public class RecorderSignal extends AbstractSignal {

    private String file;
    private Recorder recorder;
    
    public RecorderSignal(String file) {
        this.file = file;
    }
    

    @Override
    public void apply(BaseConnection connection) {
        BaseEndpoint endpoint = (BaseEndpoint) connection.getEndpoint();
        recorder = (Recorder) getMediaSink(MediaResource.AUDIO_RECORDER, connection);
        recorder.start(file);
    }

    @Override
    public void apply(BaseEndpoint connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cancel() {
        if (recorder != null) {
            recorder.stop();
        }
    }

}
