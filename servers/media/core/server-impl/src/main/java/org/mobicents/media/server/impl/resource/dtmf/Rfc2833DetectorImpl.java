/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.resource.dtmf;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.events.dtmf.DtmfEvent;
import org.mobicents.media.server.impl.rtp.RtpHeader;
import org.mobicents.media.server.spi.resource.DtmfDetector;

/**
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public class Rfc2833DetectorImpl extends AbstractSink implements DtmfDetector {

	private transient Logger logger = Logger.getLogger(Rfc2833DetectorImpl.class);
	
	public final static Format[] FORMATS = new Format[] { DTMF };

	private String mask = DETECTOR_MASK;
	private int duration = DETECTOR_DURATION;
	private int silence = DETECTOR_SILENCE;

	public Rfc2833DetectorImpl(String name) {
		super(name);
	}

	public void start() {
	}

	public void stop() {
	}

	public int getDuration() {
		return this.duration;
	}

	public String getMask() {
		return this.mask;
	}

	public int getSilenec() {
		return this.silence;
	}

	public void setDuration(int duartion) {
		this.duration = duration;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public void setSilenec(int silence) {
		this.silence = silence;
	}

	public void receive(Buffer buffer) {
		try {
			RtpHeader rtpHeader = (RtpHeader) buffer.getHeader();
			if (rtpHeader.getMarker()) {
				byte[] data = (byte[]) buffer.getData();

				String digit = TONE[data[0]];
				boolean end = (data[1] & 0x80) != 0;
				if (!end) {
					this.push(digit);
				}
			}
		} finally {
			buffer.dispose();
		}
	}

	public void push(String symbol) {

		if (symbol.matches(mask)) {
			// send event;
			if (logger.isDebugEnabled()) {
				logger.debug("Send DTMF event: " + symbol);
			}
			int eventId = 0;
			if (symbol.equals("0")) {
				eventId = DtmfEvent.DTMF_0;
			} else if (symbol.equals("1")) {
				eventId = DtmfEvent.DTMF_1;
			} else if (symbol.equals("2")) {
				eventId = DtmfEvent.DTMF_2;
			} else if (symbol.equals("3")) {
				eventId = DtmfEvent.DTMF_3;
			} else if (symbol.equals("4")) {
				eventId = DtmfEvent.DTMF_4;
			} else if (symbol.equals("5")) {
				eventId = DtmfEvent.DTMF_5;
			} else if (symbol.equals("6")) {
				eventId = DtmfEvent.DTMF_6;
			} else if (symbol.equals("7")) {
				eventId = DtmfEvent.DTMF_7;
			} else if (symbol.equals("8")) {
				eventId = DtmfEvent.DTMF_8;
			} else if (symbol.equals("9")) {
				eventId = DtmfEvent.DTMF_9;
			} else if (symbol.equals("A")) {
				eventId = DtmfEvent.DTMF_A;
			} else if (symbol.equals("B")) {
				eventId = DtmfEvent.DTMF_B;
			} else if (symbol.equals("C")) {
				eventId = DtmfEvent.DTMF_C;
			} else if (symbol.equals("D")) {
				eventId = DtmfEvent.DTMF_D;
			} else if (symbol.equals("*")) {
				eventId = DtmfEvent.DTMF_STAR;
			} else if (symbol.equals("#")) {
				eventId = DtmfEvent.DTMF_HASH;
			} else {
				logger.error("DTMF event " + symbol + " not identified");
				return;
			}

			DtmfEvent dtmfEvent = new DtmfEvent(getEndpoint(), getConnection(), eventId);
			super.sendEvent(dtmfEvent);

		}
	}

	@Override
	public void disconnect(MediaSource source) {
		super.disconnect(source);
	}

	public Format[] getFormats() {
		return FORMATS;
	}

	public boolean isAcceptable(Format format) {
		return DTMF.equals(format);
	}

}
