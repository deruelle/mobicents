/**
 * Start time:15:16:44 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:15:16:44 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface CScreeningIndicator {
	/**
	 * screening indicator indicator value. See Q.763 - 3.10f
	 */
	public final static int _USER_PROVIDED_VERIFIED_PASSED = 1;

	/**
	 * screening indicator indicator value. See Q.763 - 3.10f
	 */
	public final static int _NETWORK_PROVIDED = 3;
	
	
	//AGAIN WHICH GOES WHERE?
	/**
	 * screening indicator indicator value. See Q.763 - 3.26g
	 */
	public final static int _USER_PROVIDED_NVERIFIED_PASSED = 0;
//	/**
//	 * screening indicator indicator value. See Q.763 - 3.26g
//	 */
//	public final static int _USER_PROVIDED_VERIFIED_PASSED = 1;
	/**
	 * screening indicator indicator value. See Q.763 - 3.26g
	 */
	public final static int _USER_PROVIDED_VERIFIED_FAILED = 2;

//	/**
//	 * screening indicator indicator value. See Q.763 - 3.26g
//	 */
//	public final static int _NETWORK_PROVIDED = 3;
}
