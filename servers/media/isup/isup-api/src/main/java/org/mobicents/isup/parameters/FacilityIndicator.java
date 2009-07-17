/**
 * Start time:11:50:01 2009-03-31<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:11:50:01 2009-03-31<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class FacilityIndicator extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x18;
	/**
	 * See Q.763 3.22 facility indicator user-to-user service
	 */
	public static final int _FACILITY_INDICATOR_UTUS = 2;
	private int facilityIndicator = 0;

	public FacilityIndicator(byte[] b) {
		super();
		decodeElement(b);
	}

	public FacilityIndicator(int facilityIndicator) {
		super();
		this.facilityIndicator = facilityIndicator;
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

		this.facilityIndicator = b[0];
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		byte[] b = { (byte) this.facilityIndicator };
		return b;
	}

	public int getFacilityIndicator() {
		return facilityIndicator;
	}

	public void setFacilityIndicator(int facilityIndicator) {
		this.facilityIndicator = facilityIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
