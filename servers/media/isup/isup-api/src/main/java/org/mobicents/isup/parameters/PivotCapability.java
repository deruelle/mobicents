/**
 * Start time:15:52:32 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:15:52:32 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class PivotCapability extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x7B;
	/**
	 * See Q.763 3.84 Pivot possible indicator : no indication
	 */
	public static final int _PPI_NO_INDICATION = 0;

	/**
	 * See Q.763 3.84 Pivot possible indicator : pivot routing possible before
	 * ACM
	 */
	public static final int _PPI_PRPB_ACM = 1;

	/**
	 * See Q.763 3.84 Pivot possible indicator : pivot routing possible before
	 * ANM
	 */
	public static final int _PPI_PRPB_ANM = 2;

	/**
	 * See Q.763 3.84 Pivot possible indicator : pivot routing possible any time
	 * during the call
	 */
	public static final int _PPI_PRPB_ANY = 3;

	/**
	 * See Q.763 3.84 Interworking to redirection indicator (national use)
	 */
	public static final boolean _ITRI_ALLOWED = false;

	/**
	 * See Q.763 3.84 Interworking to redirection indicator (national use)
	 */
	public static final boolean _ITRI_NOT_ALLOWED = true;

	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	private byte[] pivotCapabilities;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		for (int index = 0; index < this.pivotCapabilities.length; index++) {
			this.pivotCapabilities[index] = (byte) (this.pivotCapabilities[index] & 0x7F);
		}

		this.pivotCapabilities[this.pivotCapabilities.length - 1] = (byte) ((this.pivotCapabilities[this.pivotCapabilities.length - 1]) | (0x01 << 7));
		return this.pivotCapabilities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {

		setPivotCapabilities(b);
		return b.length;
	}

	public byte[] getPivotCapabilities() {
		return pivotCapabilities;
	}

	public void setPivotCapabilities(byte[] pivotCapabilities) {
		if (pivotCapabilities == null || pivotCapabilities.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}

		this.pivotCapabilities = pivotCapabilities;
	}

	public static byte getPivotCapabilityByte(boolean itriNotAllowed, int pivotPossibility) {
		int b = (itriNotAllowed ? _TURN_ON : _TURN_OFF) << 6;
		b |= pivotPossibility & 0x07;

		return (byte) b;

	}

	public static boolean getITRINotAllowed(byte b) {
		return ((b >> 6) & 0x01) == _TURN_ON;
	}

	public static int getPivotCapability(byte b) {
		return b & 0x07;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
