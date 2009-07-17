/**
 * Start time:16:55:01 2009-04-02<br>
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
 * Start time:16:55:01 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RedirectionNumberRestriction extends AbstractParameter {

	/**
	 * See Q.763 Presentation restricted indicator presentation allowed
	 */
	public static final byte _PRI_PA = 0;
	/**
	 * See Q.763 Presentation restricted indicator presentation restricted
	 */
	public static final byte _PRI_PR = 1;
	public static final int _PARAMETER_CODE = 0x40;

	private byte presentationRestrictedIndicator;

	public RedirectionNumberRestriction(byte presentationRestrictedIndicator) {
		super();
		this.presentationRestrictedIndicator = presentationRestrictedIndicator;
	}

	public RedirectionNumberRestriction(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length != 1) {
			throw new ParameterRangeInvalidException("byte[] must  not be null and length must  be 1");
		}

		this.presentationRestrictedIndicator = (byte) (b[0] & 0x03);
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return new byte[] { (byte) (this.presentationRestrictedIndicator & 0x03) };
	}

	public byte getPresentationRestrictedIndicator() {
		return presentationRestrictedIndicator;
	}

	public void setPresentationRestrictedIndicator(byte presentationRestrictedIndicator) {
		this.presentationRestrictedIndicator = presentationRestrictedIndicator;
	}
	public int getCode() {

		return _PARAMETER_CODE;
	}
}
