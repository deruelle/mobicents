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
package org.mobicents.media.server.impl.rtp.sdp;

import java.util.HashMap;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.VideoFormat;

/**
 * Defines relation between audio/video format and RTP payload number as
 * specified by Audio/Video Profile spec.
 * 
 * @author Oleg Kulikov
 */
public abstract class AVProfile {

    public final static String AUDIO = "audio";
    public final static String VIDEO = "video";
    
    public final static RTPAudioFormat PCMU = new RTPAudioFormat(0, AudioFormat.ULAW, 8000, 8, 1);
    public final static RTPAudioFormat PCMA = new RTPAudioFormat(8, AudioFormat.ALAW, 8000, 8, 1);
    public final static RTPAudioFormat SPEEX = new RTPAudioFormat(97, AudioFormat.SPEEX, 8000, 8, 1);
    public final static RTPAudioFormat G729 = new RTPAudioFormat(18, AudioFormat.G729, 8000, 8, 1);
    public final static RTPAudioFormat GSM = new RTPAudioFormat(3, AudioFormat.GSM, 8000, 8, 1);
    
    public final static RTPVideoFormat H261 = new RTPVideoFormat(3, VideoFormat.H261, 90000);
    
    private final static HashMap<Integer, RTPFormat> audioFormats = new HashMap();
    private final static HashMap<Integer, RTPFormat> videoFormats = new HashMap();
    
    static {
        audioFormats.put(PCMU.getPayloadType(), PCMU);
        audioFormats.put(PCMA.getPayloadType(), PCMA);
        audioFormats.put(SPEEX.getPayloadType(), SPEEX);
        audioFormats.put(G729.getPayloadType(), G729);
        audioFormats.put(GSM.getPayloadType(), GSM);
    }

    static {
        videoFormats.put(H261.getPayloadType(), H261);
    }
    
    /**
     * Gets the audio format related to payload type.
     * 
     * @param pt the payload type
     * @return AudioFormat object.
     */
    public static RTPAudioFormat getAudioFormat(int pt) {
        return (RTPAudioFormat) audioFormats.get(pt);
    }

    /**
     * Gets the video format related to payload type.
     * 
     * @param pt the payload type
     * @return VideoFormat object.
     */
    public static RTPVideoFormat getVideoFormat(int pt) {
        return (RTPVideoFormat) videoFormats.get(pt);
    }
    
}
