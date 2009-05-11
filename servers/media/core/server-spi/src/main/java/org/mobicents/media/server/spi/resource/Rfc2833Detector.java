package org.mobicents.media.server.spi.resource;

import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.format.AudioFormat;

/**
 * 
 * @author amit bhayani
 * 
 */
public interface Rfc2833Detector extends MediaSink {
	
	public final static String RFC2833_DETECTOR_MASK = "[0-9, A,B,C,D,*,#]";

	public final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");

	public final static Format[] FORMATS = new Format[] { DTMF };

	public final static String[] TONE = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#", "A",
			"B", "C", "D" };

}
