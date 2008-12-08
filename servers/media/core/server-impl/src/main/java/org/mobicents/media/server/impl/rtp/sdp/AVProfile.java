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

import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class AVProfile {
	public final static String AUDIO = "audio";
	public final static String VIDEO = "video";

	public final static RTPAudioFormat PCMU = new RTPAudioFormat(0, AudioFormat.ULAW, 8000, 8, 1);
	public final static RTPAudioFormat GSM = new RTPAudioFormat(3, AudioFormat.GSM, 8000, 8, 1);
	public final static RTPAudioFormat PCMA = new RTPAudioFormat(8, AudioFormat.ALAW, 8000, 8, 1);
	public final static RTPAudioFormat SPEEX_NB = new RTPAudioFormat(97, AudioFormat.SPEEX, 8000, 8, 1);
	public final static RTPAudioFormat G729 = new RTPAudioFormat(18, AudioFormat.G729, 8000, 8, 1);
	

	// public final static RTPAudioFormat DTMF_FORMAT = new
	// DtmfFormat(DTMF.RTP_PAYLOAD, "telephone-event");

	public static RTPAudioFormat getAudioFormat(int pt) {
		switch (pt) {
		case 0:
			return PCMU;
		case 3:
			return GSM;
		case 8:
			return PCMA;
		case 18:
			return G729;
		case 97:
			return SPEEX_NB;
		default:
			return null;
		}
	}

	public static int getPayload(Format fmt) {
		if (fmt.matches(PCMU)) {
			return 0;
		} else if (fmt.matches(GSM)) {
			return 3;
		} else if (fmt.matches(PCMA)) {
			return 8;
		} else if (fmt.matches(G729)) {
			return 18;
		} else if (fmt.matches(SPEEX_NB)) {
			return 97;
		} else {
			return -1;
		}
	}
}
