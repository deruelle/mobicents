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
import java.util.Timer;
import java.util.TimerTask;
import javax.media.Buffer;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.PushBufferStream;
import org.mobicents.media.goertzel.Filter;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;
import org.mobicents.media.server.spi.dtmf.DtmfDetector;
import org.mobicents.media.server.spi.dtmf.DtmfListener;

/**
 *
 * @author Oleg Kulikov
 */
public class InbandDetector implements DtmfDetector, BufferTransferHandler {

    public final static Timer timer = new Timer();
    public final static String[][] events = new String[][] {
        {"1", "2", "3", "A"},
        {"4", "5", "6", "B"},
        {"7", "8", "9", "C"},
        {"*", "0", "#", "D"}
    };
    
    private int[] lowFreq = new int[]{697, 770, 852, 941};
    private int[] highFreq = new int[]{1209, 1336, 1477, 1633};
    
    private String mask= "%d";
    
    private StringBuffer digitBuffer = new StringBuffer();
    private List <DtmfListener> listeners = new ArrayList();
    private Codec codec;
    private byte[] localBuffer = new byte[8000];
    private int offset = 0;
    private Filter filter = new Filter(100);
    private TimerTask cleanTask;
    
    public InbandDetector(PushBufferStream stream) throws UnsupportedFormatException {
        stream.setTransferHandler(this);
        if (!stream.getFormat().matches(Codec.LINEAR_AUDIO)) {
            codec = CodecLocator.getCodec(stream.getFormat(), Codec.LINEAR_AUDIO);
            if (codec == null) {
                throw new UnsupportedFormatException(stream.getFormat());
            }
        }
    }
        
    public void setDtmfMask(String mask) {
        this.mask = mask;
    }

    public void addListener(DtmfListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DtmfListener listener) {
        listeners.remove(listener);
    }

    public void transferData(PushBufferStream stream) {
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
        
        //buffer full?
        if (offset == 8000) {
            double[] signal = new double[4000];
            int k = 0;
            for (int i = 0; i < 4000; i++) {
                signal[i] = (data[k++] << 8) | (data[k++] & 0xff);
            }
            
            int f1 = checkFreq(lowFreq, signal);
            if (f1 == -1) {
                return;
            }
            
            int f2 = checkFreq(highFreq, signal);
            if (f2 == -1) {
                return;
            }
            
            if (cleanTask != null) {
                cleanTask.cancel();
            }
            
            digitBuffer.append(events[f1][f2]);
            String digits = digitBuffer.toString();
            
            if (digits.matches(mask)) {
                sendEvent(digits);
                digitBuffer = new StringBuffer();
                return;
            } 
            
            cleanTask = new CleanTask();
            timer.schedule(cleanTask, 5000);
        }
    }

    private int checkFreq(int[] freq, double[] data) {
        for (int i = 0; i < freq.length; i++) {
            if (filter.detect(freq[i], data)) {
                return i;
            }
        }
        return -1;
    }
    
    private void sendEvent(String digits) {
        for (DtmfListener listener: listeners) {
            listener.onDTMF(digits);
        }
    }
    
    private class CleanTask extends TimerTask {
        public void run() {
            digitBuffer = new StringBuffer();
        }
    }
}
