/**
 * Start time:16:06:53 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:16:06:53 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CRedirectReason {
	/**
	 * See Q.763 3.45 Redirecting reason unknown/not available
	 */
	public static final int _UNA = 0;
	/**
	 * See Q.763 3.45 Redirecting reason user busy
	 */
	public static final int _USER_BUSY = 1;

	/**
	 * See Q.763 3.45 Redirecting reason no reply
	 */
	public static final int _NO_REPLY = 2;
	/**
	 * See Q.763 3.45 Redirecting reason unconditional
	 */
	public static final int _UNCONDITIONAL = 3;

	/**
	 * See Q.763 3.45 Redirecting reason deflection during alerting
	 */
	public static final int _DEFLECTION_DA = 4;

	/**
	 * See Q.763 3.45 Redirecting reason deflection immediate response
	 */
	public static final int _DEFLECTION_IE = 5;

	/**
	 * See Q.763 3.45 Redirecting reason mobile subscriber not reachable
	 */
	public static final int _MOBILE_SNR = 6;
}
