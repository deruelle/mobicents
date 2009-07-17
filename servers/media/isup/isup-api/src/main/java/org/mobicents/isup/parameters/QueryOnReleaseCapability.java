/**
 * Start time:08:28:43 2009-04-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:08:28:43 2009-04-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class QueryOnReleaseCapability extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x85;
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;
	private byte[] capabilities;

	/**
	 * See Q.763 QoR capability indicator : no indication
	 */
	public static final boolean _QoRI_NO_INDICATION = false;

	/**
	 * See Q.763 QoR capability indicator : QoR support
	 */
	public static final boolean _QoRI_SUPPORT = true;

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
			this.capabilities[index] = (byte) (this.capabilities[index] & 0x7F);
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

	public static boolean isQoRSupport(byte b) {
		return (b & 0x01) == _TURN_ON;
	}

	public static byte getQoRSupport(boolean enabled) {

		return (byte) (enabled ? _TURN_ON : _TURN_OFF);
	}
	public int getCode() {

		return _PARAMETER_CODE;
	}
}
