/**
 * Start time:12:43:13 2009-04-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:12:43:13 2009-04-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class UserTeleserviceInformation extends AbstractParameter {
	// NOTE: Q.931 4.5.17 High layer compatibility --> it has the same structure
	// and encoding
	public static final int _PARAMETER_CODE = 0x34;
	/**
	 * See Q.931 4.5.17 Coding standard : ITU-T standardized coding,
	 */
	public static final int _CODING_STANDARD_ITU_T = 0;
	/**
	 * See Q.931 4.5.17 Coding standard : ISO/IEC standard
	 */
	public static final int _CODING_STANDARD_ISO_IEC = 1;
	/**
	 * See Q.931 4.5.17 Coding standard : National standard
	 */
	public static final int _CODING_STANDARD_NATIONAL = 2;
	/**
	 * See Q.931 4.5.17 Coding standard : Standard defined for the network
	 * (either public or private) present on the network side of the interface
	 */
	public static final int _CODING_STANDARD_DFTN = 3;

	/**
	 * See Q.931 4.5.17 Interpretation : First (primary or only) high layer
	 * characteristics identification (in octet 4) to be used in the call All
	 * other values are reserved
	 */
	public static final int _INTERPRETATION_FHGCI = 4;
	/**
	 * See Q.931 4.5.17 Presentation method of protocol profile : High layer
	 * protocol profile (without specification of attributes) All other values
	 * are reserved.
	 */
	public static final int _PRESENTATION_METHOD_HLPP = 1;

	/**
	 * See Q.931 4.5.17 High layer characteristics identification : telephony
	 */
	public static final int _HLCI_TELEPHONY = 1;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Facsimile
	 * Group 2/3 (Recommendation F.182 [68])
	 */
	public static final int _HLCI_FG_2_3 = 4;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Facsimile
	 * Group 4 Class I (Recommendation F.184 [69])
	 */
	public static final int _HLCI_FG_4 = 0x21;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Facsimile
	 * service Group 4, Classes II ad III (Recommendation F.184)
	 */
	public static final int _HLCI_FG_4_C_II_III = 0x24;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Syntax based
	 * Videotex (Recommendation F.300 [73] and T.102 [74])
	 */
	public static final int _HLCI_SBVT = 0x32;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification :
	 * International Videotex interworking via gateways or interworking units
	 * (Recommendation F.300 and T.101 [75])
	 */
	public static final int _HLCI_IVTI = 0x33;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Telex
	 * service (Recommendation F.60 [76])
	 */
	public static final int _HLCI_TLS = 0x35;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Message
	 * Handling Systems (MHS) (X.400-series Recommendation [77])
	 */
	public static final int _HLCI_MHS = 0x38;

	/**
	 * See Q.931 4.5.17 High layer characteristics identification : OSI
	 * application (Note 6) (X.200-series Recommendations [78])
	 */
	public static final int _HLCI_OSIA = 0x41;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : FTAM
	 * application (ISO 8571)
	 */
	public static final int _HLCI_FTAM = 0x42;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Reserved for
	 * maintenance (Note 8)
	 */
	public static final int _HLCI_MAINTAINENCE = 0x5E;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Reserved for
	 * management (Note 8)
	 */
	public static final int _HLCI_MANAGEMENT = 0x5F;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification :
	 * Videotelephony (Recommendations F.720 [91] and F.721 [79]) and F.731
	 * profile 1a) (Note 9)
	 */
	public static final int _HLCI_VIDEOTELEPHONY = 0x60;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification :
	 * Videoconferencing Recommendation F.702 [94] and F.731 [97] Profile 1b
	 * (Note 9)
	 */
	public static final int _HLCI_VIDEO_CONF = 0x61;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Audiographic
	 * conferencing Recommendations F.702 [94] and F.731 [97] (including at
	 * least profile 2a2 and optionally 2a1, 2a3, 2b1, 2b2, and 2bc) (Notes 9
	 * and 10)
	 */
	public static final int _HLCI_AUDIOGRAPHIC_CONF = 0x62;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Reserved for
	 * audiovisual service (F.700-series Recommendations [80]) - minimal value
	 * in reserved range
	 */
	public static final int _HLCI_AUDIO_VID_LOW_RANGE = 0x63;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Reserved for
	 * audiovisual service (F.700-series Recommendations [80]) - maximum value
	 * in reserved range
	 */
	public static final int _HLCI_AUDIO_VID_HIGH_RANGE = 0x67;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Multimedia
	 * services F.700-series Recommendations [80] (Note 9)
	 */
	public static final int _HLCI_MMS = 0x68;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Reserved for
	 * audiovisual service (F.700-series Recommendations [80]) - minimal value
	 * in reserved range
	 */
	public static final int _HLCI_AUDIO_VID_LOW_RANGE2 = 0x69;
	/**
	 * See Q.931 4.5.17 High layer characteristics identification : Reserved for
	 * audiovisual service (F.700-series Recommendations [80]) - maximum value
	 * in reserved range
	 */
	public static final int _HLCI_AUDIO_VID_HIGH_RANGE2 = 0x6F;

	/**
	 * See Q.931 4.5.17 Extended High layer characteristics identification :
	 * Capability set of initial channel associated with an active 3.1 kHz audio
	 * or speech call
	 */
	public static final int _EACI_CSIC_AA_3_1_CALL = 0x21;

	/**
	 * See Q.931 4.5.17 Extended High layer characteristics identification :
	 * Capability set of initial channel of H.221
	 */
	public static final int _EACI_CSIC_H221 = 0x01;

	/**
	 * See Q.931 4.5.17 Extended High layer characteristics identification :
	 * Capability set of subsequent channel of H.221
	 */
	public static final int _EACI_CSSC_H221 = 0x02;
	private int codingStandard;
	private int interpretation;
	private int presentationMethod;
	private int highLayerCharIdentification;

	// I hate this, its soo jsr 17 style...
	private boolean eHighLayerCharIdentificationPresent;
	private boolean eVidedoTelephonyCharIdentificationPresent;

	private int eHighLayerCharIdentification;
	private int eVidedoTelephonyCharIdentification;

	public UserTeleserviceInformation(int codingStandard, int interpretation, int presentationMethod, int highLayerCharIdentification) {
		super();
		this.setCodingStandard(codingStandard);
		this.setInterpretation(interpretation);
		this.setPresentationMethod(presentationMethod);
		this.setHighLayerCharIdentification(highLayerCharIdentification);
	}

	public UserTeleserviceInformation(int codingStandard, int interpretation, int presentationMethod, int highLayerCharIdentification, int eVidedoTelephonyCharIdentification,
			boolean notImportantIgnoredParameter) {
		super();
		this.setCodingStandard(codingStandard);
		this.setInterpretation(interpretation);
		this.setPresentationMethod(presentationMethod);
		this.setHighLayerCharIdentification(highLayerCharIdentification);
		setEVidedoTelephonyCharIdentification(eVidedoTelephonyCharIdentification);
	}

	public UserTeleserviceInformation(int codingStandard, int interpretation, int presentationMethod, int highLayerCharIdentification, int eHighLayerCharIdentification) {
		super();
		this.setCodingStandard(codingStandard);
		this.setInterpretation(interpretation);
		this.setPresentationMethod(presentationMethod);
		this.setHighLayerCharIdentification(highLayerCharIdentification);
		this.setEHighLayerCharIdentification(eHighLayerCharIdentification);
	}

	public UserTeleserviceInformation(byte[] b) throws ParameterRangeInvalidException {
		super();
		// FIXME: this is only elementID

		decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length < 2) {
			throw new ParameterRangeInvalidException("byte[] must not be null and length must be greater than  1");
		}

		try {
			this.setPresentationMethod(b[0]);
			this.setInterpretation((b[0] >> 2));
			this.setCodingStandard((b[0] >> 5));
			this.setHighLayerCharIdentification(b[1]);
		} catch (Exception e) {
			throw new ParameterRangeInvalidException(e);
		}
		boolean ext = ((b[1] >> 7) & 0x01) == 0;
		if (ext && b.length != 3) {
			throw new ParameterRangeInvalidException("byte[] indicates extension to high layer cahracteristic indicator, however there isnt enough bytes");
		}
		if (!ext) {
			// ??
			return b.length;
		}

		// FIXME: add check for excesive byte?, it should not happen though?
		if (this.highLayerCharIdentification == _HLCI_MAINTAINENCE || this.highLayerCharIdentification == _HLCI_MANAGEMENT) {
			this.setEHighLayerCharIdentification(b[2] & 0x7F);
		} else if ((this.highLayerCharIdentification >= _HLCI_AUDIO_VID_LOW_RANGE && this.highLayerCharIdentification <= _HLCI_AUDIO_VID_HIGH_RANGE)
				|| (this.highLayerCharIdentification >= _HLCI_AUDIO_VID_LOW_RANGE2 && this.highLayerCharIdentification <= _HLCI_AUDIO_VID_HIGH_RANGE2)) {
			this.setEVidedoTelephonyCharIdentification(b[2] & 0x7F);
		} else {
			logger.warning("HLCI indicates value which does not allow for extension, but its present....");
		}
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		byte[] b = null;
		if (this.eHighLayerCharIdentificationPresent || this.eVidedoTelephonyCharIdentificationPresent) {
			b = new byte[3];
		} else {
			b = new byte[2];
		}

		int v = 0;
		v = this.presentationMethod & 0x03;
		v |= (this.interpretation & 0x07) << 2;
		v |= (this.codingStandard & 0x03) << 5;
		v |= 0x80;

		b[0] = (byte) v;
		b[1] = (byte) (this.highLayerCharIdentification & 0x7F);
		if (this.eHighLayerCharIdentificationPresent || this.eVidedoTelephonyCharIdentificationPresent) {

			if (this.eHighLayerCharIdentificationPresent) {
				b[2] = (byte) (0x80 | (this.eHighLayerCharIdentification & 0x7F));
			} else {
				b[2] = (byte) (0x80 | (this.eVidedoTelephonyCharIdentification & 0x7F));
			}
			return b;
		} else {
			b[1] |= 0x80;
			return b;
		}
	}

	public int getCodingStandard() {
		return codingStandard;
	}

	public void setCodingStandard(int codingStandard) {
		this.codingStandard = codingStandard & 0x03;
	}

	public int getInterpretation() {
		return interpretation;
	}

	public void setInterpretation(int interpretation) {
		this.interpretation = interpretation & 0x07;
	}

	public int getPresentationMethod() {
		return presentationMethod;
	}

	public void setPresentationMethod(int presentationMethod) {
		this.presentationMethod = presentationMethod & 0x03;
	}

	public int getHighLayerCharIdentification() {
		return highLayerCharIdentification;
	}

	public void setHighLayerCharIdentification(int highLayerCharIdentification) {
		this.highLayerCharIdentification = highLayerCharIdentification & 0x7F;
	}

	public int getEHighLayerCharIdentification() {
		return eHighLayerCharIdentification;
	}

	public void setEHighLayerCharIdentification(int highLayerCharIdentification) {

		if (this.eVidedoTelephonyCharIdentificationPresent) {
			throw new IllegalStateException("Either Extended VideoTlephony or Extended HighLayer octet is set. ExtendedVideoTlephony is already present");
		}
		if (this.highLayerCharIdentification == _HLCI_MAINTAINENCE || this.highLayerCharIdentification == _HLCI_MANAGEMENT) {
			this.eHighLayerCharIdentificationPresent = true;
			this.eHighLayerCharIdentification = highLayerCharIdentification & 0x7F;
		} else {
			throw new IllegalArgumentException("Can not set this octet - HLCI is of value: " + this.highLayerCharIdentification);
		}
	}

	public int getEVidedoTelephonyCharIdentification() {
		return eVidedoTelephonyCharIdentification;
	}

	public void setEVidedoTelephonyCharIdentification(int eVidedoTelephonyCharIdentification) {
		if (this.eHighLayerCharIdentificationPresent) {
			throw new IllegalStateException("Either Extended VideoTlephony or Extended HighLayer octet is set. ExtendedHighLayer is already present");
		}
		if ((this.highLayerCharIdentification >= _HLCI_AUDIO_VID_LOW_RANGE && this.highLayerCharIdentification <= _HLCI_AUDIO_VID_HIGH_RANGE)
				|| (this.highLayerCharIdentification >= _HLCI_AUDIO_VID_LOW_RANGE2 && this.highLayerCharIdentification <= _HLCI_AUDIO_VID_HIGH_RANGE2)) {
			this.eVidedoTelephonyCharIdentificationPresent = true;
			this.eVidedoTelephonyCharIdentification = eVidedoTelephonyCharIdentification & 0x7F;
		} else {
			throw new IllegalArgumentException("Can not set this octet - HLCI is of value: " + this.highLayerCharIdentification);
		}
	}

	public boolean isEHighLayerCharIdentificationPresent() {
		return eHighLayerCharIdentificationPresent;
	}

	public boolean isEVidedoTelephonyCharIdentificationPresent() {
		return eVidedoTelephonyCharIdentificationPresent;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
