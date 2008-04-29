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

package org.mobicents.media.server.impl.ann;

import java.util.Properties;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.BaseResource;
import org.mobicents.media.server.impl.common.MediaResourceState;
import org.mobicents.media.server.impl.jmf.proxy.MediaPushProxy;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaSource;
import org.mobicents.media.server.spi.NotificationListener;

/**
 *
 * @author Oleg Kulikov
 */
public class LocalProxy extends BaseResource implements MediaSource {

    private MediaPushProxy mediaProxy;
    private BaseEndpoint endpoint;
    private BaseConnection connection;
    
    public LocalProxy(Endpoint endpoint, Connection connection) {
        this.endpoint = (BaseEndpoint) endpoint;
        this.connection = (BaseConnection) connection;
    }
    
    public void setInputStream(PushBufferStream stream) throws UnsupportedFormatException {
        mediaProxy.setInputStream(stream);
    }
    
    public PushBufferStream prepare(Endpoint endpoint) {
        setState(MediaResourceState.PREPARED);
        return mediaProxy;
    }

    public void configure(Properties config) {
        mediaProxy = new MediaPushProxy(endpoint.getPacketizationPeriod(),
                connection.getAudioFormat());
        setState(MediaResourceState.CONFIGURED);
    }

    public void release() {
        mediaProxy.stop();
        setState(MediaResourceState.CONFIGURED);
    }

    public void addListener(NotificationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeListener(NotificationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void start() {
        mediaProxy.start();
        setState(MediaResourceState.STARTED);
    }

    public void stop() {
        mediaProxy.stop();
        setState(MediaResourceState.CONFIGURED);
    }

    public void add(String id, PushBufferStream stream) throws UnsupportedFormatException {
        mediaProxy.setInputStream(stream);
    }


}
