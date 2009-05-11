package org.mobicents.media.server.spi.resource;

import org.mobicents.media.MediaSink;

/**
 * 
 * @author amit bhayani
 *
 */
public interface InbandDetector extends MediaSink {
	
	public static final String INBAND_DETECTOR_MASK = "[0-9, A,B,C,D,*,#]";
	public static final int INBAND_DETECTOR_SILENCE = 500;
	public static final int INBAND_DETECTOR_DURATION = 50;
	
	public String getMask();
	public void setMask(String mask);
	
	public void setSilenec(int silence);
	public int getSilenec();
	
	public void setDuration(int duartion);
	public int getDuration();

}
