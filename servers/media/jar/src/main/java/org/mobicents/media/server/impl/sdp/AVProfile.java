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

package org.mobicents.media.server.impl.sdp;

import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.spi.dtmf.DTMF;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class AVProfile {
    public final static String AUDIO = "audio";
    public final static String VIDEO = "video";
    
    public final static RTPAudioFormat PCMU = new RTPAudioFormat(0, AudioFormat.ULAW, 8000, 8, 1);
    public final static RTPAudioFormat PCMA = new RTPAudioFormat(8, AudioFormat.ALAW, 8000, 8, 1);
    public final static RTPAudioFormat DTMF_FORMAT = 
            new DtmfFormat(DTMF.RTP_PAYLOAD, "telephone-event");
    
    public static RTPAudioFormat getAudioFormat(int pt) {
        switch (pt) {
            case 0 : return PCMU;
            case 8 : return PCMA;
            default : return null;
        }
    }
    
    public static int getPayload(Format fmt) {
        if (fmt.matches(PCMU)) {
            return 0;
        } else if (fmt.matches(PCMA)) {
            return 8;
        } else {
            return -1;
        }
    }
}
