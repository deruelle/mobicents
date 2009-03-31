/**
 * Start time:11:58:42 2009-03-31<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:11:58:42 2009-03-31<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class ForwardCallIndicators extends AbstractParameter {

	private int nationalCallIdentificator = 0;
	private int endToEndMethodIndicator = 0;
	private int interworkingIndicator = 0;
	private int endToEndInformationIndicator = 0;
	private int isdnUserPartIndicator = 0;
	private int isdnUserPartReferenceIndicator = 0;
	private int sccpMethodIndicator = 0;
	private int isdnAccessIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _NATIONAL_CALL_INDICATOR_NATIONAL_CALL = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _NATIONAL_CALL_INDICATOR_INTERNATIONAL_CALL = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_METHOD_INDICATOR_NOMETHODAVAILABLE = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_METHOD_INDICATOR_PASSALONG = 1;
	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_METHOD_INDICATOR_SCCP = 2;
	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_METHOD_INDICATOR_SCCP_AND_PASSALONG = 3;
	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_INFORMATION_INDICATOR_NOT_AVAILABLE = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_INFORMATION_INDICATOR_AVAILABLE = 1;
	/**
	 * See q.763 3.5
	 */
	public static final int _INTERWORKING_INDICATOR_NOT_ENCOUTNERED = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _INTERWORKING_INDICATOR_ENCOUTNERED = 1;
	/**
	 * See q.763 3.5
	 */
	public static final int _ISDN_ACCESS_INDICATOR_TERMINATING_ACCESS_NOT_ISDN = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _ISDN_ACCESS_INDICATOR_TERMINATING_ACCESS_ISDN = 1;

	/**
	 * See q.763 3.5
	 */
	public static final int _SCCP_METHOD_INDICATOR_NOINDICATION = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _SCCP_METHOD_INDICATOR_CONNECTIONLESS = 1;
	/**
	 * See q.763 3.5
	 */
	public static final int _SCCP_METHOD_INDICATOR_CONNECTION_ORIENTED = 2;
	/**
	 * See q.763 3.5
	 */
	public static final int _SCCP_METHOD_INDICATOR_CONNECTIONLESS_AND_CONNECTION_ORIENTED = 3;

	/**
	 * See q.763 3.23
	 */
	public static final int _ISDN_USER_PART_INDICATOR_NOTUSED = 0;
	/**
	 * See q.763 3.23
	 */
	public static final int _ISDN_USER_PART_INDICATOR_USED = 1;

	/**
	 * See q.763 3.23
	 */
	public static final int _ISDN_USER_PART_REFERENCE_INDICATOR_PREFERED_ALL_THE_WAY = 0;

	/**
	 * See q.763 3.23
	 */
	public static final int _ISDN_USER_PART_REFERENCE_INDICATOR_NOT_REQUIRED_ALL_THE_WAY = 1;

	/**
	 * See q.763 3.23
	 */
	public static final int _ISDN_USER_PART_REFERENCE_INDICATOR_REQUIRED_ALL_THE_WAY = 2;

	public ForwardCallIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	public ForwardCallIndicators(int nationalCallIdentificator, int endToEndMethodIndicator, int interworkingIndicator, int endToEndInformationIndicator, int isdnUserPartIndicator,
			int isdnUserPartReferenceIndicator, int sccpMethodIndicator, int isdnAccessIndicator) {
		super();
		this.nationalCallIdentificator = nationalCallIdentificator;
		this.endToEndMethodIndicator = endToEndMethodIndicator;
		this.interworkingIndicator = interworkingIndicator;
		this.endToEndInformationIndicator = endToEndInformationIndicator;
		this.isdnUserPartIndicator = isdnUserPartIndicator;
		this.isdnUserPartReferenceIndicator = isdnUserPartReferenceIndicator;
		this.sccpMethodIndicator = sccpMethodIndicator;
		this.isdnAccessIndicator = isdnAccessIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 2) {
			throw new IllegalArgumentException("byte[] must not be null or have different size than 2");
		}
		int v = 0;

		v = b[0];

		this.nationalCallIdentificator = v & 0x01;
		this.endToEndMethodIndicator = (v >> 1) & 0x03;
		this.interworkingIndicator = (v >> 3) & 0x01;
		this.endToEndInformationIndicator = (v >> 4) & 0x01;
		this.isdnUserPartIndicator = (v >> 5) & 0x01;
		this.isdnUserPartReferenceIndicator = (v >> 6) & 0x03;

		v = b[1];

		this.isdnAccessIndicator = v & 0x01;
		// FIXME: should we allow older bytes to pass ?
		this.sccpMethodIndicator = (v >> 1) & 0x03;

		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		byte[] b = new byte[2];

		b[0] |= this.nationalCallIdentificator & 0x01;
		b[0] |= (this.endToEndMethodIndicator & 0x03) << 1;
		b[0] |= (this.interworkingIndicator & 0x01) << 3;
		b[0] |= (this.endToEndInformationIndicator & 0x01) << 4;
		b[0] |= (this.isdnUserPartIndicator & 0x01) << 5;
		b[0] |= (this.isdnUserPartReferenceIndicator & 0x01) << 6;

		b[1] = (byte) (this.isdnAccessIndicator & 0x01);
		// FIXME should we allow here older bytes to pass
		b[1] |= (this.sccpMethodIndicator & 0x03) << 1;
		return b;
	}

	public int getNationalCallIdentificator() {
		return nationalCallIdentificator;
	}

	public void setNationalCallIdentificator(int nationalCallIdentificator) {
		this.nationalCallIdentificator = nationalCallIdentificator;
	}

	public int getEndToEndMethodIndicator() {
		return endToEndMethodIndicator;
	}

	public void setEndToEndMethodIndicator(int endToEndMethodIndicator) {
		this.endToEndMethodIndicator = endToEndMethodIndicator;
	}

	public int getInterworkingIndicator() {
		return interworkingIndicator;
	}

	public void setInterworkingIndicator(int interworkingIndicator) {
		this.interworkingIndicator = interworkingIndicator;
	}

	public int getEndToEndInformationIndicator() {
		return endToEndInformationIndicator;
	}

	public void setEndToEndInformationIndicator(int endToEndInformationIndicator) {
		this.endToEndInformationIndicator = endToEndInformationIndicator;
	}

	public int getIsdnUserPartIndicator() {
		return isdnUserPartIndicator;
	}

	public void setIsdnUserPartIndicator(int isdnUserPartIndicator) {
		this.isdnUserPartIndicator = isdnUserPartIndicator;
	}

	public int getIsdnUserPartReferenceIndicator() {
		return isdnUserPartReferenceIndicator;
	}

	public void setIsdnUserPartReferenceIndicator(int isdnUserPartReferenceIndicator) {
		this.isdnUserPartReferenceIndicator = isdnUserPartReferenceIndicator;
	}

	public int getSccpMethodIndicator() {
		return sccpMethodIndicator;
	}

	public void setSccpMethodIndicator(int sccpMethodIndicator) {
		this.sccpMethodIndicator = sccpMethodIndicator;
	}

	public int getIsdnAccessIndicator() {
		return isdnAccessIndicator;
	}

	public void setIsdnAccessIndicator(int isdnAccessIndicator) {
		this.isdnAccessIndicator = isdnAccessIndicator;
	}

}
