/**
 * Start time:12:00:59 2009-04-02<br>
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
 * Start time:12:00:59 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class OptionalForwardCallIndicators extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x08;
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	/**
	 * See Q.763 3.38 Simple segmentation indicator : no additional information
	 * will be sent
	 */
	public final static boolean _SSI_NO_ADDITIONAL_INFO = false;

	/**
	 * See Q.763 3.38 Simple segmentation indicator : additional information
	 * will be sent in a segmentation message
	 */
	public final static boolean _SSI_ADDITIONAL_INFO = true;

	/**
	 * See Q.763 3.38 Connected line identity request indicator :
	 */
	public final static boolean _CLIRI_NOT_REQUESTED = false;

	/**
	 * See Q.763 3.38 Connected line identity request indicator :
	 */
	public final static boolean _CLIRI_REQUESTED = true;
	/**
	 * See Q.763 3.38 Closed user group call indicator : non-CUG call
	 */
	public final static int _CUGCI_NON_CUG_CALL = 0;

	/**
	 * See Q.763 3.38 Closed user group call indicator : closed user group call,
	 * outgoing access allowed
	 */
	public final static int _CUGCI_CUG_CALL_OAL = 2;

	/**
	 * See Q.763 3.38 Closed user group call indicator : closed user group call,
	 * outgoing access not allowed
	 */
	public final static int _CUGCI_CUG_CALL_OANL = 3;

	private int closedUserGroupCallIndicator;
	private boolean simpleSegmentationIndicator;
	private boolean connectedLineIdentityRequestIndicator;

	public OptionalForwardCallIndicators(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	public OptionalForwardCallIndicators(int closedUserGroupCallIndicator, boolean simpleSegmentationIndicator, boolean connectedLineIdentityRequestIndicator) {
		super();
		this.closedUserGroupCallIndicator = closedUserGroupCallIndicator;
		this.simpleSegmentationIndicator = simpleSegmentationIndicator;
		this.connectedLineIdentityRequestIndicator = connectedLineIdentityRequestIndicator;
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
		this.closedUserGroupCallIndicator = (byte) (b[0] & 0x03);
		this.simpleSegmentationIndicator = ((b[0] >> 2) & 0x01) == _TURN_ON;
		this.connectedLineIdentityRequestIndicator = ((b[0] >> 7) & 0x01) == _TURN_ON;
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		int b0 = 0;

		b0 = this.closedUserGroupCallIndicator & 0x03;
		b0 |= (this.simpleSegmentationIndicator ? _TURN_ON : _TURN_OFF) << 2;
		b0 |= (this.connectedLineIdentityRequestIndicator ? _TURN_ON : _TURN_OFF) << 7;

		return new byte[] { (byte) b0 };
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}

	public int getClosedUserGroupCallIndicator() {
		return closedUserGroupCallIndicator;
	}

	public void setClosedUserGroupCallIndicator(int closedUserGroupCallIndicator) {
		this.closedUserGroupCallIndicator = closedUserGroupCallIndicator;
	}

	public boolean isSimpleSegmentationIndicator() {
		return simpleSegmentationIndicator;
	}

	public void setSimpleSegmentationIndicator(boolean simpleSegmentationIndicator) {
		this.simpleSegmentationIndicator = simpleSegmentationIndicator;
	}

	public boolean isConnectedLineIdentityRequestIndicator() {
		return connectedLineIdentityRequestIndicator;
	}

	public void setConnectedLineIdentityRequestIndicator(boolean connectedLineIdentityRequestIndicator) {
		this.connectedLineIdentityRequestIndicator = connectedLineIdentityRequestIndicator;
	}
	
	
}
