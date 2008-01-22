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

package org.mobicents.media.server.impl.test.audio;

import java.io.IOException;
import java.util.Timer;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public class MeanderGenerator extends PushBufferDataSource {
    
    private MeanderStream stream;
    
    public MeanderGenerator(int duration, Timer timer) {
        stream = new MeanderStream(duration, timer);
    }
    
    @Override
    public PushBufferStream[] getStreams() {
        return new PushBufferStream[]{stream};
    }

    @Override
    public String getContentType() {
        return ContentDescriptor.RAW;
    }

    @Override
    public void start() throws IOException {
        stream.start();
    }

    @Override
    public void stop() throws IOException {
        stream.stop();
    }

    @Override
    public Object getControl(String ctrl) {
        return null;
    }

    @Override
    public Object[] getControls() {
        return new Object[0];
    }

    @Override
    public Time getDuration() {
        return MeanderGenerator.DURATION_UNBOUNDED;
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public void disconnect() {
    }

}
