/**
 * Start time:16:07:38 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:16:07:38 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface COriginalRedirectReason {
	/**
	 * See Q.763 3.45 Original redirection reason unknown/not available
	 */
	public static final int _UNA = 0;
	/**
	 * See Q.763 3.45 Original redirection reason user busy
	 */
	public static final int _USER_BUSY = 1;

	/**
	 * See Q.763 3.45 Original redirection reason no reply
	 */
	public static final int _NO_REPLY = 2;
	/**
	 * See Q.763 3.45 Original redirection reason unconditional
	 */
	public static final int _UNCONDITIONAL = 3;
}
