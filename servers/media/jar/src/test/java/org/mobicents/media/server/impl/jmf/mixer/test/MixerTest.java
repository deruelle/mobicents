package org.mobicents.media.server.impl.jmf.mixer.test;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mobicents.media.server.impl.NewSuperXCase;
import org.mobicents.media.server.impl.fft.SpectralAnalyser;
import org.mobicents.media.server.impl.jmf.mixer.AudioMixer;
import org.mobicents.media.server.impl.test.audio.SineStream;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.test.SpectrumEvent;

public class MixerTest extends NewSuperXCase implements NotificationListener {

    private final static int FREQ[] = new int[]{50,100,150};
    private final static int PACKETIZATION_PERIOD = 20;
    private final static int JITTER = 60;
    private final static int ERROR = 10;
    private final static int TEST_DURATION = 5;
    
    private int count = 0;
    
    protected AudioMixer testSubject = null;
    protected AudioMixer mixer = null;
    
    protected File dumpFile = null;
    
    private SpectralAnalyser analyser;
    private SineStream[] sineStream;

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(MixerTest.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception {
        mixer = new AudioMixer(PACKETIZATION_PERIOD, JITTER);
        
        sineStream = new SineStream[FREQ.length];
        for (int i = 0; i < FREQ.length; i++) {
            sineStream[i] = new SineStream(FREQ[i], PACKETIZATION_PERIOD, false);
            mixer.addInputStream(sineStream[i]);
        }
        
        analyser = new SpectralAnalyser();
        analyser.addListener(this);
        analyser.configure(null);

    }

    @Override
    protected void tearDown() throws Exception {
        analyser.stop();
        mixer.stop();
        
        for (int i = 0; i < FREQ.length; i++) {
            sineStream[i].stop();
        }
    }

    public void testSignal() throws Exception {
        for (int i = 0; i < FREQ.length; i++) {
            sineStream[i].start();
        }
        
        mixer.start();
        analyser.prepare(null, mixer.getOutputStream());
        analyser.start();
        
        if (!doTest(TEST_DURATION)) {
            fail("Test failed on "  + count + " second," + getReason());
        }
    }
    
    public void update(NotifyEvent event) {
        count++;
        
        double spectra[] = ((SpectrumEvent) event).getSpectra();
        boolean res = checkFreq(spectra, FREQ, ERROR);
        if (!res) {
            doFail(null);
        }
    }

}
