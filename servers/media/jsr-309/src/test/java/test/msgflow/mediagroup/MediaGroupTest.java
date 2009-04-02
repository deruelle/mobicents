package test.msgflow.mediagroup;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import javax.media.mscontrol.JoinEvent;
import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.JoinableStream;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.StatusEvent;
import javax.media.mscontrol.StatusEventListener;
import javax.media.mscontrol.Joinable.Direction;
import javax.media.mscontrol.JoinableStream.StreamType;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.Player;
import javax.media.mscontrol.mediagroup.PlayerEvent;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.resource.Error;
import javax.media.mscontrol.resource.MediaEventListener;

import org.apache.log4j.Logger;
import org.mobicents.javax.media.mscontrol.MediaSessionImpl;

import test.msgflow.MessageFlowHarness;

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
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection();
		final MediaGroup MG1 = myMediaSession.createMediaGroup();
		final ContextImpl ser = new ContextImpl();
		final String REMOTE_SDP = "v=0\n" + "m=audio 1234 RTP/AVP  0 \n" + "c=IN IP4 192.168.145.1\n"
				+ "a=rtpmap:0 PCMU/8000\n";

		StatusEventListener statusEvtList = new StatusEventListener() {

			public void onEvent(StatusEvent event) {
				if (event.getError().equals(Error.e_OK)) {
					if (JoinEvent.ev_Joined.equals(event.getEventID())) {
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
		JoinableStream stream = MG1.getJoinableStream(StreamType.audio);
		assertNotNull(stream);
		assertNull(MG1.getJoinableStream(StreamType.video));

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
		final NetworkConnection NC3 = myMediaSession.createNetworkConnection();
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

	public void testPlayer() throws Exception {
		final MediaSessionImpl myMediaSession = (MediaSessionImpl) msControlFactory.createMediaSession();
		final NetworkConnection NC1 = myMediaSession.createNetworkConnection();
		final MediaGroup MG1 = myMediaSession.createMediaGroup();
		final Player player = MG1.getPlayer();
		final ContextImpl ser = new ContextImpl();

		URI uri = new URI("file://home/abhayani/workarea/temp/test.wav");
		try {
			player.play(uri, null, null);
			fail("Player shouldn't have executed play as its not yet connected to other MO");
		} catch (MsControlException e) {
			// expected
		}

		StatusEventListener statusEvtList = new StatusEventListener() {

			public void onEvent(StatusEvent event) {
				if (event.getError().equals(Error.e_OK)) {
					if (JoinEvent.ev_Joined.equals(event.getEventID())) {
						logger.info("Join successful " + event);

						MediaEventListener<PlayerEvent> playerListener = new MediaEventListener<PlayerEvent>() {

							public void onEvent(PlayerEvent anEvent) {
								if (Error.e_OK.equals(anEvent.getError())) {
									if (Player.ev_PlayComplete.equals(anEvent.getEventID())) {
										testPassed = true;
									}
								} else {
									logger.error("Received Error from Player " + anEvent);
								}
							}
						};

						player.addListener(playerListener);
						try {
							player.play(new URI("file://home/abhayani/workarea/temp/test.wav"), null, null);
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

	private class ContextImpl implements Serializable {

	}
}