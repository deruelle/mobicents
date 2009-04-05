/**
 * Start time:15:16:34 2009-04-04<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Start time:15:16:34 2009-04-04<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class TerminatingNetworkRoutingNumber extends AbstractNumber {

	// FIXME: shoudl we add max octets ?
	private int tnrnLengthIndicator = 0;
	private int numberingPlanIndicator = 0;
	private int natureOfAddressIndicator = 0;

	public TerminatingNetworkRoutingNumber(byte[] representation) {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	public TerminatingNetworkRoutingNumber(ByteArrayInputStream bis) {
		super(bis);
		// TODO Auto-generated constructor stub
	}

	public TerminatingNetworkRoutingNumber(String address, int tnrnLengthIndicator, int numberingPlanIndicator, int natureOfAddressIndicator) {
		super(address);
		this.tnrnLengthIndicator = tnrnLengthIndicator;
		this.numberingPlanIndicator = numberingPlanIndicator;
		this.natureOfAddressIndicator = natureOfAddressIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		return super.decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return super.encodeElement();
	}

	@Override
	public int decodeHeader(ByteArrayInputStream bis) throws IllegalArgumentException {
		int b = bis.read() & 0xff;

		this.oddFlag = (b & 0x80) >> 7;
		this.tnrnLengthIndicator = b & 0x0F;
		this.numberingPlanIndicator = (b >> 4) & 0x07;
		return 1;
	}

	@Override
	public int encodeHeader(ByteArrayOutputStream bos) {
		int b = 0;
		// Even is 000000000 == 0
		boolean isOdd = this.oddFlag == _FLAG_ODD;
		if (isOdd)
			b |= 0x80;
		b |= this.tnrnLengthIndicator & 0x0F;
		b |= (this.numberingPlanIndicator & 0x07) << 4;
		bos.write(b);
		return 1;
	}

	@Override
	public int decodeBody(ByteArrayInputStream bis) throws IllegalArgumentException {
		if (this.tnrnLengthIndicator > 0) {
			this.natureOfAddressIndicator = bis.read();
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int encodeBody(ByteArrayOutputStream bos) {

		if (this.tnrnLengthIndicator > 0) {
			bos.write(this.natureOfAddressIndicator);
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int decodeDigits(ByteArrayInputStream bis) throws IllegalArgumentException {
		return super.decodeDigits(bis, this.tnrnLengthIndicator - 1);
	}

	/**
	 * This method is used in encodeElement. Encodes digits part. This is
	 * because
	 * 
	 * @param bos
	 *            - where digits will be encoded
	 * @return - number of bytes encoded
	 * 
	 */
	public int encodeDigits(ByteArrayOutputStream bos) {
		boolean isOdd = this.oddFlag == _FLAG_ODD;

		byte b = 0;
		int count = (!isOdd) ? address.length() : address.length() - 1;
		int bytesCount = 0;
		for (int i = 0; i < count - 1; i += 2) {
			String ds1 = address.substring(i, i + 1);
			String ds2 = address.substring(i + 1, i + 2);

			int d1 = Integer.parseInt(ds1, 16);
			int d2 = Integer.parseInt(ds2, 16);

			b = (byte) (d2 << 4 | d1);
			bos.write(b);
			bytesCount++;
		}

		if (isOdd) {
			String ds1 = address.substring(count, count + 1);
			int d = Integer.parseInt(ds1);

			b = (byte) (d & 0x0f);
			bos.write(b);
			bytesCount++;
		}

		return bytesCount;
	}

	@Override
	public void setAddress(String address) {
		// TODO Auto-generated method stub
		super.setAddress(address);
		int l = super.address.length();
		// +1 for NAI
		this.tnrnLengthIndicator = l / 2 + l % 2 + 1;
		if (tnrnLengthIndicator > 9) {
			throw new IllegalArgumentException("Maximum octets for this parameter in digits part is 8.");
			// FIXME: add check for digit (max 7 ?)
		}
	}

	public int getNumberingPlanIndicator() {
		return numberingPlanIndicator;
	}

	public void setNumberingPlanIndicator(int numberingPlanIndicator) {
		this.numberingPlanIndicator = numberingPlanIndicator;
	}

	public int getNatureOfAddressIndicator() {
		return natureOfAddressIndicator;
	}

	public void setNatureOfAddressIndicator(int natureOfAddressIndicator) {
		this.natureOfAddressIndicator = natureOfAddressIndicator;
	}

	public int getTnrnLengthIndicator() {
		return tnrnLengthIndicator;
	}

}
