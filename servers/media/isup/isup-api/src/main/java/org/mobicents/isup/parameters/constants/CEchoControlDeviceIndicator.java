/**
 * Start time:14:39:23 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:39:23 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CEchoControlDeviceIndicator {
	/**
	 * See q.763 3.5 Echo control device indicator incoming echo control device
	 * not included
	 */
	public static final boolean _IECD_NOT_INCLUDED = false;
	/**
	 * See q.763 3.5 Echo control device indicator incoming echo control device
	 * included
	 */
	public static final boolean _IECD_INCLUDED = true;
}
