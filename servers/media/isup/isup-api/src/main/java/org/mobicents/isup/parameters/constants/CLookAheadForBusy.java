/**
 * Start time:15:47:57 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:47:57 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CLookAheadForBusy {

	/**
	 * See Q.763 3.34 LFB (Look ahead for busy) : LFB allowed
	 */
	public static final int _INDICATOR_ALLOWED = 0;
	/**
	 * See Q.763 3.34 LFB (Look ahead for busy) : path reserved (national use)
	 */
	public static final int _INDICATOR_PATH_RESERVED = 1;
	/**
	 * See Q.763 3.34 LFB (Look ahead for busy) : LFB not allowed
	 */
	public static final int _INDICATOR_NOT_ALLOWED = 2;
	
}
