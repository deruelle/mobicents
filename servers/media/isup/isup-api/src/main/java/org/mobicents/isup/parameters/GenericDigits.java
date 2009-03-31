/**
 * Start time:12:24:47 2009-03-31<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:12:24:47 2009-03-31<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class GenericDigits extends AbstractParameter {

	/**
	 * See Q.763 3.24
	 */
	public static final int _ENCODING_SCHEME_BCD_EVEN = 0;

	/**
	 * See Q.763 3.24
	 */
	public static final int _ENCODING_SCHEME_BCD_ODD = 1;

	/**
	 * See Q.763 3.24
	 */
	public static final int _ENCODING_SCHEME_IA5 = 2;

	/**
	 * See Q.763 3.24
	 */
	public static final int _ENCODING_SCHEME_BINARY = 3;

	/**
	 * See Q.763 3.24
	 */
	public static final int _TYP_OF_DIGITS_ACCOUNT_CODE = 0;

	/**
	 * See Q.763 3.24
	 */
	public static final int _TYP_OF_DIGITS_AUTHORIZATION_CODE = 1;

	/**
	 * See Q.763 3.24
	 */
	public static final int _TYP_OF_DIGITS_PRIVATE_NETWORKING_TRAVELING_CLASS_MARK = 2;

	/**
	 * See Q.763 3.24
	 */
	public static final int _TYP_OF_DIGITS_BUSINESS_GROUP_COMMUNICATION_IDENTITY = 3;
	private int encodignScheme = 0;
	private int typeOfDigits = 0;
	private int[] digits = null;

	public GenericDigits(byte[] b) {
		super();
		decodeElement(b);
	}

	public GenericDigits(int encodignScheme, int typeOfDigits, int[] digits) {
		super();
		this.encodignScheme = encodignScheme;
		this.typeOfDigits = typeOfDigits;
		this.setDigits(digits);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length < 2) {
			throw new IllegalArgumentException("byte[] must not be null or has size less than 2");
		}
		this.typeOfDigits = b[0] & 0x1F;
		this.encodignScheme = (b[0] >> 5) & 0x07;
		this.digits = new int[b.length - 1];

		for (int index = 1; index < b.length; index++) {
			this.digits[index - 1] = b[index];
		}
		return 1 + this.digits.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		byte[] b = new byte[this.digits.length + 1];

		b[0] |= this.typeOfDigits & 0x1F;
		b[0] |= ((this.encodignScheme & 0x07) << 5);

		for (int index = 1; index < b.length; index++) {
			b[index] = (byte) this.digits[index - 1];
		}
		return b;

	}

	public int getEncodignScheme() {
		return encodignScheme;
	}

	public void setEncodignScheme(int encodignScheme) {
		this.encodignScheme = encodignScheme;
	}

	public int getTypeOfDigits() {
		return typeOfDigits;
	}

	public void setTypeOfDigits(int typeOfDigits) {
		this.typeOfDigits = typeOfDigits;
	}

	public int[] getDigits() {
		return digits;
	}

	public void setDigits(int[] digits) {
		if (digits == null)
			throw new IllegalArgumentException("Digits must not be null");
		this.digits = digits;
	}

}
