/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.ivr;

import java.util.Properties;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.ann.AnnResourceManager;
import org.mobicents.media.server.impl.dtmf.DTMFResourceLocator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.spi.UnknownMediaResourceException;

/**
 *
 * @author Oleg Kulikov
 */
public class IVRResourceManager extends AnnResourceManager {
    @Override
    public MediaResource getResource(BaseEndpoint endpoint, String name, 
            Connection connection, Properties config) throws UnknownMediaResourceException {
        if (name.equals(Endpoint.RESOURCE_DTMF_DETECTOR)) {
            return DTMFResourceLocator.getDetector(endpoint, connection, config);
        } else if (name.equals(Endpoint.RESOURCE_AUDIO_SINK)) {
            return new LocalSplitter(endpoint, connection);
        } else return super.getResource(endpoint, name, connection, config);
    }
}
