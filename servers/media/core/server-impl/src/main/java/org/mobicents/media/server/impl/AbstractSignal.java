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
package org.mobicents.media.server.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class AbstractSignal implements Serializable {

    private List<NotificationListener> listeners = new ArrayList();

    protected void sendEvent(NotifyEvent evt) {
        for (NotificationListener listener : listeners) {
            listener.update(evt);
        }
        listeners.clear();
    }

    public abstract void apply(BaseConnection connection);
    public abstract void apply(BaseEndpoint connection);

    public abstract void cancel();
    
    protected MediaSource getMediaSource(MediaResource id, Connection connection) {
        BaseEndpoint endpoint = (BaseEndpoint) connection.getEndpoint();
        return endpoint.getMediaSource(id, connection);
    }

    protected MediaSink getMediaSink(MediaResource id, Connection connection) {
        BaseEndpoint endpoint = (BaseEndpoint) connection.getEndpoint();
        return endpoint.getMediaSink(id, connection);
    }

    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }
}
