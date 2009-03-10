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

import org.mobicents.media.server.impl.rtp.RtpFactory;

/**
 * 
 * @author Oleg Kulikov
 */
public interface TrunkManagementMBean {
	/**
	 * Gets the JNDI name of the endpoint.
	 * 
	 * @return the JNDI name string.
	 */
	public String getTrunkName();

	/**
	 * Gets the name of used RTP Factory.
	 * 
	 * @return the JNDI name of the RTP Factory
	 */
	public RtpFactory getRtpFactory();

	/**
	 * Gets ammount of Endpoints.
	 * 
	 * @return the amount of endpoints included into this trunk.
	 */
	public Integer getChannels();

}
