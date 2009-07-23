/**
 * Start time:15:50:34 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:50:34 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CSateliteIndicator {
	/**
	 * See Q.763 3.35 Satellite indicator : no satellite circuit in the
	 * connection
	 */
	public static final int _NO_SATELLITE = 0;

	/**
	 * See Q.763 3.35 Satellite indicator : one satellite circuit in the
	 * connection
	 */
	public static final int _ONE_SATELLITE = 1;

	/**
	 * See Q.763 3.35 Satellite indicator : two satellite circuits in the
	 * connection
	 */
	public static final int _TWO_SATELLITE = 2;

}
