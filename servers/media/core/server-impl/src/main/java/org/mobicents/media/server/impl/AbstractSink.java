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

import java.util.ArrayList;
import java.util.List;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent1;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class AbstractSink implements MediaSink {

    protected MediaSource mediaStream;
    private List<NotificationListener> listeners = new ArrayList();

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.MediaSink#connect(MediaStream).
     */
    public void connect(MediaSource mediaStream) {
        this.mediaStream = mediaStream;
        if (((AbstractSource) mediaStream).sink == null) {
            mediaStream.connect(this);
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.MediaSink#disconnect(MediaStream).
     */
    public void disconnect(MediaSource mediaStream) {
        this.mediaStream = null;
        ((AbstractSource) mediaStream).sink = null;
    }
    
    public void addListener(NotificationListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(NotificationListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
    
    protected void sendEvent(NotifyEvent1 evt) {
        synchronized (listeners) {
            for (NotificationListener listener : listeners) {
                listener.update(evt);
            }
        }
        listeners.clear();
    }
    
    public void dispose() {
        synchronized(listeners) {
            listeners.clear();
        }
    }
    
}
