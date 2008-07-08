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

import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;

/**
 * 
 * @author Oleg Kulikov
 */
public interface Codec {
	public final static AudioFormat LINEAR_AUDIO = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
			AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
	public final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
	public final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);

	public final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);

	public Format[] getSupportedInputFormats();

	public Format[] getSupportedOutputFormats(Format fmt);

	public byte[] process(byte[] media);
}
