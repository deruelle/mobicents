/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import static org.junit.Assert.*;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.test.TransmissionTester;
import org.mobicents.media.server.spi.Timer;

/**
 *
 * @author kulikov
 */
public class ProxyTest {

    private Proxy proxy;
    private Timer timer;
    private TransmissionTester tester;
    
    public ProxyTest() {
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
        proxy = new Proxy("test");
        tester = new TransmissionTester(timer);
        tester.connect(proxy.getInput());
        tester.connect(proxy.getOutput());
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of start method, of class Proxy.
     */
    @Test
    public void testTransmission() {
        proxy.start();
        tester.start();
        assertTrue(tester.getMessage(), tester.isPassed());
        proxy.stop();
    }

    @Test
    public void testTransmission2() {
        proxy.setFormat(new Format[]{new AudioFormat("test")});
        proxy.start();
        tester.start();
        assertFalse("Transmission works", tester.isPassed());
        proxy.stop();
    }

}