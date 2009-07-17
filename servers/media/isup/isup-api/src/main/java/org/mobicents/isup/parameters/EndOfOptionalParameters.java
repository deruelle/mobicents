/**
 * Start time:11:21:05 2009-03-31<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Start time:11:21:05 2009-03-31<br>
 * Project: mobicents-isup-stack<br>
 * This class represent element that encodes end of parameters
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class EndOfOptionalParameters extends AbstractParameter {

	public EndOfOptionalParameters() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EndOfOptionalParameters(byte[] b) {
		super();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	/**
	 * heeh, value is zero actually :D
	 */
	public static final int _PARAMETER_CODE = 0;

	public int decodeElement(byte[] b) throws IllegalArgumentException {

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		// TODO Auto-generated method stub
		return new byte[] { 0 };
	}

	@Override
	public int encodeElement(ByteArrayOutputStream bos) throws IOException {
		bos.write(0);
		return 1;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
