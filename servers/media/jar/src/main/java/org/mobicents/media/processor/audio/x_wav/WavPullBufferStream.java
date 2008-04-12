/*
 * WavPullBufferStream.java
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

import com.ibm.media.codec.audio.ulaw.JavaDecoder;
import java.io.IOException;
import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.Track;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public class WavPullBufferStream implements PullBufferStream {
    
    private Handler handler;
    private Track track;
    private TrackControl trackControl;
    
    private double timestamp;
    private double duration;
    private boolean eom = false;
    
    private Codec[] codecChain;
    private Codec decompressor;
    private Codec compressor;
    
    /** Creates a new instance of WavPullBufferStream */
    public WavPullBufferStream(Handler handler, Track track, TrackControl trackControl) {
        this.handler = handler;
        this.track = track;
        this.trackControl = trackControl;
        this.duration = track.getDuration().getSeconds();
        
        Format inputFormat = track.getFormat();
        Format outputFormat = trackControl.getFormat();
        
        if (outputFormat == null) {
            decompressor = null;
            compressor = null;        
        } else if (inputFormat.matches(outputFormat)) {
            decompressor = null;
            compressor = null;        
        } else if (inputFormat.getEncoding().equalsIgnoreCase("ulaw")) {
            decompressor = new JavaDecoder();
        }
    }

    private boolean isTranscodingRequired(Track track, TrackControl trackControl) {
        Format inputFormat = track.getFormat();
        Format outputFormat = trackControl.getFormat();
        return !((outputFormat == null) || (inputFormat.matches(outputFormat)));
    }
    
    private Codec getDecompressor(Format fmt) {
        if (fmt.getEncoding().equalsIgnoreCase("ulaw")) {
            return new JavaDecoder();
        } else return null;
    }
    
    public boolean willReadBlock() {
        return false;
    }

    public void read(Buffer buffer) throws IOException {
        track.readFrame(buffer);

        AudioFormat fmt = (AudioFormat) trackControl.getFormat();
        
        timestamp = buffer.getTimeStamp() / 1000000000;
        if ((duration-timestamp) < 1 && !eom) {
            eom = true;
            buffer.setEOM(true);
            handler.endOfMedia();
        }
    }

    public Format getFormat() {
        return track.getFormat();
    }

    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor("audio.x_wav");
    }

    public long getContentLength() {
        return PullBufferStream.LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
        return false;
    }

    public Object[] getControls() {
        return null;
    }

    public Object getControl(String string) {
        return null;
    }
    
}
