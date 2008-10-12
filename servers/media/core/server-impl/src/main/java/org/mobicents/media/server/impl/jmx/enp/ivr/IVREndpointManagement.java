/*
 * IVREndpointManagement.java
 *
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
package org.mobicents.media.server.impl.jmx.enp.ivr;

import java.util.HashMap;

import org.mobicents.media.server.impl.enp.ivr.*;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.jmx.enp.ann.AnnEndpointManagement;
import org.mobicents.media.server.impl.jmx.EndpointManagementMBean;
import org.mobicents.media.server.spi.Endpoint;

/**
 * Provides implementation of the IVR endpoint MBean.
 *
 * @author Oleg Kulikov
 */
public class IVREndpointManagement extends AnnEndpointManagement
        implements IVREndpointManagementMBean {

    private Logger logger = Logger.getLogger(IVREndpointManagement.class);
    private String recordDir;
    private String mediaType;
    private HashMap<String, Endpoint> endpointsMap= new HashMap<String, Endpoint>();
    /** Creates a new instance of IVREndpointManagement */
    public IVREndpointManagement() {
    }

    public String getRecordDir() {
        return recordDir;
    }

    public void setRecordDir(String recordDir) {
        this.recordDir = recordDir;

        if (this.getState() == STARTED) {
            ((IVREndpointImpl) getEndpoint()).setRecordDir(recordDir);
        }
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
        if (this.getState() == STARTED) {
            ((IVREndpointImpl) getEndpoint()).setMediaType(mediaType);
        }
    }

    @Override
    public Endpoint createEndpoint() throws Exception {
        IVREndpointImpl endpoint = new IVREndpointImpl(getJndiName(),endpointsMap);
        return endpoint;
    }

    @Override
    public EndpointManagementMBean cloneEndpointManagementMBean() {
        IVREndpointManagement clone = new IVREndpointManagement();
        try {
            clone.setJndiName(this.getJndiName());
            clone.setRecordDir(this.getRecordDir());
            clone.setMediaType(this.getMediaType());
        } catch (Exception ex) {
            logger.error("IVREndpointManagement clonning failed ", ex);
            return null;
        }
        return clone;
    }

    /**
     * Starts MBean.
     */
    @Override
    public void startService() throws Exception {
        super.startService();
        ((IVREndpointImpl) getEndpoint()).setRecordDir(recordDir);
        ((IVREndpointImpl) getEndpoint()).setMediaType(mediaType);
    }
}
