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

package org.mobicents.media.server.impl.rtp;

import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;

/**
 *  interface representing a sending stream within an RTP session.
 *
 * @author Oleg Kulikov
 */
public interface SendStream {
    /**
     * Changes the output stream.
     * The new stream will be sent with same SSRC.
     *
     * @param the new output stream.
     */
    public void setStream(PushBufferStream stream)  throws UnsupportedFormatException;
    /**
     * Starts tranmitting over network.
     */
    public void start();
    
    /**
     * Temorary terminates tranmitting over network.
     */
    public void stop();
    
    /**
     * Closes this stream.
     */
    public void close();
}
