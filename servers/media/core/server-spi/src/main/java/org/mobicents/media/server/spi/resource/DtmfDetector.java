package org.mobicents.media.server.spi.resource;

import org.mobicents.media.MediaSink;

/**
 * 
 * @author amit bhayani
 * 
 */
public interface DtmfDetector extends MediaSink {

	public static final String DETECTOR_MASK = "[0-9, A,B,C,D,*,#]";

	public static final int DETECTOR_SILENCE = 500;

	public static final int DETECTOR_DURATION = 50;

	

	public final static String[] TONE = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#", "A",
			"B", "C", "D" };

	public String getMask();

	public void setMask(String mask);



}
