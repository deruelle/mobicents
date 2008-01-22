/*
 * TrunkManagement.java
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
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.system.ServiceMBeanSupport;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class TrunkManagement extends ServiceMBeanSupport
        implements TrunkManagementMBean {
    
    private Integer channels;
    private String jndiName;
    private String bindAddress;
    private int packetizationPeriod;
    private int jitter;
    private String portRange;
    
    private transient Logger logger = Logger.getLogger(TrunkManagement.class);
    
    /**
     * Creates a new instance of EndpointManagement
     */
    public TrunkManagement() {
    }
    
    /**
     * Gets the JNDI name to which this trunk instance is bound.
     *
     * @return the JNDI name.
     */
    public String getJndiName() {
        return jndiName;
    }
    
    /**
     * Sets the JNDI name to which this trunk object should be bound.
     *
     * @param jndiName the JNDI name to which trunk object will be bound.
     */
    public void setJndiName(String jndiName) throws NamingException {
        this.jndiName = jndiName;
    }
    
    /**
     * Gets the IP address to which trunk is bound.
     * All endpoints of the trunk use this address for RTP connection.
     *
     * @return the IP address string to which this trunk is bound.
     */
    public String getBindAddress() {
        return this.bindAddress;
    }
    
    /**
     * Modify the bind address.
     * All endpoints of the trunk use this address for RTP connection.
     *
     * @param the IP address string.
     */
    public void setBindAddress(String bindAddress) throws UnknownHostException {
        this.bindAddress = bindAddress;
    }
    
    /**
     * Gets ammount of Endpoints.
     *
     * @return the amount of endpoints included into this trunk.
     */
    public Integer getChannels() {
        return channels;
    }
    
    /**
     * Sets the amount of endpoints included into this trunk.
     *
     * @param channels the number of endpoints.
     */
    public void setChannels(Integer channels) {
        this.channels = channels;
    }
    
    /**
     * Gets the size of the jitter value.
     * 
     * @return buffer size in milliseconds.
     */
    public Integer getJitter() {
        return jitter;
    }
    
    /**
     * Modify size of the jitter buffer.
     * 
     * @param jitter the size of the jitter buffer in milliseconds.
     */
    public void setJitter(Integer jitter) {
        this.jitter = jitter;
    }
    
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Endpoint#getPcketizationPeriod();
     */
    public Integer getPacketizationPeriod() {
        return packetizationPeriod;
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Endpoint#setPcketizationPeriod(Integer);
     */
    public void setPacketizationPeriod(Integer period) {
        packetizationPeriod = period;
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Endpoint#getPortRange();
     */
    public String getPortRange() {
        return portRange;
    }

    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.media.server.spi.Endpoint#setPortRange(String);
     */
    public void setPortRange(String portRange) {
        this.portRange = portRange;
    }
    
    public abstract EndpointManagement initEndpointManagement();
    
    /**
     * Starts MBean.
     */
    @Override
    public void startService() throws Exception {
        logger.info("Starting Trunk MBean " + this.getServiceName());
        MBeanServer mbeanServer = MBeanServerLocator.locate();
        
        for (int i = 0; i < this.getChannels(); i++) {
            //constructs names for endpoints
            String name = getServiceName().getCanonicalName() +  ", endpoint=" + i;
            String localName = this.getJndiName() + "/" + i;
            
            //constructs endpoint MBean
            EndpointManagement endpoint = this.initEndpointManagement();
            endpoint.setJndiName(localName);
            endpoint.setBindAddress(this.getBindAddress());
            endpoint.setPacketizationPeriod(this.getPacketizationPeriod());
            endpoint.setPortRange(this.getPortRange());
            endpoint.setJitter(this.getJitter());
            
            //register Endpoint as MBean
            ObjectName objectName = new ObjectName(name);
            mbeanServer.registerMBean(endpoint, objectName);
            mbeanServer.invoke(objectName, "start", new Object[]{}, new String[]{});
        }
        logger.info("Started Trunk MBean " + this.getServiceName());
    }
    
    /**
     * Stops MBean.
     */
    @Override
    public void stopService() {
        logger.info("Stopping Trunk MBean " + this.getServiceName());
        MBeanServer mbeanServer = MBeanServerLocator.locate();
        
        for (int i = 0; i < this.getChannels(); i++) {
            String name = getServiceName().getCanonicalName() +  ", endpoint=" + i;
            try {
                ObjectName objectName = new ObjectName(name);
                mbeanServer.invoke(objectName, "stop", new Object[]{}, new String[]{});
                mbeanServer.unregisterMBean(objectName);
            } catch (Exception e) {
                logger.error("Could not unregister endpoint", e);
            }
        }
        
        logger.info("Stopped Trunk BMean " + this.getServiceName());
    }
    
}
