/**
 * Start time:09:12:26 2009-04-02<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:09:12:26 2009-04-02<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class NatureOfConnectionIndicators extends AbstractParameter {

	private static final int _ECHO_DEVICE_INCLUDED = 1;
	private static final int _ECHO_DEVICE_NOT_INCLUDED = 0;

	/**
	 * See Q.763 3.35
	 */
	public static final boolean _ECHO_CONTROL_DEVICE_INDICATOR_INCLUDED = true;

	/**
	 * See Q.763 3.35
	 */
	public static final boolean _ECHO_CONTROL_DEVICE_INDICATOR_NOT_INCLUDED = false;

	/**
	 * See Q.763 3.35
	 */
	public static final int _SATELLITE_INDICATOR_NO_SATELLITE = 0;

	/**
	 * See Q.763 3.35
	 */
	public static final int _SATELLITE_INDICATOR_ONE_SATELLITE = 1;

	/**
	 * See Q.763 3.35
	 */
	public static final int _SATELLITE_INDICATOR_TWO_SATELLITE = 2;

	/**
	 * See Q.763 3.35
	 */
	public static final int _CONTINUITY_CHECK_INDICATOR_NOT_REQUIRED = 0;
	/**
	 * See Q.763 3.35
	 */
	public static final int _CONTINUITY_CHECK_INDICATOR_REQUIRED_ON_THIS_CIRCUIT = 1;

	/**
	 * See Q.763 3.35
	 */
	public static final int _CONTINUITY_CHECK_INDICATOR_PERFORMED_ON_PREVIOUS_CIRCUIT = 0;

	private byte satelliteIndicator = 0;
	private byte continuityCheckIndicator = 0;
	private boolean echoControlDeviceIndicator = false;

	public NatureOfConnectionIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	public NatureOfConnectionIndicators(byte satelliteIndicator, byte continuityCheckIndicator, boolean echoControlDeviceIndicator) {
		super();
		this.satelliteIndicator = satelliteIndicator;
		this.continuityCheckIndicator = continuityCheckIndicator;
		this.echoControlDeviceIndicator = echoControlDeviceIndicator;
	}

	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must not be null and must have length of 1");
		}
		this.satelliteIndicator = (byte) (b[0] & 0x03);
		this.continuityCheckIndicator = (byte) ((b[0] >> 2) & 0x03);
		this.echoControlDeviceIndicator = ((b[0] >> 4) == _ECHO_DEVICE_INCLUDED);

		return 1;
	}

	public byte[] encodeElement() throws IOException {

		int b0 = 0;
		b0 = this.satelliteIndicator & 0x03;
		b0 |= (this.continuityCheckIndicator & 0x03) << 2;
		b0 |= (this.echoControlDeviceIndicator ? _ECHO_DEVICE_INCLUDED : _ECHO_DEVICE_NOT_INCLUDED) << 4;
		return new byte[] { (byte) b0 };
	}

	public byte getSatelliteIndicator() {
		return satelliteIndicator;
	}

	public void setSatelliteIndicator(byte satelliteIndicator) {
		this.satelliteIndicator = satelliteIndicator;
	}

	public byte getContinuityCheckIndicator() {
		return continuityCheckIndicator;
	}

	public void setContinuityCheckIndicator(byte continuityCheckIndicator) {
		this.continuityCheckIndicator = continuityCheckIndicator;
	}

	public boolean isEchoControlDeviceIndicator() {
		return echoControlDeviceIndicator;
	}

	public void setEchoControlDeviceIndicator(boolean echoControlDeviceIndicator) {
		this.echoControlDeviceIndicator = echoControlDeviceIndicator;
	}

}
