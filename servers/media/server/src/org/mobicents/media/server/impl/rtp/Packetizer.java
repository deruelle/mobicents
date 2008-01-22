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

import java.io.IOException;
import javax.media.Buffer;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.PushBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public class Packetizer implements BufferTransferHandler {

    private byte[] buffer;
    private int pos;
    private int threshold;
    private boolean ready = false;

    /**
     * Creates a new instance of Packetizer
     */
    public Packetizer(int buffSize) {
    //buffer = new Vector(buffSize);
    }

    public Packetizer(int buffSize, int threshold) {
        this.threshold = threshold;
        this.buffer = new byte[buffSize];
    }

    public void transferData(PushBufferStream stream) {
        synchronized (this) {
            Buffer packet = new Buffer();
            try {
                stream.read(packet);
            } catch (IOException e) {
            }

            byte[] data = (byte[]) packet.getData();
            if (data == null) {
                return;
            }

            while (pos + data.length > buffer.length) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    return;
                }
            }

            System.arraycopy(data, 0, buffer, pos, data.length);
            pos += data.length;

            ready = pos >= threshold;
            if (ready) {
                notifyAll();
            }
        }
    }

    public byte[] next(int count) throws InterruptedException {
        synchronized (this) {
            //wait while buffer caching data
            if (!this.ready) {
                wait();
            }

            //read data
            int len = Math.min(count, pos);
            
            if (len == 0) {
                return new byte[count];
            }
            
            byte[] data = new byte[len];

            System.arraycopy(buffer, 0, data, 0, len);
            System.arraycopy(buffer, len, buffer, 0, buffer.length - len);
            pos -= len;
            ready = pos >= threshold;

            notifyAll();
            return data;
        }
    }
}
