/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.events.test;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Utils;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.enp.fft.SpectralAnalyser;
import org.mobicents.media.server.spi.NotificationListener;
import static org.junit.Assert.*;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.test.SpectrumEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class SineGeneratorTest {

    private final static int F = 50;
    private final static int TEST_DURATION = 10000;
    private final static int ERROR = 5;
    private ArrayList spectra = new ArrayList();

    public SineGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetFormats() {
        SineGenerator g = new SineGenerator(F);
        AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
        assertEquals(1, g.getFormats().length);
        assertEquals(LINEAR, g.getFormats()[0]);
    }

    /**
     * Test of start method, of class SineGenerator.
     */
    @Test
    @SuppressWarnings("static-access")
    public void testGenerator() throws Exception {
        SineGenerator g = new SineGenerator(F);
        SpectralAnalyser a = new SpectralAnalyser();

        a.addListener(new Registrator());
        a.connect(g);

        g.start();

        try {
            Thread.currentThread().sleep(TEST_DURATION);
        } catch (InterruptedException e) {
            fail("Interrupted");
            return;
        }

        g.stop();

        if (spectra.size() == 0) {
            fail("Empty");
        }

        synchronized (spectra) {
            for (Object s : spectra) {
                double[] ss = (double[]) s;
                int[] ext = Utils.getFreq(ss);
                boolean res = Utils.checkFreq(ext, new int[]{F}, ERROR);
                if (!res) {
                    fail(Utils.getReason());
                }

            }
        }
    }

    private class Registrator implements NotificationListener {

        public void update(NotifyEvent event) {
            double[] s = ((SpectrumEvent) event).getSpectra();
//            for (int i = 0; i < s.length; i++) {
//                System.out.print(s[i] + " ");
//            }
//            System.out.println();
            synchronized (spectra) {
                spectra.add(s);
            }
        }
    }
}