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
package org.mobicents.media.server.impl.events.dtmf;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;

/**
 * Implements inband DTMF detector.
 * 
 * Inband means that DTMF is transmitted within the audio of the phone 
 * conversation, i.e. it is audible to the conversation partners. Therefore only 
 * uncompressed codecs like g711 alaw or ulaw can carry inband DTMF reliably. 
 * Female voice are known to once in a while trigger the recognition of a DTMF 
 * tone. For analog lines inband is the only possible means to transmit DTMF. 
 *
 * @author Oleg Kulikov
 */
public class InbandDetector extends AbstractSink {

    private final static AudioFormat LINEAR = 
            new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    
    private final static Format[] FORMATS = new Format[]{LINEAR};
    public final static String[][] events = new String[][]{
        {"1", "2", "3", "A"},
        {"4", "5", "6", "B"},
        {"7", "8", "9", "C"},
        {"*", "0", "#", "D"}
    };
    /** DTMF tone duration in milliseconds */
    private int TONE_DURATION = 50;
    private double THRESHOLD = 30;
    private int[] lowFreq = new int[]{697, 770, 852, 941};
    private int[] highFreq = new int[]{1209, 1336, 1477, 1633};
    private byte[] localBuffer;
    private int offset = 0;
    private Filter filter = null;
    private boolean started = false;
    private DtmfDetector detector;
    
    /**
     * Creates new instance of Detector.
     */
    public InbandDetector(DtmfDetector detector) {
    	super("InbandDetector");
        this.detector = detector;
        localBuffer = new byte[16 * TONE_DURATION];
        filter = new Filter();
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.MediaSink#start().
     */
    public void start() {
        started = true;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.server.spi.MediaSink#stop().
     */
    public void stop() {
        started = false;
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.protocol.BufferTransferHandler.transferData().
     */
    public void receive(Buffer buffer) {
        byte[] data = (byte[]) buffer.getData();
        int len = Math.min(localBuffer.length - offset, data.length);
        System.arraycopy(data, 0, localBuffer, offset, len);
        offset += len;

        // buffer full?
        if (offset == localBuffer.length) {
            double[] signal = new double[localBuffer.length / 2];
            int k = 0;
            for (int i = 0; i < signal.length; i++) {
                signal[i] = ((localBuffer[k++] & 0xff) | (localBuffer[k++] << 8));
            }

            localBuffer = new byte[localBuffer.length];
            offset = 0;

            double p[] = getPower(lowFreq, signal);
            double P[] = getPower(highFreq, signal);

            String tone = getTone(p, P);
            if (tone != null) {
                detector.digitBuffer.push(tone);
            }
        }

    }

    /**
     * Calculate power of the specified frequencies.
     * 
     * @param freq the array of frequencies
     * @param data signal
     * @return the power for the respective frequency
     */
    private double[] getPower(int[] freq, double[] data) {
        double[] power = new double[freq.length];
        for (int i = 0; i < freq.length; i++) {
            power[i] = filter.getPower(freq[i], data, (double) TONE_DURATION / (double) 1000);
        }
        return power;
    }

    /**
     * Searches maximum value in the specified array.
     * 
     * @param data[] input data.
     * @return the index of the maximum value in the data array.
     */
    private int getMax(double data[]) {
        int idx = 0;
        double max = data[0];
        for (int i = 1; i < data.length; i++) {
            if (max < data[i]) {
                max = data[i];
                idx = i;
            }
        }
        return idx;
    }

    /**
     * Searches DTMF tone.
     * 
     * @param f the low frequency array
     * @param F the high frequency array.
     * @return DTMF tone.
     */
    private String getTone(double f[], double F[]) {
        int fm = getMax(f);
        boolean fd = true;

        for (int i = 0; i < f.length; i++) {
            if (fm == i) {
                continue;
            }
            double r = f[fm] / (f[i] + 1E-15);
            if (r < THRESHOLD) {
                fd = false;
                break;
            }
        }

        if (!fd) {
            return null;
        }

        int Fm = getMax(F);
        boolean Fd = true;

        for (int i = 0; i < F.length; i++) {
            if (Fm == i) {
                continue;
            }
            double r = F[Fm] / (F[i] + 1E-15);
            if (r < THRESHOLD) {
                Fd = false;
                break;
            }
        }

        if (!Fd) {
            return null;
        }

        return events[fm][Fm];
    }

    public Format[] getFormats() {
        return FORMATS;
    }

    public boolean isAcceptable(Format format) {
        return format.matches(LINEAR);
    }
}
