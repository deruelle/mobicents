/**
 * Start time:14:32:41 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:32:41 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CChargeIndicator {
	/**
	 * See q.763 3.5 Charge indicator no indication
	 */
	public static final int _NOINDICATION = 0;
	/**
	 * See q.763 3.5 Charge indicator no charge
	 */
	public static final int _NOCHARGE = 1;
	/**
	 * See q.763 3.5 Charge indicator charge
	 */
	public static final int _CHARGE = 2;
}
