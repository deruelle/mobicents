/**
 * Start time:14:54:53 2009-04-02<br>
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
 * Start time:14:54:53 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class RedirectingNumber extends AbstractNAINumber {

	public static final int _PARAMETER_CODE = 0x0B;
	protected int numberingPlanIndicator = 0;

	protected int addressRepresentationRestrictedIndicator = 0;

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

	
	
	
	public RedirectingNumber(int natureOfAddresIndicator, String address, int numberingPlanIndicator, int addressRepresentationRestrictedIndicator) {
		super(natureOfAddresIndicator, address);
		this.numberingPlanIndicator = numberingPlanIndicator;
		this.addressRepresentationRestrictedIndicator = addressRepresentationRestrictedIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.isup.parameters.AbstractNumber#decodeBody(java.io.
	 * ByteArrayInputStream)
	 */
	public RedirectingNumber(byte[] representation) throws ParameterRangeInvalidException {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	public RedirectingNumber(ByteArrayInputStream bis) throws ParameterRangeInvalidException {
		super(bis);
		// TODO Auto-generated constructor stub
	}

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
		int c = this.natureOfAddresIndicator << 4;
		c |= (this.addressRepresentationRestrictedIndicator << 2);

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
	/**
	 * <pre>
	 * a) Odd/even indicator: as for 3.9 a).
	 * 	b) Nature of address indicator: as for 3.10 b).
	 * 	c) Numbering plan indicator: as for 3.9 d).
	 * 	d) Address presentation restricted indicator: as for 3.10 e).
	 * 	e) Address signal: as for 3.10 g).
	 * 	f) Filler: as for 3.9 f).
	 * </pre>
	 */
}
