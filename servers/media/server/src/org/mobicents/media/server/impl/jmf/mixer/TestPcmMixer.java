/*
 * TestPcmMixer.java
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

package org.mobicents.media.server.impl.jmf.mixer;

import javax.media.Format;
import javax.media.Manager;
import javax.media.Player;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.player.AudioPlayer;
import org.mobicents.media.server.impl.rtp.RtpDataSource;

/**
 *
 * @author Oleg Kulikov
 */
public class TestPcmMixer {
    
    public static AudioFormat fmt = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
                
    //public static Timer timer = new Timer();
    public static int PERIOD = 20;
    
    /** Creates a new instance of TestPcmMixer */
    public TestPcmMixer() {
    }
     
    private PushBufferStream startSignal(String url) throws Exception {
        AudioPlayer audioPlayer = new AudioPlayer(PERIOD);
        audioPlayer.setFormat(fmt);
        //audioPlayer.setTimer(timer);
        //audioPlayer.addListener(new TestListener());
        return audioPlayer.start(url);
    }
    
    private void startPlayer(PushBufferStream stream) throws Exception {        
        DataSource ds = new RtpDataSource(stream);
        ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW);

        ProcessorModel pm = new ProcessorModel(ds, new Format[]{Codec.PCMU}, cd);
        Processor dsp = Manager.createRealizedProcessor(pm);
        dsp.start();
        
        DataSource dso = dsp.getDataOutput();
        dso.start();
        
        Player player = Manager.createRealizedPlayer(dso);
        player.start();
    }
    
    public void test() throws Exception {
        AudioMixer mixer = new AudioMixer(PERIOD, 100, Codec.PCMU);
        //mixer.setTimer(timer);
        PushBufferStream s1 = startSignal("file:/c:/sounds/welcome-l.wav");
        PushBufferStream s2 = startSignal("file:/c:/sounds/fox-full.wav");
        
        mixer.addInputStream(s2);
        mixer.addInputStream(s1);
        mixer.start();
        startPlayer(mixer.getOutputStream());
        
//        Thread.currentThread().sleep(9000);
        //mixer.stop();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        TestPcmMixer mixerTest = new TestPcmMixer();
        mixerTest.test();
    }
    
}
