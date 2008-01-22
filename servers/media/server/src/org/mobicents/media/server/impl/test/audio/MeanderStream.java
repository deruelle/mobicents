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
public class MeanderStream implements PushBufferStream {

    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    private BufferTransferHandler transferHandler;
    private AudioFormat fmt = LINEAR_AUDIO;
    private boolean started = false;
    private byte[] silence;
    private byte[] sound;
    private long seqNumber = 0;
    private Timer timer;
    private int sizeInBytes;
    private int duration;
    private boolean isSilence = false;
    private TimerTask transmittor;

    public MeanderStream(int duration, Timer timer) {
        this.duration = duration;
        this.timer = timer;

        sizeInBytes = (int) ((fmt.getSampleRate() / 1000) *
                (fmt.getSampleSizeInBits() / 8) *
                duration);
        silence = new byte[sizeInBytes];
        sound = new byte[sizeInBytes];

        int k = 0;
        for (int i = 0; i < sound.length / 2; i++) {
            sound[k++] = (byte) (Short.MAX_VALUE >> 8);
            sound[k++] = (byte) (Short.MAX_VALUE);
        }

    }

    protected void start() {
        if (!started && transferHandler != null) {
            started = true;
            if (timer == null) {
                timer = new Timer();
            }
            transmittor = new Transmitter(this);
            timer.scheduleAtFixedRate(transmittor, duration, duration);
        }
    }

    protected void stop() {
        if (started) {
            started = false;
            transmittor.cancel();
        }
    }

    public Format getFormat() {
        return fmt;
    }

    public void read(Buffer buffer) throws IOException {
        byte[] media = isSilence ? silence : sound;
        buffer.setOffset(0);
        buffer.setLength(media.length);
        buffer.setSequenceNumber(seqNumber);
        buffer.setDuration(duration);
        buffer.setTimeStamp(seqNumber * duration); //@todo: synchronize clock
        buffer.setData(media);
        buffer.setFormat(LINEAR_AUDIO);
        seqNumber++;
        isSilence = !isSilence;
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
        return MeanderStream.LENGTH_UNKNOWN;
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
