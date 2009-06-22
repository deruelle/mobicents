package org.openxdm.xcap.client.test.subscription;

import static org.junit.Assert.assertTrue;
import gov.nist.javax.sip.Utils;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openxdm.xcap.client.Response;
import org.openxdm.xcap.client.test.AbstractXDMJunitTest;
import org.openxdm.xcap.client.test.ServerConfiguration;
import org.openxdm.xcap.common.key.DocumentUriKey;
import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.xcapdiff.DocumentType;
import org.openxdm.xcap.common.xcapdiff.XcapDiff;

/**
 * first puts a new doc through xcap then subscribes it through sip, etag from xcap put response and notify must match.
 * Then update document through xcap and a notify with old and new etag should arrive.
 * Finally delete document and a notify with old etag should arrive.
 * Unsubscribe to clean up.
 */
public class SubscribeDocumentTest extends AbstractXDMJunitTest implements SipListener {
	
	private static Logger logger = Logger.getLogger(SubscribeDocumentTest.class);
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(SubscribeDocumentTest.class);
	}
	
	protected enum Tests {
		test1,
		test2,
		test3,
		test4
	}
	protected Tests testRunning; 
	
	protected String subscriberUsername = "eduardo";
	protected String domain = "openxdm.org";
	protected String subscriberSipUri = "sip:"+subscriberUsername+"@" + domain;
	
	protected SipProvider sipProvider;
	protected AddressFactory addressFactory;
	protected MessageFactory messageFactory;
	protected HeaderFactory headerFactory;
	protected SipStack sipStack;
	protected ContactHeader contactHeader;
	protected String notifierPort;
	protected String transport;
	protected ListeningPoint listeningPoint;
	
	protected Dialog subscriberDialog;
	protected String subscriberToTag; 
	
	protected String newEtag;
	protected String previousEtag;
	
	// a sempahore to control processing using async events
	protected Semaphore semaphore = new Semaphore(1);
	
	/**
	 * the key used to manipulate XCAP document
	 */
	protected DocumentUriKey getDocumentUriKey() {
		
		return new UserDocumentUriKey(appUsage.getAUID(),subscriberSipUri,documentName);
	}
	
	/**
	 * the content used in initial xcap put and initial subscribe
	 * @return
	 */
	protected String getContent() {
		return 	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
					"<list>" +
						"<entry uri=\""+getDocumentUriKey().getDocumentSelector()+"\"/>" +
					"</list>" +
				"</resource-lists>";
	}
	
	/**
	 * puts a new doc through xcap, store etag of response and then subscribes doc through sip
	 * @throws InvalidArgumentException 
	 * @throws ParseException 
	 * @throws SipException 
	 */
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException, ParseException, InvalidArgumentException, SipException {
		
		// set test state machine
		testRunning = Tests.test1;
		
		// send put request and get response
		Response putResponse = client.put(getDocumentUriKey(),appUsage.getMimetype(),getContent(),null);
		
		// check put response
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response code should be 201",putResponse.getCode() == 201);
		
		// set initial etag
		newEtag = putResponse.getETag();
		
		// send subscribe
		sendInitialSubscribe();
		
		// let's wait for test to succeed or timeout
		synchronized (this) {
			this.wait(15000);
		}
		
		
		
		assertTrue("Test timer expired (15 secs)",passed);
	}
	
	private void sendInitialSubscribe() throws ParseException, InvalidArgumentException, SipException {

			// create >From Header
			Address address = addressFactory.createAddress(subscriberSipUri);
			FromHeader fromHeader = headerFactory.createFromHeader(
					address, Utils.getInstance().generateTag());

			// create To Header
			ToHeader toHeader = headerFactory.createToHeader(address,
					null);

			// Create ViaHeaders
			ArrayList viaHeaders = new ArrayList();
			int port = sipProvider.getListeningPoint(transport).getPort();
			ViaHeader viaHeader = headerFactory.createViaHeader(ServerConfiguration.SERVER_HOST,
					port, transport, null);
			viaHeaders.add(viaHeader);

			// Create a new CallId header
			CallIdHeader callIdHeader = sipProvider.getNewCallId();

			// Create a new Cseq header
			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L,
					Request.SUBSCRIBE);

			// Create a new MaxForwardsHeader
			MaxForwardsHeader maxForwards = headerFactory
					.createMaxForwardsHeader(70);

			// Create the request.
			Request request = messageFactory.createRequest(address.getURI(),
					Request.SUBSCRIBE, callIdHeader, cSeqHeader, fromHeader,
					toHeader, viaHeaders, maxForwards);
			
			// Create contact headers
			SipURI contactURI = addressFactory.createSipURI(subscriberUsername, listeningPoint.getIPAddress());
			contactURI.setTransportParam(transport);
			contactURI.setPort(port);
			// add the contact address.
			Address contactAddress = addressFactory.createAddress(contactURI);
			// create and save contact header
			contactHeader = headerFactory.createContactHeader(contactAddress);
			request.addHeader(contactHeader);

			// add route
			request.addHeader(headerFactory.createRouteHeader(addressFactory
					.createAddress("<sip:"+ServerConfiguration.SERVER_HOST+":" + notifierPort
							+ ";transport=" + transport + ";lr>")));
						
			// Create an event header for the subscription.
			request.addHeader(headerFactory.createEventHeader("xcap-diff"));

			// add content
			request.setContent(getContent(), headerFactory.createContentTypeHeader("application", "resource-lists+xml"));
			
			// create the client transaction.
			ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(request);

			// save the dialog
			this.subscriberDialog = clientTransaction.getDialog();
			
			// send the request out.
			clientTransaction.sendRequest();
			
	}
	
	private DocumentType processNotify(RequestEvent requestEvent) throws ParseException, SipException, InvalidArgumentException, JAXBException {
		
		Request notify = requestEvent.getRequest();
		
		javax.sip.message.Response response = messageFactory.createResponse(200, notify);
		// SHOULD add a Contact
		ContactHeader contact = (ContactHeader) contactHeader.clone();
		((SipURI)contact.getAddress().getURI()).setParameter( "id", "sub" );
		response.addHeader( contact );
		requestEvent.getServerTransaction().sendResponse(response);		

		SubscriptionStateHeader subscriptionState = (SubscriptionStateHeader) requestEvent.getRequest().getHeader(SubscriptionStateHeader.NAME);
		assertTrue("subscription not active", subscriptionState.getState().equalsIgnoreCase(SubscriptionStateHeader.ACTIVE));
		
		// unmarshall content
		StringReader stringReader = new StringReader(new String(requestEvent.getRequest().getRawContent()));
		XcapDiff xcapDiff = (XcapDiff) jaxbContext.createUnmarshaller().unmarshal(stringReader);
		
		assertTrue("unexpected xcap root in xcap diff",xcapDiff.getXcapRoot().equals("http://"+ServerConfiguration.SERVER_HOST+":"+ServerConfiguration.SERVER_PORT+ServerConfiguration.SERVER_XCAP_ROOT+"/"));
		stringReader.close();
		
		assertTrue("not a single document element inside xcap diff document received", xcapDiff.getDocumentOrElementOrAttribute().size() == 1 && xcapDiff.getDocumentOrElementOrAttribute().get(0) instanceof DocumentType);
		
		return (DocumentType) xcapDiff.getDocumentOrElementOrAttribute().get(0);
		
	}
	
	/*
	 * etag from xcap put response and notify must match.
	 */
	private void processTest1Notify(RequestEvent requestEvent) throws JAXBException, ParseException, SipException, InvalidArgumentException {
		DocumentType documentType = processNotify(requestEvent);
		assertTrue("previous etag is set", documentType.getPreviousEtag() == null || documentType.getPreviousEtag().equals(""));	
		assertTrue("new etag ("+documentType.getNewEtag()+") doesn't match one received in XCAP PUT response ("+newEtag+")", documentType.getNewEtag() != null && documentType.getNewEtag().equals(newEtag));	
	}

	// ---- TEST 2
	
	private void doTest2() throws InterruptedException, HttpException, IOException {
		
		// set test state machine
		testRunning = Tests.test2;
		
		String content =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<resource-lists xmlns=\"urn:ietf:params:xml:ns:resource-lists\">" +
				"<list/>" +
			"</resource-lists>";
		
		// send put request and get response
		Response putResponse = client.put(getDocumentUriKey(),appUsage.getMimetype(),content,null);
		
		// check put response
		assertTrue("Put response must exists",putResponse != null);
		assertTrue("Put response code should be 200",putResponse.getCode() == 200);
		
		// set etags
		previousEtag = newEtag;
		newEtag = putResponse.getETag();
		
	}

	private void processTest2Notify(RequestEvent requestEvent) throws ParseException, SipException, InvalidArgumentException, JAXBException {
		DocumentType documentType = processNotify(requestEvent);
		assertTrue("previous etag ("+documentType.getPreviousEtag()+") doesn't match one received in first XCAP PUT response ("+previousEtag+")", documentType.getPreviousEtag() != null && documentType.getPreviousEtag().equals(previousEtag));	
		assertTrue("new etag ("+documentType.getNewEtag()+") doesn't match one received in XCAP PUT response ("+newEtag+")", documentType.getNewEtag() != null && documentType.getNewEtag().equals(newEtag));	
	}
	
	// ---- TEST 3
	
	private void doTest3() throws InterruptedException, HttpException, IOException {
		
		// set test state machine
		testRunning = Tests.test3;
		
		// send put request and get response
		Response deleteResponse = client.delete(getDocumentUriKey(),null);
		
		// set previous etag
		previousEtag = newEtag;
		
		// check put response
		assertTrue("Delete response must exists",deleteResponse != null);
		assertTrue("Delete response code should be 200",deleteResponse.getCode() == 200);
		
	}

	private void processTest3Notify(RequestEvent requestEvent) throws ParseException, SipException, InvalidArgumentException, JAXBException {
		DocumentType documentType = processNotify(requestEvent);
		assertTrue("previous etag doesn't match one received in second XCAP PUT response", documentType.getPreviousEtag() != null && documentType.getPreviousEtag().equals(previousEtag));	
		assertTrue("new etag is not null", documentType.getNewEtag() == null);			
	}

	// ---- TEST 4
	
	private void doTest4() throws ParseException, SipException, InvalidArgumentException {
		testRunning = Tests.test4;
		unsubscribe();
		
	}

	private void unsubscribe() throws ParseException, SipException, InvalidArgumentException {
		
		Request request = this.subscriberDialog
				.createRequest(Request.SUBSCRIBE);
		ToHeader toHeader = (ToHeader)request.getHeader(ToHeader.NAME);
		if (toHeader.getTag() == null) {
			toHeader.setTag(subscriberToTag);			
		}
		
		// Create a new MaxForwardsHeader
		request.setHeader(headerFactory
				.createMaxForwardsHeader(70));
		// Create an event header for the subscription.
		request.addHeader(headerFactory.createEventHeader("xcap-diff"));
		// add expires header
		request.setExpires(headerFactory.createExpiresHeader(0));
		// create client transaction
		ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(request);
		// send request
		clientTransaction.sendRequest();
	}
	
	private void processTest4Notify(RequestEvent requestEvent) throws ParseException, SipException, InvalidArgumentException, JAXBException {
		
		Request notify = requestEvent.getRequest();
		
		javax.sip.message.Response response = messageFactory.createResponse(200, notify);
		// SHOULD add a Contact
		ContactHeader contact = (ContactHeader) contactHeader.clone();
		((SipURI)contact.getAddress().getURI()).setParameter( "id", "sub" );
		response.addHeader( contact );
		requestEvent.getServerTransaction().sendResponse(response);	
		
		SubscriptionStateHeader subscriptionState = (SubscriptionStateHeader) requestEvent.getRequest().getHeader(SubscriptionStateHeader.NAME);
		assertTrue("subscription didn't terminate", subscriptionState.getState().equalsIgnoreCase(SubscriptionStateHeader.TERMINATED));
					
	}
	
	// --- METHODS TO PROCESS SIP REQUESTS AND RESPONSES
	
	public void processRequest(RequestEvent requestEvent) {
		
		System.out.println("Request rcvd {\n"+requestEvent.getRequest()+"\n}");
		
		Request request = requestEvent.getRequest();
		
		if (request.getMethod().equals(Request.NOTIFY)) {
		
			try {
				switch (testRunning) {
				case test1:
					processTest1Notify(requestEvent);
					semaphore.acquire();
					doTest2();
					semaphore.release();
					break;

				case test2:
					semaphore.acquire();
					processTest2Notify(requestEvent);
					doTest3();
					semaphore.release();
					break;

				case test3:
					processTest3Notify(requestEvent);
					doTest4();
					break;
					
				case test4:
					processTest4Notify(requestEvent);
					setTestResult(true);
					break;	

				default:
					System.err.println("unknown test");
					setTestResult(false);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				setTestResult(false);
			}
			
		}
	}

	public void processResponse(ResponseEvent responseReceivedEvent) {
		
		System.out.println("Response received:\n" + responseReceivedEvent.getResponse());
		assertTrue("received response to subscribe which signals that subscription was not approved", responseReceivedEvent.getResponse().getStatusCode() == 200);

		if (subscriberToTag == null) {
			subscriberToTag = ((ToHeader)responseReceivedEvent.getResponse().getHeader(ToHeader.NAME)).getTag();
		}
	}
	
	// --- UNUSED SIP LISTENER METHODS
	
	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void processIOException(IOExceptionEvent arg0) {
		throw new RuntimeException("processIOException(IOExceptionEvent"+arg0+")");
	}

	public void processTimeout(TimeoutEvent arg0) {
		throw new RuntimeException("processTimeout(TimeoutEvent="+arg0+")");
		
	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	// ---- TEST setup/cleanup
	
	@Before
	public void runBefore() throws IOException, InterruptedException {
			
		super.runBefore();
		
		try {
			// init sip stack
			notifierPort = "5060";

			transport = "udp";

			SipFactory sipFactory = SipFactory.getInstance();
			sipFactory.setPathName("gov.nist");
			Properties properties = new Properties();

			properties.setProperty("javax.sip.USE_ROUTER_FOR_ALL_URIS", "false");

			properties.setProperty("javax.sip.STACK_NAME", "subscriber");
			properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
			"subscriberdebug.txt");
			properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
			"subscriberlog.txt");

			properties.setProperty("javax.sip.FORKABLE_EVENTS", "foo");

			// Set to 0 in your production code for max speed.
			// You need 16 for logging traces. 32 for debug + traces.
			// Your code will limp at 32 but it is best for debugging.
			properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "0");

			sipStack = sipFactory.createSipStack(properties);
			logger.info("createSipStack " + sipStack);
			headerFactory = sipFactory.createHeaderFactory();
			addressFactory = sipFactory.createAddressFactory();
			messageFactory = sipFactory.createMessageFactory();
			
			this.listeningPoint = sipStack.createListeningPoint(ServerConfiguration.SERVER_HOST, 6060,
					transport);
			sipProvider = sipStack.createSipProvider(listeningPoint);
			sipProvider.addSipListener(this);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to start sip stack. Exception msg: "+e.getMessage());
		}
	}
	
	@After
	public void runAfter() throws IOException {
		
		super.runAfter();
		
		try {
			sipProvider.removeSipListener(this);
			sipStack.deleteSipProvider(sipProvider);
			sipStack.deleteListeningPoint(listeningPoint);
			sipStack.stop();
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to stop sip stack. Exception msg: "+e.getMessage());
		}
	}
	
	private boolean passed = false;
	/**
	 * sets test result
	 * @param result
	 */
	protected void setTestResult(boolean result) {
		passed = result;
		synchronized (this) {
			
			this.notifyAll();
		}
	}
	
	// ------ JAXB resource lists and xcap diff context
	
	protected static final JAXBContext jaxbContext = initJAXBContext();
	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext.newInstance(
					"org.openxdm.xcap.common.xcapdiff" +
					":org.openxdm.xcap.client.appusage.resourcelists.jaxb");
		} catch (JAXBException e) {
			logger.error("failed to create jaxb context");
			return null;
		}
	}

}
