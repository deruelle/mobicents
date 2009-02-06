package org.mobicents.slee.sipevent.server.subscription.jmx;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;

public class SubscriptionControlManagement implements SubscriptionControlManagementMBean {

	private Logger logger = Logger.getLogger(SubscriptionControlManagement.class);
	
	private int defaultExpires = 3600;
	private int maxExpires = defaultExpires;
	private int minExpires = 60;
	private int defaultWaitingExpires = (24*60*60);
	private int maxForwards = 70;
	private String contactAddressDisplayName = "Mobicents SIP Event Server";
	private boolean eventListSupportOn = true;
	private String pChargingVectorHeaderTerminatingIOI = "mobicents.org";
	
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
		
		logger.info("SIP Event Subscription Control Management MBean started.");
	}
	
	public void stopService() {
		try {
			SleeContainer.lookupFromJndi().getMBeanServer().unregisterMBean(new ObjectName(MBEAN_NAME));
			logger.info("SIP Event Subscription Control Management MBean stopped.");
		} catch (Exception e) {
			logger.error("Failed to stop SIP Event Subscription Control Management MBean.",e);
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

	public int getDefaultWaitingExpires() {
		return defaultWaitingExpires;
	}

	public void setDefaultWaitingExpires(int defaultWaitingExpires) {
		this.defaultWaitingExpires = defaultWaitingExpires;
	}

	public int getMaxForwards() {
		return maxForwards;
	}

	public void setMaxForwards(int maxForwards) {
		this.maxForwards = maxForwards;
	}

	public String getContactAddressDisplayName() {
		return contactAddressDisplayName;
	}

	public void setContactAddressDisplayName(String contactAddressDisplayName) {
		this.contactAddressDisplayName = contactAddressDisplayName;
	}

	public boolean getEventListSupportOn() {
		return eventListSupportOn;
	}

	public void setEventListSupportOn(boolean eventListSupportOn) {
		this.eventListSupportOn = eventListSupportOn;
	}
	
	public String getPChargingVectorHeaderTerminatingIOI() {
		return pChargingVectorHeaderTerminatingIOI;
	}
	
	public void setPChargingVectorHeaderTerminatingIOI(
			String chargingVectorHeaderTerminatingIOI) {
		pChargingVectorHeaderTerminatingIOI = chargingVectorHeaderTerminatingIOI;
	}
}
