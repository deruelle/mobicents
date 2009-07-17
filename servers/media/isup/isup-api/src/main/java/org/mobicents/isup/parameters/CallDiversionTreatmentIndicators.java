/**
 * Start time:12:50:23 2009-04-05<br>
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
 * Start time:12:50:23 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CallDiversionTreatmentIndicators extends AbstractParameter {
	public static final int _PARAMETER_CODE = 0x6E;
	/**
	 * See Q.763 3.72 Call to be diverted indicator : no indication
	 */
	public static final int _CTBDI_NO_INDICATION = 0;

	/**
	 * See Q.763 3.72 Call to be diverted indicator : call diversion allowed
	 */
	public static final int _CTBDI_CDA = 1;

	/**
	 * See Q.763 3.72 Call to be diverted indicator : call diversion not allowed
	 */
	public static final int _CTBDI_CDNA = 2;

	private byte[] callDivertedIndicators = null;

	
	
	public CallDiversionTreatmentIndicators() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CallDiversionTreatmentIndicators(byte[] b) throws ParameterRangeInvalidException{
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
			throw new ParameterRangeInvalidException("byte[] must  not be null and length must  be greater than 0");
		}
		this.callDivertedIndicators = b;
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		for (int index = 0; index < this.callDivertedIndicators.length; index++) {
			this.callDivertedIndicators[index] = (byte) (this.callDivertedIndicators[index] & 0x03);
		}

		this.callDivertedIndicators[this.callDivertedIndicators.length - 1] = (byte) ((this.callDivertedIndicators[this.callDivertedIndicators.length - 1]) | (0x01 << 7));
		return this.callDivertedIndicators;
	}

	public byte[] getCallDivertedIndicators() {
		return callDivertedIndicators;
	}

	public void setCallDivertedIndicators(byte[] callDivertedIndicators) {
		this.callDivertedIndicators = callDivertedIndicators;
	}

	public static int getDiversionIndicator(byte b) {
		return b & 0x03;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
