package org.mobicents.media.server.impl.jmx;

import javax.management.ObjectName;

import org.jboss.logging.Logger;
import org.jboss.system.ServiceMBeanSupport;
import org.mobicents.media.server.impl.Version;

/**
 * 
 * @author amit.bhayani
 * 
 */

// TODO Handle option to Start, Stop MediaServer gracefully
public class MediaServerManagement extends ServiceMBeanSupport implements MediaServerManagementMBean {

	private ObjectName rtpManagerMBean;
	private ObjectName annEndpointManagementMBean;
	private ObjectName prEndpointManagementMBean;
	private ObjectName loopEndpointManagementMBean;
	private ObjectName confEndpointManagementMBean;
	private ObjectName ivrEndpointManagementMBean;

	public MediaServerManagement() {
		super(MediaServerManagementMBean.class);
		logger.info("[[[[[[[[[ " + getVersion() + " starting... ]]]]]]]]]");
	}

	private static Logger logger = Logger.getLogger(MediaServerManagement.class);

	@Override
	protected void startService() throws Exception {
		super.startService();
		logger.info("[[[[[[[[[ " + getVersion() + " Started " + "]]]]]]]]]");
	}

	@Override
	protected void stopService() throws Exception {
		super.stopService();
		logger.info("[[[[[[[[[ " + getVersion() + " Stopped " + "]]]]]]]]]");
	}

	public String getVersion() {
		return Version.instance.toString();
	}

	public ObjectName getRtpManagerMBean() {
		return rtpManagerMBean;
	}

	public void setRtpManagerMBean(ObjectName rtpManagerMBean) {
		this.rtpManagerMBean = rtpManagerMBean;
	}

	public ObjectName getAnnEndpointManagementMBean() {
		return annEndpointManagementMBean;
	}

	public void setAnnEndpointManagementMBean(ObjectName annEndpointManagementMBean) {
		this.annEndpointManagementMBean = annEndpointManagementMBean;
	}

	public ObjectName getPrEndpointManagementMBean() {
		return prEndpointManagementMBean;
	}

	public void setPrEndpointManagementMBean(ObjectName prEndpointManagementMBean) {
		this.prEndpointManagementMBean = prEndpointManagementMBean;
	}

	public ObjectName getLoopEndpointManagementMBean() {
		return loopEndpointManagementMBean;
	}

	public void setLoopEndpointManagementMBean(ObjectName loopEndpointManagementMBean) {
		this.loopEndpointManagementMBean = loopEndpointManagementMBean;
	}

	public ObjectName getConfEndpointManagementMBean() {
		return confEndpointManagementMBean;
	}

	public void setConfEndpointManagementMBean(ObjectName confEndpointManagementMBean) {
		this.confEndpointManagementMBean = confEndpointManagementMBean;
	}

	public ObjectName getIvrEndpointManagementMBean() {
		return ivrEndpointManagementMBean;
	}

	public void setIvrEndpointManagementMBean(ObjectName ivrEndpointManagementMBean) {
		this.ivrEndpointManagementMBean = ivrEndpointManagementMBean;
	}



}
