/**
 * Start time:11:25:13 2009-03-31<br>
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
 * Start time:11:25:13 2009-03-31<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class EventInformation extends AbstractParameter {

	public static final int _PARAMETER_CODE = 0x24;
	private final static int _TURN_ON = 1;
	private final static int _TURN_OFF = 0;

	/**
	 * See Q.763 3.21 Event indicator : ALERTING
	 */
	public static final int _EVENT_INDICATOR_ALERTING = 1;

	/**
	 * See Q.763 3.21 Event indicator : PROGRESS
	 */
	public static final int _EVENT_INDICATOR_PROGRESS = 2;

	/**
	 * See Q.763 3.21 Event indicator : in-band information or an appropriate
	 * pattern is now available
	 */
	public static final int _EVENT_INDICATOR_IIIOPA = 3;

	/**
	 * See Q.763 3.21 Event indicator : call forwarded on busy (national use)
	 */
	public static final int _EVENT_INDICATOR_CFOB = 4;

	/**
	 * See Q.763 3.21 Event indicator : call forwarded on no reply (national
	 * use)
	 */
	public static final int _EVENT_INDICATOR_CFONNR = 5;

	/**
	 * See Q.763 3.21 Event indicator : call forwarded unconditional (national
	 * use)
	 */
	public static final int _EVENT_INDICATOR_CFOU = 6;

	/**
	 * See Q.763 3.21 Event presentation restricted indicator (national use) :
	 * no indication
	 */
	public static final boolean _EVENT_PRESENTATION_INI = false;

	/**
	 * See Q.763 3.21 Event presentation restricted indicator (national use) :
	 * presentation restricted
	 */
	public static final boolean _EVENT_PRESENTATION_IPR = true;

	private int eventIndicator ;
	private boolean eventPresentationRestrictedIndicator;

	public EventInformation(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	public EventInformation(int eventIndicator) {
		super();
		this.eventIndicator = eventIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length != 1) {
			throw new ParameterRangeInvalidException("byte[] must not be null or have different size than 1");
		}
		
		this.eventIndicator = b[0] & 0x7F;
		this.eventPresentationRestrictedIndicator = ((b[0] >> 7) & 0x01) == _TURN_ON;
		
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		byte[] b = new byte[] { (byte) (this.eventIndicator & 0x7F) };

		b[0] |= (byte) ((this.eventPresentationRestrictedIndicator ? _TURN_ON : _TURN_OFF) << 7);
		return b;
	}

	public int getEventIndicator() {
		return eventIndicator;
	}

	public void setEventIndicator(int eventIndicator) {
		this.eventIndicator = eventIndicator;
	}

	public boolean isEventPresentationRestrictedIndicator() {
		return eventPresentationRestrictedIndicator;
	}

	public void setEventPresentationRestrictedIndicator(boolean eventPresentationRestrictedIndicator) {
		this.eventPresentationRestrictedIndicator = eventPresentationRestrictedIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}
