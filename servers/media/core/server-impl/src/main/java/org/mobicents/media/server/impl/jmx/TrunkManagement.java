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

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanProxy;
import org.jboss.system.ServiceMBeanSupport;

/**
 * 
 * @author Oleg Kulikov
 */
public class TrunkManagement extends ServiceMBeanSupport implements TrunkManagementMBean {

	private Integer channels;
	private String jndiName;
	private ObjectName endpointManagementMBean = null;
	private transient Logger logger = Logger.getLogger(TrunkManagement.class);

	/**
	 * Creates a new instance of EndpointManagement
	 */
	public TrunkManagement() {
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
	 * @param channels
	 *            the number of endpoints.
	 */
	public void setChannels(Integer channels) {
		this.channels = channels;
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
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public ObjectName getEndpointManagementMBean() {
		return this.endpointManagementMBean;
	}

	public void setEndpointManagementMBean(ObjectName endpointManagementMBean) {
		this.endpointManagementMBean = endpointManagementMBean;

	}

	/**
	 * Starts MBean.
	 */
	@Override
	public void startService() throws Exception {
		logger.info("Starting Trunk MBean " + this.getServiceName());

		for (int i = 0; i < this.getChannels(); i++) {

			// constructs names for endpoints
			String serviceName = getServiceName().getCanonicalName() + ", endpoint=" + i;

			String endpointClassName = (getServer().getObjectInstance(getEndpointManagementMBean())).getClassName();

			String endpointManagementMBeanClassName = endpointClassName + "MBean";

			Class endpointMBeanClass = Thread.currentThread().getContextClassLoader().loadClass(
					endpointManagementMBeanClassName);

			EndpointManagementMBean endpointManagementMBean = (EndpointManagementMBean) MBeanProxy.get(
					endpointMBeanClass, getEndpointManagementMBean(), getServer());

			// String localName = endpointManagementMBean.getJndiName() + "/" +
			// i;
			String localName = this.getJndiName() + "/" + i;

			EndpointManagementMBean endpoint = endpointManagementMBean.cloneEndpointManagementMBean();
			endpoint.setJndiName(localName);

			// register Endpoint as MBean
			ObjectName objectName = new ObjectName(serviceName);
			getServer().registerMBean(endpoint, objectName);
			getServer().invoke(objectName, "start", new Object[] {}, new String[] {});
		}
		logger.info("Started Trunk MBean " + this.getServiceName());
	}

	/**
	 * Stops MBean.
	 */
	@Override
	public void stopService() {
		logger.info("Stopping Trunk MBean " + this.getServiceName());
		MBeanServer mbeanServer = getServer();

		for (int i = 0; i < this.getChannels(); i++) {
			String name = getServiceName().getCanonicalName() + ", endpoint="
					+ i;
			try {
				ObjectName objectName = new ObjectName(name);
				mbeanServer.invoke(objectName, "stop", new Object[] {},
						new String[] {});
				mbeanServer.unregisterMBean(objectName);
			} catch (Exception e) {
				logger.error("Could not unregister endpoint", e);
			}
		}

		logger.info("Stopped Trunk BMean " + this.getServiceName());
	}

}
