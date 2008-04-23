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
package org.mobicents.media.server.impl.dtmf;

import java.io.IOException;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanServerLocator;
import org.mobicents.media.Buffer;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.goertzel.Filter;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.common.MediaResourceState;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;
import org.mobicents.media.server.impl.common.MediaResourceState;

/**
 * 
 * @author Oleg Kulikov
 */
public class InbandDetector extends BaseDtmfDetector implements
        BufferTransferHandler {

    public final static String[][] events = new String[][]{
        {"1", "2", "3", "A"}, {"4", "5", "6", "B"},
        {"7", "8", "9", "C"}, {"*", "0", "#", "D"}};
    
    private int[] lowFreq = new int[]{697, 770, 852, 941};
    private int[] highFreq = new int[]{1209, 1336, 1477, 1633};
    private Codec codec;
    private byte[] localBuffer = new byte[16000];
    private int offset = 0;
    private Filter filter = new Filter(10);
    private double threshold = 10;
    
    private Logger logger = Logger.getLogger(InbandDetector.class);

    public InbandDetector() {
        super();
        //Properties props = new Properties();
        try {
//            props.load(getClass().getResourceAsStream("dtmf.properties"));
//            int t = Integer.parseInt(props.getProperty("dtmf.threshold"));
            filter = new Filter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void configure(Properties config) {
        setState(MediaResourceState.CONFIGURED);
    }

    public void prepare(PushBufferStream stream)
            throws UnsupportedFormatException {
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

    public void start() {
        setState(MediaResourceState.STARTED);
    }

    public void stop() {
        setState(MediaResourceState.PREPARED);
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

        int len = Math.min(8000/*16000*/ - offset, data.length);
        System.arraycopy(data, 0, localBuffer, offset, len);
        offset += len;

        if (logger.isDebugEnabled()) {
            logger.debug("append " + len + " bytes to the local buffer, buff size=" + offset);
        }

        // buffer full?
        if (offset == 8000/*16000*/) {
            double[] signal = new double[4000/*8000*/];
            int k = 0;
            for (int i = 0; i < signal.length; i++) {
                signal[i] = ((localBuffer[k++] & 0xff) | (localBuffer[k++] << 8));
            }

            localBuffer = new byte[8000/*16000*/];
            offset = 0;

            double p[] = getPower(lowFreq, signal);
//            System.out.println("LOW----------------------");
//            print(p);
            
//            System.out.println("HIGH----------------------");
            double P[] = getPower(highFreq, signal);
//            print(P);
            String tone = getTone(p, P);
            if (tone != null) {
                digitBuffer.push(tone);
            }
        }
    }

    private void print(double p[]) {
        for (int i = 0; i < p.length; i++) {
            System.out.print(p[i] + " ");
        }
        System.out.println();
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
            power[i] = filter.getPower(freq[i], data);
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
    
    private String getTone(double f[], double F[]) {
        int fm = getMax(f);
//        System.out.println("fm=" + fm);
        boolean fd = true;
        
        double ft = threshold;
        for (int i = 0; i < f.length; i++) {
            if (fm == i) continue;
            double r = f[fm]/(f[i] + 1E-15);
//            System.out.println("r=" + r);
            if (r < threshold) {
                fd = false;
                break;
            } else if (r > ft) {
                ft = r;
            }
        }
        
        ft = threshold + (ft - threshold)/2;
//        System.out.println("++++ ft=" + ft);
        
        if (!fd) return null;

        int Fm = getMax(F);
//        System.out.println("Fm=" + Fm);
        boolean Fd = true;
        
        for (int i = 0; i < F.length; i++) {
            if (Fm == i) continue;
            double r = F[Fm]/(F[i] + 1E-15);
//            System.out.println("R=" + r);
            if (r < threshold) {
                Fd = false;
                break;
            }
        }
        
        if (!Fd) return null;
        
        return events[fm][Fm];
    }
    
    private int checkFreq(int[] freq, double[] data) {
        for (int i = 0; i < freq.length; i++) {
            if (logger.isDebugEnabled()) {
                logger.debug("Checking freq: " + freq[i]);
            }
            if (filter.detect(freq[i], data)) {
                return i;
            }
        }
        return -1;
    }

    public void release() {
        localBuffer = null;
        setState(MediaResourceState.NULL);
    }
}
