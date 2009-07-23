/**
 * Start time:15:54:54 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:54:54 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CTypeOfNetworkIdentification {

	/**
	 * See Q.763 Type of network identification : national network
	 * identification
	 */
	public static final int _NNI = 0x02;

	/**
	 * See Q.763 Type of network identification : reserved for international
	 * network identification
	 */
	public static final int _RESERVED_INI = 0x03;
}
