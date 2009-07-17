/**
 * Start time:12:48:19 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:12:48:19 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SCFID extends NetworkRoutingNumber {
	public static final int _PARAMETER_CODE = 0x66;
	// FIXME: Q.1218 - oleg is this correct? :
	// http://www.itu.int/ITU-T/asn1/database/itu-t/q/q1238.2/2000/IN-CS3-SSF-SCF-datatypes.html
	public SCFID() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SCFID(byte[] representation) throws ParameterRangeInvalidException {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	public SCFID(ByteArrayInputStream bis) throws ParameterRangeInvalidException {
		super(bis);
		// TODO Auto-generated constructor stub
	}

	public SCFID(String address, int numberingPlanIndicator, int natureOfAddressIndicator) {
		super(address, numberingPlanIndicator, natureOfAddressIndicator);
		// TODO Auto-generated constructor stub
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}

}
