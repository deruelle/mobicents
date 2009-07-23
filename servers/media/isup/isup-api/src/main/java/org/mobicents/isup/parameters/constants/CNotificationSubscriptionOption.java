/**
 * Start time:14:42:43 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:42:43 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CNotificationSubscriptionOption {
	/**
	 * see Q.763 3.6 Notification subscription options Unknown
	 */
	public static final int _UNKNOWN = 0;

	/**
	 * see Q.763 3.6 Notification subscription options presentation not allowed
	 */
	public static final int _P_NOT_ALLOWED = 1;

	/**
	 * see Q.763 3.6 Notification subscription options presentation allowed with
	 * redirection number
	 */
	public static final int _P_A_WITH_RN = 2;

	/**
	 * see Q.763 3.6 Notification subscription options presentation allowed
	 * without redirection number
	 */
	public static final int _P_A_WITHOUT_RN = 3;

}
