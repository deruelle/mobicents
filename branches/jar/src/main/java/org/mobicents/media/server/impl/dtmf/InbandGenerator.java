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

package org.mobicents.media.server.impl.dtmf;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.ContentDescriptor;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;

/**
 *
 * @author Oleg Kulikov
 */
public class InbandGenerator implements PushBufferStream {
    private final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1);
    
    public final static String[][] events = new String[][] {
        {"1", "2", "3", "A"},
        {"4", "5", "6", "B"},
        {"7", "8", "9", "C"},
        {"*", "0", "#", "D"}
    };
    
    private int[] lowFreq = new int[]{697, 770, 852, 941};
    private int[] highFreq = new int[]{1209, 1336, 1477, 1633};

    private BufferTransferHandler transferHandler;

    private boolean started = false;
    private byte[] data;
    private long seqNumber = 0;
    private Timer timer = new Timer();

    private int offset = 0;
    private int sizeInBytes;
    private int duration;
    
    private Codec codec;
    
    public InbandGenerator(String tone, int duration) {
        this.duration = duration;
        codec = CodecLocator.getCodec(Codec.LINEAR_AUDIO, Codec.PCMU);
        sizeInBytes = (int)(
                (LINEAR.getSampleRate()/ 1000) * 
                (LINEAR.getSampleSizeInBits()/8) *
                duration
                );
        System.out.println("Size in bytes=" + sizeInBytes);
        
        int len = (int)LINEAR.getSampleRate();
        data = new byte[LINEAR.getSampleSizeInBits() / 8 * len];
        
        int[] freq = getFreq(tone);
        System.out.println("f0=" + freq[0] + ", f1=" + freq[1]);
        
        Random rnd = new Random();
        
        int k = 0;
        for (int i = 0; i < len; i++) {
            short s = (short) (
                    (short)(Short.MAX_VALUE/2 * Math.sin(2 * Math.PI * freq[0] * i / len)) +
                    (short)(Short.MAX_VALUE/2 * Math.sin(2 * Math.PI * freq[1] * i / len))
            );
            data[k++] = (byte) (s);
            data[k++] = (byte) (s >> 8);
            //System.out.println("s=" + s);
        }
    }
    
    public static void print(byte[] data) {
        System.out.println("--------------------");
        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i] + " ");
        }
        System.out.println();
        System.out.println("--------------------");
    }
    
    public void read(Buffer buffer) throws IOException {
        //System.out.println("reading");
        byte[] media = new byte[sizeInBytes];
        
        
        int count = Math.min(data.length - offset, sizeInBytes);
        System.arraycopy(data, offset, media, 0, count);
        offset += count;
        if (offset == data.length) {
            offset = 0;
        }

        //System.out.println("src=");
        //print(media);
        
        byte[] media1 = codec.process(media);
        
        //System.out.println("compressed=");
        //print(media1);
        
        buffer.setOffset(0);
        buffer.setLength(media1.length);
        buffer.setSequenceNumber(seqNumber);
        buffer.setDuration(duration);
        buffer.setTimeStamp(seqNumber * duration); //@todo: synchronize clock
        buffer.setData(media1);
        buffer.setFormat(Codec.PCMU);
        seqNumber++;
    }
    
    private int[] getFreq(String tone) {
        int freq[] = new int[2];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (events[i][j].equalsIgnoreCase(tone)) {
                    freq[0] = lowFreq[i];
                    freq[1]= highFreq[j];
                }
            }
        }
        return freq;
    }
    
    public Format getFormat() {
        return codec.PCMU;
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
        return LENGTH_UNKNOWN;
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
