/**
 * Start time:16:49:41 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:16:49:41 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CircuitGroupSuperVisionMessageType extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x15;
	/**
	 * See Q.763 3.13 Circuit group supervision message type indicator
	 * maintenance oriented
	 */
	public static final int _CIRCUIT_GROUP_SMTIMO = 0;
	/**
	 * See Q.763 3.13 Circuit group supervision message type indicator hardware
	 * failure oriented
	 */
	public static final int _CIRCUIT_GROUP_SMTIHFO = 1;

	private int circuitGroupSuperVisionMessageTypeIndicator = 0;

	public CircuitGroupSuperVisionMessageType(byte[] b) {
		super();
		decodeElement(b);
	}

	public CircuitGroupSuperVisionMessageType(int circuitGroupSuperVisionMessageTypeIndicator) {
		super();
		this.circuitGroupSuperVisionMessageTypeIndicator = circuitGroupSuperVisionMessageTypeIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must not be null or has size different than 1.");
		}
		this.circuitGroupSuperVisionMessageTypeIndicator = b[0] & 0x03;
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		byte[] b = new byte[] { (byte) (this.circuitGroupSuperVisionMessageTypeIndicator & 0x03) };

		return b;
	}

	public int getCircuitGroupSuperVisionMessageTypeIndicator() {
		return circuitGroupSuperVisionMessageTypeIndicator;
	}

	public void setCircuitGroupSuperVisionMessageTypeIndicator(int circuitGroupSuperVisionMessageTypeIndicator) {
		this.circuitGroupSuperVisionMessageTypeIndicator = circuitGroupSuperVisionMessageTypeIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
