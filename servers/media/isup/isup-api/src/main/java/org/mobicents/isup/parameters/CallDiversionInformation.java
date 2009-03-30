/**
 * Start time:14:32:32 2009-03-30<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Start time:14:32:32 2009-03-30<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CallDiversionInformation extends AbstractParameter {

	/**
	 * see Q.763 3.6
	 */
	public static final int _NOTIFICATION_SUBSCRIPTION_OPTIONS_UNKNOWN = 0;

	/**
	 * see Q.763 3.6
	 */
	public static final int _NOTIFICATION_SUBSCRIPTION_OPTIONS_PRESENTATION_NOT_ALLOWED = 1;

	/**
	 * see Q.763 3.6
	 */
	public static final int _NOTIFICATION_SUBSCRIPTION_OPTIONS_PRESENTATION_ALLOWED_WITH_REDIRECTION_NUMBER = 2;

	/**
	 * see Q.763 3.6
	 */
	public static final int _NOTIFICATION_SUBSCRIPTION_OPTIONS_PRESENTATION_ALLOWED_WITHOUT_REDIRECTION_NUMBER = 3;

	private int notificationSubscriptionOptions = 0;

	/**
	 * see Q.763 3.6
	 */
	public static final int _REDIRECTING_REASON_UNKNOWN = 0;

	/**
	 * see Q.763 3.6
	 */
	public static final int _REDIRECTING_REASON_USER_BUSY = 1;

	/**
	 * see Q.763 3.6
	 */
	public static final int _REDIRECTING_REASON_NO_REPLY = 2;

	/**
	 * see Q.763 3.6
	 */
	public static final int _REDIRECTING_REASON_UNCONDITIONAL = 3;

	/**
	 * see Q.763 3.6
	 */
	public static final int _REDIRECTING_REASON_DEFLECTION_DURING_ALERTING = 4;
	/**
	 * see Q.763 3.6
	 */
	public static final int _REDIRECTING_REASON_DEFLECTION_IMMEDIATE_RESPONSE = 5;

	/**
	 * see Q.763 3.6
	 */
	public static final int _REDIRECTING_REASON_MOBILSE_SUBSCRIBER_NOT_REACHABLE = 6;
	private int redirectingReason = 0;

	public CallDiversionInformation(int notificationSubscriptionOptions, int redirectingReason) {
		super();
		this.notificationSubscriptionOptions = notificationSubscriptionOptions;
		this.redirectingReason = redirectingReason;
	}

	public CallDiversionInformation(byte[] b) {
		decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must not be null or have different size than 1");
		}

		int v = b[0];
		this.notificationSubscriptionOptions = v & 0x07;
		this.redirectingReason = (v >> 3) & 0x0F;

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		byte[] b = new byte[1];

		int v = 0;
		v |= this.notificationSubscriptionOptions & 0x07;
		v |= (this.redirectingReason & 0x0F) << 3;
		b[0] = (byte) v;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.ISUPComponent#encodeElement(java.io.ByteArrayOutputStream
	 * )
	 */
	public int encodeElement(ByteArrayOutputStream bos) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNotificationSubscriptionOptions() {
		return notificationSubscriptionOptions;
	}

	public void setNotificationSubscriptionOptions(int notificationSubscriptionOptions) {
		this.notificationSubscriptionOptions = notificationSubscriptionOptions;
	}

	public int getRedirectingReason() {
		return redirectingReason;
	}

	public void setRedirectingReason(int redirectingReason) {
		this.redirectingReason = redirectingReason;
	}

}
