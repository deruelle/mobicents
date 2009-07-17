/**
 * Start time:18:28:42 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:18:28:42 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class ContinuitiyIndicators extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x10;
	private final static int _TURN_ON = 1;
	private final static int _TURN_OFF = 0;

	/**
	 * See Q.763 3.18
	 */
	public static final boolean _CONTINUITY_CHECK_FAILED = false;

	/**
	 * See Q.763 3.18
	 */
	public static final boolean _CONTINUITY_CHECK_SUCCESSFUL = true;

	private boolean continuityCheck = false;

	public ContinuitiyIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	public ContinuitiyIndicators(boolean continuityCheck) {
		super();
		this.continuityCheck = continuityCheck;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must not be null or have different size than 1");
		}
		this.continuityCheck = (b[0] & 0x01) == _TURN_ON;
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return new byte[] { (byte) (this.continuityCheck ? _TURN_ON : _TURN_OFF) };
	}

	public boolean isContinuityCheck() {
		return continuityCheck;
	}

	public void setContinuityCheck(boolean continuityCheck) {
		this.continuityCheck = continuityCheck;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
