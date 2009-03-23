package test.msgflow.networkconnection;

import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.NetworkConnectionEvent;
import javax.media.mscontrol.networkconnection.NetworkConnectionException;
import javax.media.mscontrol.resource.MediaEventListener;
import javax.sdp.SdpException;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;

import test.msgflow.MessageFlowHarness;

/**
 * 
 * @author amit bhayani
 *
 */
public class NetworkConnectionTest extends MessageFlowHarness {

	private static Logger logger = Logger.getLogger(NetworkConnectionTest.class);
	private MGW mgw;
	boolean modifyCRCXEventReceived = false;
	boolean modifyMDCXEventReceived = false;

	public NetworkConnectionTest() {
		super();
	}

	public NetworkConnectionTest(String name) {
		super(name);
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

	public void testNetworkConnectionModify() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection myNetworkConnection = myMediaSession.createNetworkConnection();

		final String REMOTE_SDP = "v=0\n" + "m=audio 1234 RTP/AVP  0 \n" + "c=IN IP4 192.168.145.1\n"
				+ "a=rtpmap:0 PCMU/8000\n";

		MediaEventListener<NetworkConnectionEvent> myNetworkConnectionListerner = new MediaEventListener<NetworkConnectionEvent>() {

			public void onEvent(NetworkConnectionEvent anEvent) {

				if (NetworkConnection.ev_Modify.equals(anEvent.getEventID())) {
					logger.info("CRCX Modify successful " + anEvent);
					modifyCRCXEventReceived = true;

					try {
						assertNotNull(anEvent.getSource().getRawLocalSessionDescription());
					} catch (NetworkConnectionException e) {
						e.printStackTrace();
						fail();
					}

					try {
						assertNotNull(anEvent.getSource().getLocalSessionDescription());
					} catch (NetworkConnectionException e1) {
						e1.printStackTrace();
						fail();
					}

					try {
						anEvent.getSource().getRawRemoteSessionDescription();
						fail("RawRemoteSessionDescription should be null");
					} catch (NetworkConnectionException e) {
						// expected
					}

					try {
						anEvent.getSource().getRemoteSessionDescription();
						fail("RemoteSessionDescription should be null");
					} catch (NetworkConnectionException e) {
						// expected
					}

					myNetworkConnection.removeListener(this);

					MediaEventListener<NetworkConnectionEvent> myNetworkConnectionListernerInner = new MediaEventListener<NetworkConnectionEvent>() {

						public void onEvent(NetworkConnectionEvent anEvent) {

							if (NetworkConnection.ev_Modify.equals(anEvent.getEventID())) {
								logger.info("MDCX Modify successful " + anEvent);
								modifyMDCXEventReceived = true;

								try {
									assertNotNull(anEvent.getSource().getRawLocalSessionDescription());
								} catch (NetworkConnectionException e) {
									e.printStackTrace();
									fail();
								}

								try {
									assertNotNull(anEvent.getSource().getLocalSessionDescription());
								} catch (NetworkConnectionException e1) {
									e1.printStackTrace();
									fail();
								}

								try {
									assertNotNull(anEvent.getSource().getRawRemoteSessionDescription());
								} catch (NetworkConnectionException e) {
									e.printStackTrace();
									fail();
								}

								try {
									assertNotNull(anEvent.getSource().getRemoteSessionDescription());
								} catch (NetworkConnectionException e) {
									e.printStackTrace();
									fail();
								}
							} else {
								logger.error("Modify failed" + anEvent);
							}
						}
					};

					myNetworkConnection.addListener(myNetworkConnectionListernerInner);
					try {
						myNetworkConnection.modify(null, REMOTE_SDP);
					} catch (NetworkConnectionException e) {						
						e.printStackTrace();
						fail();
					} catch (SdpException e) {
						e.printStackTrace();
						fail();
					}

				} else {
					logger.error("Modify failed" + anEvent);
				}

			}
		};
		// register listener
		myNetworkConnection.addListener(myNetworkConnectionListerner);
		// modify media connection to get the answer.
		myNetworkConnection.modify("$", null);

		waitForMessage();

	}

	public void tearDown() {

		assertTrue("Expected to receive Modify event for CRCX", modifyCRCXEventReceived);
		assertTrue("Expected to receive Modify event for MDCX", modifyMDCXEventReceived);
		this.mgw.checkState();
		try {
			super.tearDown();
		} catch (Exception ex) {
		}

		logger.info(this.getName() + " Completed");
	}

}
