package test.msgflow.networkconnection;

import java.io.Serializable;

import javax.media.mscontrol.JoinEvent;
import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.JoinableStream;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.StatusEvent;
import javax.media.mscontrol.StatusEventListener;
import javax.media.mscontrol.Joinable.Direction;
import javax.media.mscontrol.JoinableStream.StreamType;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.NetworkConnectionEvent;
import javax.media.mscontrol.networkconnection.NetworkConnectionException;
import javax.media.mscontrol.resource.Error;
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
public class NetworkConnectionTest extends MessageFlowHarness implements Serializable {

	private MGW mgw;

	public NetworkConnectionTest() {
		super();
		logger = Logger.getLogger(NetworkConnectionTest.class);
	}

	public NetworkConnectionTest(String name) {
		super(name);
		logger = Logger.getLogger(NetworkConnectionTest.class);
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
					testPassed = true;

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
								testPassed = (testPassed && true);

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
		assertTrue(this.getName() + " passed = " + testPassed, testPassed);

	}

	public void testNetworkConnectionJoin() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection myNetworkConnection1 = myMediaSession.createNetworkConnection();

		final NetworkConnection myNetworkConnection2 = myMediaSession.createNetworkConnection();

		StatusEventListener statusEvtList = new StatusEventListener() {

			public void onEvent(StatusEvent event) {
				if (event.getError().equals(Error.e_OK)) {
					logger.info("Join successful " + event);
					testPassed = true;

				} else {
					logger.error("Join failed " + event);
					fail("Join of NC1 and NC2 failed");
				}
			}
		};

		myNetworkConnection1.addListener(statusEvtList);
		myNetworkConnection1.joinInitiate(Direction.SEND, myNetworkConnection2, this);

		waitForMessage();

		/*
		 * Test for COntainers
		 */
		// Get other container
		Joinable[] otherContainer = myNetworkConnection1.getJoinees();
		assertEquals(1, otherContainer.length);

		// NC1 ---> Send ---> NC2
		otherContainer = myNetworkConnection1.getJoinees(Direction.SEND);
		assertEquals(1, otherContainer.length);

		// NC2 is joined to NC1 Container with direction RECV and NC2 joinees is
		// equal to NC1
		assertEquals(myNetworkConnection1, (otherContainer[0].getJoinees(Direction.RECV))[0]);

		// Direction.RECV should fetch no Container
		otherContainer = myNetworkConnection1.getJoinees(Direction.RECV);
		assertEquals(0, otherContainer.length);

		/*
		 * Test for Audio Stream
		 */
		// Joinable Stream is of type Audio only
		JoinableStream stream = myNetworkConnection1.getJoinableStream(StreamType.audio);
		assertNotNull(stream);
		assertNull(myNetworkConnection1.getJoinableStream(StreamType.video));

		// Joined to one other NC
		Joinable[] j = stream.getJoinees();
		assertEquals(1, j.length);

		// This NC is SENDing stream to other NC
		Joinable[] temp = stream.getJoinees(Direction.SEND);
		assertNotNull(temp[0]);

		// The stream from this NC and its joinees joinees will be same
		JoinableStream streamOther = (JoinableStream) j[0];
		Joinable[] j1 = streamOther.getJoinees();

		assertEquals(j1[0], stream);
		assertNotNull(streamOther.getJoinees(Direction.RECV)[0]);

		// NC1 is already joined to NC2. Trying to connect NC1 to NC3 should
		// throw Exception
		final NetworkConnection myNetworkConnection3 = myMediaSession.createNetworkConnection();
		try {
			myNetworkConnection1.joinInitiate(Direction.DUPLEX, myNetworkConnection3, this);
			fail("NC1 already connected to NC2");
		} catch (MsControlException e) {
			// expected. Ignore
			// e.printStackTrace();
		}

