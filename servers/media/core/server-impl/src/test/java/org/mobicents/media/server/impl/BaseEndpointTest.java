/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.server.local.management.EndpointLocalManagement;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.TooManyConnectionsException;
import org.mobicents.media.server.spi.events.EventFactory;
import org.mobicents.media.server.spi.events.EventIdentifier;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.RequestedEvent;
import org.mobicents.media.server.spi.events.RequestedSignal;
import org.mobicents.media.server.spi.events.pkg.Announcement;
import org.mobicents.media.server.spi.events.pkg.DTMF;

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

	@Test
	public void testSupportedPackageName() {
		TestEndpoint enp = new TestEndpoint("test");
		String[] supportedPackages = enp.getSupportedPackages();
		assertNotNull(supportedPackages);
	}

	@Test
	public void testExecute() {

		TestEndpoint enp = new TestEndpoint("test");

		EventFactory eventFactory = new EventFactory();
		RequestedSignal signal = eventFactory.createRequestedSignal(DTMF.DTMF.getPackageName(), DTMF.DTMF
				.getEventName());
		NotificationListener listener = new NotificationListenerImpl();
		signal.setHandler(listener);

		RequestedEvent event = eventFactory.createRequestedEvent(DTMF.DTMF.getPackageName(), DTMF.DTMF.getEventName());
		event.setHandler(listener);
		enp.execute(new RequestedSignal[] { signal }, new RequestedEvent[] { event }, null);

	}

	public class NotificationListenerImpl implements NotificationListener {

		public void update(NotifyEvent event) {
			EventIdentifier id = event.getEventID();
			assertEquals("org.mobicents.media.events.dtmf", id.getPackageName());
			assertEquals("PACKAGE_NOT_SUPPORTED", id.getEventName());
		}
	}

	public class TestEndpoint extends BaseEndpoint {

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

		public String[] getSupportedPackages() {
			String[] supportedpackages = new String[] { Announcement.PACKAGE_NAME };
			return supportedpackages;
		}
	}
}
