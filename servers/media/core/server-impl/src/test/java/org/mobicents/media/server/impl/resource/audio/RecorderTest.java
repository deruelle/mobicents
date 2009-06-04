package org.mobicents.media.server.impl.resource.audio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Format;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author amit bhayani
 * 
 */
public class RecorderTest {

	private Logger logger = Logger.getLogger(RecorderTest.class);

	private EndpointImpl endpointPlayer;
	private TimerImpl timer;
	private Semaphore semaphore;
	private AudioPlayerImpl player;

	private EndpointImpl endpointRecorder;
	private RecorderImpl recorder;

	private final static Format[] formats = new Format[] { AVProfile.PCMA, AVProfile.PCMU, AVProfile.GSM,
			Codec.LINEAR_AUDIO };

	private boolean stopped = false;
	private boolean completed = false;
	private boolean failed = false;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {

		stopped = false;
		completed = false;
		failed = false;

		timer = new TimerImpl();
		endpointPlayer = new EndpointImpl();
		endpointPlayer.setTimer(timer);

		player = new AudioPlayerImpl("test");
		player.setEndpoint(endpointPlayer);
		player.setResourceType(10);

		endpointRecorder = new EndpointImpl();
		endpointPlayer.setTimer(timer);

		recorder = new RecorderImpl("test");
		recorder.setEndpoint(endpointRecorder);
		recorder.addListener(new RecorderListener());

		semaphore = new Semaphore(0);

	}

	@Test
	public void testSupportedFormats() {
		Format[] supported = recorder.getFormats();
		assertEquals(formats.length, supported.length);
		for (int i = 0; i < supported.length; i++) {
			boolean found = false;
			for (int j = 0; j < formats.length; j++) {
				if (supported[i].equals(formats[j])) {
					found = true;
					break;
				}
			}
			if (!found) {
				fail("Not found " + supported[i]);
			}
		}
	}

	@Test
	public void test_Wav_L16_8000() throws Exception {
		URL url = RecorderTest.class.getClassLoader().getResource("org/mobicents/media/server/impl/fox-full.wav");

		String tempFilePath = url.getPath();
		String recordDir = tempFilePath.substring(0, tempFilePath.lastIndexOf('/'));

		recorder.setRecordDir(recordDir);
		// let us record for 15 sec
		recorder.setRecordTime(15);

		System.out.println(recordDir);

		player.setFile(url.toExternalForm());
		player.connect(recorder);

		recorder.start("recorder-test/fox-full-recorded.wav");
		player.start();

		// After 8 sec we fire Stop
		semaphore.tryAcquire(8, TimeUnit.SECONDS);

		recorder.stop();
		semaphore.tryAcquire(2, TimeUnit.SECONDS);

		assertEquals(true, stopped);

		// Test directory creation
		try {
			File file = new File(recordDir + "/" + "recorder-test/fox-full-recorded.wav");
			assertEquals(true, file.exists());
		} catch (Exception e) {
			logger.error(e);
			fail("Recoded File 8kulaw-recorded.wav not created ");
		}
	}

	@Test
	public void test_8000_MONO_ALAW() throws Exception {
		URL url = AudioPlayerTest.class.getClassLoader().getResource(
				"org/mobicents/media/server/impl/addf8-Alaw-GW.wav");

		String tempFilePath = url.getPath();
		String recordDir = tempFilePath.substring(0, tempFilePath.lastIndexOf('/'));

		System.out.println(recordDir);

		recorder.setRecordDir(recordDir);

		// The file to be played is only of 2 sec.
		recorder.setRecordTime(4);

		player.setFile(url.toExternalForm());
		player.connect(recorder);

		recorder.start("addf8-Alaw-GW-recorded.wav");
		player.start();

		semaphore.tryAcquire(7, TimeUnit.SECONDS);

		// Here the File is just 2sec and timer is based on Byte size. And hence
		// no event is ever fired. Look at RecoderImpl TODO if we should have a
		// Timer too?
		assertEquals(false, completed);

		try {
			File file = new File(recordDir + "/" + "addf8-Alaw-GW-recorded.wav");
			assertEquals(true, file.exists());
		} catch (Exception e) {
			logger.error(e);
			fail("Recoded File addf8-Alaw-GW-recorded.wav not created ");
		}
	}

	@Test
	public void test_8000_MONO_ULAW() throws Exception {
		URL url = AudioPlayerTest.class.getClassLoader().getResource("org/mobicents/media/server/impl/8kulaw.wav");

		String tempFilePath = url.getPath();
		String recordDir = tempFilePath.substring(0, tempFilePath.lastIndexOf('/'));

		System.out.println(recordDir);

		recorder.setRecordDir(recordDir);

		// let us record for 5 sec
		recorder.setRecordTime(5);

		player.setFile(url.toExternalForm());
		player.connect(recorder);

		recorder.start("8kulaw-recorded.wav");
		player.start();

		semaphore.tryAcquire(10, TimeUnit.SECONDS);

		assertEquals(true, completed);

		try {
			File file = new File(recordDir + "/" + "8kulaw-recorded.wav");
			assertEquals(true, file.exists());
		} catch (Exception e) {
			logger.error(e);
			fail("Recoded File 8kulaw-recorded.wav not created ");
		}
	}


	private class RecorderListener implements NotificationListener {

		public void update(NotifyEvent event) {
			switch (event.getEventID()) {
			case RecorderEvent.STOPPED:
				System.out.println("STOPPED");
				stopped = true;
				semaphore.release();
				break;
			case RecorderEvent.DURATION_OVER:
				System.out.println("DURATION_OVER");
				completed = true;
				semaphore.release();
				break;
			case RecorderEvent.FAILED:
				System.out.println("FAILED");
				failed = true;
				semaphore.release();
				break;
			}
		}

	}

	@After
	public void tearDown() {
	}

}
