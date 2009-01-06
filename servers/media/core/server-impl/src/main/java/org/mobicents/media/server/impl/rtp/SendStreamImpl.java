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
import java.util.Collection;
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
    private RtpHeader header = new RtpHeader();

    private int payloadType;
    private Format format;
    
    public SendStreamImpl(RtpSocketImpl rtpSocket) {
        super("SendStreamImpl");
        this.rtpSocket = rtpSocket;
        packetizer = new RtpPacketizer();
    }

    private int getPayloadType(Format fmt) {
        if (format != null && fmt.equals(format)) {
            return payloadType;
        }
        
        payloadType = rtpSocket.getPayloadType(fmt);
        format = fmt;
        
        return payloadType;
    }
    
    public void receive(Buffer buffer) {
        if (buffer.getFlags() != Buffer.FLAG_SYSTEM_TIME) {
            packetizer.process(buffer, rtpSocket.period);

            AudioFormat fmt = (AudioFormat) buffer.getFormat();
            pt = getPayloadType(fmt);

            header.init((byte) pt, seq++, (int) buffer.getTimeStamp(), ssrc);
            buffer.setHeader(header);
        }
        boolean error=false;
        try {
            rtpSocket.peer.send(buffer);
        } catch (IOException e) {
        } finally {
            buffer.dispose();
        }
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.MediaSink.isAcceptable(Format).
     */
    public boolean isAcceptable(Format fmt) {
        boolean res = false;
        for (Format f : formats) {
            if (f.matches(fmt)) {
                res = true;
                break;
            }
        }
        return res;
    }

    public Format[] getFormats() {
        return formats;
    }

    public void setFormats(Collection<Format> fmts) {
        formats = new Format[fmts.size()];
        fmts.toArray(formats);
    }
}
