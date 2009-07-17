/**
 * Start time:13:04:01 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:13:04:01 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class GenericReference extends AbstractParameter {

	
	//FIXME: impl this.
	public static final int _PARAMETER_CODE = -300;
	/**
	 * 	
	 */
	public GenericReference() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @throws ParameterRangeInvalidException 
	 * @throws ParameterRangeInvalidException 
	 * 	
	 */
	public GenericReference(byte[] b) throws ParameterRangeInvalidException {
		decodeElement(b);
	}
	/* (non-Javadoc)
	 * @see org.mobicents.isup.parameters.ISUPParameter#getCode()
	 */
	public int getCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
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

}
