/*
 * WavPullBufferDataSource.java
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

package org.mobicents.media.processor.audio.x_wav;

import com.sun.media.parser.audio.WavParser;
import java.io.IOException;
import javax.media.BadHeaderException;
import javax.media.Time;
import javax.media.Track;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public class WavPullBufferDataSource extends PullBufferDataSource {
    
    private Track[] tracks;
    private WavPullBufferStream[] streams;
    private WavParser parser;
    private Handler handler;
    
    private boolean stopped = false;
    
    public WavPullBufferDataSource(Handler handler, WavParser parser) {
        this.handler = handler;
        this.parser = parser;
    }
    
    public PullBufferStream[] getStreams() {
        return streams;
    }
    
    public String getContentType() {
        return "raw";
    }
    
    public void connect() throws IOException {
        try {
            tracks = parser.getTracks();
            streams = new WavPullBufferStream[tracks.length];
            
            for (int i = 0; i < tracks.length; i++) {
                streams[i] = new WavPullBufferStream(handler, tracks[i], null);
            }
        } catch (BadHeaderException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void disconnect() {
    }
    
    public void start() throws IOException {
        parser.start();
    }
    
    public void stop() throws IOException {
        parser.stop();
    }
    
    public Object getControl(String string) {
        return null;
    }
    
    public Object[] getControls() {
        return null;
    }
    
    public Time getDuration() {
        return parser.getDuration();
    }
    
    private class RunThread implements Runnable {
        public void run() {
            while (!stopped) {
                for (int i = 0; i < streams.length; i++) {
                    
                }
            }
        }
    }
}
