/**
 * Start time:11:25:13 2009-03-31<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

/**
 * Start time:11:25:13 2009-03-31<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class EventInformation extends AbstractParameter {

	/**
	 * See Q.763 3.21
	 */
	public static final int _EVENT_INDICATOR_ALERTING = 1;

	/**
	 * See Q.763 3.21
	 */
	public static final int _EVENT_INDICATOR_PROGRESS = 2;

	/**
	 * See Q.763 3.21
	 */
	public static final int _EVENT_INDICATOR_INBAND_INFORMATION_OR_PATTERN_AVAILABLE = 3;

	/**
	 * See Q.763 3.21
	 */
	public static final int _EVENT_INDICATOR_CALL_FORWARDED_ON_BUSY = 4;

	/**
	 * See Q.763 3.21
	 */
	public static final int _EVENT_INDICATOR_CALL_FORWARDED_ON_NOREPLY = 5;

	/**
	 * See Q.763 3.21
	 */
	public static final int _EVENT_INDICATOR_CALL_FORWARDED_ON_UNCODITIONAL = 6;

	/**
	 * See Q.763 3.21
	 */
	public static final int _EVENT_PRESENTATION_INDICATOR_NO_INDICATION = 0;

	/**
	 * See Q.763 3.21
	 */
	public static final int _EVENT_PRESENTATION_INDICATOR_PRESENTATION_RESTRICTED = 1;

	private int eventIndicator = 0;
	private int eventPresentationRestrictedIndicator = 0;

	public EventInformation(byte[] b) {
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
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 1) {
			throw new IllegalArgumentException("byte[] must not be null or have different size than 1");
		}

		this.eventIndicator = b[0] & 0x7F;
		this.eventIndicator = (b[0] >> 7) & 0x01;
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		byte[] b = new byte[] { (byte) (this.eventIndicator & 0x7F) };

		b[0] |= (byte) ((this.eventPresentationRestrictedIndicator & 0x01) << 7);
		return b;
	}

	public int getEventIndicator() {
		return eventIndicator;
	}

	public void setEventIndicator(int eventIndicator) {
		this.eventIndicator = eventIndicator;
	}

}
