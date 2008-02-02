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

import org.mobicents.media.server.spi.dtmf.DtmfDetector;

/**
 * Implements digit buffer.
 * 
 * @author Oleg Kulikov
 */
public class DigitBuffer {
    public final static int TIMEOUT = 5000;
    public final static int SILENCE = 100;
    
    private StringBuffer buffer = new StringBuffer();
    private String mask;
    private DtmfDetector detector;
    
    private long lastActivity;
    private long lastSymbol;
    
    public DigitBuffer(DtmfDetector detector, String mask) {
        this.detector = detector;
        this.mask = mask;
    }
    
    public void push(String symbol) {
        if (!symbol.equals(lastSymbol)) {
            buffer.append(symbol);
            return;
        }
        
        long now = System.currentTimeMillis();
        
        if (now - lastActivity < SILENCE) {
            buffer.append(symbol);
            lastActivity = now;
        }
    }
}
