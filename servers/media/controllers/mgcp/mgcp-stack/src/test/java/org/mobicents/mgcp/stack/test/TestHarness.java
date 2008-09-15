package org.mobicents.mgcp.stack.test;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Properties;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class TestHarness extends TestCase {
	
	protected static final String ABORT_ON_FAIL = "org.mobicents.mgcp.stack.test.ABORT_ON_FAIL";
	
	protected static final String LOG_FILE_NAME = "org.mobicents.mgcp.stack.test.LOG_FILE";
	
	protected static final String LOCAL_ADDRESS = "127.0.0.1";
	
	protected static final int CA_PORT = 2724;
	
	protected static final int MGW_PORT = 2729;
	
	protected static String logFileName = "mgcplog.txt";
	
	protected static boolean abortOnFail  = true;
	
	private static boolean testPassed = true;
	
	protected static int testCounter;
	
	private static Logger logger = Logger.getLogger("mgcp.test");
	
	private static String currentMethodName;

	private static String currentClassName;
	
	protected TestResult testResult;
	
	//protected static Appender console = new ConsoleAppender(new SimpleLayout());
	
	public void init(){
		try {
			Properties tckProperties = new Properties();
			
			
			InputStream is = getClass().getResourceAsStream("/test.properties");
			System.out.println("Input Straem = "+ is);
			
			
			tckProperties.load(is);
			
			

			Enumeration props = tckProperties.propertyNames();
			while (props.hasMoreElements()) {
				String propname = (String) props.nextElement();
				System.setProperty(propname, tckProperties
						.getProperty(propname));
			}

			
			String flag = System.getProperties().getProperty(ABORT_ON_FAIL);

			String lf = System.getProperties().getProperty(LOG_FILE_NAME);
			if (lf != null)
				logFileName = lf;
			abortOnFail = (flag != null && flag.equalsIgnoreCase("true"));

			// JvB: init log4j
			//PropertyConfigurator.configure("log4j.properties");
			
			BasicConfigurator.configure();

			// If already created a print writer then just use it.
			if (lf != null)
				logger.addAppender(new FileAppender(new SimpleLayout(),
						logFileName));
			else
				logger.addAppender(new FileAppender(new SimpleLayout(),
						"testoutput.txt"));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}
	
	protected TestHarness() {

	}
	
	public TestHarness(String name) {
		this(name, false); // default: disable auto-dialog
	}
	
	protected TestHarness(String name, boolean autoDialog) {
		super(name);
		this.testResult = new TestResult();
		init();
	}	
	
	
	private static void println(String messageToPrint) {
		logger.info(messageToPrint);
	}
	
	private static void logSuccess(String message) {
		testCounter++;
		Throwable throwable = new Throwable();
		StackTraceElement frameset[] = throwable.getStackTrace();
		StackTraceElement frame = frameset[2];
		String className = frame.getClassName();
		//int ind = className.lastIndexOf(".");
		//if (ind != -1) {
		//	className = className.substring(ind + 1);
		//}
		
		logger.info(className + ":" + frame.getMethodName() + "(" +
						frame.getFileName() + ":"+ frame.getLineNumber() + ")" +  " : Status =  passed ! ");
		String methodName = frame.getMethodName();

		if (!(currentMethodName != null && methodName.equals(currentMethodName) && currentClassName
				.equals(className))) {
			currentClassName = className;
			currentMethodName = methodName;
			System.out.println("\n");
			System.out.print(currentClassName + ":" + currentMethodName);
		}
	}

	

	private static void logSuccess() {
		Throwable throwable = new Throwable();
		StackTraceElement frameset[] = throwable.getStackTrace();
		StackTraceElement frame = frameset[2];
		String className = frame.getClassName();
		//int ind = className.lastIndexOf(".");
		//if (ind != -1) {
		//	className = className.substring(ind + 1);
		//}
		logger.info(className + ":" + frame.getMethodName() + ": Status =  passed ! ");
		String methodName = frame.getMethodName();

		if (!(currentMethodName != null && methodName.equals(currentMethodName) && currentClassName
				.equals(className))) {
			currentClassName = className;
			currentMethodName = methodName;
			System.out.println("\n");
			System.out.print(currentClassName + ":" + currentMethodName);
		}
	}
	
	

	private static void logFailureDetails(String reason) {
		Throwable throwable = new Throwable();
		StackTraceElement frameset[] = throwable.getStackTrace();
		StackTraceElement frame = frameset[2];
		String className = frame.getClassName();
		logFailure(className, frame.getMethodName(), reason);
		
	}

	

	private static void logFailure(String className, String methodName,
			String reason) {
		println(" Test in function " + className
				+ ":" + methodName + " failed because of " + reason);
		
		StringWriter stringWriter = new StringWriter();
		new Exception().printStackTrace(new PrintWriter(stringWriter));
		println(stringWriter.getBuffer().toString());
		
		testPassed = false;
		if (abortOnFail) {
			new Exception().printStackTrace();
			System.exit(0);
		}
	}

	private static void logFailure(String reason) {
		logFailureDetails(reason);
	}

	public static void assertTrue(boolean cond) {
		if (cond) {
			logSuccess();
		} else {
			logFailure("assertTrue failed");
		}
		if (!cond) {
			new Exception().printStackTrace();
			fail("assertion failure");
		}

		TestCase.assertTrue(cond);
	}

	public static void assertTrue(String diagnostic, boolean cond) {
		if (cond) {
			logSuccess("assertTrue " + diagnostic);
		} else {
			logFailure(diagnostic);
		}
		if (!cond) {
			new Exception(diagnostic).printStackTrace();
			fail(diagnostic + " : Assertion Failure ");

		}

		TestCase.assertTrue(diagnostic, cond);
	}

	public static void assertEquals(Object me, Object him) {
		if (me == him) {
			logSuccess();
		} else if (me == null && him != null) {
			logFailure("assertEquals failed");

		} else if (me != null && him == null) {
			logFailure("assertEquals failed");

		} else if (!me.equals(him)) {
			logFailure("assertEquals failed");

		}
		TestCase.assertEquals(me, him);
	}

	public static void assertEquals(String me, String him) {
		if (me == him) {
			logSuccess();
		} else if (me == null && him != null) {
			logFailure("assertEquals failed");

		} else if (me != null && him == null) {
			logFailure("assertEquals failed");

		} else if (!me.equals(him)) {
			logFailure("assertEquals failed");

		}
		TestCase.assertEquals(me, him);
	}

	public static void assertEquals(String reason, Object me, Object him) {
		if (me == him) {
			logSuccess("assertEquals : " + reason);
		} else if (me == null && him != null) {
			logFailure("assertEquals failed");
		} else if (me != null && him == null) {
			logFailure("assertEquals failed");
		} else if (!me.equals(him)) {
			logFailure(reason);
		}
		TestCase.assertEquals(reason, me, him);
	}

	public static void assertEquals(String reason, String me, String him) {
		if (me == him) {
			logSuccess("assertEquals " + reason);
		} else if (me == null && him != null) {
			logFailure("assertEquals failed");
		} else if (me != null && him == null) {
			logFailure("assertEquals failed");
		} else if (!me.equals(him)) {
			logFailure("assertEquals failed");
		}
		TestCase.assertEquals(reason, me, him);

	}

	public static void assertNotNull(String reason, Object thing) {
		if (thing != null) {
			logSuccess("assertNotNull " + reason);
		} else {
			logFailure(reason);
		}
		TestCase.assertNotNull(reason, thing);
	}

	public static void assertNull(String reason, Object thing) {
		if (thing == null) {
			logSuccess("assertNull " + reason);
		} else {
			logFailure(reason);
		}
		TestCase.assertNull(reason, thing);
	}

	public static void assertSame(String diagnostic, Object thing,
			Object thingie) {
		if (thing == thingie) {
			logSuccess("assertSame " + diagnostic);
		} else {
			logFailure(diagnostic);
		}
		TestCase.assertSame(diagnostic, thing, thingie);
	}

	public static void fail(String message) {
		logFailure(message);
			
		TestCase.fail(message);
	}
	
	public static void fail(String message, Exception ex) {
		logFailure(message);
		logger.error(message,ex);
	}

	public static void fail() {
		logFailure("Unknown reason for failure. Check logs for more info.");
		new Exception().printStackTrace();
		TestCase.fail();
	}	
	
	public void logTestCompleted() {
		
		logger.info(this.getName() + " Completed");
		
	}
	
	public void logTestCompleted(String info) {
		logger.info(this.getName() + ":" + info +" Completed");
			
	}
	
	public void setUp() throws Exception {
		testPassed = true;
	}

	public void tearDown() throws Exception {
		assertTrue("Test failed. See log for details.", testPassed);
	}	

}
