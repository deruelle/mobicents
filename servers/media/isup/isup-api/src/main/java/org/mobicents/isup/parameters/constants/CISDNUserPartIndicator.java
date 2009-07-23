/**
 * Start time:14:37:59 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:37:59 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CISDNUserPartIndicator {
	/**
	 * See q.763 3.5 ISDN user part indicator (Note 2) ISDN user part not used
	 * all the way
	 */
	public static final boolean _NOT_UATW = false;
	/**
	 * See q.763 3.5 ISDN user part indicator (Note 2) ISDN user part used all
	 * the way
	 */
	public static final boolean _UATW = true;
	
	
	
	/**
	 * See q.763 3.23 ISDN user part preference indicator : ISDN user part
	 * preferred all the way
	 */
	public static final int _PREFERED_ALL_THE_WAY = 0;

	/**
	 * See q.763 3.23 ISDN user part preference indicator : ISDN user part not
	 * required all the way
	 */
	public static final int _NRATW = 1;

	/**
	 * See q.763 3.23 ISDN user part preference indicator : ISDN user part
	 * required all the way
	 */
	public static final int _RATW = 2;
	
}
