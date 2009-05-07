/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.dtmf;

import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.rtp.RtpHeader;

/**
 * 
 * @author kulikov
 * @author amit bhayani
 */
public class Rfc2833Generator extends AbstractSource implements Runnable {

	private BufferFactory bufferFactory = null;

	private byte digit;
	private boolean endOfEvent = false;

	// Volume range from 0 to 63
	private int volume = 10;

	// Min duration = 40ms and max = 500ms
	private int duration = 40;
	private int seq = 0;

	private int mediaPackets = 0;

	private RtpHeader rtpHeader = new RtpHeader();

	public Rfc2833Generator(String name) {
		super(name);
		bufferFactory = new BufferFactory(10, name);
	}

	private byte encode(String digit) {
		if (digit.equals("1")) {
			return 1;
		} else if (digit.equals("2")) {
			return 2;
		} else if (digit.equals("3")) {
			return 3;
		} else if (digit.equals("4")) {
			return 4;
		} else if (digit.equals("5")) {
			return 5;
		} else if (digit.equals("6")) {
			return 6;
		} else if (digit.equals("7")) {
			return 7;
		} else if (digit.equals("8")) {
			return 8;
		} else if (digit.equals("9")) {
			return 9;
		} else if (digit.equals("0")) {
			return 0;
		} else if (digit.equals("*")) {
			return 10;
		} else if (digit.equals("#")) {
			return 11;
		} else if (digit.equals("A")) {
			return 12;
		} else if (digit.equals("B")) {
			return 13;
		} else if (digit.equals("C")) {
			return 14;
		} else if (digit.equals("D")) {
			return 15;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void setDigit(String digit) {
		this.digit = encode(digit);
	}

	public void setDuraion(int duration) {
		if (duration < 40) {
			throw new IllegalArgumentException("Duration cannot be less than 40ms");
		}
		this.duration = duration;
	}

	public void setVolume(int volume) {
		if (volume < 0 || volume > 63) {
			throw new IllegalArgumentException("Volume cannot be less than 0db or greater than 63db");
		}
		this.volume = volume;
	}

	public void start() {
		seq = 0;
		mediaPackets = duration / getEndpoint().getTimer().getHeartBeat();
		// Last two will be End of Event packets
		mediaPackets = mediaPackets + 2;
		this.run();

	}

	public void stop() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Format[] getFormats() {
		return Rfc2833Detector.FORMATS;
	}

	public void run() {
		long timeStamp = System.currentTimeMillis();
		boolean marker = true;
		int eventDuration = 160;
		endOfEvent = false;
		for (seq = 0; seq < mediaPackets; seq++) {
			if (seq == (mediaPackets - 3)) {
				endOfEvent = true;
			}
			Buffer buffer = bufferFactory.allocate();

			rtpHeader.setMarker(marker);
			buffer.setHeader(rtpHeader);
			marker = false;

			byte[] data = (byte[]) buffer.getData();
			data[0] = digit;
			data[1] = endOfEvent ? (byte) (volume | 0x80) : (byte) (volume & 0x7f);

			data[2] = (byte) (eventDuration >> 8);
			data[3] = (byte) (eventDuration);
			eventDuration = eventDuration + 160;

			buffer.setOffset(0);
			buffer.setLength(4);
			buffer.setFormat(Rfc2833Detector.DTMF);
			buffer.setSequenceNumber(seq);
			buffer.setTimeStamp(timeStamp);

			otherParty.receive(buffer);
		}
	}

}
