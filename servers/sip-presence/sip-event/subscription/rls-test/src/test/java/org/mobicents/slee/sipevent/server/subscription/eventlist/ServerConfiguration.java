package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.util.Properties;

import org.openxdm.xcap.client.XCAPClient;
import org.openxdm.xcap.client.XCAPClientImpl;

public class ServerConfiguration {

	private static Properties properties = new Properties();
	
	public static String SERVER_HOST;
	public static int SERVER_PORT;
	public static String SERVER_XCAP_ROOT;
	
	static {
		ServerConfiguration serverConfiguration = new ServerConfiguration();
		try {
			//properties.load(serverConfiguration.getClass().getResourceAsStream("configuration.properties"));
			SERVER_HOST = getServerHost();
			SERVER_PORT = getServerPort();
			SERVER_XCAP_ROOT = getServerXcapRoot();
		} catch (Exception e) {
			SERVER_HOST = "127.0.0.1";
			SERVER_PORT = 8080;
			SERVER_XCAP_ROOT = "/mobicents";
		}
		
	}
	
	private static String getServerHost() {
		return properties.getProperty("SERVER_HOST", "127.0.0.1");
	}
	
	private static int getServerPort() {
		int serverPort = 8080;
		String serverPortProperty = properties.getProperty("SERVER_PORT");
		if (serverPortProperty != null) {
			try {
				serverPort = Integer.parseInt(serverPortProperty);
			}
			catch (Exception e) {
				// ignore
			}
		}
		return serverPort;
	}

	private static String getServerXcapRoot() {
		return properties.getProperty("SERVER_XCAP_ROOT", "/mobicents");
	}

	public static XCAPClient getXCAPClientInstance() throws InterruptedException {
		return new XCAPClientImpl(SERVER_HOST,SERVER_PORT,SERVER_XCAP_ROOT);
	}
	
}
