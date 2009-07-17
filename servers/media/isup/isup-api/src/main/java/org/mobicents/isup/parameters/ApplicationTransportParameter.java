/**
 * Start time:15:10:58 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:15:10:58 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class ApplicationTransportParameter extends AbstractParameter {

	//FIXME: Oleg ? how can we do that ? Q.763 3.82
	
	/* (non-Javadoc)
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public static final int _PARAMETER_CODE = 0x78;

	
	
	
	public ApplicationTransportParameter(byte[] b) throws ParameterRangeInvalidException{
		super();
		decodeElement(b);
	}

	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCode() {
		
		return _PARAMETER_CODE;
	}
}
