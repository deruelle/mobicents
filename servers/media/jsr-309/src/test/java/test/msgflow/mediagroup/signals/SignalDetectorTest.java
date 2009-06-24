package test.msgflow.mediagroup.signals;

import java.io.Serializable;

import javax.media.mscontrol.MediaEventListener;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.join.JoinEvent;
import javax.media.mscontrol.join.JoinEventListener;
import javax.media.mscontrol.join.Joinable;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.signals.SignalDetector;
import javax.media.mscontrol.mediagroup.signals.SignalDetectorEvent;
import javax.media.mscontrol.networkconnection.NetworkConnection;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;

import test.msgflow.MessageFlowHarness;



/**
 * 
 * 
 * @author amit bhayani
 * 
 */
public class SignalDetectorTest extends MessageFlowHarness {

	// TODO: Do SignalDetectorTest needs its own MGW?
	private MGW mgw;

	public SignalDetectorTest() {
		super();
		logger = Logger.getLogger(SignalDetectorTest.class);
	}

	public SignalDetectorTest(String name) {
		super(name);
		logger = Logger.getLogger(SignalDetectorTest.class);
	}

	public void setUp() {
		try {
			super.setUp();
			mgw = new MGW(mgwProvider);

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected Exception");
		}
	}
	
	
	public void testSignalDetect() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		final MediaGroup MG1 = myMediaSession.createMediaGroup(MediaGroup.SIGNALDETECTOR);
		final SignalDetector detector = MG1.getSignalDetector();
		final ContextImpl ser = new ContextImpl();

		

		JoinEventListener statusEvtList = new JoinEventListener() {

			public void onEvent(JoinEvent event) {
				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("Join successful " + event);

						MediaEventListener<SignalDetectorEvent> sigDetectorListener = new MediaEventListener<SignalDetectorEvent>() {

							public void onEvent(SignalDetectorEvent anEvent) {
								if (anEvent.isSuccessful()) {
									if (SignalDetectorEvent.SIGNAL_DETECTED == anEvent.getEventType()) {
										logger.debug(" SignalDetection successfully. received DTMF =  " + anEvent.getSignalString());
										testPassed = true;
									} else{
										logger.error(" SignalDetection successfully. But EventType is = "+anEvent.getEventType());
									}
								} else {
									logger.error("Received Error from Player " + anEvent);
								}
							}
						};

						detector.addListener(sigDetectorListener);
						try {							
							detector.receiveSignals(1, null, null, null);
						} catch (MsControlException e) {
							logger.error(e);
							fail("Player failed");
						}
					} else {
						logger.error("Join failed " + event);
						fail("Join of MG1 and NC1 failed");
					}

				} else {
					logger.error("Join failed " + event);
					fail("Join of MG1 and NC1 failed");
				}
			}
		};

		MG1.addListener(statusEvtList);
		MG1.joinInitiate(Joinable.Direction.DUPLEX, NC1, ser);

		waitForMessage();
		waitForMessage();

		assertTrue(this.getName() + " passed = " + testPassed, testPassed);
	}

	private class ContextImpl implements Serializable {

	}
	
	public void tearDown() {
		this.mgw.checkState();
		try {
			super.tearDown();
		} catch (Exception ex) {
		}
	}

}
