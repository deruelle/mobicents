/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.enp.cnf;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Format;
import org.mobicents.media.Utils;
import org.mobicents.media.format.AudioFormat;
import static org.junit.Assert.*;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.events.test.SineGenerator;
import org.mobicents.media.server.impl.enp.fft.SpectralAnalyser;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.test.SpectrumEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class AudioMixerTest {

    private final static int TEST_DURATION = 10000;
    private int[] F = new int[] {50,150, 450};    
    private final static int FREQ_ERROR = 3;
    
    private AudioMixer mixer;
    private ArrayList spectra = new ArrayList();
    private int errorCount;
    private int MAX_ERRORS = 3 * TEST_DURATION / 10000;
    public AudioMixerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        mixer = new AudioMixer("test");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of connect method, of class AudioMixer.
     */
    @Test
    public void testConnectDisconnect() {
        Source src = new Source();
        try {
            mixer.connect(src);
            assertEquals(mixer.getInputCount(), 1);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        mixer.disconnect(src);
        assertEquals(mixer.getInputCount(), 0);
    }

    @Test
    public void testGetFormats() {
        AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR,8000, 16, 1,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
        assertEquals(1, mixer.getFormats().length);
        assertEquals(LINEAR, mixer.getFormats()[0]);
    }

    @Test
    public void testOutputFormats() {
        AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR,8000, 16, 1,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
        assertEquals(1, mixer.getOutput().getFormats().length);
        assertEquals(LINEAR, mixer.getOutput().getFormats()[0]);
    }
    
    @Test
    @SuppressWarnings("static-access")
    public void testTransmission() {
        SpectralAnalyser a = new SpectralAnalyser();
        
        SineGenerator g0 = new SineGenerator(F[0]);
        SineGenerator g1 = new SineGenerator(F[1]);
        SineGenerator g2 = new SineGenerator(F[2]);
        
        try {
            mixer.connect(g0);
            mixer.connect(g1);
            mixer.connect(g2);
        
            mixer.start();
        
            a.addListener(new Registrator());
            a.connect(mixer.getOutput()); 
        
            g0.start();
            g1.start();
            g2.start();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        try {
            Thread.currentThread().sleep(TEST_DURATION);
        } catch (InterruptedException e) {
            fail("Interrupted");
            return;
        }
        
        g0.stop();
        g1.stop();
        g2.stop();
        
        mixer.stop();
        
        if (spectra.size() ==0) {
            fail("Empty");
        }
        
        for (Object s : spectra ) {
            double[] ss = (double[])s;
            int[] ext = Utils.getFreq(ss);
           
//            for (int i = 0; i < ext.length; i++) {
//                System.out.print(ext[i] + " ");
//            }
//            System.out.println("-----------------");
            boolean res = Utils.checkFreq(ext, F, FREQ_ERROR);
            if (!res) {
                errorCount++;
            }
        }
        if (errorCount > MAX_ERRORS) {
            fail("Total errors=" + errorCount + ", max allowed=" + MAX_ERRORS);
        }
  
    }
    
    private class Source extends AbstractSource {

        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Format[] getFormats() {
            return null;
        }
    }

    private class Registrator implements NotificationListener {

        public void update(NotifyEvent event) {
            double[] s = ((SpectrumEvent)event).getSpectra();
            spectra.add(s);
        }
        
    }
    
}