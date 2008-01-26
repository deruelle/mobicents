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
package org.mobicents.media.server.impl.jmf.splitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

/**
 * Represents output stream for destination.
 * 
 * @author Oleg Kulikov
 */
public class SplitterOutputStream implements PushBufferStream {

    private long id = System.currentTimeMillis();
    public final static long PERIOD = 1000;
    private Format fmt;
    private BufferTransferHandler transferHandler;
    private List<Buffer> buffers = Collections.synchronizedList(new ArrayList());

    public SplitterOutputStream(Format fmt) {
        if (fmt != null) {
            this.fmt = fmt;
        } else {
            this.fmt = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
        }
    }

    public Format getFormat() {
        return fmt;
    }

    public void read(Buffer buffer) throws IOException {
        if (!buffers.isEmpty()) {
            Buffer buff = buffers.remove(0);
            buffer.setData(buff.getData());
            buffer.setDiscard(buff.isDiscard());
            buffer.setDuration(buff.getDuration());
            buffer.setEOM(buff.isEOM());
            buffer.setFlags(buff.getFlags());
            buffer.setFormat(buff.getFormat());
            buffer.setLength(buff.getLength());
            buffer.setOffset(buff.getOffset());
            buffer.setSequenceNumber(buff.getSequenceNumber());
            buffer.setTimeStamp(buff.getTimeStamp());
        }
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
        this.transferHandler = transferHandler;
    }

    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    public long getContentLength() {
        return SplitterOutputStream.LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
        return false;
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String control) {
        return null;
    }

    protected void push(Buffer buffer) {
        buffers.add(buffer);
        if (transferHandler != null) {
            transferHandler.transferData(this);
        }
    }

    protected void close() {
    }

}
