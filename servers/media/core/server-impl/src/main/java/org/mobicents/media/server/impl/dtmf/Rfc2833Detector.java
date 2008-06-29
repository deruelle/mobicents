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
import org.mobicents.media.Buffer;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.common.MediaResourceState;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author Oleg Kulikov
 */
public class Rfc2833Detector extends BaseDtmfDetector implements BufferTransferHandler {

    private final static String[] DTMF = new String[]{
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#"
    };
    private Logger logger = Logger.getLogger(Rfc2833Detector.class);

    public Rfc2833Detector() {
        super();
    }

    public void configure(Properties config) {
        setState(MediaResourceState.CONFIGURED);
    }

    public void prepare(Endpoint endpoint, PushBufferStream stream) throws UnsupportedFormatException {
        stream.setTransferHandler(this);
        logger.info("Set transfer handler");
        if (getState() == MediaResourceState.STARTING) {
            setState(MediaResourceState.PREPARED);
            logger.info("Restarting detector");
            start();
        } else {
            setState(MediaResourceState.PREPARED);
            logger.info("Detector prepared");
        }
    }

    public void start() {
        if (getState() == MediaResourceState.PREPARED) {
            logger.info("DTMF detector started");
            setState(MediaResourceState.STARTED);
        } else {
            logger.info("DTMF detector starting");
            setState(MediaResourceState.STARTING);
        }
    }

    public void stop() {
        setState(MediaResourceState.PREPARED);
        logger.debug("Detector stopped");
    }

    public void transferData(PushBufferStream stream) {
        Buffer buffer = new Buffer();
        try {
            stream.read(buffer);
        } catch (IOException e) {
        }

        logger.info("DTMF packet");
        if (getState() != MediaResourceState.STARTED) {
            return;
        }
        
        byte[] data = (byte[]) buffer.getData();

        String digit = DTMF[data[0]];
        boolean end = (data[1] & 0x7f) != 0;

        if (logger.isDebugEnabled()) {
            logger.debug("Arrive packet, digit=" + digit + ", end=" + end);
        }

        digitBuffer.push(digit);
    }

    public void release() {
        setState(MediaResourceState.NULL);
    }

    public PushBufferStream newBranch(String branchID) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void remove(String branchID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
