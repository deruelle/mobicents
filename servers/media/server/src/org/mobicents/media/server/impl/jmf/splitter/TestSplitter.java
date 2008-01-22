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
package org.mobicents.media.server.impl.jmf.splitter;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.Player;
import javax.media.Processor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.jmf.player.AudioPlayer;
import org.mobicents.media.server.impl.jmf.proxy.MediaPushProxy;
import org.mobicents.media.server.impl.packetrelay.LocalDataSource;

/**
 *
 * @author Oleg Kulikov
 */
public class TestSplitter {

    MediaSplitter mediaSplitter = new MediaSplitter();
    private Player player;
    
    private PushBufferStream pushStream1;
    private PushBufferStream pushStream2;
    
    private class Repiter implements ControllerListener {
        public void controllerUpdate(ControllerEvent evt) {
            if (evt instanceof EndOfMediaEvent) {
                System.out.println("Repeat");
                try {
                    Thread.currentThread().sleep(20000);
                } catch (Exception e) {
                    return;
                }
                
                Processor processor = (Processor) evt.getSource();
                processor.stop();
                processor.close();
                processor.deallocate();
                
//                player.stop();
//                player.close();
//                player.deallocate();
                
                try {
                    doTest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
    private PushBufferStream generateStream() throws Exception {
        AudioPlayer generator = new AudioPlayer(20);
        return generator.start("file:/c:/sounds/welcome.wav");
    }

    private void play(PushBufferStream stream) throws Exception {
        DataSource localDs = new LocalDataSource(stream);
        Player player = Manager.createRealizedPlayer(localDs);
        player.start();
    }

    private void doTest() throws Exception {
        PushBufferStream stream = generateStream();
        MediaPushProxy proxy =new MediaPushProxy(20, null);
        proxy.setInputStream(stream);
        mediaSplitter.setInputStream(proxy);
        
        System.out.println("Got branch");
        if (pushStream1 == null) {
            pushStream1 = mediaSplitter.newBranch();
        }
        
        System.out.println("Play branch");
        play(pushStream1);
        
        Thread.currentThread().sleep(3000,0);
        
        System.out.println("Got branch");
        if (pushStream2 == null) {
            pushStream2 = mediaSplitter.newBranch();
        }
        System.out.println("Play branch");
        play(pushStream2);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        TestSplitter t = new TestSplitter();
        t.doTest();
    }
}
