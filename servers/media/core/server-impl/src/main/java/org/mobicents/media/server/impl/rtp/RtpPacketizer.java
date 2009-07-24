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

package org.mobicents.media.server.impl.rtp;

import java.io.Serializable;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 *
 * @author Oleg Kulikov
 */
public class RtpPacketizer implements Serializable {
    
    private long seq = 0;
    /**
     * Set required timestamp mark.
     * 
     * @param buffer the media buffer
     * @param packetPeriod  packetization period
     */
    public void process(Buffer buffer, int packetPeriod) {
        Format fmt = buffer.getFormat();
        buffer.setSequenceNumber(seq++);
        if (fmt.equals(Codec.PCMA)) {
            buffer.setTimeStamp(buffer.getSequenceNumber() * 8 * packetPeriod);
        } else if (fmt.equals(Codec.PCMU)) {
            buffer.setTimeStamp(buffer.getSequenceNumber() * 8 * packetPeriod);
        } else if (fmt.equals(Codec.SPEEX)) {
            buffer.setTimeStamp(buffer.getSequenceNumber() * 16 * packetPeriod);
        } else if (fmt.equals(Codec.G729)) {
            buffer.setTimeStamp(buffer.getSequenceNumber() * 8 * packetPeriod);
        } else if (fmt.equals(Codec.GSM)) {
            buffer.setTimeStamp(buffer.getSequenceNumber() * 8 * packetPeriod);
        } else {
            buffer.setTimeStamp(buffer.getSequenceNumber() * 8 * packetPeriod);
        }
    }
}