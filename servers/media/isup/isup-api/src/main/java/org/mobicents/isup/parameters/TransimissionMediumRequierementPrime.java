/**
 * Start time:18:41:12 2009-04-03<br>
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
 * Start time:18:41:12 2009-04-03<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class TransimissionMediumRequierementPrime extends AbstractParameter {
	
	public static final int _PARAMETER_CODE = 0x3E;
	/**
	 * 0 0 0 0 0 0 0 0 speech
	 */
	public static int _MEDIUM_SPEECH = 0;

	/**
	 * 0 0 0 0 0 0 1 0 - 64 kbit/s unrestricted
	 */
	public static int _MEDIUM_64_KBIT_UNRESTRICTED = 2;
	/**
	 * 0 0 0 0 0 0 1 1 - 3.1 kHz audio
	 */
	public static int _MEDIUM_3_1_KHz_AUDIO = 3;
	/**
	 * 0 0 0 0 0 1 0 0 - reserved for alternate speech (service 2)/64 kbit/s
	 * unrestricted (service 1)
	 */
	public static int _MEDIUM_RESERVED_ASS2 = 4;
	/**
	 * 0 0 0 0 0 1 0 1 - reserved for alternate 64 kbit/s unrestricted (service
	 * 1)/speech (service 2)
	 */
	public static int _MEDIUM_RESERVED_ASS1 = 5;
	/**
	 * 0 0 0 0 0 1 1 0 - 64 kbit/s preferred
	 */
	public static int _MEDIUM_64_KBIT_PREFERED = 6;
	/**
	 * 0 0 0 0 0 1 1 1 - 2 × 64 kbit/s unrestricted
	 */
	public static int _MEDIUM_2x64_KBIT_UNRESTRICTED = 7;
	/**
	 * 0 0 0 0 1 0 0 0 - 384 kbit/s unrestricted
	 */
	public static int _MEDIUM_384_KBIT_UNRESTRICTED = 8;
	/**
	 * 0 0 0 0 1 0 0 1 - 1536 kbit/s unrestricted
	 */
	public static int _MEDIUM_1536_KBIT_UNRESTRICTED = 9;
	/**
	 * 0 0 0 0 1 0 1 0 - 1920 kbit/s unrestricted
	 */
	public static int _MEDIUM_1920_KBIT_UNRESTRICTED = 10;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */

	public TransimissionMediumRequierementPrime(int transimissionMediumRequirement) {
		super();
		this.transimissionMediumRequirement = transimissionMediumRequirement;
	}

	public TransimissionMediumRequierementPrime(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	// Defualt indicate speech
	private int transimissionMediumRequirement;

	// FIXME: again wrapper class but hell there is a lot of statics....
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length != 1) {
			throw new ParameterRangeInvalidException("byte[] must  not be null and length must  be 1");
		}

		this.transimissionMediumRequirement = b[0];

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		return new byte[] { (byte) this.transimissionMediumRequirement };
	}

	public int getTransimissionMediumRequirement() {
		return transimissionMediumRequirement;
	}

	public void setTransimissionMediumRequirement(int transimissionMediumRequirement) {
		this.transimissionMediumRequirement = transimissionMediumRequirement;
	}
	
	public int getCode() {

		return _PARAMETER_CODE;
	}

}
