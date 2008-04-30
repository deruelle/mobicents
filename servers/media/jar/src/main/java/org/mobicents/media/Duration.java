package org.mobicents.media;
/**
 * 
 * Standard JMF class -- see <a href="http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/Duration.html" target="_blank">this class in the JMF Javadoc</a>.
 * Complete.
 * @author Ken Larson
 *
 */
public interface Duration {

	public static Time DURATION_UNBOUNDED = new Time(Long.MAX_VALUE);
	public static Time DURATION_UNKNOWN = new Time(Long.MAX_VALUE - 1) ;
	
	public Time getDuration();
}
