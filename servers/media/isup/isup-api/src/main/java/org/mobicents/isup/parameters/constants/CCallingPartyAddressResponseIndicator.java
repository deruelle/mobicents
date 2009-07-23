/**
 * Start time:15:40:18 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:40:18 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CCallingPartyAddressResponseIndicator {
	/**
	 * See Q.763 3.28 Calling party address response indicator : calling party
	 * address not included
	 */
	public static final int _ADDRESS_NOT_INCLUDED = 0;

	/**
	 * See Q.763 3.28 Calling party address response indicator : calling party
	 * address included
	 */
	public static final int _ADDRESS_INCLUDED = 3;
	/**
	 * See Q.763 3.28 Calling party address response indicator : calling party
	 * address not available
	 */
	public static final int _ADDRESS_NOT_AVAILABLE = 1;
}
