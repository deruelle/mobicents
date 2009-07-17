/**
 * Start time:13:29:12 2009-04-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:13:29:12 2009-04-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class CallTransferReference extends AbstractParameter {
	public static final int _PARAMETER_CODE = 0x43;
	private int callTransferReference = 0;

	public CallTransferReference(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	public CallTransferReference(int callTransferReference) {
		super();
		this.callTransferReference = callTransferReference;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length != 1) {
			throw new ParameterRangeInvalidException("byte[] must  not be null and length must  be 1");
		}
		this.callTransferReference = b[0];
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return new byte[] { (byte) this.callTransferReference };
	}

	public int getCallTransferReference() {
		return callTransferReference;
	}

	public void setCallTransferReference(int callTransferReference) {
		this.callTransferReference = callTransferReference;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
