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
package org.mobicents.media.server.impl.enp.test;

import java.io.Serializable;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author Oleg Kulikov
 */
public class Echo implements MediaSink, MediaSource, Serializable {

    private LoopEndpointImpl endpoint;

    public Echo(Endpoint endpoint) {
        this.endpoint = (LoopEndpointImpl) endpoint;
       // this.mediaProxy = new MediaPushProxy(Codec.LINEAR_AUDIO);
    }

    public void release() {
       // mediaProxy.stop();
    }

    public void start() {
//        mediaProxy.start();
    }

    public void stop() {
//        mediaProxy.stop();
    }

    public void receive(Buffer buffer) {
   //     mediaProxy.receive(buffer);
    }

    public Format getFormat() {
        return null;//        return mediaProxy.getFormat();
    }

    public void setTransferHandler(MediaSink handler) {
        //mediaProxy.setTransferHandler(handler);
    }

    public void connect(MediaSource source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void disconnect(MediaSource source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void connect(MediaSink sink) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void disconnect(MediaSink sink) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Format[] getFormats() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isAcceptable(Format format) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
