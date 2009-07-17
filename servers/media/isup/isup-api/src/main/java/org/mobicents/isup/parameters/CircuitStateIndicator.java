/**
 * Start time:17:10:53 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:17:10:53 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CircuitStateIndicator extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x26;
	// FIXME: Q.763 3.14 - Oleg is this correct?

	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state "0"
	 * : transient
	 */
	public static int _MBS_NPS_TRANSIENT = 0;

	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state "0"
	 * : unequipped
	 */
	public static int _MBS_NPS_UNEQUIPED = 3;

	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state
	 * ~"0" : no blocking - active
	 */
	public static int _MBS_NO_BLOCKING = 0;

	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state
	 * ~"0" : localy blocked
	 */
	public static int _MBS_LOCALY_BLOCKED = 1;
	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state
	 * ~"0" : remotely blocked blocked
	 */
	public static int _MBS_REMOTELY_BLOCKED = 2;

	/**
	 * See Q.763 3.14 Maintenance blocking state - for call processing state
	 * ~"0" : locally and remotely blocked
	 */
	public static int _MBS_LAR_BLOCKED = 3;

	/**
	 * See Q.763 3.14 Call processing state : circuit incoming busy
	 */
	public static int _CPS_CIB = 1;
	/**
	 * See Q.763 3.14 Call processing state : circuit outgoing busy
	 */
	public static int _CPS_COB = 2;
	/**
	 * See Q.763 3.14 Call processing state : idle
	 */
	public static int _CPS_IDLE = 3;

	/**
	 * See Q.763 3.14 Hardware blocking state (Note: if this does not equal "0"
	 * Call Processing State must be equal to "3") : no blocking (active)
	 */
	public static int _HBS_NO_BLOCKING = 0;
	/**
	 * See Q.763 3.14 Hardware blocking state (Note: if this does not equal "0"
	 * Call Processing State must be equal to "3") : locally blocked
	 */
	public static int _HBS_LOCALY_BLOCKED = 1;
	/**
	 * See Q.763 3.14 Hardware blocking state (Note: if this does not equal "0"
	 * Call Processing State must be equal to "3") : remotely blocked
	 */
	public static int _HBS_REMOTELY_BLOCKED = 2;
	/**
	 * See Q.763 3.14 Hardware blocking state (Note: if this does not equal "0"
	 * Call Processing State must be equal to "3") : locally and remotely
	 * blocked
	 */
	public static int _HBS_LAR_BLOCKED = 3;

	private byte[] circuitState = null;

	public CircuitStateIndicator(byte[] circuitState) {
		super();
		this.decodeElement(circuitState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		setCircuitState(b);
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return this.circuitState;
	}

	public byte[] getCircuitState() {
		return circuitState;
	}

	public void setCircuitState(byte[] circuitState) {
		if (circuitState == null || circuitState.length == 0) {
			throw new IllegalArgumentException("byte[] must nto be null and length must be greater than 0");
		}
		this.circuitState = circuitState;
	}

	public static byte getCircuitState(int MBS, int CPS, int HBS) {
		int v = 0;
		// FIXME: Shoudl we here enforce proper coding? according to note or
		// move it to encodeElement??
		if (HBS != _HBS_NO_BLOCKING) {
			// NOTE – If bits F E are not coded 0 0, bits D C must be coded 1 1.
			// - this means CPS == 3
			CPS = _CPS_IDLE;
		}

		v = MBS & 0x03;
		v |= (CPS & 0x03) << 2;
		v |= (HBS & 0x03) << 4;
		return (byte) v;
	}

	public int getCallProcessingState(byte b) {
		return (b >> 2) & 0x03;
	}

	public int getHardwareBlockingState(byte b) {
		return (b >> 4) & 0x03;
	}

	public int getMaintenanceBlockingState(byte b) {
		return b & 0x03;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}

}
