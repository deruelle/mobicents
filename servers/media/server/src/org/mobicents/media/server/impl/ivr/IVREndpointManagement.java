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

package org.mobicents.media.server.impl.ivr;

import org.mobicents.media.server.impl.ann.AnnEndpointManagement;
import org.mobicents.media.server.spi.Endpoint;

/**
 * Provides implementation of the IVR endpoint MBean.
 *
 * @author Oleg Kulikov
 */
public class IVREndpointManagement extends AnnEndpointManagement 
    implements IVREndpointManagementMBean {

    private String recordDir;
    private String mediaType;
    
    /** Creates a new instance of IVREndpointManagement */
    public IVREndpointManagement() {
    }
    
    public String getRecordDir() {
        return recordDir;
    }
    
    public void setRecordDir(String recordDir) {
        this.recordDir = recordDir;
        if (this.getState() == STARTED) {
            ((IVREndpointImpl)getEndpoint()).setRecordDir(recordDir);
        }
    }

    public String getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
        if (this.getState() == STARTED) {
            ((IVREndpointImpl)getEndpoint()).setMediaType(mediaType);
        }
    }
    
    @Override
    public Endpoint createEndpoint() throws Exception {
        IVREndpointImpl endpoint = new IVREndpointImpl(getJndiName());
        return endpoint;
    }
    
}
