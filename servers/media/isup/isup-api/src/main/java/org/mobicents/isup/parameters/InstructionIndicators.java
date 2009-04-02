/**
 * Start time:12:42:55 2009-04-02<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Start time:12:42:55 2009-04-02<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class InstructionIndicators extends AbstractParameter {

	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	/**
	 * See Q.763 3.41
	 */
	public static final boolean _TRANSIT_INDICATOR_TRANSIT_INTEPRETATION = false;
	/**
	 * See Q.763 3.41
	 */
	public static final boolean _TRANSIT_INDICATOR_END_TO_END_INTEPRETATION = true;
	/**
	 * See Q.763 3.41
	 */
	public static final boolean _RELEASE_CALL_INDICATOR_DO_NOT_RELEASE = false;
	/**
	 * See Q.763 3.41
	 */
	public static final boolean _RELEASE_CALL_INDICATOR_RELEASE = true;

	/**
	 * See Q.763 3.41
	 */
	public static final boolean _DISCARD_MESSAGE_INDICATOR_DO_NOT_DISCARD = false;
	/**
	 * See Q.763 3.41
	 */
	public static final boolean _DISCARD_MESSAGE_INDICATOR_DISCARD = true;

	/**
	 * See Q.763 3.41
	 */
	public static final boolean _DISCARD_PARAMETER_INDICATOR_DO_NOT_DISCARD = false;
	/**
	 * See Q.763 3.41
	 */
	public static final boolean _DISCARD_PARAMETER_INDICATOR_DISCARD = true;

	/**
	 * See Q.763 3.41
	 */
	public static final int PASS_ON_NOT_POSSIBLE_INDICATOR_RELEASE_CALL = 0;

	/**
	 * See Q.763 3.41
	 */
	public static final int PASS_ON_NOT_POSSIBLE_INDICATOR_DISCARD_MESSAGE = 1;

	/**
	 * See Q.763 3.41
	 */
	public static final int PASS_ON_NOT_POSSIBLE_INDICATOR_DISCARD_PARAMETER = 2;

	/**
	 * See Q.763 3.41
	 */
	public static final int BAND_INTERWORKING_INDICATOR_PASS_ON = 0;

	/**
	 * See Q.763 3.41
	 */
	public static final int BAND_INTERWORKING_INDICATOR_DISCARD_MESSAGE = 1;

	/**
	 * See Q.763 3.41
	 */
	public static final int BAND_INTERWORKING_INDICATOR_RELEASE_CALL = 2;

	/**
	 * See Q.763 3.41
	 */
	public static final int BAND_INTERWORKING_INDICATOR_DISCARD_PARAMETER = 3;

	private boolean transitAtIntermediateExchangeIndicator = false;
	private boolean releaseCallindicator = false;
	private boolean sendNotificationIndicator = false;
	private boolean discardMessageIndicator = false;
	private boolean discardParameterIndicator = false;
	private byte passOnNotPossibleIndicator = 0;
	private byte bandInterworkingIndicator = 0;

	private boolean secondOctetPresenet = false;

	private byte[] raw = null;
	private boolean useAsRaw = false;

	public InstructionIndicators(byte[] b) {
		super();
		decodeElement(b);
	}

	/**
	 * This constructor shall be used in cases more octets are defined, User
	 * needs to manipulate and encode them properly.
	 * 
	 * @param b
	 * @param userAsRaw
	 */
	public InstructionIndicators(byte[] b, boolean userAsRaw) {
		super();
		this.raw = b;
		this.useAsRaw = userAsRaw;
	}

	public InstructionIndicators(boolean transitAtIntermediateExchangeIndicator, boolean releaseCallindicator, boolean sendNotificationIndicator, boolean discardMessageIndicator,
			boolean discardParameterIndicator, byte passOnNotPossibleIndicator, boolean secondOctetPresenet, byte[] raw, boolean useAsRaw) {
		super();
		this.transitAtIntermediateExchangeIndicator = transitAtIntermediateExchangeIndicator;
		this.releaseCallindicator = releaseCallindicator;
		this.sendNotificationIndicator = sendNotificationIndicator;
		this.discardMessageIndicator = discardMessageIndicator;
		this.discardParameterIndicator = discardParameterIndicator;
		this.passOnNotPossibleIndicator = passOnNotPossibleIndicator;
		this.secondOctetPresenet = secondOctetPresenet;
		this.raw = raw;
		this.useAsRaw = useAsRaw;
	}

	public InstructionIndicators(boolean transitAtIntermediateExchangeIndicator, boolean releaseCallindicator, boolean sendNotificationIndicator, boolean discardMessageIndicator,
			boolean discardParameterIndicator, byte passOnNotPossibleIndicator, byte bandInterworkingIndicator) {
		super();
		this.transitAtIntermediateExchangeIndicator = transitAtIntermediateExchangeIndicator;
		this.releaseCallindicator = releaseCallindicator;
		this.sendNotificationIndicator = sendNotificationIndicator;
		this.discardMessageIndicator = discardMessageIndicator;
		this.discardParameterIndicator = discardParameterIndicator;
		this.passOnNotPossibleIndicator = passOnNotPossibleIndicator;
		this.setBandInterworkingIndicator(bandInterworkingIndicator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length < 1) {
			throw new IllegalArgumentException("byte[] must  not be null and length must  be greater than  0");
		}

		// XXX: Cheat, we read only defined in Q763 2 octets, rest we ignore...
		int index = 0;
		int v = b[index];

		try {
			// watch extension byte
			while (((v >> 7) & 0x01) == 1) {
				v = b[index];
				if (index == 0) {
					this.transitAtIntermediateExchangeIndicator = (v & 0x01) == _TURN_ON;
					this.releaseCallindicator = ((v >> 1) & 0x01) == _TURN_ON;
					this.sendNotificationIndicator = ((v >> 2) & 0x01) == _TURN_ON;
					this.discardMessageIndicator = ((v >> 3) & 0x01) == _TURN_ON;
					this.discardParameterIndicator = ((v >> 4) & 0x01) == _TURN_ON;
					this.passOnNotPossibleIndicator = (byte) ((v >> 5) & 0x03);
				} else if (index == 1) {
					this.bandInterworkingIndicator = (byte) (v & 0x03);
				} else {
					if (logger.isLoggable(Level.FINEST)) {
						logger.finest("Skipping octets with index[" + index + "] in " + this.getClass().getName() + ". This should not be called for us .... Instead one should use raw");
					}
				}
				index++;
			}
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			throw new IllegalArgumentException("Failed to parse passed value due to wrong encoding.", aioobe);
		}
		return b.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		if (this.useAsRaw) {
			// FIXME: make sure we properly encode ext bit?
			return this.raw;
		}
		byte[] b = null;
		if (this.secondOctetPresenet) {
			b = new byte[2];
			b[1] = (byte) (0x80 | (this.bandInterworkingIndicator & 0x03));
		} else {
			b = new byte[1];
		}

		b[0] = 0;

		b[0] |= (this.transitAtIntermediateExchangeIndicator ? _TURN_ON : _TURN_OFF);
		b[0] |= (this.releaseCallindicator ? _TURN_ON : _TURN_OFF) << 1;
		b[0] |= (this.sendNotificationIndicator ? _TURN_ON : _TURN_OFF) << 2;
		b[0] |= (this.discardMessageIndicator ? _TURN_ON : _TURN_OFF) << 3;
		b[0] |= (this.discardParameterIndicator ? _TURN_ON : _TURN_OFF) << 4;
		b[0] |= (this.passOnNotPossibleIndicator & 0x03) << 5;

		return b;
	}

	public boolean isTransitAtIntermediateExchangeIndicator() {
		return transitAtIntermediateExchangeIndicator;
	}

	public void setTransitAtIntermediateExchangeIndicator(boolean transitAtIntermediateExchangeIndicator) {
		this.transitAtIntermediateExchangeIndicator = transitAtIntermediateExchangeIndicator;
	}

	public boolean isReleaseCallindicator() {
		return releaseCallindicator;
	}

	public void setReleaseCallindicator(boolean releaseCallindicator) {
		this.releaseCallindicator = releaseCallindicator;
	}

	public boolean isSendNotificationIndicator() {
		return sendNotificationIndicator;
	}

	public void setSendNotificationIndicator(boolean sendNotificationIndicator) {
		this.sendNotificationIndicator = sendNotificationIndicator;
	}

	public boolean isDiscardMessageIndicator() {
		return discardMessageIndicator;
	}

	public void setDiscardMessageIndicator(boolean discardMessageIndicator) {
		this.discardMessageIndicator = discardMessageIndicator;
	}

	public boolean isDiscardParameterIndicator() {
		return discardParameterIndicator;
	}

	public void setDiscardParameterIndicator(boolean discardParameterIndicator) {
		this.discardParameterIndicator = discardParameterIndicator;
	}

	public byte getPassOnNotPossibleIndicator() {
		return passOnNotPossibleIndicator;
	}

	public void setPassOnNotPossibleIndicator(byte passOnNotPossibleIndicator) {
		this.passOnNotPossibleIndicator = passOnNotPossibleIndicator;
	}

	public byte getBandInterworkingIndicator() {
		return bandInterworkingIndicator;
	}

	public void setBandInterworkingIndicator(byte bandInterworkingIndicator) {
		this.bandInterworkingIndicator = bandInterworkingIndicator;
		this.secondOctetPresenet = true;
	}

}
