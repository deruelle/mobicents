/**
 * Start time:16:03:11 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:16:03:11 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CRedirectCapabilityIndicator {
	/**
	 * See Q.763 3.96 Redirect possible indicator : not used
	 */
	public static final int _NOT_USED = 0;
	/**
	 * See Q.763 3.96 Redirect possible indicator : redirect possible before
	 * ACM use)
	 */
	public static final int _RPB_ACM = 1;
	/**
	 * See Q.763 3.96 Redirect possible indicator : redirect possible before
	 * ANM
	 */
	public static final int _RPB_ANM = 2;
	/**
	 * See Q.763 3.96 Redirect possible indicator : redirect possible at any
	 * time during the call
	 */
	public static final int _RPANTDC = 3;
}
