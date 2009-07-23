/**
 * Start time:14:44:34 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:44:34 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CInternalNetworkNumberIndicator {
	/**
	 * See Q.763 Internal network number indicator (INN) : reserved
	 */
	public static final int _RESERVED = 0;

	/**
	 * See Q.763 Internal network number indicator (INN) : routing to internal
	 * network number not allowed
	 */
	public static final int _RTINNNA = 1;
		
	//Now which is correct!!!!!!!!!!!!!
	
	/**
	 * internal network number indicator indicator value. See Q.763 - 3.9c
	 */
	public final static int _ROUTING_ALLOWED = 0;
	/**
	 * internal network number indicator indicator value. See Q.763 - 3.9c
	 * 
	 */
	public final static int _ROUTING_NOT_ALLOWED = 1;

}
