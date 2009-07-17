/**
 * Start time:13:44:22 2009-03-31<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:13:44:22 2009-03-31<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class GenericNotificationIndicator extends AbstractParameter {

	/**
	 * See Q.763 3.25 Notification indicator : user suspended
	 */
	public static final int _NI_USER_SUSPENDED = 0;

	/**
	 * See Q.763 3.25 Notification indicator : user resumed
	 */
	public static final int _NI_USER_RESUMED = 1;

	/**
	 * See Q.763 3.25 Notification indicator : bearer service change
	 */
	public static final int _NI_BEARER_SERVICE_CHANGE = 2;
	/**
	 * See Q.763 3.25 Notification indicator : discriminator for extension to
	 * ASN.1
	 */
	public static final int _NI_DISCRIMINATOR_FOR_EXTENSION_TO_ASN1 = 3;

	/**
	 * See Q.763 3.25 Notification indicator : conference established
	 */
	public static final int _NI_CONFERENCE_ESTABILISHED = 0x42;

	/**
	 * See Q.763 3.25 Notification indicator : conference disconnected
	 */
	public static final int _NI_CONFERENCE_DISCONNECTED = 0x43;

	/**
	 * See Q.763 3.25 Notification indicator : other party added
	 */
	public static final int _NI_OTHER_PARTY_ADDED = 0x44;
	/**
	 * See Q.763 3.25 Notification indicator : isolated
	 */
	public static final int _NI_ISOLATED = 0x45;
	/**
	 * See Q.763 3.25 Notification indicator : reattached
	 */
	public static final int _NI_REATTACHED = 0x46;

	/**
	 * See Q.763 3.25 Notification indicator : other party isolated
	 */
	public static final int _NI_OTHER_PARTY_ISOLATED = 0x47;

	/**
	 * See Q.763 3.25 Notification indicator : other party reattached
	 */
	public static final int _NI_OTHER_PARTY_REATTACHED = 0x48;
	/**
	 * See Q.763 3.25 Notification indicator : other party split
	 */
	public static final int _NI_OTHER_PARTY_SPLIT = 0x49;
	/**
	 * See Q.763 3.25 Notification indicator : other party disconnected
	 */
	public static final int _NI_OTHER_PARTY_DISCONNECTED = 0x4A;

	/**
	 * See Q.763 3.25 Notification indicator : conference floating
	 */
	public static final int _NI_CONFERENCE_FLOATING = 0x4B;

	/**
	 * See Q.763 3.25 Notification indicator : call is a waiting call
	 */
	public static final int _NI_CALL_IS_AWAITING = 0xC0;

	/**
	 * See Q.763 3.25 Notification indicator : diversion activated (used in
	 * DSS1)
	 */
	public static final int _NI_DIVERSION_ACTIVATED = 0x68;

	/**
	 * See Q.763 3.25 Notification indicator : call transfer, alerting
	 */
	public static final int _NI_CALL_TRANSFER_ALERTING = 0x69;

	/**
	 * See Q.763 3.25 Notification indicator : call transfer, active
	 */
	public static final int _NI_CALL_TRANSFER_ACTIVE = 0x6A;

	/**
	 * See Q.763 3.25 Notification indicator : remote hold
	 */
	public static final int _NI_REMOTE_HOLD = 0x79;

	/**
	 * See Q.763 3.25 Notification indicator : remote retrieval
	 */
	public static final int _NI_REMOTE_RETRIEVAL = 0x8A;

	/**
	 * See Q.763 3.25 Notification indicator : call is diverting
	 */
	public static final int _NI_RCID = 0x8B;

	public static final int _PARAMETER_CODE = 0x2C;
	private int[] notificationIndicator;

	public GenericNotificationIndicator(byte[] b) {
		super();
		decodeElement(b);
	}

	public GenericNotificationIndicator(int[] notificationIndicator) {
		super();
		this.setNotificationIndicator(notificationIndicator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length < 2) {
			throw new IllegalArgumentException("byte[] must  not be null and length must be 1 or greater");
		}

		this.notificationIndicator = new int[b.length];

		for (int i = 0; i < b.length; i++) {
			int extFlag = (b[i] >> 7) & 0x01;
			if (extFlag == 0x01 && (b.length - 1 > i)) {
				throw new IllegalArgumentException("Extenstion flag idnicates end of data, however byte[] has more octets. Index: " + i);
			}
			this.notificationIndicator[i] = b[i] & 0x7F;
		}
		return this.notificationIndicator.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		byte[] b = new byte[this.notificationIndicator.length];

		for (int index = 0; index < b.length; index++) {
			b[index] = (byte) (this.notificationIndicator[index] & 0x7F);
		}

		// sets extension bit to show that we dont have more octets
		b[b.length - 1] |= 1 << 7;

		return b;
	}

	public int[] getNotificationIndicator() {
		return notificationIndicator;
	}

	public void setNotificationIndicator(int[] notificationIndicator) {
		if (notificationIndicator == null) {
			throw new IllegalArgumentException("Notification indicator must not be null");
		}
		this.notificationIndicator = notificationIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
