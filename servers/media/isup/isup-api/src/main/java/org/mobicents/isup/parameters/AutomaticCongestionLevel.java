/**
 * Start time:13:42:13 2009-03-30<br>
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
 * Start time:13:42:13 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class AutomaticCongestionLevel extends AbstractParameter {
	public static final int _PARAMETER_CODE = 0x27;
	public static final int _CONGESTION_LEVE_1_EXCEEDED = 1;
	public static final int _CONGESTION_LEVE_2_EXCEEDED = 2;

	private int automaticCongestionLevel = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must not be null or have different size than 1");
		}
		this.automaticCongestionLevel = b[0] & 0x01;
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		return new byte[] { (byte) this.automaticCongestionLevel };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.ISUPComponent#encodeElement(java.io.ByteArrayOutputStream
	 * )
	 */
	public int encodeElement(ByteArrayOutputStream bos) throws IOException {
		bos.write(this.automaticCongestionLevel);
		return 1;
	}

	public int getAutomaticCongestionLevel() {
		return automaticCongestionLevel;
	}

	public void setAutomaticCongestionLevel(int automaticCongestionLevel) {
		this.automaticCongestionLevel = automaticCongestionLevel & 0x01;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
