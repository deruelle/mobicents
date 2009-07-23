/**
 * Start time:14:38:51 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:38:51 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CISDNAccessIndicator {
	/**
	 * See q.763 3.5 ISDN access indicator terminating access non-ISDN
	 */
	public static final boolean _TA_NOT_ISDN = false;
	/**
	 * See q.763 3.5 ISDN access indicator terminating access ISDN
	 */
	public static final boolean _TA_ISDN = true;
	
	//AGAIN WHICH CAN BE SET WHERE!!!!!!!!!!!!!!!
	/**
	 * See q.763 3.5 ISDN access indicator : originating access non-ISDN
	 */
	public static final boolean _OA_N_ISDN = false;
	/**
	 * See q.763 3.5 ISDN access indicator : originating access ISDN
	 */
	public static final boolean _OA_ISDN = true;
}
