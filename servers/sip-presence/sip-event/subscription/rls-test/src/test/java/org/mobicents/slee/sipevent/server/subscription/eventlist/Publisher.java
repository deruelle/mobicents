package org.mobicents.slee.sipevent.server.subscription.eventlist;

import gov.nist.javax.sip.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.SIPETagHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 * 
 * @author Eduardo Martins
 * 
 */
public class Publisher implements SipListener {

	private final String eventPackage = "presence";
	private final String contentType = "application";
	private final String contentSubType = "pidf+xml";
	private final static Timer timer = new Timer();

	private SipProvider sipProvider;
	private AddressFactory addressFactory;
	private MessageFactory messageFactory;
	private HeaderFactory headerFactory;
	private SipStack sipStack;
	private ContactHeader contactHeader;
	private String notifierPort;
	private String transport;
	private ListeningPoint listeningPoint;

	private int expires = 60;
	private String etag;
	private final String tupleId = Utils.getInstance().generateTag();
	
	private final String publisher;
	private final int listeningPort;
	private final ResourceListServerSipTest test;

	private enum StateMachine {
		starting, started, stopping
	}

	private StateMachine stateMachine;

	public Publisher(String publisher, int listeningPort, ResourceListServerSipTest test) {
		this.publisher = publisher;
		this.listeningPort = listeningPort;
		this.test = test;
	}

	public void publish() throws InvalidArgumentException,
			TooManyListenersException, ParseException, SipException {
		this.stateMachine = StateMachine.starting;
		initSipStack();
		sendPublishRequest(getAvailableRequestContent(), this.expires);
	}

