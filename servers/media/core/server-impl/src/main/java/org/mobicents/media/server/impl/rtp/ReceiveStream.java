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
package org.mobicents.media.server.impl.rtp;

import org.mobicents.media.Buffer;
import org.apache.log4j.Logger;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.Timer;

/**
 *
 * @author Oleg Kulikov
 */
public class ReceiveStream extends AbstractSource implements Runnable {

    private int period;
    private JitterBuffer jitterBuffer;
    private Timer timer = new Timer();
    private Buffer frame;
    protected Format[] formats;
    
    private Logger logger = Logger.getLogger(ReceiveStream.class);

    /** Creates a new instance of ReceiveStream */
    public ReceiveStream(RtpSocket rtpSocket, int period, int jitter) {
    	super("ReceiveStream");
        this.period = period;

        jitterBuffer = new JitterBuffer(rtpSocket, jitter, period);

        timer.setListener(this);
        timer.start();
    }
    
    protected void push(RtpPacket rtpPacket) {
        jitterBuffer.write(rtpPacket);
    }

    public void stop() {
        timer.stop();
    }
    
    public void run() {
        frame = jitterBuffer.read();
        if (frame == null) {
            return;
        }
        if (sink == null) {
            return;
        }
        
        
        sink.receive(frame);
    }

    public void start() {
    }

    public Format[] getFormats() {
        return formats;
    }
}
