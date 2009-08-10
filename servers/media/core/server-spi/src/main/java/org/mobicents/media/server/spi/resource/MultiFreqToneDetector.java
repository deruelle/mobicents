package org.mobicents.media.server.spi.resource;

import org.mobicents.media.MediaSink;

/**
 * 
 * @author amit.bhayani
 *
 */
public interface MultiFreqToneDetector extends MediaSink {
	
    /**
     * Describes the power level of the tone, expressed in dBm0
     * 
     * @param level the value in dBm0
     */
    public void setVolume(int level);

    /**
     * Describes the power level of the tone, expressed in dBm0
     * 
     * @return the value in dBm0
     */
    public int getVolume();
    
    public FrequencyBean getFreqBean();
    
    public void setFreqBean(FrequencyBean freqBean);

}
