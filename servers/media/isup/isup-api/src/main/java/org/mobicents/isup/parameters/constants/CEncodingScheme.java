/**
 * Start time:15:33:14 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:33:14 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CEncodingScheme {
	/**
	 * See Q.763 3.24 Encoding scheme : BCD even: (even number of digits)
	 */
	public static final int _BCD_EVEN = 0;

	/**
	 * See Q.763 3.24 Encoding scheme : BCD odd: (odd number of digits)
	 */
	public static final int _BCD_ODD = 1;

	/**
	 * See Q.763 3.24 Encoding scheme : IA5 character
	 */
	public static final int _IA5 = 2;

	/**
	 * See Q.763 3.24 Encoding scheme : binary coded
	 */
	public static final int _BINARY = 3;

}
