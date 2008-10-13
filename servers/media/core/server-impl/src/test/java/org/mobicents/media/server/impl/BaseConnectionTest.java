/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl;

import java.io.IOException;
import java.util.HashMap;
import javax.sdp.SdpException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.mobicents.media.server.local.management.EndpointLocalManagement;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author Oleg Kulikov
 */
public class BaseConnectionTest {
	private ConnectionListener cListener=new HollowConnectionListener();
    public BaseConnectionTest() {
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
     * Test of getId method, of class BaseConnection.
     */
    @Test
    public void testGetId() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            if (con.getId() == null) {
                fail("Connection ID is null");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getState method, of class BaseConnection.
     */
    @Test
    public void testGetState() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            assertEquals(ConnectionState.NULL, con.getState());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getLifeTime method, of class BaseConnection.
     */
    @Test
    public void testGetLifeTime() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            assertEquals(30, con.getLifeTime());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of setLifeTime method, of class BaseConnection.
     */
    @Test
    public void testSetLifeTime() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            con.setLifeTime(50);
            assertEquals(50, con.getLifeTime());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getMode method, of class BaseConnection.
     */
    @Test
    public void testGetMode() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            assertEquals(ConnectionMode.SEND_RECV, con.getMode());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of setMode method, of class BaseConnection.
     */
    @Test
    public void testSetMode() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            con.setMode(ConnectionMode.RECV_ONLY);
            assertEquals(ConnectionMode.RECV_ONLY, con.getMode());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getEndpoint method, of class BaseConnection.
     */
    @Test
    public void testGetEndpoint() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            assertEquals(enp, con.getEndpoint());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getMux method, of class BaseConnection.
     */
    @Test
    public void testGetMux() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            if (con.getMux()== null) {
                fail("Mux should be not null");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getDemux method, of class BaseConnection.
     */
    @Test
    public void testGetDemux() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            if (con.getDemux()== null) {
                fail("Demux should be not null");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of addListener method, of class BaseConnection.
     */
    @Test
    public void testAddListener() {
    }

    /**
     * Test of removeListener method, of class BaseConnection.
     */
    @Test
    public void testRemoveListener() {
    }

    /**
     * Test of setState method, of class BaseConnection.
     */
    @Test
    public void testSetState() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            con.setState(ConnectionState.OPEN);
            assertEquals(ConnectionState.OPEN, con.getState());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of close method, of class BaseConnection.
     */
    @Test
    public void testClose() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            TestConnection con = new TestConnection(enp, ConnectionMode.SEND_RECV);
            assertEquals(true, con.getLifeTimeTimerState());
            con.close();
            assertEquals(ConnectionState.CLOSED, con.getState());
            assertEquals(false, con.getLifeTimeTimerState());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private class TestConnection extends BaseConnection {
        public TestConnection (Endpoint enp, ConnectionMode mode) throws ResourceUnavailableException {
            super(enp, mode);
        }

        public String getLocalDescriptor() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getRemoteDescriptor() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setRemoteDescriptor(String descriptor) throws SdpException, IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setOtherParty(Connection other) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

		public String getOtherEnd() throws IllegalArgumentException {
			// TODO Auto-generated method stub
			return null;
		}
    }
    
    private class TestEndpoint extends BaseEndpoint {
        public TestEndpoint(String localName) {
            super(localName);
        }

        @Override
        public HashMap initMediaSources() {
            return new HashMap();
        }

        @Override
        public HashMap initMediaSinks() {
            return new HashMap();
        }

		public String[] getEndpointNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public EndpointLocalManagement[] getEndpoints() {
			// TODO Auto-generated method stub
			return null;
		}
    }
}