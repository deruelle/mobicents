package org.mobicents.examples.wakeupbot;
import java.util.Properties;

import java.util.Properties;
import java.util.logging.Logger;

import org.mobicents.slee.resource.xmpp.XmppResourceAdaptor;
public final class BotConf {
	private String serviceName = "gmail.com";     
	private  String username = ""; 
	private  String password = "";
	private String resource = "MobicentsWakeUpBot";
	private String connectionID = "org.mobicents.examples.wakeupbot.WakeUpBotSbb";
	private  String serviceHost = "talk.google.com";
	private  int servicePort = 5222;
	private boolean loadSuccessful=false;
	private static Logger logger;
	private static BotConf conf=null;
	static
	{
		logger = Logger.getLogger(XmppResourceAdaptor.class.toString());
	}
	private BotConf()
	{
		Properties props=new Properties();
		try
		{
			props.load(getClass().getResourceAsStream("google-wakupbot.properties"));
		}catch(Exception IOE)
		{
			logger.info("FAILED TO LOAD: google-wakupbot.properties");
			
		}
		String tmp=props.getProperty("org.mobicents.wakeupbot.user");
		if(tmp==null)
		{
			logger.info("PROPERTY \"org.mobicents.wakeupbot.user\" cant be null");
		}else
			username=tmp;
		
		tmp=props.getProperty("org.mobicents.wakeupbot.pass");
		if(tmp==null)
		{
			logger.info("PROPERTY \"org.mobicents.wakeupbot.pass\" cant be null");
		}else
			password=tmp;
		
		logger.info("LOADING OTHER PROPERTIES!!");
		tmp=props.getProperty("org.mobicents.wakeupbot.serviceName");
		if(tmp==null)
		{
			logger.info("PROPERTY \"org.mobicents.wakeupbot.serviceName\" is null. DEFAULT:"+serviceName+"!!!");
		}else
			serviceName=tmp;
		
		tmp=props.getProperty("org.mobicents.wakeupbot.resource");
		if(tmp==null)
		{
			logger.info("PROPERTY \"org.mobicents.wakeupbot.resource\" is null. DEFAULT:"+resource+"!!!");
		}else
			resource=tmp;
		
		tmp=props.getProperty("org.mobicents.wakeupbot.connectionID");
		if(tmp==null)
		{
			logger.info("PROPERTY \"org.mobicents.wakeupbot.connectionID\" is null. DEFAULT:"+connectionID+"!!!");
		}else
			connectionID=tmp;
		
		tmp=props.getProperty("org.mobicents.wakeupbot.serviceHost");
		if(tmp==null)
		{
			logger.info("PROPERTY \"org.mobicents.wakeupbot.serviceHost\" is null. DEFAULT:"+serviceHost+"!!!");
		}else
			serviceHost=tmp;
		
		tmp=props.getProperty("org.mobicents.wakeupbot.servicePort");
		if(tmp==null)
		{
			logger.info("PROPERTY \"org.mobicents.wakeupbot.servicePort\" is null. DEFAULT:"+servicePort+"!!!");
		}else
		{
			int tmpInt=Integer.parseInt(tmp);
			servicePort=tmpInt;
		}
	}
	public static BotConf getBonConf()
	{
		if(conf==null)
			conf=new BotConf();
		
		return conf;
	}
	/**
	 * 
	 * @return password which will be used for UID returned by {@link  #getUsername()}. <br>Default <b>""</b>. 
	 */
	public  String getPassword() {
		return password;
	}
	private  void setPassword(String password) {
		password = password;
	}
	/**
	 * 
	 * @return Resource identifier. Fully qualified identifier: UID@serviceName/resource <br>Default <b>MobicentWakeUpBot</b>.
	 */
	public  String getResource() {
		return resource;
	}
	private  void setResource(String resource) {
		resource = resource;
	}
	/**
	 * 
	 * @return DNS name of host which provides service. <br>Default <b>talk.google.com</b>.
	 */
	public  String getServiceHost() {
		return serviceHost;
	}
	private  void setServiceHost(String serviceHost) {
		serviceHost = serviceHost;
	}
	/**
	 * 
	 * @return Service name. Serice name identifies node within XMPP protocol. <br>Default <b>gmail.com</b>.
	 */
	public  String getServiceName() {
		return serviceName;
	}
	private  void setServiceName(String serviceName) {
		serviceName = serviceName;
	}
	/**
	 * 
	 * @return Port number on which serviceHost is listening. <br>Default <b>5222</b>.
	 */
	public  int getServicePort() {
		return servicePort;
	}
	private  void setServicePort(int servicePort) {
		servicePort = servicePort;
	}
	/**
	 * 
	 * @return Username whihc will identify bot. It has to be valid user. <br>Default <b>""</b>.
	 */
	public  String getUsername() {
		return username;
	}
	private  void setUsername(String username) {
		username = username;
	}

	
	public  String getConnectionID() {
		return connectionID;
	}
	private  void setConnectionID(String connectionID) {
		connectionID = connectionID;
	}
	public boolean isLoadSuccessful()
	{
		return loadSuccessful;
	}
}
