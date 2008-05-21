/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.test.audio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.media.server.impl.NewSuperXCase;
import static org.junit.Assert.*;
import org.mobicents.media.server.impl.fft.SpectralAnalyser;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.test.SpectrumEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class SineStreamTest extends NewSuperXCase implements NotificationListener {

    private final static int FREQ = 50;
    private final static int ERROR = 10;
    private final static int TEST_DURATION = 5;
    
    private SpectralAnalyser analyser;
    private SineStream sineStream;
    private int count = 0;
    
    public SineStreamTest() {
    }

    @Before
    @Override
    public void setUp() {
        analyser = new SpectralAnalyser();
        analyser.addListener(this);
        analyser.configure(null);
        sineStream = new SineStream(FREQ, 20, false);
    }

    @After
    @Override
    public void tearDown() {
        sineStream.stop();
        analyser.stop();
    }
    /**
     * Test signal
     */
    @Test
    public void testSignal() throws Exception {
        System.out.println("STARTING TEST");
        analyser.prepare(null, sineStream);
        sineStream.start();
        analyser.start();

        
        if (!doTest(TEST_DURATION)) {
            fail("Test failed on "  + count + " second," + getReason());
        }
    }

    public void update(NotifyEvent event) {
        count++;
        
        double spectra[] = ((SpectrumEvent) event).getSpectra();
        boolean res = checkFreq(spectra, new int[]{FREQ}, ERROR);
        if (!res) {
            doFail(null);
        }
    }

}
