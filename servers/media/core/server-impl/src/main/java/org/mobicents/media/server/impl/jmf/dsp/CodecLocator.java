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

package org.mobicents.media.server.impl.jmf.dsp;

import java.util.ArrayList;
import java.util.List;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;

/**
 * 
 * @author Oleg Kulikov
 */
public class CodecLocator {
	private final static AudioFormat linearAudio = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
			AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
	private final static AudioFormat pcma = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
	private final static AudioFormat pcmu = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);

	private final static AudioFormat speex = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
	private final static String packagePrefix = "org.mobicents.media.server.impl.jmf.dsp";

	private static String getMedia(Format fmt) {
		if (fmt instanceof AudioFormat) {
			return "audio";
		}
		return "unknown";
	}

	private static List<Codec> codecs = new ArrayList();
	static {
		codecs.add(new org.mobicents.media.server.impl.jmf.dsp.audio.alaw.Encoder());
		codecs.add(new org.mobicents.media.server.impl.jmf.dsp.audio.alaw.Decoder());
		codecs.add(new org.mobicents.media.server.impl.jmf.dsp.audio.ulaw.Encoder());
		codecs.add(new org.mobicents.media.server.impl.jmf.dsp.audio.ulaw.Decoder());

		codecs.add(new org.mobicents.media.server.impl.jmf.dsp.audio.speex.Encoder());
		codecs.add(new org.mobicents.media.server.impl.jmf.dsp.audio.speex.Decoder());
	}

	private static boolean matches(Format[] supported, Format input) {
		for (int i = 0; i < supported.length; i++) {
			if (supported[i].matches(input)) {
				return true;
			}
		}
		return false;
	}

	public static synchronized Codec getCodec(Format inputFmt, Format outputFmt) {
		for (Codec codec : codecs) {
			if ((matches(codec.getSupportedInputFormats(), inputFmt))
					&& (matches(codec.getSupportedOutputFormats(inputFmt), outputFmt))) {
				Class cls = codec.getClass();
				try {
					return (Codec) cls.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
