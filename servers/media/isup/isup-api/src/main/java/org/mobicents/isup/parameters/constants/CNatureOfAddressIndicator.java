/**
 * Start time:14:50:01 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters.constants;

/**
 * Start time:14:50:01 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface CNatureOfAddressIndicator {

	/**
	 * See Q.763 3.90 Nature of address indicator : network routing number in
	 * national (significant) number format (national use)
	 */
	public static final int _NRNI_NATIONAL_NF = 1;

	/**
	 * See Q.763 3.90 Nature of address indicator : network routing number in
	 * network specific number format (national use)
	 */
	public static final int _NRNI_NETWORK_SNF = 2;

	// AGAIN< WHICH GOES WHERE!!!!!!!!!!!!!

	/**
	 * nature of address indicator value. See Q.763 - 3.46b network routing
	 * number in national (significant) number format (national use)
	 */
	public final static int _NRNINNF = 6;

	/**
	 * nature of address indicator value. See Q.763 - 3.46b network routing
	 * number in network-specific number format (national use)
	 */
	public final static int _NRNINSNF = 7;
	/**
	 * nature of address indicator value. See Q.763 - 3.46b reserved for network
	 * routing number concatenated with Called Directory Number (national use)
	 */
	public final static int _RNRNCWCDN = 8;

}
