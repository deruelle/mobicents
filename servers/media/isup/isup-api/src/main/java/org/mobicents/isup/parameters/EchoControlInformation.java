/**
 * Start time:18:36:08 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:18:36:08 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class EchoControlInformation extends AbstractParameter {

	/**
	 * See Q.763 3.19 Outgoing echo control device information indicator : no
	 * information
	 */
	public static final int _OUTGOING_ECHO_CDII_NOINFO = 0;

	/**
	 * See Q.763 3.19 Outgoing echo control device information indicator :
	 * outgoing echo control device not included and not available
	 */
	public static final int _OUTGOING_ECHO_CDII_NINA = 1;

	/**
	 * See Q.763 3.19 Outgoing echo control device information indicator :
	 * outgoing echo control device included
	 */
	public static final int _OUTGOING_ECHO_CDII_INCLUDED = 2;

	/**
	 * See Q.763 3.19 Outgoing echo control device information indicator :
	 * outgoing echo control device not included but available
	 */
	public static final int _OUTGOING_ECHO_CDII_NIA = 3;

	/**
	 * See Q.763 3.19 Incoming echo control device information indicator : no
	 * information
	 */
	public static final int _INCOMING_ECHO_CDII_NOINFO = 0;

	/**
	 * See Q.763 3.19 Incoming echo control device information indicator :
	 * incoming echo control device not included and not available
	 */
	public static final int _INCOMING_ECHO_CDII_NINA = 1;

	/**
	 * See Q.763 3.19 Incoming echo control device information indicator :
	 * incoming echo control device included
	 */
	public static final int _INCOMING_ECHO_CDII_INCLUDED = 2;

	/**
	 * See Q.763 3.19 Incoming echo control device information indicator :
	 * incoming echo control device not included but available
	 */
	public static final int _INCOMING_ECHO_CDII_NIA = 3;

	/**
	 * See Q.763 3.19 Incoming echo control device request indicator : no
	 * information
	 */
	public static final int _INCOMING_ECHO_CDRI_NOINFO = 0;

	/**
	 * See Q.763 3.19 Incoming echo control device request indicator : incoming
	 * echo control device activation request
	 */
	public static final int _INCOMING_ECHO_CDRI_AR = 1;

	/**
	 * See Q.763 3.19 Incoming echo control device request indicator : incoming
	 * echo control device deactivation request (Note 2)
	 */
	public static final int _INCOMING_ECHO_CDRI_DR = 2;

	/**
	 * See Q.763 3.19 Outgoing echo control device request indicator : no
	 * information
	 */
	public static final int _OUTGOING_ECHO_CDRI_NOINFO = 0;

	/**
	 * See Q.763 3.19 Outgoing echo control device request indicator : outgoing
	 * echo control device activation request
	 */
	public static final int _OUTGOING_ECHO_CDRI_AR = 1;

	/**
	 * See Q.763 3.19 Outgoing echo control device request indicator : outgoing
	 * echo control device deactivation request (Note 1)
	 */
	public static final int _OUTGOING_ECHO_CDRI_DR = 2;

	public static final int _PARAMETER_CODE = 0x37;
	private int outgoingEchoControlDeviceInformationIndicator;
	private int incomingEchoControlDeviceInformationIndicator;
	private int outgoingEchoControlDeviceInformationRequestIndicator;
	private int incomingEchoControlDeviceInformationRequestIndicator;

	
	
	public EchoControlInformation(int outgoingEchoControlDeviceInformationIndicator, int incomingEchoControlDeviceInformationIndicator, int outgoingEchoControlDeviceInformationRequestIndicator,
			int incomingEchoControlDeviceInformationRequestIndicator) {
		super();
		this.outgoingEchoControlDeviceInformationIndicator = outgoingEchoControlDeviceInformationIndicator;
		this.incomingEchoControlDeviceInformationIndicator = incomingEchoControlDeviceInformationIndicator;
		this.outgoingEchoControlDeviceInformationRequestIndicator = outgoingEchoControlDeviceInformationRequestIndicator;
		this.incomingEchoControlDeviceInformationRequestIndicator = incomingEchoControlDeviceInformationRequestIndicator;
	}

	public EchoControlInformation(byte[] b) {
		super();
		decodeElement(b);
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

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
