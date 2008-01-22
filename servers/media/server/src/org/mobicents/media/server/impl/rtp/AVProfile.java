/*
 * AVProfile.java
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

package org.mobicents.media.server.impl.rtp;

import javax.media.format.AudioFormat;
import javax.sdp.SdpConstants;

/**
 * Creates Format objects for RTP media streams operating under the RTP 
 * Audio/Video Profile.  
 *
 * @author Oleg Kulikov
 */
public class AVProfile {
    public final static String AUDIO = "audio";
    public final static String VIDEO = "video";
    
    /** Creates a new instance of AVProfile */
    public AVProfile() {
    }
    
    /**
     * Creates audio format for specified payload sampled at 8kHz (8bit, mono).
     *
     * @param payload RTP payload type.
     */
    public synchronized static AudioFormat createAudioFormat(int payload) {
        switch (payload) {
            case SdpConstants.PCMA :
                return new RTPAudioFormat(payload, AudioFormat.ALAW, 8000, 8, 1);
            case SdpConstants.PCMU :
                return new RTPAudioFormat(payload, AudioFormat.ULAW, 8000, 8, 1);
            case SdpConstants.G723 :
                return new RTPAudioFormat(payload, AudioFormat.G723, 8000, 8, 1);
            case SdpConstants.GSM :
                return new RTPAudioFormat(payload, AudioFormat.GSM, 8000, 8, 1);
            case SdpConstants.G728 :
                return new RTPAudioFormat(payload, AudioFormat.G728, 8000, 8, 1);
            case SdpConstants.G729:
                return new RTPAudioFormat(payload, AudioFormat.G729, 8000, 8, 1);
            default : return null;
        }
    }
        
}
