/**
 * Start time:17:25:24 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:17:25:24 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class ServiceActivation extends AbstractParameter {

	// FIXME: this is again simple container
	/**
	 * See Q.763 3.49
	 */
	public final static byte _FEATURE_CODE_CALL_TRANSFER = 1;

	public static final int _PARAMETER_CODE = 0x33;

	private byte[] featureCodes;

	public ServiceActivation(byte[] featureCodes) {
		super();
		this.featureCodes = featureCodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		this.featureCodes = b;
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return this.featureCodes;
	}

	public byte[] getFeatureCodes() {
		return featureCodes;
	}

	public void setFeatureCodes(byte[] featureCodes) {
		this.featureCodes = featureCodes;
	}
	public int getCode() {

		return _PARAMETER_CODE;
	}
}
