/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.audio;

import java.net.URL;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author kulikov
 */
public class AudioPlayerTest {

    private EndpointImpl endpoint;
    private TimerImpl timer;
    private Semaphore semaphore;
    
    private volatile boolean started = false;
    private volatile boolean failed = false;
    private volatile boolean end_of_media = false;
    private volatile boolean isFormatCorrect = true;
    private volatile boolean isSizeCorrect = true;
    private volatile boolean isCorrectTimestamp = true;
    private volatile boolean isSeqCorrect = true;
    
    private boolean res = false;
    
    private final static Format[] formats = new Format[]{
        AVProfile.L16_MONO,
        AVProfile.L16_STEREO,
        AVProfile.PCMA,
        AVProfile.PCMU,
        AVProfile.SPEEX,
        AVProfile.GSM,
        Codec.LINEAR_AUDIO                
    };
    private AudioPlayer player;
    
    public AudioPlayerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        timer = new TimerImpl();
        endpoint = new EndpointImpl();
        endpoint.setTimer(timer);
        player = new AudioPlayer("test");
        player.setEndpoint(endpoint);
        player.setResourceType(10);
        player.addListener(new PlayerListener());
        semaphore = new Semaphore(0);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSupportedFormats() {
        Format[] supported = player.getFormats();
        assertEquals(formats.length, supported.length);
        for (int i = 0; i < supported.length; i++) {
            boolean found = false;
            for (int j = 0; j < formats.length; j++) {
                if (supported[i].equals(formats[j])) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("Not found " + supported[i]);
            }
        }
    }

    /**
     * Test of getResourceType method, of class AudioPlayer.
     */
    @Test
    public void testGetResourceType() {
        assertEquals(10, player.getResourceType());
    }

    @Test
    public void test_Wav_L16_8000() throws Exception {
        //URL url = AudioPlayerTest.class.getClassLoader().getResource("org/mobicents/media/server/impl/dtmf-0.wav");
        URL url = new URL("file:///home/abhayani/workarea/mobicents/svn/trunk/servers/media/core/server-impl/src/test/resources/org/mobicents/media/server/impl/dtmf-0.wav");
        player.setFile(url.toExternalForm());
        player.connect(new TestSink("test"));
        player.start();
        
        semaphore.tryAcquire(10, TimeUnit.SECONDS);
        assertEquals(false, failed);
        assertEquals(true, started);
        assertEquals(true, end_of_media);
        assertEquals(true, isFormatCorrect);
        assertEquals(true, isSizeCorrect);
    }
    
    private class TestSink extends AbstractSink {
        private long lastTick = 0;
        private long lastSeqNo = 0;
        
        private TestSink(String name) {
            super(name);
        }
        
        public Format[] getFormats() {
            return new Format[0];
        }

        public boolean isAcceptable(Format format) {
            return true;
        }

        public void receive(Buffer buffer) {
            if (!buffer.isEOM()) {
                isFormatCorrect &= buffer.getFormat().matches(Codec.LINEAR_AUDIO);
                isSizeCorrect = ((buffer.getLength() - buffer.getOffset()) == 320);
                
                if (lastTick > 0) {
                    isCorrectTimestamp = (buffer.getTimeStamp() - lastTick) == timer.getHeartBeat();
                    lastTick = buffer.getTimeStamp();
                }
                
                if (lastSeqNo > 0) {
                    isSeqCorrect = (buffer.getSequenceNumber() - lastSeqNo) == 1;
                    lastSeqNo = buffer.getSequenceNumber();
                }
            }
        }

    }
    
    private class PlayerListener implements NotificationListener {

        public void update(NotifyEvent event) {
            switch (event.getEventID()) {
                case AudioPlayerEvent.STARTED :
                	System.out.println("Started ");
                    started = true;
                    break;
                case AudioPlayerEvent.END_OF_MEDIA :
                	System.out.println("End ");
                   end_of_media = true;
                   semaphore.release();
                   break;
                case AudioPlayerEvent.FAILED :
                	System.out.println("Failed ");
                    failed = true;
                    semaphore.release();
                    break;
            }
        }
        
    }
}