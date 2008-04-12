package org.mobicents.media.server.impl.jmx;

import javax.management.ObjectName;

import org.jboss.system.ServiceMBean;

/**
 * The Media Server Managemnet interface that is responsible for starting and
 * stopping of server
 * 
 * @author amit.bhayani
 * 
 */
public interface MediaServerManagementMBean extends ServiceMBean {

	public String getVersion();

	public ObjectName getAnnTrunkManagementMBean();

	public void setAnnTrunkManagementMBean(ObjectName annTrunkManagementMBean);

	public ObjectName getPRTrunkManagementMBean();

	public void setPRTrunkManagementMBean(ObjectName prTrunkManagementMBean);

	public ObjectName getLoopEndpointManagementMBean();

	public void setLoopEndpointManagementMBean(
			ObjectName loopEndpointManagementMBean);

	public ObjectName getConfEndpointManagementMBean();

	public void setConfEndpointManagementMBean(
			ObjectName confEndpointManagementMBean);

}
