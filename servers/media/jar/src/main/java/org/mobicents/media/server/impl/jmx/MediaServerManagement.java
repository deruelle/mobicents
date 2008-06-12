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

	private ObjectName annTrunkManagementMBean;
	private ObjectName confTrunkManagementMBean;
	private ObjectName dftEndpointManagementMBean;
	private ObjectName ivrTrunkManagementMBean;
	private ObjectName loopTrunkManagementMBean;
	private ObjectName prTrunkManagementMBean;

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

	public ObjectName getAnnTrunkManagementMBean() {
		return this.annTrunkManagementMBean;
	}

	public ObjectName getConfTrunkManagementMBean() {
		return this.confTrunkManagementMBean;
	}

	public ObjectName getDFTEndpointManagementMBean() {
		return this.dftEndpointManagementMBean;
	}

	public ObjectName getIVRTrunkManagementMBean() {
		return this.ivrTrunkManagementMBean;
	}

	public ObjectName getLoopTrunkManagementMBean() {
		return this.loopTrunkManagementMBean;
	}

	public ObjectName getPRTrunkManagementMBean() {
		return this.prTrunkManagementMBean;
	}

	public void setAnnTrunkManagementMBean(ObjectName annTrunkManagementMBean) {
		this.annTrunkManagementMBean = annTrunkManagementMBean;
	}

	public void setConfTrunkManagementMBean(ObjectName confTrunkManagementMBean) {
		this.confTrunkManagementMBean = confTrunkManagementMBean;

	}

	public void setDFTEndpointManagementMBean(ObjectName dftEndpointManagementMBean) {
		this.dftEndpointManagementMBean = dftEndpointManagementMBean;

	}

	public void setIVRTrunkManagementMBean(ObjectName ivrTrunkManagementMBean) {
		this.ivrTrunkManagementMBean = ivrTrunkManagementMBean;

	}

	public void setLoopTrunkManagementMBean(ObjectName loopTrunkManagementMBean) {
		this.loopTrunkManagementMBean = loopTrunkManagementMBean;

	}

	public void setPRTrunkManagementMBean(ObjectName prTrunkManagementMBean) {
		this.prTrunkManagementMBean = prTrunkManagementMBean;

	}

}
