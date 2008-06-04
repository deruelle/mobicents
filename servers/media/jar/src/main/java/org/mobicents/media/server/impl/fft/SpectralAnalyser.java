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
package org.mobicents.media.server.impl.fft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.BaseResource;
import org.mobicents.media.server.impl.common.MediaResourceState;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaSink;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.test.SpectrumEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class SpectralAnalyser extends BaseResource implements MediaSink, BufferTransferHandler {

    private List<NotificationListener> listeners = new ArrayList();
    private Codec codec;
    private int offset = 0;
    private byte[] localBuffer = new byte[16000];
    private Logger logger = Logger.getLogger(SpectralAnalyser.class);

    public void configure(Properties config) {
        setState(MediaResourceState.CONFIGURED);
    }

    public void release() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addListener(NotificationListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(NotificationListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void start() {
        setState(MediaResourceState.STARTED);
    }

    public void stop() {
        setState(MediaResourceState.PREPARED);
    }

    public void prepare(Endpoint endpoint, PushBufferStream stream) throws UnsupportedFormatException {
        stream.setTransferHandler(this);
        if (!stream.getFormat().matches(Codec.LINEAR_AUDIO)) {
            codec = CodecLocator.getCodec(stream.getFormat(),
                    Codec.LINEAR_AUDIO);
            setState(MediaResourceState.PREPARED);
            if (codec == null) {
                throw new UnsupportedFormatException(stream.getFormat());
            }
        }
    }

    public PushBufferStream newBranch(String branchID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private double[] mod(Complex[] x) {
        double[] res = new double[x.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = Math.sqrt(x[i].re() * x[i].re() + x[i].im() * x[i].im());
        }
        return res;
    }

    protected void sendEvent(double[] spectra) {
        SpectrumEvent evt = new SpectrumEvent(this, EventID.TEST_SPECTRA, spectra);
        synchronized (listeners) {
            for (NotificationListener listener : listeners) {
                listener.update(evt);
            }
        }
    }

    public void transferData(PushBufferStream stream) {
        if (getState() != MediaResourceState.STARTED) {
            return;
        }

        Buffer buffer = new Buffer();
        try {
            stream.read(buffer);
        } catch (IOException e) {
        }

        byte[] data = (byte[]) buffer.getData();
        if (codec != null) {
            data = codec.process(data);
        }

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

            //resampling
            Complex[] signal = new Complex[8192];
            double k = (double) (media.length - 1) / (double) (signal.length);

            for (int i = 0; i < signal.length; i++) {
                int p = (int) (k * i);
                int q = (int) (k * i) + 1;

                double K = (media[q] - media[p]) * media.length;
                double dx = (double) i / (double) signal.length - (double) p / (double) media.length;
                signal[i] = new Complex(media[p] + K * dx, 0);
            }

/*            int k = 0;
            for (int i = 0; i < 8000; i++) {
                signal[i] = new Complex(
                        (localBuffer[k++] & 0xff) | (localBuffer[k++] << 8), 0);
                System.out.println(i + " " + signal[i].re());
            }

            //pad with zero
            for (int i = 0; i < 192; i++) {
                signal[8000 + i] = new Complex(0, 0);
            }
*/
            localBuffer = new byte[16000];
            offset = 0;

            Complex[] sp = FFT.fft(signal);
            double[] res = mod(sp);

            sendEvent(res);
        }
    }
}
