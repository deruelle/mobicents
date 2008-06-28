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
package org.mobicents.media.server.impl.jmf.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.ContentDescriptor;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author Oleg Kulikov
 */
public class MediaPushProxy implements PushBufferStream, BufferTransferHandler {

    private int period = 20;
    private BufferTransferHandler transferHandler;
    private AudioFormat fmt = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
    private List<Buffer> buffers = Collections.synchronizedList(new ArrayList());
    private boolean started = false;
    private int sizeInBytes;
    private long seq = 0;
    private Codec codec;
    private TimerTask transmission;
    private Timer timer;
    
    public MediaPushProxy() {
    }

    public MediaPushProxy(Timer timer, int period, AudioFormat fmt) {
        this.period = period;
        this.fmt = fmt;
        this.timer = timer;
    }

    public MediaPushProxy(Format fmt) {
        this.fmt = (AudioFormat) fmt;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setInputStream(PushBufferStream inputStream) throws UnsupportedFormatException {
        if (inputStream != null) {
            inputStream.setTransferHandler(this);
            sizeInBytes = ((AudioFormat)inputStream.getFormat()).getSampleSizeInBits() * period;
            if (!fmt.matches(inputStream.getFormat())) {
                codec = CodecLocator.getCodec(inputStream.getFormat(), fmt);
                if (codec == null) {
                    throw new UnsupportedFormatException(inputStream.getFormat());
                }
            }
            start();
        } else {
            stop();
        }
    }

    public void start() {
        if (transferHandler != null && !started) {
            transmission = new Transmission(this);
            timer.scheduleAtFixedRate(transmission, 0, period);
            started = true;
        }
    }

    public void stop() {
        if (started) {
            transmission.cancel();
            timer.purge();
            started = false;
        }
    }

    public Format getFormat() {
        return fmt;
    }

    private byte[] readFrame(int count) {
        byte[] frame = new byte[count];
        int remainder = count;

        while (remainder > 0 && !buffers.isEmpty()) {
            Buffer buff = buffers.get(0);
            byte[] data = (byte[]) buff.getData();

            if (data == null) {
                buffers.remove(0);
                break;
            }

            int offset = buff.getOffset();

            int len = Math.min(remainder, data.length - offset);
            System.arraycopy(data, offset, frame, count - remainder, len);

            remainder -= len;
            offset += len;

            if (offset == data.length) {
                buffers.remove(0);
            } else {
                buff.setOffset(offset);
            }
        }

        return frame;
    }

    public void read(Buffer buffer) throws IOException {
        byte[] fdata = readFrame(sizeInBytes);
        byte[] data = codec != null ? codec.process(fdata) : fdata;
        buffer.setData(data);
        buffer.setDiscard(false);
        buffer.setDuration(period);
        buffer.setEOM(false);
        buffer.setFlags(0);
        buffer.setFormat(fmt);
        buffer.setLength(data.length);
        buffer.setOffset(0);
        buffer.setTimeStamp(seq * period);
        buffer.setSequenceNumber(seq++);
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
        this.transferHandler = transferHandler;
        start();
    }

    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    public long getContentLength() {
        return MediaPushProxy.LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
        return false;
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String ctrl) {
        return null;
    }

    public void transferData(PushBufferStream bufferStream) {
        Buffer buffer = new Buffer();
        try {
            bufferStream.read(buffer);
            buffers.add((Buffer) buffer.clone());
        } catch (IOException e) {
        }
    }

    private class Transmission extends TimerTask {

        private PushBufferStream pushStream;

        public Transmission(PushBufferStream pushStream) {
            this.pushStream = pushStream;
        }

        public void run() {
            if (!buffers.isEmpty()) {
                transferHandler.transferData(pushStream);
            } 
        }
    }
}
