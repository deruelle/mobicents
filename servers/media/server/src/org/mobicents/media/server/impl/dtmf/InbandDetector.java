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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.media.Buffer;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;
import org.mobicents.media.goertzel.Filter;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;
import org.mobicents.media.server.spi.NotificationListener;

/**
 *
 * @author Oleg Kulikov
 */
public class InbandDetector extends BaseDtmfDetector implements BufferTransferHandler {

    public final static String[][] events = new String[][] {
        {"1", "2", "3", "A"},
        {"4", "5", "6", "B"},
        {"7", "8", "9", "C"},
        {"*", "0", "#", "D"}
    };
    
    private int[] lowFreq = new int[]{697, 770, 852, 941};
    private int[] highFreq = new int[]{1209, 1336, 1477, 1633};
    
    
    private List <NotificationListener> listeners = new ArrayList();
    private Codec codec;
    private byte[] localBuffer = new byte[8000];
    private int offset = 0;
    private Filter filter = new Filter(500000);
    private boolean started = false;
    
    private Logger logger = Logger.getLogger(InbandDetector.class);
    
    public InbandDetector() {
        super();
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/dtmf.properties"));
            int t = Integer.parseInt(props.getProperty("dtmf.threshold"));
            filter = new Filter(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    public void prepare(PushBufferStream stream) throws UnsupportedFormatException {
        stream.setTransferHandler(this);
        if (!stream.getFormat().matches(Codec.LINEAR_AUDIO)) {
            codec = CodecLocator.getCodec(stream.getFormat(), Codec.LINEAR_AUDIO);
            if (codec == null) {
                throw new UnsupportedFormatException(stream.getFormat());
            }
        }
    }

    public void start() {
        this.started = true;
    }
    
    public void stop() {
        this.started = false;
    }
    

    public void transferData(PushBufferStream stream) {
        if (!started) {
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
        
        int len = Math.min(8000 - offset, data.length);
        System.arraycopy(data, 0, localBuffer, offset, len);
        offset += len;
        
        if (logger.isDebugEnabled()) {
            logger.debug("append " + len + " bytes to the local buffer, buff size=" + offset);
        }
        
        //buffer full?
        if (offset == 8000) {
            double[] signal = new double[4000];
            int k = 0;
            for (int i = 0; i < 4000; i++) {
                signal[i] = (localBuffer[k++] << 8) | (localBuffer[k++] & 0xff);
            }
            
            localBuffer = new byte[8000];
            offset = 0;
            
            logger.debug("Checking low frequencies");
            int f1 = checkFreq(lowFreq, signal);
            if (f1 == -1) {
                logger.debug("No low frequencies were found, break");
                return;
            }
            
            logger.debug("Found frequency " + lowFreq[f1] + " Checking hight frequencies");
            int f2 = checkFreq(highFreq, signal);
            if (f2 == -1) {
                logger.debug("No high frequencies were found, break");
                return;
            }
            
            logger.debug("Found frequency " + highFreq[f1]  + ", evt=" + events[f1][f2]);
            
            digitBuffer.push(events[f1][f2]);
        }
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
    
}
