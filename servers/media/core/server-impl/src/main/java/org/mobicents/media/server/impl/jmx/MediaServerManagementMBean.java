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

}
