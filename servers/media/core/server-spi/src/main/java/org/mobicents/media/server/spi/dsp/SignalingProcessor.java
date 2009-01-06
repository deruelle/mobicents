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

package org.mobicents.media.server.spi.dsp;

import java.io.Serializable;
import org.mobicents.media.Format;

/**
 * Processor is concerned with the digital processing of the media signals. 
 * 
 * @author Oleg Kulikov
 */
public interface SignalingProcessor extends Serializable {
    /**
     * Configures signaling processor.
     * 
     * @param inputFormat the format of the input signal.
     * @param outputFormat the format of the output signal.
     */
    public void configure(Format[] inputFormats, Format[] outputFormats);
    
}
