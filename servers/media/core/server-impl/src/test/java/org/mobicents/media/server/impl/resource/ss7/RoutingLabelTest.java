/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.ss7;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kulikov
 */
public class RoutingLabelTest {

    private final int opc = 14148;
    private final int dpc = 14235;
    private final int sls = 1;
    
    private RoutingLabel label;
    
    public RoutingLabelTest() {
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
     * Test of getOPC method, of class RoutingLabel.
     */
    @Test
    public void testEncodingDecoding() {
        label = new RoutingLabel(opc, dpc, sls);
        byte[] bin = label.toByteArray();
        
        RoutingLabel label2 = new RoutingLabel(bin);
        assertEquals(label2.getDPC(), label.getDPC());
        assertEquals(label2.getOPC(), label.getOPC());
        assertEquals(label2.getSLS(), label.getSLS());
    }


}