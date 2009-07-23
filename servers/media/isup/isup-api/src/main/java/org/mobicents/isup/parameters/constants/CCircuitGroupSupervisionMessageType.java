/**
 * Start time:15:04:35 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:04:35 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CCircuitGroupSupervisionMessageType {
	/**
	 * See Q.763 3.13 Circuit group supervision message type indicator
	 * maintenance oriented
	 */
	public static final int _MO = 0;
	/**
	 * See Q.763 3.13 Circuit group supervision message type indicator hardware
	 * failure oriented
	 */
	public static final int _HFO = 1;
}
