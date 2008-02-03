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

package org.mobicents.media.server.impl.dtmf;

import org.apache.log4j.Logger;

/**
 * Implements digit buffer.
 * 
 * @author Oleg Kulikov
 */
public class DigitBuffer {
    public final static int TIMEOUT = 5000;
    public final static int SILENCE = 500;
    
    private StringBuffer buffer = new StringBuffer();
    private String mask = "[0-9, a,b,c,d,*,#]";
    
    private BaseDtmfDetector detector;
    
    private long lastActivity = System.currentTimeMillis();
    private String lastSymbol;
    
    private Logger logger = Logger.getLogger(DigitBuffer.class);
    
    public DigitBuffer(BaseDtmfDetector detector) {
        this.detector = detector;
    }
    
    public String getMask() {
        return mask;
    }
    
    public void setMask(String mask) {
        this.buffer = new StringBuffer();
        this.mask = mask;
    }
    
    public void push(String symbol) {
        long now = System.currentTimeMillis();
        
        if (now - lastActivity > TIMEOUT) {
            buffer = new StringBuffer();
        }
        
        if (!symbol.equals(lastSymbol) || (now - lastActivity > SILENCE) ) {
            buffer.append(symbol);
            lastActivity = now;
            lastSymbol = symbol;
            String digits = buffer.toString();
            if (digits.matches(mask)) {
                //send event;
                if (logger.isDebugEnabled()) {
                    logger.debug("Send DTMF event: " + digits);
                }
                detector.sendEvent(digits);
                buffer = new StringBuffer();
            }
        }
        
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println("DIGIT BUFFER");
        DigitBuffer digitBuffer = new DigitBuffer(null);
        //digitBuffer.setMask("[\\d]{2}[*]");
        digitBuffer.push("1");
        digitBuffer.push("1");
        
        Thread.currentThread().sleep(500);
        
        digitBuffer.push("2");
        digitBuffer.push("2");
        digitBuffer.push("2");
        
        Thread.currentThread().sleep(500);
        
//        digitBuffer.push("*");
//        digitBuffer.push("*");
//        digitBuffer.push("*");
        
        Thread.currentThread().sleep(6000);
        
        digitBuffer.push("6");
        digitBuffer.push("6");
        
        Thread.currentThread().sleep(500);
        
        digitBuffer.push("7");
        digitBuffer.push("7");
        digitBuffer.push("7");
        
        Thread.currentThread().sleep(500);
        
        digitBuffer.push("*");
        digitBuffer.push("*");
        digitBuffer.push("*");
        
    }
}
