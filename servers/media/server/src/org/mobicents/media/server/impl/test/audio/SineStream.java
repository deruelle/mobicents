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
import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

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
    
    /** Creates a new instance of GeneratorStream */
    public SineStream(int freq, int duration) {
        this.freq = freq;
        this.duration = duration;
        
        sizeInBytes = (int)(
                (LINEAR_AUDIO.getSampleRate()/ 1000) * 
                (LINEAR_AUDIO.getSampleSizeInBits()/8) *
                duration
                );
        System.out.println("Size in bytes=" + sizeInBytes);
        
        int len = (int)LINEAR_AUDIO.getSampleRate();
        data = new byte[LINEAR_AUDIO.getSampleSizeInBits() / 8 * len];
        int k = 0;
        for (int i = 0; i < len; i++) {
            short s = (short) (Short.MAX_VALUE * Math.sin(2 * Math.PI * freq * i / len));
            data[k++] = (byte) (s >> 8);
            data[k++] = (byte) s;
        }

    }

    public Format getFormat() {
        return LINEAR_AUDIO;
    }

    public void read(Buffer buffer) throws IOException {
        System.out.println("reading");
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

    protected void start() {
        if (!started && transferHandler != null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new Transmitter(this), 0, duration);
            started = true;
        }
    }

    protected void stop() {
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

