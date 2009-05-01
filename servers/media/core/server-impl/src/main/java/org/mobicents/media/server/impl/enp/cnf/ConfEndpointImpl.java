/*
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
package org.mobicents.media.server.impl.enp.cnf;

import java.util.ArrayList;

import org.mobicents.media.Component;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;

import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.MediaResource;

import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.impl.rtp.RtpSocket;

import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ResourceUnavailableException;

import org.apache.log4j.Logger;
import org.mobicents.media.server.spi.Timer;

/**
 * 
 * @author Oleg Kulikov
 */
public class ConfEndpointImpl extends BaseEndpoint {

    private final static Format FORMATS[] = new Format[]{LINEAR_AUDIO, PCMA, PCMU, SPEEX, GSM, G729, DTMF};
    private ArrayList<RtpSocket> sockets = new ArrayList();
    private ArrayList<RxChannel> rxChannels = new ArrayList();
    private ArrayList<TxChannel> txChannels = new ArrayList();
    private transient Logger logger = Logger.getLogger(ConfEndpointImpl.class);

    public ConfEndpointImpl(String localName) {
        super(localName);
        this.setMaxConnectionsAvailable(5);
    }

    @Override
    public void start() throws ResourceUnavailableException {
        super.start();

        try {
            RtpFactory rtpFactory = getRtpFactory();
            for (int i = 0; i < this.getMaxConnectionsAvailable(); i++) {
                sockets.add(rtpFactory.getRTPSocket());
            }
        } catch (Exception e) {
            throw new ResourceUnavailableException(e.getMessage());
        }

        for (int i = 0; i < this.getMaxConnectionsAvailable(); i++) {
            rxChannels.add(new RxChannel(i));
            txChannels.add(new TxChannel(this, i));
        }
    }

    @Override
    public void stop() {
        for (int i = 0; i < this.getMaxConnectionsAvailable(); i++) {
            sockets.get(i).close();
            rxChannels.get(i).close();
            txChannels.get(i).close();
        }

        sockets.clear();

        rxChannels.clear();
        txChannels.clear();
        
        super.stop();
    }

    @Override
    public Format[] getFormats() {
        return FORMATS;
    }


    @Override
    public MediaSink getPrimarySink(Connection connection) {
        int index = ((BaseConnection) connection).getIndex();
        return rxChannels.get(index).getInput();
    }

    @Override
    public MediaSource getPrimarySource(Connection connection) {
        int index = ((BaseConnection) connection).getIndex();
        return txChannels.get(index).getSource();
    }

    @Override
    public void allocateMediaSources(Connection connection, Format[] formats) {
        lock.lock();
        try {
            TxChannel txChannel = txChannels.get(((BaseConnection) connection).getIndex());
            txChannel.configure(FORMATS, formats);
            txChannel.addListener((BaseConnection) connection);
            txChannel.start();
            for (RxChannel rxChannel : rxChannels) {
                if (rxChannel.isActive() && txChannel.getIndex() != rxChannel.getIndex()) {
                    txChannel.attach(rxChannel);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void allocateMediaSinks(Connection connection) {
        lock.lock();
        try {
            RxChannel rxChannel = rxChannels.get(((BaseConnection) connection).getIndex());
            rxChannel.addListener((BaseConnection) connection);
            rxChannel.start();
            for (TxChannel txChannel : txChannels) {
                if (txChannel.isActive() && rxChannel.getIndex() != txChannel.getIndex()) {
                    txChannel.attach(rxChannel);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected MediaSource getMediaSource(MediaResource id, Connection connection) {
        lock.lock();
        try {
            int index = ((BaseConnection) connection).getIndex();
            return txChannels.get(index).getMediaSource(id);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected MediaSink getMediaSink(MediaResource id, Connection connection) {
        lock.lock();
        try {
            int index = ((BaseConnection) connection).getIndex();
            return rxChannels.get(index).getMediaSink(id);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void releaseMediaSources(Connection connection) {
        lock.lock();
        try {
            TxChannel txChannel = txChannels.get(((BaseConnection) connection).getIndex());
            txChannel.stop();
            txChannel.removeListener((BaseConnection) connection);
            for (RxChannel rxChannel : rxChannels) {
                txChannel.deattach(rxChannel);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void releaseMediaSinks(Connection connection) {
        lock.lock();
        try {
            RxChannel rxChannel = rxChannels.get(((BaseConnection) connection).getIndex());
            rxChannel.stop();
            rxChannel.removeListener((BaseConnection) connection);
            for (TxChannel txChannel : txChannels) {
                txChannel.deattach(rxChannel);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public RtpSocket allocateRtpSocket(Connection connection) throws ResourceUnavailableException {
        lock.lock();
        try {
            int index = ((BaseConnection) connection).getIndex();
            return sockets.get(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deallocateRtpSocket(RtpSocket rtpSocket, Connection connection) {
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
