package org.mobicents.media.server.impl.enp.fft;

import java.io.File;

import java.util.Collection;
import org.mobicents.media.Component;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.MediaResource;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.local.management.EndpointLocalManagement;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.Timer;

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

    public EndpointLocalManagement[] getEndpoints() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getEndpointNames() {
        // TODO Auto-generated method stub
        return null;
    }

	public String[] getSupportedPackages() {		
		return new String[]{};
	}

    @Override
    public MediaSink getPrimarySink(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MediaSource getPrimarySource(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void allocateMediaSources(Connection connection, Format[] formats) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void allocateMediaSinks(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected MediaSource getMediaSource(MediaResource id, Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected MediaSink getMediaSink(MediaResource id, Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void releaseMediaSources(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void releaseMediaSinks(Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RtpSocket allocateRtpSocket(Connection connection) throws ResourceUnavailableException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deallocateRtpSocket(RtpSocket rtpSocket, Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Format[] getFormats() {
        return null;
    }

    public Component getComponent(int resourceID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Timer getTimer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTimer(Timer timer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
