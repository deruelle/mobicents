/*
 * Handler.java
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
import javax.media.EndOfMediaEvent;
import javax.media.IncompatibleSourceException;
import javax.media.NotConfiguredError;
import javax.media.Processor;
import javax.media.Track;
import javax.media.control.TrackControl;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import org.mobicents.media.processor.MediaProcessor;

/**
 *
 * @author Oleg Kulikov
 */
public class Handler extends MediaProcessor {
    
    //wav demultiplexer
    private WavParser wavParser;
    private Track[] tracks;
    
    private TrackControl[] fmtControls;
    
    /** Creates a new instance of Handler */
    public Handler() {
        wavParser = new WavParser();
    }
        
    public ContentDescriptor[] getSupportedContentDescriptors() throws NotConfiguredError {
        return new ContentDescriptor[]{new ContentDescriptor("audio.x_wav")};
    }
        
    public void setSource(DataSource dataSource) throws IOException, IncompatibleSourceException {
        wavParser.setSource(dataSource);
        try {
            tracks = wavParser.getTracks();
        } catch (BadHeaderException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    
    protected void endOfMedia() {
        sendEvent(new EndOfMediaEvent(this,
                Processor.Started, Processor.Started, Processor.Realized,
                wavParser.getMediaTime()));
    }

    public TrackControl[] doConfigure() {
        TrackControl[] trackControls = new TrackControl[tracks.length];
        for (int i = 0; i < trackControls.length; i++) {
            trackControls[i] = new WavFormatControl(tracks[i]);
        }
        return trackControls;
    }

    public Track[] doRealize() {
        return tracks;
    }
    
}

