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
package org.mobicents.media.server.impl.jmf.proxy;

import org.mobicents.media.server.impl.jmf.player.PlayerListener;
import javax.media.protocol.PushBufferStream;
import java.util.Date;
import java.util.Timer;
import javax.media.Manager;
import javax.media.Player;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.player.AudioPlayer;
import org.mobicents.media.server.impl.jmf.player.PlayerEvent;
import org.mobicents.media.server.impl.packetrelay.LocalDataSource;

/**
 *
 * @author Oleg Kulikov
 */
public class TestProxy {

    private static AudioFormat fmt = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

    private AudioPlayer audioPlayer;
    private Player player;
    private MediaPushProxy proxy = new MediaPushProxy(20, Codec.PCMU);
    private DataSource ds;
    private PushBufferDataSource dss;
    private Timer timer;
    
    private TestProxy() {
        
        audioPlayer = new AudioPlayer(20);
        audioPlayer.setFormat(fmt);
        audioPlayer.setTimer(timer);
        audioPlayer.addListener(new TestListener());
    }

    private class TestListener implements PlayerListener {

        public void update(PlayerEvent evt) {
            switch (evt.getEventID()) {
                case PlayerEvent.STARTED:
                    System.out.println(new Date() + " started");
                    break;
                case PlayerEvent.END_OF_MEDIA:
                    System.out.println(new Date() + " stopped");
                    try {
                        Thread.currentThread().sleep(3000);
                        doTest();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private PushBufferStream generateStream() throws Exception {
        return audioPlayer.start("file:/c:/sounds/welcome.wav");
    }

    private void play(PushBufferStream stream) throws Exception {
        //System.out.println("Creating splitter");
        //proxy.addListener(new TestListener());

        proxy.setInputStream(stream);
        DataSource localDs = new LocalDataSource(proxy);

        //System.out.println("Playing");
        Player player = Manager.createRealizedPlayer(localDs);
        player.start();
    }

    private void doTest() throws Exception {
        PushBufferStream stream = generateStream();
        play(stream);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        TestProxy t = new TestProxy();
        t.doTest();
    }
}
