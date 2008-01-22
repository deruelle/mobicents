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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public class PushBufferAudioStream implements PushBufferStream, BufferTransferHandler {

    private AudioPlayer audioPlayer;
    private BufferTransferHandler transferHandler;
    private List<Buffer> media = Collections.synchronizedList(new ArrayList());
    private boolean started = false;
    private boolean active = false;
    
    private TimerTask transmittor;
    private int packetSize;
    private long seq = 0;
    
    public PushBufferAudioStream(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.packetSize = (int) (
                (audioPlayer.format.getSampleRate() / 1000) *
                (audioPlayer.format.getSampleSizeInBits() / 8) *
                audioPlayer.packetPeriod);
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
        }
    }

    protected void stop() {
        if (started) {
            transmittor.cancel();
            audioPlayer.timer.purge();
            started = false;
            media.clear();
        }
    }

    public void setActive(boolean active) {
        if (this.active && !active) {
            this.active = active;
            audioPlayer.endOfMedia();
        } else if (!this.active && active) {
            this.active = active;
            audioPlayer.started();
        }
    }

    public void read(Buffer buffer) throws IOException {
        if (!media.isEmpty()) {
            byte[] data = new byte[packetSize];

            int count = 0;
            while (count < packetSize && !media.isEmpty()) {
                Buffer buff = media.get(0);
                
                if (buff.isEOM()) {
                    buffer.setEOM(true);
                    setActive(false);
                    return;
                }
                
                int len = Math.min(packetSize - count, buff.getLength() - buff.getOffset());
                System.arraycopy(buff.getData(), buff.getOffset(), data, count, len);

                count += len;
                buff.setOffset(buff.getOffset() + len);

                if (buff.getOffset() == buff.getLength()) {
                    media.remove(buff);
                }
            }

           buffer.setData(data);
           buffer.setOffset(0);
           buffer.setLength(data.length);
           buffer.setDuration(audioPlayer.packetPeriod);
           buffer.setFormat(audioPlayer.format);
           buffer.setTimeStamp(seq * audioPlayer.packetPeriod);
           buffer.setSequenceNumber(seq++);
        }
        setActive(!media.isEmpty());
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

    public Object getControl(String arg0) {
        return null;
    }

    public void transferData(PushBufferStream stream) {
        try {
            Buffer buffer = new Buffer();
            stream.read(buffer);
            media.add((Buffer) buffer.clone());
        } catch (IOException e) {
        }
    }

    private class Transmittor extends TimerTask {

        private PushBufferStream stream;

        public Transmittor(PushBufferStream stream) {
            this.stream = stream;
        }

        public void run() {
            if (transferHandler != null) {
                transferHandler.transferData(stream);
            }
        }
    }
}
