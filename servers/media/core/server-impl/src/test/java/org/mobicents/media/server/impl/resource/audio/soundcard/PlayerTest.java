package org.mobicents.media.server.impl.resource.audio.soundcard;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.server.impl.FailureEventImpl;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.audio.AudioPlayerImpl;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author amit bhayani
 *
 */
public class PlayerTest {

	Logger logger = Logger.getLogger(PlayerTest.class);

	private TimerImpl timer;
	private Semaphore semaphore;
	private AudioPlayerImpl audioPlayer;

	private PlayerImpl soundCardPlayer;

	private boolean completed = false;
	private boolean failed = false;
	private boolean stopped = false;
	private boolean started = false;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {

		timer = new TimerImpl();

		audioPlayer = new AudioPlayerImpl("test", timer);
		audioPlayer.addListener(new AudioPlayerListener());

		soundCardPlayer = new PlayerImpl("soundCardPlayerTest");
		soundCardPlayer.addListener(new PlayerListener());

		semaphore = new Semaphore(0);

	}

	private void testRecording(String src) throws Exception {
		URL url = PlayerTest.class.getClassLoader().getResource(src);
		String path = url.getPath();

		audioPlayer.setURL(url.toExternalForm());
		audioPlayer.connect(soundCardPlayer);

		audioPlayer.start();
		soundCardPlayer.start();

		semaphore.tryAcquire(60, TimeUnit.SECONDS);

		assertTrue("It is expected that audio player finishes playback", completed);
		// completed = false;

		soundCardPlayer.stop();
		audioPlayer.stop();

		// give a bit of time to complete stopping
		semaphore.tryAcquire(5, TimeUnit.SECONDS);

		assertFalse("Player failed", failed);
		assertTrue("Audio Player have to send COMPLETED event", completed);
		assertTrue("The Sound Player have to send Started event", started);
		assertTrue("The Sound Player have to send Stopped event", stopped);
	}

	@Test
	public void test_Wav_L16_MONO() throws Exception {
		testRecording("org/mobicents/media/server/impl/gwn44m.wav");
	}

	@Test
	public void test_Wav_L16_STEREO() throws Exception {
		testRecording("org/mobicents/media/server/impl/gwn44s.wav");
	}

	@Test
	public void test_Wav_LINERA() throws Exception {
		testRecording("org/mobicents/media/server/impl/fox-full.wav");
	}

	@After
	public void tearDown() {

	}

	private class AudioPlayerListener implements NotificationListener {

		public void update(NotifyEvent event) {
			switch (event.getEventID()) {
			case NotifyEvent.COMPLETED:
				completed = true;
				semaphore.release();
				break;
			case NotifyEvent.START_FAILED:
			case NotifyEvent.TX_FAILED:
				failed = true;
				semaphore.release();
				break;
			}
		}

	}

	private class PlayerListener implements NotificationListener {

		public void update(NotifyEvent event) {
			switch (event.getEventID()) {
			case NotifyEvent.STARTED:
				started = true;
				System.out.println("The Player started");
				break;
			case NotifyEvent.STOPPED:
				stopped = true;
				System.out.println("The Player Stopped");
				break;
			case NotifyEvent.RX_FAILED:
				completed = false;
				failed = true;
				semaphore.release();

				FailureEventImpl failEvent = (FailureEventImpl) event;
				System.out.println(failEvent.getException());
				logger.error("Player failed " + failEvent.getException());
				break;

			}
		}

	}
}
