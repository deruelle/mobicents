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
package org.mobicents.media.server.impl.conference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.jmf.mixer.AudioMixer;

/**
 *
 * @author Oleg Kulikov
 */
public class LocalMixer {

    private String id;
    private Map streams = Collections.synchronizedMap(new HashMap());
    private AudioMixer mixer;
    private Logger logger = Logger.getLogger(LocalMixer.class);

    public LocalMixer(String id, AudioFormat fmt,
            int packetizationPeriod, int jitter) throws UnsupportedFormatException {
        this.id = id;
        mixer = new AudioMixer(packetizationPeriod, jitter, fmt);
    }

    public void add(String id, PushBufferStream pushStream) throws UnsupportedFormatException {
        streams.put(id, pushStream);
        mixer.addInputStream(pushStream);
        if (logger.isDebugEnabled()) {
            logger.debug("id=" + this.id + ", add stream from connection id=" + id +
                    ", total streams=" + mixer.size());
        }
    }

    public PushBufferStream remove(String id) {
        PushBufferStream pushStream = (PushBufferStream) streams.remove(id);
        if (pushStream != null) {
            mixer.removeInputStream(pushStream);
            if (logger.isDebugEnabled()) {
                logger.debug("id=" + this.id + ", removed stream of connection id=" + id +
                        ", total streams=" + mixer.size());
            }
        }
        return pushStream;
    }

    public PushBufferStream getOutputStream() {
        return mixer.getOutputStream();
    }

    public void start() {
        mixer.start();
    }

    public void stop() {
        if (logger.isDebugEnabled()) {
            logger.debug("id=" + this.id + " stop mixer");
        }
        mixer.stop();
    }

    @Override
    public String toString() {
        return "LocalMixer[" + id + "]";
    }
}
