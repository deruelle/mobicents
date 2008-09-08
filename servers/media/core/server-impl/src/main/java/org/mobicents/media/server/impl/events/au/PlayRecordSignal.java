/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.au;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.events.ann.AnnSignal;
import org.mobicents.media.server.impl.events.ann.PlayerEvent;
import org.mobicents.media.server.spi.events.EventDetector;
import org.mobicents.media.server.spi.events.Options;

/**
 *
 * @author Oleg Kulikov
 */
public class PlayRecordSignal extends AnnSignal implements EventDetector {

    private Recorder recorder;
    
    public PlayRecordSignal(Options options) {
        super(options);
        recorder = new Recorder("wav");
    }
    
    @Override
    public String getID() {
        return "PLAY_RECORD";
    }


    @Override
    public void start() {
        System.out.println("*** START RECORDING TO " + (String) options.get("recorder.url"));
        super.start();
        recorder.start((String) options.get("recorder.url"));
    }

    @Override
    public void stop() {
        super.stop();
        recorder.stop();
    }

    @Override
    public void update(PlayerEvent event) {
        super.update(event);
    }

    public Object getParameter(String name) {
        return null;
    }

    public void setParameter(String name, Object value) {
    }

    public boolean isAcceptable(Format format) {
        return true;
    }

    public void connect(MediaSource source) {
        recorder.connect(source);
    }

    public void disconnect(MediaSource source) {
        recorder.disconnect(source);
    }

    public void receive(Buffer buffer) {
    }

}
