/**
 * Start time:14:40:51 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:40:51 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CSCCPMethodIndicator {
	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) no indication
	 */
	public static final int _NO_INDICATION = 0;
	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) connectionless method
	 * available (national use)
	 */
	public static final int _CONNECTIONLESS = 1;
	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) connection oriented method
	 * available
	 */
	public static final int _CONNECTION_ORIENTED = 2;
	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) connectionless and
	 * connection oriented methods available (national use)
	 */
	public static final int _CONNLESS_AND_CONN_ORIENTED = 3;
	

	
}
