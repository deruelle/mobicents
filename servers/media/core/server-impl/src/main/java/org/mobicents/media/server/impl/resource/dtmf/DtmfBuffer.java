/*
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

import java.io.Serializable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.spi.resource.DtmfDetector;

/**
 * Implements digit buffer.
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public abstract class DtmfBuffer extends AbstractSink implements DtmfDetector, Serializable {

    public final static int TIMEOUT = 5000;
    private static final String DETECTOR_MASK = "[0-9, A,B,C,D,*,#]";    // Silence is time difference forced between two digits. Default is user 2
    // digits per sec. Reduce this to suit your requirements
    public int interdigitInterval = DEFAULT_INTERDIGIT_INTERVAL;
    private StringBuffer buffer = new StringBuffer();
    
    private Matcher matcher;
    private String mask;
    private Pattern maskPattern;
    
    private long lastActivity = System.currentTimeMillis();
    private String lastSymbol;
    private transient Logger logger = Logger.getLogger(DtmfBuffer.class);

    public DtmfBuffer(String name) {
        super(name);
        buffer = new StringBuffer();
        
        maskPattern = Pattern.compile(DETECTOR_MASK);
        setMask(DETECTOR_MASK);
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setInterdigitInterval(int silence) {
        this.interdigitInterval = silence;
    }

    public int getInterdigitInterval() {
        return this.interdigitInterval;
    }

    public void push(String symbol) {
        long now = System.currentTimeMillis();

        if (now - lastActivity > TIMEOUT) {
            buffer = new StringBuffer();
        }
        
        if (!symbol.equals(lastSymbol) || (now - lastActivity > interdigitInterval)) {
            buffer.append(symbol);
            lastActivity = now;
            lastSymbol = symbol;
            String digits = buffer.toString();
            
            matcher = maskPattern.matcher(digits);
            if (matcher.matches()) {
                // send event;
                if (logger.isDebugEnabled()) {
                    logger.debug("Send DTMF event: " + digits);
                }

                int eventId = 0;
                if (digits.equals("0")) {
                    eventId = DtmfEvent.DTMF_0;
                } else if (digits.equals("1")) {
                    eventId = DtmfEvent.DTMF_1;
                } else if (digits.equals("2")) {
                    eventId = DtmfEvent.DTMF_2;
                } else if (digits.equals("3")) {
                    eventId = DtmfEvent.DTMF_3;
                } else if (digits.equals("4")) {
                    eventId = DtmfEvent.DTMF_4;
                } else if (digits.equals("5")) {
                    eventId = DtmfEvent.DTMF_5;
                } else if (digits.equals("6")) {
                    eventId = DtmfEvent.DTMF_6;
                } else if (digits.equals("7")) {
                    eventId = DtmfEvent.DTMF_7;
                } else if (digits.equals("8")) {
                    eventId = DtmfEvent.DTMF_8;
                } else if (digits.equals("9")) {
                    eventId = DtmfEvent.DTMF_9;
                } else if (digits.equals("A")) {
                    eventId = DtmfEvent.DTMF_A;
                } else if (digits.equals("B")) {
                    eventId = DtmfEvent.DTMF_B;
                } else if (digits.equals("C")) {
                    eventId = DtmfEvent.DTMF_C;
                } else if (digits.equals("D")) {
                    eventId = DtmfEvent.DTMF_D;
                } else if (symbol.equals("*")) {
                    eventId = DtmfEvent.DTMF_STAR;
                } else if (symbol.equals("#")) {
                    eventId = DtmfEvent.DTMF_HASH;
                } else {
                    logger.error("DTMF event " + symbol + " not identified");
                    return;
                }

                System.out.println("Send DTMF " + eventId);
                DtmfEvent dtmfEvent = new DtmfEvent(this, eventId);
                super.sendEvent(dtmfEvent);

                buffer = new StringBuffer();
            }
        }

    }
}
