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

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.BaseVirtualEndpoint;
import org.mobicents.media.server.local.management.EndpointLocalManagement;
import org.mobicents.media.server.spi.Endpoint;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class EndpointManagement extends ServiceMBeanSupport implements EndpointManagementMBean{

	protected Endpoint endpoint;
	private String jndiName;
	private String rtpFactoryName;
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
	 * @param jndiName
	 *            the JNDI name to which trunk object will be bound.
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
	 * Gets the name of used RTP Factory.
	 * 
	 * @return the JNDI name of the RTP Factory
	 */
	public String getRtpFactoryName() {
		return rtpFactoryName;
	}

	/**
	 * Sets the name of used RTP Factory.
	 * 
	 * @param rtpFactoryName
	 *            the JNDI name of the RTP Factory.
	 */
	public void setRtpFactoryName(String rtpFactoryName) {
		this.rtpFactoryName = rtpFactoryName;
		if (this.getState() == STARTED) {
			((BaseEndpoint) getEndpoint()).setRtpFactoryName(rtpFactoryName);
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
					ctx = (Context) ctx.lookup(tokens[i]);
				} catch (NamingException e) {
					ctx = ctx.createSubcontext(tokens[i]);
				}
			}
		}

		ctx.bind(tokens[tokens.length - 1], endpoint);
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

	public abstract Endpoint createEndpoint() throws Exception;

	public abstract EndpointManagementMBean cloneEndpointManagementMBean();

	/*
	 * private void mapStun(int localPort, String localAddress) { try { if
	 * (InetAddress.getByName(localAddress).isLoopbackAddress()) {logger.warn(
	 * "The Ip address provided is the loopback address, stun won't be enabled for it"
	 * ); this.publicAddressFromStun = localAddress; } else { StunAddress
	 * localStunAddress = new StunAddress(localAddress, localPort);
	 * 
	 * StunAddress serverStunAddress = new StunAddress( stunServerAddress,
	 * stunServerPort);
	 * 
	 * NetworkConfigurationDiscoveryProcess addressDiscovery = new
	 * NetworkConfigurationDiscoveryProcess( localStunAddress,
	 * serverStunAddress); addressDiscovery.start(); StunDiscoveryReport report
	 * = addressDiscovery.determineAddress(); if (report.getPublicAddress() !=
	 * null) { this.publicAddressFromStun =
	 * report.getPublicAddress().getSocketAddress
	 * ().getAddress().getHostAddress(); //TODO set a timer to retry the binding
	 * and provide a callback to update the global ip address and port } else {
	 * useStun = false;logger.error(
	 * "Stun discovery failed to find a valid public ip address, disabling stun !"
	 * ); } logger.info("Stun report = " + report); addressDiscovery.shutDown();
	 * } } catch (Throwable t) { logger.error("Stun lookup has failed: " +
	 * t.getMessage()); }
	 * 
	 * }
	 */
	/**
	 * Starts MBean.
	 */
	@Override
	public void startService() throws Exception {
		endpoint = createEndpoint();
		((BaseEndpoint) endpoint).setRtpFactoryName(rtpFactoryName);

		rebind();
		logger.info("Started Endpoint MBean " + this.getJndiName());
	}

	/**
	 * Stops MBean.
	 */
	@Override
	public void stopService() {
		unbind(jndiName);
		logger.info("Stopped Endpoint MBean " + this.getJndiName());
	}

	// #########################
	// # MANAGEMENT OPERATIONS #
	// #########################

	private EndpointLocalManagement getEndpoint(String endpointName) throws IllegalArgumentException {

		if (endpointName.equals(this.endpoint.getLocalName())) {
			if (log.isDebugEnabled()) {
				log.debug("Fetching MBean static endpoint, possible danger of missuse.");
			}
			return (EndpointLocalManagement) this.endpoint;
		}
		EndpointLocalManagement[] locals = ((EndpointLocalManagement) endpoint).getEndpoints();
		for (EndpointLocalManagement l : locals) {
			if (l.getLocalName().equals(endpointName))
				return l;
		}
		throw new IllegalArgumentException("Endpoint with name: " + endpointName + ", is not available/present.");
	}

	public int getConnectionsCount(String endpointName) throws IllegalArgumentException {
		return getEndpoint(endpointName).getConnectionsCount();
	}

	public long getCreationTime(String endpointName) throws IllegalArgumentException {
		return getEndpoint(endpointName).getCreationTime();
	}

	public boolean getGatherPerformanceFlag(String endpointName) throws IllegalArgumentException {
		return getEndpoint(endpointName).getGatherPerformanceFlag();
	}

	public long getNumberOfBytes(String endpointName) throws IllegalArgumentException {
		return getEndpoint(endpointName).getNumberOfBytes();
	}

	public long getPacketsCount(String endpointName) throws IllegalArgumentException {
		return getEndpoint(endpointName).getPacketsCount();
	}

	public String[] getEndpointNames() throws IllegalArgumentException {

		return ((EndpointLocalManagement) endpoint).getEndpointNames();

	}

	public int getEndpointsCount() throws IllegalArgumentException {
		// This is just to have this info clearly visible in MBean view.
		String[] tmp = getEndpointNames();
		if (tmp == null) {
			return -1;
		} else {
			return tmp.length;
		}
	}

	public long getConnectionCreationTime(String endpoint, String connectionId) throws IllegalArgumentException {

		return getEndpoint(endpoint).getConnectionCreationTime(connectionId);

	}

	public String[] getConnectionIds(String endpointName) throws IllegalArgumentException {
		return getEndpoint(endpointName).getConnectionIds();
	}

	public String getConnectionLocalSDP(String endpoint, String connectionId) throws IllegalArgumentException {
		return getEndpoint(endpoint).getConnectionLocalSDP(connectionId);
	}

	public String getConnectionRemoteSDP(String endpoint, String connectionId) throws IllegalArgumentException {
		return getEndpoint(endpoint).getConnectionRemoteSDP(connectionId);
	}


	public String getOtherEnd(String endpoint, String connectionId) throws IllegalArgumentException {

		return getEndpoint(endpoint).getOtherEnd(connectionId);
	}

	public String getConnectionMode(String endpointName, String connectionId) throws IllegalArgumentException {
		return getEndpoint(endpointName).getConnectionMode(connectionId);
	}

	public String getConnectionState(String endpointName, String connectionId) throws IllegalArgumentException {
		return getEndpoint(endpointName).getConnectionState(connectionId);
	}

	public String getRTPFacotryJNDIName(String endpointName) throws IllegalArgumentException {
		return getEndpoint(endpointName).getRTPFacotryJNDIName();
	}

	public void setGatherPerformanceData(String endpointName, boolean value) throws IllegalArgumentException {
		
		
		
		
		try{
		if(endpointName==null)
		{
			//This means its a trunk call
			((EndpointLocalManagement)this.endpoint).setGatherPerformanceData(value);
			for(EndpointLocalManagement elm : ((EndpointLocalManagement) endpoint).getEndpoints())
			{
				elm.setGatherPerformanceData(value);
			}
		}else
		{	EndpointLocalManagement elm = getEndpoint(endpointName);
			elm.setGatherPerformanceData(value);
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public void setRTPFacotryJNDIName(String endpointName, String jndiName) throws IllegalArgumentException {
		EndpointLocalManagement elm = getEndpoint(endpointName);
		elm.setRTPFacotryJNDIName(jndiName);
	}

	public void destroyConnection(String name, String connectionId) throws IllegalArgumentException {
		BaseEndpoint elm = (BaseEndpoint) getEndpoint(name);
		try {
			elm.deleteConnection(connectionId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Connection does not exist?");
		}
	}

	public void destroyEndpoint(String name) throws IllegalArgumentException {
		if (!name.equals(this.endpoint.getLocalName())) {
			BaseEndpoint elm = (BaseEndpoint) getEndpoint(name);
			try {
				// This is weird....
				elm.deleteAllConnections();
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Connection does not exist?");
			}
		} else {
			throw new IllegalArgumentException("Dont use delete operation on MBean endpoint!!!");
		}
	}

	public String getTrunkName() {
		if (this.endpoint == null) {
			return "NOT DEFINED";
		} else {
			return this.endpoint.getLocalName();
		}
	}

	
	
	public int getInterArrivalJitter(String endpointName, String connectionId)
			throws IllegalArgumentException {
		EndpointLocalManagement elm = getEndpoint(endpointName);
		
		return elm.getInterArrivalJitter(connectionId);
	}

	public int getOctetsReceived(String endpointName, String connectionId)
			throws IllegalArgumentException {
		EndpointLocalManagement elm = getEndpoint(endpointName);
		
		return elm.getOctetsReceived(connectionId);
	}

	public int getOctetsSent(String endpointName, String connectionId)
			throws IllegalArgumentException {
		EndpointLocalManagement elm = getEndpoint(endpointName);
		
		return elm.getOctetsSent(connectionId);
	}

	public int getPacketsLost(String endpointName, String connectionId)
			throws IllegalArgumentException {
		EndpointLocalManagement elm = getEndpoint(endpointName);
		
		return elm.getPacketsLost(connectionId);
	}

	public int getPacketsReceived(String endpointName, String connectionId)
			throws IllegalArgumentException {
		EndpointLocalManagement elm = getEndpoint(endpointName);
		
		return elm.getPacketsReceived(connectionId);
	}

	public int getPacketsSent(String endpointName, String connectionId)
			throws IllegalArgumentException {
		EndpointLocalManagement elm = getEndpoint(endpointName);
		
		return elm.getPacketsSent(connectionId);
	}

	// ////////////////
	// // STOP/START //
	// ////////////////
	public void startPlatform() {
		
		((BaseVirtualEndpoint)this.endpoint).start();
		
	}

	public void stopPlatform() {
		((BaseVirtualEndpoint)this.endpoint).stop();
		
	}

	public void tearDownPlatform() {
		this.stopPlatform();
		for(String name: this.getEndpointNames())
		{
			this.destroyEndpoint(name);
		}

		
	}

	
	
}