		// NC1 is not joined to NC3. Trying to unjoin NC3 should raise exception
		try {
			myNetworkConnection1.unjoinInitiate(myNetworkConnection3, this);
			fail("NC1 not connected to NC3");
		} catch (MsControlException e) {
			// expected
			// e.printStackTrace();
		}
		assertTrue(this.getName() + " passed = " + testPassed, testPassed);
	}

	public void testNetworkConnectionUnJoin() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection();

		final NetworkConnection NC2 = myMediaSession.createNetworkConnection();

		// StatusEventListenerImpl impl = new StatusEventListenerImpl(NC1, NC2);
		final ContextImpl serImpl = new ContextImpl();

		StatusEventListener impl = new StatusEventListener() {
			public void onEvent(StatusEvent event) {

				if (event.getError().equals(Error.e_OK)) {
					if (JoinEvent.ev_Joined.equals(event.getEventID())) {
						logger.info("testNetworkConnectionUnJoin - Join successful " + event);
						try {
							NC2.unjoinInitiate(NC1, serImpl);
						} catch (MsControlException e) {
							e.printStackTrace();
							fail("Unjoin failed");
						}
					} else if (JoinEvent.ev_Unjoined.equals(event.getEventID())) {
						logger.info("testNetworkConnectionUnJoin - Un-Join successful " + event);

						// After un-join the list of Joinees should be 0
						try {
							Joinable[] j1 = NC1.getJoinees();
							assertEquals(0, j1.length);
						} catch (MsControlException e) {
							logger.error(e);
							fail(" unexpected error " + e.getMessage());
						}

						try {
							Joinable[] j2 = NC2.getJoinees();
							assertEquals(0, j2.length);
						} catch (MsControlException e) {
							logger.error(e);
							fail(" unexpected error " + e.getMessage());
						}

						testPassed = true;
					} else {
						logger.error("This event is not expected " + event.getEventID());
						fail("Expected either JoinEvent.ev_Joined or JoinEvent.ev_Unjoined but received "
								+ event.getEventID());
					}

				} else {
					System.out.println("Hereeeeeee  error NOT ok" + event);
					logger.error("testNetworkConnectionUnJoin - Join failed " + event);
					fail("Join of NC1 and NC2 failed");
				}
			}
		};

		NC2.addListener(impl);

		logger.info("testNetworkConnectionUnJoin - joinInitiate calling");
		NC2.joinInitiate(Direction.DUPLEX, NC1, serImpl);

		waitForMessage();
		waitForMessage();
		assertTrue(this.getName() + " passed = " + testPassed, testPassed);
	}

	public void testNetworkConnectionReJoin() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection();

		final NetworkConnection NC2 = myMediaSession.createNetworkConnection();

		// StatusEventListenerImpl impl = new StatusEventListenerImpl(NC1, NC2);
		final ContextImpl serImpl = new ContextImpl();

		StatusEventListener impl = new StatusEventListener() {
			public void onEvent(StatusEvent event) {

				if (event.getError().equals(Error.e_OK)) {
					if (JoinEvent.ev_Joined.equals(event.getEventID())) {
						logger.info("testNetworkConnectionReJoin - Join successful " + event);

						Joinable[] temp;
						try {
							// NC2 <--- RECV Stream <--- NC1
							temp = NC2.getJoinableStreams()[0].getJoinees(Direction.RECV);
							assertNotNull(temp[0]);

							// This should return empty Joinees
							temp = NC2.getJoinableStreams()[0].getJoinees(Direction.SEND);
							assertEquals(0, temp.length);
						} catch (MsControlException e1) {
							logger.error(e1);
							fail("Unxpected error " + e1.getMessage());
						}
						NC2.removeListener(this);

						StatusEventListener impl = new StatusEventListener() {
							public void onEvent(StatusEvent event) {

								if (event.getError().equals(Error.e_OK)) {
									if (JoinEvent.ev_Joined.equals(event.getEventID())) {
										logger.info("testNetworkConnectionReJoin - Join successful " + event);

										Joinable[] temp;
										try {
											// NC2 --> SEND Stream---> NC1
											temp = NC2.getJoinableStreams()[0].getJoinees(Direction.SEND);
											assertNotNull(temp[0]);

											// This should return empty Joinees
											temp = NC2.getJoinableStreams()[0].getJoinees(Direction.RECV);
											assertEquals(0, temp.length);

											testPassed = true;
										} catch (MsControlException e1) {
											logger.error(e1);
											fail("Unxpected error " + e1.getMessage());
										}
									} else {
										logger.error("testNetworkConnectionReJoin This event is not expected "
												+ event.getEventID());
										fail("Expected either JoinEvent.ev_Joined but received " + event.getEventID());
									}

								} else {
									logger.error("testNetworkConnectionReJoin - Join failed " + event);
									fail("Join of NC1 and NC2 failed");
								}
							}
						};
						NC2.addListener(impl);
						try {
							NC2.joinInitiate(Direction.SEND, NC1, serImpl);
						} catch (MsControlException e) {
							logger.equals(e);
							fail("Unjoin failed");
						}

					} else {
						logger.error("testNetworkConnectionReJoin This event is not expected " + event.getEventID());
						fail("Expected either JoinEvent.ev_Joined but received " + event.getEventID());
					}

				} else {
					System.out.println("Hereeeeeee  error NOT ok" + event);
					logger.error("testNetworkConnectionReJoin - Join failed " + event);
					fail("Join of NC1 and NC2 failed");
				}

			}
		};

		NC2.addListener(impl);
		NC2.joinInitiate(Direction.RECV, NC1, serImpl);

		waitForMessage();
		waitForMessage();
		assertTrue(this.getName() + " passed = " + testPassed, testPassed);
	}

	public void testNetworkConnectionRelease() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection();
		final NetworkConnection NC2 = myMediaSession.createNetworkConnection();
		final ContextImpl impl = new ContextImpl();

		final String REMOTE_SDP = "v=0\n" + "m=audio 1234 RTP/AVP  0 \n" + "c=IN IP4 192.168.145.1\n"
				+ "a=rtpmap:0 PCMU/8000\n";

		MediaEventListener<NetworkConnectionEvent> NC1Listener = new MediaEventListener<NetworkConnectionEvent>() {

			public void onEvent(NetworkConnectionEvent anEvent) {

				if (NetworkConnection.ev_Modify.equals(anEvent.getEventID())) {
					logger.info("MDCX Modify successful " + anEvent);

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

					StatusEventListener statusEvtList = new StatusEventListener() {

						JoinableStream joinStream1 = null;
						JoinableStream joinStream2 = null;

						public void onEvent(StatusEvent event) {

							if (event.getError().equals(Error.e_OK)) {
								if (JoinEvent.ev_Joined.equals(event.getEventID())) {
									logger.info("Join successful " + event);
									try {
										joinStream1 = NC1.getJoinableStreams()[0];
										joinStream2 = NC2.getJoinableStreams()[0];
										NC1.release();
									} catch (MsControlException e) {
										logger.equals(e);
										fail();
									}

								} else if (JoinEvent.ev_Unjoined.equals(event.getEventID())) {
									logger.info("UnJoin successful " + event);

									try {
										NC1.modify(null, REMOTE_SDP);
										fail("IllegalStateException not raised");
									} catch (IllegalStateException e) {
										logger.debug("Expected Exception " + e.getMessage());
										try {
											joinStream1.joinInitiate(Direction.DUPLEX, joinStream2, impl);
											fail("IllegalStateException not raised");
										} catch (IllegalStateException e1) {
											logger.debug("Expected Exception " + e.getMessage());
											testPassed = true;
										} catch (Exception e2) {
											fail("This exception is not expected");
											logger.error(e2);
										}

									} catch (Exception e) {
										fail("This exception is not expected");
										logger.error(e);
									}
								}
							} else {
								logger.error("Join failed " + event);
								fail("Join of NC1 and NC2 failed");
							}
						}
					};
					NC1.addListener(statusEvtList);

					try {
						NC1.joinInitiate(Direction.DUPLEX, NC2, impl);
					} catch (MsControlException e) {
						logger.error(e);
						fail("join failed");
					}

				} else {
					logger.error("Modify failed" + anEvent);
					fail("Modify failed");
				}
			}
		};

		// register listener
		NC1.addListener(NC1Listener);
		// modify media connection to get the answer.
		NC1.modify("$", REMOTE_SDP);

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
