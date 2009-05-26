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
package org.mobicents.media.server.impl.resource.fft;

import org.mobicents.media.Format;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.spi.Endpoint;

/**
 * 
 * @author Oleg Kulikov
 */
public class SpectraAnalyzer extends AbstractSink {

    private Endpoint endpoint;
    
    public SpectraAnalyzer(Endpoint endpoint, String name) {
        super(name);        
        this.endpoint = endpoint;
    }
    
    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private int offset = 0;
    private byte[] localBuffer = new byte[16000];
    private FFT fft = new FFT();
    private static transient Logger logger = Logger.getLogger(SpectraAnalyzer.class);

    public void start() {
    }

    public void stop() {
    }

    private double[] mod(Complex[] x) {
        double[] res = new double[x.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = Math.sqrt(x[i].re() * x[i].re() + x[i].im() * x[i].im());
        }
        return res;
    }

    protected void sendEvent(double[] spectra) {
        SpectrumEvent evt = new SpectrumEvent(endpoint, getConnection(), getName(), spectra);
        sendEvent(evt);
    }

    public void receive(Buffer buffer) {
        byte[] data = (byte[]) buffer.getData();
        int len = Math.min(16000 - offset, data.length);
        System.arraycopy(data, 0, localBuffer, offset, len);
        offset += len;

        if (logger.isDebugEnabled()) {
            logger.debug("append " + len + " bytes to the local buffer, buff size=" + offset);
        }

        // buffer full?
        if (offset == 16000) {
            double[] media = new double[8000];
            int j = 0;
            for (int i = 0; i < media.length; i++) {
                media[i] = (localBuffer[j++] & 0xff) | (localBuffer[j++] << 8);
            }

            // resampling
            Complex[] signal = new Complex[8192];
            double k = (double) (media.length - 1) / (double) (signal.length);

            for (int i = 0; i < signal.length; i++) {
                int p = (int) (k * i);
                int q = (int) (k * i) + 1;

                double K = (media[q] - media[p]) * media.length;
                double dx = (double) i / (double) signal.length - (double) p / (double) media.length;
                signal[i] = new Complex(media[p] + K * dx, 0);
            }
            localBuffer = new byte[16000];
            offset = 0;

            Complex[] sp = fft.fft(signal);
            double[] res = mod(sp);
            sendEvent(res);
        }
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.MediaSink.isAcceptable(Format).
     */
    public boolean isAcceptable(Format fmt) {
        return fmt.matches(LINEAR_AUDIO);
    }

    public Format[] getFormats() {
        return new Format[]{LINEAR_AUDIO};
    }
}

