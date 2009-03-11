package org.mobicents.servers.media.examples.simple;

import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.header.CallID;
import jain.protocol.ip.mgcp.CreateProviderException;
import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.JainMgcpStack;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.logging.Logger;

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

import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackImpl;
import org.mobicents.servers.media.examples.simple.proxies.ann.AnnProxy;
import org.mobicents.servers.media.examples.simple.proxies.echo.EchoProxy;

public class SimplExample implements JainMgcpExtendedListener, SipListener {

	private Map<String, CallProxy> callIdToProxy = null;
	private Map<Integer, CallProxy> mgcpTransactionToProxy = null;
	private Map<RequestIdentifier, CallProxy> requestIdIdToProxy = null;
	private static final String SIP_BIND_ADDRESS = "javax.sip.IP_ADDRESS";

	// private static final String SIP_PORT_BIND = "javax.sip.PORT";

	// rivate static final String TRANSPORTS_BIND = "javax.sip.TRANSPORT";

	// private static final String STACK_NAME_BIND = "javax.sip.STACK_NAME";

	private int port = 9060;

	private String stackAddress = "0.0.0.0";
	private int mgcpServerPort = 2727;
	private SipStackImpl sipStack = null;
	private SipFactory sipFactory;
	private SipProvider sipProvider;
	private MessageFactory messageFactory = null;
	private HeaderFactory headerFactory = null;
	private AddressFactory addressFactory = null;

	private JainMgcpStack mgpcStack = null;
	private InetAddress mgcpClientAddress = null;
	private InetAddress mgcpServerAddress = null;
	private int mgcpClientPort = 2724;
	private JainMgcpProvider mgcpProvider = null;

	private static Logger logger = Logger.getLogger(SimplExample.class.getName());

