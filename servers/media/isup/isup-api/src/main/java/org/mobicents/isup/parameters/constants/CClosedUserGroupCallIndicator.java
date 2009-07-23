/**
 * Start time:15:59:16 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:59:16 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CClosedUserGroupCallIndicator {
	/**
	 * See Q.763 3.38 Closed user group call indicator : non-CUG call
	 */
	public final static int _NON_CUG_CALL = 0;

	/**
	 * See Q.763 3.38 Closed user group call indicator : closed user group call,
	 * outgoing access allowed
	 */
	public final static int _CUG_CALL_OAL = 2;

	/**
	 * See Q.763 3.38 Closed user group call indicator : closed user group call,
	 * outgoing access not allowed
	 */
	public final static int _CUG_CALL_OANL = 3;
}
