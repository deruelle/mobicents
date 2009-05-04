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

package org.mobicents.media.server.spi.dsp;

import java.io.Serializable;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;

/**
 * 
 * @author Oleg Kulikov
 */
public interface Codec extends Serializable {

	public final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
	public final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
	public final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, AudioFormat.NOT_SPECIFIED, 1);
	public final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, AudioFormat.NOT_SPECIFIED, 1);
	public final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, AudioFormat.NOT_SPECIFIED, 1);
	public final static AudioFormat LINEAR_AUDIO = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
			AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

	public final static AudioFormat L16_MONO = new AudioFormat(AudioFormat.LINEAR, 44100, 16, 1,
			AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

	public final static AudioFormat L16_STEREO = new AudioFormat(AudioFormat.LINEAR, 44100, 16, 2,
			AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

	public Format getSupportedInputFormat();

	public Format getSupportedOutputFormat();

	/**
	 * Perform encoding/decoding procedure.
	 * 
	 * @param buffer
	 *            the media buffer.
	 */
	public void process(Buffer buffer);

}
