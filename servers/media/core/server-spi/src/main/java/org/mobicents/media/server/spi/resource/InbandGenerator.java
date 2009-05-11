package org.mobicents.media.server.spi.resource;

import org.mobicents.media.MediaSource;

/**
 * 
 * @author amit bhayani
 *
 */
public interface InbandGenerator extends MediaSource {
	
	public void setDigit(String digit);
	public String getDigit();
	
	public void setDuraion(int duration);
	public int getDuration();
	
	

}
