/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.dtmf;

import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.BaseEndpoint;

/**
 *
 * @author kulikov
 */
public class Rfc2833Generator extends AbstractSource implements Runnable {

    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");
    private final static Format[] FORMATS = new Format[] {DTMF};
    private BufferFactory bufferFactory = new BufferFactory(10);
    
    private String digit;
    private boolean endOfEvent = false;
    private int volume = 10;
    private int duration;
    private int seq;
    
    public Rfc2833Generator(BaseEndpoint endpoint) {
        super("DTMF[" + endpoint.getLocalName() + "]");
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
        this.digit = digit;
    }
    
    public void setDuraion(int duration) {
        this.duration = duration;
    }
    
    public void start() {
        seq = 0;
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void stop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Format[] getFormats() {
        return FORMATS;
    }

    public void run() {
        Buffer buffer = bufferFactory.allocate();
        byte[] data = (byte[]) buffer.getData();
        data[0] = encode(digit);
        data[1] = endOfEvent ? (byte)(volume | 0x80) : (byte)(volume & 0x7f);
        data[2] = (byte)(duration >> 8);
        data[3] = (byte)(duration);
        buffer.setOffset(0);
        buffer.setLength(4);
        buffer.setFormat(DTMF);
        buffer.setSequenceNumber(seq++);
        buffer.setTimeStamp(System.currentTimeMillis());
    }

}
