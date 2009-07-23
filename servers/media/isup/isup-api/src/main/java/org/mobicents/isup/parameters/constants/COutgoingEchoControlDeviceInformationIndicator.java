/**
 * Start time:15:20:58 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:20:58 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface COutgoingEchoControlDeviceInformationIndicator {
	/**
	 * See Q.763 3.19 Outgoing echo control device request indicator : no
	 * information
	 */
	public static final int _NOINFO = 0;

	/**
	 * See Q.763 3.19 Outgoing echo control device request indicator : outgoing
	 * echo control device activation request
	 */
	public static final int _AR = 1;

	/**
	 * See Q.763 3.19 Outgoing echo control device request indicator : outgoing
	 * echo control device deactivation request (Note 1)
	 */
	public static final int _DR = 2;
	
	
	/**
	 * See Q.763 3.35 Echo control device indicator : outgoing echo control
	 * device included
	 */
	public static final boolean _INCLUDED = true;

	/**
	 * See Q.763 3.35 Echo control device indicator : outgoing echo control
	 * device not included
	 */
	public static final boolean _NOT_INCLUDED = false;
}
