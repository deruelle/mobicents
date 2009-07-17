/**
 * Start time:09:11:07 2009-04-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:09:11:07 2009-04-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class RedirectCapability extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x4E;
	/**
	 * See Q.763 3.96 Redirect possible indicator : not used
	 */
	public static final int _RPI_NOT_USED = 0;
	/**
	 * See Q.763 3.96 Redirect possible indicator : redirect possible before
	 * ACM use)
	 */
	public static final int _RPI_RPB_ACM = 1;
	/**
	 * See Q.763 3.96 Redirect possible indicator : redirect possible before
	 * ANM
	 */
	public static final int _RPI_RPB_ANM = 2;
	/**
	 * See Q.763 3.96 Redirect possible indicator : redirect possible at any
	 * time during the call
	 */
	public static final int _RPI_RPANTDC = 3;

	private byte[] capabilities;

	public RedirectCapability(byte[] capabilities) {
		super();
		decodeElement(capabilities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		this.setCapabilities(b);
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		for (int index = 0; index < this.capabilities.length; index++) {
			this.capabilities[index] = (byte) (this.capabilities[index] & 0x07);
		}

		this.capabilities[this.capabilities.length - 1] = (byte) ((this.capabilities[this.capabilities.length - 1]) | (0x01 << 7));
		return this.capabilities;
	}

	public byte[] getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(byte[] capabilities) {
		if (capabilities == null || capabilities.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}
		this.capabilities = capabilities;
	}

	public static int getCapability(byte b) {
		return b & 0x7F;
	}
	public int getCode() {

		return _PARAMETER_CODE;
	}
}
