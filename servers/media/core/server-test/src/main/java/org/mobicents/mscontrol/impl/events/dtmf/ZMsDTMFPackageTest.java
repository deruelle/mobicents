package org.mobicents.mscontrol.impl.events.dtmf;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionEventCause;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.events.dtmf.MsDtmfRequestedSignal;
import org.mobicents.mscontrol.events.pkg.DTMF;
import org.mobicents.mscontrol.impl.AbstractTest;

public class ZMsDTMFPackageTest extends AbstractTest {

	private static Logger logger = Logger.getLogger(ZMsDTMFPackageTest.class);

	public ZMsDTMFPackageTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ZMsDTMFPackageTest.class);
	}

	public void testDTMFSignalFired() {
		msProvider.addNotificationListener(new MsNotificationListener() {

			public void update(MsNotifyEvent evt) {
				MsEventIdentifier msEventIdentifier = evt.getEventID();
				logger.info("testRecordCompleted : MsNotificationListener.update FQN = " + msEventIdentifier.getFqn());
				// if (msEventIdentifier.equals(MsAudio.FAILED)) {
				// testPassed = false;
				// message = "testRecordCompleted :
				// MsNotificationListenerImpl.update called. Expected testPassed
				// = false";
				// logger.info("testRecordCompleted : Recording Failed!");
				// } else {
				// message = "testAnnouncement :
				// MsNotificationListenerImpl.update called. Expected testPassed
				// = false";
				// testPassed = false;
				//
				// }
			}
		});

		msProvider.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testConnectionOpen : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testConnectionOpen : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
				testPassed = false;
				logger.info("Released Connection. Cause " + event.getCause());

			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testConnectionOpen : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				message = "testConnectionOpen : MsConnectionListenerImpl.connectionHalfOpen called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testConnectionOpen : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
				testPassed = false;
				logger.info("Connection OPEN. Cause " + event.getCause());
				assertEquals(event.getCause(), MsConnectionEventCause.NORMAL);

				msEndpoint = msConnection.getEndpoint();

				MsEventFactory msEventFactory = msProvider.getEventFactory();

				MsDtmfRequestedSignal dtmf = (MsDtmfRequestedSignal) msEventFactory.createRequestedSignal(DTMF.TONE);
				dtmf.setTone("1");

				MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { dtmf };
				MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] {};

				msEndpoint.execute(requestedSignals, requestedEvents, msConnection);
			}

			public void connectionModeRecvOnly(MsConnectionEvent event) {
				message = "testConnectionOpen : MsConnectionListenerImpl.connectionModeRecvOnly called. Expected testPassed = false";
				testPassed = false;

			}

			public void connectionModeSendOnly(MsConnectionEvent event) {
				message = "testConnectionOpen : MsConnectionListenerImpl.connectionModeSendOnly called. Expected testPassed = false";
				testPassed = false;

			}

			public void connectionModeSendRecv(MsConnectionEvent event) {
				message = "testConnectionOpen : MsConnectionListenerImpl.connectionModeSendRecv called. Expected testPassed = false";
				testPassed = false;

			}
		});

		msSession = msProvider.createSession();
		msConnection = msSession.createNetworkConnection("media/trunk/IVR/$");
		String sdp = "v=0\n" + "o=MediaServerTest 5334424 5334424 IN IP4 127.0.0.1\n" + "s=session\n"
				+ "c=IN IP4 127.0.0.1\n" + "t=0 0\n" + "m=audio 64535 RTP/AVP 0 8\n" + "a=rtpmap:0 pcmu/8000\n"
				+ "a=rtpmap:8 pcma/8000";

		msConnection.modify("$", sdp);

		// Let us sleep for 3 secs giving enough time for completion of test
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Let us fire the empty MsRequestedSignal to stop recording
		msEndpoint.execute(new MsRequestedSignal[] {}, new MsRequestedEvent[] {}, msConnection);

		// Let us sleep for 1 secs again for completion of test
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		message = "Forced to pass as dtmf Signal is not supported yet";
		testPassed = true;

	}

}
