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
package org.mobicents.media.server.impl.events.dtmf;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;

/**
 *
 * @author Oleg Kulikov
 */
public class InbandGenerator extends AbstractSource {

    private final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1);
    public final static String[][] events = new String[][]{
        {"1", "2", "3", "A"},
        {"4", "5", "6", "B"},
        {"7", "8", "9", "C"},
        {"*", "0", "#", "D"}};

    public static String getToneName(int row, int column) {
        try {
            return events[row][column];
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            return null;
        }
    }
    
    private int[] lowFreq = new int[]{697, 770, 852, 941};
    private int[] highFreq = new int[]{1209, 1336, 1477, 1633};
    
    private boolean started = false;
    private byte[] data;
    private long seqNumber = 0;
    private Timer timer = new Timer();
    private int offset = 0;
    private int sizeInBytes;
    private int packetPeriod;

    public InbandGenerator(String name) {
        super(name);
    }
    
    public InbandGenerator(String tone, int packetPeriod) {
    	 super("InbandGenerator");
        this.packetPeriod = packetPeriod;
        //codec = CodecLocator.getCodec(Codec.LINEAR_AUDIO, Codec.PCMU);

        sizeInBytes = (int) (LINEAR.getSampleRate() *
                (LINEAR.getSampleSizeInBits() / 8) / 1000 * packetPeriod);
        //System.out.println("Size in bytes=" + sizeInBytes);

        data = new byte[(int) LINEAR.getSampleRate() * LINEAR.getSampleSizeInBits() / 8];
        int len = data.length / 2;

        int[] freq = getFreq(tone);
        //System.out.println("f0=" + freq[0] + ", f1=" + freq[1]);

        int k = 0;
        for (int i = 0; i < len; i++) {
            short s = (short) ((short) (Short.MAX_VALUE / 2 * Math.sin(2 * Math.PI * freq[0] * i / len)) +
                    (short) (Short.MAX_VALUE / 2 * Math.sin(2 * Math.PI * freq[1] * i / len)));
            data[k++] = (byte) (s);
            data[k++] = (byte) (s >> 8);
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
        buffer.setDuration(packetPeriod);
        buffer.setTimeStamp(seqNumber * packetPeriod); //@todo: synchronize clock
        buffer.setData(media);
        buffer.setFormat(LINEAR);
        seqNumber++;
    }

    private int[] getFreq(String tone) {
        int freq[] = new int[2];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (events[i][j].equalsIgnoreCase(tone)) {
                    freq[0] = lowFreq[i];
                    freq[1] = highFreq[j];
                }
            }
        }
        return freq;
    }

    public Format getFormat() {
        return LINEAR;
    }

/*    public void setTransferHandler(BufferTransferHandler transferHandler) {
        this.transferHandler = transferHandler;
        if (transferHandler != null) {
            start();
        }
    }
*/

    public void start() {
/*        if (!started && transferHandler != null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new Transmitter(this), 0, packetPeriod);
            started = true;
        }
 */ 
    }

    public void stop() {
        if (started) {
            timer.cancel();
            timer.purge();
            started = false;
        }
    }

    private class Transmitter extends TimerTask {

//        private PushBufferStream stream;

        public Transmitter(/*PushBufferStream stream*/) {
            //this.stream = stream;
        }

        public void run() {
           // transferHandler.transferData(stream);
        }
    }

    public Format[] getFormats() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
