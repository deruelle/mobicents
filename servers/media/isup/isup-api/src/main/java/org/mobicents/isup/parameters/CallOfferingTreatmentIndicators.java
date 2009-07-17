/**
 * Start time:13:33:13 2009-04-05<br>
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
 * Start time:13:33:13 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CallOfferingTreatmentIndicators extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x70;
	/**
	 * See Q.763 3.74 Call to be offered indicator : no indication
	 */
	public static final int _CTBOI_NO_INDICATION = 0;

	/**
	 * See Q.763 3.74 Call to be offered indicator : call offering not allowed
	 */
	public static final int _CTBOI_CONA = 1;

	/**
	 * See Q.763 3.74 Call to be offered indicator : call offering allowed
	 */
	public static final int _CTBOI_COA = 2;

	private byte[] callOfferingTreatmentIndicators = null;

	public CallOfferingTreatmentIndicators(byte[] b) throws ParameterRangeInvalidException {
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
			throw new ParameterRangeInvalidException("byte[] must not be null and length must be greater than 0");
		}
		setCallOfferingTreatmentIndicators(b);
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		for (int index = 0; index < this.callOfferingTreatmentIndicators.length; index++) {
			this.callOfferingTreatmentIndicators[index] = (byte) ((this.callOfferingTreatmentIndicators[index] & 0x03) | 0x80);
		}

		this.callOfferingTreatmentIndicators[this.callOfferingTreatmentIndicators.length - 1] = (byte) ((this.callOfferingTreatmentIndicators[this.callOfferingTreatmentIndicators.length - 1]) & 0x7F);
		return this.callOfferingTreatmentIndicators;
	}

	public byte[] getCallOfferingTreatmentIndicators() {
		return callOfferingTreatmentIndicators;
	}

	public void setCallOfferingTreatmentIndicators(byte[] callOfferingTreatmentIndicators) {
		if (callOfferingTreatmentIndicators == null || callOfferingTreatmentIndicators.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}

		this.callOfferingTreatmentIndicators = callOfferingTreatmentIndicators;
	}

	public static int getCallOfferingIndicator(byte b) {
		return b & 0x03;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}