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

import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.Demultiplexer;
import org.mobicents.media.server.impl.MediaResource;
import org.mobicents.media.server.impl.Multiplexer;
import org.mobicents.media.server.impl.dsp.Processor;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.ResourceUnavailableException;

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
            sockets[0] = rtpFactory.getRTPSocket(this);
            sockets[1] = rtpFactory.getRTPSocket(this);
        } catch (Exception e) {
            throw new ResourceUnavailableException(e.getMessage());
        }

        dsp[0] = new Processor(getLocalName());
        dsp[1] = new Processor(getLocalName());

        mux[0] = new Multiplexer();
        mux[1] = new Multiplexer();

        demux[0] = new Demultiplexer(FORMATS);
        demux[1] = new Demultiplexer(FORMATS);

        demux[0].connect(dsp[0].getInput());
        dsp[0].getOutput().connect(mux[1]);

        demux[1].connect(dsp[1].getInput());
        dsp[1].getOutput().connect(mux[0]);

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
}
