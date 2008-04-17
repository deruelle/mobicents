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
package org.mobicents.media.server.impl.conference;

import java.util.Properties;

import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.jmx.EndpointManagement;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.UnknownMediaResourceException;

/**
 *
 * @author Oleg Kulikov
 */
public class ConfEndpointManagement extends EndpointManagement
        implements ConfEndpointManagementMBean {

    private Properties dtmfConfig;

    @Override
    public Endpoint createEndpoint() throws Exception {
        return new ConfEndpointImpl(this.getJndiName());
    }

    public void setDTMF(Properties config) throws UnknownMediaResourceException {
        this.dtmfConfig = config;
        if (this.getState() == STARTED) {
            getEndpoint().configure(MediaResourceType.DTMF_DETECTOR, config);
        }
    }

    public Properties getDTMF() {
        return dtmfConfig;
    }
}
