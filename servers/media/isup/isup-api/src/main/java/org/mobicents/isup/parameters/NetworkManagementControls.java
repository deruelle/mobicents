/**
 * Start time:12:02:43 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:12:02:43 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class NetworkManagementControls extends AbstractParameter {
	
	public static final int _PARAMETER_CODE = 0x5B;
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;
	//FIXME - should we switch to boolean[] - its a slight perf loss :P
	private byte[] networkManagementControls = null;

	public NetworkManagementControls(byte[] b) {
		super();
		decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {

		setNetworkManagementControls(b);
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		for (int index = 0; index < this.networkManagementControls.length; index++) {
			this.networkManagementControls[index] = (byte) (this.networkManagementControls[index] & 0x01);
		}

		this.networkManagementControls[this.networkManagementControls.length - 1] = (byte) ((this.networkManagementControls[this.networkManagementControls.length - 1]) | (0x01 << 7));
		return this.networkManagementControls;
	}

	public static boolean isTARControlEnabled(byte b) {
		return (b & 0x01) == _TURN_ON;
	}

	public static byte getTAREnabledByte(boolean enabled) {
		return (byte) (enabled ? _TURN_ON : _TURN_OFF);
	}

	public byte[] getNetworkManagementControls() {
		return networkManagementControls;
	}

	public void setNetworkManagementControls(byte[] networkManagementControls) {
		if (networkManagementControls == null || networkManagementControls.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}
		this.networkManagementControls = networkManagementControls;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
