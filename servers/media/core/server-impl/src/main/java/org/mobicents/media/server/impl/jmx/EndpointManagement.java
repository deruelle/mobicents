/*
 * EndpointManagement.java
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.java.stun4j.StunAddress;
import net.java.stun4j.client.NetworkConfigurationDiscoveryProcess;
import net.java.stun4j.client.StunDiscoveryReport;

import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.sdp.AVProfile;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class EndpointManagement extends ServiceMBeanSupport
        implements EndpointManagementMBean {
    
    private Endpoint endpoint;
    
    private String jndiName;
    private String bindAddress;
    private Integer packetizationPeriod;
    private Integer jitter;
    private String portRange;
    
    private boolean enablePCMA = false;
    private boolean enablePCMU = false;
    private boolean enableSpeex = false;
    
    private String stunServerAddress;
	private int stunServerPort;
	private boolean useStun = false;
	private boolean usePortMapping = true;
	private String publicAddressFromStun = null;
    
    private Properties dtmfConfig;
    private transient Logger logger = Logger.getLogger(EndpointManagement.class);
    
    /**
     * Creates a new instance of EndpointManagement
     */
    public EndpointManagement() {
    }
    
    public Endpoint getEndpoint() {
        return endpoint;
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
        String oldName = this.jndiName;
        this.jndiName = jndiName;
        
        if (this.getState() == STARTED) {
            unbind(oldName);
            try {
                rebind();
            } catch (NamingException e) {
                NamingException ne = new NamingException("Failed to update JNDI name");
                ne.setRootCause(e);
                throw ne;
            }
        }
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
        if (this.getState() == STARTED) {
            InetAddress address = InetAddress.getByName(bindAddress);
            this.getEndpoint().setBindAddress(address);
        }
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
        if (this.getState() == STARTED) {
            this.getEndpoint().setJitter(jitter);
        }
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
        if (this.getState() == STARTED) {
            this.getEndpoint().setPacketizationPeriod(period);
        }
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
        if (this.getState() == STARTED) {
            this.getEndpoint().setPortRange(portRange);
        }
    }
    
    public void setPCMA(Boolean enabled) {
        this.enablePCMA = enabled;
        if (this.getState() == STARTED) {
            if (enabled) {
                endpoint.addFormat(AVProfile.getPayload(AVProfile.PCMA), AVProfile.PCMA);
            } else {
                endpoint.removeFormat(AVProfile.PCMA);
            }
        }
    }
    
    public Boolean getPCMA() {
        return this.enablePCMA;
    }

    public void setPCMU(Boolean enabled) {
        this.enablePCMU = enabled;
        if (this.getState() == STARTED) {
            if (enabled) {
                endpoint.addFormat(AVProfile.getPayload(AVProfile.PCMU), AVProfile.PCMU);
            } else {
                endpoint.removeFormat(AVProfile.PCMU);
            }
        }
    }
    
    public Boolean getPCMU() {
        return this.enablePCMU;
    }
    
    public void setSpeex(Boolean enabled){
    	this.enableSpeex = enabled;
        if (this.getState() == STARTED) {
            if (enabled) {
                endpoint.addFormat(AVProfile.getPayload(AVProfile.SPEEX_NB), AVProfile.SPEEX_NB);
            } else {
                endpoint.removeFormat(AVProfile.SPEEX_NB);
            }
        }    	
    }
    
    public Boolean getSpeex(){
    	return this.enableSpeex;
    }
    
    /**
     * DTMF detector configuration 
     * 
     * @param config configuration.
     */
    public void setDTMF(Properties config) {
        this.dtmfConfig = config;
        if (this.getState() == STARTED) {
            getEndpoint().setDefaultConfig(MediaResourceType.DTMF_DETECTOR, config);
        }
    }
    
    /**
     * Gets the current DTMF detector configuration.
     * 
     * @return current configuration.
     */
    public Properties getDTMF() {
        return dtmfConfig;
    }
    
    public String getStunServerAddress() {
		return stunServerAddress;
	}

	public void setStunServerAddress(String stunServerAddress) {
		this.stunServerAddress = stunServerAddress;
        if (this.getState() == STARTED) {
            this.getEndpoint().setStunServerAddress(stunServerAddress);
        }
	}

	public int getStunServerPort() {
		return stunServerPort;
	}

	public void setStunServerPort(int stunServerPort) {
		this.stunServerPort = stunServerPort;
        if (this.getState() == STARTED) {
            this.getEndpoint().setStunServerPort(stunServerPort);
        }
	}
	
	public boolean isUseStun() {
		return useStun;
	}

	public void setUseStun(boolean useStun) {
		this.useStun = useStun;
        if (this.getState() == STARTED) {
            this.getEndpoint().setUseStun(useStun);
        }
	}
    
    /**
     * Binds trunk object to the JNDI under the jndiName.
     */
    private void rebind() throws NamingException {
        Context ctx = new InitialContext();
        String tokens[] = jndiName.split("/");
        
        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].trim().length() > 0) {
                try {
                    ctx = (Context)ctx.lookup(tokens[i]);
                } catch (NamingException  e) {
                    ctx = ctx.createSubcontext(tokens[i]);
                }
            }
        }
        
        ctx.bind(tokens[tokens.length - 1], endpoint);
    }
    
    /**
     * Unbounds object under specified name.
     *
     * @param jndiName the JNDI name of the object to be unbound.
     */
    private void unbind(String jndiName) {
        try {
            InitialContext initialContext = new InitialContext();
            initialContext.unbind(jndiName);
        } catch (NamingException e) {
            logger.error("Failed to unbind endpoint", e);
        }
    }
    
    public abstract Endpoint createEndpoint() throws Exception;
    
    public abstract EndpointManagementMBean cloneEndpointManagementMBean();
    
    private void mapStun(int localPort, String localAddress) {
        try {
            if (InetAddress.getByName(localAddress).isLoopbackAddress()) {
                logger.warn("The Ip address provided is the loopback address, stun won't be enabled for it");
                this.publicAddressFromStun = localAddress;
            } else {
                StunAddress localStunAddress = new StunAddress(localAddress,
                        localPort);

                StunAddress serverStunAddress = new StunAddress(
                        stunServerAddress, stunServerPort);

                NetworkConfigurationDiscoveryProcess addressDiscovery = new NetworkConfigurationDiscoveryProcess(
                        localStunAddress, serverStunAddress);
                addressDiscovery.start();
                StunDiscoveryReport report = addressDiscovery.determineAddress();
                if (report.getPublicAddress() != null) {
                    this.publicAddressFromStun = report.getPublicAddress().getSocketAddress().getAddress().getHostAddress();
                //TODO set a timer to retry the binding and provide a callback to update the global ip address and port
                } else {
                    useStun = false;
                    logger.error("Stun discovery failed to find a valid public ip address, disabling stun !");
                }
                logger.info("Stun report = " + report);
                addressDiscovery.shutDown();
            }
        } catch (Throwable t) {
            logger.error("Stun lookup has failed: " + t.getMessage());
        }

    }

    /**
     * Starts MBean.
     */
    @Override
    public void startService() throws Exception {
        this.endpoint = createEndpoint();
        
        InetAddress address = InetAddress.getByName(bindAddress);
        
        this.getEndpoint().setBindAddress(address);
        this.getEndpoint().setPortRange(portRange);
        this.getEndpoint().setPacketizationPeriod(packetizationPeriod);
        this.getEndpoint().setJitter(jitter);
        
        if(!this.usePortMapping && this.useStun) {
        	int startPort = 31000;
        	while(this.publicAddressFromStun == null) {
        		mapStun(startPort++, bindAddress);
        	}
        }
        
        if (this.enablePCMA) {
            endpoint.addFormat(AVProfile.getPayload(AVProfile.PCMA), AVProfile.PCMA);
        }
        
        if (this.enablePCMU) {
            endpoint.addFormat(AVProfile.getPayload(AVProfile.PCMU), AVProfile.PCMU);
        }
        
        if(this.enableSpeex){
        	endpoint.addFormat(AVProfile.getPayload(AVProfile.SPEEX_NB), AVProfile.SPEEX_NB);
        }
        
        this.getEndpoint().setDefaultConfig(MediaResourceType.DTMF_DETECTOR, dtmfConfig);
        
        rebind();
        logger.info("Started Endpoint MBean " + this.getJndiName());
    }
    
    public boolean isUsePortMapping() {
		return usePortMapping;
	}

	public void setUsePortMapping(boolean usePortMapping) {
		this.usePortMapping = usePortMapping;
        if (this.getState() == STARTED) {
            this.getEndpoint().setUsePortMapping(usePortMapping);
        }
	}

	public String getPublicAddressFromStun() {
		return publicAddressFromStun;
	}

	public void setPublicAddressFromStun(String publicAddressFromStun) {
		this.publicAddressFromStun = publicAddressFromStun;
        if (this.getState() == STARTED) {
            this.getEndpoint().setPublicAddressFromStun(publicAddressFromStun);
        }
	}

	/**
     * Stops MBean.
     */
    @Override
    public void stopService() {
        unbind(jndiName);
        logger.info("Stopped Endpoint MBean " + this.getJndiName());
    }
    
}
