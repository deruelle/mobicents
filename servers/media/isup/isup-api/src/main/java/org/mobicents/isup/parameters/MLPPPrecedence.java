/**
 * Start time:08:42:25 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:08:42:25 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class MLPPPrecedence extends AbstractParameter {

	
	public static final int _PARAMETER_CODE = 0x3A;
	/**
	 * See Q.763 3.34 LFB (Look ahead for busy) : LFB allowed
	 */
	public static final int _LFB_INDICATOR_ALLOWED = 0;
	/**
	 * See Q.763 3.34 LFB (Look ahead for busy) : path reserved (national use)
	 */
	public static final int _LFB_INDICATOR_PATH_RESERVED = 1;
	/**
	 * See Q.763 3.34 LFB (Look ahead for busy) : LFB not allowed
	 */
	public static final int _LFB_INDICATOR_NOT_ALLOWED = 2;

	/**
	 * See Q.763 3.34 Precedence level : flash override
	 */
	public static final int _PLI_FLASH_OVERRIDE = 0;

	/**
	 * See Q.763 3.34 Precedence level : flash
	 */
	public static final int _PLI_FLASH = 1;
	/**
	 * See Q.763 3.34 Precedence level : immediate
	 */
	public static final int _PLI_IMMEDIATE = 2;
	/**
	 * See Q.763 3.34 Precedence level : priority
	 */
	public static final int _PLI_PRIORITY = 3;

	/**
	 * See Q.763 3.34 Precedence level : routine
	 */
	public static final int _PLI_ROUTINE = 4;

	private int lfb;
	private int precedenceLevel ;
	private int mllpServiceDomain ;
	// FIXME: ensure zero in first digit.?
	private byte[] niDigits ;

	public MLPPPrecedence(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	public MLPPPrecedence(byte lfb, byte precedenceLevel, int mllpServiceDomain, byte[] niDigits) {
		super();
		this.lfb = lfb;
		this.precedenceLevel = precedenceLevel;
		this.mllpServiceDomain = mllpServiceDomain;
		setNiDigits(niDigits);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length != 6) {
			throw new ParameterRangeInvalidException("byte[] must  not be null and length must  be 6");
		}

		this.precedenceLevel = (byte) (b[0] & 0x0F);
		this.lfb = (byte) ((b[0] >> 5) & 0x03);
		byte v = 0;
		this.niDigits = new byte[4];
		for (int i = 0; i < 2; i++) {
			v = 0;
			v = b[i + 1];
			this.niDigits[i * 2] = (byte) (v & 0x0F);
			this.niDigits[i * 2 + 1] = (byte) ((v >> 4) & 0x0F);
		}

		this.mllpServiceDomain = b[3] << 16;
		this.mllpServiceDomain |= b[4] << 8;
		this.mllpServiceDomain |= b[5];
		return 6;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		byte[] b = new byte[6];
		b[0] = (byte) ((this.lfb & 0x03) << 5);
		b[0] |= this.precedenceLevel & 0x0F;
		byte v = 0;
		for (int i = 0; i < 2; i++) {
			v = 0;

			v |= (this.niDigits[i * 2] & 0x0F) << 4;
			v |= (this.niDigits[i * 2 + 1] & 0x0F);

			b[i + 1] = v;
		}

		b[3] = (byte) (this.mllpServiceDomain >> 16);
		b[4] = (byte) (this.mllpServiceDomain >> 8);
		b[5] = (byte) this.mllpServiceDomain;
		return b;
	}

	public byte getLfb() {
		return (byte) lfb;
	}

	public void setLfb(byte lfb) {
		this.lfb = lfb;
	}

	public byte getPrecedenceLevel() {
		return (byte) precedenceLevel;
	}

	public void setPrecedenceLevel(byte precedenceLevel) {
		this.precedenceLevel = precedenceLevel;
	}

	public int getMllpServiceDomain() {
		return mllpServiceDomain;
	}

	public void setMllpServiceDomain(int mllpServiceDomain) {
		this.mllpServiceDomain = mllpServiceDomain;
	}

	public byte[] getNiDigits() {
		return niDigits;
	}

	public void setNiDigits(byte[] niDigits) {
		if (niDigits == null || niDigits.length != 4) {
			throw new IllegalArgumentException();
		}
		this.niDigits = niDigits;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
