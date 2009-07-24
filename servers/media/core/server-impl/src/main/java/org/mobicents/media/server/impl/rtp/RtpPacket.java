/*
 * RtpPacket.java
 *
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 *
 * @author Oleg Kulikov
 */
public class RtpPacket implements Serializable {

    private int version = 2;
    private boolean padding = false;
    private boolean extensions = false;
    private int cc = 0;
    private boolean marker = false;
    private int payloadType;
    private int seqNumber;
    private int timestamp;
    private long ssrc;
    private byte[] payload;
    private int offset = 0;
    private int length = 0;

    public RtpPacket(ByteBuffer readerBuffer) {
        int len = readerBuffer.limit();
        int b = readerBuffer.get() & 0xff;
        
        version = (b & 0x0C) >> 6;
        padding = (b & 0x20) == 0x020;
        extensions = (b & 0x10) == 0x10;
        cc = b & 0x0F;
        
        b = readerBuffer.get() & 0xff;
        marker = (b & 0x80) == 0x80;
        payloadType = b & 0x7F;

        seqNumber = (readerBuffer.get() & 0xff) << 8;
        seqNumber = seqNumber | (readerBuffer.get() & 0xff);

        timestamp = readerBuffer.getInt();
        ssrc = readerBuffer.getInt();
        
        payload = new byte[len - 12];
        readerBuffer.get(payload, 0, payload.length);
    }
    
    /** Creates a new instance of RtpPacket */
    public RtpPacket(byte[] data) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        int b = in.read() & 0xff;
        version = (b & 0x0C) >> 6;
        padding = (b & 0x20) == 0x020;
        extensions = (b & 0x10) == 0x10;
        cc = b & 0x0F;

        b = in.read() & 0xff;

        marker = (b & 0x80) == 0x80;
        payloadType = b & 0x7F;
        seqNumber = (in.read() & 0xff) << 8;
        seqNumber = seqNumber | (in.read() & 0xff);

        timestamp = in.readInt();
        ssrc = in.readInt();

        payload = new byte[160];
        int numBytes = in.read(payload);
        if (numBytes < 0) {
            numBytes = 0;
        }
        byte[] realPayload = new byte[numBytes];
        for (int q = 0; q < numBytes; q++) {
            realPayload[q] = payload[q];
        }
        payload = realPayload;
    }

    public RtpPacket(byte payloadType, int seqNumber, int timestamp, long ssrc,
            byte[] payload) {
        this.payloadType = payloadType;
        this.payload = payload;
        this.seqNumber = seqNumber;
        this.timestamp = timestamp;
        this.ssrc = ssrc;
    }

    public RtpPacket(byte payloadType, int seqNumber, int timestamp, long ssrc,
            byte[] payload, int offset, int length) {
        this.payloadType = payloadType;
        this.payload = payload;
        this.seqNumber = seqNumber;
        this.timestamp = timestamp;
        this.ssrc = ssrc;
        this.offset = offset;
        this.length = length;
    }

    public int getPayloadType() {
        return payloadType;
    }

    public int getSeqNumber() {
        return this.seqNumber;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bout.write(0x80);
        bout.write(payloadType);
        bout.write((byte) ((seqNumber & 0xFF00) >> 8));
        bout.write((byte) (seqNumber & 0x00FF));

        bout.write((byte) ((timestamp & 0xFF000000) >> 24));
        bout.write((byte) ((timestamp & 0x00FF0000) >> 16));
        bout.write((byte) ((timestamp & 0x0000FF00) >> 8));
        bout.write((byte) ((timestamp & 0x000000FF)));

        bout.write((byte) ((ssrc & 0xFF000000) >> 24));
        bout.write((byte) ((ssrc & 0x00FF0000) >> 16));
        bout.write((byte) ((ssrc & 0x0000FF00) >> 8));
        bout.write((byte) ((ssrc & 0x000000FF)));

        bout.write(payload, offset, length);
        return bout.toByteArray();
    }

    @Override
    public String toString() {
        return "RTP Packet[seq=" + this.seqNumber + ", timestamp=" + timestamp +
                ", payload_size=" + payload.length + "]";
    }
}
