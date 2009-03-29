/**
 * Start time:17:36:23 2009-03-29<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Start time:17:36:23 2009-03-29<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author Oleg Kulikoff
 */
public class GenericNumber extends AbstractNumber {
	/**
	 * number qualifier indicator indicator value. See Q.763 - 3.26a
	 */
	public final static int _NQI_ADDITIONAL_CALLED_NUMBER = 1;
	/**
	 * number qualifier indicator indicator value. See Q.763 - 3.26a
	 */
	public final static int _NQI_ADDITIONAL_CONNECTED_NUMBER = 5;
	/**
	 * number qualifier indicator indicator value. See Q.763 - 3.26a
	 */
	public final static int _NQI_ADDITIONAL_CALLING_PARTY_NUMBER = 6;
	/**
	 * number qualifier indicator indicator value. See Q.763 - 3.26a
	 */
	public final static int _NQI_ADDITIONAL_ORIGINAL_CALLED_NUMBER = 7;
	/**
	 * number qualifier indicator indicator value. See Q.763 - 3.26a
	 */
	public final static int _NQI_ADDITIONAL_REDIRECTING_NUMBER = 8;
	/**
	 * number qualifier indicator indicator value. See Q.763 - 3.26a
	 */
	public final static int _NQI_ADDITIONAL_REDIRECTION_NUMBER = 9;

	/**
	 * numbering plan indicator indicator value. See Q.763 - 3.9d
	 */
	public final static int _NPI_ISDN = 1;
	/**
	 * numbering plan indicator indicator value. See Q.763 - 3.9d
	 */
	public final static int _NPI_DATA = 3;
	/**
	 * numbering plan indicator indicator value. See Q.763 - 3.9d
	 */
	public final static int _NPI_TELEX = 4;

	/**
	 * number incomplete indicator indicator value. See Q.763 - 3.10c
	 */
	public final static int _NI_INCOMPLETE = 1;
	/**
	 * number incomplete indicator indicator value. See Q.763 - 3.10c
	 */
	public final static int _NI_COMPLETE = 0;

	protected int numberQualifierIndicator = 0;
	protected int numberingPlanIndicator = 0;

	protected int addressRepresentationREstrictedIndicator = 0;
	protected int numberIncompleteIndicator = 0;
	protected int screeningIndicator = 0;

	public GenericNumber(int natureOfAddresIndicator, String address, int numberQualifierIndicator, int numberingPlanIndicator, int addressRepresentationREstrictedIndicator,
			int numberIncompleteIndicator, int screeningIndicator) {
		super(natureOfAddresIndicator, address);
		this.numberQualifierIndicator = numberQualifierIndicator;
		this.numberingPlanIndicator = numberingPlanIndicator;
		this.addressRepresentationREstrictedIndicator = addressRepresentationREstrictedIndicator;
		this.numberIncompleteIndicator = numberIncompleteIndicator;
		this.screeningIndicator = screeningIndicator;
	}

	public GenericNumber(byte[] representation) {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	public GenericNumber(ByteArrayInputStream bis) {
		super(bis);
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
		int b = bis.read() & 0xff;

		this.numberIncompleteIndicator = (b & 0x80) >> 7;
		this.numberingPlanIndicator = (b & 0x70) >> 4;
		this.addressRepresentationREstrictedIndicator = (b & 0x0c) >> 2;
		this.screeningIndicator = (b & 0x03);
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.isup.parameters.AbstractNumber#encodeBody(java.io.
	 * ByteArrayOutputStream)
	 */
	@Override
	public int encodeBody(ByteArrayOutputStream bos) {
		int c = this.natureOfAddresIndicator << 4;
		c |= (this.numberIncompleteIndicator << 7);
		c |= (this.addressRepresentationREstrictedIndicator << 2);
		c |= (this.screeningIndicator);
		bos.write(c);
		return 1;
	}

	@Override
	public int decodeHeader(ByteArrayInputStream bis) throws IllegalArgumentException {
		this.numberQualifierIndicator = bis.read() & 0xff;
		return super.decodeHeader(bis) + 1;
	}

	@Override
	public int encodeHeader(ByteArrayOutputStream bos) {
		bos.write(this.numberQualifierIndicator);
		return super.encodeHeader(bos) + 1;
	}

	public int getNumberQualifierIndicator() {
		return numberQualifierIndicator;
	}

	public void setNumberQualifierIndicator(int numberQualifierIndicator) {
		this.numberQualifierIndicator = numberQualifierIndicator;
	}

	public int getNumberingPlanIndicator() {
		return numberingPlanIndicator;
	}

	public void setNumberingPlanIndicator(int numberingPlanIndicator) {
		this.numberingPlanIndicator = numberingPlanIndicator;
	}

	public int getAddressRepresentationREstrictedIndicator() {
		return addressRepresentationREstrictedIndicator;
	}

	public void setAddressRepresentationREstrictedIndicator(int addressRepresentationREstrictedIndicator) {
		this.addressRepresentationREstrictedIndicator = addressRepresentationREstrictedIndicator;
	}

	public int getNumberIncompleteIndicator() {
		return numberIncompleteIndicator;
	}

	public void setNumberIncompleteIndicator(int numberIncompleteIndicator) {
		this.numberIncompleteIndicator = numberIncompleteIndicator;
	}

	public int getScreeningIndicator() {
		return screeningIndicator;
	}

	public void setScreeningIndicator(int screeningIndicator) {
		this.screeningIndicator = screeningIndicator;
	}

}
