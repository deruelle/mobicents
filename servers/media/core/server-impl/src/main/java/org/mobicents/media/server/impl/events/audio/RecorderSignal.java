/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.audio;

import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.Generator;
import org.mobicents.media.server.spi.events.RequestedSignal;

/**
 *
 * @author Oleg Kulikov
 */
public class RecorderSignal extends AbstractSignal {

    private String file;

    public RecorderSignal(String file) {
        this.file = file;
    }
    

    @Override
    public void apply(BaseConnection connection) {
        BaseEndpoint endpoint = (BaseEndpoint) connection.getEndpoint();
        Recorder recorder = (Recorder) endpoint.getMediaSink(Generator.AUDIO_RECORDER, connection);
        recorder.start(file);
    }

    @Override
    public void apply(BaseEndpoint connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
