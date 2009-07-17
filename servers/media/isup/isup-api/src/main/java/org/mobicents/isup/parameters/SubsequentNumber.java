/**
 * Start time:17:32:06 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:17:32:06 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class SubsequentNumber extends AbstractNAINumber {

	public static final int _PARAMETER_CODE = 0x05;
	
	public SubsequentNumber(byte[] representation) throws ParameterRangeInvalidException {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	public SubsequentNumber(ByteArrayInputStream bis) throws ParameterRangeInvalidException {
		super(bis);
		// TODO Auto-generated constructor stub
	}

	public SubsequentNumber(String address) {
		super(0, address);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.isup.parameters.AbstractNumber#decodeBody(java.io.
	 * ByteArrayInputStream)
	 */
	@Override
	public int decodeBody(ByteArrayInputStream bis) throws IllegalArgumentException {
		// NOTE: we leave this.

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.isup.parameters.AbstractNumber#encodeBody(java.io.
	 * ByteArrayOutputStream)
	 */
	@Override
	public int encodeBody(ByteArrayOutputStream bos) {
		// NOTE: we leave this.
		return 0;
	}
	public int getCode() {

		return _PARAMETER_CODE;
	}

}
