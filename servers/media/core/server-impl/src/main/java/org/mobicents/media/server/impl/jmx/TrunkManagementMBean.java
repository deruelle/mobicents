/*
 * TrunkManagementMBean.java
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

import javax.naming.NamingException;

import org.jboss.system.ServiceMBean;

/**
 * 
 * @author Oleg Kulikov
 */
public interface TrunkManagementMBean extends ServiceMBean {
	/**
	 * Gets the JNDI name of the endpoint.
	 * 
	 * @return the JNDI name string.
	 */
	public String getJndiName();

	/**
	 * Modify the JNDI name of the endpoint.
	 * 
	 * @param jndiName
	 *            the new value of the Jndi name.
	 */
	public void setJndiName(String jndiName) throws NamingException;

	/**
	 * Gets the name of used RTP Factory.
	 * 
	 * @return the JNDI name of the RTP Factory
	 */
	public String getRtpFactoryName();

	/**
	 * Sets the name of used RTP Factory.
	 * 
	 * @param rtpFactoryName
	 *            the JNDI name of the RTP Factory.
	 */
	public void setRtpFactoryName(String rtpFactoryName) throws Exception;

	/**
	 * Gets ammount of Endpoints.
	 * 
	 * @return the amount of endpoints included into this trunk.
	 */
	public Integer getChannels();

	/**
	 * Sets the amount of endpoints included into this trunk.
	 * 
	 * @param channels
	 *            the number of endpoints.
	 */
	public void setChannels(Integer channels);

	// #########################
	// # MANAGEMENT OPERATIONS #
	// #########################
	public int getConnectionsCount(String endpointName) throws IllegalArgumentException;

	public long getCreationTime(String endpointName) throws IllegalArgumentException;

	public long getPacketsCount(String endpointName) throws IllegalArgumentException;

	public long getNumberOfBytes(String endpointName) throws IllegalArgumentException;

	public String getRTPFacotryJNDIName(String endpointName) throws IllegalArgumentException;

	public void setGatherPerformanceData(String endpointName, boolean flag) throws IllegalArgumentException;

	public boolean getGatherPerformanceFlag(String endpointName) throws IllegalArgumentException;

	public String[] getEndpointNames() throws IllegalArgumentException;

	public int getEndpointsCount() throws IllegalArgumentException;

	public String[] getConnectionIds(String endpointName) throws IllegalArgumentException;

	public void setRTPFacotryJNDIName(String endpointName, String jndiName) throws IllegalArgumentException;

	public void destroyConnection(String name, String connectionId) throws IllegalArgumentException;

	public void destroyEndpoint(String name) throws IllegalArgumentException;

	public String getTrunkName();

	// ######################################
	// # MANAGEMENT OPERATIONS: Connections #
	// ######################################
	public long getConnectionCreationTime(String endpoint, String connectionId) throws IllegalArgumentException;

	public String getConnectionLocalSDP(String endpoint, String connectionId) throws IllegalArgumentException;

	public String getConnectionRemoteSDP(String endpoint, String connectionId) throws IllegalArgumentException;

	public String getOtherEnd(String endpoint, String connectionId) throws IllegalArgumentException;

	public String getConnectionState(String endpointName, String connectionId) throws IllegalArgumentException;

	public String getConnectionMode(String endpointName, String connectionId) throws IllegalArgumentException;

	public int getPacketsSent(String endpointName, String connectionId) throws IllegalArgumentException;

	public int getPacketsReceived(String endpointName, String connectionId) throws IllegalArgumentException;

	public int getOctetsReceived(String endpointName, String connectionId) throws IllegalArgumentException;

	public int getOctetsSent(String endpointName, String connectionId) throws IllegalArgumentException;

	public int getInterArrivalJitter(String endpointName, String connectionId) throws IllegalArgumentException;

	public int getPacketsLost(String endpointName, String connectionId) throws IllegalArgumentException;

	// #########################
	// # START/STOP part #
	// #########################

	public void startPlatform();

	public void stopPlatform();

	public void tearDownPlatform();
}
