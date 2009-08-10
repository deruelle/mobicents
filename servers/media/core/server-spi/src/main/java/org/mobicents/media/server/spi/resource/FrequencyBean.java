package org.mobicents.media.server.spi.resource;

/**
 * 
 * @author amit.bhayani
 *
 */
public class FrequencyBean {

	int lowFreq;
	int highFreq;
	int duration;

	public FrequencyBean(int lowFreq, int highFreq, int duration) {
		this.lowFreq = lowFreq;
		this.highFreq = highFreq;
		this.duration = duration;
	}

	public int getLowFreq() {
		return lowFreq;
	}

	public int getHighFreq() {
		return highFreq;
	}

	public int getDuration() {
		return duration;
	}
}
