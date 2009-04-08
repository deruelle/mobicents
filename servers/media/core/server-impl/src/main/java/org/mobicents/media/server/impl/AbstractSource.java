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

import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;
import org.jboss.util.id.UID;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class AbstractSource extends AbstractWorkDataGatherer implements MediaSource {

    protected transient MediaSink sink;
    private List<NotificationListener> listeners = new CopyOnWriteArrayList();
    private String id = null;
    private String name = null;

    public AbstractSource(String name) {
        this.id = (new UID()).toString();
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.MediaStream#connect(MediaSink).
     */
    public void connect(MediaSink sink) {
        this.sink = sink;
        if (((AbstractSink) sink).mediaStream == null) {
            sink.connect(this);
        }
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.MediaStream#diconnection(MediaSink).
     */
    public void disconnect(MediaSink sink) {
        if (this.sink != null) {
            this.sink = null;
            ((AbstractSink) sink).mediaStream = null;
            sink.disconnect(this);
        }
    }

    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean equals(Object other) {
        return other == this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    protected void sendEvent(NotifyEvent evt) {
        for (NotificationListener listener : listeners) {
            listener.update(evt);
        }
    }

    public void dispose() {
        listeners.clear();
        if (this.sink != null) {
            ((AbstractSink) sink).mediaStream = null;
        }
    }

    @Override
    public String toString() {
        return (new StringBuffer().append(this.name).append(" - ").append(this.id)).toString();

    }
}
