/**
 * Start time:12:56:36 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:12:56:36 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CCSS extends AbstractParameter {

	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	/**
	 * See Q.763 3.83 CCNR possible indicator : not possible
	 */
	public static final boolean _NOT_CCSS_CALL = false;
	/**
	 * See Q.763 3.83 CCNR possible indicator : possible
	 */
	public static final boolean _CCSS_CALL = true;
	public static final int _PARAMETER_CODE = 0x4B;

	private boolean ccssCall = false;

	public CCSS(boolean ccssCall) {
		super();
		this.ccssCall = ccssCall;
	}

	public CCSS(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length == 0) {
			throw new ParameterRangeInvalidException("byte[] must not be null and length must be 1");
		}

		this.ccssCall = (b[0] & 0x01) == _TURN_ON;

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return new byte[] { (byte) (this.ccssCall ? _TURN_ON : _TURN_OFF) };
	}

	
	public boolean isCcssCall() {
		return ccssCall;
	}

	public void setCcssCall(boolean ccssCall) {
		this.ccssCall = ccssCall;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
