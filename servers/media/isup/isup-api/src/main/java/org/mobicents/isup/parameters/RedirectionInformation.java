/**
 * Start time:15:18:18 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:15:18:18 2009-04-02<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class RedirectionInformation extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0;
	/**
	 * See Q.763 3.45 Redirecting indicator no redirection (national use)
	 */
	public static final int _RI_NO_REDIRECTION = 0;

	/**
	 * See Q.763 3.45 Redirecting indicator no redirection (national use)
	 */
	public static final int _RI_CALL_REROUTED = 1;

	/**
	 * See Q.763 3.45 Redirecting indicator call rerouted, all redirection
	 * information presentation restricted (national use)
	 */
	public static final int _RI_CALL_R_RID = 2;

	/**
	 * See Q.763 3.45 Redirecting indicator call diverted
	 */
	public static final int _RI_CALL_D = 3;

	/**
	 * See Q.763 3.45 Redirecting indicator call diverted, all redirection
	 * information presentation restricted
	 */
	public static final int _RI_CALL_D_RIR = 4;

	/**
	 * See Q.763 3.45 Redirecting indicator call rerouted, redirection number
	 * presentation restricted (national use)
	 */
	public static final int _RI_CALL_R_RNPR = 5;

	/**
	 * See Q.763 3.45 Redirecting indicator call diversion, redirection number
	 * presentation restricted (national use)
	 */
	public static final int _RI_CALL_D_RNPR = 6;

	/**
	 * See Q.763 3.45 Original redirection reason unknown/not available
	 */
	public static final int _ORR_UNA = 0;
	/**
	 * See Q.763 3.45 Original redirection reason user busy
	 */
	public static final int _ORR_USER_BUSY = 1;

	/**
	 * See Q.763 3.45 Original redirection reason no reply
	 */
	public static final int _ORR_NO_REPLY = 2;
	/**
	 * See Q.763 3.45 Original redirection reason unconditional
	 */
	public static final int _ORR_UNCONDITIONAL = 3;

	/**
	 * See Q.763 3.45 Redirecting reason unknown/not available
	 */
	public static final int _RR_UNA = 0;
	/**
	 * See Q.763 3.45 Redirecting reason user busy
	 */
	public static final int _RR_USER_BUSY = 1;

	/**
	 * See Q.763 3.45 Redirecting reason no reply
	 */
	public static final int _RR_NO_REPLY = 2;
	/**
	 * See Q.763 3.45 Redirecting reason unconditional
	 */
	public static final int _RR_UNCONDITIONAL = 3;

	/**
	 * See Q.763 3.45 Redirecting reason deflection during alerting
	 */
	public static final int _RR_DEFLECTION_DA = 4;

	/**
	 * See Q.763 3.45 Redirecting reason deflection immediate response
	 */
	public static final int _RR_DEFLECTION_IE = 5;

	/**
	 * See Q.763 3.45 Redirecting reason mobile subscriber not reachable
	 */
	public static final int _RR_MOBILE_SNR = 6;
	private int redirectingIndicator;
	private int originalRedirectionReason;
	private int redirectionCounter;
	private int redirectionReason;

	public RedirectionInformation(byte[] b) throws IllegalArgumentException {
		super();
		decodeElement(b);
	}

	public RedirectionInformation(int redirectingIndicator, int originalRedirectionReason, int redirectionCounter, int redirectionReason) throws IllegalArgumentException {
		super();
		this.setRedirectingIndicator(redirectingIndicator);
		this.setOriginalRedirectionReason(originalRedirectionReason);
		this.setRedirectionCounter(redirectionCounter);
		this.setRedirectionReason(redirectionReason);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 2) {
			throw new IllegalArgumentException("byte[] must  not be null and length must  be 2");
		}

		this.setRedirectingIndicator((b[0] & 0x07));
		this.setOriginalRedirectionReason(((b[0] >> 4) & 0x0F));
		this.setRedirectionCounter((b[1] & 0x07));
		this.setRedirectionReason(((b[1] >> 4) & 0x0F));
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		int b0 = redirectingIndicator & 0x07;
		b0 |= (this.originalRedirectionReason & 0x0F) << 4;

		int b1 = redirectionCounter & 0x07;
		b1 |= (this.redirectionReason & 0x0F) << 4;
		return new byte[] { (byte) b0, (byte) b1 };
	}

	public int getRedirectingIndicator() {
		return redirectingIndicator;
	}

	public void setRedirectingIndicator(int redirectingIndicator) {
		this.redirectingIndicator = redirectingIndicator & 0x07;
	}

	public int getOriginalRedirectionReason() {
		return originalRedirectionReason;
	}

	public void setOriginalRedirectionReason(int originalRedirectionReason) {
		this.originalRedirectionReason = originalRedirectionReason & 0x0F;
	}

	public int getRedirectionCounter() {
		return redirectionCounter;
	}

	public void setRedirectionCounter(int redirectionCounter) throws IllegalArgumentException {
		if (redirectionCounter < 1 || redirectionCounter > 5) {
			throw new IllegalArgumentException("Out of range - must be between 1 and 5");
		} 
		this.redirectionCounter = redirectionCounter & 0x07;
	}

	public int getRedirectionReason() {
		return redirectionReason;
	}

	public void setRedirectionReason(int redirectionReason) {
		this.redirectionReason = redirectionReason & 0x0F;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
