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

import java.io.IOException;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;

/**
 *
 * @author Oleg Kulikov
 */
public class SendStreamImpl extends AbstractSink implements SendStream {

    //payload type
    private int pt = 0;
    //sequence number
    private int seq = 0;
    //source synchronization
    private final long ssrc = System.currentTimeMillis();
    //packetizer
    //private Packetizer packetizer;
    private RtpPacketizer packetizer;
    //the amount of ticks in one milliseconds
    int ticks;
    
    protected Format[] formats;
    
    private RtpSocketImpl rtpSocket;
    private Logger logger = Logger.getLogger(SendStreamImpl.class);
    
    public SendStreamImpl(RtpSocketImpl rtpSocket) {
        this.rtpSocket = rtpSocket;
        packetizer = new RtpPacketizer();
    }

    
    public void receive(Buffer buffer) {
        packetizer.process(buffer, rtpSocket.period);
        
        AudioFormat fmt = (AudioFormat) buffer.getFormat();
        pt = rtpSocket.getPayloadType(fmt);
        
        byte[] data = (byte[]) buffer.getData();        
        RtpPacket p = new RtpPacket((byte) pt, (int) seq++, (int) buffer.getTimeStamp(),
                ssrc, data,buffer.getOffset(), buffer.getLength());
        try {
            rtpSocket.peer.send(p);
            if (logger.isDebugEnabled()) {
                logger.debug("--> send " + data.length + " bytes packet, fmt=" + buffer.getFormat());
            }
        } catch (IOException e) {
            logger.error("I/O Error", e);
        }
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.MediaSink.isAcceptable(Format).
     */
    public boolean isAcceptable(Format fmt) {
        boolean res = false;
        for (Format format : formats) {
            if (format.matches(fmt)) {
                res = true;
                break;
            }
        }
        return res;
    }

    public Format[] getFormats() {
        return formats;
    }
}
