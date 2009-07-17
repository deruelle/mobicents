/**
 * Start time:14:32:32 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:14:32:32 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CallDiversionInformation extends AbstractParameter {

	/**
	 * see Q.763 3.6 Notification subscription options Unknown
	 */
	public static final int _NSO_UNKNOWN = 0;

	/**
	 * see Q.763 3.6 Notification subscription options presentation not allowed
	 */
	public static final int _NSO_P_NOT_ALLOWED = 1;

	/**
	 * see Q.763 3.6 Notification subscription options presentation allowed with
	 * redirection number
	 */
	public static final int _NSO_P_A_WITH_RN = 2;

	/**
	 * see Q.763 3.6 Notification subscription options presentation allowed
	 * without redirection number
	 */
	public static final int _NSO_P_A_WITHOUT_RN = 3;

	private int notificationSubscriptionOptions = 0;

	/**
	 * see Q.763 3.6 Notification subscription options Unknown
	 */
	public static final int _REDIRECTING_REASON_UNKNOWN = 0;

	/**
	 * see Q.763 3.6 Redirecting reason User busy
	 */
	public static final int _REDIRECTING_REASON_USER_BUSY = 1;

	/**
	 * see Q.763 3.6 Redirecting reason no reply
	 */
	public static final int _REDIRECTING_REASON_NO_REPLY = 2;

	/**
	 * see Q.763 3.6 Redirecting reason unconditional
	 */
	public static final int _REDIRECTING_REASON_UNCONDITIONAL = 3;

	/**
	 * see Q.763 3.6 Redirecting reason deflection during alerting
	 */
	public static final int _REDIRECTING_REASON_DDA = 4;
	/**
	 * see Q.763 3.6 Redirecting reason deflection immediate response
	 */
	public static final int _REDIRECTING_REASON_DIR = 5;

	/**
	 * see Q.763 3.6 Redirecting reason mobile subscriber not reachable
	 */
	public static final int _REDIRECTING_REASON_MSNR = 6;

	public static final int _PARAMETER_CODE = 0x36;
	private int redirectingReason = 0;

	public CallDiversionInformation(int notificationSubscriptionOptions, int redirectingReason) {
		super();
		this.notificationSubscriptionOptions = notificationSubscriptionOptions;
		this.redirectingReason = redirectingReason;
	}

	public CallDiversionInformation(byte[] b) throws ParameterRangeInvalidException{
		decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length != 1) {
			throw new ParameterRangeInvalidException("byte[] must not be null or have different size than 1");
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
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.ISUPComponent#encodeElement(java.io.ByteArrayOutputStream
	 * )
	 */
	public int encodeElement(ByteArrayOutputStream bos) throws IOException {
		byte[] b = this.encodeElement();
		bos.write(b);
		return b.length;
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

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
