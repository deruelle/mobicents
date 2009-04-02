/**
 * Start time:12:00:59 2009-04-02<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:12:00:59 2009-04-02<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class OptionalForwardCallIndicator extends AbstractParameter {

	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	/**
	 * See Q.763 3.38
	 */
	private final static boolean _SIMPLE_SEGMENTATION_INDICATOR_NO_ADDITIONAL_INFO = false;

	/**
	 * See Q.763 3.38
	 */
	private final static boolean _SIMPLE_SEGMENTATION_INDICATOR_ADDITIONAL_INFO = true;

	/**
	 * See Q.763 3.38
	 */
	private final static boolean _CONNECTED_LINE_IDENTITY_REQUEST_INDICATOR_NOT_REQUESTED = false;

	/**
	 * See Q.763 3.38
	 */
	private final static boolean _CONNECTED_LINE_IDENTITY_REQUEST_INDICATOR_REQUESTED = true;
	/**
	 * See Q.763 3.38
	 */
	private final static int _CLOSED_USER_GROUP_CALL_INDICATOR_NON_CUG_CALL = 0;

	/**
	 * See Q.763 3.38
	 */
	private final static int _CLOSED_USER_GROUP_CALL_INDICATOR_CUG_CALL_OUTGOIGN_ACCESS_ALLOWED = 2;

	/**
	 * See Q.763 3.38
	 */
	private final static int _CLOSED_USER_GROUP_CALL_INDICATOR_CUG_CALL_OUTGOIGN_ACCESS_NOT_ALLOWED = 3;

	private byte closedUserGroupCallIndicator = 0;
	private boolean simpleSegmentationIndicator = false;
	private boolean connectedLineIdentityRequestIndicator = false;

	public OptionalForwardCallIndicator(byte[] b) {
		super();
		decodeElement(b);
	}

	public OptionalForwardCallIndicator(byte closedUserGroupCallIndicator, boolean simpleSegmentationIndicator, boolean connectedLineIdentityRequestIndicator) {
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
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must  not be null and length must  be 1");
		}
		this.closedUserGroupCallIndicator = (byte) (b[0] & 0x03);
		this.simpleSegmentationIndicator = ((b[0] >> 2) & 0x01) == _TURN_ON;
		this.simpleSegmentationIndicator = ((b[0] >> 7) & 0x01) == _TURN_ON;
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
		b0 |= (this.connectedLineIdentityRequestIndicator ? _TURN_ON : _TURN_OFF) << 2;
		b0 |= (this.connectedLineIdentityRequestIndicator ? _TURN_ON : _TURN_OFF) << 7;

		return new byte[] { (byte) b0 };
	}

}
