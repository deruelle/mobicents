/**
 * Start time:18:36:26 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:18:36:26 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class OriginalCalledINNumber extends CalledNumber {

	public static final int _PARAMETER_CODE = 0x7F;
	public OriginalCalledINNumber(byte[] representation) throws ParameterRangeInvalidException {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	public OriginalCalledINNumber(ByteArrayInputStream bis) throws ParameterRangeInvalidException {
		super(bis);
		// TODO Auto-generated constructor stub
	}

	public OriginalCalledINNumber(int natureOfAddresIndicator, String address, int numberingPlanIndicator, int addressRepresentationREstrictedIndicator) {
		super(natureOfAddresIndicator, address, numberingPlanIndicator, addressRepresentationREstrictedIndicator);
		// TODO Auto-generated constructor stub
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
