/**
 * Start time:13:42:38 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:13:42:38 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class ConferenceTreatmentIndicators extends AbstractParameter {

	/**
	 * See Q.763 3.76 Conference acceptance indicator (Note) : no indication
	 */
	public static final int _CAI_NO_INDICATION = 0;

	/**
	 * See Q.763 3.76 Conference acceptance indicator (Note) : accept conference
	 * request
	 */
	public static final int _CAI_ACR = 1;

	/**
	 * See Q.763 3.76 Conference acceptance indicator (Note) : reject conference
	 * request
	 */
	public static final int _CAI_RCR = 2;

	public static final int _PARAMETER_CODE = 0x72;

	private byte[] conferenceAcceptance = null;

	public ConferenceTreatmentIndicators(byte[] b) {
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
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}
		setConferenceAcceptance(b);
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		for (int index = 0; index < this.conferenceAcceptance.length; index++) {
			this.conferenceAcceptance[index] = (byte) (this.conferenceAcceptance[index] & 0x03);
		}

		this.conferenceAcceptance[this.conferenceAcceptance.length - 1] = (byte) ((this.conferenceAcceptance[this.conferenceAcceptance.length - 1]) | (0x01 << 7));
		return this.conferenceAcceptance;
	}

	public byte[] getConferenceAcceptance() {
		return conferenceAcceptance;
	}

	public void setConferenceAcceptance(byte[] conferenceAcceptance) {
		if (conferenceAcceptance == null || conferenceAcceptance.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}

		this.conferenceAcceptance = conferenceAcceptance;
	}

	public static int getConferenceTreatmentIndicator(byte b) {
		return b & 0x03;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
