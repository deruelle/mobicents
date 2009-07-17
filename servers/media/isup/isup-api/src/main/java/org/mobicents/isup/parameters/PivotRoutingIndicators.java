/**
 * Start time:16:16:18 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:16:16:18 2009-04-05<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class PivotRoutingIndicators extends AbstractParameter {
	public static final int _PARAMETER_CODE = 0x7C;
	/**
	 * See Q.763 3.85 Pivot routing indicators : no indication
	 */
	public static final int _PRI_NO_INDICATION = 0;
	/**
	 * See Q.763 3.85 Pivot routing indicators : pivote request
	 */
	public static final int _PRI_PIVOT_REQUEST = 1;
	/**
	 * See Q.763 3.85 Pivot routing indicators : cancel pivot request
	 */
	public static final int _PRI_C_PR = 2;
	/**
	 * See Q.763 3.85 Pivot routing indicators : pivot request failure
	 */
	public static final int _PRI_PRF = 3;
	/**
	 * See Q.763 3.85 Pivot routing indicators : interworking to redirection
	 * prohibited (backward) (national use)
	 */
	public static final int _PRI_ITRP = 4;

	private byte[] pivotRoutingIndicators;

	public PivotRoutingIndicators(byte[] pivotRoutingIndicators) {
		super();
		decodeElement(pivotRoutingIndicators);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		for (int index = 0; index < this.pivotRoutingIndicators.length; index++) {
			this.pivotRoutingIndicators[index] = (byte) (this.pivotRoutingIndicators[index] & 0x7F);
		}

		this.pivotRoutingIndicators[this.pivotRoutingIndicators.length - 1] = (byte) ((this.pivotRoutingIndicators[this.pivotRoutingIndicators.length - 1]) | (0x01 << 7));
		return this.pivotRoutingIndicators;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {

		setPivotRoutingIndicators(b);
		return b.length;
	}

	public byte[] getPivotRoutingIndicators() {
		return pivotRoutingIndicators;
	}

	public void setPivotRoutingIndicators(byte[] pivotRoutingIndicators) {
		if (pivotRoutingIndicators == null || pivotRoutingIndicators.length == 0) {
			throw new IllegalArgumentException("byte[] must not be null and length must be greater than 0");
		}
		this.pivotRoutingIndicators = pivotRoutingIndicators;
	}
	public int getCode() {

		return _PARAMETER_CODE;
	}
}
