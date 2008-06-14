/*
 * GeneratorStream.java
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
package org.mobicents.media.server.impl.test.audio;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.ContentDescriptor;
import org.mobicents.media.protocol.PushBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public class SineStream implements PushBufferStream {

    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    private BufferTransferHandler transferHandler;
    private boolean started = false;
    private byte[] data;
    private long seqNumber = 0;
    private int freq;
    private Timer timer = new Timer();
    private int offset = 0;
    private int sizeInBytes;
    private int duration;
    private boolean terminateAfterSequence = false;

    /** Creates a new instance of GeneratorStream */
    public SineStream(int freq, int duration, boolean terminateAfterSequence) {
        this.terminateAfterSequence = terminateAfterSequence;
        this.freq = freq;
        this.duration = duration;

        sizeInBytes = (int) (LINEAR_AUDIO.getSampleRate() * (LINEAR_AUDIO.getSampleSizeInBits() / 8)/1000 * this.duration); // Duration
        // is
        // in
        // mS
        //System.out.println("SineStream - Size in bytes=" + sizeInBytes);

        // We F in 1/s - however we can put here duration of 10s,0.1s - this
        // would affect how values are spread, this is bad, we must add scalling
        // to that

        double d = (double) 1000 / this.duration;
        data = new byte[(int)LINEAR_AUDIO.getSampleRate() * LINEAR_AUDIO.getSampleSizeInBits()/8];

        int len = data.length / 2;
        int k = 0;

        for (int i = 0; i < len; i++) {
            short s = (short) (Short.MAX_VALUE * Math.sin(2 * Math.PI * freq * i / len));
            data[k++] = (byte) s;
            data[k++] = (byte) (s >> 8);
        }

    }

    public Format getFormat() {
        return LINEAR_AUDIO;
    }

    public void read(Buffer buffer) throws IOException {
//        System.out.println("reading");
        byte[] media = new byte[sizeInBytes];

        int count = Math.min(data.length - offset, sizeInBytes);
        System.arraycopy(data, offset, media, 0, count);
        offset += count;
        if (offset == data.length) {
            offset = 0;
        }

        buffer.setOffset(0);
        buffer.setLength(media.length);
        buffer.setSequenceNumber(seqNumber);
        buffer.setDuration(duration);
        buffer.setTimeStamp(seqNumber * duration); //@todo: synchronize clock
        buffer.setData(media);
        buffer.setFormat(LINEAR_AUDIO);
        seqNumber++;
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
        this.transferHandler = transferHandler;
        if (transferHandler != null) {
            start();
        }
    }

    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    public long getContentLength() {
        return SineStream.LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
        return false;
    }

    public Object[] getControls() {
        return null;
    }

    public Object getControl(String string) {
        return null;
    }

    public void start() {
        if (!started && transferHandler != null) {
            timer = new Timer();

            if (!this.terminateAfterSequence) {
                timer.scheduleAtFixedRate(new Transmitter(this), 0, duration);
            } else {
                timer.schedule(new Transmitter(this), duration);
            }
            started = true;
        }
    }

    public void stop() {
        if (started) {
            timer.cancel();
            timer.purge();
            started = false;
        }
    }

    private class Transmitter extends TimerTask {

        private PushBufferStream stream;

        public Transmitter(PushBufferStream stream) {
            this.stream = stream;
        }

        public void run() {
            transferHandler.transferData(stream);
        }
    }
}

