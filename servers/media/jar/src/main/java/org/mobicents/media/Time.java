package org.mobicents.media;

public class Time implements java.io.Serializable {
	public static final long ONE_SECOND = 1000000000;

	public static final Time TIME_UNKNOWN = new Time(Long.MAX_VALUE - 1);

	private static final double NANO_TO_SEC = 1E-9;

	protected long nanoseconds;

	public Time(long nanoseconds) {
		this.nanoseconds = nanoseconds;
	}

	public Time(double seconds) {
		this.nanoseconds = secondsToNanoseconds(seconds);
	}

	protected long secondsToNanoseconds(double seconds) {
		return (long) (seconds * ONE_SECOND);
	}

	public long getNanoseconds() {
		return nanoseconds;
	}

	public double getSeconds() {
		return nanoseconds * NANO_TO_SEC;
	}

	static { // for Serializable compatibility.
	}
}