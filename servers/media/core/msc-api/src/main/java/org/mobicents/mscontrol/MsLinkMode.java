package org.mobicents.mscontrol;

/**
 * 
 * {@link MsLink} are either one way represented as <code>HALF_DUPLEX</code>
 * or two way represented as <code>FULL_DUPLEX</code>
 * <p>
 * In <code>HALF_DUPLEX</code>, the stream only flows from
 * <code>endpointA</code> to <code>endpointB</code> in given example but <b>not</b>
 * in reverse direction. </br>Example
 * </p>
 * <p>
 * <blockquote>
 * 
 * <pre>
 * MsLink msLink;
 * ...	 
 * msLink.join(enpointA, enpointB);
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author Oleg Kulikov
 */
public enum MsLinkMode {
	HALF_DUPLEX, FULL_DUPLEX;
}
