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

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.common.events.EventCause;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.EventDetector;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * Implements common fatures for DTMF detector.
 * 
 * @author Oleg Kulikov
 */
public class BaseDtmfDetector extends AbstractSink implements EventDetector {

    private final static String[] TONE = new String[]{
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#"
    };
    private final static AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR, 8000, 8, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");
    private final static Format[] FORMATS = new Format[]{
        LINEAR, DTMF
    };
    
    private InbandDetector inband;
    private Rfc2833Detector rfc2833;
    protected DtmfBuffer digitBuffer;
    private List<NotificationListener> listeners = new ArrayList();
    private Logger logger = Logger.getLogger(BaseDtmfDetector.class);

    public BaseDtmfDetector() {
        digitBuffer = new DtmfBuffer(this);
        inband = new InbandDetector(this);
        rfc2833 = new Rfc2833Detector(this);
    }

    @Override
    public void connect(MediaSource source) {
        inband.connect(source);
        rfc2833.connect(source);
    }

    @Override
    public void disconnect(MediaSource source) {
        inband.disconnect(source);
        rfc2833.disconnect(source);
    }
    
    public void setDtmfMask(String mask) {
        digitBuffer.setMask(mask);
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

    protected void sendEvent(String seq) {
        NotifyEvent evt = new NotifyEvent(this, "org.mobicents.media.dtmf.DTMF", getCause(seq), seq);
        synchronized (listeners) {
            for (NotificationListener listener : listeners) {
                listener.update(evt);
            }

            listeners.clear();
        }
        this.stop();
    }

    private void stop() {
        
    }
    
    private EventCause getCause(String seq) {
        if (seq.equals("0")) {
            return EventCause.DTMF_DIGIT_0;
        } else if (seq.equals("1")) {
            return EventCause.DTMF_DIGIT_1;
        } else if (seq.equals("2")) {
            return EventCause.DTMF_DIGIT_2;
        } else if (seq.equals("3")) {
            return EventCause.DTMF_DIGIT_3;
        } else if (seq.equals("4")) {
            return EventCause.DTMF_DIGIT_4;
        } else if (seq.equals("5")) {
            return EventCause.DTMF_DIGIT_5;
        } else if (seq.equals("6")) {
            return EventCause.DTMF_DIGIT_6;
        } else if (seq.equals("7")) {
            return EventCause.DTMF_DIGIT_7;
        } else if (seq.equals("8")) {
            return EventCause.DTMF_DIGIT_8;
        } else if (seq.equals("9")) {
            return EventCause.DTMF_DIGIT_9;
        } else if (seq.equals("10")) {
            return EventCause.DTMF_DIGIT_STAR;
        } else if (seq.equals("11")) {
            return EventCause.DTMF_DIGIT_NUM;
        } else {
            return EventCause.DTMF_SEQ;
        }
    }

    public Format[] getFormats() {
        return FORMATS;
    }

    public boolean isAcceptable(Format format) {
        return format.matches(DTMF) || format.matches(LINEAR);
    }


    public void receive(Buffer buffer) {
    }

    public String getID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getParameter(String name) {
        return null;
    }

    public void setParameter(String name, Object value) {
    }
}
