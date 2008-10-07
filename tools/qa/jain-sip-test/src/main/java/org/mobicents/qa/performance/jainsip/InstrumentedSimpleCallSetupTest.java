package org.mobicents.qa.performance.jainsip;

import java.text.ParseException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.ListeningPoint;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionDoesNotExistException;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.mobicents.qa.performance.jainsip.governor.*;
import org.mobicents.qa.performance.jainsip.inspector.TestInspector;
import org.mobicents.qa.performance.jainsip.inspector.TestProbe;
import org.mobicents.qa.performance.jainsip.util.SimpleCallSetupTestFileUtils;
import org.mobicents.qa.performance.jainsip.util.SippController;

public class InstrumentedSimpleCallSetupTest implements SipListener {

    private TestProbe probe;

    private AddressFactory addressFactory;
    private MessageFactory messageFactory;
    private HeaderFactory headerFactory;
    private SipStack sipStack;
    private SipProvider sipProvider;
    private String transport = "udp";
    private boolean sendBye;

    private Timer myTimer;
    private int timeout;

    public InstrumentedSimpleCallSetupTest(String ip, String port, boolean debug, boolean sendBye, TestProbe probe) {

	SipFactory sipFactory = SipFactory.getInstance();
	sipFactory.setPathName("gov.nist");

	Properties properties = new Properties();
	properties.setProperty("javax.sip.STACK_NAME", "mobicents-standalone-test");
	properties.setProperty("gov.nist.javax.sip.MAX_SERVER_TRANSACTIONS", "500000");
	properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "debuglog.txt");
	properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "stack_log.txt");
	properties.setProperty("javax.sip.AUTOMATIC_DIALOG_SUPPORT", "false");
	properties.setProperty("gov.nist.javax.sip.THREAD_POOL_SIZE", "64");
	properties.setProperty("gov.nist.javax.sip.REENTRANT_LISTENER", "true");
	properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", debug ? "32" : "0");

	try {
	    sipStack = sipFactory.createSipStack(properties);
	    System.out.println((new StringBuilder("\n")).append("SipStack ").append(sipStack).append(" created.\n").toString());
	} catch (PeerUnavailableException e) {
	    e.printStackTrace();
	    System.err.println(e.getMessage());
	    throw new RuntimeException("Stack failed to initialize");
	}
	try {
	    headerFactory = sipFactory.createHeaderFactory();
	    addressFactory = sipFactory.createAddressFactory();
	    messageFactory = sipFactory.createMessageFactory();

	    ListeningPoint listeningPoint = sipStack.createListeningPoint(ip, Integer.parseInt(port), transport);
	    sipProvider = sipStack.createSipProvider(listeningPoint);
	    sipProvider.addSipListener(this);

	    this.sendBye = sendBye;
	    this.probe = probe;
	    if (sendBye) {
		this.myTimer = new Timer();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}
    }

    protected Response createResponse(Request request, int responseType) throws ParseException {
	SipURI sipUri = (SipURI) request.getRequestURI();
	SipURI sipAddress = addressFactory.createSipURI(sipUri.getUser(), sipUri.getHost());
	Response response = messageFactory.createResponse(responseType, request);
	response.addHeader(headerFactory.createContactHeader(addressFactory.createAddress(sipAddress)));
	return response;
    }

    public void processRequest(RequestEvent requestEvent) {
	String method = requestEvent.getRequest().getMethod();
	if (Request.INVITE.equalsIgnoreCase(method)) {
	    try {
		probe.dialogCreated();

		ServerTransaction serverTransaction = sipProvider.getNewServerTransaction(requestEvent.getRequest());
		Dialog dialog = sipProvider.getNewDialog(serverTransaction);
		serverTransaction.sendResponse(createResponse(requestEvent.getRequest(), Response.OK));

		if (sendBye) {
		    myTimer.schedule(new ByeTimerTask(dialog, sipProvider), timeout * 1000);
		}

	    } catch (Exception e) {
		probe.dialogTerminatedUnexpectedly();
		e.printStackTrace();
	    }
	} else if (Request.BYE.equalsIgnoreCase(method)) {
	    try {
		probe.dialogTerminated();

		requestEvent.getServerTransaction().sendResponse(createResponse(requestEvent.getRequest(), Response.OK));
	    } catch (Exception e) {
		probe.dialogTerminatedUnexpectedly();
		e.printStackTrace();
	    }
	}
    }

    public void processResponse(ResponseEvent responseevent) {
    }

    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
    }

    public void processIOException(IOExceptionEvent exceptionEvent) {
	probe.dialogTerminatedUnexpectedly();
    }

    public void processTimeout(TimeoutEvent timeoutEvent) {
	probe.dialogTerminatedUnexpectedly();
    }

    public void processTransactionTerminated(TransactionTerminatedEvent transactionterminatedevent) {
    }

    public static void main(String args[]) {

	String ip = "127.0.0.1";
	String port = "5060";
	// String timeout = "60";
	boolean debug = false;
	boolean sendBye = false;

	for (int i = 0; i < args.length; i++) {
	    if ("-i".equals(args[i])) {
		ip = args[++i];
		System.out.println("IP flag; " + ip);
	    } else if ("-p".equals(args[i])) {
		port = args[++i];
		System.out.println("Port flag: " + port);
	    } else if ("-debug".equals(args[i])) {
		debug = true;
		System.out.println("Debug flag");
	    } else if ("-sendbye".equals(args[i])) {
		sendBye = true;
		System.out.println("Send BYE flag");
		// } else if ("-t".equals(args[i])) {
		// timeout = args[++i];
		// System.out.println("Timeout flag: " + timeout);
	    } else if ("--help".equals(args[i])) {
		System.out.println("Simple Call Setup Test. Available options: ");
		System.out.println("-i ip --- interface to bind (default 127.0.0.1)");
		System.out.println("-p port --- port to bind (default 5060)");
		System.out.println("-d --- produces debug log (default false)");
		System.out.println("-sendbye --- application sends bye instead of UA (default false) ");
		// System.out.println("-t timeout --- timeout when using -sendbye option (default false)");
		System.out.println("--help --- prints this help and exits");
		return;
	    }
	}

	TestInspector inspector = new TestInspector();
	new InstrumentedSimpleCallSetupTest(ip, port, debug, sendBye, inspector);

	String control = "" + ((int) Math.round(Math.random() * 50000) + 10000);
	System.out.println("Random control port for sipp: " + control + "\n");

	if (sendBye) {
	    SimpleCallSetupTestFileUtils.extract("uac-sendbye.xml", ".");
	    SimpleCallSetupTestFileUtils.writeSippStartFile("test.sh", ".", "uac-sendbye.xml", ip, port, control);
	} else {
	    SimpleCallSetupTestFileUtils.extract("uac.xml", ".");
	    SimpleCallSetupTestFileUtils.writeSippStartFile("test.sh", ".", "uac.xml", ip, port, control);
	}
	SimpleCallSetupTestFileUtils.extract("download-and-compile-sipp.sh", ".");
	SimpleCallSetupTestFileUtils.executize(".");

	new NoRateGovernor(inspector, new SippController(ip, control));
	// new FixedRateGovernor(inspector, new SippController(ip, control), 100);
	// new LinearRateGovernor(inspector, new SippController(ip, control), 10, 1);
	// new ExponentialLinearRateGovernor(inspector, new SippController(ip, control), 4, 1.2, 1);
	// new MyFirstHeuristicsGovernor(inspector, new SippController(ip, control));
    }

    private class ByeTimerTask extends TimerTask {

	private final Dialog dialog;
	private final SipProvider provider;

	public void run() {
	    try {
		if (dialog != null) {
		    dialog.sendRequest(provider.getNewClientTransaction(dialog.createRequest(Request.BYE)));
		} else {
		    System.out.println("Avoding sending BYE on null dialog");
		}
	    } catch (TransactionDoesNotExistException e) {
		e.printStackTrace();
	    } catch (TransactionUnavailableException e) {
		e.printStackTrace();
	    } catch (SipException e) {
		e.printStackTrace();
	    }
	}

	public ByeTimerTask(Dialog dialog, SipProvider provider) {
	    super();
	    this.dialog = dialog;
	    this.provider = provider;
	}
    }

}