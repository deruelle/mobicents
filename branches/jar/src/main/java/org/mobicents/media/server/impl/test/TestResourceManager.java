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

package org.mobicents.media.server.impl.test;

import org.mobicents.media.server.impl.ann.*;
import org.mobicents.media.server.impl.common.MediaResourceType;

import java.util.Properties;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.BaseResourceManager;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.spi.UnknownMediaResourceException;

/**
 *
 * @author Oleg Kulikov
 */
public class TestResourceManager extends BaseResourceManager {
    private Echo echo;
    
    @Override
    public synchronized MediaResource getResource(BaseEndpoint endpoint,MediaResourceType type, 
            Connection connection, Properties config) throws UnknownMediaResourceException {
        if (type==type.AUDIO_SOURCE) {
            if (echo == null) {
                echo = new Echo(endpoint, connection);
            }
            return echo;
        } else if (type==type.AUDIO_SINK) {
            if (echo == null) {
                echo = new Echo(endpoint, connection);
            }
            return echo;
        } else return super.getResource(endpoint, type, connection, config);
    }

}
