package org.mobicents.media.server.impl.enp.fft;

import java.io.File;

import org.mobicents.media.server.impl.BaseEndpoint;

public class DFTEndpointImpl extends BaseEndpoint {

	private String testName = null;
	private File dumpDir = null;

	private int[] sineFrequencies = null;
	private int sineDuration = -1;


	public DFTEndpointImpl(String localName) {
		super(localName);
		this.setMaxConnectionsAvailable(1);

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