	public SimplExample() {
		super();

		try {

			this.callIdToProxy = new HashMap<String, CallProxy>();
			this.mgcpTransactionToProxy = new HashMap<Integer, CallProxy>();
			this.requestIdIdToProxy = new HashMap<RequestIdentifier, CallProxy>();

			this.mgcpClientAddress = InetAddress.getByName("127.0.0.1");
			this.mgcpServerAddress = InetAddress.getByName("127.0.0.1");
			this.init();
		} catch (UnknownHostException e) {
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
		} catch (CreateProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return props;
	}

	private Properties readMGCPProperties() {

		Properties props = new Properties();
		try {
			props.load(this.getClass().getResourceAsStream("mgcp.properties"));
			String v = props.getProperty("clientPort", this.mgcpClientPort + "");
			this.mgcpClientPort = Integer.valueOf(v);
			v = props.getProperty("serverPort", this.mgcpServerPort + "");
			this.mgcpServerPort = Integer.valueOf(v);

			v = props.getProperty("clientIP", this.mgcpClientAddress + "");
			this.mgcpClientAddress = InetAddress.getByName(v);

			v = props.getProperty("serverIP", this.mgcpServerAddress + "");
			this.mgcpServerAddress = InetAddress.getByName(v);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;

	}

	private void init() throws ProviderDoesNotExistException, SipException, InvalidArgumentException, TooManyListenersException, CreateProviderException {

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
		System.err.println("Sipt stack started: " + lp.getIPAddress() + ":" + lp.getPort());

		Properties mgcpProperties = readMGCPProperties();
		this.mgpcStack = new JainMgcpStackImpl(this.mgcpClientAddress, this.mgcpClientPort);
		this.mgcpProvider = this.mgpcStack.createProvider();
		this.mgcpProvider.addJainMgcpListener(this);
	}

	// MGCP

	public void processMgcpCommandEvent(JainMgcpCommandEvent command) {

		if (command instanceof Notify) {
			Notify notify=(Notify) command;
			CallProxy cp = getCallProxy(notify.getRequestIdentifier());
			if (cp != null) {
				cp.processMgcpCommandEvent(command);
			}
		}
	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent response) {
		try {
			CallProxy cp = getCallProxy(response);
			if (cp != null) {
				cp.processMgcpResponseEvent(response);
			} else {
				System.err.println("--- SEVERE 2 ---");
			}
		} catch (RuntimeException re) {
			re.printStackTrace();
		}

	}

	public void transactionEnded(int arg0) {
		CallProxy cp = getCallProxy(arg0);
		if (cp != null) {
			cp.transactionEnded(arg0);
		} else {
			logger.severe("No call proxy for txid: " + arg0);
		}

	}

	public void transactionRxTimedOut(JainMgcpCommandEvent commandTimedOut) {
		CallProxy cp = getCallProxy(commandTimedOut);
		if (cp != null) {
			cp.transactionRxTimedOut(commandTimedOut);
		} else {
			logger.severe("No call proxy for txid: " + commandTimedOut.getTransactionHandle() + " for timed out event");
		}

	}

	public void transactionTxTimedOut(JainMgcpCommandEvent commandTimeOut) {
		CallProxy cp = getCallProxy(commandTimeOut);
		if (cp != null) {
			cp.transactionTxTimedOut(commandTimeOut);
		} else {
			logger.severe("No call proxy for txid: " + commandTimeOut.getTransactionHandle() + " for timed out event2");
		}
	}

	// SIP

	public void processDialogTerminated(DialogTerminatedEvent dte) {
		CallProxy cp = getCallProxy(dte.getDialog().getCallId());
		if (cp != null) {
			cp.processDialogTerminated(dte);
		} else {
			logger.severe("No call proxy for callID: " + dte.getDialog().getCallId().getCallId() + " for timed out dialog");
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
					logger.severe("No call proxy for callID: " + request.getRequest().getHeader(CallID.NAME) + " for process request.");
					return;
				}
			}
			if (cp != null) {
				cp.processRequest(request);
			} else {
				logger.severe("No call proxy for callID: " + request.getRequest().getHeader(CallID.NAME) + " for process request.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processResponse(ResponseEvent response) {
		CallProxy cp = getCallProxy(response.getResponse());
		if (cp != null) {
			cp.processResponse(response);
		} else {
			logger.severe("No call proxy for callID: " + response.getResponse().getHeader(CallID.NAME) + " for process response.");
		}

	}

	public void processTimeout(TimeoutEvent te) {
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
			logger.severe("No call proxy for callID: " + msg.getHeader(CallID.NAME) + " for tx timeout.");
			System.err.println("No call proxy for callID: " + msg.getHeader(CallID.NAME) + " for tx timeout.");
		}

	}

	protected CallProxy getCallProxy(Message msg) {
		CallID callIDHeader = (CallID) msg.getHeader(CallID.NAME);
		return this.getCallProxy(callIDHeader);
	}

	protected CallProxy getCallProxy(JainMgcpEvent mgcpEvent) {
		return this.mgcpTransactionToProxy.get(mgcpEvent.getTransactionHandle());
	}

	protected CallProxy getCallProxy(int txID) {
		return this.mgcpTransactionToProxy.get(txID);
	}

	public void removeCallProxy(JainMgcpEvent mgcpEvent) {
		this.mgcpTransactionToProxy.remove(mgcpEvent.getTransactionHandle());
	}

	public void removeCallProxy(Message msg) {
		CallIdHeader callIDHeader = (CallIdHeader) msg.getHeader(CallID.NAME);
		this.removeCallProxy(callIDHeader);
	}

	public void removeCallProxy(CallIdHeader msg) {

		this.callIdToProxy.remove(msg.getCallId());
	}

	public void removeCallProxy(int txID) {
		this.mgcpTransactionToProxy.remove(txID);
	}

	public void addCallProxy(Message msg, CallProxy cp) {
		CallID callIDHeader = (CallID) msg.getHeader(CallID.NAME);
		this.callIdToProxy.put(callIDHeader.getCallId(), cp);
	}

	public void addCallProxy(RequestIdentifier ri, AnnProxy cp) {
		this.requestIdIdToProxy.put(ri, cp);

	}

	public void removeCallProxy(RequestIdentifier ri) {
		this.requestIdIdToProxy.remove(ri);
	}

	public CallProxy getCallProxy(CallIdHeader callId) {
		return this.callIdToProxy.get(callId.getCallId());
	}

	public CallProxy getCallProxy(RequestIdentifier ri) {
		return this.requestIdIdToProxy.get(ri);
	}

	public void addCallProxy(JainMgcpEvent mgcpEvent, CallProxy cp) {

		this.mgcpTransactionToProxy.put(mgcpEvent.getTransactionHandle(), cp);
	}

	public int getMgcpServerPort() {
		return mgcpServerPort;
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

	public InetAddress getMgcpServerAddress() {
		return mgcpServerAddress;
	}

	public JainMgcpProvider getMgcpProvider() {
		return mgcpProvider;
	}

	public int getPort() {
		return port;
	}

	public InetAddress getMgcpClientAddress() {
		return mgcpClientAddress;
	}

	public int getMgcpClientPort() {
		return mgcpClientPort;
	}

	public String getStackAddress() {
		return stackAddress;
	}

	public static void main(String[] args) {
		SimplExample se = new SimplExample();
		try {

			// SipURI uri = se.getAddressFactory().createSipURI("1010",
			// "127.0.0.1");

			// CSeqHeader cseq = se.getHeaderFactory().createCSeqHeader(1l,
			// Request.INVITE);

			// Request r= se.getMessageFactory().createRequest(uri,
			// Request.INVITE, se.getSipProvider().getNewCallId(),cseq , arg4,
			// arg5, arg6, arg7, arg8, arg9);

			Thread.currentThread().sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		se.stop();
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

	public void stop() {
		this.sipStack.stop();

	}

}
