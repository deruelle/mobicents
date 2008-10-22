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
package org.mobicents.media.server.impl.events.dtmf;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;

/**
 *
 * @author Oleg Kulikov
 */
public class Rfc2833Detector extends AbstractSink {

    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");
    private final static Format[] FORMATS = new Format[] {DTMF};
    
    private final static String[] TONE = new String[]{
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#"
    };
    private BaseDtmfDetector detector;
    private Logger logger = Logger.getLogger(Rfc2833Detector.class);

    public Rfc2833Detector(BaseDtmfDetector detector) {
    	super("Rfc2833Detector");
        this.detector = detector;
    }

    public void start() {
    }

    public void stop() {
    }

    public void receive(Buffer buffer) {
        byte[] data = (byte[]) buffer.getData();

        String digit = TONE[data[0]];
        boolean end = (data[1] & 0x7f) != 0;

        detector.digitBuffer.push(digit);
    }

    @Override
    public void disconnect(MediaSource source) {
        super.disconnect(source);
    }
    public Format[] getFormats() {
        return FORMATS;
    }

    public boolean isAcceptable(Format format) {
        return DTMF.matches(format);
    }
}
