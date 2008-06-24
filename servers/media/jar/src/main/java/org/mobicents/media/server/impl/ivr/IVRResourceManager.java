/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.ivr;

import java.util.Properties;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.ann.AnnResourceManager;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.dtmf.DTMFResourceLocator;
import org.mobicents.media.server.impl.fft.SpectralAnalyser;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.spi.UnknownMediaResourceException;

/**
 *
 * @author Oleg Kulikov
 */
public class IVRResourceManager extends AnnResourceManager {
    @Override
    public MediaResource getResource(BaseEndpoint endpoint, MediaResourceType type, 
            Connection connection, Properties config) throws UnknownMediaResourceException {
        if (type==type.DTMF_DETECTOR) {
            return DTMFResourceLocator.getDetector(config);
        } else if (type==type.AUDIO_SINK) {
            return new LocalSplitter(endpoint, connection);
        } else if (type == type.SPECTRUM_ANALYSER) {
            return new SpectralAnalyser();
        } else return super.getResource(endpoint, type, connection, config);
    }
}
