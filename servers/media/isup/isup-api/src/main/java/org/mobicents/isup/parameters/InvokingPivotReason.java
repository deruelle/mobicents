/**
 * Start time:09:11:07 2009-04-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:09:11:07 2009-04-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class InvokingPivotReason extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0;
	/**
	 * See Q.763 3.94.4 Pivot Reason : unknown/ not available
	 */
	public static final int _PR_UNKNOWN = 0;
	/**
	 * See Q.763 3.94.4 Pivot Reason : service provider portability (national
	 * use)
	 */
	public static final int _PR_SPP = 1;
	/**
	 * See Q.763 3.94.4 Pivot Reason : reserved for location portability
	 */
	public static final int _PR_RFLP = 2;
	/**
	 * See Q.763 3.94.4 Pivot Reason : reserved for service portability
	 */
	public static final int _PR_RFSP = 3;

	private byte[] reasons = null;

	public InvokingPivotReason(byte[] reasons) {
		super();
		decodeElement(reasons);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		this.setReasons(b);
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		for (int index = 0; index < this.reasons.length; index++) {
			this.reasons[index] = (byte) (this.reasons[index] & 0x7F);
		}

		this.reasons[this.reasons.length - 1] = (byte) ((this.reasons[this.reasons.length - 1]) | (0x01 << 7));
		return this.reasons;
	}

	public byte[] getReasons() {
		return reasons;
	}

	public void setReasons(byte[] reasons) {
		if (reasons == null || reasons.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}
		this.reasons = reasons;
	}

	public static int getReason(byte b) {
		return b & 0x7F;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
