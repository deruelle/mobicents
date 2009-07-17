/**
 * Start time:16:59:42 2009-04-03<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:16:59:42 2009-04-03<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class SuspendResumeIndicators extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x22;
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	private boolean suspendResumeIndicator;

	/**
	 * See Q.763 3.52 Suspend/resume indicator : ISDN subscriber initiated
	 */
	public static final boolean _SRI_ISDN_SI = false;

	/**
	 * See Q.763 3.52 Suspend/resume indicator : network initiated
	 */
	public static final boolean _SRI_NI = true;

	public SuspendResumeIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	public SuspendResumeIndicators(boolean suspendResumeIndicator) {
		super();
		this.suspendResumeIndicator = suspendResumeIndicator;
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

		this.suspendResumeIndicator = (b[0] & 0x01) == _TURN_ON;

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return new byte[] { (byte) (this.suspendResumeIndicator ? _TURN_ON : _TURN_OFF) };
	}

	public boolean isSuspendResumeIndicator() {
		return suspendResumeIndicator;
	}

	public void setSuspendResumeIndicator(boolean suspendResumeIndicator) {
		this.suspendResumeIndicator = suspendResumeIndicator;
	}
	public int getCode() {

		return _PARAMETER_CODE;
	}
}
