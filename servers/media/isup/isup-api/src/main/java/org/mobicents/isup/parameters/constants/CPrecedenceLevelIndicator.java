/**
 * Start time:15:48:29 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:48:29 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CPrecedenceLevelIndicator {

	/**
	 * See Q.763 3.34 Precedence level : flash override
	 */
	public static final int _FLASH_OVERRIDE = 0;

	/**
	 * See Q.763 3.34 Precedence level : flash
	 */
	public static final int _FLASH = 1;
	/**
	 * See Q.763 3.34 Precedence level : immediate
	 */
	public static final int _IMMEDIATE = 2;
	/**
	 * See Q.763 3.34 Precedence level : priority
	 */
	public static final int _PRIORITY = 3;

	/**
	 * See Q.763 3.34 Precedence level : routine
	 */
	public static final int _ROUTINE = 4;
	
}
