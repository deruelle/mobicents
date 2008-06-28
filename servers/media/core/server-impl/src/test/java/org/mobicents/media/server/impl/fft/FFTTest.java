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

    private final static int F = 697;
    private final static int F1 = 1209;
    
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
        int N = 8000;
        int M = 8192;
        
        Complex[] signal = new Complex[N];
        
        for (int i = 0; i < N; i++ ) {
            short s = (short)(
                    (short) (Short.MAX_VALUE/2 * Math.sin(2 * Math.PI * F * i / N)) + 
                    (short) (Short.MAX_VALUE/2 * Math.sin(2 * Math.PI * F1 * i / N))
                    );
            signal[i] = new Complex(s, 0);
        }
        
        Complex[] x= new Complex[M];
        double k = (double)(N-1)/(double)(M);
        System.out.println("k=" + k);
        for (int i = 0; i < M; i++) {
            int p = (int)(k * i);
            int q = (int)(k * i) + 1;
            
            double K = (signal[q].re() - signal[p].re()) * N;
            double dx = (double)i/(double)M - (double)p/(double)N;
            x[i] = new Complex(signal[p].re() + K*dx, 0);
        }
        
//        Complex[] f = FFT.fft(x);
        
//        for (int i = 0; i < f.length; i++) {
//            System.out.println(i + ": " + Math.sqrt(f[i].re() * f[i].re() + f[i].im()*f[i].im()));
//        }
    }


}