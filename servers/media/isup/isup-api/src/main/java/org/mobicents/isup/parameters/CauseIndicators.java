/**
 * Start time:15:14:32 2009-03-30<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Start time:15:14:32 2009-03-30<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class CauseIndicators extends AbstractParameter {

	// FIXME: we ignore EXT fields , is this ok ?
	/**
	 * See Q.850
	 */
	public static final int _CODING_STANDARD_ITUT = 0;

	/**
	 * See Q.850
	 */
	public static final int _CODING_STANDARD_IOS_IEC = 1;

	/**
	 * See Q.850
	 */
	public static final int _CODING_STANDARD_NATIONAL = 2;
	/**
	 * See Q.850
	 */
	public static final int _CODING_STANDARD_SPECIFIC = 3;
	private int codingStandard = 0;

	/**
	 * See Q.850
	 */
	public static final int _LOCATION_USER = 0;

	/**
	 * See Q.850 private network serving the local user (LPN)
	 */
	public static final int _LOCATION_PRIVATE_NSLU = 1;

	/**
	 * See Q.850 public network serving the local user (LN)
	 */
	public static final int _LOCATION_PUBLIC_NSLU = 2;

	/**
	 * See Q.850 transit network (TN)
	 */
	public static final int _LOCATION_TRANSIT_NETWORK = 3;

	/**
	 * See Q.850 private network serving the remote user (RPN)
	 */
	public static final int _LOCATION_PRIVATE_NSRU = 5;

	/**
	 * See Q.850 public network serving the remote user (RLN)
	 */
	public static final int _LOCATION_PUBLIC_NSRU = 4;
	/**
	 * See Q.850
	 */
	public static final int _LOCATION_INTERNATIONAL_NETWORK = 7;

	/**
	 * See Q.850 network beyond interworking point (BI)
	 */
	public static final int _LOCATION_NETWORK_BEYOND_IP = 10;

	private int location = 0;
	private int causeValue = 0;
	private byte[] diagnostics = null;

	public CauseIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	public CauseIndicators(int codingStandard, int location, int causeValue, byte[] diagnostics) {
		super();
		this.codingStandard = codingStandard;
		this.location = location;
		this.causeValue = causeValue;
		this.diagnostics = diagnostics;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length < 2) {
			throw new IllegalArgumentException("byte[] must not be null or has size less than 2");
		}

		// first two bytes are mandatory
		int v = 0;
		// remove ext
		v = b[0] & 0x7F;
		this.location = v & 0x0F;
		this.codingStandard = v >> 5;
		v = 0;
		v = b[1] & 0x7;

		if (b.length == 2) {
			return 2;
		} else {
			if ((b.length - 2) % 3 != 0) {
				throw new IllegalArgumentException("Diagnostics partt  must have 3xN bytes, it has: " + (b.length - 2));
			}

			int byteCounter = 2;

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (int i = 2; i < b.length; i++) {
				bos.write(b[i]);
				byteCounter++;
			}

			this.diagnostics = bos.toByteArray();

			return byteCounter;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		int v = this.location & 0x0F;
		v |= (this.codingStandard & 0x03) << 5;
		bos.write(v);
		bos.write(this.causeValue);
		if (this.diagnostics != null)
			bos.write(this.diagnostics);
		return bos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.ISUPComponent#encodeElement(java.io.ByteArrayOutputStream
	 * )
	 */
	public int encodeElement(ByteArrayOutputStream bos) throws IOException {
		byte[] b = this.encodeElement();
		bos.write(b);
		return b.length;
	}

	public int getCodingStandard() {
		return codingStandard;
	}

	public void setCodingStandard(int codingStandard) {
		this.codingStandard = codingStandard;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public int getCauseValue() {
		return causeValue;
	}

	public void setCauseValue(int causeValue) {
		this.causeValue = causeValue;
	}

	public byte[] getDiagnostics() {
		return diagnostics;
	}

	public void setDiagnostics(byte[] diagnostics) {
		this.diagnostics = diagnostics;
	}

}
