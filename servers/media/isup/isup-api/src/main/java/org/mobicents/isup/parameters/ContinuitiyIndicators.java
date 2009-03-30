/**
 * Start time:18:28:42 2009-03-30<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:18:28:42 2009-03-30<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class ContinuitiyIndicators extends AbstractParameter {

	/**
	 * See Q.763 3.18
	 */
	public static final int _CONTINUITY_CHECK_FAILED = 0;

	/**
	 * See Q.763 3.18
	 */
	public static final int _CONTINUITY_CHECK_SUCCESSFUL = 1;

	private int continuityCheck = 0;

	public ContinuitiyIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	public ContinuitiyIndicators(int continuityCheck) {
		super();
		this.continuityCheck = continuityCheck;
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
		this.continuityCheck = b[0] & 0x01;
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return new byte[] { (byte) (this.continuityCheck & 0x01) };
	}

}
