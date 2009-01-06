/*
 * IVREndpointManagementMBean.java
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

import org.mobicents.media.server.impl.jmx.enp.ann.AnnEndpointManagementMBean;

/**
 * MBean that gives support IVREndpoint. 
 * Object of class org.mobicents.server.spi.ivr.IVREndpoint will be bound
 * in JNDI with the name provided with method {@link #setJndiName}.
 *
 * @author Oleg Kulikov
 */
public interface IVREndpointManagementMBean extends AnnEndpointManagementMBean {
    public String getRecordDir();
    public void setRecordDir(String recordDir);
    
    public String getMediaType();
    public void setMediaType(String mediaType);
    
    public String getDtmfMode();
    public void setDtmfMode(String mode);
}
