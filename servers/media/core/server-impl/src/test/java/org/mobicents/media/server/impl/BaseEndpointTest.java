/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.server.impl.common.ConnectionMode;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.TooManyConnectionsException;

/**
 *
 * @author Oleg Kulikov
 */
public class BaseEndpointTest {

    public BaseEndpointTest() {
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
     * Test of getLocalName method, of class BaseEndpoint.
     */
    @Test
    public void testGetLocalName() {
        TestEndpoint enp = new TestEndpoint("test");
        assertEquals("test", enp.getLocalName());
    }

    /**
     * Test of getRtpFactoryName method, of class BaseEndpoint.
     */
    @Test
    public void testGetRtpFactoryName() {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setRtpFactoryName("rtp/test");
        assertEquals("rtp/test", enp.getRtpFactoryName());
    }

    /**
     * Test of setRtpFactoryName method, of class BaseEndpoint.
     */
    @Test
    public void testSetRtpFactoryName() {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setRtpFactoryName("rtp/test");
        assertEquals("rtp/test", enp.getRtpFactoryName());
    }

    /**
     * Test of getMaxConnectionsAvailable method, of class BaseEndpoint.
     */
    @Test
    public void testGetMaxConnectionsAvailable() {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setMaxConnectionsAvailable(10);
        assertEquals(10, enp.getMaxConnectionsAvailable());
    }

    /**
     * Test of setMaxConnectionsAvailable method, of class BaseEndpoint.
     */
    @Test
    public void testSetMaxConnectionsAvailable() {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setMaxConnectionsAvailable(1);
        try {
            Connection con = enp.createLocalConnection(ConnectionMode.SEND_RECV);
            assertEquals(1, enp.getConnections().size());

            try {
                Connection con1 = enp.createLocalConnection(ConnectionMode.SEND_RECV);
                fail("To many connections");
            } catch (TooManyConnectionsException e) {
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of hasConnections method, of class BaseEndpoint.
     */
    @Test
    public void testHasConnections() {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setMaxConnectionsAvailable(1);
        try {
            assertEquals(false, enp.hasConnections());
            Connection con = enp.createLocalConnection(ConnectionMode.SEND_RECV);
            assertEquals(1, enp.getConnections().size());

            assertEquals(true, enp.hasConnections());
            enp.deleteConnection(con.getId());
            assertEquals(false, enp.hasConnections());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getConnection method, of class BaseEndpoint.
     */
    @Test
    public void testGetConnection() {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setMaxConnectionsAvailable(1);
        try {
            Connection con = enp.createLocalConnection(ConnectionMode.SEND_RECV);
            assertEquals(con, enp.getConnection(con.getId()));

            enp.deleteConnection(con.getId());
            assertEquals(0, enp.getConnections().size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getConnections method, of class BaseEndpoint.
     */
    @Test
    public void testGetConnections() {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setMaxConnectionsAvailable(1);
        try {
            Connection con = enp.createLocalConnection(ConnectionMode.SEND_RECV);
            assertEquals(1, enp.getConnections().size());

            enp.deleteConnection(con.getId());
            assertEquals(0, enp.getConnections().size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of createConnection method, of class BaseEndpoint.
     */
    @Test
    public void testCreateConnection() throws Exception {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setMaxConnectionsAvailable(1);
        try {
            Connection con = enp.createConnection(ConnectionMode.SEND_RECV);
            assertEquals(1, enp.getConnections().size());

            enp.deleteConnection(con.getId());
            assertEquals(0, enp.getConnections().size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of createLocalConnection method, of class BaseEndpoint.
     */
    @Test
    public void testCreateLocalConnection() throws Exception {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setMaxConnectionsAvailable(1);
        try {
            Connection con = enp.createLocalConnection(ConnectionMode.SEND_RECV);
            assertEquals(1, enp.getConnections().size());

            enp.deleteConnection(con.getId());
            assertEquals(0, enp.getConnections().size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of deleteConnection method, of class BaseEndpoint.
     */
    @Test
    public void testDeleteConnection() {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setMaxConnectionsAvailable(1);
        try {
            Connection con = enp.createLocalConnection(ConnectionMode.SEND_RECV);
            assertEquals(1, enp.getConnections().size());

            enp.deleteConnection(con.getId());
            assertEquals(0, enp.getConnections().size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of deleteAllConnections method, of class BaseEndpoint.
     */
    @Test
    public void testDeleteAllConnections() {
        TestEndpoint enp = new TestEndpoint("test");
        enp.setMaxConnectionsAvailable(2);
        try {
            enp.createLocalConnection(ConnectionMode.SEND_RECV);
            assertEquals(1, enp.getConnections().size());
            enp.createLocalConnection(ConnectionMode.SEND_RECV);
            assertEquals(2, enp.getConnections().size());

            enp.deleteAllConnections();
            assertEquals(0, enp.getConnections().size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getPackageName method, of class BaseEndpoint.
     */
    @Test
    public void testGetPackageName() {
        TestEndpoint enp = new TestEndpoint("test");
        String packageName = enp.getPackageName("org.mobicents.media.ann.PLAY");
        assertEquals("org.mobicents.media.ann", packageName);
    }

    /**
     * Test of getEventName method, of class BaseEndpoint.
     */
    @Test
    public void testGetEventName() {
        TestEndpoint enp = new TestEndpoint("test");
        String evt = enp.getEventName("org.mobicents.media.ann.PLAY");
        assertEquals("PLAY", evt);
    }

    public class TestEndpoint extends BaseEndpoint {

        public TestEndpoint(String localName) {
            super(localName);
        }
    }
}