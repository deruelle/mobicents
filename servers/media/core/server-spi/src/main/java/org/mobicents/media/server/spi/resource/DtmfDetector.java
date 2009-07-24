package org.mobicents.media.server.spi.resource;

import org.mobicents.media.MediaSink;

/**
 * 
 * @author amit bhayani
 * 
 */
public interface DtmfDetector extends MediaSink {

    /**
     * Default level of the DTMF tone in decibells.
     */
    public final static int DEFAULT_SIGNAL_LEVEL = -30;
    /**
     * Default interdigit time interval in millisconds.
     */
    public final static int DEFAULT_INTERDIGIT_INTERVAL = 500;
    
    
    /**
     * Specifies mask for dtmf sequence.
     * 
     * @return the mask assigned to detector as regular expression.
     */
    public String getMask();

    /**
     * Assign mask for DTMF sequence detection.
     * 
     * @param mask the regular expression string.
     */
    public void setMask(String mask);

    /**
     * The time the system will wait between DTMF digits. 
     * If this value is reached, the system fires dtmf event.
     * 
     * @param interval the time interval in millisconds.
     */
    public void setInterdigitInterval(int interval);

    /**
     * The time the system will wait between DTMF digits. 
     * If this value is reached, the system fires dtmf event.
     * 
     * @return  the time interval in millisconds.
     */
    public int getInterdigitInterval();

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
    
}
