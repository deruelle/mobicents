/**
 * Start time:16:08:01 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:16:08:01 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CRedirectingIndicator {
	/**
	 * See Q.763 3.45 Redirecting indicator no redirection (national use)
	 */
	public static final int _NO_REDIRECTION = 0;

	/**
	 * See Q.763 3.45 Redirecting indicator no redirection (national use)
	 */
	public static final int _CALL_REROUTED = 1;

	/**
	 * See Q.763 3.45 Redirecting indicator call rerouted, all redirection
	 * information presentation restricted (national use)
	 */
	public static final int _CALL_RD = 2;

	/**
	 * See Q.763 3.45 Redirecting indicator call diverted
	 */
	public static final int _CALL_D = 3;

	/**
	 * See Q.763 3.45 Redirecting indicator call diverted, all redirection
	 * information presentation restricted
	 */
	public static final int _CALL_DR = 4;

	/**
	 * See Q.763 3.45 Redirecting indicator call rerouted, redirection number
	 * presentation restricted (national use)
	 */
	public static final int _CALL_R_RNPR = 5;

	/**
	 * See Q.763 3.45 Redirecting indicator call diversion, redirection number
	 * presentation restricted (national use)
	 */
	public static final int _CALL_D_RNPR = 6;
}