	private void initSipStack() throws PeerUnavailableException,
			TransportNotSupportedException, InvalidArgumentException,
			ObjectInUseException, TooManyListenersException {

		// init sip stack
		notifierPort = "5060";

		transport = "udp";

		SipFactory sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties properties = new Properties();

		properties.setProperty("javax.sip.USE_ROUTER_FOR_ALL_URIS", "false");

		properties.setProperty("javax.sip.STACK_NAME", publisher);
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "publisher_"
				+ listeningPort + "_debug.txt");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "publisher_"
				+ listeningPort + "_log.txt");

		properties.setProperty("javax.sip.FORKABLE_EVENTS", "foo");

		// Set to 0 in your production code for max speed.
		// You need 16 for logging traces. 32 for debug + traces.
		// Your code will limp at 32 but it is best for debugging.
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "0");

		sipStack = sipFactory.createSipStack(properties);

		headerFactory = sipFactory.createHeaderFactory();
		addressFactory = sipFactory.createAddressFactory();
		messageFactory = sipFactory.createMessageFactory();

		this.listeningPoint = sipStack.createListeningPoint(
				ServerConfiguration.SERVER_HOST, listeningPort, transport);
		sipProvider = sipStack.createSipProvider(listeningPoint);
		sipProvider.addSipListener(this);

	}

	private void sendPublishRequest(String content, int expires) throws ParseException,
			InvalidArgumentException, SipException {

		// create From Header
		Address address = addressFactory.createAddress(publisher);
		FromHeader fromHeader = headerFactory.createFromHeader(address, Utils.getInstance()
				.generateTag());

		// create To Header
		ToHeader toHeader = headerFactory.createToHeader(address, null);

		// Create ViaHeaders
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		int port = sipProvider.getListeningPoint(transport).getPort();
		ViaHeader viaHeader = headerFactory.createViaHeader(
				ServerConfiguration.SERVER_HOST, port, transport, null);
		viaHeaders.add(viaHeader);

		// Create a new CallId header
		CallIdHeader callIdHeader = sipProvider.getNewCallId();

		// Create a new Cseq header
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L,
				Request.PUBLISH);

		// Create a new MaxForwardsHeader
		MaxForwardsHeader maxForwards = headerFactory
				.createMaxForwardsHeader(70);

		// Create the request.
		Request request = messageFactory.createRequest(address.getURI(),
				Request.PUBLISH, callIdHeader, cSeqHeader, fromHeader,
				toHeader, viaHeaders, maxForwards);

		// Create contact headers
		SipURI contactURI = (SipURI) addressFactory.createURI(publisher + ":"
				+ listeningPoint.getIPAddress());
		contactURI.setTransportParam(transport);
		contactURI.setPort(port);
		// add the contact address.
		Address contactAddress = addressFactory.createAddress(contactURI);
		// create and save contact header
		contactHeader = headerFactory.createContactHeader(contactAddress);
		request.addHeader(contactHeader);

		// add route
		request.addHeader(headerFactory.createRouteHeader(addressFactory
				.createAddress("<sip:" + ServerConfiguration.SERVER_HOST + ":"
						+ notifierPort + ";transport=" + transport + ";lr>")));

		// Create an event header for the subscription.
		request.addHeader(headerFactory.createEventHeader(this.eventPackage));

		// add expires
		request.addHeader(headerFactory.createExpiresHeader(expires));

		if (content != null) {
			// add content
			request.setContent(content, headerFactory.createContentTypeHeader(
					this.contentType, this.contentSubType));
		}

		if (etag != null) {
			// add etag header
			request.addHeader(headerFactory.createSIPIfMatchHeader(etag));
		}
		
		// create the client transaction.
		ClientTransaction clientTransaction = sipProvider
				.getNewClientTransaction(request);

		// send the request out.
		clientTransaction.sendRequest();
	}

	private String getAvailableRequestContent() {
		return "<?xml version='1.0' encoding='UTF-8'?>"
				+ "<presence xmlns='urn:ietf:params:xml:ns:pidf' entity='"
				+ publisher
				+ "'>"
				+ "<tuple id='"+tupleId+"'><status><basic>open</basic></status></tuple>"
				+ "</presence>";
	}	
	private String getBusyRequestContent() {
		return "<?xml version='1.0' encoding='UTF-8'?>"
				+ "<presence xmlns='urn:ietf:params:xml:ns:pidf' xmlns:dm='urn:ietf:params:xml:ns:pidf:data-model' xmlns:rpid='urn:ietf:params:xml:ns:pidf:rpid' xmlns:c='urn:ietf:params:xml:ns:pidf:cipid' entity='"
				+ publisher
				+ "'>"
				+ "<tuple id='"+tupleId+"'><status><basic>open</basic></status></tuple>"
				+ "<dm:person id='"+tupleId+"'>"
				+ "<rpid:activities><rpid:busy/></rpid:activities>"
				+ "<dm:note>Busy</dm:note>" + "</dm:person>" + "</presence>";
	}
	private String getUnavailableRequestContent() {
		return "<?xml version='1.0' encoding='UTF-8'?>"
		+ "<presence xmlns='urn:ietf:params:xml:ns:pidf' entity='"
		+ publisher
		+ "'>"
		+ "<tuple id='"+tupleId+"'><status><basic>closed</basic></status></tuple>"
		+ "</presence>";
	}
	private boolean busyState = false;
	private String getRefreshRequestContent() {
		if (busyState) {
			busyState = false;
			return getAvailableRequestContent();
		}
		else {
			busyState = true;
			return getBusyRequestContent();
		}
	}
	
	private void refresh() throws SipException, InvalidArgumentException,
			ParseException {
		sendPublishRequest(getRefreshRequestContent(), this.expires);
	}

	public void unpublish() throws SipException, InvalidArgumentException,
			ParseException {
		sendPublishRequest(null,0);
	}

	private void setRefreshTimer(int expires) {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				try {
					this.cancel();
					refresh();
				} catch (Exception e) {
					e.printStackTrace();
					test.failTest(e.getMessage());
				}
			}
		};
		timer.schedule(task, (expires - 1) * 1000);
	}

	// JAIN SIP LISTENER

	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processIOException(IOExceptionEvent arg0) {
		test.failTest("processIOException: arg0 = " + arg0);
	}

	public void processRequest(RequestEvent arg0) {
		test.failTest("processRequest: arg0 = " + arg0);
	}

	public void processResponse(ResponseEvent arg0) {

		Response response = arg0.getResponse();
		System.out.println("Publisher " + publisher + " rcvd response:\n"
				+ response);

		switch (this.stateMachine) {
		case starting:
			// initial request response
			if (response.getStatusCode() > 299) {
				test.failTest("unexpected response when " + this.stateMachine
						+ " publisher " + publisher + ", can't proceeed");
			} else if (response.getStatusCode() > 199) {
				SIPETagHeader sIPETagHeader = (SIPETagHeader) response
						.getHeader(SIPETagHeader.NAME);
				ExpiresHeader expiresHeader = response.getExpires();
				if (sIPETagHeader != null && expiresHeader != null) {
					this.expires = expiresHeader.getExpires();
					setRefreshTimer(this.expires);
					etag = sIPETagHeader.getETag();
				}
			} else {
				test.failTest("unexpected response when " + this.stateMachine
						+ " publisher " + publisher + ", can't proceeed");
			}
			break;
		case started:
			// refresh request response
			if (response.getStatusCode() > 299) {
				test.failTest("unexpected response when " + this.stateMachine
						+ " publisher " + publisher + ", can't proceeed");
			} else if (response.getStatusCode() > 199) {
				SIPETagHeader sIPETagHeader = (SIPETagHeader) response
						.getHeader(SIPETagHeader.NAME);
				ExpiresHeader expiresHeader = response.getExpires();
				if (sIPETagHeader != null && expiresHeader != null) {
					this.expires = expiresHeader.getExpires();
					setRefreshTimer(this.expires);
					etag = sIPETagHeader.getETag();
				}
			} else {
				test.failTest("unexpected response when " + this.stateMachine
						+ " publisher " + publisher + ", can't proceeed");
			}
			break;
		case stopping:
			// refresh request response
			if (response.getStatusCode() > 299) {
				test.failTest("unexpected response when " + this.stateMachine
						+ " publisher " + publisher + ", can't proceeed");
			} else if (response.getStatusCode() > 199) {
				// TODO finished
			} else {
				test.failTest("unexpected response when " + this.stateMachine
						+ " publisher " + publisher + ", can't proceeed");
			}
			break;

		default:
			test.failTest("invalid state " + this.stateMachine
					+ " on publisher " + publisher);

		}
	}

	public void processTimeout(TimeoutEvent arg0) {
		test.failTest("processTimeout: arg0 = " + arg0);
	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		
	}

}