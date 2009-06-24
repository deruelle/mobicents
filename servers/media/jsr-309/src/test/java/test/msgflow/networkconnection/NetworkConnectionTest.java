package test.msgflow.networkconnection;

import java.io.Serializable;

import javax.media.mscontrol.MediaEventListener;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.join.JoinEvent;
import javax.media.mscontrol.join.JoinEventListener;
import javax.media.mscontrol.join.Joinable;
import javax.media.mscontrol.join.JoinableStream;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.SdpPortManager;
import javax.media.mscontrol.networkconnection.SdpPortManagerEvent;
import javax.media.mscontrol.networkconnection.SdpPortManagerException;

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

	public void testprocessSdpOffer() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection myNetworkConnection = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		final String REMOTE_SDP = "v=0\n" + "m=audio 1234 RTP/AVP  0 \n" + "c=IN IP4 192.168.145.1\n"
				+ "a=rtpmap:0 PCMU/8000\n";

		SdpPortManager manager = myNetworkConnection.getSdpPortManager();

		MediaEventListener<SdpPortManagerEvent> myNetworkConnectionListerner = new MediaEventListener<SdpPortManagerEvent>() {

			public void onEvent(SdpPortManagerEvent anEvent) {

				if (anEvent.isSuccessful()) {
					logger.info("CRCX Modify successful " + anEvent);
					if (SdpPortManagerEvent.ANSWER_GENERATED == anEvent.getEventType()) {
						testPassed = true;
						assertNotNull(anEvent.getMediaServerSdp());
					}
				} else {
					logger.error("processSdpOffer failed" + anEvent);
				}

			}
		};
		// register listener
		manager.addListener(myNetworkConnectionListerner);
		manager.processSdpOffer(REMOTE_SDP.getBytes());
		waitForMessage();
		assertNotNull(manager.getUserAgentSessionDescription());
		assertNotNull(manager.getMediaServerSessionDescription());
		assertTrue(this.getName() + " passed = " + testPassed, testPassed);

	}

	public void testgenerateSDPOffer() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection myNetworkConnection = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		SdpPortManager manager = myNetworkConnection.getSdpPortManager();

		MediaEventListener<SdpPortManagerEvent> myNetworkConnectionListerner = new MediaEventListener<SdpPortManagerEvent>() {

			public void onEvent(SdpPortManagerEvent anEvent) {

				if (anEvent.isSuccessful()) {
					logger.info("CRCX Modify successful " + anEvent);
					if (SdpPortManagerEvent.OFFER_GENERATED == anEvent.getEventType()) {
						testPassed = true;
						assertNotNull(anEvent.getMediaServerSdp());
					}
				} else {
					logger.error("processSdpOffer failed" + anEvent);
				}
			}
		};
		// register listener
		manager.addListener(myNetworkConnectionListerner);
		manager.generateSdpOffer();
		waitForMessage();
		assertNull(manager.getUserAgentSessionDescription());
		assertNotNull(manager.getMediaServerSessionDescription());

		try {
			manager.generateSdpOffer();
			fail("Shouldn't allow calling generateSdpOffer() again");
		} catch (SdpPortManagerException e) {
			logger.error("expected error", e);
		}

		assertTrue(this.getName() + " passed = " + testPassed, testPassed);

	}

	public void testprocessSdpAnswer() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection myNetworkConnection = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		final String REMOTE_SDP = "v=0\n" + "m=audio 1234 RTP/AVP  0 \n" + "c=IN IP4 192.168.145.1\n"
				+ "a=rtpmap:0 PCMU/8000\n";

		SdpPortManager manager = myNetworkConnection.getSdpPortManager();

		MediaEventListener<SdpPortManagerEvent> myNetworkConnectionListerner = new MediaEventListener<SdpPortManagerEvent>() {

			public void onEvent(SdpPortManagerEvent anEvent) {

				if (anEvent.isSuccessful()) {
					logger.info("CRCX Modify successful " + anEvent);
					if (SdpPortManagerEvent.ANSWER_PROCESSED == anEvent.getEventType()) {
						testPassed = true;
						assertNotNull(anEvent.getMediaServerSdp());
					}
				} else {
					logger.error("processSdpOffer failed" + anEvent);
				}

			}
		};
		// register listener
		manager.addListener(myNetworkConnectionListerner);
		manager.processSdpAnswer(REMOTE_SDP.getBytes());
		waitForMessage();
		assertNotNull(manager.getUserAgentSessionDescription());
		assertNotNull(manager.getMediaServerSessionDescription());
		assertTrue(this.getName() + " passed = " + testPassed, testPassed);

	}

	public void testNetworkConnectionJoin() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection myNetworkConnection1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		final NetworkConnection myNetworkConnection2 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		JoinEventListener jointEvtList = new JoinEventListener() {

			public void onEvent(JoinEvent event) {
				if (event.isSuccessful()) {
					logger.info("Join successful " + event);
					testPassed = true;
				} else {
					logger.error("Join failed " + event);
					fail("Join of NC1 and NC2 failed");
				}
			}
		};

		myNetworkConnection1.addListener(jointEvtList);
		myNetworkConnection1.joinInitiate(Joinable.Direction.SEND, myNetworkConnection2, this);

		waitForMessage();

		/*
		 * Test for COntainers
		 */
		// Get other container
		Joinable[] otherContainer = myNetworkConnection1.getJoinees();
		assertEquals(1, otherContainer.length);

		// NC1 ---> Send ---> NC2
		otherContainer = myNetworkConnection1.getJoinees(Joinable.Direction.SEND);
		assertEquals(1, otherContainer.length);

		// NC2 is joined to NC1 Container with direction RECV and NC2 joinees is
		// equal to NC1
		assertEquals(myNetworkConnection1, (otherContainer[0].getJoinees(Joinable.Direction.RECV))[0]);

		// Direction.RECV should fetch no Container
		otherContainer = myNetworkConnection1.getJoinees(Joinable.Direction.RECV);
		assertEquals(0, otherContainer.length);

		/*
		 * Test for Audio Stream
		 */
		// Joinable Stream is of type Audio only
		JoinableStream stream = myNetworkConnection1.getJoinableStream(JoinableStream.StreamType.audio);
		assertNotNull(stream);
		assertNull(myNetworkConnection1.getJoinableStream(JoinableStream.StreamType.video));

		// Joined to one other NC
		Joinable[] j = stream.getJoinees();
		assertEquals(1, j.length);

		// This NC is SENDing stream to other NC
		Joinable[] temp = stream.getJoinees(Joinable.Direction.SEND);
		assertNotNull(temp[0]);

		// The stream from this NC and its joinees joinees will be same
		JoinableStream streamOther = (JoinableStream) j[0];
		Joinable[] j1 = streamOther.getJoinees();

		assertEquals(j1[0], stream);
		assertNotNull(streamOther.getJoinees(Joinable.Direction.RECV)[0]);

		// NC1 is already joined to NC2. Trying to connect NC1 to NC3 should
		// throw Exception
		final NetworkConnection myNetworkConnection3 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		try {
			myNetworkConnection1.joinInitiate(Joinable.Direction.DUPLEX, myNetworkConnection3, this);
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
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		final NetworkConnection NC2 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		// StatusEventListenerImpl impl = new StatusEventListenerImpl(NC1, NC2);
		final ContextImpl serImpl = new ContextImpl();

		JoinEventListener impl = new JoinEventListener() {
			public void onEvent(JoinEvent event) {

				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("testNetworkConnectionUnJoin - Join successful " + event);
						try {
							NC2.unjoinInitiate(NC1, serImpl);
						} catch (MsControlException e) {
							e.printStackTrace();
							fail("Unjoin failed");
						}
					} else if (JoinEvent.UNJOINED == event.getEventType()) {
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
						logger.error("This event is not expected " + event.getEventType());
						fail("Expected either JoinEvent.ev_Joined or JoinEvent.ev_Unjoined but received "
								+ event.getEventType());
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
		NC2.joinInitiate(Joinable.Direction.DUPLEX, NC1, serImpl);

		waitForMessage();
		waitForMessage();
		assertTrue(this.getName() + " passed = " + testPassed, testPassed);
	}

	public void testNetworkConnectionReJoinSameMO() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		final NetworkConnection NC2 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		// StatusEventListenerImpl impl = new StatusEventListenerImpl(NC1, NC2);
		final ContextImpl serImpl = new ContextImpl();

		JoinEventListener impl = new JoinEventListener() {
			public void onEvent(JoinEvent event) {

				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("testNetworkConnectionReJoin - Join successful " + event);

						Joinable[] temp;
						try {
							// NC2 <--- RECV Stream <--- NC1
							temp = NC2.getJoinableStreams()[0].getJoinees(Joinable.Direction.RECV);
							assertNotNull(temp[0]);

							// This should return empty Joinees
							temp = NC2.getJoinableStreams()[0].getJoinees(Joinable.Direction.SEND);
							assertEquals(0, temp.length);
						} catch (MsControlException e1) {
							logger.error(e1);
							fail("Unxpected error " + e1.getMessage());
						}
						NC2.removeListener(this);

						JoinEventListener impl = new JoinEventListener() {
							public void onEvent(JoinEvent event) {

								if (event.isSuccessful()) {
									if (JoinEvent.JOINED == event.getEventType()) {
										logger.info("testNetworkConnectionReJoin - Join successful " + event);

										Joinable[] temp;
										try {
											// NC2 --> SEND Stream---> NC1
											temp = NC2.getJoinableStreams()[0].getJoinees(Joinable.Direction.SEND);
											assertNotNull(temp[0]);

											// This should return empty Joinees
											temp = NC2.getJoinableStreams()[0].getJoinees(Joinable.Direction.RECV);
											assertEquals(0, temp.length);

											testPassed = true;
										} catch (MsControlException e1) {
											logger.error(e1);
											fail("Unxpected error " + e1.getMessage());
										}
									} else {
										logger.error("testNetworkConnectionReJoin This event is not expected "
												+ event.getEventType());
										fail("Expected either JoinEvent.ev_Joined but received " + event.getEventType());
									}

								} else {
									logger.error("testNetworkConnectionReJoin - Join failed " + event);
									fail("Join of NC1 and NC2 failed");
								}
							}
						};
						NC2.addListener(impl);
						try {
							NC2.joinInitiate(Joinable.Direction.SEND, NC1, serImpl);
						} catch (MsControlException e) {
							logger.equals(e);
							fail("Unjoin failed");
						}

					} else {
						logger.error("testNetworkConnectionReJoin This event is not expected " + event.getEventType());
						fail("Expected either JoinEvent.ev_Joined but received " + event.getEventType());
					}

				} else {
					logger.error("testNetworkConnectionReJoin - Join failed " + event);
					fail("Join of NC1 and NC2 failed");
				}

			}
		};

		NC2.addListener(impl);
		NC2.joinInitiate(Joinable.Direction.RECV, NC1, serImpl);

		waitForMessage();
		waitForMessage();
		assertTrue(this.getName() + " passed = " + testPassed, testPassed);
	}

	public void testNetworkConnectionReJoinOtherMO() throws Exception {

		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		final NetworkConnection NC2 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		final NetworkConnection NC3 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);

		final ContextImpl serImpl = new ContextImpl();

		JoinEventListener impl = new JoinEventListener() {
			public void onEvent(JoinEvent event) {

				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("testNetworkConnectionReJointoOtherMO - Join successful " + event);

						Joinable[] temp;
						try {
							// NC2 <--- RECV Stream <--- NC1
							temp = NC2.getJoinableStreams()[0].getJoinees(Joinable.Direction.RECV);
							assertNotNull(temp[0]);

							// NC2 ---> SEND Stream ---> NC1
							temp = NC2.getJoinableStreams()[0].getJoinees(Joinable.Direction.SEND);
							assertNotNull(temp[0]);

							NC2.unjoinInitiate(NC1, serImpl);
						} catch (MsControlException e1) {
							logger.error(e1);
							fail("Unxpected error " + e1.getMessage());
						}

					} else if (JoinEvent.UNJOINED == event.getEventType()) {
						logger.info("testNetworkConnectionReJointoOtherMO - UnJoin successful " + event);

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

						JoinEventListener impl2 = new JoinEventListener() {
							public void onEvent(JoinEvent event) {

								if (event.isSuccessful()) {
									if (JoinEvent.JOINED == event.getEventType()) {
										logger.info("testNetworkConnectionReJointoOtherMO - Join successful " + event);

										Joinable[] temp;
										try {
											// NC2 <--- RECV Stream <--- NC1
											temp = NC3.getJoinableStreams()[0].getJoinees(Joinable.Direction.RECV);
											assertEquals(0, temp.length);

											// NC2 ---> SEND Stream ---> NC1
											temp = NC3.getJoinableStreams()[0].getJoinees(Joinable.Direction.SEND);
											assertNotNull(temp[0]);
											testPassed = true;
										} catch (MsControlException e1) {
											logger.error(e1);
											fail("Unxpected error " + e1.getMessage());
										}

									} else {
										logger.error("testNetworkConnectionReJointoOtherMO This event is not expected "
												+ event.getEventType());
										fail("Expected either JoinEvent.ev_Joined but received " + event.getEventType());
									}

								} else {
									logger.error("testNetworkConnectionReJointoOtherMO - Join failed " + event);
									fail("Join of NC1 and NC2 failed");
								}

							}
						};

						NC3.addListener(impl2);
						try {
							NC3.joinInitiate(Joinable.Direction.SEND, NC2, serImpl);
						} catch (MsControlException e) {
							logger.error(e);
							fail("NC3 join to NC2 failed");
						}
					} else {
						logger.error("testNetworkConnectionReJointoOtherMO This event is not expected "
								+ event.getEventType());
						fail("Expected either JoinEvent.ev_Joined but received " + event.getEventType());
					}

				} else {
					logger.error("testNetworkConnectionReJointoOtherMO - Join failed " + event);
					fail("Join of NC1 and NC2 failed");
				}

			}
		};

		NC2.addListener(impl);
		NC2.joinInitiate(Joinable.Direction.DUPLEX, NC1, serImpl);

		waitForMessage();
		waitForMessage();
		assertTrue(this.getName() + " passed = " + testPassed, testPassed);
	}

	public void testNetworkConnectionRelease() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		final NetworkConnection NC2 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		final ContextImpl impl = new ContextImpl();

		final String REMOTE_SDP = "v=0\n" + "m=audio 1234 RTP/AVP  0 \n" + "c=IN IP4 192.168.145.1\n"
				+ "a=rtpmap:0 PCMU/8000\n";

		final SdpPortManager manager = NC1.getSdpPortManager();

		MediaEventListener<SdpPortManagerEvent> NC1Listener = new MediaEventListener<SdpPortManagerEvent>() {

			public void onEvent(SdpPortManagerEvent anEvent) {

				if (anEvent.isSuccessful()) {
					if (SdpPortManagerEvent.ANSWER_GENERATED == anEvent.getEventType()) {
						logger.info("MDCX Modify successful " + anEvent);

						assertNotNull(anEvent.getMediaServerSdp());

						try {
							assertNotNull(manager.getUserAgentSessionDescription());
							assertNotNull(manager.getMediaServerSessionDescription());
						} catch (SdpPortManagerException e3) {
							logger.error(e3);
							fail("Failed to get the SDP");
						}

						JoinEventListener statusEvtList = new JoinEventListener() {

							JoinableStream joinStream1 = null;
							JoinableStream joinStream2 = null;

							public void onEvent(JoinEvent event) {

								if (event.isSuccessful()) {
									if (JoinEvent.JOINED == event.getEventType()) {
										logger.info("Join successful " + event);
										try {
											joinStream1 = NC1.getJoinableStreams()[0];
											joinStream2 = NC2.getJoinableStreams()[0];
											NC1.release();
										} catch (MsControlException e) {
											logger.error(e);
											fail();
										}

									} else if (JoinEvent.UNJOINED == event.getEventType()) {
										logger.info("UnJoin successful " + event);

										try {
											manager.processSdpOffer(REMOTE_SDP.getBytes());
											fail("IllegalStateException not raised");
										} catch (IllegalStateException e) {
											logger.debug("Expected Exception " + e.getMessage());
											try {
												joinStream1.joinInitiate(Joinable.Direction.DUPLEX, joinStream2, impl);
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
							NC1.joinInitiate(Joinable.Direction.DUPLEX, NC2, impl);
						} catch (MsControlException e) {
							logger.error(e);
							fail("join failed");
						}

					}
				} else {
					logger.error("Modify failed" + anEvent);
					fail("Modify failed");
				}

			}
		};

		// register listener
		manager.addListener(NC1Listener);
		// modify media connection to get the answer.
		manager.processSdpOffer(REMOTE_SDP.getBytes());

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
