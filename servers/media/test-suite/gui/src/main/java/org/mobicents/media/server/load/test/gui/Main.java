/*
 * Main.java
 */

package org.mobicents.media.server.load.test.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.mobicents.media.server.load.test.EchoLoadTest;

/**
 * The main class of the application.
 */
public class Main extends SingleFrameApplication {

	private Logger logger = Logger.getLogger(Main.class);

	/**
	 * At startup create and show the main frame of the application.
	 */
	@Override
	protected void startup() {
		show(new MainView(this));
	}

	/**
	 * This method is to initialize the specified window by injecting resources.
	 * Windows shown in our application come fully initialized from the GUI
	 * builder, so this additional configuration is not needed.
	 */
	@Override
	protected void configureWindow(java.awt.Window root) {
	}

	/**
	 * A convenient static getter for the application instance.
	 * 
	 * @return the instance of Main
	 */
	public static Main getApplication() {
		return Application.getInstance(Main.class);
	}

	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args) {
		System.out.println("*********Launching Test *************");
		Main main = new Main();
		main.test(args);

	}

	public void test(String[] args) {
		InputStream inStreamLog4j = getClass().getClassLoader().getResourceAsStream("log4j.properties");
		Properties propertiesLog4j = new Properties();
		try {
			propertiesLog4j.load(inStreamLog4j);
			PropertyConfigurator.configure(propertiesLog4j);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.debug("log4j configured");

		launch(Main.class, args);
	}

}
