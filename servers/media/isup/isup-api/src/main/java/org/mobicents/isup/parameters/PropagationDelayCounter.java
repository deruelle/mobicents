/**
 * Start time:14:20:15 2009-04-02<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:14:20:15 2009-04-02<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class PropagationDelayCounter extends AbstractParameter {

	private int propagationDelay = 0;

	public PropagationDelayCounter(byte[] b) {
		super();
		decodeElement(b);
	}

	public PropagationDelayCounter(int propagationDelay) {
		super();
		this.propagationDelay = propagationDelay;
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

		this.propagationDelay = b[0] << 8;
		this.propagationDelay |= b[1];
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		byte b0 = (byte) (this.propagationDelay >> 8);
		byte b1 = (byte) this.propagationDelay;
		return new byte[] { b0, b1 };
	}

	public int getPropagationDelay() {
		return propagationDelay;
	}

	public void setPropagationDelay(int propagationDelay) {
		this.propagationDelay = propagationDelay;
	}

}
