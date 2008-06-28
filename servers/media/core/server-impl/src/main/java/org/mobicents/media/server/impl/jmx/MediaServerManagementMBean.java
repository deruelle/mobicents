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

	public ObjectName getAnnTrunkManagementMBean();

	public void setAnnTrunkManagementMBean(ObjectName annTrunkManagementMBean);

	public ObjectName getPRTrunkManagementMBean();

	public void setPRTrunkManagementMBean(ObjectName prTrunkManagementMBean);

	public ObjectName getLoopTrunkManagementMBean();

	public void setLoopTrunkManagementMBean(ObjectName loopTrunkManagementMBean);

	public ObjectName getConfTrunkManagementMBean();

	public void setConfTrunkManagementMBean(ObjectName confTrunkManagementMBean);

	public ObjectName getIVRTrunkManagementMBean();

	public void setIVRTrunkManagementMBean(ObjectName ivrTrunkManagementMBean);

	public ObjectName getDFTEndpointManagementMBean();

	public void setDFTEndpointManagementMBean(ObjectName dftEndpointManagementMBean);

}
