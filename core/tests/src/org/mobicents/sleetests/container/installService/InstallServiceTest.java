package org.mobicents.sleetests.container.installService;

import java.rmi.RemoteException;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;




import com.opencloud.sleetck.lib.AbstractSleeTCKTest;

import com.opencloud.sleetck.lib.resource.TCKActivityID;
import com.opencloud.sleetck.lib.resource.TCKSbbMessage;

import com.opencloud.sleetck.lib.resource.testapi.TCKResourceListener;
import com.opencloud.sleetck.lib.resource.testapi.TCKResourceTestInterface;

import com.opencloud.sleetck.lib.testutils.BaseTCKResourceListener;
import com.opencloud.sleetck.lib.testutils.FutureResult;

import org.mobicents.slee.container.component.ComponentKey;
import org.mobicents.slee.container.component.ResourceAdaptorIDImpl;
import org.mobicents.slee.container.component.ServiceIDImpl;
import org.mobicents.slee.container.management.jmx.SleeCommandInterface;


public class InstallServiceTest extends AbstractSleeTCKTest {
	

	public void setUp() throws Exception {
		super.setUp();

		getLog()
				.info(
						"\n========================\nConnecting to resource\n========================\n");
		TCKResourceListener resourceListener = new TestResourceListenerImpl();
		setResourceListener(resourceListener);
		/*
		 * Properties props = new Properties(); try {
		 * props.load(getClass().getResourceAsStream("sipStack.properties"));
		 *  } catch (IOException IOE) { logger.info("FAILED TO LOAD:
		 * sipStack.properties");
		 *  }
		 */

	}

	protected FutureResult result;

	

	/*
	 * protected void setResultPassed(String msg) throws Exception {
	 * logger.info("Success: " + msg);
	 * 
	 * HashMap sbbData = new HashMap(); sbbData.put("result", Boolean.TRUE);
	 * sbbData.put("message", msg);
	 * TCKSbbUtils.getResourceInterface().sendSbbMessage(sbbData); }
	 * 
	 * protected void setResultFailed(String msg) throws Exception {
	 * logger.info("Failed: " + msg);
	 * 
	 * HashMap sbbData = new HashMap(); sbbData.put("result", Boolean.FALSE);
	 * sbbData.put("message", msg);
	 * TCKSbbUtils.getResourceInterface().sendSbbMessage(sbbData); }
	 */

	private class TestResourceListenerImpl extends BaseTCKResourceListener {

		public synchronized void onSbbMessage(TCKSbbMessage message,
				TCKActivityID calledActivity) throws RemoteException {
			Map sbbData = (Map) message.getMessage();
			Boolean sbbPassed = (Boolean) sbbData.get("result");
			String sbbTestMessage = (String) sbbData.get("message");

			getLog().info(
					"Received message from SBB: passed=" + sbbPassed
							+ ", message=" + sbbTestMessage);

			if (sbbPassed.booleanValue()) {
				result.setPassed();
			} else {
				result.setFailed(0, sbbTestMessage);
			}
		}

		public void onException(Exception exception) throws RemoteException {
			getLog().warning("Received exception from SBB or resource:");
			getLog().warning(exception);
			result.setError(exception);
		}
	}

	private TCKActivityID tckActivityID = null;

	private String activityName = null;

