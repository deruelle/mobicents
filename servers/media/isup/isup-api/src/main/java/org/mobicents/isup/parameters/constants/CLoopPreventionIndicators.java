/**
 * Start time:15:46:17 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:46:17 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface CLoopPreventionIndicators {
	/**
	 * See Q.763 3.67 Response indicator : insufficient information (note)
	 */
	public static final int _RI_INS_INFO = 0;

	/**
	 * See Q.763 3.67 Response indicator : no loop exists
	 */
	public static final int _RI_NO_LOOP_E = 1;

	/**
	 * See Q.763 3.67 Response indicator : simultaneous transfer
	 */
	public static final int _RI_SIM_TRANS = 2;
}
