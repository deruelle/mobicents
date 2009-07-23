/**
 * Start time:14:57:37 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:57:37 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CCallOfferingTreatmentIndicator {
	/**
	 * See Q.763 3.74 Call to be offered indicator : no indication
	 */
	public static final int _NO_INDICATION = 0;

	/**
	 * See Q.763 3.74 Call to be offered indicator : call offering not allowed
	 */
	public static final int _CONA = 1;

	/**
	 * See Q.763 3.74 Call to be offered indicator : call offering allowed
	 */
	public static final int _COA = 2;
}
