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
import javax.media.Buffer;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.PushBufferStream;
import org.mobicents.media.server.spi.dtmf.DtmfDetector;
import org.mobicents.media.server.spi.dtmf.DtmfListener;

/**
 *
 * @author Oleg Kulikov
 */
public class Rfc2833 implements DtmfDetector, BufferTransferHandler {
    
    private String mask= "%d";
    private StringBuffer digitBuffer = new StringBuffer();
    private String current;
    private boolean detecting = false ;
    private List <DtmfListener> listeners = new ArrayList();
    
    public Rfc2833(PushBufferStream stream) {
        stream.setTransferHandler(this);
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
        int event = data[0] >> 7;
        
        if (!detecting) {
            digitBuffer.append(event);
            detecting = true;
        }
        
        boolean end = (event & 0x01) == 0x01;
        if (end) {
            detecting = false;
            String digits = digitBuffer.toString();
            if (digits.matches(mask)) {
                digitBuffer = new StringBuffer();
                sendEvent(digits);
            }
        }
        
    }

    private void sendEvent(String digits) {
        for (DtmfListener listener: listeners) {
            listener.onDTMF(digits);
        }
    }
}
