/**
 * Start time:18:36:26 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * 
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:18:36:26 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class OriginalCalledINNumberImpl extends CalledNumberImpl implements OriginalCalledINNumber {

	public OriginalCalledINNumberImpl(byte[] representation) throws ParameterRangeInvalidException {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	public OriginalCalledINNumberImpl(ByteArrayInputStream bis) throws ParameterRangeInvalidException {
		super(bis);
		// TODO Auto-generated constructor stub
	}

	public OriginalCalledINNumberImpl(int natureOfAddresIndicator, String address, int numberingPlanIndicator, int addressRepresentationREstrictedIndicator) {
		super(natureOfAddresIndicator, address, numberingPlanIndicator, addressRepresentationREstrictedIndicator);
		// TODO Auto-generated constructor stub
	}

	public OriginalCalledINNumberImpl() throws ParameterRangeInvalidException {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
