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

package org.mobicents.media.server.impl.jmx.rtp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.java.stun4j.StunAddress;
import net.java.stun4j.client.NetworkConfigurationDiscoveryProcess;
import net.java.stun4j.client.StunDiscoveryReport;

import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.rtp.RtpFactory;

/**
 * 
 * @author Oleg Kulikov
 */
public class RTPManager extends ServiceMBeanSupport implements RTPManagerMBean {

	private String jndiName;

	private String bindAddress;
	private Integer packetizationPeriod;
	private Integer jitter;
	private String portRange;

	private String stunServerAddress;
	private int stunServerPort;
	private boolean useStun = false;
	private boolean usePortMapping = true;
	private String publicAddressFromStun = null;
	private HashMap<Integer, Format> audioFormats;
	
	private RtpFactory rtpFactory;
	private transient Logger logger = Logger.getLogger(RTPManager.class);

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getJndiName();
	 */
	public String getJndiName() {
		return jndiName;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#setJndiName(String);
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
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getBindAddress();
	 */
	public String getBindAddress() {
		return this.bindAddress;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#setBindAddress(String);
	 */
	public void setBindAddress(String bindAddress) throws UnknownHostException {
		this.bindAddress = bindAddress;
		if (getState() == RTPManager.STARTED) {
			rtpFactory.setBindAddress(bindAddress);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getJitter();
	 */
	public Integer getJitter() {
		return jitter;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getJitter(Integer);
	 */
	public void setJitter(Integer jitter) {
		this.jitter = jitter;
		if (getState() == RTPManager.STARTED) {
			rtpFactory.setJitter(jitter);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getPcketizationPeriod();
	 */
	public Integer getPacketizationPeriod() {
		return packetizationPeriod;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#setPcketizationPeriod(Integer);
	 */
	public void setPacketizationPeriod(Integer period) {
		packetizationPeriod = period;
		if (getState() == RTPManager.STARTED) {
			rtpFactory.setPacketizationPeriod(period);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getPortRange();
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
		if (getState() == RTPManager.STARTED) {
			rtpFactory.setPortRange(portRange);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getStunServerAddress();
	 */
	public String getStunServerAddress() {
		return stunServerAddress;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getStunServerAddress(String);
	 */
	public void setStunServerAddress(String stunServerAddress) {
		this.stunServerAddress = stunServerAddress;
		if (getState() == RTPManager.STARTED) {
			rtpFactory.setStunServerAddress(stunServerAddress);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getStunServerPort();
	 */
	public int getStunServerPort() {
		return stunServerPort;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#setStunServerPort(Integer);
	 */
	public void setStunServerPort(int stunServerPort) {
		this.stunServerPort = stunServerPort;
		if (getState() == RTPManager.STARTED) {
			rtpFactory.setStunServerPort(stunServerPort);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#isUseStun();
	 */
	public boolean isUseStun() {
		return useStun;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#setUseStun(Boolean);
	 */
	public void setUseStun(boolean useStun) {
		this.useStun = useStun;
		if (getState() == RTPManager.STARTED) {
			rtpFactory.setUseStun(useStun);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#isUsePortMapping();
	 */
	public boolean isUsePortMapping() {
		return usePortMapping;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#isUsePortMapping(Boolean);
	 */
	public void setUsePortMapping(boolean usePortMapping) {
		this.usePortMapping = usePortMapping;
		if (getState() == RTPManager.STARTED) {
			rtpFactory.setUsePortMapping(usePortMapping);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getPublicAddressFromStun();
	 */
	public String getPublicAddressFromStun() {
		return publicAddressFromStun;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#setPublicAddressFromStun(String);
	 */
	public void setPublicAddressFromStun(String publicAddressFromStun) {
		this.publicAddressFromStun = publicAddressFromStun;
		if (getState() == RTPManager.STARTED) {
			rtpFactory.setPublicAddressFromStun(publicAddressFromStun);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getAudioFormats();
	 */
	public String getAudioFormats() {
		FormatDescription fd = new FormatDescription();
		return fd.getDescription(audioFormats);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#setAudioFormats(String);
	 */
	public void setAudioFormats(String desc) {
		FormatDescription fd = new FormatDescription();
		audioFormats = fd.parse(desc);
		if (getState() == RTPManager.STARTED) {
			rtpFactory.setAudioFormats(audioFormats);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#getVideoFormats();
	 */
	public String getVideoFormats() {
		return null;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.impl.jmx.rtp.RtpManagerMBean#setVideoFormats(String);
	 */
	public void setVideoFormats(String videoFormats) {
	}

	@Override
	public void startService() throws Exception {
		logger.info("Deploying RTP manager: " + this.getName());
		if (!this.usePortMapping) {
			int randomPort = 9000;			
			for (int q = 0; q <= 10; q++) {
				if (mapStun(randomPort + q * 1003, bindAddress))
					break;
			}
		}
		rtpFactory = new RtpFactory();
		rtpFactory.setBindAddress(bindAddress);
		rtpFactory.setJitter(jitter);
		rtpFactory.setPacketizationPeriod(packetizationPeriod);
		rtpFactory.setPortRange(portRange);
		rtpFactory.setPublicAddressFromStun(publicAddressFromStun);
		rtpFactory.setStunServerAddress(stunServerAddress);
		rtpFactory.setStunServerPort(stunServerPort);
		rtpFactory.setUsePortMapping(usePortMapping);
		rtpFactory.setUseStun(useStun);
		rtpFactory.setAudioFormats(audioFormats);
		rebind();
	}

	/**
	 * Stops MBean.
	 */
	@Override
	public void stopService() {
		unbind(jndiName);
		logger.info("Stopped RTP manager " + this.getJndiName());
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
					ctx = (Context) ctx.lookup(tokens[i]);
				} catch (NamingException e) {
					ctx = ctx.createSubcontext(tokens[i]);
				}
			}
		}

		ctx.bind(tokens[tokens.length - 1], rtpFactory);
	}

	/**
	 * Unbounds object under specified name.
	 * 
	 * @param jndiName
	 *            the JNDI name of the object to be unbound.
	 */
	private void unbind(String jndiName) {
		try {
			InitialContext initialContext = new InitialContext();
			initialContext.unbind(jndiName);
		} catch (NamingException e) {
			logger.error("Failed to unbind endpoint", e);
		}
	}

	private boolean mapStun(int localPort, String localAddress) {
		try {
			if (InetAddress.getByName(localAddress).isLoopbackAddress()) {
				logger.warn("The Ip address provided is the loopback address, stun won't be enabled for it");
				this.publicAddressFromStun = localAddress;
			} else {
				StunAddress localStunAddress = new StunAddress(localAddress, localPort);

				StunAddress serverStunAddress = new StunAddress(stunServerAddress, stunServerPort);

				NetworkConfigurationDiscoveryProcess addressDiscovery = new NetworkConfigurationDiscoveryProcess(
						localStunAddress, serverStunAddress);
				addressDiscovery.start();
				StunDiscoveryReport report = addressDiscovery.determineAddress();
				if (report.getPublicAddress() != null) {
					this.publicAddressFromStun = report.getPublicAddress().getSocketAddress().getAddress()
							.getHostAddress();
					// TODO set a timer to retry the binding and provide a
					// callback to update the global ip address and port
				} else {
					useStun = false;
					logger.error("Stun discovery failed to find a valid public ip address, disabling stun !");
				}
				logger.info("Stun report = " + report);
				addressDiscovery.shutDown();
			}
		} catch (Throwable t) {
			logger.error("Stun lookup has failed: " + t.getMessage());
			return false;
		}
		return true;

	}

}
