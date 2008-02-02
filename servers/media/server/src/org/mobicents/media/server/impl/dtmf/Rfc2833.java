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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.media.Buffer;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.dtmf.DtmfDetector;
import org.mobicents.media.server.spi.events.Basic;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class Rfc2833 implements DtmfDetector, BufferTransferHandler {
    
    public final static Timer TIMER = new Timer();
    
    private String mask= "\\.";
    private StringBuffer digitBuffer = new StringBuffer();
    private String current = "";
    private boolean started = false;
    private TimerTask cleanTask;
    private boolean detecting = false;
    
    private List <NotificationListener> listeners = new ArrayList();
    private final static String[] DTMF = new String[]{
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#"
    };
    
    private Logger logger = Logger.getLogger(Rfc2833.class);
    
    public Rfc2833() {
    }
    
    public void setDtmfMask(String mask) {
        this.mask = mask;
    }

    public void start(PushBufferStream stream) throws UnsupportedFormatException {
        this.started = true;
        stream.setTransferHandler(this);       
        cleanTask = new CleanTask();
        TIMER.scheduleAtFixedRate(cleanTask, 5000, 5000);
        logger.debug("Detector started");
    }
    
    public void stop() {
        this.started = false;
        cleanTask.cancel();
        TIMER.purge();
        logger.debug("Detector stopped");
    }
    
    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }
    
    public void transferData(PushBufferStream stream) {
        logger.info("transfering...");
        if (!started) {
            return;
        }
        
        detecting = true;
        
        Buffer buffer = new Buffer();
        try {
            stream.read(buffer);
        } catch (IOException e) {
        }
        
        byte[] data = (byte[]) buffer.getData();
        
        String digit = DTMF[data[0]];
        boolean end = (data[1] & 0x7f) != 0;
        
        logger.debug("Arrive packet, digit=" + digit + ", end=" + end);
        
        if (!current.equals(digit)) {
            current = digit;
        }
        
        if (end) {
            digitBuffer.append(current);
            logger.debug("append " + current  + " to digit buffer");
            
            String digits = digitBuffer.toString();
            logger.debug("buffer: " + digits);
            
            if (digits.matches(mask)) {
                digitBuffer = new StringBuffer();
                sendEvent(digits);
                stop();
            }
        }
        
    }

    private void sendEvent(String digits) {
        NotifyEvent evt = new NotifyEvent(this, Basic.DTMF, getCause(digits), digits);
        logger.debug("sending event: didgits=" + digits);
        for (NotificationListener listener: listeners) {
            listener.update(evt);
        }
    }
    
    private int getCause(String seq) {
        if (seq.equals("0")) {
            return Basic.CAUSE_DIGIT_0;
        } else if (seq.equals("1")) {
            return Basic.CAUSE_DIGIT_1;
        } else if (seq.equals("2")) {
            return Basic.CAUSE_DIGIT_2;
        } else if (seq.equals("3")) {
            return Basic.CAUSE_DIGIT_3;
        } else if (seq.equals("4")) {
            return Basic.CAUSE_DIGIT_4;
        } else if (seq.equals("5")) {
            return Basic.CAUSE_DIGIT_5;
        } else if (seq.equals("6")) {
            return Basic.CAUSE_DIGIT_6;
        } else if (seq.equals("7")) {
            return Basic.CAUSE_DIGIT_7;
        } else if (seq.equals("8")) {
            return Basic.CAUSE_DIGIT_8;
        } else if (seq.equals("9")) {
            return Basic.CAUSE_DIGIT_9;
        } else if (seq.equals("10")) {
            return Basic.CAUSE_DIGIT_STAR;
        } else if (seq.equals("11")) {
            return Basic.CAUSE_DIGIT_NUM;
        } else {
            return Basic.CAUSE_SEQ;
        }
    }
    
    private class CleanTask extends TimerTask {
        public void run() {
            if (detecting) {
                detecting = false;
                return;
            }
            digitBuffer = new StringBuffer();
        }
    }
    
}
