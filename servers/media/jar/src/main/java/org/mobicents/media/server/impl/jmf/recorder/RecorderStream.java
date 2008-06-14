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
package org.mobicents.media.server.impl.jmf.recorder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import org.mobicents.media.Buffer;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.PushBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public class RecorderStream extends InputStream implements BufferTransferHandler {

    private List<Buffer> buffers = new CopyOnWriteArrayList();
    private int available = 0;
    private Semaphore semaphore = new Semaphore(0);
    private boolean blocked = false;
    private boolean eom = false;

    public RecorderStream(PushBufferStream inputStream) {
        inputStream.setTransferHandler(this);
    }

    @Override
    public int available() {
        return available;
    }

    @Override
    public int read() throws IOException {
        //System.out.println("Read byte");
        if (eom) {
            //System.out.println("******EOM*****");
            return -1;
        }

        if (buffers.isEmpty()) {
            blocked = true;
            try {
                //System.out.println("read(): block");
                semaphore.acquire();
                //System.out.println("read(): unblock");
            } catch (InterruptedException e) {
                return -1;
            }
        }

        byte[] buff = new byte[1];
        int count = readBytes(buff);

        //System.out.println("read bytes=" + count + ", value=" + (buff[0] & 0xff) + ", available=" + available);
        return count == -1 ? -1 : buff[0] & 0xff;
    }

    @Override
    public int read(byte[] buff) {
        //System.out.println("Read buffer");
        if (buffers.isEmpty()) {
            blocked = true;
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                return -1;
            }
        }
        return readBytes(buff);
    }

    private int readBytes(byte[] buff) {
        if (buffers.isEmpty()) {
            return -1;
        }

        int count = 0;
        while (count < buff.length && !buffers.isEmpty()) {
            Buffer buffer = buffers.get(0);
            byte[] data = (byte[]) buffer.getData();

            int remainder = buff.length - count;
            int len = Math.min(remainder, buffer.getLength() - buffer.getOffset());

            System.arraycopy(data, buffer.getOffset(), buff, count, len);
            count += len;

            buffer.setOffset(buffer.getOffset() + len);
            if (buffer.getOffset() == buffer.getLength()) {
                buffers.remove(0);
            }
            
            if (buffer.isEOM()) {
                eom = true;
                //break;
            }

        }

        available -= count;
        return count;
    }

    public void transferData(PushBufferStream stream) {
        Buffer buffer = new Buffer();
        try {
            stream.read(buffer);
            //System.out.println("transfer data, isEOM=" + buffer.isEOM());
        } catch (IOException e) {
        }

        available += (buffer.getLength() - buffer.getOffset());
        buffers.add(buffer);

        if (blocked) {
            blocked = false;
            semaphore.release();
        }
    }
}
