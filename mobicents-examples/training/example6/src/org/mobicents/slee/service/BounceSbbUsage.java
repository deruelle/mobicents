package org.mobicents.slee.service;

public interface BounceSbbUsage {
	
	public abstract void incrementNumberOfAnyMessage(long value);
	
	public abstract void sampleTimeBetweenAnyMessages(long value);

}
