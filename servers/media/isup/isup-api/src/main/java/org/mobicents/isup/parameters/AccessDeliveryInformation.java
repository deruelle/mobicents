/**
 * Start time:13:31:04 2009-03-30<br>
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
 * Start time:13:31:04 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AccessDeliveryInformation extends AbstractParameter {

	/**
	 * See Q.763 3.2  Access delivery indicator: set-up message generated
	 */
	public static final int _ACI_SME= 0;
	
	/**
	 * See Q.763 3.2  Access delivery indicator:no set-up message generated
	 */
	public static final int _ACI_NO_SME= 1;

	public final static int _PARAMETER_CODE = 0x2E;
	
	private int accessDeliveryIndicator;
	
	
	public AccessDeliveryInformation(int accessDeliveryIndicator) {
		super();
		this.accessDeliveryIndicator = accessDeliveryIndicator;
	}

	public AccessDeliveryInformation(byte[] representation) {
		super();
		this.decodeElement(representation);
	}

	
	/* (non-Javadoc)
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if(b == null || b.length!=1)
		{
			throw new IllegalArgumentException("byte[] must not be null or have different size than 1");
		}
		this.accessDeliveryIndicator = (byte) (b[0] & 0x01);
		
		return 1;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		
		return new byte[]{(byte)this.accessDeliveryIndicator};
	}

	/* (non-Javadoc)
	 * @see org.mobicents.isup.ISUPComponent#encodeElement(java.io.ByteArrayOutputStream)
	 */
	public int encodeElement(ByteArrayOutputStream bos) throws IOException {
		bos.write(this.accessDeliveryIndicator);
		return 1;
	}

	public int getAccessDeliveryIndicator() {
		return accessDeliveryIndicator;
	}

	public void setAccessDeliveryIndicator(int accessDeliveryIndicator) {
		this.accessDeliveryIndicator = accessDeliveryIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}

	
}
