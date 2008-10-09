package org.mobicents.slee.sipevent.server.publication.jmx;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;

public class PublicationControlManagement implements PublicationControlManagementMBean {

	private Logger logger = Logger.getLogger(PublicationControlManagement.class);
	
	private int defaultExpires = 3600;
	private int maxExpires = defaultExpires;
	private int minExpires = 60;
	private String contactAddressDisplayName = "Mobicents SIP Event Server";
		
	public void startService() throws Exception {
		MBeanServer mbs=SleeContainer.lookupFromJndi().getMBeanServer();
		ObjectName on=null;
		try {
			on=new ObjectName(MBEAN_NAME);
			
		} catch (MalformedObjectNameException e) {
			logger.error(e);
		}
		
		try {
			if (mbs.getObjectInstance(on) != null) {
				mbs.unregisterMBean(on);
			}
		} catch (InstanceNotFoundException e) {
			// ignore
		} catch (Exception e) {
			logger.error(e);
		}
		
		try {
			mbs.registerMBean(this, on);
		} catch (InstanceAlreadyExistsException e) {
			logger.error(e);
		}
		
		logger.info("Management MBean started.");
	}
	
	public void stopService() {
		try {
			SleeContainer.lookupFromJndi().getMBeanServer().unregisterMBean(new ObjectName(MBEAN_NAME));
			logger.info("Management MBean stopped.");
		} catch (Exception e) {
			logger.error("Failed to stop Management MBean.",e);
		}
	}

	public int getDefaultExpires() {
		return defaultExpires;
	}

	public void setDefaultExpires(int defaultExpires) {
		this.defaultExpires = defaultExpires;
	}

	public int getMaxExpires() {
		return maxExpires;
	}

	public void setMaxExpires(int maxExpires) {
		this.maxExpires = maxExpires;
	}

	public int getMinExpires() {
		return minExpires;
	}

	public void setMinExpires(int minExpires) {
		this.minExpires = minExpires;
	}

	public String getContactAddressDisplayName() {
		return contactAddressDisplayName;
	}

	public void setContactAddressDisplayName(String contactAddressDisplayName) {
		this.contactAddressDisplayName = contactAddressDisplayName;
	}
	
}
