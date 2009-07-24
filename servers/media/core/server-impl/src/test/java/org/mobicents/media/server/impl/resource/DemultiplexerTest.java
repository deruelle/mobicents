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
import static org.junit.Assert.*;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.test.TransmissionTester;
import org.mobicents.media.server.spi.Timer;

/**
 *
 * @author kulikov
 */
public class DemultiplexerTest {

    private Timer timer;
    private Demultiplexer demux;
    private TransmissionTester tester;
    
    public DemultiplexerTest() {
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
        demux = new Demultiplexer("test");
        tester = new TransmissionTester(timer);
        tester.connect(demux);
        tester.connect(demux.getInput());
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getFormats method, of class Demultiplexer.
     */
    @Test
    public void testInputFormats() {
        Format[] supported = demux.getInput().getFormats();
        assertEquals(1, supported.length);
        
        supported = demux.getFormats();
        assertEquals(1, supported.length);
    }

    /**
     * Test of connect method, of class Demultiplexer.
     */
    @Test
    public void testTransmission() {
        demux.getInput().start();
        tester.start();
        assertTrue(tester.getMessage(), tester.isPassed());
        demux.getInput().stop();
    }

    

    
}