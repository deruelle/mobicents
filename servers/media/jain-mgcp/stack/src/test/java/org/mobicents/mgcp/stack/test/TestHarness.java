package org.mobicents.mgcp.stack.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;

public class TestHarness extends TestCase {

	protected static final String ABORT_ON_FAIL = "org.mobicents.mgcp.stack.test.ABORT_ON_FAIL";

	protected static final String LOG_FILE_NAME = "org.mobicents.mgcp.stack.test.LOG_FILE";

	protected static final String LOCAL_ADDRESS = "127.0.0.1";

	protected static final int CA_PORT = 2724;

	protected static final int MGW_PORT = 2729;

	protected static String logFileName = "mgcplog.txt";

	protected static boolean abortOnFail = true;

	private static boolean testPassed = true;

	protected static int testCounter;

	private static Logger logger = Logger.getLogger("mgcp.test");

	private static String currentMethodName;

	private static String currentClassName;

	protected TestResult testResult;

	// protected static Appender console = new ConsoleAppender(new
	// SimpleLayout());

	public void init() {
		try {
			Properties tckProperties = new Properties();

			InputStream is = getClass().getResourceAsStream("/test.properties");
			System.out.println("Input Stream = " + is);

			tckProperties.load(is);

			Enumeration props = tckProperties.propertyNames();
			while (props.hasMoreElements()) {
				String propname = (String) props.nextElement();
				System.setProperty(propname, tckProperties.getProperty(propname));
			}

			String flag = System.getProperties().getProperty(ABORT_ON_FAIL);

			String lf = System.getProperties().getProperty(LOG_FILE_NAME);
			if (lf != null)
				logFileName = lf;
			abortOnFail = (flag != null && flag.equalsIgnoreCase("true"));

			// JvB: init log4j
			// PropertyConfigurator.configure("log4j.properties");

			InputStream inStreamLog4j = getClass().getResourceAsStream("/log4j.properties");
			Properties propertiesLog4j = new Properties();
			try {
				propertiesLog4j.load(inStreamLog4j);
				PropertyConfigurator.configure(propertiesLog4j);
			} catch (IOException e) {
				e.printStackTrace();
				BasicConfigurator.configure();
			}

			logger.debug("log4j configured");

			// If already created a print writer then just use it.
			try{
			if (lf != null)
				logger.addAppender(new FileAppender(new SimpleLayout(), logFileName));
			else
				logger.addAppender(new FileAppender(new SimpleLayout(), "testoutput.txt"));
			} catch(FileNotFoundException fnfe){
				
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	protected TestHarness() {
		init();

	}

	public TestHarness(String name) {
		this(name, false); // default: disable auto-dialog
	}

	protected TestHarness(String name, boolean autoDialog) {
		super(name);
		this.testResult = new TestResult();
		init();
	}



	public void logTestCompleted() {

		logger.info(this.getName() + " Completed");

	}

	public void logTestCompleted(String info) {
		logger.info(this.getName() + ":" + info + " Completed");

	}

	public void setUp() throws Exception {
		testPassed = true;
	}

	public void tearDown() throws Exception {
		assertTrue("Test failed. See log for details.", testPassed);
	}

}
