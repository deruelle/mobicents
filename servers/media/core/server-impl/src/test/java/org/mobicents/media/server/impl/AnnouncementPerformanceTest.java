/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.test.SpectrumEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class AnnouncementPerformanceTest extends NewSuperXCase implements NotificationListener {

    private UA[] parties = new UA[100];
    private int[] FREQ = new int[]{50};
    private int ERROR=5;
    
    int count = 0;
    private long currentTime;
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AnnouncementPerformanceTest.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception {
        for (int i = 0; i < parties.length; i++) {
            parties[i] = new UA(new Analyzer(), i);
        }
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public void testPerformance() {
        for (int i = 0; i < parties.length; i++) {
            new Thread(parties[i]).start();
        }

        if (!doTest(120)) {
            fail("Test failed on "  + count + " second," + getReason());
        }
    }

    public void update(NotifyEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private class Analyzer implements NotificationListener {
        private long currentTime;
        
        public void update(NotifyEvent event) {
            count++;
            long now = System.currentTimeMillis();
//            System.out.println(now - currentTime);
            currentTime = now;
            double spectra[] = ((SpectrumEvent) event).getSpectra();
            boolean res = checkFreq(spectra, FREQ, ERROR);
            if (!res) {
                doFail(null);
            }

        }
    }
}
