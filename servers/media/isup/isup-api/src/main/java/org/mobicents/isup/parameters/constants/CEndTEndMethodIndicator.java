/**
 * Start time:14:35:42 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:35:42 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CEndTEndMethodIndicator {
	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2)
	 */
	public static final int _NOMETHODAVAILABLE = 0;
	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2)
	 */
	public static final int _PASSALONG = 1;
	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2)
	 */
	public static final int _SCCP = 2;
	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2)
	 */
	public static final int _SCCP_AND_PASSALONG = 3;
	
	
	/**
	 * See q.763 3.5 End-to-end information indicator (national use) (Note 2) :
	 * no end-to-end information available
	 */
	public static final boolean _NOT_AVAILABLE = false;
	/**
	 * See q.763 3.5 End-to-end information indicator (national use) (Note 2) :
	 * end-to-end information available
	 */
	public static final boolean _AVAILABLE = true;
}
