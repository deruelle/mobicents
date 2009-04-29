package test.msgflow.mediagroup.signals;

import java.io.Serializable;

import javax.media.mscontrol.JoinEvent;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.StatusEvent;
import javax.media.mscontrol.StatusEventListener;
import javax.media.mscontrol.Joinable.Direction;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.MediaGroupConfig;
import javax.media.mscontrol.mediagroup.signals.SignalDetector;
import javax.media.mscontrol.mediagroup.signals.SignalDetectorEvent;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.NetworkConnectionConfig;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.MediaEventListener;

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
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnectionConfig.c_Basic);
		final MediaGroup MG1 = myMediaSession.createMediaGroup(MediaGroupConfig.c_SignalDetector);
		final SignalDetector detector = MG1.getSignalDetector();
		final ContextImpl ser = new ContextImpl();

		

		StatusEventListener statusEvtList = new StatusEventListener() {

			public void onEvent(StatusEvent event) {
				if (event.getError().equals(Error.e_OK)) {
					if (JoinEvent.ev_Joined.equals(event.getEventType())) {
						logger.info("Join successful " + event);

						MediaEventListener<SignalDetectorEvent> sigDetectorListener = new MediaEventListener<SignalDetectorEvent>() {

							public void onEvent(SignalDetectorEvent anEvent) {
								if (Error.e_OK.equals(anEvent.getError())) {
									if (SignalDetector.ev_SignalDetected.equals(anEvent.getEventType())) {
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
		MG1.joinInitiate(Direction.DUPLEX, NC1, ser);

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
