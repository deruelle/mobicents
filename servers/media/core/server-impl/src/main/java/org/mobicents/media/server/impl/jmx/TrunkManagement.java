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

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.rtp.RtpFactory;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class TrunkManagement implements TrunkManagementMBean {

	private String trunkName;
	private Integer channels = 0;
	private RtpFactory rtpFactory = null;

	private transient Logger logger = Logger
			.getLogger(TrunkManagement.class);

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
	public String getTrunkName() {
		return this.trunkName;
	}

	/**
	 * Sets the JNDI name to which this trunk object should be bound.
	 * 
	 * @param jndiName
	 *            the JNDI name to which trunk object will be bound.
	 */
	public void setTrunkName(String trunkName) {
		this.trunkName = trunkName;
	}

	/**
	 * Gets the name of used RTP Factory.
	 * 
	 * @return the JNDI name of the RTP Factory
	 */
	public RtpFactory getRtpFactory() {
		return this.rtpFactory;
	}

	/**
	 * Sets the name of used RTP Factory.
	 * 
	 * @param rtpFactoryName
	 *            the JNDI name of the RTP Factory.
	 */
	public void setRtpFactory(RtpFactory rtpFactory) {
		this.rtpFactory = rtpFactory;
	}

	public Integer getChannels() {
		return this.channels;
	}

	public void setChannels(Integer channels) {
		this.channels = channels;
	}

	public abstract Endpoint createEndpoint(String localName) throws Exception;

	public void create() {
		logger.info("Starting trunk " + trunkName + ", channels=" + channels);
	}

	/**
	 * Starts MBean.
	 */
	public void start() throws Exception {

		EndpointQuery endpointQuery = EndpointQuery.getInstance();
		for (int i = 0; i < channels; i++) {
			String name = this.trunkName + "/" + i;
			Endpoint enp = createEndpoint(name);

			((BaseEndpoint) enp).setRtpFactory(this.rtpFactory);
			enp.start();

			endpointQuery.addEndpoint(enp.getLocalName(), enp);

			// rebind(enp.getLocalName(), enp);
			logger.info("Started endpoint: " + this.trunkName + "/" + i);
		}

	}

	/**
	 * Stops MBean.
	 */
	public void stop() {
		Endpoint endpoint = null;
		logger.info("Stoping trunk " + trunkName + ", channels=" + channels);
		for (int i = 0; i < channels; i++) {
			endpoint = EndpointQuery.getInstance().remove(trunkName + "/" + i);
			if (endpoint != null) {
				endpoint.stop();
			}
			logger.info("Stopped endpoint: " + trunkName + "/" + i);
		}
	}

	public void destroy() {

	}
}
