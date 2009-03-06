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
import org.mobicents.media.server.impl.rtp.RtpFactory;

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
	public RtpFactory getRtpFactory();

	/**
	 * Sets the name of used RTP Factory.
	 * 
	 * @param rtpFactoryName
	 *            the JNDI name of the RTP Factory.
	 */
	public void setRtpFactory(RtpFactory rtpFactory) throws Exception;

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

}
