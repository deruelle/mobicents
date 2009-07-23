/**
 * Start time:15:15:46 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:15:46 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CConferenceAcceptanceIndicator {
	/**
	 * See Q.763 3.76 Conference acceptance indicator (Note) : no indication
	 */
	public static final int _NO_INDICATION = 0;

	/**
	 * See Q.763 3.76 Conference acceptance indicator (Note) : accept conference
	 * request
	 */
	public static final int _ACR = 1;

	/**
	 * See Q.763 3.76 Conference acceptance indicator (Note) : reject conference
	 * request
	 */
	public static final int _RCR = 2;
}
