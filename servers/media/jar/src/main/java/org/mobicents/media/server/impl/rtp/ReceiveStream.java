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
import javax.media.Buffer;
import javax.media.Format;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;
/**
 *
 * @author Oleg Kulikov
 */
public class ReceiveStream implements PushBufferStream, Runnable {

    private BufferTransferHandler transferHandler;
    private long seq = 0L;
    private boolean stopped = false;
    private Format fmt;
    private int period;
    private JitterBuffer jitterBuffer;
    private Thread runThread;

    private Logger logger = Logger.getLogger(ReceiveStream.class);
    
    /** Creates a new instance of ReceiveStream */
    public ReceiveStream(Format fmt, int period, int jitter) {
        this.fmt = fmt;
        this.period = period;

        jitterBuffer = new JitterBuffer(fmt, jitter);

        runThread = new Thread(this);
        runThread.start();
    }

    public Format getFormat() {
        return fmt;
    }

    public void read(Buffer buffer) throws IOException {
        try {
            byte[] data = jitterBuffer.next();

            buffer.setData(data);
            buffer.setOffset(0);
            buffer.setLength(data.length);
            buffer.setFormat(fmt);
            buffer.setSequenceNumber(seq);
            buffer.setTimeStamp(period * seq);
            seq++;
        } catch (InterruptedException e) {
            stopped = true;
        }
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
        synchronized (this) {
            this.transferHandler = transferHandler;
            notifyAll();
        }
    }

    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
        return false;
    }

    public Object[] getControls() {
        return null;
    }

    public Object getControl(String ctrl) {
        return null;
    }

    protected void push(int seq, byte[] data) {
        if (logger.isDebugEnabled()) {
            logger.debug("push " + data.length + " bytes to jitter buffer, fmt=" + fmt);
        }
        jitterBuffer.push(seq, data);
    }

    protected void disable() {
        synchronized (this) {
            stopped = true;
            notifyAll();
        }
    }

    private void waitForHandler() throws InterruptedException {
        synchronized (this) {
            if (transferHandler == null) {
                wait();
            }
        }
    }

    public void run() {
        try {
            waitForHandler();
        } catch (InterruptedException e) {
            return;
        }
        while (!stopped) {
            if (this.jitterBuffer.isReady()) {
                transferHandler.transferData(this);
            } else try {
                Thread.currentThread().sleep(20);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
