package org.mobicents.media.server.impl.jmf.recorderplayer.test;

import java.net.MalformedURLException;

import java.util.Timer;
import org.mobicents.media.server.impl.common.events.PlayerEventType;
import org.mobicents.media.server.impl.jmf.player.AudioPlayer;
import org.mobicents.media.server.impl.jmf.player.PlayerEvent;
import org.mobicents.media.server.impl.jmf.player.PlayerListener;

public class PlayerEventTest extends EventSuperCase {

    protected AudioPlayer ap = null;
    private Timer timer = new Timer();

    @Override
    protected EventTypeListener getEventTypeListener() {
        return new PlayerEventTypeListener();
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();
        ap = new AudioPlayer(timer, 20);
        ap.addListener((PlayerListener) currentListener);
    }

    @Override
    protected void tearDown() throws Exception {
        if (ap != null) {
            ap.stop();
        }
        super.tearDown();
    }

    public void testNormalSequence() throws Exception {
        //Normal is start, end_of_media ?
        PlayerEventType[] sequence = new PlayerEventType[]{PlayerEventType.STARTED, PlayerEventType.END_OF_MEDIA};
        super.currentListener.setExpectedQueue(sequence);
        super.setUpFiles("wav", true);
        ap.start(recordFile.toURL().toString());
        if (!doTest(1)) {
            fail(getReason());
        }

    }

    public void testNormalStopSequence() throws Exception {
        PlayerEventType[] sequence = new PlayerEventType[]{PlayerEventType.STARTED, PlayerEventType.STOP_BY_REQUEST};
        super.currentListener.setExpectedQueue(sequence);
        super.setUpFiles("wav", true);
        ap.start(recordFile.toURL().toString());
        ap.stop();
        if (!doTest(1)) {
            fail(getReason());
        }
    }

    public void testErrorSequence_ErrorOnStart_1() throws Exception {
        //that doesnt matter, we will 
        PlayerEventType[] sequence = new PlayerEventType[]{PlayerEventType.STARTED, PlayerEventType.STOP_BY_REQUEST};
        super.currentListener.setExpectedQueue(sequence);
        super.setUpFiles("wav", false);
        boolean _npe, _io, _url;
        _npe = _io = _url = false;
        try {
            ap.start("aaa://tmp");
        } catch (MalformedURLException mue) {
            _url = true;
        }


        if (!_url) {
            doFail("Didnt receive NPE[" + _npe + "] URL[" + _url + "] IO[" + _io + "]");
            fail(getReason());
        }

        ap.stop();
    }

    class PlayerEventTypeListener extends EventTypeListener implements PlayerListener {

        public void update(PlayerEvent event) {
            doOnEventCheck(event.getEventType());

        }
    }
}
