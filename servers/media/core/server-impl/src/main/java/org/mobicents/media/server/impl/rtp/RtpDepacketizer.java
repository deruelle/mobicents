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

import java.util.HashMap;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;

/**
 *
 * @author kulikov
 */
public class RtpDepacketizer {
    
    private int period;
    private HashMap<Integer, Format> rtpMap;
    
    private JitterBuffer jitterBuffer;
    private long seq;
    
    public RtpDepacketizer(int jitter, int period, HashMap<Integer, Format> rtpMap) {
        this.period = period;
        this.rtpMap = rtpMap;
        jitterBuffer = new JitterBuffer(jitter, period);
    }
    
    public void push(RtpPacket rtpPacket) {
        jitterBuffer.write(rtpPacket);
    }
    
    public void evolve(Buffer buffer) {
        RtpPacket rtpPacket = jitterBuffer.read();
        if (rtpPacket != null) {
            int pt = rtpPacket.getPayloadType();
            Format fmt = rtpMap.get(pt);
            buffer.setFormat(fmt);
            buffer.setTimeStamp(getTimestamp(rtpPacket.getTimestamp(), fmt));
            buffer.setSequenceNumber(seq++);
            
            byte[] data = (byte[]) buffer.getData();
            byte[] payload = rtpPacket.getPayload();
            
            System.arraycopy(payload, 0, data, 0, payload.length);
            
            buffer.setOffset(0);
            buffer.setLength(payload.length);
            buffer.setDuration(period);
            buffer.setDiscard(false);
        } else {
            buffer.setDiscard(true);
        }
    }
    
    public void reset() {
        jitterBuffer.reset();
    }
    
    private long getTimestamp(int timestamp, Format format) {
        if (format.matches(AVProfile.SPEEX)) {
            return timestamp / 16;
        } else {
            return timestamp / 8;
        }
    }
}
