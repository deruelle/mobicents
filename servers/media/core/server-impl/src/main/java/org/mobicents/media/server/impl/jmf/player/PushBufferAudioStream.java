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
package org.mobicents.media.server.impl.jmf.player;

import java.io.IOException;
import java.util.TimerTask;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.ContentDescriptor;
import org.mobicents.media.protocol.PushBufferStream;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;

/**
 * 
 * @author Oleg Kulikov
 */
public class PushBufferAudioStream implements PushBufferStream {

    private AudioPlayer audioPlayer;
    private BufferTransferHandler transferHandler;
    private byte[] frame;
    private int duration;
    private boolean eom = false;
    private boolean started = false;
    private TimerTask transmittor;
    private int packetSize;
    private long seq = 0;
    private AudioInputStream localStream;
    private Codec codec;
    
    public PushBufferAudioStream(AudioPlayer audioPlayer,
            AudioInputStream stream) throws UnsupportedFormatException {
        this.audioPlayer = audioPlayer;
        this.localStream = stream;

        AudioFormat fmt = new AudioFormat(getEncoding(stream.getFormat().getEncoding()), stream.getFormat().getFrameRate(), stream.getFormat().getFrameSize() * 8, stream.getFormat().getChannels());

        if (!fmt.matches(audioPlayer.format)) {
            codec = CodecLocator.getCodec(fmt, audioPlayer.format);
            if (codec == null) {
                throw new UnsupportedFormatException(audioPlayer.format);
            }
        }

        packetSize = (int) ((fmt.getSampleRate() / 1000) * (fmt.getSampleSizeInBits() / 8) * audioPlayer.packetPeriod);
    }

    private String getEncoding(Encoding encoding) {
        if (encoding == Encoding.ALAW) {
            return "ALAW";
        } else if (encoding == Encoding.ULAW) {
            return "ULAW";
        } else if (encoding == Encoding.PCM_SIGNED) {
            return "LINEAR";
        } else if (encoding == Encoding.PCM_UNSIGNED) {
            return "LINEAR";
        } else {
            return null;
        }
    }

    public Format getFormat() {
        return audioPlayer.format;
    }

    protected void start() {
        if (!started) {
            transmittor = new Transmittor(this);
            audioPlayer.timer.scheduleAtFixedRate(transmittor,
                    audioPlayer.packetPeriod, audioPlayer.packetPeriod);
            started = true;
            audioPlayer.started();
        }
    }

    private void terminate() {
        if (started) {
            transmittor.cancel();
            audioPlayer.timer.purge();
        }
    }

    protected void stop() {
        if (started) {
            terminate();
            started = false;
            audioPlayer.stopped();
        }
    }

    protected void fail(Exception e) {
        terminate();
        started = false;
        audioPlayer.failed(e);
    }

    public void read(Buffer buffer) throws IOException {
        buffer.setData(frame);
        buffer.setDuration(duration);
        buffer.setLength(frame.length);
        buffer.setOffset(0);
        buffer.setFormat(audioPlayer.format);
        buffer.setTimeStamp(seq * audioPlayer.packetPeriod);
        buffer.setEOM(eom);
        buffer.setSequenceNumber(seq++);
        if (codec != null) {
            codec.process(buffer);
        }
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
        this.transferHandler = transferHandler;
    }

    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    public long getContentLength() {
        return PushBufferAudioStream.LENGTH_UNKNOWN;
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

    private class Transmittor extends TimerTask {

        private PushBufferStream stream;

        public Transmittor(PushBufferStream stream) {
            this.stream = stream;
        }

        public void run() {
            byte[] packet = new byte[packetSize];
            try {
                int len = localStream.read(packet);
                if (len == -1) {
                    terminate();
                    if (started) {
                        audioPlayer.endOfMedia();
                    }
                } else {
                    frame = new byte[len];
                    System.arraycopy(packet, 0, frame, 0, len);

                    duration = (int) (audioPlayer.packetPeriod * len / packetSize);
                    eom = len < packetSize;

//                    if (codec != null) {
//                        frame = codec.process(frame);
//                    }

                    if (transferHandler != null) {
                        transferHandler.transferData(stream);
                    }
                }
            } catch (IOException e) {
                fail(e);
            }
        }
    }
}
