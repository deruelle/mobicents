package org.mobicents.media.server.impl.jmx;

import javax.management.ObjectName;

import org.jboss.system.ServiceMBean;

/**
 * The Media Server Managemnet interface that is responsible for starting and
 * stopping of server.
 * 
 * This MBean displays the current version of MMS
 * 
 * @author amit.bhayani
 * 
 */
public interface MediaServerManagementMBean extends ServiceMBean {

	public String getVersion();

	
	public ObjectName getRtpManagerMBean();

	public void setRtpManagerMBean(ObjectName rtpManagerMBean);

	public ObjectName getAnnEndpointManagementMBean();

	public void setAnnEndpointManagementMBean(ObjectName annEndpointManagementMBean);

	public ObjectName getPrEndpointManagementMBean();

	public void setPrEndpointManagementMBean(ObjectName prEndpointManagementMBean);

	public ObjectName getLoopEndpointManagementMBean();

	public void setLoopEndpointManagementMBean(ObjectName loopEndpointManagementMBean);

	public ObjectName getConfEndpointManagementMBean();

	public void setConfEndpointManagementMBean(ObjectName confEndpointManagementMBean);

	public ObjectName getIvrEndpointManagementMBean();

	public void setIvrEndpointManagementMBean(ObjectName ivrEndpointManagementMBean);
	
	public String getPlatformStateAsString();
	public ServerState getPlatformState();
	
	/**
	 * Moves platform into STOPING state. In this state it does not allow to create any more endpoints. It awaits for endpoints to die. Once they terminate
	 * platform state shifts to STOPED
	 */
	public void stopPlatform();
	/**
	 * Moves platform into runnign state. Allows endpoints to be created.
	 */
	public void startPlatform();
	/**
	 * Moves platform to STOPED state - terminates any processing that may be in progress.
	 */
	public void tearDownPlatform();
	public void setDependencyMBean(ObjectName name);

}
