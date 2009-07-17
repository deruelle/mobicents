/**
 * Start time:13:31:04 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Start time:13:31:04 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CallingPartyCategory extends AbstractParameter {
	public static final int _PARAMETER_CODE = 0x09;
	/**
	 * See Q.763 3.11
	 */
	public static final byte _CATEGORY_UNKNOWN = 0;

	/**
	 * See Q.763 3.11 operator, language French
	 */
	public static final byte _CATEGORY_OL_FRENCH = 1;

	/**
	 * See Q.763 3.11 operator, language English
	 */
	public static final byte _CATEGORY_OL_ENGLISH = 2;

	/**
	 * See Q.763 3.11 operator, language German
	 */
	public static final byte _CATEGORY_OL_GERMAN = 3;

	/**
	 * See Q.763 3.11 operator, language Russian
	 */
	public static final byte _CATEGORY_OL_RUSSIAN = 4;

	/**
	 * See Q.763 3.11 operator, language Spanish
	 */
	public static final byte _CATEGORY_OL_SPANISH = 5;
	private byte callingPartyCategory = 0;

	public CallingPartyCategory(byte callingPartyCategory) {
		super();
		this.callingPartyCategory = callingPartyCategory;
	}

	public CallingPartyCategory(byte[] representation) {
		super();
		this.decodeElement(representation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must not be null or have different size than 1");
		}
		this.callingPartyCategory = b[0];

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		return new byte[] { this.callingPartyCategory };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.ISUPComponent#encodeElement(java.io.ByteArrayOutputStream
	 * )
	 */
	public int encodeElement(ByteArrayOutputStream bos) throws IOException {
		bos.write(this.callingPartyCategory);
		return 1;
	}

	public byte getCallingPartyCategory() {
		return callingPartyCategory;
	}

	public void setCallingPartyCategory(byte callingPartyCategory) {
		this.callingPartyCategory = callingPartyCategory;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
