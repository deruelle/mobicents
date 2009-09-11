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
import static org.junit.Assert.*;

import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.test.TransmissionTester;
import org.mobicents.media.server.spi.Timer;


/**
 *
 * @author kulikov
 */
public class MultiplexerTest {

    private Multiplexer mux;
    private Timer timer;
    private TransmissionTester tester;
    
    public MultiplexerTest() {
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
        timer.start();
        
        mux = new Multiplexer("test");
        tester = new TransmissionTester(timer);
        tester.connect(mux);
        tester.connect(mux.getOutput());
    }

    @After
    public void tearDown() {
        timer.stop();
    }

    @Test
    public void testFormats() {
        Format[] supported = mux.getOutput().getFormats();
        assertEquals(1, supported.length);
        
        supported = mux.getFormats();
        assertEquals(1, supported.length);
    }
    
    /**
     * Test of connect method, of class Demultiplexer.
     */
    @Test
    public void testTransmission() {
        mux.getOutput().start();
        tester.start();
        assertTrue(tester.getMessage(), tester.isPassed());
        mux.getOutput().stop();
    }
    
    
    
}