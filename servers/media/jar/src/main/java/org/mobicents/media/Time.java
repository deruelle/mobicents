package org.mobicents.media;
/**
 * Standard JMF class -- see <a href="http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/Time.html" target="_blank">this class in the JMF Javadoc</a>.
 * 
 * Complete.
 * @author Ken Larson
 *
 */
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