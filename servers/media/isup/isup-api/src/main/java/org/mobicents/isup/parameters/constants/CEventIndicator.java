/**
 * Start time:15:25:07 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:25:07 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CEventIndicator {
	/**
	 * See Q.763 3.21 Event indicator : ALERTING
	 */
	public static final int _ALERTING = 1;

	/**
	 * See Q.763 3.21 Event indicator : PROGRESS
	 */
	public static final int _PROGRESS = 2;

	/**
	 * See Q.763 3.21 Event indicator : in-band information or an appropriate
	 * pattern is now available
	 */
	public static final int _IIIOPA = 3;

	/**
	 * See Q.763 3.21 Event indicator : call forwarded on busy (national use)
	 */
	public static final int _CFOB = 4;

	/**
	 * See Q.763 3.21 Event indicator : call forwarded on no reply (national
	 * use)
	 */
	public static final int _CFONNR = 5;

	/**
	 * See Q.763 3.21 Event indicator : call forwarded unconditional (national
	 * use)
	 */
	public static final int _CFOU = 6;
}
