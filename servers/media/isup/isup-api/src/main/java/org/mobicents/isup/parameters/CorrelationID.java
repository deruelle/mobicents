/**
 * Start time:12:48:19 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:12:48:19 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * 
 */
public class CorrelationID extends GenericDigits {

	public static final int _PARAMETER_CODE = 0x65;

	public CorrelationID(byte[] b) {
		super(b);
		// TODO Auto-generated constructor stub
	}

	public CorrelationID(int encodignScheme, int typeOfDigits, int[] digits) {
		super(encodignScheme, typeOfDigits, digits);
		// TODO Auto-generated constructor stub
	}

	// FIXME: Q.1218 -- weird document.... Oleg is this correct? or should it be
	// mix of GenericNumber and Generic digits?
	public int getCode() {

		return _PARAMETER_CODE;
	}
}
