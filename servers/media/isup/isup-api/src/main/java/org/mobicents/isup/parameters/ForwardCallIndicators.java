/**
 * Start time:11:58:42 2009-03-31<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:11:58:42 2009-03-31<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class ForwardCallIndicators extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x07;
	private final static int _TURN_ON = 1;
	private final static int _TURN_OFF = 0;

	private boolean nationalCallIdentificator = false;
	private int endToEndMethodIndicator = 0;
	private boolean interworkingIndicator = false;
	private boolean endToEndInformationIndicator = false;
	private boolean isdnUserPartIndicator = false;
	private int isdnUserPartReferenceIndicator = 0;
	private int sccpMethodIndicator = 0;
	private boolean isdnAccessIndicator = false;

	/**
	 * See q.763 3.5 National/international call indicator (Note 1) : call to be
	 * treated as a national call
	 */
	public static final boolean _NCI_NATIONAL_CALL = false;

	/**
	 * See q.763 3.5 National/international call indicator (Note 1) : call to be
	 * treated as an international call
	 */
	public static final boolean _NCI_INTERNATIONAL_CALL = true;

	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2) : no end-to-end method
	 * available (only link-by-link method available)
	 */
	public static final int _ETEMI_NOMETHODAVAILABLE = 0;
	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2) : pass-along method
	 * available (national use)
	 */
	public static final int _ETEMI_PASSALONG = 1;
	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2) : SCCP method
	 * available
	 */
	public static final int _ETEMI_SCCP = 2;
	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2) : pass-along and SCCP
	 * methods available (national use)
	 */
	public static final int _ETEMI_SCCP_AND_PASSALONG = 3;
	/**
	 * See q.763 3.5 End-to-end information indicator (national use) (Note 2) :
	 * no end-to-end information available
	 */
	public static final boolean _ETEII_NOT_AVAILABLE = false;
	/**
	 * See q.763 3.5 End-to-end information indicator (national use) (Note 2) :
	 * end-to-end information available
	 */
	public static final boolean _ETEII_AVAILABLE = true;
	/**
	 * See q.763 3.5 Interworking indicator (Note 2)
	 */
	public static final boolean _II_NOT_ENCOUTNERED = false;
	/**
	 * See q.763 3.5 Interworking indicator (Note 2)
	 */
	public static final boolean _II_ENCOUTNERED = true;
	/**
	 * See q.763 3.5 ISDN access indicator : originating access non-ISDN
	 */
	public static final boolean _ISDN_AI_OA_N_ISDN = false;
	/**
	 * See q.763 3.5 ISDN access indicator : originating access ISDN
	 */
	public static final boolean _ISDN_AI_OA_ISDN = true;

	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) : no indication
	 */
	public static final int _SCCP_MI_NOINDICATION = 0;
	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) : connectionless method
	 * available (national use)
	 */
	public static final int _SCCP_MI_CONNECTIONLESS = 1;
	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) : connection oriented method
	 * available
	 */
	public static final int _SCCP_MI_CONNECTION_ORIENTED = 2;
	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) : connectionless and
	 * connection oriented methods available (national use)
	 */
	public static final int _SCCP_MI_CL_AND_CO = 3;

	/**
	 * See q.763 3.23 ISDN user part indicator (Note 2) : ISDN user part not
	 * used all the way
	 */
	public static final boolean _ISDN_UPI_NOTUSED = false;
	/**
	 * See q.763 3.23 ISDN user part indicator (Note 2) : ISDN user part used
	 * all the way
	 */
	public static final boolean _ISDN_UPI_USED = true;

	/**
	 * See q.763 3.23 ISDN user part preference indicator : ISDN user part
	 * preferred all the way
	 */
	public static final int _ISDN_UPRI_PREFERED_ALL_THE_WAY = 0;

	/**
	 * See q.763 3.23 ISDN user part preference indicator : ISDN user part not
	 * required all the way
	 */
	public static final int _ISDN_UPRI_NRATW = 1;

	/**
	 * See q.763 3.23 ISDN user part preference indicator : ISDN user part
	 * required all the way
	 */
	public static final int _ISDN_UPRI_RATW = 2;

	public ForwardCallIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	public ForwardCallIndicators(boolean nationalCallIdentificator, int endToEndMethodIndicator, boolean interworkingIndicator, boolean endToEndInformationIndicator, boolean isdnUserPartIndicator,
			int isdnUserPartReferenceIndicator, int sccpMethodIndicator, boolean isdnAccessIndicator) {
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

		this.nationalCallIdentificator = (v & 0x01) == _TURN_ON;
		this.endToEndMethodIndicator = (v >> 1) & 0x03;
		this.interworkingIndicator = ((v >> 3) & 0x01) == _TURN_ON;
		this.endToEndInformationIndicator = ((v >> 4) & 0x01) == _TURN_ON;
		this.isdnUserPartIndicator = ((v >> 5) & 0x01) == _TURN_ON;
		this.isdnUserPartReferenceIndicator = (v >> 6) & 0x03;

		v = b[1];

		this.isdnAccessIndicator = (v & 0x01) == _TURN_ON;
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

		b[0] |= this.nationalCallIdentificator ? _TURN_ON : _TURN_OFF;
		b[0] |= (this.endToEndMethodIndicator & 0x03) << 1;
		b[0] |= (this.interworkingIndicator ? _TURN_ON : _TURN_OFF) << 3;
		b[0] |= (this.endToEndInformationIndicator ? _TURN_ON : _TURN_OFF) << 4;
		b[0] |= (this.isdnUserPartIndicator ? _TURN_ON : _TURN_OFF) << 5;
		b[0] |= (this.isdnUserPartReferenceIndicator & 0x03) << 6;

		b[1] = (byte) (this.isdnAccessIndicator ? _TURN_ON : _TURN_OFF);
		// FIXME should we allow here older bytes to pass
		b[1] |= (this.sccpMethodIndicator & 0x03) << 1;
		return b;
	}

	public boolean isNationalCallIdentificator() {
		return nationalCallIdentificator;
	}

	public void setNationalCallIdentificator(boolean nationalCallIdentificator) {
		this.nationalCallIdentificator = nationalCallIdentificator;
	}

	public int getEndToEndMethodIndicator() {
		return endToEndMethodIndicator;
	}

	public void setEndToEndMethodIndicator(int endToEndMethodIndicator) {
		this.endToEndMethodIndicator = endToEndMethodIndicator;
	}

	public boolean isInterworkingIndicator() {
		return interworkingIndicator;
	}

	public void setInterworkingIndicator(boolean interworkingIndicator) {
		this.interworkingIndicator = interworkingIndicator;
	}

	public boolean isEndToEndInformationIndicator() {
		return endToEndInformationIndicator;
	}

	public void setEndToEndInformationIndicator(boolean endToEndInformationIndicator) {
		this.endToEndInformationIndicator = endToEndInformationIndicator;
	}

	public boolean isIsdnUserPartIndicator() {
		return isdnUserPartIndicator;
	}

	public void setIsdnUserPartIndicator(boolean isdnUserPartIndicator) {
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

	public boolean isIsdnAccessIndicator() {
		return isdnAccessIndicator;
	}

	public void setIsdnAccessIndicator(boolean isdnAccessIndicator) {
		this.isdnAccessIndicator = isdnAccessIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
