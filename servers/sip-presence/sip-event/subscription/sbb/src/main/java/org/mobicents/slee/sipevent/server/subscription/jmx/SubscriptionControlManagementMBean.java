package org.mobicents.slee.sipevent.server.subscription.jmx;

/**
 * JMX Configuration of the SIP Event Subscription Control.
 * 
 * @author martins
 *
 */
public interface SubscriptionControlManagementMBean {

	
	public static final String MBEAN_NAME="slee:sippresence=SipEventSubscriptionControl";
	
	/**
	 * Retrieves default subscription time in seconds.
	 * @return
	 */
	public int getDefaultExpires();

	/**
	 * Defines default subscription time in seconds.
	 * @param defaultExpires
	 */
	public void setDefaultExpires(int defaultExpires);
	
	/**
	 * Retrieves maximum subscription time in seconds.
	 * @return
	 */
	public int getMaxExpires();
	
	/**
	 * Defines maximum subscription time in seconds.
	 * @param maxExpires
	 */
	public void setMaxExpires(int maxExpires);
	
	/**
	 * Retrieves minimum subscription time in seconds.
	 * @return
	 */
	public int getMinExpires();
	
	/**
	 * Defines minimum subscription time in seconds.
	 * @param maxExpires
	 */
	public void setMinExpires(int minExpires);
	
	/**
	 * Retrieves default expires value, in seconds, to keep a subscription in waiting state.
	 * @return
	 */
	public int getDefaultWaitingExpires();
	
	/**
	 * Defines default expires value, in seconds, to keep a subscription in waiting state.
	 * @param defaultWaitingExpires
	 */
	public void setDefaultWaitingExpires(int defaultWaitingExpires);
	
	/**
	 * Retrieves Max-Forwards header value for generated NOTIFY requests.
	 * @return
	 */
	public int getMaxForwards();
	
	/**
	 * Defines Max-Forwards header value for generated NOTIFY requests.
	 * @param maxForwards
	 */
	public void setMaxForwards(int maxForwards);
	
	/**
	 * Retrieves the display name used in contact header's addresses.
	 * @return
	 */
	public String getContactAddressDisplayName();
	
	/**
	 * Defines the display name used in contact header's addresses.
	 * @param contactAddressDisplayName
	 */
	public void setContactAddressDisplayName(String contactAddressDisplayName);
	
	/**
	 * Indicates if event list support is on, that is, this server acts as Resource List Server too
	 * @return
	 */
	public boolean getEventListSupportOn();
	
	/**
	 * Turns on/off event list support, that is, if this server acts as Resource List Server too
	 * @param eventListSupportOn
	 */
	public void setEventListSupportOn(boolean eventListSupportOn);
	
}
