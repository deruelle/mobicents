/**
 * Start time:09:12:26 2009-04-02<br>
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
 * Start time:09:12:26 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class NatureOfConnectionIndicators extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x06;
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	/**
	 * See Q.763 3.35 Echo control device indicator : outgoing echo control
	 * device included
	 */
	public static final boolean _ECDI_INCLUDED = true;

	/**
	 * See Q.763 3.35 Echo control device indicator : outgoing echo control
	 * device not included
	 */
	public static final boolean _ECDI_NOT_INCLUDED = false;

	/**
	 * See Q.763 3.35 Satellite indicator : no satellite circuit in the
	 * connection
	 */
	public static final int _SI_NO_SATELLITE = 0;

	/**
	 * See Q.763 3.35 Satellite indicator : one satellite circuit in the
	 * connection
	 */
	public static final int _SI_ONE_SATELLITE = 1;

	/**
	 * See Q.763 3.35 Satellite indicator : two satellite circuits in the
	 * connection
	 */
	public static final int _SI_TWO_SATELLITE = 2;

	/**
	 * See Q.763 3.35 Continuity check indicator
	 */
	public static final int _CCI_NOT_REQUIRED = 0;
	/**
	 * See Q.763 3.35 Continuity check indicator
	 */
	public static final int _CCI_REQUIRED_ON_THIS_CIRCUIT = 1;

	/**
	 * See Q.763 3.35 Continuity check indicator
	 */
	public static final int _CCI_PERFORMED_ON_PREVIOUS_CIRCUIT = 0;

	private int satelliteIndicator = 0;
	private int continuityCheckIndicator = 0;
	private boolean echoControlDeviceIndicator = false;

	public NatureOfConnectionIndicators(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	public NatureOfConnectionIndicators(byte satelliteIndicator, byte continuityCheckIndicator, boolean echoControlDeviceIndicator) {
		super();
		this.satelliteIndicator = satelliteIndicator;
		this.continuityCheckIndicator = continuityCheckIndicator;
		this.echoControlDeviceIndicator = echoControlDeviceIndicator;
	}

	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length != 1) {
			throw new ParameterRangeInvalidException("byte[] must not be null and must have length of 1");
		}
		this.satelliteIndicator = (byte) (b[0] & 0x03);
		this.continuityCheckIndicator = (byte) ((b[0] >> 2) & 0x03);
		this.echoControlDeviceIndicator = ((b[0] >> 4) == _TURN_ON);

		return 1;
	}

	public byte[] encodeElement() throws IOException {

		int b0 = 0;
		b0 = this.satelliteIndicator & 0x03;
		b0 |= (this.continuityCheckIndicator & 0x03) << 2;
		b0 |= (this.echoControlDeviceIndicator ? _TURN_ON : _TURN_OFF) << 4;
		return new byte[] { (byte) b0 };
	}

	public int getSatelliteIndicator() {
		return satelliteIndicator;
	}

	public void setSatelliteIndicator(int satelliteIndicator) {
		this.satelliteIndicator = satelliteIndicator & 0x03;
	}

	public int getContinuityCheckIndicator() {
		return continuityCheckIndicator;
	}

	public void setContinuityCheckIndicator(int continuityCheckIndicator) {
		this.continuityCheckIndicator = continuityCheckIndicator & 0x03;
	}

	public boolean isEchoControlDeviceIndicator() {
		return echoControlDeviceIndicator;
	}

	public void setEchoControlDeviceIndicator(boolean echoControlDeviceIndicator) {
		this.echoControlDeviceIndicator = echoControlDeviceIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
