/*
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.enp.test;

import org.mobicents.media.Component;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.resource.Demultiplexer;
import org.mobicents.media.server.impl.MediaResource;
import org.mobicents.media.server.impl.resource.Multiplexer;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author Oleg Kulikov
 */
public class LoopEndpointImpl extends BaseEndpoint {

    private final static Format FORMATS[] = new Format[] {LINEAR_AUDIO, PCMA, PCMU, SPEEX, G729, DTMF, GSM};
    
    private Multiplexer mux;
    private Demultiplexer demux;
    private RtpSocket rtpSocket;

    public LoopEndpointImpl(String localName) {
        super(localName);
        
        this.setMaxConnectionsAvailable(1);
        mux = new Multiplexer("");
        demux = new Demultiplexer("Demultiplexer "+this.getLocalName());
    }

    @Override
    public void start() throws ResourceUnavailableException{
    	
		super.start();
        demux.connect(mux);
        try {
            RtpFactory rtpFactory = getRtpFactory();
            rtpSocket = rtpFactory.getRTPSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void stop() {
        //disconnecting components
        demux.disconnect(mux);
        
        //dispose components
        demux.dispose();
        mux.dispose();

        //close sockets
        if (rtpSocket != null) {
            rtpSocket.close();
        }
        super.stop();
    }

    @Override
    public Format[] getFormats() {
        return FORMATS;
    }
    
    @Override
    public int getConnectionsCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getCreationTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getGatherPerformanceFlag() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public long getNumberOfBytes() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getPacketsCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setGatherPerformanceFlag(boolean flag) {
        // TODO Auto-generated method stub
    }

    public String[] getSupportedPackages() {
        return new String[]{};
    }

    public void onModeChange(Connection connection, ConnectionMode oldMode) {
    }

    @Override
    public MediaSink getPrimarySink(Connection connection) {
        return demux.getInput();
    }

    @Override
    public MediaSource getPrimarySource(Connection connection) {
        return mux.getOutput();
    }

    @Override
    public void allocateMediaSources(Connection connection, Format[] formats) {
        mux.getOutput().start();
    }

    @Override
    public void allocateMediaSinks(Connection connection) {
        demux.start();
    }

    @Override
    protected MediaSource getMediaSource(MediaResource id, Connection connection) {
        return null;
    }

    @Override
    protected MediaSink getMediaSink(MediaResource id, Connection connection) {
        return null;
    }

    @Override
    public void releaseMediaSources(Connection connection) {
        mux.getOutput().stop();
    }

    @Override
    public void releaseMediaSinks(Connection connection) {
        demux.stop();
    }

    @Override
    public RtpSocket allocateRtpSocket(Connection connection) throws ResourceUnavailableException {
        return rtpSocket;
    }

    @Override
    public void deallocateRtpSocket(RtpSocket rtpSocket, Connection connection) {
    }

    public Component getComponent(int resourceID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
