/**
 * Start time:13:06:26 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;

/**
 * Start time:13:06:26 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CalledINNumber extends CalledNumber {
	public static final int _PARAMETER_CODE = 0x6F;
	/**
	 * @param representation
	 */
	public CalledINNumber(byte[] representation) {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param bis
	 */
	public CalledINNumber(ByteArrayInputStream bis) {
		super(bis);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param natureOfAddresIndicator
	 * @param address
	 * @param numberingPlanIndicator
	 * @param addressRepresentationREstrictedIndicator
	 */
	public CalledINNumber(int natureOfAddresIndicator, String address, int numberingPlanIndicator, int addressRepresentationREstrictedIndicator) {
		super(natureOfAddresIndicator, address, numberingPlanIndicator, addressRepresentationREstrictedIndicator);
		// TODO Auto-generated constructor stub
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
