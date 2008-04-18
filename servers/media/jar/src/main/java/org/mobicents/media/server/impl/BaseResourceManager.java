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

package org.mobicents.media.server.impl;

import java.io.Serializable;
import java.util.Properties;

import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.dtmf.DTMFResourceLocator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.spi.UnknownMediaResourceException;

/**
 * Creates and configures media resources.
 * 
 * @author Oleg Kulikov
 */
public class BaseResourceManager implements Serializable {
    public MediaResource getResource(BaseEndpoint endpoint, MediaResourceType type, Properties config) throws UnknownMediaResourceException {
        throw new UnknownMediaResourceException(type);
    }
    public MediaResource getResource(BaseEndpoint endpoint, MediaResourceType type, Connection connection, Properties config) throws UnknownMediaResourceException {
        if (MediaResourceType.DTMF_DETECTOR==type) {
            return DTMFResourceLocator.getDetector(endpoint, connection, config);
        }
        throw new UnknownMediaResourceException(type);
    }
    
}
