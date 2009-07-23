/**
 * Start time:15:09:09 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:09:09 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface CHardwareBlockingState {
	/**
	 * See Q.763 3.14 Hardware blocking state (Note: if this does not equal "0"
	 * Call Processing State must be equal to "3") : no blocking (active)
	 */
	public static int _NO_BLOCKING = 0;
	/**
	 * See Q.763 3.14 Hardware blocking state (Note: if this does not equal "0"
	 * Call Processing State must be equal to "3") : locally blocked
	 */
	public static int _LOCALY_BLOCKED = 1;
	/**
	 * See Q.763 3.14 Hardware blocking state (Note: if this does not equal "0"
	 * Call Processing State must be equal to "3") : remotely blocked
	 */
	public static int _REMOTELY_BLOCKED = 2;
	/**
	 * See Q.763 3.14 Hardware blocking state (Note: if this does not equal "0"
	 * Call Processing State must be equal to "3") : locally and remotely
	 * blocked
	 */
	public static int _LAR_BLOCKED = 3;

}
