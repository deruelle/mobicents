package test.msgflow.mediagroup;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import javax.media.mscontrol.MediaEventListener;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.join.JoinEvent;
import javax.media.mscontrol.join.JoinEventListener;
import javax.media.mscontrol.join.Joinable;
import javax.media.mscontrol.join.JoinableStream;
import javax.media.mscontrol.join.Joinable.Direction;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.PlayerEvent;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.resource.ResourceEvent;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;

import test.msgflow.MessageFlowHarness;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MediaGroupTest extends MessageFlowHarness {
	private MGW mgw;

	public MediaGroupTest() {
		super();
		logger = Logger.getLogger(MediaGroupTest.class);
	}

	public MediaGroupTest(String name) {
		super(name);
		logger = Logger.getLogger(MediaGroupTest.class);
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

	public void tearDown() {
		this.mgw.checkState();
		try {
			super.tearDown();
		} catch (Exception ex) {
		}
	}

	// TODO Do we repeat all test of NC?

	public void testMediaGroupJoin() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		final MediaGroup MG1 = myMediaSession.createMediaGroup(MediaGroup.PLAYER);
		final ContextImpl ser = new ContextImpl();
		final String REMOTE_SDP = "v=0\n" + "m=audio 1234 RTP/AVP  0 \n" + "c=IN IP4 192.168.145.1\n"
				+ "a=rtpmap:0 PCMU/8000\n";

		JoinEventListener statusEvtList = new JoinEventListener() {

			public void onEvent(JoinEvent event) {
				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("Join successful " + event);
						testPassed = true;
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
		MG1.joinInitiate(Direction.SEND, NC1, ser);

		waitForMessage();

		/*
		 * Test for Containers
		 */
		// Get other container
		Joinable[] otherContainer = NC1.getJoinees();
		assertEquals(1, otherContainer.length);

		// MG1 ---> Send ---> NC1
		otherContainer = MG1.getJoinees(Direction.SEND);
		assertEquals(1, otherContainer.length);

		// NC1 is joined to MG1 Container with direction RECV and NC1 joinees is
		// equal to MG1
		assertEquals(MG1, (otherContainer[0].getJoinees(Direction.RECV))[0]);

		// Direction.RECV should fetch no Container
		otherContainer = MG1.getJoinees(Direction.RECV);
		assertEquals(0, otherContainer.length);

		/*
		 * Test for Audio Stream
		 */
		// Joinable Stream is of type Audio only
		JoinableStream stream = MG1.getJoinableStream(JoinableStream.StreamType.audio);
		assertNotNull(stream);
		assertNull(MG1.getJoinableStream(JoinableStream.StreamType.video));

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

		// MG1 is already joined to NC1. Trying to connect MG1 to NC3 should
		// throw Exception
		final NetworkConnection NC3 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		try {
			MG1.joinInitiate(Direction.DUPLEX, NC3, ser);
			fail("MG1 already connected to NC1");
		} catch (MsControlException e) {
			// expected. Ignore
			// e.printStackTrace();
		}

		// MG1 is not joined to NC3. Trying to unjoin NC3 should raise exception
		try {
			MG1.unjoinInitiate(NC3, ser);
			fail("MG1 not connected to NC3");
		} catch (MsControlException e) {
			// expected
			// e.printStackTrace();
		}
		assertTrue(this.getName() + " passed = " + testPassed, testPassed);

	}

	public void testPlayComplete() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		final MediaGroup MG1 = myMediaSession.createMediaGroup(MediaGroup.PLAYER);
		final Player player = MG1.getPlayer();
		final ContextImpl ser = new ContextImpl();

		URI uri = new URI("file://home/abhayani/workarea/temp/test.wav");
		try {
			player.play(uri, null, null);
			fail("Player shouldn't have executed play as its not yet connected to other MO");
		} catch (MsControlException e) {
			// expected
		}

		JoinEventListener statusEvtList = new JoinEventListener() {

			public void onEvent(JoinEvent event) {
				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("Join successful " + event);

						MediaEventListener<PlayerEvent> playerListener = new MediaEventListener<PlayerEvent>() {

							public void onEvent(PlayerEvent anEvent) {
								if (anEvent.isSuccessful()) {
									if (PlayerEvent.PLAY_COMPLETED == anEvent.getEventType()) {
										logger.debug(" Play completed successfully " + anEvent.getEventType());
										testPassed = true;
									}
								} else {
									logger.error("Received Error from Player " + anEvent);
								}
							}
						};

						player.addListener(playerListener);
						try {
							URI[] files = new URI[2];
							files[0] = new URI("file://home/abhayani/workarea/temp/test1.wav");
							files[1] = new URI("file://home/abhayani/workarea/temp/test2.wav");
							player.play(files, null, null);
						} catch (MsControlException e) {
							logger.error(e);
							fail("Player failed");
						} catch (URISyntaxException e) {
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

	// TODO : The MGW sends NTFY even after stopping all announcement. Need to
	// fix that
	public void testPlayerStop() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		final MediaGroup MG1 = myMediaSession.createMediaGroup(MediaGroup.PLAYER);
		final Player player = MG1.getPlayer();
		final ContextImpl ser = new ContextImpl();

		JoinEventListener statusEvtList = new JoinEventListener() {

			public void onEvent(JoinEvent event) {
				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("Join successful " + event);

						MediaEventListener<PlayerEvent> playerListener = new MediaEventListener<PlayerEvent>() {

							public void onEvent(PlayerEvent anEvent) {
								if (anEvent.isSuccessful()) {
									if (PlayerEvent.PLAY_COMPLETED == anEvent.getEventType()
											&& ResourceEvent.STOPPED == anEvent.getQualifier()) {
										logger.debug(" Play Stopped successfully ");
										testPassed = true;

									} else {
										logger.error("Player did not stop and Event received = " + anEvent);
										fail("Failed to stop player");
									}
								} else {
									logger.error("Received Error from Player " + anEvent);
								}
							}
						};

						player.addListener(playerListener);
						try {
							URI[] files = new URI[2];
							files[0] = new URI("file://home/abhayani/workarea/temp/test1.wav");
							files[1] = new URI("file://home/abhayani/workarea/temp/test2.wav");
							player.play(files, null, null);

							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// Ignore
							}

							player.stop();
						} catch (MsControlException e) {
							logger.error(e);
							fail("Player failed");
						} catch (URISyntaxException e) {
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

	public void testMediaGroupRelease() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		final MediaGroup MG1 = myMediaSession.createMediaGroup(MediaGroup.PLAYER);
		final Player player = MG1.getPlayer();
		final ContextImpl ser = new ContextImpl();

		JoinEventListener statusEvtList = new JoinEventListener() {

			public void onEvent(JoinEvent event) {
				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("Join successful " + event);

						MG1.release();

					} else if (JoinEvent.UNJOINED == event.getEventType()) {
						// Once MG is released, trying to call play should throw
						// an exception that MG is not joined to any other MO
						try {
							player.play(new URI("file://home/abhayani/workarea/temp/test3.wav"), null, null);
						} catch (MsControlException e) {
							logger.debug("Expected Error ", e);
							testPassed = true;
						} catch (URISyntaxException e) {
							logger.error(e);
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

		assertTrue(this.getName() + " passed = " + testPassed, testPassed);

	}

	public void testv_Fail() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		final MediaGroup MG1 = myMediaSession.createMediaGroup(MediaGroup.PLAYER);
		final Player player = MG1.getPlayer();
		final ContextImpl ser = new ContextImpl();

		JoinEventListener statusEvtList = new JoinEventListener() {

			public void onEvent(JoinEvent event) {
				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("Join successful " + event);

						MediaEventListener<PlayerEvent> playerListener = new MediaEventListener<PlayerEvent>() {

							public void onEvent(PlayerEvent anEvent) {
								if (anEvent.isSuccessful()) {
									if (PlayerEvent.PLAY_COMPLETED == anEvent.getEventType()) {
										logger.debug(" Play completed successfully " + anEvent.getEventType());
									}
								} else {
									logger.error("Received Error from Player " + anEvent);
								}
							}
						};

						player.addListener(playerListener);
						try {
							URI[] files = new URI[2];
							files[0] = new URI("file://home/abhayani/workarea/temp/test1.wav");
							files[1] = new URI("file://home/abhayani/workarea/temp/test2.wav");
							player.play(files, null, null);
						} catch (MsControlException e) {
							logger.error(e);
							fail("Player failed");
						} catch (URISyntaxException e) {
							logger.error(e);
							fail("Player failed");
						}
						Parameters p = MG1.createParameters();
						p.put(Player.BEHAVIOUR_IF_BUSY, Player.FAIL_IF_BUSY);
						try {
							player.play(new URI("file://home/abhayani/workarea/temp/test3.wav"), null, p);
						} catch (MsControlException e) {
							logger.debug("Expected Error ", e);
							testPassed = true;
						} catch (URISyntaxException e) {
							logger.error(e);
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

	public void testv_Queue() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		final MediaGroup MG1 = myMediaSession.createMediaGroup(MediaGroup.PLAYER);
		final Player player = MG1.getPlayer();
		final ContextImpl ser = new ContextImpl();

		JoinEventListener statusEvtList = new JoinEventListener() {

			public void onEvent(JoinEvent event) {
				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("Join successful " + event);

						MediaEventListener<PlayerEvent> playerListener = new MediaEventListener<PlayerEvent>() {
							int noOfPlayCompleted = 0;

							public void onEvent(PlayerEvent anEvent) {
								if (anEvent.isSuccessful()) {
									if (PlayerEvent.PLAY_COMPLETED == anEvent.getEventType()) {
										logger.debug(" Play completed successfully " + anEvent.getEventType());
										noOfPlayCompleted++;
										if (noOfPlayCompleted == 2) {
											testPassed = true;
										}
									}
								} else {
									logger.error("Received Error from Player " + anEvent);
									fail("Player failed");
								}
							}
						};

						player.addListener(playerListener);
						try {
							URI[] files = new URI[2];
							files[0] = new URI("file://home/abhayani/workarea/temp/test1.wav");
							files[1] = new URI("file://home/abhayani/workarea/temp/test2.wav");
							player.play(files, null, null);

							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
							}

							Parameters p = MG1.createParameters();
							p.put(Player.BEHAVIOUR_IF_BUSY, Player.QUEUE_IF_BUSY);
							player.play(new URI("file://home/abhayani/workarea/temp/test3.wav"), null, p);

						} catch (MsControlException e) {
							logger.error(e);
							fail("Player failed");
						} catch (URISyntaxException e) {
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
		waitForMessage();

		assertTrue(this.getName() + " passed = " + testPassed, testPassed);
	}

	public void testv_Stop() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection(NetworkConnection.BASIC);
		final MediaGroup MG1 = myMediaSession.createMediaGroup(MediaGroup.PLAYER);
		final Player player = MG1.getPlayer();
		final ContextImpl ser = new ContextImpl();

		JoinEventListener statusEvtList = new JoinEventListener() {

			public void onEvent(JoinEvent event) {
				if (event.isSuccessful()) {
					if (JoinEvent.JOINED == event.getEventType()) {
						logger.info("Join successful " + event);

						MediaEventListener<PlayerEvent> playerListener = new MediaEventListener<PlayerEvent>() {
							int noOfPlayCompleted = 0;

							public void onEvent(PlayerEvent anEvent) {
								if (anEvent.isSuccessful()) {
									if (PlayerEvent.PLAY_COMPLETED == anEvent.getEventType()) {
										if (ResourceEvent.STOPPED == anEvent.getQualifier()) {
											logger.debug(" Play Stopped successfully " + anEvent.getEventType());
											testPassed = true;
										} else if (ResourceEvent.STANDARD_COMPLETION == anEvent.getQualifier()) {
											logger.debug(" Play Completed successfully " + anEvent.getEventType());
											testPassed = testPassed && true;
										}
									} else {
										logger.error("Received Error from Player " + anEvent);
										fail("Player failed");
									}
								} else {
									logger.error("Received Error from Player " + anEvent);
									fail("Player failed");
								}
							}
						};

						player.addListener(playerListener);
						try {
							URI[] files = new URI[2];
							files[0] = new URI("file://home/abhayani/workarea/temp/test1.wav");
							files[1] = new URI("file://home/abhayani/workarea/temp/test2.wav");
							player.play(files, null, null);

							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
							}

							Parameters p = MG1.createParameters();
							p.put(Player.BEHAVIOUR_IF_BUSY, Player.STOP_IF_BUSY);
							player.play(new URI("file://home/abhayani/workarea/temp/test3.wav"), null, p);

						} catch (MsControlException e) {
							logger.error(e);
							fail("Player failed");
						} catch (URISyntaxException e) {
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
		waitForMessage();

		assertTrue(this.getName() + " passed = " + testPassed, testPassed);
	}

	private class ContextImpl implements Serializable {

	}
}
