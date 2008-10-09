package org.mobicents.slee.sipevent.server.publication.jmx;

/**
 * JMX Configuration of the SIP Event Subscription Control.
 * 
 * @author martins
 *
 */
public interface PublicationControlManagementMBean {

	
	public static final String MBEAN_NAME="slee:sippresence=SipEventPublicationControl";
	
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
	 * Retrieves the display name used in contact header's addresses.
	 * @return
	 */
	public String getContactAddressDisplayName();
	
	/**
	 * Defines the display name used in contact header's addresses.
	 * @param contactAddressDisplayName
	 */
	public void setContactAddressDisplayName(String contactAddressDisplayName);
}
