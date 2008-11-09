package org.mobicents.media.server.impl.jmx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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

	
	protected Set<ObjectName> dependantBeans=new HashSet<ObjectName>();
	
	protected List<ObjectName> hugeList=new ArrayList<ObjectName>();
	
	protected Timer stopTimer=new Timer();
	protected TimerTask runningTask=null;
	
	protected ServerState state=ServerState.RUNNING;
	
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
		hugeList.add(ivrEndpointManagementMBean);
	}

	public ObjectName getAnnEndpointManagementMBean() {
		return annEndpointManagementMBean;
	}

	public void setAnnEndpointManagementMBean(ObjectName annEndpointManagementMBean) {
		this.annEndpointManagementMBean = annEndpointManagementMBean;
		hugeList.add(annEndpointManagementMBean);
	}

	public ObjectName getPrEndpointManagementMBean() {
		return prEndpointManagementMBean;
	}

	public void setPrEndpointManagementMBean(ObjectName prEndpointManagementMBean) {
		this.prEndpointManagementMBean = prEndpointManagementMBean;
		hugeList.add(prEndpointManagementMBean);
	}

	public ObjectName getLoopEndpointManagementMBean() {
		return loopEndpointManagementMBean;
	}

	public void setLoopEndpointManagementMBean(ObjectName loopEndpointManagementMBean) {
		this.loopEndpointManagementMBean = loopEndpointManagementMBean;
		hugeList.add(loopEndpointManagementMBean);
	}

	public ObjectName getConfEndpointManagementMBean() {
		return confEndpointManagementMBean;
	}

	public void setConfEndpointManagementMBean(ObjectName confEndpointManagementMBean) {
		this.confEndpointManagementMBean = confEndpointManagementMBean;
		hugeList.add(confEndpointManagementMBean);
	}

	public ObjectName getIvrEndpointManagementMBean() {
		return ivrEndpointManagementMBean;
	}

	public void setIvrEndpointManagementMBean(ObjectName ivrEndpointManagementMBean) {
		this.ivrEndpointManagementMBean = ivrEndpointManagementMBean;
		hugeList.add(ivrEndpointManagementMBean);
	}

	public void setDependencyMBean(ObjectName name)
	{
		this.dependantBeans.add(name);
		hugeList.add(name);
	}
	
	public String getPlatformStateAsString()
	{
		return state.toString();
	}
	public ServerState getPlatformState()
	{
		return state;
	}
	
	/**
	 * Moves platform into STOPING state. In this state it does not allow to create any more endpoints. It awaits for endpoints to die. Once they terminate
	 * platform state shifts to STOPED
	 */
	public void stopPlatform()
	{
		state=ServerState.STOPPING;
		invokeOperation("stopPlatform",true);
		
		
	}
	/**
	 * Moves platform into runnign state. Allows endpoints to be created.
	 */
	public void startPlatform()
	{
		if(this.runningTask!=null)
		{
			try
			{
				this.runningTask.cancel();
				this.runningTask=null;
			}catch(Exception e)
			{
				
			}
		}
		invokeOperation("startPlatform",false);
		state=ServerState.RUNNING;
		
	}
	/**
	 * Moves platform to STOPED state - terminates any processing that may be in progress.
	 */
	public void tearDownPlatform()
	{
		state=ServerState.STOPPING;
		invokeOperation("tearDownPlatform",true);
	}
	
	
	private void invokeOperation(String name, boolean startTimer)
	{
		//now we have to iterate over mbeans and make them stop ?
		String operationName=name;
		Object[] params=null;
		String[] types=null;
		for(ObjectName on:hugeList)
		{
			try{
				super.server.invoke(on, name, params, types);
			}catch(Exception e)
			{
				log.error("Failed to perform operation: "+name+" on MBean: "+on,e);
			}
		}
		
		if(startTimer)
		{
			if(this.runningTask!=null)
			{
				this.runningTask.cancel();
			}
			this.runningTask=new StateChangeTimerTask();
			this.stopTimer.scheduleAtFixedRate(runningTask, 5000, 5000);
		}
	}
	
	private class StateChangeTimerTask extends TimerTask
	{

		@Override
		public void run() {
			
			String operationName="getEndpointsCount";
			Object[] params=null;
			String[] types=null;
			for(ObjectName on:hugeList)
			{
				try{
					Object value=server.invoke(on, operationName, params, types);
					Integer i=(Integer) value;
					if(i!=null && i.intValue()>0)
					{
						return;
					}
				}catch(Exception e)
				{
					log.error("Failed to perform operation: "+operationName+" on MBean: "+on,e);
				}
				
			}
			
			state=ServerState.STOPPED;
			runningTask.cancel();
			runningTask=null;
			
		}
		
	}


}
