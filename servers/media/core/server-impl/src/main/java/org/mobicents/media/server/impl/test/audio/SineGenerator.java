/*
 * Generator.java
 *
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
import org.mobicents.media.Time;
import org.mobicents.media.protocol.PushBufferDataSource;
import org.mobicents.media.protocol.PushBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public class SineGenerator extends PushBufferDataSource {

    private SineStream[] streams;

    /** Creates a new instance of Generator */
    public SineGenerator(int duration, int[] freqs, boolean terminateAfterSequence) {
        streams = new SineStream[freqs.length];
        for (int i = 0; i < freqs.length; i++) {
            streams[i] = new SineStream(freqs[i], duration,terminateAfterSequence);
        }
    }

    public PushBufferStream[] getStreams() {
        return streams;
    }

    public String getContentType() {
        return "RAW";
    }

    public void connect() throws IOException {
    }

    public void disconnect() {
    }

    public void start() throws IOException {
        for (int i = 0; i < streams.length; i++) {
            streams[i].start();
        }
    }

    public void stop() throws IOException {
        for (int i = 0; i < streams.length; i++) {
            streams[i].stop();
        }
    }

    public Object getControl(String ctrlName) {
        return null;
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Time getDuration() {
        return SineGenerator.DURATION_UNKNOWN;
    }
}
