/**
 * Start time:11:38:33 2009-04-02<br>
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
 * Start time:11:38:33 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class OptionalBakwardCallIndicators extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x29;

	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	/**
	 * See Q.763 3.37 In-band information indicator
	 */
	public final static boolean _IBII_NO_INDICATION = false;
	/**
	 * See Q.763 3.37 In-band information indicator
	 */
	public final static boolean _IBII_AVAILABLE = true;

	/**
	 * See Q.763 3.37 Call diversion may occur indicator
	 */
	public final static boolean _CDI_NO_INDICATION = false;

	/**
	 * See Q.763 3.37 Call diversion may occur indicator
	 */
	public final static boolean _CDI_MAY_OCCUR = true;

	/**
	 * See Q.763 3.37 Simple segmentation indicator
	 */
	public final static boolean _SSIR_NO_ADDITIONAL_INFO = false;

	/**
	 * See Q.763 3.37 Simple segmentation indicator
	 */
	public final static boolean _SSIR_ADDITIONAL_INFO = true;

	/**
	 * See Q.763 3.37 MLPP user indicator
	 */
	public final static boolean _MLLPUI_NO_INDICATION = false;

	/**
	 * See Q.763 3.37 MLPP user indicator
	 */
	public final static boolean _MLLPUI_USER = true;

	private boolean inbandInformationIndicator;
	private boolean callDiversionMayOccurIndicator;
	private boolean simpleSegmentationIndicator;
	private boolean mllpUserIndicator;

	public OptionalBakwardCallIndicators(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	public OptionalBakwardCallIndicators(boolean inbandInformationIndicator, boolean callDiversionMayOccurIndicator, boolean simpleSegmentationIndicator, boolean mllpUserIndicator) {
		super();
		this.inbandInformationIndicator = inbandInformationIndicator;
		this.callDiversionMayOccurIndicator = callDiversionMayOccurIndicator;
		this.simpleSegmentationIndicator = simpleSegmentationIndicator;
		this.mllpUserIndicator = mllpUserIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length != 1) {
			throw new ParameterRangeInvalidException("byte[] must  not be null and length must  be 1");
		}
		this.inbandInformationIndicator = (b[0] & 0x01) == _TURN_ON;
		this.callDiversionMayOccurIndicator = ((b[0] >> 1) & 0x01) == _TURN_ON;
		this.simpleSegmentationIndicator = ((b[0] >> 2) & 0x01) == _TURN_ON;
		this.mllpUserIndicator = ((b[0] >> 3) & 0x01) == _TURN_ON;
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		int b0 = 0;

		b0 = this.inbandInformationIndicator ? _TURN_ON : _TURN_OFF;
		b0 |= (this.callDiversionMayOccurIndicator ? _TURN_ON : _TURN_OFF) << 1;
		b0 |= (this.simpleSegmentationIndicator ? _TURN_ON : _TURN_OFF) << 2;
		b0 |= (this.mllpUserIndicator ? _TURN_ON : _TURN_OFF) << 3;

		return new byte[] { (byte) b0 };
	}

	public boolean isInbandInformationIndicator() {
		return this.inbandInformationIndicator;
	}

	public void setInbandInformationIndicator(boolean inbandInformationIndicator) {
		this.inbandInformationIndicator = inbandInformationIndicator;
	}

	public boolean isCallDiversionMayOccurIndicator() {
		return callDiversionMayOccurIndicator;
	}

	public void setCallDiversionMayOccurIndicator(boolean callDiversionMayOccurIndicator) {
		this.callDiversionMayOccurIndicator = callDiversionMayOccurIndicator;
	}

	public boolean isSimpleSegmentationIndicator() {
		return simpleSegmentationIndicator;
	}

	public void setSimpleSegmentationIndicator(boolean simpleSegmentationIndicator) {
		this.simpleSegmentationIndicator = simpleSegmentationIndicator;
	}

	public boolean isMllpUserIndicator() {
		return mllpUserIndicator;
	}

	public void setMllpUserIndicator(boolean mllpUserIndicator) {
		this.mllpUserIndicator = mllpUserIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
