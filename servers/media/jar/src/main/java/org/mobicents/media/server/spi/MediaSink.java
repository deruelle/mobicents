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

package org.mobicents.media.server.spi;

import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;

/**
 *
 * @author Oleg Kulikov
 */
public interface MediaSink extends MediaResource {
    /**
     * Prepares this media resource.
     * 
     * @param mediaStream the media stream.
     */
    public void prepare(PushBufferStream mediaStream) throws UnsupportedFormatException;

}
