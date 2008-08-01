package org.mobicents.media.server.load.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class EchoLoadTest {

	private Logger logger = Logger.getLogger(EchoLoadTest.class);

	public static String AudioFileToPlay = null;

	public static int Jitter = -1;

	public static int Packetization = -1;

	public static String EndpointBindAddress = null;

	public static int EndpointLowPortNumber = -1;

	public static int EndpointHighPortNumber = -1;
	
	public static String EndpointDTMFDetector = null;

	public static int ClientMGCPStackPort = -1;

	public static int ServerMGCPStackPort = -1;

	public static String JBossBindAddress = null;

	int count = 0;

	private ExecutorService pool;

	// The number of UA that will be started simultaneously
	int UserAgentsCount = -1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		EchoLoadTest loadTest = new EchoLoadTest();
		try {
			loadTest.test();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void test() throws IOException {

		InputStream inStreamLog4j = getClass().getClassLoader().getResourceAsStream("log4j.properties");
		Properties propertiesLog4j = new Properties();
		try {
			propertiesLog4j.load(inStreamLog4j);
			PropertyConfigurator.configure(propertiesLog4j);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.debug("log4j configured");		
		logger.info("Main Thread will go to sleep for 5 min. Kill me if required");

		InputStream inStreamLoadTest = getClass().getClassLoader().getResourceAsStream("mmsloadtest.properties");
		Properties propertiesLoadTest = new Properties();

		propertiesLoadTest.load(inStreamLoadTest);

		AudioFileToPlay = propertiesLoadTest.getProperty("audio.file");
		Jitter = Integer.parseInt(propertiesLoadTest.getProperty("endpoint.jitter"));
		Packetization = Integer.parseInt(propertiesLoadTest.getProperty("endpoint.packetization.period"));
		EndpointBindAddress = propertiesLoadTest.getProperty("endpoint.bind.address");
		EndpointLowPortNumber = Integer.parseInt(propertiesLoadTest.getProperty("endpoint.low.port.number"));
		EndpointHighPortNumber = Integer.parseInt(propertiesLoadTest.getProperty("endpoint.high.port.number"));
		
		EndpointDTMFDetector = propertiesLoadTest.getProperty("endpoint.dtmf.detector");

		ClientMGCPStackPort = Integer.parseInt(propertiesLoadTest.getProperty("client.mgcp.stack.port"));

		ServerMGCPStackPort = Integer.parseInt(propertiesLoadTest.getProperty("server.mgcp.ra.stack.port"));

		JBossBindAddress = propertiesLoadTest.getProperty("jboss.bind.address");
		
		UserAgentsCount = Integer.parseInt(propertiesLoadTest.getProperty("user.agents"));

		logger.info("*********************************************************************");

		logger.info("MMS Load Test Configuration");
		logger.info("");
		logger.info("AudioFileToPlay = " + AudioFileToPlay);
		logger.info("UserAgentsCount = " + UserAgentsCount);
		logger.info("Jitter = " + Jitter);
		logger.info("Packetization = " + Packetization);
		logger.info("EndpointBindAddress = " + EndpointBindAddress);
		logger.info("EndpointLowPortNumber = " + EndpointLowPortNumber);
		logger.info("EndpointHighPortNumber = " + EndpointHighPortNumber);
		logger.info("EndpointDTMFDetector = " + EndpointDTMFDetector);
		logger.info("ClientMGCPStackPort = " + ClientMGCPStackPort);
		logger.info("ServerMGCPStackPort = " + ServerMGCPStackPort);
		logger.info("JBossBindAddress = " + JBossBindAddress);

		logger.info("*********************************************************************");

		// pool = Executors.newFixedThreadPool(poolSize);
		// for (int count = 0; count < poolSize; count++) {
		// UA ua = new UA(count);
		// pool.execute(ua);
		// }
		//
		// pool.shutdown();
		// logger.info("Shutdown the pool Executor");

		Thread t[] = new Thread[UserAgentsCount];
		for (int i = 0; i < UserAgentsCount; i++) {
			t[i] = new Thread(new UA(i));
			t[i].start();
			try {
				t[i].join();
			} catch (InterruptedException e) {
				logger.error("InterruptedException ", e);
			}
		}

		try {
			Thread.sleep(1000 * 60 * 5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error("InterruptedException ", e);
		}

	}

}
