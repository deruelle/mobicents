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

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:13:49:42 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UIDActionIndicators extends AbstractParameter {

	
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	private byte[] udiActionIndicators = null;
	/**
	 * See Q.763 3.78 Through-connection instruction indicator : no indication
	 */
	public static final boolean _TCII_NO_INDICATION = false;

	/**
	 * See Q.763 3.78 Through-connection instruction indicator : through-connect
	 * in both directions
	 */
	public static final boolean _TCII_TCIBD = true;

	/**
	 * See Q.763 3.78 T9 timer instruction indicator : no indication
	 */
	public static final boolean _T9_TII_NO_INDICATION = false;

	/**
	 * See Q.763 3.78 T9 timer instruction indicator : stop or do not start T9
	 * timer
	 */
	public static final boolean _T9_TII_SDNST9T = false;
	public static final int _PARAMETER_CODE = 0x74;

	public UIDActionIndicators(byte[] udiActionIndicators) throws ParameterRangeInvalidException {
		super();
		decodeElement(udiActionIndicators);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		try{
			setUdiActionIndicators(b);
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
		for (int index = 0; index < this.udiActionIndicators.length; index++) {
			this.udiActionIndicators[index] = (byte) (this.udiActionIndicators[index] & 0x7F);
		}

		this.udiActionIndicators[this.udiActionIndicators.length - 1] = (byte) ((this.udiActionIndicators[this.udiActionIndicators.length - 1]) | (0x01 << 7));
		return this.udiActionIndicators;
	}

	public byte[] getUdiActionIndicators() {
		return udiActionIndicators;
	}

	public void setUdiActionIndicators(byte[] udiActionIndicators) {
		if (udiActionIndicators == null || udiActionIndicators.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}
		this.udiActionIndicators = udiActionIndicators;
	}

	public static byte getUIDAction(boolean TCII, boolean T9) {
		byte b = (byte) (TCII ? _TURN_ON : _TURN_OFF);
		b |= (T9 ? _TURN_ON : _TURN_OFF) << 1;
		return b;
	}

	public static boolean getT9Indicator(byte b) {
		return ((b >> 1) & 0x01) == _TURN_ON;
	}

	public static boolean getTCIIndicator(byte b) {
		return ((b >> 1) & 0x01) == _TURN_ON;
	}
	public int getCode() {

		return _PARAMETER_CODE;
	}
}
