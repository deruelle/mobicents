/**
 * Start time:13:47:48 2009-04-05<br>
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
 * Start time:13:47:48 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class DisplayInformation extends AbstractParameter {
	public static final int _PARAMETER_CODE = 0x73;
	// FIXME: Q.931 4.5.16 Display - Oleg is this correct?

	private byte[] info;

	public DisplayInformation(byte[] info) throws ParameterRangeInvalidException {
		super();
		// FIXME: this is only elementID
		super.tag = new byte[] { 0x28 };
		decodeElement(info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		try{
			setInfo(b);
		}catch(Exception e)
		{
			throw new ParameterRangeInvalidException(e);
		}
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		for (int index = 0; index < this.info.length; index++) {
			this.info[index] = (byte) (this.info[index] & 0x7F);
		}

		this.info[this.info.length - 1] = (byte) ((this.info[this.info.length - 1]) | (0x01 << 7));
		return this.info;
	}

	public byte[] getInfo() {
		return info;
	}

	public void setInfo(byte[] info) throws IllegalArgumentException {
		if (info == null || info.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}
		this.info = info;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
