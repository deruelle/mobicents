/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.ss7;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author kulikov
 */
public class Mtp2Test {

    private TransferProxy channel;
    
    private Mtp1DummyImpl terminal1;
    private Mtp1DummyImpl terminal2;
    
    private Mtp3 link1;
    private Mtp3 link2;
    
    
    public Mtp2Test() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        channel = new TransferProxy();
        
        terminal1 = new Mtp1DummyImpl(channel);
        terminal2 = new Mtp1DummyImpl(channel);

        link1 = new Mtp3("1");
        link2 = new Mtp3("2");
        
        link1.setLayer1(terminal1);
        link2.setLayer1(terminal2);        
        
        link1.setOPC(14148);
        link1.setDPC(14141);
        link1.setSLS(1);
        
        link2.setDPC(14148);
        link2.setOPC(14141);
        link2.setSLS(1);
    }

    @After
    public void tearDown() {
        link1.stop();
        link2.stop();
    }

    /**
     * Test of setLayer1 method, of class Mtp2.
     */
    @Test
    public void testLink() throws Exception {
        link1.start();
        link2.start();
        
        Thread.currentThread().sleep(180000);
    }


}