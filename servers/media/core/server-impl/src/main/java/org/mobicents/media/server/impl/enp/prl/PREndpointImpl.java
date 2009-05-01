/*
 * PREndpointImpl.java
 *
 * Mobicents Media Gateway
 *
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
package org.mobicents.media.server.impl.enp.prl;

import org.mobicents.media.Component;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.resource.Demultiplexer;
import org.mobicents.media.server.impl.MediaResource;
import org.mobicents.media.server.impl.resource.Multiplexer;
import org.mobicents.media.server.impl.dsp.Processor;
import org.mobicents.media.server.impl.events.dtmf.DTMFMode;
import org.mobicents.media.server.impl.events.dtmf.DtmfDetector;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.Timer;

/**
 *
 * @author Oleg Kulikov
 */
public class PREndpointImpl extends BaseEndpoint implements ConnectionListener {

    private final static Format FORMATS[] = new Format[]{LINEAR_AUDIO, PCMA, PCMU, SPEEX, G729, GSM, DTMF};
    private Processor[] dsp = new Processor[2];
    private RtpSocket sockets[] = new RtpSocket[2];
    private Multiplexer[] mux = new Multiplexer[2];
    private Demultiplexer[] demux = new Demultiplexer[2];
    private DtmfDetector[] dtmfDetector = new DtmfDetector[2];
    private DTMFMode dtmfMode = DTMFMode.AUTO;

    /**
     * Creates a new instance of PREndpointImpl
     * @param endpointsMap 
     */
    public PREndpointImpl(String localName) {
        super(localName);
        this.setMaxConnectionsAvailable(2);
        addConnectionListener(this);
    }

    @Override
    public void start() throws ResourceUnavailableException {
        super.start();

        try {
            RtpFactory rtpFactory = this.getRtpFactory();
            sockets[0] = rtpFactory.getRTPSocket();
            sockets[1] = rtpFactory.getRTPSocket();
        } catch (Exception e) {
            throw new ResourceUnavailableException(e.getMessage());
        }

        dsp[0] = new Processor(getLocalName());
        dsp[1] = new Processor(getLocalName());

        mux[0] = new Multiplexer("");
        mux[1] = new Multiplexer("");

        demux[0] = new Demultiplexer("Demultiplexer[0] "+this.getLocalName());
        demux[1] = new Demultiplexer("Demultiplexer[1] "+this.getLocalName());

        dtmfDetector[0] = new DtmfDetector("0");
        dtmfDetector[1] = new DtmfDetector("1");

        dtmfDetector[0].connect(demux[0]);
        demux[0].connect(dsp[0].getInput());
        dsp[0].getOutput().connect(mux[1]);

        dtmfDetector[1].connect(demux[1]);
        demux[1].connect(dsp[1].getInput());
        dsp[1].getOutput().connect(mux[0]);

        dsp[0].getOutput().start();
        dsp[1].getOutput().start();
        demux[0].start();
        demux[1].start();
    }

    @Override
    public void stop() {
        sockets[0].close();
        sockets[1].close();
        super.stop();
    }

    @Override
    public Format[] getFormats() {
        return FORMATS;
    }

    @Override
    public MediaSink getPrimarySink(Connection connection) {
        return demux[((BaseConnection) connection).getIndex()].getInput();
    }

    @Override
    public MediaSource getPrimarySource(Connection connection) {
        return mux[((BaseConnection) connection).getIndex()].getOutput();
    }

    @Override
    public RtpSocket allocateRtpSocket(Connection connection) throws ResourceUnavailableException {
        lock.lock();
        try {
            return sockets[((BaseConnection) connection).getIndex()];
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deallocateRtpSocket(RtpSocket rtpSocket, Connection connection) {
    }

    @Override
    public void allocateMediaSources(Connection connection, Format[] formats) {
        lock.lock();
        try {
            mux[((BaseConnection) connection).getIndex()].getOutput().start();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void allocateMediaSinks(Connection connection) {
        lock.lock();
        try {
            demux[((BaseConnection) connection).getIndex()].start();
            dtmfDetector[((BaseConnection) connection).getIndex()].addListener((BaseConnection) connection);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void releaseMediaSources(Connection connection) {
        lock.lock();
        try {
            mux[((BaseConnection) connection).getIndex()].getOutput().stop();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void releaseMediaSinks(Connection connection) {
        lock.lock();
        try {
            demux[((BaseConnection) connection).getIndex()].stop();
            dtmfDetector[((BaseConnection) connection).getIndex()].removeListener((BaseConnection) connection);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected MediaSource getMediaSource(MediaResource id, Connection connection) {
        return null;
    }

    @Override
    protected MediaSink getMediaSink(MediaResource id, Connection connection) {
        if (id == MediaResource.DTMF_DETECTOR) {
            return dtmfDetector[((BaseConnection)connection).getIndex()];
        }
        return null;
    }

    public String[] getSupportedPackages() {
        return new String[]{};
    }

    private boolean isAllConnected() {
        for (Connection connection : getConnections()) {
            if (connection.getState() != ConnectionState.OPEN) {
                return false;
            }
        }
        return true;
    }
    
    public void onStateChange(Connection connection, ConnectionState oldState) {
        if (getConnections().size() == 2 && isAllConnected()) {            
            Format[] fi = demux[0].getFormats();
            Format[] fo = mux[1].getFormats();
            
            if (fi != null && fo != null) {
                dsp[0].configure(fi, fo);
            } 

            fi = demux[1].getFormats();
            fo = mux[0].getFormats();
            if (fi != null && fo != null) {
                dsp[1].configure(fi, fo);
            } 
        }
    }

    public void onModeChange(Connection connection, ConnectionMode oldMode) {
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
