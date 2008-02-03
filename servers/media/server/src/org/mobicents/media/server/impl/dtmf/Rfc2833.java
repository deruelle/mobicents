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

package org.mobicents.media.server.impl.dtmf;

import java.io.IOException;
import javax.media.Buffer;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 */
public class Rfc2833 extends BaseDtmfDetector implements BufferTransferHandler {
    
    
    private boolean started = false;
    private final static String[] DTMF = new String[] {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#"
    };
    
    private Logger logger = Logger.getLogger(Rfc2833.class);
    
    public Rfc2833() {
        super();
    }
    
    public void start(PushBufferStream stream) throws UnsupportedFormatException {
        this.started = true;
        stream.setTransferHandler(this);       
        logger.debug("Detector started");
    }
    
    public void stop() {
        this.started = false;
        logger.debug("Detector stopped");
    }
    
    public void transferData(PushBufferStream stream) {
        logger.info("transfering...");
        if (!started) {
            return;
        }
        
        Buffer buffer = new Buffer();
        try {
            stream.read(buffer);
        } catch (IOException e) {
        }
        
        byte[] data = (byte[]) buffer.getData();
        
        String digit = DTMF[data[0]];
        boolean end = (data[1] & 0x7f) != 0;
        
        logger.debug("Arrive packet, digit=" + digit + ", end=" + end);        
        digitBuffer.push(digit);
    }
    
}
