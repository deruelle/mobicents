/**
 * Start time:18:44:18 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Start time:18:44:18 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class NetworkRoutingNumber extends AbstractNumber {

	public static final int _PARAMETER_CODE = 0x84;
	/**
	 * See Q.763 3.90 Numbering plan indicator : ISDN (Telephony) numbering plan
	 * (ITU-T Recommendation E.164)
	 */
	public static final int _NPI_ISDN_NP = 1;

	/**
	 * See Q.763 3.90 Nature of address indicator : network routing number in
	 * national (significant) number format (national use)
	 */
	public static final int _NAI_NRNI_NATIONAL_NF = 1;

	/**
	 * See Q.763 3.90 Nature of address indicator : network routing number in
	 * network specific number format (national use)
	 */
	public static final int _NAI_NRNI_NETWORK_SNF = 2;

	private int numberingPlanIndicator;
	private int natureOfAddressIndicator;

	public NetworkRoutingNumber(String address, int numberingPlanIndicator, int natureOfAddressIndicator) {
		super(address);
		this.numberingPlanIndicator = numberingPlanIndicator;
		this.natureOfAddressIndicator = natureOfAddressIndicator;
	}

	public NetworkRoutingNumber() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NetworkRoutingNumber(byte[] representation) {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	public NetworkRoutingNumber(ByteArrayInputStream bis) {
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

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.isup.parameters.AbstractNumber#encodeBody(java.io.
	 * ByteArrayOutputStream)
	 */
	@Override
	public int encodeBody(ByteArrayOutputStream bos) {

		return 0;
	}

	@Override
	public int decodeHeader(ByteArrayInputStream bis) throws IllegalArgumentException {

		int b = bis.read() & 0xff;

		this.oddFlag = (b & 0x80) >> 7;
		this.numberingPlanIndicator = (b & 0x70) >> 4;
		this.natureOfAddressIndicator = b & 0x0F;
		return 1;
	}

	@Override
	public int encodeHeader(ByteArrayOutputStream bos) {
		int b = 0;
		// Even is 000000000 == 0
		boolean isOdd = this.oddFlag == _FLAG_ODD;
		if (isOdd)
			b |= 0x80;

		b |= (this.numberingPlanIndicator & 0x07) << 4;
		b |= this.natureOfAddressIndicator & 0x0F;
		bos.write(b);

		return 1;
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

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
