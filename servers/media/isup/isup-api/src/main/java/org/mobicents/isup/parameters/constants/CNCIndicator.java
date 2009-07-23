/**
 * Start time:15:28:47 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:28:47 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CNCIndicator {
	/**
	 * See q.763 3.5 National/international call indicator (Note 1) : call to be
	 * treated as a national call
	 */
	public static final boolean _NATIONAL_CALL = false;

	/**
	 * See q.763 3.5 National/international call indicator (Note 1) : call to be
	 * treated as an international call
	 */
	public static final boolean _INTERNATIONAL_CALL = true;
}
