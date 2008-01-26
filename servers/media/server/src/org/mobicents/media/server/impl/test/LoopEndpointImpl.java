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
package org.mobicents.media.server.impl.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.jmf.proxy.MediaPushProxy;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.UnknownSignalException;

/**
 *
 * @author Oleg Kulikov
 */
public class LoopEndpointImpl extends BaseEndpoint {

    private transient MediaPushProxy mediaProxy;
    private Format fmt = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);

    public LoopEndpointImpl(String localName) {
        super(localName);
        setMaxConnectionsAvailable(2);
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Endpoint#deleteConnection();
     */
    @Override
    public synchronized void deleteConnection(String connectionID) {
        try {
            mediaProxy.setInputStream(null);
        } catch (UnsupportedFormatException e) {
        } finally {
            super.deleteConnection(connectionID);
        }
    }

    @Override
    public void addAudioStream(PushBufferStream stream, String connectionID) {
        try {
            mediaProxy = new MediaPushProxy(stream.getFormat());
            mediaProxy.setPeriod(this.getPacketizationPeriod());
            mediaProxy.setInputStream(stream);
        } catch (UnsupportedFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<PushBufferStream> getAudioStreams(BaseConnection connection) {
        List list = new ArrayList();
        list.add(mediaProxy);
        return list;
    }

    public void play(int signalID, String[] params, String connectionID, NotificationListener listener, boolean keepAlive) throws UnknownSignalException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
