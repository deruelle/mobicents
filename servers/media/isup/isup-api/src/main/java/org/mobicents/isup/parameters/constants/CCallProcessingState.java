/**
 * Start time:15:08:41 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:08:41 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CCallProcessingState {

	/**
	 * See Q.763 3.14 Call processing state : circuit incoming busy
	 */
	public static int _CIB = 1;
	/**
	 * See Q.763 3.14 Call processing state : circuit outgoing busy
	 */
	public static int _COB = 2;
	/**
	 * See Q.763 3.14 Call processing state : idle
	 */
	public static int _IDLE = 3;
	
}
