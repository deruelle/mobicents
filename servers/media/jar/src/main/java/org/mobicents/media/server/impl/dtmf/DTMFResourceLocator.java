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

import java.util.Properties;
import org.mobicents.media.server.impl.common.dtmf.DTMFType;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.spi.UnknownMediaResourceException;

/**
 * Intializes DTMF resources.
 * 
 * @author Oleg Kulikov
 */
public class DTMFResourceLocator {
    /**
     * Constructs DTMF detector using specified config.
     * 
     * Config has the following meanings:
     * <p>
     * detector.type - the type of DTMF detector. Possible values are: 
     * RFC2833, AUTO, INBAND.
     * 
     * detector.threshold - the value greater then 10 which determone the 
     * sensitivity of the detector. Valid only for INBAND detector type.
     * 
     * dtmf.payload the number of RTP payload
     * interdigits.interval - the minimum value in milliseconds between two digits.
     * </p>
     * 
     * @param config the config used for DTMF detector creation;
     * @return DTMF detector instance
     * @throws org.mobicents.media.server.spi.UnknownMediaResourceException
     */
    public synchronized static MediaResource getDetector(Properties config) throws UnknownMediaResourceException {        
        if (config == null) {
            return new Rfc2833Detector();
        }
        
        MediaResource detector = null;
        
        DTMFType type = DTMFType.valueOf(config.getProperty("detector.mode"));
        if (type == DTMFType.INBAND) {
            detector = new InbandDetector();
        } else {
            detector = new Rfc2833Detector();
        }
        
        detector.configure(config);
        return detector;
    }
}
