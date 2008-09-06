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

import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class AbstractSource implements MediaSource {

    protected MediaSink sink;

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
        this.sink = null;
        ((AbstractSink) sink).mediaStream = null;
    }
}