	private String jnpHostURL="jnp://127.0.0.1:1099";
	private String raTypeDUName="sip-ra-type.jar";
	private String raDUName="sip-local-ra.jar";
	private String raLINK="SipRA";
	private String testServiceDUName="DummySbbService-DU.jar";
	private String raID="JSIPSTUB#net.java.slee.sip#1.2";
	private String serviceID="StateEventsPassedTestService#mobicents#0.1";
	private ComponentKey sid = new ComponentKey(this.serviceID);
	private ServiceIDImpl service = new ServiceIDImpl(sid);
	private ComponentKey raId = new ComponentKey(this.raID);
	private ResourceAdaptorIDImpl ra = new ResourceAdaptorIDImpl(raId);
	
	
	public void run(FutureResult result) throws Exception {
		this.result = result;

		TCKResourceTestInterface resource = utils().getResourceInterface();
		activityName = utils().getTestParams().getProperty("activityName");
		tckActivityID = resource.createActivity(activityName);

		
		
		Properties props=new Properties();
		try
		{
			getLog().info(" == LOADING PROPS FROM: test.properties ==");
			props.load(getClass().getResourceAsStream("test.properties"));
			jnpHostURL=props.getProperty("jnpHost",jnpHostURL);
			raTypeDUName=props.getProperty("raTypeDUName",raTypeDUName);
			raDUName=props.getProperty("raDUName",raDUName);
			raLINK=props.getProperty("raLINK",raLINK);
			testServiceDUName=props.getProperty("testServiceDUName",testServiceDUName);
			raID=props.getProperty("raID",raID);
			serviceID=props.getProperty("serviceID",serviceID);
			sid = new ComponentKey(this.serviceID);
			service = new ServiceIDImpl(sid);
			raId = new ComponentKey(this.raID);
			ra = new ResourceAdaptorIDImpl(raId);
			getLog().info(" == FINISHED LOADING PROPS ==");
		}catch(Exception IOE)
		{
			getLog().info("FAILED TO LOAD: test.properties");
			
		}
		
		
		utils()
				.getLog()
				.info(
						"\n===================\nSTARTING DEPLOYMENT IN FEW uS\n===================\nACTIVITY:"
								+ activityName
								+ "\n=======================================");

		String mcHOME = System.getProperty("MOBICENTS_HOME");
		if(mcHOME==null)
			result.setError(" == The System Property MOBICENTS_HOME is required, but does not exist!! ==");
				
		
		
		
		
		SleeCommandInterface SCI=new SleeCommandInterface(jnpHostURL);
		String dusPATH="file://"+mcHOME + "/tests/lib/container/";
		getLog().info(" == STARTIGN DEPLOYMENT ==");
		ArrayList errors=new ArrayList(2);
		boolean raTypeInstalled,raInstalled,raEntityCreated,raEntityActivated,raLinkCreated,serviceInstalled,serviceActivated;
		raTypeInstalled=raInstalled=raEntityActivated=raEntityCreated=raLinkCreated=serviceActivated=serviceInstalled=false;
		try{
			Object opResult=null;
			getLog().info(" == DEPOYING RA TYPE:"+dusPATH+raTypeDUName+" ==");
			opResult=SCI.invokeOperation("-install",dusPATH+raTypeDUName,null,null);
			getLog().info(" == DEPLOLYED RA TYPE:"+opResult+" ==");
			raTypeInstalled=true;
			getLog().info(" == DEPOYING RA :"+dusPATH+dusPATH+" ==");
			opResult=SCI.invokeOperation("-install",dusPATH+raDUName,null,null);
			getLog().info(" == DEPLOYED RA:"+opResult+" ==");
			raInstalled=true;
			getLog().info(" == CREATING RA ENTITY:"+ra+" ==");
			opResult=SCI.invokeOperation("-createRaEntity",ra.toString(),raLINK,null);
			getLog().info(" == RA ENTITY CREATED:"+opResult+" ==");
			raEntityCreated=true;
			getLog().info(" == ACTIVATING RA ENTITY:"+ra+" ==");
			opResult=SCI.invokeOperation("-activateRaEntity", raLINK, null, null);
			getLog().info(" == RA ENTITY ACTIVATED:"+opResult+" ==");
			raEntityActivated=true;
			getLog().info(" == CREATING RA LINK:"+raLINK+" ==");
			opResult=SCI.invokeOperation("-createRaLink", raLINK, raLINK, null);
			getLog().info(" == RA LINK CREATED:"+opResult+" ==");
			raLinkCreated=true;
			getLog().info(" == INSTALLING TEST SERVICE:"+dusPATH+testServiceDUName+" ==");
			opResult=SCI.invokeOperation("-install",dusPATH+testServiceDUName,null,null);
			getLog().info(" == SERVICE INSTALLED:"+opResult+" ==");
			serviceInstalled=true;
			
    		getLog().info(" == ACTIVATING SERVICE ==");
    		opResult=SCI.invokeOperation("-activateService", service.toString(), null, null);
			getLog().info(" == SERVICE ACTIVATED:"+opResult+" ==");
			serviceActivated=true;
		}catch(Exception e)
		{
			e.printStackTrace();
			errors.add(e);
		}
		
		try
		{
			Object opResult=null;
			if(serviceActivated)
			{
				opResult=SCI.invokeOperation("-deactivateService", service.toString(), null, null);
				serviceActivated=!serviceActivated;
				getLog().info(" == SERVICE DEACTIVATED:"+opResult+" ==");
			}
			if(serviceInstalled)
			{
				opResult=SCI.invokeOperation("-uninstall",dusPATH+testServiceDUName,null,null);
				serviceInstalled=!serviceInstalled;
				getLog().info(" == SERVICE UNINSTALLED:"+opResult+" ==");
			}
			if(raLinkCreated)
			{
				opResult=SCI.invokeOperation("-removeRaLink", raLINK, null, null);
				raLinkCreated=!raLinkCreated;
				getLog().info(" == RA LINK REMOVED:"+opResult+" ==");
			}
			if(raEntityActivated)
			{
				opResult=SCI.invokeOperation("-deactivateRaEntity", raLINK, null, null);
				raEntityActivated=!raEntityActivated;
				getLog().info(" == RA ENTITY DEACTIVATED:"+opResult+" ==");
			}
			if(raEntityCreated)
			{
				opResult=SCI.invokeOperation("-removeRaEntity",raLINK,null,null);
				raEntityCreated=!raEntityCreated;
				getLog().info(" == RA ENTITY REMOVED:"+opResult+" ==");
			}
			if(raInstalled)
			{
				opResult=SCI.invokeOperation("-uninstall",dusPATH+raDUName,null,null);
				raInstalled=!raInstalled;
				getLog().info(" == RA UNINSTALLED:"+opResult+" ==");
			}
			if(raTypeInstalled)
			{
				opResult=SCI.invokeOperation("-uninstall",dusPATH+raTypeDUName,null,null);
				raTypeInstalled=!raTypeInstalled;
				getLog().info(" == RA TYPE UNINSTALLED:"+opResult+" ==");
			}
		}catch(Exception e)
		{
			errors.add(e);
		}
		if(!errors.isEmpty())
		{
			getLog().info(" == SOME ERRORS OCURED!!: ==");
			StringBuffer sb=new StringBuffer(400);
			StringBuffer stackTraceSb=null;
			Iterator it=errors.iterator();
			int i=0;
			while(it.hasNext())
			{
				
				Exception ex=(Exception) it.next();
				StackTraceElement[] ste=ex.getStackTrace();
				if(ste!=null && ste.length >0)
				{
					stackTraceSb=new StringBuffer(1000);
					stackTraceSb.append("\n");
					for(int c=0;c<ste.length;c++)
						stackTraceSb.append("=>"+ste[c].getClassName()+" -> "+ste[c].getMethodName()+":"+ste[c].getLineNumber()+"\n");
				}
				sb.append("\n[#"+i+++"] -> "+ex.getMessage());
				if(stackTraceSb!=null)
					sb.append("\nTRACE:\n"+stackTraceSb+"\n");
				
				stackTraceSb=null;
			}
			getLog().info(" == ERROR INFO: ==\n"+sb);
		}
	}

}
