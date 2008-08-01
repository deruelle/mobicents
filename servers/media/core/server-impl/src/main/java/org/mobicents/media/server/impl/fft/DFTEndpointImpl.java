package org.mobicents.media.server.impl.fft;

import java.io.File;

import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.BaseResourceManager;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.UnknownSignalException;

public class DFTEndpointImpl extends BaseEndpoint {

	private String testName = null;
	private File dumpDir = null;

	private int[] sineFrequencies = null;
	private int sineDuration = -1;

	private DFTSineSignal played = null;

	public DFTEndpointImpl(String localName) {
		super(localName);
		this.setMaxConnectionsAvailable(1);

	}

	public void play(EventID signalID, String[] params, String connectionID,
			NotificationListener listener, boolean keepAlive, boolean beginRecordingImmediately)
			throws UnknownSignalException {

		if (played != null) {
			played.stop();
		}

		switch (signalID) {
		case PLAY:
			played = new DFTSineSignal(listener, this, super
					.getConnection(connectionID));
			played.start();
			break;

		default:
			break;
		}

	}

	@Override
	public BaseResourceManager initResourceManager() {

		return new DFTResourceManager();
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public File getDumpDir() {
		return dumpDir;
	}

	public void setDumpDir(File dumpDir) {
		this.dumpDir = dumpDir;
	}

	public int[] getSineFrequencies() {
		return sineFrequencies;
	}

	public void setSineFrequencies(int[] sineFrequencies) {
		this.sineFrequencies = sineFrequencies;
	}

	public int getSineDuration() {
		return sineDuration;
	}

	public void setSineDuration(int sineDuration) {
		this.sineDuration = sineDuration;
	}

}
