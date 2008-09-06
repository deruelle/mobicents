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
public abstract class AbstractSink implements MediaSink {

    protected MediaSource mediaStream;

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
}
