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
public class MediaServerManagement extends ServiceMBeanSupport implements
		MediaServerManagementMBean {

	int dtmfThreshold = 500000;

	private ObjectName annTrunkManagementMBean;
	private ObjectName confEndpointManagementMBean;
	private ObjectName loopEndpointManagementMBean;
	private ObjectName prTrunkManagementMBean;

	public MediaServerManagement() {
		super(MediaServerManagementMBean.class);
		logger.info("[[[[[[[[[ " + getVersion() + " starting... ]]]]]]]]]");
	}

	private static Logger logger = Logger
			.getLogger(MediaServerManagement.class);

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

	public ObjectName getAnnTrunkManagementMBean() {
		return this.annTrunkManagementMBean;
	}

	public ObjectName getConfEndpointManagementMBean() {
		return this.confEndpointManagementMBean;
	}

	public ObjectName getLoopEndpointManagementMBean() {
		return this.loopEndpointManagementMBean;
	}

	public ObjectName getPRTrunkManagementMBean() {
		return this.prTrunkManagementMBean;
	}

	public String getVersion() {
		return Version.instance.toString();
	}

	public void setAnnTrunkManagementMBean(ObjectName annTrunkManagementMBean) {
		this.annTrunkManagementMBean = annTrunkManagementMBean;

	}

	public void setConfEndpointManagementMBean(
			ObjectName confEndpointManagementMBean) {
		this.confEndpointManagementMBean = confEndpointManagementMBean;

	}

	public void setLoopEndpointManagementMBean(
			ObjectName loopEndpointManagementMBean) {
		this.loopEndpointManagementMBean = loopEndpointManagementMBean;

	}

	public void setPRTrunkManagementMBean(ObjectName prTrunkManagementMBean) {
		this.prTrunkManagementMBean = prTrunkManagementMBean;

	}

	public int getDtmfThreshold(){
		return this.dtmfThreshold;
	}

	public void setDtmfThreshold(int threshold){
		this.dtmfThreshold = threshold;
	}

}
