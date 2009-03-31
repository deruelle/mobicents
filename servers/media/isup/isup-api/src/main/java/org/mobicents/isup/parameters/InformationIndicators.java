/**
 * Start time:14:36:25 2009-03-31<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:14:36:25 2009-03-31<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class InformationIndicators extends AbstractParameter {

	/**
	 * See Q.763 3.28
	 */
	public static final int _CALLING_PARTYS_ADDRESS_RESPONSE_INDICATOR_ADDRESS_NOT_INCLUDED = 0;

	/**
	 * See Q.763 3.28
	 */
	public static final int _CALLING_PARTYS_ADDRESS_RESPONSE_INDICATOR_ADDRESS_INCLUDED = 3;
	/**
	 * See Q.763 3.28
	 */
	public static final int _CALLING_PARTYS_ADDRESS_RESPONSE_INDICATOR_ADDRESS_NOT_AVAILABLE = 1;

	/**
	 * See Q.763 3.28
	 */
	public static final int _HOLD_PROVIDED_INDICATOR_NOT_PROVIDED = 0;
	/**
	 * See Q.763 3.28
	 */
	public static final int _HOLD_PROVIDED_INDICATOR_PROVIDED = 1;

	/**
	 * See Q.763 3.28
	 */
	public static final int _CHARGE_INFORMATION_INDICATOR_NOT_INCLUDED = 0;
	/**
	 * See Q.763 3.28
	 */
	public static final int _CHARGE_INFORMATION_INDICATOR_INCLUDED = 1;

	/**
	 * See Q.763 3.28
	 */
	public static final int _SOLICITED_INFORMATION_INDICATOR_SOLICITED = 0;
	/**
	 * See Q.763 3.28
	 */
	public static final int _SOLICITED_INFORMATION_INDICATOR_UNSOLICITED = 1;

	private int callingPartyAddressResponseIndicator = 0;
	private int holdProvidedIndicator = 0;
	private int callingPartysCategoryResponseIndicator = 0;
	private int chargeInformationResponseIndicator = 0;
	private int solicitedInformationIndicator = 0;
	// FIXME: should we care about it.
	private int reserved = 0;

	public InformationIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	public InformationIndicators(int callingPartyAddressResponseIndicator, int holdProvidedIndicator, int callingPartysCategoryResponseIndicator, int chargeInformationResponseIndicator,
			int solicitedInformationIndicator, int reserved) {
		super();
		this.callingPartyAddressResponseIndicator = callingPartyAddressResponseIndicator;
		this.holdProvidedIndicator = holdProvidedIndicator;
		this.callingPartysCategoryResponseIndicator = callingPartysCategoryResponseIndicator;
		this.chargeInformationResponseIndicator = chargeInformationResponseIndicator;
		this.solicitedInformationIndicator = solicitedInformationIndicator;
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

		this.reserved = b[1] & 0x0F;
		this.callingPartyAddressResponseIndicator = b[0] & 0x03;
		this.holdProvidedIndicator = (b[0] >> 2) & 0x01;
		this.callingPartysCategoryResponseIndicator = (b[0] >> 5) & 0x01;
		this.chargeInformationResponseIndicator = (b[0] >> 6) & 0x01;
		this.solicitedInformationIndicator = (b[0] >> 7) & 0x01;
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		int b1 = this.callingPartyAddressResponseIndicator & 0x03;
		b1 |= (this.holdProvidedIndicator & 0x01) << 2;
		b1 |= (this.callingPartysCategoryResponseIndicator & 0x01) << 5;
		b1 |= (this.chargeInformationResponseIndicator & 0x01) << 6;
		b1 |= (this.solicitedInformationIndicator & 0x01) << 7;

		int b2 = this.reserved & 0x0F;
		byte[] b = new byte[] { (byte) b1, (byte) b2 };
		return b;
	}

	public int getCallingPartyAddressResponseIndicator() {
		return callingPartyAddressResponseIndicator;
	}

	public void setCallingPartyAddressResponseIndicator(int callingPartyAddressResponseIndicator) {
		this.callingPartyAddressResponseIndicator = callingPartyAddressResponseIndicator;
	}

	public int getHoldProvidedIndicator() {
		return holdProvidedIndicator;
	}

	public void setHoldProvidedIndicator(int holdProvidedIndicator) {
		this.holdProvidedIndicator = holdProvidedIndicator;
	}

	public int getCallingPartysCategoryResponseIndicator() {
		return callingPartysCategoryResponseIndicator;
	}

	public void setCallingPartysCategoryResponseIndicator(int callingPartysCategoryResponseIndicator) {
		this.callingPartysCategoryResponseIndicator = callingPartysCategoryResponseIndicator;
	}

	public int getChargeInformationResponseIndicator() {
		return chargeInformationResponseIndicator;
	}

	public void setChargeInformationResponseIndicator(int chargeInformationResponseIndicator) {
		this.chargeInformationResponseIndicator = chargeInformationResponseIndicator;
	}

	public int getSolicitedInformationIndicator() {
		return solicitedInformationIndicator;
	}

	public void setSolicitedInformationIndicator(int solicitedInformationIndicator) {
		this.solicitedInformationIndicator = solicitedInformationIndicator;
	}

	public int getReserved() {
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

}
