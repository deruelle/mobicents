/**
 * Start time:15:21:39 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:21:39 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CIncomingEchoControlDeviceInformationIndicator {
	/**
	 * See Q.763 3.19 Incoming echo control device information indicator : no
	 * information
	 */
	public static final int _NOINFO = 0;

	/**
	 * See Q.763 3.19 Incoming echo control device information indicator :
	 * incoming echo control device not included and not available
	 */
	public static final int _NINA = 1;

	/**
	 * See Q.763 3.19 Incoming echo control device information indicator :
	 * incoming echo control device included
	 */
	public static final int _INCLUDED = 2;

	/**
	 * See Q.763 3.19 Incoming echo control device information indicator :
	 * incoming echo control device not included but available
	 */
	public static final int _NIA = 3;
}
