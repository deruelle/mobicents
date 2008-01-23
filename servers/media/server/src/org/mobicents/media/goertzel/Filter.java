/*
 * Filter.java
 *
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.media.goertzel;

/**
 * The Goertzel Algorithm is a DFT in disguise, with some numerical tricks to 
 * eliminate complex number arithmetic, roughly doubling the efficiency. 
 * This note presents the Goertzel Algorithm, and in particular, ways to improve 
 * its ability to isolate frequencies of interest. 
 * 
 * The Goertzel Algorithm has received a lot of attention recently for mobile 
 * telephone applications, but there are certainly many other ways it can be used. 
 *
 * @author Oleg Kulikov
 */
public class Filter {
    
    private double threshold;
    
    /** 
     * Creates a new instance of the Goertzel Filter. 
     *
     * @param threshold the minium "weigth factor"
     */
    public Filter(double threshold) {
        this.threshold = threshold;
    }
    
    public boolean detect(double f, double[] signal) {
        int N = signal.length;
        
        double realW = 2.0 * Math.cos(2.0*Math.PI*f/N);
        double imagW = Math.sin(2.0*Math.PI*f/N);
        
        double d1 = 0.0;
        double d2 = 0.0;
        double y = 0;
        
        for (int n = 0; n < N; ++n) {
            y  = signal[n] + realW*d1 - d2;
            d2 = d1;
            d1 = y;
        }
        
        double resultr = 0.5*realW*d1 - d2;
        double resulti = imagW*d1;
        
        double r = Math.sqrt(Math.pow(resultr, 2) + Math.pow(resulti, 2));
        return r > threshold;
    } 
    
    
}
