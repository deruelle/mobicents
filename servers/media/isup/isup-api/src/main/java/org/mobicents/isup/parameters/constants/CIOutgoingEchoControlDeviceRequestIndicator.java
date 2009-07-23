/**
 * Start time:15:22:25 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:22:25 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CIOutgoingEchoControlDeviceRequestIndicator {
	/**
	 * See Q.763 3.19 Incoming echo control device request indicator : no
	 * information
	 */
	public static final int _NOINFO = 0;

	/**
	 * See Q.763 3.19 Incoming echo control device request indicator : incoming
	 * echo control device activation request
	 */
	public static final int _AR = 1;

	/**
	 * See Q.763 3.19 Incoming echo control device request indicator : incoming
	 * echo control device deactivation request (Note 2)
	 */
	public static final int _DR = 2;

}
