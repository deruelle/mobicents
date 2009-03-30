/**
 * Start time:18:36:08 2009-03-30<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:18:36:08 2009-03-30<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class EchoControlInformation extends AbstractParameter {

	/**
	 * See Q.763 3.19
	 */
	public static final int _OUTGOING_ECHO_CONTROL_DEVICE_INFORMATION_INDICATOR_NOINFO = 0;

	/**
	 * See Q.763 3.19
	 */
	public static final int _OUTGOING_ECHO_CONTROL_DEVICE_INFORMATION_INDICATOR_NOT_INCLUDED_NOT_AVAILABLE = 1;

	/**
	 * See Q.763 3.19
	 */
	public static final int _OUTGOING_ECHO_CONTROL_DEVICE_INFORMATION_INDICATOR_INCLUDED = 2;

	/**
	 * See Q.763 3.19
	 */
	public static final int _OUTGOING_ECHO_CONTROL_DEVICE_INFORMATION_INDICATOR_NOT_INCLUDED_AVAILABLE = 3;

	/**
	 * See Q.763 3.19
	 */
	public static final int _INCOMING_ECHO_CONTROL_DEVICE_INFORMATION_INDICATOR_NOINFO = 0;

	/**
	 * See Q.763 3.19
	 */
	public static final int _INCOMING_ECHO_CONTROL_DEVICE_INFORMATION_INDICATOR_NOT_INCLUDED_NOT_AVAILABLE = 1;

	/**
	 * See Q.763 3.19
	 */
	public static final int _INCOMING_ECHO_CONTROL_DEVICE_INFORMATION_INDICATOR_INCLUDED = 2;

	/**
	 * See Q.763 3.19
	 */
	public static final int _INCOMING_ECHO_CONTROL_DEVICE_INFORMATION_INDICATOR_NOT_INCLUDED_AVAILABLE = 3;

	/**
	 * See Q.763 3.19
	 */
	public static final int _INCOMING_ECHO_CONTROL_DEVICE_REQUEST_INDICATOR_NOINFO = 0;

	/**
	 * See Q.763 3.19
	 */
	public static final int _INCOMING_ECHO_CONTROL_DEVICE_REQUEST_INDICATOR_ACTIVATION_REQEUST = 1;

	/**
	 * See Q.763 3.19
	 */
	public static final int _INCOMING_ECHO_CONTROL_DEVICE_REQUEST_INDICATOR_DEACTIVATION_REQEUST = 2;

	/**
	 * See Q.763 3.19
	 */
	public static final int _OUTGOING_ECHO_CONTROL_DEVICE_REQUEST_INDICATOR_NOINFO = 0;

	/**
	 * See Q.763 3.19
	 */
	public static final int _OUTGOING_ECHO_CONTROL_DEVICE_REQUEST_INDICATOR_ACTIVATION_REQEUST = 1;

	/**
	 * See Q.763 3.19
	 */
	public static final int _OUTGOING_ECHO_CONTROL_DEVICE_REQUEST_INDICATOR_DEACTIVATION_REQEUST = 2;
	private int outgoingEchoControlDeviceInformationIndicator = 0;
	private int incomingEchoControlDeviceInformationIndicator = 0;
	private int outgoingEchoControlDeviceInformationRequestIndicator = 0;
	private int incomingEchoControlDeviceInformationRequestIndicator = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must not be null or have different size than 1");
		}
		byte v = b[0];

		this.outgoingEchoControlDeviceInformationIndicator = v & 0x03;
		this.incomingEchoControlDeviceInformationIndicator = (v >> 2) & 0x03;
		this.outgoingEchoControlDeviceInformationRequestIndicator = (v >> 4) & 0x03;
		this.incomingEchoControlDeviceInformationRequestIndicator = (v >> 6) & 0x03;
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		byte v = 0;

		v |= this.outgoingEchoControlDeviceInformationIndicator & 0x03;
		v |= (this.incomingEchoControlDeviceInformationIndicator & 0x03) << 2;
		v |= (this.outgoingEchoControlDeviceInformationRequestIndicator & 0x03) << 4;
		v |= (this.incomingEchoControlDeviceInformationRequestIndicator & 0x03) << 6;

		byte[] b = { v };
		return b;
	}

	public int getOutgoingEchoControlDeviceInformationIndicator() {
		return outgoingEchoControlDeviceInformationIndicator;
	}

	public void setOutgoingEchoControlDeviceInformationIndicator(int outgoingEchoControlDeviceInformationIndicator) {
		this.outgoingEchoControlDeviceInformationIndicator = outgoingEchoControlDeviceInformationIndicator;
	}

	public int getIncomingEchoControlDeviceInformationIndicator() {
		return incomingEchoControlDeviceInformationIndicator;
	}

	public void setIncomingEchoControlDeviceInformationIndicator(int incomingEchoControlDeviceInformationIndicator) {
		this.incomingEchoControlDeviceInformationIndicator = incomingEchoControlDeviceInformationIndicator;
	}

	public int getOutgoingEchoControlDeviceInformationRequestIndicator() {
		return outgoingEchoControlDeviceInformationRequestIndicator;
	}

	public void setOutgoingEchoControlDeviceInformationRequestIndicator(int outgoingEchoControlDeviceInformationRequestIndicator) {
		this.outgoingEchoControlDeviceInformationRequestIndicator = outgoingEchoControlDeviceInformationRequestIndicator;
	}

	public int getIncomingEchoControlDeviceInformationRequestIndicator() {
		return incomingEchoControlDeviceInformationRequestIndicator;
	}

	public void setIncomingEchoControlDeviceInformationRequestIndicator(int incomingEchoControlDeviceInformationRequestIndicator) {
		this.incomingEchoControlDeviceInformationRequestIndicator = incomingEchoControlDeviceInformationRequestIndicator;
	}

}
