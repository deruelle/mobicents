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

package org.mobicents.media.processor.audio.x_wav;

import java.awt.Component;
import javax.media.Codec;
import javax.media.Format;
import javax.media.NotConfiguredError;
import javax.media.Renderer;
import javax.media.Track;
import javax.media.UnsupportedPlugInException;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;

/**
 *
 * @author Oleg Kulikov
 */
public class WavFormatControl implements TrackControl {
    
    private AudioFormat fmt;
    private Track track;
    
    private Format[] supportedFormats = new Format[] {
        new AudioFormat(AudioFormat.LINEAR),
        new AudioFormat(AudioFormat.ULAW),
    };
    
    /** Creates a new instance of WavFormatControl */
    public WavFormatControl(Track track) {
        this.track = track;
    }

    public Format getFormat() {
        return fmt;
    }

    public Format setFormat(Format format) {
        fmt = (AudioFormat) format;
        return format;
    }

    public Format[] getSupportedFormats() {
        return supportedFormats;
    }

    public boolean isEnabled() {
        return track.isEnabled();
    }

    public void setEnabled(boolean enabled) {
        track.setEnabled(enabled);
    }

    public Component getControlComponent() {
        return null;
    }

    public void setCodecChain(Codec[] codec) throws UnsupportedPlugInException, NotConfiguredError {
    }

    public void setRenderer(Renderer renderer) throws UnsupportedPlugInException, NotConfiguredError {
    }

    public Object[] getControls() {
        return null;
    }

    public Object getControl(String string) {
        return null;
    }
    
}
