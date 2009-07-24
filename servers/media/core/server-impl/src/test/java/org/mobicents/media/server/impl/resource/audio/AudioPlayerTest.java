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
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.events.FailureEvent;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author kulikov
 * @author amit bhayani
 */
public class AudioPlayerTest {

    private TimerImpl timer;
    private Semaphore semaphore;
    private volatile boolean started = false;
    private volatile boolean failed = false;
    private volatile boolean end_of_media = false;
    private volatile boolean isFormatCorrect = true;
    private volatile boolean isSizeCorrect = true;
    private volatile boolean isCorrectTimestamp = true;
    private volatile boolean isSeqCorrect = true;
    
    private AudioPlayerImpl player;
    private TestSink sink;
    
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
        started = false;
        failed = false;
        end_of_media = false;
        isFormatCorrect = true;
        isSizeCorrect = true;
        isCorrectTimestamp = false;
        isSeqCorrect = false;

        timer = new TimerImpl();
        
        player = new AudioPlayerImpl("test", timer);
        player.addListener(new PlayerListener());
        
        sink = new TestSink("test-sink");
        player.connect(sink);
        
        semaphore = new Semaphore(0);
    }

    @After
    public void tearDown() {
        player.disconnect(sink);
    }

    @Test
    public void testFailure() throws Exception {
        String file = "http://localhost:8080/media-jsr309-servlet/audio/AAABBB.wav";

        player.setURL(file);
        player.start();

        semaphore.tryAcquire(2, TimeUnit.SECONDS);
        assertTrue(failed);
    }

    private void testPlayback(String file, Format fmt, int size) throws Exception {
        URL url = AudioPlayerTest.class.getClassLoader().getResource(file);
        player.setURL(url.toExternalForm());
        sink.setFormat(fmt);
        sink.setSize(size);
        sink.start();
        player.start();
        
        semaphore.tryAcquire(60, TimeUnit.SECONDS);
        
        assertEquals(false, failed);
        assertEquals(true, started);
        assertEquals(true, end_of_media);
        assertEquals(true, isFormatCorrect);
        assertEquals(true, isSizeCorrect);
        assertEquals(true, isSeqCorrect);
    }
    
    @Test
    public void test_GSM() throws Exception {
        testPlayback("org/mobicents/media/server/impl/cnfannouncement.gsm", AVProfile.GSM, 33);
    }

    @Test
    public void test_8000_MONO_ALAW() throws Exception {
        testPlayback("org/mobicents/media/server/impl/addf8-Alaw-GW.wav", AVProfile.PCMA, 160);
    }

    @Test
    public void test_8000_MONO_ULAW() throws Exception {
        testPlayback("org/mobicents/media/server/impl/8kulaw.wav", AVProfile.PCMU,160);
    }

    @Test
    public void test_Wav_L16_8000() throws Exception {
        testPlayback("org/mobicents/media/server/impl/dtmf-0.wav", Codec.LINEAR_AUDIO, 320);
    }

    @Test
    public void test_L16_44100_MONO() throws Exception {
        testPlayback("org/mobicents/media/server/impl/gwn44m.wav", AVProfile.L16_MONO, 1764);
    }

    @Test
    public void test_L16_44100_STEREO() throws Exception {
        testPlayback("org/mobicents/media/server/impl/gwn44s.wav", AVProfile.L16_STEREO, 1764*2);
    }

    @Test
    public void test_SpeexNB() throws Exception {
        testPlayback("org/mobicents/media/server/impl/sin8m.spx", AVProfile.SPEEX, 160);
    }

    private class TestSink extends AbstractSink {

        private long lastTick = 0;
        private long lastSeqNo = 0;

        private Format fmt;
        private int size;
        
        private TestSink(String name) {
            super(name);
        }

        public void setFormat(Format fmt) {
            this.fmt = fmt;
        }
        
        public void setSize(int size) {
            this.size = size;
        }
        
        public Format[] getFormats() {
            return new Format[0];
        }

        public boolean isAcceptable(Format format) {
            return true;
        }

        @Override
        public void onMediaTransfer(Buffer buffer) {
            if (!buffer.isEOM()) {
                isFormatCorrect &= buffer.getFormat().matches(fmt);
                isSizeCorrect = ((buffer.getLength()) == size);

                lastTick = buffer.getTimeStamp();

                if (lastSeqNo > 0) {
                    isSeqCorrect = (buffer.getSequenceNumber() - lastSeqNo) == 1;
                }
                lastSeqNo = buffer.getSequenceNumber();
            }
        }
    }

    private class PlayerListener implements NotificationListener {

        public void update(NotifyEvent event) {
            switch (event.getEventID()) {
                case AudioPlayerEvent.STARTED:
                    started = true;
                    break;
                case AudioPlayerEvent.COMPLETED :
                    end_of_media = true;
                    semaphore.release();
                    break;
                case AudioPlayerEvent.START_FAILED:
                    failed = true;
                    ((FailureEvent) event).getException().printStackTrace();
                    semaphore.release();
                    break;
            }
        }
    }
}