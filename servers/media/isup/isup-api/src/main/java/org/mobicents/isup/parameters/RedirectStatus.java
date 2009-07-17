/**
 * Start time:09:49:43 2009-04-06<br>
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
 * Start time:09:49:43 2009-04-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class RedirectStatus extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x8A;
	/**
	 * See Q.763 3.98 Redirect status indicator : not used
	 */
	public static int _RSI_NOT_USED = 0;
	/**
	 * See Q.763 3.98 Redirect status indicator : ack of redirection
	 */
	public static int _RSI_AOR = 1;
	/**
	 * See Q.763 3.98 Redirect status indicator : redirection will not be
	 * invoked
	 */
	public static int _RSI_RWNBI = 2;

	private byte[] status;

	public RedirectStatus(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		try{
			setStatus(b);
		}catch(Exception e)
		{
			throw new ParameterRangeInvalidException(e);
		}
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		for (int index = 0; index < this.status.length; index++) {
			this.status[index] = (byte) (this.status[index] & 0x03);
		}

		this.status[this.status.length - 1] = (byte) ((this.status[this.status.length - 1]) | (0x01 << 7));
		return this.status;
	}

	public byte[] getStatus() {
		return status;
	}

	public void setStatus(byte[] status) {
		if (status == null || status.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}
		this.status = status;
	}

	public static int getStatusIndicator(byte b) {
		return b & 0x03;
	}
	public int getCode() {

		return _PARAMETER_CODE;
	}
}
