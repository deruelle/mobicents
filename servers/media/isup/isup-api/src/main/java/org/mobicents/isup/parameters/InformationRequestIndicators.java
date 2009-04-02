/**
 * Start time:14:20:07 2009-04-01<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:14:20:07 2009-04-01<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class InformationRequestIndicators extends AbstractParameter {

	/**
	 * Flag that indicates that information is requested
	 */
	public static final int _INDICATOR_REQUESTED = 1;

	/**
	 * Flag that indicates that information is not requested
	 */
	public static final int _INDICATOR_NOT_REQUESTED = 0;

	private int callingPartAddressRequestIndicator = 0;
	private int holdingIndicator = 0;
	private int callingpartysCategoryRequestIndicator = 0;
	private int chargeInformationRequestIndicator = 0;
	private int maliciousCallIdentificationRequestIndicator = 0;

	// FIXME: should we carre about this?
	private int reserved = 0;

	public InformationRequestIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	public InformationRequestIndicators(int callingPartAddressRequestIndicator, int holdingIndicator, int callingpartysCategoryRequestIndicator, int chargeInformationRequestIndicator,
			int maliciousCallIdentificationRequestIndicator, int reserved) {
		super();
		this.callingPartAddressRequestIndicator = callingPartAddressRequestIndicator;
		this.holdingIndicator = holdingIndicator;
		this.callingpartysCategoryRequestIndicator = callingpartysCategoryRequestIndicator;
		this.chargeInformationRequestIndicator = chargeInformationRequestIndicator;
		this.maliciousCallIdentificationRequestIndicator = maliciousCallIdentificationRequestIndicator;
		this.reserved = reserved;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 2) {
			throw new IllegalArgumentException("byte[] must  not be null and length must  be 2");
		}

		this.callingPartAddressRequestIndicator = b[0] & 0x01;
		this.holdingIndicator = (b[0] >> 1) & 0x01;
		this.callingpartysCategoryRequestIndicator = (b[0] >> 3) & 0x01;
		this.chargeInformationRequestIndicator = (b[0] >> 4) & 0x01;
		this.maliciousCallIdentificationRequestIndicator = (b[0] >> 7) & 0x01;
		this.reserved = (b[0] >> 4) & 0x0F;
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		int b0 = 0;
		int b1 = 0;
		b0 |= this.callingPartAddressRequestIndicator & 0x01;
		b0 |= (this.holdingIndicator & 0x01) << 1;
		b0 |= (this.callingpartysCategoryRequestIndicator & 0x01) << 3;
		b0 |= (this.chargeInformationRequestIndicator & 0x01) << 4;
		b0 |= (this.maliciousCallIdentificationRequestIndicator & 0x01) << 7;

		b1 |= (this.reserved & 0x0F) << 4;

		return new byte[] { (byte) b0, (byte) b1 };
	}

	public int getCallingPartAddressRequestIndicator() {
		return callingPartAddressRequestIndicator;
	}

	public void setCallingPartAddressRequestIndicator(int callingPartAddressRequestIndicator) {
		this.callingPartAddressRequestIndicator = callingPartAddressRequestIndicator;
	}

	public int getHoldingIndicator() {
		return holdingIndicator;
	}

	public void setHoldingIndicator(int holdingIndicator) {
		this.holdingIndicator = holdingIndicator;
	}

	public int getCallingpartysCategoryRequestIndicator() {
		return callingpartysCategoryRequestIndicator;
	}

	public void setCallingpartysCategoryRequestIndicator(int callingpartysCategoryRequestIndicator) {
		this.callingpartysCategoryRequestIndicator = callingpartysCategoryRequestIndicator;
	}

	public int getChargeInformationRequestIndicator() {
		return chargeInformationRequestIndicator;
	}

	public void setChargeInformationRequestIndicator(int chargeInformationRequestIndicator) {
		this.chargeInformationRequestIndicator = chargeInformationRequestIndicator;
	}

	public int getMaliciousCallIdentificationRequestIndicator() {
		return maliciousCallIdentificationRequestIndicator;
	}

	public void setMaliciousCallIdentificationRequestIndicator(int maliciousCallIdentificationRequestIndicator) {
		this.maliciousCallIdentificationRequestIndicator = maliciousCallIdentificationRequestIndicator;
	}

	public int getReserved() {
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

}
