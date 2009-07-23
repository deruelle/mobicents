/**
 * Start time:15:33:40 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:33:40 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CTypeOfDigits {

	/**
	 * See Q.763 3.24 Type of digits : reserved for account code
	 */
	public static final int _ACCOUNT_CODE = 0;

	/**
	 * See Q.763 3.24 Type of digits : reserved for authorisation code
	 */
	public static final int _AUTHORIZATION_CODE = 1;

	/**
	 * See Q.763 3.24 Type of digits : reserved for private networking
	 * travelling class mark
	 */
	public static final int _PNTCM = 2;

	/**
	 * See Q.763 3.24 Type of digits : reserved for business communication group
	 * identity
	 */
	public static final int _BGCI = 3;
	
}
