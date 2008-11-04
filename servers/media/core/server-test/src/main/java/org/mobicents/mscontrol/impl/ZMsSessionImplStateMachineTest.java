package org.mobicents.mscontrol.impl;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsSessionEvent;
import org.mobicents.mscontrol.MsSessionListener;
import org.mobicents.mscontrol.MsSessionState;

public class ZMsSessionImplStateMachineTest extends AbstractTest {

	private static Logger logger = Logger.getLogger(ZMsProviderImplTest.class);

	public ZMsSessionImplStateMachineTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ZMsSessionImplStateMachineTest.class);
	}

	public void testSessionInitialization() {

		msProvider.addSessionListener(new MsSessionListener() {
			public void sessionActive(MsSessionEvent evt) {
				message = "testSessionInitialization : MsSessionListenerImpl.sessionActive called. Expected testPassed = false";
				testPassed = false;

			}

			public void sessionCreated(MsSessionEvent evt) {
				message = "testSessionInitialization : MsSessionListenerImpl.sessionCreated called. Expected testPassed = true";
				testPassed = true;
			}

			public void sessionInvalid(MsSessionEvent evt) {
				message = "testSessionInitialization : MsSessionListenerImpl.sessionInvalid called. Expected testPassed = false";
				testPassed = false;

			}

		});
		msSession = msProvider.createSession();

		assertNotNull(msSession);
		assertSame(MsSessionState.IDLE, msSession.getState());
	}

	public void testSessionActive() {

		msProvider.addSessionListener(new MsSessionListener() {
			public void sessionActive(MsSessionEvent evt) {
				message = "testSessionActive : MsSessionListenerImpl.sessionActive called. Expected testPassed = true";
				testPassed = true;
			}

			public void sessionCreated(MsSessionEvent evt) {
				message = "testSessionActive : MsSessionListenerImpl.sessionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void sessionInvalid(MsSessionEvent evt) {
				message = "testSessionActive : MsSessionListenerImpl.sessionInvalid called. Expected testPassed = false";
				testPassed = false;

			}

		});

		msSession = msProvider.createSession();

		assertNotNull(msSession);
		assertSame(MsSessionState.IDLE, msSession.getState());

		msConnection = msSession.createNetworkConnection("endpoint1");

		assertSame(MsSessionState.ACTIVE, msSession.getState());
	}

	// TODO : guarantee of order?
	public void testSessionLocalListener() {

		msProvider.addSessionListener(new MsSessionListener() {
			public void sessionActive(MsSessionEvent evt) {
				message = message
						+ "testSessionLocalListener : MsSessionListenerImpl.sessionActive (provider) called. Expected testPassed = true";
				testPassed = testPassed && true;

			}

			public void sessionCreated(MsSessionEvent evt) {
				message = message
						+ "testSessionLocalListener : MsSessionListenerImpl.sessionCreated (provider) called. Expected testPassed = false";
				testPassed = false;
			}

			public void sessionInvalid(MsSessionEvent evt) {
				message = message
						+ "testSessionLocalListener : MsSessionListenerImpl.sessionInvalid (provider) called. Expected testPassed = false";
				testPassed = false;

			}

		});
		msSession = msProvider.createSession();

		// This Listener should be called before Listener at Provider level
		msSession.addSessionListener(new MsSessionListener() {
			public void sessionActive(MsSessionEvent evt) {
				message = "testSessionLocalListener : MsSessionListenerImpl.sessionActive (session) called. Expected testPassed = true";
				testPassed = true;

			}

			public void sessionCreated(MsSessionEvent evt) {
				message = "testSessionLocalListener : MsSessionListenerImpl.sessionCreated (session) called. Expected testPassed = false";
				testPassed = false;
			}

			public void sessionInvalid(MsSessionEvent evt) {
				message = "testSessionLocalListener : MsSessionListenerImpl.sessionInvalid (session) called. Expected testPassed = false";
				testPassed = false;

			}

		});

		assertNotNull(msSession);
		assertSame(MsSessionState.IDLE, msSession.getState());

		msConnection = msSession.createNetworkConnection("endpoint1");

		assertSame(MsSessionState.ACTIVE, msSession.getState());
	}

	public void testSessionInvalid() {

		msProvider.addSessionListener(new MsSessionListener() {
			public void sessionActive(MsSessionEvent evt) {
				message = "testSessionActive : MsSessionListenerImpl.sessionActive called. Expected testPassed = false";
				testPassed = false;
			}

			public void sessionCreated(MsSessionEvent evt) {
				message = "testSessionActive : MsSessionListenerImpl.sessionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void sessionInvalid(MsSessionEvent evt) {
				message = "testSessionActive : MsSessionListenerImpl.sessionInvalid called. Expected testPassed = true";
				testPassed = true;
				logger.info("Session Invalidated. Cause = " + evt.getEventCause());

			}

		});

		msSession = msProvider.createSession();

		assertNotNull(msSession);
		assertSame(MsSessionState.IDLE, msSession.getState());

		msConnection = msSession.createNetworkConnection("media/trunk/Announcement/$");
		assertSame(MsSessionState.ACTIVE, msSession.getState());

		msConnection.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testGetConnection : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testGetConnection : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
				testPassed = false;
				logger.info("Released Connection");
			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testGetConnection : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				testPassed = false;
				// Lets release Connection here
				event.getConnection().release();
				logger.info("Releasing Connection");

			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testGetConnection : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
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

		// This will acquire resources and assign MsEndpoint for Connection
		msConnection.modify("$", null);

		// Lets sleep more for SessionListener to be called
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
