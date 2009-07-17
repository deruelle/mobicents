/**
 * Start time:15:16:34 2009-04-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Start time:15:16:34 2009-04-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class TerminatingNetworkRoutingNumber extends AbstractNumber {

	/**
	 * see Q.763 3.66 c4 : subscriber number (national use)
	 */
	public static final int _NAI_SN = 1;
	/**
	 * see Q.763 3.66 c4 : unknown (national use)
	 */
	public static final int _NAI_UNKNOWN = 2;
	/**
	 * see Q.763 3.66 c4 : national (significant) number
	 */
	public static final int _NAI_NATIONAL_SN = 3;
	/**
	 * see Q.763 3.66 c4 : international number
	 */
	public static final int _NAI_IN = 4;
	/**
	 * see Q.763 3.66 c4 : network specific number
	 */
	public static final int _NAI_NETWORK_SN = 5;

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

	public static final int _PARAMETER_CODE = 0;
	// FIXME: shoudl we add max octets ?
	private int tnrnLengthIndicator;
	private int numberingPlanIndicator;
	private int natureOfAddressIndicator;

	public TerminatingNetworkRoutingNumber() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TerminatingNetworkRoutingNumber(byte[] representation) {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	public TerminatingNetworkRoutingNumber(ByteArrayInputStream bis) {
		super(bis);
		// TODO Auto-generated constructor stub
	}

	

	public TerminatingNetworkRoutingNumber(int numberingPlanIndicator) {
		super();
		this.setNumberingPlanIndicator(numberingPlanIndicator);
		this.tnrnLengthIndicator = 0;
	}

	public TerminatingNetworkRoutingNumber(int numberingPlanIndicator, int natureOfAddressIndicator) {
		super();
		this.setNumberingPlanIndicator(numberingPlanIndicator);
		this.setNatureOfAddressIndicator(natureOfAddressIndicator);
		this.tnrnLengthIndicator = 1;
	}
	
	public TerminatingNetworkRoutingNumber(String address,int numberingPlanIndicator, int natureOfAddressIndicator) {
		super();
		this.setNumberingPlanIndicator(numberingPlanIndicator);
		this.setNatureOfAddressIndicator(natureOfAddressIndicator);
		this.setAddress(address);
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
		if (bis.available() == 0) {
			throw new IllegalArgumentException("No more data to read.");
		}
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
		if (this.tnrnLengthIndicator> 0) {
			if (bis.available() == 0) {
				throw new IllegalArgumentException("No more data to read.");
			}
			this.setNatureOfAddressIndicator(bis.read()) ;
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
		if (this.tnrnLengthIndicator-1> 0) {
			if (bis.available() == 0) {
				throw new IllegalArgumentException("No more data to read.");
			}
			return super.decodeDigits(bis, this.tnrnLengthIndicator-1);
		} else {
			return 0;
		}
		
	}

	@Override
	public int encodeDigits(ByteArrayOutputStream bos) {
		if(this.tnrnLengthIndicator-1>0)
			return super.encodeDigits(bos);
		else
			return 0;
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
		
		if(this.tnrnLengthIndicator ==9 && !isOddFlag())
		{
			//we allow only odd! digits count in this case
			throw new IllegalArgumentException("To many digits. Maximum number of digits is 15 for tnr length of 9.");
		}
	}

	public int getNumberingPlanIndicator() {
		return numberingPlanIndicator;
	}

	public void setNumberingPlanIndicator(int numberingPlanIndicator) {
		this.numberingPlanIndicator = numberingPlanIndicator & 0x07;
	}

	public int getNatureOfAddressIndicator() {
		return natureOfAddressIndicator;
	}

	public void setNatureOfAddressIndicator(int natureOfAddressIndicator) {
		this.natureOfAddressIndicator = natureOfAddressIndicator & 0x7F;
	}

	public int getTnrnLengthIndicator() {
		return tnrnLengthIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
