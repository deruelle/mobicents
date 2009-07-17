/**
 * Start time:15:02:53 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:15:02:53 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CollectCallRequest extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x79;
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	/**
	 * See Q.763 3.81 Collect call request indicator : no indication
	 */
	public final static boolean _CCRI_NO_INDICATION = false;
	/**
	 * See Q.763 3.81 Collect call request indicator : collect call requested
	 */
	public final static boolean _CCRI_CCR = true;

	private boolean collectCallRequested = false;

	public CollectCallRequest(boolean collectCallRequested) {
		super();
		this.collectCallRequested = collectCallRequested;
	}

	public CollectCallRequest(byte[] b) {
		super();
		decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must not be null and length must be 1");
		}
		this.collectCallRequested = (b[0] & 0x01) == _TURN_ON;

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return new byte[] { (byte) (this.collectCallRequested ? _TURN_ON : _TURN_OFF) };
	}

	public boolean isCollectCallRequested() {
		return collectCallRequested;
	}

	public void setCollectCallRequested(boolean collectCallRequested) {
		this.collectCallRequested = collectCallRequested;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
