/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.resource.dtmf;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Utils;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.fft.SpectraAnalyzer;
import org.mobicents.media.server.impl.resource.fft.SpectrumEvent;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author kulikov
 */
public class InbandGeneratorImplTest implements NotificationListener {

    private final static int FREQ_ERROR = 5;
    private int MAX_ERRORS = 1;
    
    private Timer timer;
    private InbandGeneratorImpl gen;
    private SpectraAnalyzer det;
    private ArrayList<double[]> s;

    public InbandGeneratorImplTest() {
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
        gen = new InbandGeneratorImpl("Gen", timer);
        gen.setDuration(2000);
        gen.setDigit("0");

        det = new SpectraAnalyzer("det");
        det.addListener(this);
        s = new ArrayList();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDigit method, of class InbandGeneratorImpl.
     */
    @Test
    @SuppressWarnings("static-access")
    public void testDigit0() throws Exception {
        gen.connect(det);
        gen.start();
        det.start();
        
        Thread.currentThread().sleep(2000);
        
        det.stop();
        assertTrue(verify(s, new int[]{941, 1336}));
    }

    private boolean verify(ArrayList<double[]> spectra, int[] F) {
        int errorCount = 0;
        if (spectra.size() == 0) {
            return false;
        }
        
        int i =0;
        for (double[] s : spectra) {            
            int[] ext = Utils.getFreq(s);
            if (ext.length == 0) {
                return false;
            }
            for (int k = 0; k < ext.length; k++) {
                System.out.println(ext[k]);
            }
            System.out.println("-----------------");
            boolean r = Utils.checkFreq(ext, F, FREQ_ERROR);
            if (!r) {
                errorCount++;
            }
        }
        return (errorCount <= MAX_ERRORS);
    }
    
    public void update(NotifyEvent event) {
        switch (event.getEventID()) {
            case SpectrumEvent.SPECTRA :
                System.out.println("Receive spectra");
                SpectrumEvent evt = (SpectrumEvent) event;
                s.add(evt.getSpectra());
                break;
        }
    }
}