package org.mobicents.media.server.spi.resource;

import java.util.List;

import org.mobicents.media.MediaSource;

/**
 * 
 * @author amit.bhayani
 *
 */
public interface MultiFreqToneGenerator extends MediaSource {

	public void setVolume(int volume);

	public int getVolume();

	public void setFreqBeanList(List<FrequencyBean> freqBean);

	public List<FrequencyBean> getFreqBeanList();
}
