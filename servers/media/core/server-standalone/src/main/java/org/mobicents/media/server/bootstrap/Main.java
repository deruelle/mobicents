/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.media.server.bootstrap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.dependency.spi.Controller;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap;
import org.jboss.kernel.plugins.deployment.xml.BasicXMLDeployer;
import org.jboss.util.StringPropertyReplacer;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author <a href="mailto:amit.bhayani@jboss.com">amit bhayani</a>
 */
public class Main {

	private final static String HOME_DIR = "MMS_HOME";
	private final static String BOOT_URL = "/conf/bootstrap-beans.xml";
	private final static String LOG4J_URL = "/conf/log4j.properties";
	public static final String MMS_HOME = "mms.home.dir";
	public static final String MMS_MEDIA = "mms.media.dir";

	private static int index = 0;

	private Kernel kernel;
	private BasicXMLDeployer kernelDeployer;
	private Controller controller;

	private static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) throws Throwable {

		String homeDir = getHomeDir(args);
		System.setProperty(MMS_HOME, homeDir);
		System.setProperty(MMS_MEDIA, homeDir + File.separator +"media"+ File.separator);

		String Log4jURL = homeDir + LOG4J_URL;
		
		URL log4jurl = getURL(Log4jURL);		
		InputStream inStreamLog4j = log4jurl.openStream();
		Properties propertiesLog4j = new Properties();
		try {
			propertiesLog4j.load(inStreamLog4j);
			PropertyConfigurator.configure(propertiesLog4j);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("log4j configured");
		
		URL bootURL = getBootURL(args);
		Main main = new Main();

		logger.info("Booting from " + bootURL);
		main.boot(bootURL);		
	}

	/**
	 * Gets the Media Server Home directory.
	 * 
	 * @param args
	 *            the command line arguments
	 * @return the path to the home directory.
	 */
	private static String getHomeDir(String args[]) {
		if (System.getenv(HOME_DIR) == null) {
			if (args.length > index) {
				return args[index++];
			} else {
				return ".";
			}
		} else {
			return System.getenv(HOME_DIR);
		}
	}

	/**
	 * Gets the URL which points to the boot descriptor.
	 * 
	 * @param args
	 *            command line arguments.
	 * @return URL of the boot descriptor.
	 */
	private static URL getBootURL(String args[]) throws Exception {
		String bootURL = args.length > index ? args[index] : "${" + MMS_HOME + "}" + BOOT_URL;
		return getURL(bootURL);
	}

	protected void boot(URL bootURL) throws Throwable {
		BasicBootstrap bootstrap = new BasicBootstrap();
		bootstrap.run();

		registerShutdownThread();

		kernel = bootstrap.getKernel();
		kernelDeployer = new BasicXMLDeployer(kernel);

		kernelDeployer.deploy(bootURL);
		kernelDeployer.validate();

		controller = kernel.getController();
		start(kernel, kernelDeployer);
	}

	public void start(Kernel kernel, BasicXMLDeployer kernelDeployer) {
		ControllerContext context = controller.getInstalledContext("MainDeployer");
		if (context != null) {
			MainDeployer deployer = (MainDeployer) context.getTarget();
			deployer.start(kernel, kernelDeployer);
		}
	}

	public static URL getURL(String url) throws Exception {
		// replace ${} inputs
		url = StringPropertyReplacer.replaceProperties(url, System.getProperties());
		File file = new File(url);
		if (file.exists() == false) {
			throw new IllegalArgumentException("No such file: " + url);
		}
		return file.toURI().toURL();
	}

	protected void registerShutdownThread() {
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownThread()));
	}

	private class ShutdownThread implements Runnable {

		public void run() {
			System.out.println("Shutting down");
			kernelDeployer.shutdown();
			kernelDeployer = null;

			kernel.getController().shutdown();
			kernel = null;
		}
	}
}
