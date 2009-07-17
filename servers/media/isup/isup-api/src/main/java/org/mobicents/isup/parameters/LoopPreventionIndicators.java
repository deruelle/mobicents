/**
 * Start time:11:31:36 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:11:31:36 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class LoopPreventionIndicators extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x44;
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	public static final boolean _TYPE_REQUEST = false;
	public static final boolean _TYPE_RESPONSE = true;

	/**
	 * See Q.763 3.67 Response indicator : insufficient information (note)
	 */
	public static final int _RI_INS_INFO = 0;

	/**
	 * See Q.763 3.67 Response indicator : no loop exists
	 */
	public static final int _RI_NO_LOOP_E = 1;

	/**
	 * See Q.763 3.67 Response indicator : simultaneous transfer
	 */
	public static final int _RI_SIM_TRANS = 2;
	private boolean response ;
	private int responseIndicator;

	public LoopPreventionIndicators(boolean response, int responseIndicator) {
		super();
		this.response = response;
		this.responseIndicator = responseIndicator;
	}

	public LoopPreventionIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must  not be null and length must  be 1");
		}

		this.response = (b[0] & 0x01) == _TURN_ON;

		if (response) {
			this.responseIndicator = (b[0] >> 1) & 0x03;
		}
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		int v = this.response ? _TURN_ON : _TURN_OFF;
		if (this.response) {
			v |= (this.responseIndicator & 0x03) << 1;
		}
		return new byte[] { (byte) v };
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public int getResponseIndicator() {
		return responseIndicator;
	}

	public void setResponseIndicator(int responseIndicator) {
		this.responseIndicator = responseIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
