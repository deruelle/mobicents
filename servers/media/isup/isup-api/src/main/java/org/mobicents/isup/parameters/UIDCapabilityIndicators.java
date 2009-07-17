/**
 * Start time:13:49:42 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:13:49:42 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UIDCapabilityIndicators extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x75;
	
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	private byte[] uidCapabilityIndicators = null;
	/**
	 * See Q.763 3.79 Through-connection instruction indicator : no indication
	 */
	public static final boolean _TCI_NO_INDICATION = false;

	/**
	 * See Q.763 3.79 Through-connection instruction indicator :
	 * through-connection modification possible
	 */
	public static final boolean _TCI_TCMP = true;

	/**
	 * See Q.763 3.79 T9 timer indicator : no indication
	 */
	public static final boolean _T9_TII_NO_INDICATION = false;

	/**
	 * See Q.763 3.79 T9 timer indicator : stopping of T9 timer possible
	 */
	public static final boolean _T9_TI_SOT9P = false;

	public UIDCapabilityIndicators(byte[] udiActionIndicators) {
		super();
		decodeElement(udiActionIndicators);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {

		setUIDCapabilityIndicators(b);
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		for (int index = 0; index < this.uidCapabilityIndicators.length; index++) {
			this.uidCapabilityIndicators[index] = (byte) (this.uidCapabilityIndicators[index] & 0x7F);
		}

		this.uidCapabilityIndicators[this.uidCapabilityIndicators.length - 1] = (byte) ((this.uidCapabilityIndicators[this.uidCapabilityIndicators.length - 1]) | (0x01 << 7));
		return this.uidCapabilityIndicators;
	}

	public byte[] getUIDCapabilityIndicators() {
		return uidCapabilityIndicators;
	}

	public void setUIDCapabilityIndicators(byte[] uidCapabilityIndicators) {
		if (uidCapabilityIndicators == null || uidCapabilityIndicators.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}
		this.uidCapabilityIndicators = uidCapabilityIndicators;
	}

	public static byte getUIDAction(boolean TCI, boolean T9) {
		byte b = (byte) (TCI ? _TURN_ON : _TURN_OFF);
		b |= (T9 ? _TURN_ON : _TURN_OFF) << 1;
		return b;
	}

	public static boolean getT9Indicator(byte b) {
		return ((b >> 1) & 0x01) == _TURN_ON;
	}

	public static boolean getTCIndicator(byte b) {
		return ((b >> 1) & 0x01) == _TURN_ON;
	}
	public int getCode() {

		return _PARAMETER_CODE;
	}
}
