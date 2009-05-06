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
package org.mobicents.media.server.impl.rtp;

import java.io.IOException;
import java.util.Collection;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;

/**
 *
 * @author kulikov
 */
public class SendStream extends AbstractSink {

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
    private RtpSocket rtpSocket;
    private RtpHeader header = new RtpHeader();

    private int payloadType;
    private Format format;
    
    public SendStream(RtpSocket rtpSocket) {
        super("SendStream");
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
            packetizer.process(buffer, rtpSocket.timer.getHeartBeat());

            AudioFormat fmt = (AudioFormat) buffer.getFormat();
            pt = getPayloadType(fmt);

            header.init(buffer.getMarker(), (byte) pt, seq++, (int) buffer.getTimeStamp(), ssrc);
            buffer.setHeader(header);
        }
        
        boolean error=false;
        
        try {
            rtpSocket.send(buffer);
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
