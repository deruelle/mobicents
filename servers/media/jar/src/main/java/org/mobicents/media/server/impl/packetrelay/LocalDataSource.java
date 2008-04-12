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

package org.mobicents.media.server.impl.packetrelay;

import java.io.IOException;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public class LocalDataSource extends PushBufferDataSource {
    
    private PushBufferStream stream;
    
    /**
     * Creates a new instance of LocalDataSource
     */
    public LocalDataSource(PushBufferStream stream) {
        this.stream =stream;
    }

    public PushBufferStream[] getStreams() {
        return new PushBufferStream[] {stream};
    }

    public String getContentType() {
        return ContentDescriptor.RAW;
    }

    public void connect() throws IOException {
    }

    public void disconnect() {
    }

    public void start() throws IOException {
    }

    public void stop() throws IOException {
    }

    public Object getControl(String string) {
        return null;
    }

    public Object[] getControls() {
        return null;
    }

    public Time getDuration() {
        return DURATION_UNKNOWN;
    }
    
}
