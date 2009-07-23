/**
 * Start time:14:33:30 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:33:30 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CCalledPartysStatusIndicator {

	/**
	 * See q.763 3.5 Called party's status indicator no indication
	 */
	public static final int _NO_INDICATION = 0;
	/**
	 * See q.763 3.5 Called party's status indicator subscriber free
	 */
	public static final int _SUBSCRIBER_FREE = 1;
	/**
	 * See q.763 3.5 Called party's status indicator connect when free (national
	 * use)
	 */
	public static final int _CONNECT_WHEN_FREE = 2;
	
}
