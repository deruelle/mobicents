/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.impl.resource.dtmf;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.rtp.RtpHeader;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.resource.DtmfGenerator;

/**
 * 
 * @author kulikov
 * @author amit bhayani
 */
public class Rfc2833GeneratorImpl extends AbstractSource implements DtmfGenerator {

    private byte digit;
    private String sDigit;
    private boolean endOfEvent = false;    // Volume range from 0 to 63
    private int volume = 0;    // Min duration = 40ms and max = 500ms
    private int duration = 50;
    private int mediaPackets = 0;
    private RtpHeader rtpHeader = new RtpHeader();
    private int heartBeat = 20;
    private int eventDuration = 160;
    
    public Rfc2833GeneratorImpl(String name, Timer timer) {
        super(name);
        setSyncSource(timer);
        heartBeat = timer.getHeartBeat();
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
            throw new IllegalArgumentException("The Digit " + digit + " is not identified");
        }
    }

    public void setDigit(String digit) {
        this.sDigit = digit;
    }

    public String getDigit() {
        return this.sDigit;
    }

    public void setDuration(int duration) {
        if (duration < 40) {
            throw new IllegalArgumentException("Duration cannot be less than 40ms");
        }
        this.duration = duration;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 36) {
            throw new IllegalArgumentException("Volume cannot be less than 0db or greater than 63db");
        }
        this.volume = (int)volume;
    }

    public int getVolume() {
        return this.volume;
    }

    @Override
    public void start() {
        if (this.sDigit != null) {
            this.digit = encode(this.sDigit);
            mediaPackets = duration / heartBeat + 2;
            super.start();
        }
    }

    @Override
    public void stop() {
    }

    public Format[] getFormats() {
        return Rfc2833DetectorImpl.FORMATS;
    }

    public void evolve(Buffer buffer, long seq) {
        boolean marker = true;
        endOfEvent = false;
        if (seq == (mediaPackets - 3)) {
            endOfEvent = true;
        }

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
        buffer.setFormat(AVProfile.DTMF);
        buffer.setSequenceNumber(seq);
        buffer.setTimeStamp(getSyncSource().getTimestamp());

        if (seq == mediaPackets) {
            buffer.setEOM(true);
        }
    }
}
