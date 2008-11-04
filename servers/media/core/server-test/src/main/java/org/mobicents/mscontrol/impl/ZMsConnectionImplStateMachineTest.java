package org.mobicents.mscontrol.impl;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionEventCause;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsConnectionState;

/**
 * 
 * @author amit bhayani
 * 
 */
public class ZMsConnectionImplStateMachineTest extends AbstractTest {

	private static Logger logger = Logger.getLogger(ZMsConnectionImplStateMachineTest.class);

	public ZMsConnectionImplStateMachineTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ZMsConnectionImplStateMachineTest.class);
	}

	public void testConnectionCreated() {
		msProvider.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = true";
				testPassed = true;
				assertEquals(event.getCause(), MsConnectionEventCause.NORMAL);
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
				testPassed = false;
				logger.info("Released Connection");
			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				testPassed = false;
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionModeRecvOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendRecv(MsConnectionEvent event) {
				testPassed = false;
				
			}
		});

		msSession = msProvider.createSession();
		msConnection = msSession.createNetworkConnection("Endpoint1");
		assertNotNull(msConnection);
		assertEquals(msConnection.getState(), MsConnectionState.IDLE);
	}

	public void testConnectionHalfOpen() {
		msProvider.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
				testPassed = false;
				logger.info("Released Connection. Cause " + event.getCause());

			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = true";
				testPassed = true;
				logger.info("Connection HALF_OPEN. Cause " + event.getCause());
				assertEquals(event.getCause(), MsConnectionEventCause.NORMAL);
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionModeRecvOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendRecv(MsConnectionEvent event) {
				testPassed = false;
				
			}
		});

		msSession = msProvider.createSession();
		msConnection = msSession.createNetworkConnection("media/trunk/Announcement/$");
		assertNotNull(msConnection);
		assertEquals(msConnection.getState(), MsConnectionState.IDLE);

		msConnection.modify("$", null);
	}

	public void testConnectionOpen() {
		msProvider.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
				testPassed = false;
				logger.info("Released Connection. Cause " + event.getCause());

			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = true";
				testPassed = true;
				logger.info("Connection OPEN. Cause " + event.getCause());
				assertEquals(event.getCause(), MsConnectionEventCause.NORMAL);
			}

			public void connectionModeRecvOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendRecv(MsConnectionEvent event) {
				testPassed = false;
				
			}
		});

		msSession = msProvider.createSession();
		msConnection = msSession.createNetworkConnection("media/trunk/Announcement/$");
		assertNotNull(msConnection);
		assertEquals(msConnection.getState(), MsConnectionState.IDLE);

		String sdp = "v=0\n" + "o=MediaServerTest 5334424 5334424 IN IP4 127.0.0.1\n" + "s=session\n"
				+ "c=IN IP4 127.0.0.1\n" + "t=0 0\n" + "m=audio 64535 RTP/AVP 0 8\n" + "a=rtpmap:0 pcmu/8000\n"
				+ "a=rtpmap:8 pcma/8000";

		msConnection.modify("$", sdp);
	}

	public void testConnectionDisconnected() {
		msProvider.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = true";
				testPassed = true;
				logger.info("Connection Disconnected. Cause " + event.getCause());
				assertEquals(event.getCause(), MsConnectionEventCause.NORMAL);
			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
				logger.info("Connection HALF_OPEN. Cause " + event.getCause());
				assertEquals(event.getCause(), MsConnectionEventCause.NORMAL);

				// Let us release it here
				msConnection.release();
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionModeRecvOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendRecv(MsConnectionEvent event) {
				testPassed = false;
				
			}
		});

		msSession = msProvider.createSession();
		msConnection = msSession.createNetworkConnection("media/trunk/Announcement/$");
		assertNotNull(msConnection);
		assertEquals(msConnection.getState(), MsConnectionState.IDLE);

		msConnection.modify("$", null);

		// Sleep for additional 1 sec for events firing to be over
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testConnectionFailedEndpointUnknown() {
		msProvider.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = true";
				testPassed = testPassed && true;
				logger.info("Connection Disconnected. Cause " + event.getCause());
			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = true";
				testPassed = true;
				logger.info("Connection Failed. Cause " + event.getCause());

				assertEquals(event.getCause(), MsConnectionEventCause.ENDPOINT_UNKNOWN);
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionModeRecvOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendRecv(MsConnectionEvent event) {
				testPassed = false;
				
			}
		});

		msSession = msProvider.createSession();
		msConnection = msSession.createNetworkConnection("blabla/media/trunk/Announcement/$"); // Unknown
		// Endpoint
		assertNotNull(msConnection);
		assertEquals(msConnection.getState(), MsConnectionState.IDLE);

		msConnection.modify("$", null);
	}

	// TODO : guarantee of order?
	public void testConnectionLocalListener() {

		msProvider.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
				testPassed = false;
				logger.info("Released Connection");
			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				message = "testConnectionLocalListener : MsConnectionListenerImpl.connectionHalfOpen called. Expected testPassed = true";
				testPassed = true;
				assertEquals(event.getCause(), MsConnectionEventCause.NORMAL);
				logger.info("connectionHalfOpen called for MsConnection provider listener");
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionModeRecvOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendRecv(MsConnectionEvent event) {
				testPassed = false;
				
			}
		});

		msSession = msProvider.createSession();
		msConnection = msSession.createNetworkConnection("media/trunk/Announcement/$");

		msConnection.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
				testPassed = false;
				logger.info("Released Connection");
			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				message = "testConnectionLocalListener : MsConnectionListenerImpl.connectionHalfOpen called. Expected testPassed = true";
				testPassed = testPassed && true;
				assertEquals(event.getCause(), MsConnectionEventCause.NORMAL);
				logger.info("connectionHalfOpen called for MsConnection local listener");
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testConnectionCreated : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionModeRecvOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendOnly(MsConnectionEvent event) {
				testPassed = false;
				
			}

			public void connectionModeSendRecv(MsConnectionEvent event) {
				testPassed = false;
				
			}
		});

		msConnection.modify("$", null);
	}

}
