/**
 * Start time:16:42:16 2009-04-05<br>
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
 * Start time:16:42:16 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CalledDirectoryNumber extends AbstractNAINumber {

	// FIXME: whats the code ?
	public static final int _PARAMETER_CODE = 0x7D;

	/**
	 * See Q.763 Numbering plan indicator : ISDN (Telephony) numbering plan
	 * (ITU-T Recommendation E.164)
	 */
	public static final int _NPI_ISDN_NP = 1;

	/**
	 * See Q.763 Internal network number indicator (INN) : reserved
	 */
	public static final int _INNI_RESERVED = 0;

	/**
	 * See Q.763 Internal network number indicator (INN) : routing to internal
	 * network number not allowed
	 */
	public static final int _INNI_RTINNNA = 1;

	protected int numberingPlanIndicator;

	protected int internalNetworkNumberIndicator;

	/**
	 * @param representation
	 */
	public CalledDirectoryNumber(byte[] representation) {
		super(representation);
		// TODO Auto-generated constructor stub
		getNumberingPlanIndicator();
	}

	/**
	 * 
	 * @param bis
	 */
	public CalledDirectoryNumber(ByteArrayInputStream bis) {
		super(bis);
		// TODO Auto-generated constructor stub
	}

	public CalledDirectoryNumber(int natureOfAddresIndicator, String address, int numberingPlanIndicator, int internalNetworkNumberIndicator) {
		super(natureOfAddresIndicator, address);
		this.numberingPlanIndicator = numberingPlanIndicator;
		this.internalNetworkNumberIndicator = internalNetworkNumberIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.isup.parameters.AbstractNumber#decodeBody(java.io.
	 * ByteArrayInputStream)
	 */
	@Override
	public int decodeBody(ByteArrayInputStream bis) throws IllegalArgumentException {
		if (bis.available() == 0) {
			throw new IllegalArgumentException("No more data to read.");
		}
		int b = bis.read() & 0xff;

		this.internalNetworkNumberIndicator = (b & 0x80) >> 7;
		this.numberingPlanIndicator = (b >> 4) & 0x07;

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
		int c = (this.numberingPlanIndicator & 0x07) << 4;
		c |= (this.internalNetworkNumberIndicator << 7);
		bos.write(c);
		return 1;
	}

	public int getNumberingPlanIndicator() {

		return this.numberingPlanIndicator;
	}

	public void setNumberingPlanIndicator(int numberingPlanIndicator) {

		this.numberingPlanIndicator = numberingPlanIndicator;

	}

	public int getInternalNetworkNumberIndicator() {
		return internalNetworkNumberIndicator;
	}

	public void setInternalNetworkNumberIndicator(int internalNetworkNumberIndicator) {
		this.internalNetworkNumberIndicator = internalNetworkNumberIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
