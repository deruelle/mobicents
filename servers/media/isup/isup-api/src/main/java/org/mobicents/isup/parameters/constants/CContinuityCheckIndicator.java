/**
 * Start time:15:18:37 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:18:37 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CContinuityCheckIndicator {
	/**
	 * See Q.763 3.18
	 */
	public static final boolean _FAILED = false;

	/**
	 * See Q.763 3.18
	 */
	public static final boolean _SUCCESSFUL = true;
	
	
	/**
	 * See Q.763 3.35 Continuity check indicator
	 */
	public static final int _NOT_REQUIRED = 0;
	/**
	 * See Q.763 3.35 Continuity check indicator
	 */
	public static final int _REQUIRED_ON_THIS_CIRCUIT = 1;

	/**
	 * See Q.763 3.35 Continuity check indicator
	 */
	public static final int _PERFORMED_ON_PREVIOUS_CIRCUIT = 2;
}
