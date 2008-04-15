package org.mobicents.media;

public interface Duration {

	public static Time DURATION_UNBOUNDED = new Time(Long.MAX_VALUE);
	public static Time DURATION_UNKNOWN = new Time(Long.MAX_VALUE - 1) ;
	
	public Time getDuration();
}
