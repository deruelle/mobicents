package org.mobicents.mscontrol.impl.events.announcement;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionEventCause;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.events.MsEventAction;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.events.ann.MsPlayRequestedSignal;
import org.mobicents.mscontrol.events.audio.MsRecordRequestedSignal;
import org.mobicents.mscontrol.events.dtmf.MsDtmfRequestedEvent;
import org.mobicents.mscontrol.events.pkg.DTMF;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.mscontrol.events.pkg.MsAudio;
import org.mobicents.mscontrol.events.pkg.MsPackageNotSupported;
import org.mobicents.mscontrol.impl.AbstractTest;

/**
 * 
 * @author amit bhayani
 * 
 */
public class ZMsAnnouncementPackageTest extends AbstractTest {

	private static Logger logger = Logger.getLogger(ZMsAnnouncementPackageTest.class);

	private final static String ANN_WAV_FILE_PASS = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1")
			+ ":8080/mobicents-media-server-test/audio/dtmf.wav";

	private final static String ANN_WAV_FILE_FAIL = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1")
			+ ":8080/mobicents-media-server-test/audio/dtmfblabla.wav";

	public ZMsAnnouncementPackageTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ZMsAnnouncementPackageTest.class);
	}

	public void testAnnouncementCompleted() {
		msProvider.addNotificationListener(new MsNotificationListener() {

			public void update(MsNotifyEvent evt) {
				MsEventIdentifier msEventIdentifier = evt.getEventID();
				logger.info("testAnnouncement : MsNotificationListener.update FQN = " + msEventIdentifier.getFqn());
				if (msEventIdentifier.equals(MsAnnouncement.COMPLETED)) {
					testPassed = true;
					message = "testAnnouncement : MsNotificationListenerImpl.update called. Expected testPassed = true";
				} else {
					message = "testAnnouncement : MsNotificationListenerImpl.update called. Expected testPassed = false";
					testPassed = false;

				}
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

				MsPlayRequestedSignal play = (MsPlayRequestedSignal) msEventFactory
						.createRequestedSignal(MsAnnouncement.PLAY);
				play.setURL(ANN_WAV_FILE_PASS);

				MsRequestedEvent onCompleted = msEventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
				onCompleted.setEventAction(MsEventAction.NOTIFY);

				MsRequestedEvent onFailed = msEventFactory.createRequestedEvent(MsAnnouncement.FAILED);
				onFailed.setEventAction(MsEventAction.NOTIFY);

				MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { play };
				MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] { onCompleted, onFailed };

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
		msConnection = msSession.createNetworkConnection("media/trunk/Announcement/$");
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

	}

	public void testAnnouncementFailed() {
		msProvider.addNotificationListener(new MsNotificationListener() {

			public void update(MsNotifyEvent evt) {
				MsEventIdentifier msEventIdentifier = evt.getEventID();
				logger.info("testAnnouncement : MsNotificationListener.update FQN = " + msEventIdentifier.getFqn());

				if (msEventIdentifier.equals(MsAnnouncement.FAILED)) {
					testPassed = true;
					message = "testAnnouncement : MsNotificationListenerImpl.update called. Expected testPassed = true";
				} else {
					message = "testAnnouncement : MsNotificationListenerImpl.update called. Expected testPassed = false";
					testPassed = false;

				}
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

				MsPlayRequestedSignal play = (MsPlayRequestedSignal) msEventFactory
						.createRequestedSignal(MsAnnouncement.PLAY);
				play.setURL(ANN_WAV_FILE_FAIL);

				MsRequestedEvent onCompleted = msEventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
				onCompleted.setEventAction(MsEventAction.NOTIFY);

				MsRequestedEvent onFailed = msEventFactory.createRequestedEvent(MsAnnouncement.FAILED);
				onFailed.setEventAction(MsEventAction.NOTIFY);

				MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { play };
				MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] { onCompleted, onFailed };

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
		msConnection = msSession.createNetworkConnection("media/trunk/Announcement/$");
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

	}

	public void testAudioPackageNotSupported() {
		msProvider.addNotificationListener(new MsNotificationListener() {

			public void update(MsNotifyEvent evt) {
				MsEventIdentifier msEventIdentifier = evt.getEventID();
				logger.info("testAnnouncement : MsNotificationListener.update FQN = " + msEventIdentifier.getFqn());

				if (msEventIdentifier.equals(MsPackageNotSupported.AUDIO)) {
					testPassed = true;
					message = "testPackageNotSupported : MsNotificationListenerImpl.update called. Expected testPassed = true";
				} else {
					message = "testPackageNotSupported : MsNotificationListenerImpl.update called. Expected testPassed = false";
					testPassed = false;

				}
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

				MsRecordRequestedSignal record = (MsRecordRequestedSignal) msEventFactory
						.createRequestedSignal(MsAudio.RECORD);
				record.setFile("ZMsAnnouncementPackageTest.wav");

				MsRequestedEvent onFailed = msEventFactory.createRequestedEvent(MsAudio.FAILED);
				onFailed.setEventAction(MsEventAction.NOTIFY);

				MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { record };
				MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] { onFailed };

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
		msConnection = msSession.createNetworkConnection("media/trunk/Announcement/$");
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

	}
	
	public void testDtmfPackageNotSupported() {
		msProvider.addNotificationListener(new MsNotificationListener() {

			public void update(MsNotifyEvent evt) {
				MsEventIdentifier msEventIdentifier = evt.getEventID();
				logger.info("testAnnouncement : MsNotificationListener.update FQN = " + msEventIdentifier.getFqn());

				if (msEventIdentifier.equals(MsPackageNotSupported.DTMF)) {
					testPassed = true;
					message = "testPackageNotSupported : MsNotificationListenerImpl.update called. Expected testPassed = true";
				} else {
					message = "testPackageNotSupported : MsNotificationListenerImpl.update called. Expected testPassed = false";
					testPassed = false;

				}
			}
		});

		msProvider.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testDtmfPackageNotSupported : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testDtmfPackageNotSupported : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
				testPassed = false;
				logger.info("Released Connection. Cause " + event.getCause());

			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testDtmfPackageNotSupported : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				message = "testDtmfPackageNotSupported : MsConnectionListenerImpl.connectionHalfOpen called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testDtmfPackageNotSupported : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
				testPassed = false;
				logger.info("Connection OPEN. Cause " + event.getCause());
				assertEquals(event.getCause(), MsConnectionEventCause.NORMAL);

				msEndpoint = msConnection.getEndpoint();

				MsEventFactory msEventFactory = msProvider.getEventFactory();

				MsPlayRequestedSignal play = (MsPlayRequestedSignal) msEventFactory
						.createRequestedSignal(MsAnnouncement.PLAY);
				play.setURL(ANN_WAV_FILE_PASS);

				MsRequestedEvent onCompleted = msEventFactory.createRequestedEvent(MsAnnouncement.COMPLETED);
				onCompleted.setEventAction(MsEventAction.NOTIFY);
				
		        MsDtmfRequestedEvent dtmf = (MsDtmfRequestedEvent) msEventFactory.createRequestedEvent(DTMF.TONE);

				MsRequestedEvent onFailed = msEventFactory.createRequestedEvent(MsAnnouncement.FAILED);
				onFailed.setEventAction(MsEventAction.NOTIFY);

				MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { play };
				MsRequestedEvent[] requestedEvents = new MsRequestedEvent[] { onCompleted, onFailed, dtmf };

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
		msConnection = msSession.createNetworkConnection("media/trunk/Announcement/$");
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

	}	

}
