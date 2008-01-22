/*
 * RawDataSource.java
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

package org.mobicents.media.processor;

import EDU.oswego.cs.dl.util.concurrent.Semaphore;
import java.io.IOException;
import javax.media.Time;
import javax.media.Track;
import javax.media.control.TrackControl;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.protocol.SourceStream;

/**
 *
 * @author Oleg Kulikov
 */
public class RawDataSource extends PushBufferDataSource {
    
    private RawPushBufferStream[] streams;
    private boolean stopped = false;
    private Track[] tracks;
    private SourceStream[] sourceStreams;
    
    private Thread runThread;
    private MediaProcessor dsp;
    
    private Semaphore semaphore = new Semaphore(0);
    private TrackControl[] trackControls;
    
    /** Creates a new instance of RawDataSource */
    public RawDataSource(MediaProcessor dsp, Track[] tracks, TrackControl[] trackControls) {
        this.dsp = dsp;
        this.tracks = tracks;
        this.trackControls = trackControls;
    }

    public RawDataSource(MediaProcessor dsp, SourceStream[] sourceStreams) {
        this.dsp = dsp;
        this.sourceStreams = sourceStreams;
    }
    
    public PushBufferStream[] getStreams() {
        return streams;
    }

    public String getContentType() {
        return "RAW";
    }

    public void connect() throws IOException {
        streams = new RawPushBufferStream[tracks.length];
        for (int i = 0; i < streams.length; i++) {
            streams[i] = new RawPushBufferStream(this, tracks[i], trackControls[i]);
        }
    }

    public void disconnect() {
    }

    public void start() throws IOException {
        for (int i = 0; i < streams.length; i++) {
            streams[i].start(true);
        }
    }

    public void stop() throws IOException {
        for (int i = 0; i < streams.length; i++) {
            //streams[i].stop();
        }
    }

    public Object getControl(String ctrl) {
        return ctrl;
    }

    public Object[] getControls() {
        return null;
    }

    public Time getDuration() {
        return this.DURATION_UNKNOWN;
    }
    
    protected void releaseSemaphore() {
        semaphore.release();
    }
}
