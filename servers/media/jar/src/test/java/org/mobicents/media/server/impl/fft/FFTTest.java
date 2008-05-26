/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.fft;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Oleg Kulikov
 */
public class FFTTest {

    private final static int F = 4;
    
    public FFTTest() {
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

    /**
     * Test of fft method, of class FFT.
     */
    @Test
    public void fft() {
        Complex[] signal = new Complex[64];
        int len = signal.length;
        for (int i = 0; i < signal.length; i++ ) {
            short s = (short) (Short.MAX_VALUE * Math.sin(2 * Math.PI * F * i / len));
            System.out.println(s);
            signal[i] = new Complex(s, 0);
        }
        
        Complex[] f = FFT.fft(signal);
        
        for (int i = 0; i < f.length; i++) {
            System.out.println(i + " " + Math.sqrt(f[i].re() * f[i].re() + f[i].im()*f[i].im()));
        }
    }


}