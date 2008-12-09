package org.mobicents.media.server.impl.enp.fft;

import java.io.File;

import java.util.HashMap;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.Generator;
import org.mobicents.media.server.impl.events.announcement.AudioPlayer;
import org.mobicents.media.server.impl.events.connection.parameters.ConnectionParametersGenerator;
import org.mobicents.media.server.local.management.EndpointLocalManagement;
import org.mobicents.media.server.spi.events.pkg.ConnectionParameters;

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

    @Override
    public HashMap initMediaSources() {
    	
    	HashMap map = new HashMap();
		// init audio player
		map.put(Generator.CONNECTION_PARAMETERS, new ConnectionParametersGenerator());
		return map;
    }

    @Override
    public HashMap initMediaSinks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EndpointLocalManagement[] getEndpoints() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getEndpointNames() {
        // TODO Auto-generated method stub
        return null;
    }

	public String[] getSupportedPackages() {		
		return new String[]{ConnectionParameters.PACKAGE_NAME};
	}
}
