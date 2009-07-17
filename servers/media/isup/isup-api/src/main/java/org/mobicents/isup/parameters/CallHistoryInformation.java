/**
 * Start time:15:04:29 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Start time:15:04:29 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CallHistoryInformation extends AbstractParameter {
	public static final int _PARAMETER_CODE = 0;
	// XXX: again this goes aganist usuall way.
	private int callHistory;

	public CallHistoryInformation(byte[] b) {
		super();
		decodeElement(b);
	}

	public CallHistoryInformation(int callHistory) {
		super();
		this.callHistory = callHistory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		// This one is other way around, as Eduardo might say.
		if (b == null || b.length != 2) {
			throw new IllegalArgumentException("byte[] must  not be null and length must be 2");
		}

//		this.callHistory = b[0] << 8;
//		this.callHistory |= b[1];
//		//We need this, cause otherwise we get corrupted number
//		this.callHistory &=0xFFFF;
		this.callHistory = ((b[0] << 8) | b[1]) & 0xFFFF;
		
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		byte b0 = (byte) (this.callHistory >> 8);
		byte b1 = (byte) this.callHistory;
		return new byte[] { b0, b1 };
	}

	public int getCallHistory() {
		return callHistory;
	}

	public void setCallHistory(int callHistory) {
		this.callHistory = callHistory;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
