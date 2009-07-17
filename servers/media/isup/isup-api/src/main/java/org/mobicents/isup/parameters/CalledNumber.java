/**
 * Start time:13:05:28 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:13:05:28 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class CalledNumber extends AbstractNAINumber {
	public static final int _PARAMETER_CODE = 0;
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

	protected int numberingPlanIndicator;

	protected int addressRepresentationRestrictedIndicator;

	/**
	 * address presentation restricted indicator indicator value. See Q.763 -
	 * 3.10e
	 */
	public final static int _APRI_ALLOWED = 0;

	/**
	 * address presentation restricted indicator indicator value. See Q.763 -
	 * 3.10e
	 */
	public final static int _APRI_RESTRICTED = 1;

	/**
	 * address presentation restricted indicator indicator value. See Q.763 -
	 * 3.10e
	 */
	public final static int _APRI_NOT_AVAILABLE = 2;

	/**
	 * address presentation restricted indicator indicator value. See Q.763 -
	 * 3.16d
	 */
	public final static int _APRI_SPARE = 3;

	public CalledNumber(byte[] representation) throws ParameterRangeInvalidException {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	public CalledNumber(ByteArrayInputStream bis) throws ParameterRangeInvalidException {
		super(bis);
		// TODO Auto-generated constructor stub
	}

	public CalledNumber(int natureOfAddresIndicator, String address, int numberingPlanIndicator, int addressRepresentationREstrictedIndicator) {
		super(natureOfAddresIndicator, address);
		this.numberingPlanIndicator = numberingPlanIndicator;
		this.addressRepresentationRestrictedIndicator = addressRepresentationREstrictedIndicator;
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

		this.numberingPlanIndicator = (b & 0x70) >> 4;
		this.addressRepresentationRestrictedIndicator = (b & 0x0c) >> 2;

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
		int c = (this.natureOfAddresIndicator & 0x07) << 4;
		c |= ((this.addressRepresentationRestrictedIndicator & 0x03) << 2);

		bos.write(c);
		return 1;
	}

	public int getNumberingPlanIndicator() {
		return numberingPlanIndicator;
	}

	public void setNumberingPlanIndicator(int numberingPlanIndicator) {
		this.numberingPlanIndicator = numberingPlanIndicator;
	}

	public int getAddressRepresentationRestrictedIndicator() {
		return addressRepresentationRestrictedIndicator;
	}

	public void setAddressRepresentationRestrictedIndicator(int addressRepresentationREstrictedIndicator) {
		this.addressRepresentationRestrictedIndicator = addressRepresentationREstrictedIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
