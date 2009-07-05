package org.mobicents.slee.xdm.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.mx.util.MBeanServerLocator;

public class ServerConfiguration {
	
	public static String SERVER_HOST;
	public static int SERVER_PORT;
	public static String SCHEME_AND_AUTHORITY_URI;
	public static String XCAP_ROOT;
	static {
		ServerConfiguration serverConfiguration = new ServerConfiguration();
		Properties properties = new Properties();
		try {
			properties.load(serverConfiguration.getClass().getResourceAsStream("configuration.properties"));
		} catch (IOException e) {
			// ignore
		}
		
		Integer port = null;
		try {
		MBeanServer server = MBeanServerLocator.locateJBoss(); 
		ObjectName query = new ObjectName("jboss.web:type=Connector,*"); // valid query 
		Set<?> objectNames = server.queryNames(query, null); 
		for (Iterator<?> i = objectNames.iterator(); i.hasNext();) {
			ObjectName o = (ObjectName) i.next();
			String protocol = (server.getAttribute(o, "protocol")).toString();
			if(protocol.startsWith("HTTP")){
				port = (Integer) server.getAttribute(o, "port");
			}
		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (port != null) {
			SERVER_PORT = port.intValue();
		}
		else {
			SERVER_PORT = 8080;
		}
		
		SERVER_HOST = System.getProperty("bind.address","127.0.0.1");
		
		SCHEME_AND_AUTHORITY_URI = "http://"+SERVER_HOST+":"+SERVER_PORT;
		
		XCAP_ROOT = properties.getProperty("XCAP_ROOT", "/mobicents");
		
	}
	
}
