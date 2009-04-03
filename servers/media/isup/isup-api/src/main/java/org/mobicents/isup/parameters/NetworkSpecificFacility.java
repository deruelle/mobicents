/**
 * Start time:09:37:50 2009-04-02<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Start time:09:37:50 2009-04-02<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class NetworkSpecificFacility extends AbstractParameter {

	/**
	 * This tells us to include byte 1a - sets lengthOfNetworkIdentification to
	 * 1+networkdIdentification.length
	 */
	private boolean includeNetworkIdentification = false;

	private int lengthOfNetworkIdentification = 0;
	private int typeOfNetworkIdentification = 0;
	private int NetworkdIdentificationPlan = 0;
	private byte[] networkdIdentification = null;
	private byte[] networkSpecificaFacilityIndicator = null;

	public NetworkSpecificFacility(byte[] b) {
		super();
		decodeElement(b);
	}

	public NetworkSpecificFacility(boolean includeNetworkIdentification, byte typeOfNetworkIdentification, byte networkdIdentificationPlan, byte[] networkdIdentification,
			byte[] networkSpecificaFacilityIndicator) {
		super();
		this.includeNetworkIdentification = includeNetworkIdentification;
		this.typeOfNetworkIdentification = typeOfNetworkIdentification;
		NetworkdIdentificationPlan = networkdIdentificationPlan;
		this.networkdIdentification = networkdIdentification;
		this.networkSpecificaFacilityIndicator = networkSpecificaFacilityIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("byte[] must nto be null or have length greater than 1");
		}
		int shift = 0;
		this.lengthOfNetworkIdentification = b[shift];
		shift++;

		if (this.lengthOfNetworkIdentification > 0) {
			// We ignore ext bit, we dont need it ?
			this.typeOfNetworkIdentification = (byte) ((b[shift] >> 4) & 0x07);
			this.NetworkdIdentificationPlan = (byte) (b[shift] & 0x0F);
			shift++;
			byte[] _networkId = new byte[this.lengthOfNetworkIdentification - 1];
			for (int i = 2; i < this.lengthOfNetworkIdentification + 1; i++, shift++) {

				_networkId[i - 2] = (byte) (b[i] & 0x7F);
			}

			// now lets set it.
			if (_networkId.length > 0) {

				_networkId[_networkId.length - 1] |= _networkId[_networkId.length - 1] & 0x80;
			}
			this.setNetworkdIdentification(_networkId);
		}
		if (shift + 1 == b.length) {
			throw new IllegalArgumentException("There is no facility indicator. This part is mandatory!!!");
		}
		byte[] _facility = new byte[b.length - shift - 1];
		for (; shift < b.length; shift++) {
			_facility[b.length - shift] = b[shift];
		}

		return shift;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		bos.write(this.lengthOfNetworkIdentification);
		// This should always be set to true if there is network ID
		if (this.includeNetworkIdentification) {
			int b1 = 0;
			b1 = ((this.typeOfNetworkIdentification & 0x07) << 4);
			b1 |= (this.NetworkdIdentificationPlan & 0x0F);

			if (this.networkdIdentification != null && this.networkdIdentification.length > 0) {
				b1 |= 0x80;
				bos.write(b1);
				for (byte bb : networkdIdentification)
					bos.write(bb);
			} else {
				bos.write(b1);
			}
		}

		if (this.networkSpecificaFacilityIndicator == null) {
			throw new IllegalArgumentException("Network Specific Facility must not be null");
		}
		bos.write(this.networkSpecificaFacilityIndicator);

		return bos.toByteArray();
	}

	public boolean isIncludeNetworkIdentification() {
		return includeNetworkIdentification;
	}

	public void setIncludeNetworkIdentification(boolean includeNetworkIdentification) {
		this.includeNetworkIdentification = includeNetworkIdentification;
		if (this.networkdIdentification == null) {
			if (this.includeNetworkIdentification)
				this.lengthOfNetworkIdentification = 1;
		} else {
			this.lengthOfNetworkIdentification = (byte) (this.networkdIdentification.length + 1);
		}
	}

	public int getLengthOfNetworkIdentification() {
		return lengthOfNetworkIdentification;
	}

	public void setLengthOfNetworkIdentification(int lengthOfNetworkIdentification) {
		this.lengthOfNetworkIdentification = lengthOfNetworkIdentification;
	}

	public int getTypeOfNetworkIdentification() {
		return typeOfNetworkIdentification;
	}

	public void setTypeOfNetworkIdentification(byte typeOfNetworkIdentification) {
		this.typeOfNetworkIdentification = typeOfNetworkIdentification;
	}

	public int getNetworkdIdentificationPlan() {
		return NetworkdIdentificationPlan;
	}

	public void setNetworkdIdentificationPlan(byte networkdIdentificationPlan) {
		NetworkdIdentificationPlan = networkdIdentificationPlan;
	}

	public byte[] getNetworkdIdentification() {
		return networkdIdentification;
	}

	public void setNetworkdIdentification(byte[] networkdIdentification) {

		if (networkdIdentification != null && networkdIdentification.length > Byte.MAX_VALUE * 2 - 1) {
			throw new IllegalArgumentException("Length of Network Identification part must not be greater than: " + (Byte.MAX_VALUE * 2 - 1));
		}
		this.networkdIdentification = networkdIdentification;
		this.setIncludeNetworkIdentification(this.includeNetworkIdentification);
	}

	public byte[] getNetworkSpecificaFacilityIndicator() {
		return networkSpecificaFacilityIndicator;
	}

	public void setNetworkSpecificaFacilityIndicator(byte[] networkSpecificaFacilityIndicator) {
		this.networkSpecificaFacilityIndicator = networkSpecificaFacilityIndicator;
	}

}
