package org.mobicents.media.server.impl.jmf.recorderplayer.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import java.util.Timer;
import javax.sound.sampled.AudioFileFormat;

import org.mobicents.media.server.impl.SuperXCase;
import org.mobicents.media.server.impl.fft.SpectralAnalyser;
import org.mobicents.media.server.impl.jmf.player.AudioPlayer;
import org.mobicents.media.server.impl.jmf.player.PlayerEvent;
import org.mobicents.media.server.impl.jmf.player.PlayerListener;
import org.mobicents.media.server.impl.jmf.recorder.Recorder;
import org.mobicents.media.server.impl.jmf.recorder.RecorderEvent;
import org.mobicents.media.server.impl.jmf.recorder.RecorderListener;
import org.mobicents.media.server.impl.test.audio.SineGenerator;
import org.mobicents.media.server.impl.test.audio.SineStream;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.test.SpectrumEvent;

public class RecorderPlayerTest extends SuperXCase implements
		NotificationListener, RecorderListener, PlayerListener {

	// Is this correct approach?
	protected String recordDirName = System.getenv("JBOSS_HOME");
	protected File recorDir = new File(recordDirName + File.separator
			+ "server" + File.separator + "default" + File.separator + "tmp");
	protected String recordFileName = "RecorderPlayerTest";
	protected File recordFile = null;
	protected boolean tmpCreated, defaultCreated;
	protected int sineDuration = 5000;
	protected int[] sineF = new int[] { 155 };
	protected SineStream ss = null;
	protected SpectralAnalyser sa = null;
        private Timer timer = new Timer();
        
	// Recorder event flags
	protected boolean start_r, stop_on_reqeust;

	// Player event flags
	protected boolean start_p, end_of_media;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ss = (SineStream) new SineGenerator(sineDuration, sineF, true)
				.getStreams()[0];
		sa = new SpectralAnalyser();
		sa.addListener(this);
	}

	public void testADoRecord_WAV() throws Exception {
		setUpFiles("wav");

		Recorder rec = new Recorder(AudioFileFormat.Type.WAVE,
				this.sineDuration);
		rec.addListener(this);
		ss.start();
		rec.start(recordFile.toString(), ss);
		if (!doTest(sineDuration / 1000 + 2)) {
			fail(getReason());
		}

		rec.stop();
		rec.removeListener(this);
		try {
			Thread.currentThread().wait(500);
		} catch (Exception e) {

		}

		if (!start_r && !stop_on_reqeust) {
			doFail("Didnt receive one of events start[" + start_r
					+ "], stop_on_req[" + stop_on_reqeust + "]");
		}

		if (!recordFile.exists()
				|| new FileInputStream(recordFile).available() == 0) {
			doFail("Record file does not exist or is of null size.");
		}

		if (getIsFailed())
			fail(getReason());

	}

	public void testBPlayerSpectralAnalysis_WAV() throws Exception {
		AudioPlayer ap=null;
		try {
			setUpFiles("wav");
			ap = new AudioPlayer(timer, 20);
			ap.addListener(this);
			sa.prepare(null, ap.start(recordFile.toURL().toString()));
			sa.start();
			
			if(!doTest(sineDuration/1000+1))
			{
				fail(getReason());
			}
			
		} finally {
			destroyFiles();
			sa.stop();
			if(ap!=null);
			ap.stop();
		}
	}

	// for SA
	public void update(NotifyEvent event) {
		double[] spectra = ((SpectrumEvent) event).getSpectra();	
		
		if(!checkFreq(spectra, sineF, 1))
		{
			doFail("Failed to match frequencies for declared: "+Arrays.toString(sineF));
			return;
		}
	
	
	}

	// for Recorder
	public void update(RecorderEvent evt) {

		switch (evt.getEventType()) {
		case STARTED:
			if(start_r)
			{
				doFail("Revceived ["+evt.getEventType()+"] more than once!!!");
			}
			start_r = true;
			break;
		case STOP_BY_REQUEST:
			if (!start_r)
				doFail("Rececived " + evt.getEventType()
						+ " before start event!!");
			stop_on_reqeust = true;
			break;
		case FACILITY_ERROR:
			doFail("Received " + evt.getEventType() + " with message"
					+ evt.getMessage());
			break;
		default:
			doFail("Event Type not recognized for recorder["+evt.getEventType()+"]");
			break;

		}

	}

	// for Player
	public void update(PlayerEvent event) {
		switch (event.getEventType()) {
		case STARTED:
			if(start_p)
				doFail("Received ["+event.getEventType()+"] more than once!!");
			start_p=true;
			break;
		case END_OF_MEDIA:
			if(!start_p)
			{
				doFail("Received ["+event.getEventType()+"] before start event!!!");
				return;
			}
			break;
		case STOP_BY_REQUEST:
		case FACILITY_ERROR:
		default:
			doFail("Received ["+event.getEventType()+"] , this sholdnt happen!!!");
			break;
		}
	}

	private void setUpFiles(String extension) throws Exception {

		if (!recorDir.getParentFile().exists()) {
			recorDir.getParentFile().mkdir();
			defaultCreated = true;
		}

		if (!recorDir.exists()) {
			recorDir.mkdir();
			tmpCreated = true;
		}

		recordFile = new File(recorDir, recordFileName + "." + extension);
	}

	private void destroyFiles() {
		if (defaultCreated) {
			recorDir.getParentFile().delete();

		} else if (tmpCreated) {
			recorDir.delete();
		}

		defaultCreated = tmpCreated = false;
		if (recordFile.exists())
			recordFile.delete();

	}

}
