/**
 * Start time:15:06:06 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:06:06 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CMaintanenceBlockingState {
	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state "0"
	 * : transient
	 */
	public static int _NPS_TRANSIENT = 0;

	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state "0"
	 * : unequipped
	 */
	public static int _NPS_UNEQUIPED = 3;
	
	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state
	 * ~"0" : no blocking - active
	 */
	public static int _NO_BLOCKING = 0;

	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state
	 * ~"0" : localy blocked
	 */
	public static int _LOCALY_BLOCKED = 1;
	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state
	 * ~"0" : remotely blocked blocked
	 */
	public static int _REMOTELY_BLOCKED = 2;
	
	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state
	 * ~"0" : locally and remotely blocked
	 */
	public static int _MBS_LAR_BLOCKED = 3;
}
