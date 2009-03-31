package org.mobicents.servers.media.examples.jsr309;

import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.header.CallID;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.media.mscontrol.MsControlFactory;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ProviderDoesNotExistException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.TimeoutEvent;
import javax.sip.Transaction;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import org.apache.log4j.Logger;
import org.mobicents.servers.media.examples.jsr309.proxies.ann.AnnProxy;
import org.mobicents.servers.media.examples.jsr309.proxies.echo.EchoProxy;

/**
 * 
 * @author amit bhayani
 * 
 */
public class Jsr309Example implements SipListener {

	private static Logger logger = Logger.getLogger(Jsr309Example.class);

	private static final String SIP_BIND_ADDRESS = "javax.sip.IP_ADDRESS";
	private static final String JSR309_DRIVER = "org.mobicents.Driver_1.0";

	private Map<String, CallProxy> callIdToProxy = null;
	private int port = 9060;

	private String stackAddress = "0.0.0.0";
	private int mgcpServerPort = 2727;
	private SipStackImpl sipStack = null;
	private SipFactory sipFactory;
	private SipProvider sipProvider;
	private MessageFactory messageFactory = null;
	private HeaderFactory headerFactory = null;
	private AddressFactory addressFactory = null;

	protected MsControlFactory msControlFactory = null;

	public Jsr309Example() {
		this.callIdToProxy = new HashMap<String, CallProxy>();

	}

	private void init() throws ProviderDoesNotExistException, SipException, InvalidArgumentException,
			TooManyListenersException {
		Properties sipProperties = readSipProperties();
		this.sipFactory = SipFactory.getInstance();
		this.sipFactory.setPathName("gov.nist"); // hmmm

		this.sipStack = (SipStackImpl) this.sipFactory.createSipStack(sipProperties);

		ListeningPoint lp = this.sipStack.createListeningPoint(stackAddress, this.port, "udp");
		this.sipProvider = this.sipStack.createSipProvider(lp);
		this.sipProvider.addSipListener(this);

		this.messageFactory = this.sipFactory.createMessageFactory();
		this.addressFactory = this.sipFactory.createAddressFactory();
		this.headerFactory = this.sipFactory.createHeaderFactory();
		this.sipStack.start();
		logger.info("Sipt stack started: " + lp.getIPAddress() + ":" + lp.getPort());

		Properties mgcpProperties = readMGCPProperties();

		msControlFactory = javax.media.mscontrol.spi.DriverManager.getFactory(JSR309_DRIVER, mgcpProperties);
		logger.info("JSR-309 Example started successfully ");
	}

	public SipFactory getSipFactory() {
		return sipFactory;
	}

	public SipProvider getSipProvider() {
		return sipProvider;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

	public HeaderFactory getHeaderFactory() {
		return headerFactory;
	}

	public AddressFactory getAddressFactory() {
		return addressFactory;
	}

	public MsControlFactory getMsControlFactory() {
		return this.msControlFactory;
	}

	// SIP Listener Impl
	public void processDialogTerminated(DialogTerminatedEvent dte) {
		CallProxy cp = getCallProxy(dte.getDialog().getCallId());
		if (cp != null) {
			cp.processDialogTerminated(dte);
		} else {
			logger.error("No call proxy for callID: " + dte.getDialog().getCallId().getCallId()
					+ " for timed out dialog");
		}
	}

	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processRequest(RequestEvent request) {
		try {
			CallProxy cp = getCallProxy(request.getRequest());
			if (cp == null) {

				Request r = request.getRequest();

				if (r.getMethod().compareTo(Request.INVITE) == 0) {
					cp = createCallProxy(r);
					this.addCallProxy(r, cp);
				} else {
					logger.warn("No call proxy for callID: " + request.getRequest().getHeader(CallID.NAME)
							+ " for process request.");
					return;
				}
			}
			if (cp != null) {
				cp.processRequest(request);
			} else {
				logger.error("No call proxy for callID: " + request.getRequest().getHeader(CallID.NAME)
						+ " for process request.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addCallProxy(Message msg, CallProxy cp) {
		CallID callIDHeader = (CallID) msg.getHeader(CallID.NAME);
		this.callIdToProxy.put(callIDHeader.getCallId(), cp);
	}

	protected CallProxy getCallProxy(Message msg) {
		CallID callIDHeader = (CallID) msg.getHeader(CallID.NAME);
		return this.getCallProxy(callIDHeader);
	}

	public CallProxy getCallProxy(CallIdHeader callId) {
		return this.callIdToProxy.get(callId.getCallId());
	}

	private CallProxy createCallProxy(Request r) {
		String toUser = ((SipURI) ((ToHeader) r.getHeader(ToHeader.NAME)).getAddress().getURI()).getUser();
		if (toUser == null) {
			throw new RuntimeException("User can not be null.");
		}

		if (toUser.compareTo("1010") == 0) {
			return new EchoProxy(this);
		} else if (toUser.compareTo("1011") == 0) {
			return new AnnProxy(this);
		} else if (toUser.compareTo("1012") == 0) {
			return null;
		} else if (toUser.compareTo("1013") == 0) {
			return null;
		} else {
			throw new RuntimeException("Unknown user: " + toUser + " .");
		}

	}

	public void processResponse(ResponseEvent response) {
		CallProxy cp = getCallProxy(response.getResponse());
		if (cp != null) {
			cp.processResponse(response);
		} else {
			logger.error("No call proxy for callID: " + response.getResponse().getHeader(CallID.NAME)
					+ " for process response.");
		}
	}

	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTransactionTerminated(TransactionTerminatedEvent tte) {
		Transaction t = null;
		Message msg = null;
		if (tte.getClientTransaction() != null) {
			t = tte.getClientTransaction();
			msg = t.getRequest();
		} else if (tte.getServerTransaction() != null) {
			t = tte.getServerTransaction();
			msg = t.getRequest();
		} else {
			logger.error("No call proxy for callID: " + msg.getHeader(CallID.NAME) + " for tx timeout.");
		}
	}

	private Properties readSipProperties() {
		Properties props = new Properties();

		try {
			props.load(this.getClass().getResourceAsStream("sip.properties"));
			String bindAddress = props.getProperty(SIP_BIND_ADDRESS);
			if (bindAddress == null) {
				bindAddress = this.stackAddress;
				if (bindAddress != null)
					props.setProperty(SIP_BIND_ADDRESS, bindAddress);
			} else {
				this.stackAddress = bindAddress;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return props;
	}

	private Properties readMGCPProperties() {

		Properties props = new Properties();
		try {
			props.load(this.getClass().getResourceAsStream("mgcp.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;

	}

	public void stop() {
		this.sipStack.stop();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hurray it started");
		Jsr309Example example = new Jsr309Example();

		try {
			example.init();
			// SipURI uri = se.getAddressFactory().createSipURI("1010",
			// "127.0.0.1");

			// CSeqHeader cseq = se.getHeaderFactory().createCSeqHeader(1l,
			// Request.INVITE);

			// Request r= se.getMessageFactory().createRequest(uri,
			// Request.INVITE, se.getSipProvider().getNewCallId(),cseq , arg4,
			// arg5, arg6, arg7, arg8, arg9);

			Thread.currentThread().sleep(1000 * 10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProviderDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		example.stop();
	}

}
