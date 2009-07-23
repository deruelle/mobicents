/**
 * Start time:15:00:20 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:00:20 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CLocation {
	/**
	 * See Q.850
	 */
	public static final int _USER = 0;

	/**
	 * See Q.850 private network serving the local user (LPN)
	 */
	public static final int _PRIVATE_NSLU = 1;

	/**
	 * See Q.850 public network serving the local user (LN)
	 */
	public static final int _PUBLIC_NSLU = 2;

	/**
	 * See Q.850 transit network (TN)
	 */
	public static final int _TRANSIT_NETWORK = 3;

	/**
	 * See Q.850 private network serving the remote user (RPN)
	 */
	public static final int _PRIVATE_NSRU = 5;

	/**
	 * See Q.850 public network serving the remote user (RLN)
	 */
	public static final int _PUBLIC_NSRU = 4;
	/**
	 * See Q.850
	 */
	public static final int _INTERNATIONAL_NETWORK = 7;

	/**
	 * See Q.850 network beyond interworking point (BI)
	 */
	public static final int _NETWORK_BEYOND_IP = 10;
}
