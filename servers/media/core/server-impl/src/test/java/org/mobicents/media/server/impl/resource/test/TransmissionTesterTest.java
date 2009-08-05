/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.Timer;

/**
 *
 * @author kulikov
 */
public class TransmissionTesterTest {

    private Timer timer;
    private TransmissionTester tester;    
    
    public TransmissionTesterTest() {
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
        tester = new TransmissionTester(timer);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getGenerator method, of class TransmissionTester.
     */
    @Test
    public void testPassed() {
        tester.connect(tester.getDetector());
        tester.start();
        assertTrue("Test not passed", tester.isPassed());
    }

    @Test
    public void testFailure() {
        try {
            tester.start();
        } catch (Exception e) {
        }
        assertFalse("Test passed", tester.isPassed());
    }

}