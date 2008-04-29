/*
 * EndpointManagementMBean.java
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

package org.mobicents.media.server.impl.jmx;

import java.net.UnknownHostException;
import java.util.Properties;
import javax.naming.NamingException;
import org.jboss.system.ServiceMBean;

/**
 *
 * @author Oleg Kulikov
 */
public interface EndpointManagementMBean extends ServiceMBean {
    /**
     * Gets the JNDI name of the endpoint.
     *
     * @return the JNDI name string.
     */
    public String getJndiName();
    
    /**
     * Modify the JNDI name of the endpoint.
     *
     * @param jndiName the new value of the Jndi name.
     */
    public void setJndiName(String jndiName) throws NamingException;   
    /**
     * Gets the IP address to which trunk is bound.
     * All endpoints of the trunk use this address for RTP connection.
     *
     * @return the IP address string to which this trunk is bound.
     */
    public String getBindAddress();
    
    /**
     * Modify the bind address.
     * All endpoints of the trunk use this address for RTP connection.
     *
     * @param the IP address string.
     */
    public void setBindAddress(String bindAddress) throws UnknownHostException;
    
   /**
    * Gets packetization period.
    * 
    * The packetization period is the period over which encoded voice bits 
    * are collected for encapsulation in packets.
    * 
    * Higher period will reduce VoIP overhead allowing higher channel utilization
    * 
    * @return packetion period for media in milliseconds.
    */
    public Integer getPacketizationPeriod();
    
    /**
     * Modify packetization period.
     * 
     * The packetization period is the period over which encoded voice bits 
     * are collected for encapsulation in packets.
     * 
     * Higher period will reduce VoIP overhead allowing higher channel utilization
     * 
     * @param period the value of the packetization period in milliseconds.
     */
    public void setPacketizationPeriod(Integer period);
    
    /**
     * Gets the size of the jitter buffer in milliseconds.
     * 
     * Jitter buffer is used at the receiving ends of a VoIP connection. 
     * A jitter buffer stores received, time-jittered VoIP packets, that arrive 
     * within its time window. It then plays stored packets out, in sequence, 
     * and at a constant rate for subsequent decoding. A jitter buffer is
     * typically filled half-way before playing out packets to allow early, 
     * or late, packet-arrival jitter compensation.
     * 
     * Choosing a large jitter buffer reduces packet dropping from jitter but
     * increases VoIP path delay
     * 
     * @return the size of the buffer in milliseconds.
     */
    public Integer getJitter();
    
    /**
     * Modify size of the jitter buffer.
     * 
     * Jitter buffer is used at the receiving ends of a VoIP connection. 
     * A jitter buffer stores received, time-jittered VoIP packets, that arrive 
     * within its time window. It then plays stored packets out, in sequence, 
     * and at a constant rate for subsequent decoding. A jitter buffer is
     * typically filled half-way before playing out packets to allow early, 
     * or late, packet-arrival jitter compensation.
     * 
     * Choosing a large jitter buffer reduces packet dropping from jitter but
     * increases VoIP path delay
     * 
     * @param jitter the new buffer's size in milliseconds
     */
    public void setJitter(Integer jitter);
    
    /**
     * Gets the range of the available port used for RTP connections.
     * 
     * @return the range of available port in the follwing format: low-high
     */
    public String getPortRange();
    
    /**
     * Sets the range of the available port used for RTP connections.
     * 
     * @param portRange string indicating range in the follwing format: low-high
     */
    public void setPortRange(String portRange);  
    
    public void setPCMA(Boolean enabled);
    public Boolean getPCMA();

    public void setPCMU(Boolean enabled);
    public Boolean getPCMU();
    
    /**
     * DTMF detector configuration 
     * 
     * @param config configuration.
     */
    public void setDTMF(Properties config);
    
    /**
     * Gets the current DTMF detector configuration.
     * 
     * @return current configuration.
     */
    public Properties getDTMF();
}
