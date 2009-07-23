/**
 * Start time:14:43:49 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:43:49 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface CNumberingPlanIndicator {
	/**
	 * See Q.763 Numbering plan indicator : ISDN (Telephony) numbering plan
	 * (ITU-T Recommendation E.164)
	 */
	public static final int _ISDN_NP = 1; // CalledDirectoryNumber, NetworkRoutingNumber
	
	
	/**
	 * numbering plan indicator indicator value. See Q.763 - 3.9d
	 */
	public final static int _ISDN = 1; //CalledNumber
	/**
	 * numbering plan indicator indicator value. See Q.763 - 3.9d
	 */
	public final static int _DATA = 3;//CalledNumber
	/**
	 * numbering plan indicator indicator value. See Q.763 - 3.9d
	 */
	public final static int _TELEX = 4;//CalledNumber


	

}
