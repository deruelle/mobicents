/**
 * Start time:15:44:56 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:15:44:56 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CCNRPossibleIndicator extends AbstractParameter {

	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	/**
	 * See Q.763 3.83 CCNR possible indicator : not possible
	 */
	public static final boolean _CCNR_PI_NOT_POSSIBLE = false;
	/**
	 * See Q.763 3.83 CCNR possible indicator : possible
	 */
	public static final boolean _CCNR_PI_POSSIBLE = true;
	public static final int _PARAMETER_CODE = 0x7A;

	private boolean ccnrPossible = false;

	public CCNRPossibleIndicator(boolean ccnrPossible) {
		super();
		this.ccnrPossible = ccnrPossible;
	}

	public CCNRPossibleIndicator(byte[] b) {
		super();
		decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be 1");
		}

		this.ccnrPossible = (b[0] & 0x01) == _TURN_ON;

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return new byte[] { (byte) (this.ccnrPossible ? _TURN_ON : _TURN_OFF) };
	}

	public boolean isCcnrPossible() {
		return ccnrPossible;
	}

	public void setCcnrPossible(boolean ccnrPossible) {
		this.ccnrPossible = ccnrPossible;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
