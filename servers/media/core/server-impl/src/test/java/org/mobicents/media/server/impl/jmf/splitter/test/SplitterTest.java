package org.mobicents.media.server.impl.jmf.splitter.test;


import org.mobicents.media.server.impl.SuperXCase;
import org.mobicents.media.server.impl.fft.SpectralAnalyser;
import org.mobicents.media.server.impl.jmf.splitter.MediaSplitter;
import org.mobicents.media.server.impl.test.audio.SineStream;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.test.SpectrumEvent;

public class SplitterTest extends SuperXCase {

    private final static int PACKETIZATION_PERIOD = 20;
    private final static int JITTER = 60;

    private final static int ERROR = 10;
    
    private final static int FREQ = 50;
    private final static int STREAMS = 3;
    private final static int TEST_DURATION = 5;
    private MediaSplitter splitter;
    private SpectralAnalyser analysers[];
    private SineStream sineStream;
    private int count = 0;

    @Override
    protected void setUp() throws Exception {
        splitter = new MediaSplitter();
        sineStream = new SineStream(FREQ, 20, false);

        analysers = new SpectralAnalyser[STREAMS];
        for (int i = 0; i < STREAMS; i++) {
            analysers[i] = new SpectralAnalyser();
            analysers[i].addListener(new StreamHandler());
            analysers[i].configure(null);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        sineStream.stop();
        for (int i = 0; i < STREAMS; i++) {
            analysers[i].stop();
        }
    }

    public void testSignal() throws Exception {
        splitter.setInputStream(sineStream);
        sineStream.start();

        for (int i = 0; i < STREAMS; i++) {
            analysers[i].prepare(null, splitter.newBranch());
        }

        if (!doTest(TEST_DURATION)) {
            fail("Test failed on " + count + " second," + getReason());
        }

    }

    private class StreamHandler implements NotificationListener {

        public synchronized void update(NotifyEvent event) {
            count++;

            double spectra[] = ((SpectrumEvent) event).getSpectra();
            boolean res = checkFreq(spectra, new int[]{FREQ}, ERROR);
            if (!res) {
                doFail(null);
            }
        }
    }
}
