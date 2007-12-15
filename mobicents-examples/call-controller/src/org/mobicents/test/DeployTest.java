package org.mobicents.test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipListener;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;


public class DeployTest implements SipListener {

	private static Logger logger=Logger.getLogger("DeployTest.class");
	private static SIPStackBinder binder=null;
	private static Thread thr=null;
	
	private static boolean registerOkReceived=false;
	private static boolean forbidenResceived=false;
	public static void main(String[] args)
	{
		//HERE WE WILL SEND REGISTER REQUEST TO TEST deployment
		
		
		binder=SIPStackBinder.getInstance();
		binder.registerSipListener(new DeployTest());
		
		Request register=null; //This will be our request
		//WE NEED SOMETHING LIKE THIS
		//REGISTER sip:nist.gov SIP/2.0
		//Via: SIP/2.0/UDP 192.168.1.101:15940
		//Max-Forwards: 70
		//From: <sip:mobicents@nist.gov>;tag=c5aa7c5177734adf88dbd28cc2f77e84;epid=4e72c61bb4
		//To: <sip:mobicents@nist.gov>
		//Call-ID: 95cc6d3305f74d9da4a8c2aa432bd059@192.168.1.101
		//CSeq: 1 REGISTER
		//Contact: <sip:192.168.1.101:15940>;methods="INVITE, MESSAGE, INFO, SUBSCRIBE, OPTIONS, BYE, CANCEL, NOTIFY, ACK, REFER"
		//User-Agent: RTC/1.2.4949
		//Event:  registration
		//Allow-Events: presence
		//Content-Length: 0
		
		ContactHeader contactHeader = null;
		ToHeader toHeader = null;
		FromHeader fromHeader = null;
		CSeqHeader cseqHeader = null;
		ViaHeader viaHeader = null;
		CallIdHeader callIdHeader = null;
		MaxForwardsHeader maxForwardsHeader = null;
		ContentTypeHeader contentTypeHeader = null;
		RouteHeader routeHeader = null;
		// LETS CREATEOUR HEADERS
		try {
			cseqHeader = binder.getHeaderFactory().createCSeqHeader(1, Request.REGISTER);
			viaHeader = binder.getHeaderFactory().createViaHeader(binder.getStackAddress(), binder.getPort(),
					binder.getTransport(), null);
			Address fromAddres = binder.getAddressFactory()
					.createAddress("sip:mobicents@nist.gov");
	
			Address toAddress = binder.getAddressFactory().createAddress("sip:mobicents@nist.gov");
			Address contactAddress=binder.getAddressFactory().createAddress("sip:"+binder.getStackAddress()+":"+binder.getPort());
			contactHeader = binder.getHeaderFactory().createContactHeader(contactAddress);
			toHeader = binder.getHeaderFactory().createToHeader(toAddress, null);
			fromHeader = binder.getHeaderFactory().createFromHeader(fromAddres,
					"tag"+Math.random());
			callIdHeader = binder.getProvider().getNewCallId();
			maxForwardsHeader = binder.getHeaderFactory().createMaxForwardsHeader(70);
			contentTypeHeader = binder.getHeaderFactory().createContentTypeHeader("text",
					"plain");
			Address routeAddress = binder.getAddressFactory().createAddress("sip:"
					+ binder.getPeerAddres() + ":" + binder.getPeerPort());
			routeHeader = binder.getHeaderFactory().createRouteHeader(routeAddress);

		} catch (ParseException e) {
			
			e.printStackTrace();
			
		} catch (InvalidArgumentException e) {
			
			e.printStackTrace();
			
		}
		// LETS CREATE OUR REQUEST AND
		ArrayList list = new ArrayList();
		list.add(viaHeader);
		URI requestURI = null;

		
		try {
			requestURI = binder.getAddressFactory().createURI("sip:nist.gov");
			register = binder.getMessageFactory().createRequest(requestURI,
					Request.REGISTER, callIdHeader, cseqHeader, fromHeader,
					toHeader, list, maxForwardsHeader, contentTypeHeader,
					"REGISTER".getBytes());
			register.addHeader(routeHeader);
			register.addHeader(contactHeader);
		} catch (ParseException e) {
			
			e.printStackTrace();
			
		}
		
		ClientTransaction CT = null;
		//ClientTransaction CTCancel = null;
		try {
			CT = binder.getProvider().getNewClientTransaction(register);
			// dial=sipProvider.getNewDialog(CT);
			// dial=CT.getDialog();
		} catch (TransactionUnavailableException e) {
			
			e.printStackTrace();
			
		} catch (SipException e) {
			
			e.printStackTrace();
			
		}

		logger.info("========== REQUEST ============\n" + register
				+ "\n=====================================");
		// ATLAST SENT IT
		try {
			// dial.sendRequest(CT);
			CT.sendRequest();
			//dial = CTInvite.getDialog();

		} catch (SipException e) {
			
			e.printStackTrace();
			
		}
		
		try {
			thr=Thread.currentThread();
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		
		//NOW LETS INVITE sip:torosvi@nist.gov,
		//WE WILL RECEIVE 403 Response
		//INVITE sip:torosvi@nist.gov SIP/2.0
		//Via: SIP/2.0/UDP 192.168.1.101:11637
		//Max-Forwards: 70
		//From: "baranowb" <sip:mobicents@nist.gov>;tag=1f1ecbb4540d46578307b7f045f02c41;epid=6a2a61227f
		//To: <sip:torosvi@nist.gov>
		//Call-ID: 552970fb9b25499c8721a2b52848c176@192.168.1.101
		//CSeq: 1 INVITE
		//Contact: <sip:192.168.1.101:11637>
		
		try {
			cseqHeader = binder.getHeaderFactory().createCSeqHeader(1, Request.INVITE);
			viaHeader = binder.getHeaderFactory().createViaHeader(binder.getStackAddress(), binder.getPort(),
					binder.getTransport(), null);
			Address fromAddres = binder.getAddressFactory()
					.createAddress("sip:mobicents@nist.gov");
	
			Address toAddress = binder.getAddressFactory().createAddress("sip:torosvi@nist.gov");
			Address contactAddress=binder.getAddressFactory().createAddress("sip:"+binder.getStackAddress()+":"+binder.getPort());
			contactHeader = binder.getHeaderFactory().createContactHeader(contactAddress);
			toHeader = binder.getHeaderFactory().createToHeader(toAddress, null);
			fromHeader = binder.getHeaderFactory().createFromHeader(fromAddres,
					"tag"+Math.random());
			callIdHeader = binder.getProvider().getNewCallId();
			maxForwardsHeader = binder.getHeaderFactory().createMaxForwardsHeader(70);
			contentTypeHeader = binder.getHeaderFactory().createContentTypeHeader("text",
					"plain");
			Address routeAddress = binder.getAddressFactory().createAddress("sip:"
					+ binder.getPeerAddres() + ":" + binder.getPeerPort());
			routeHeader = binder.getHeaderFactory().createRouteHeader(routeAddress);

		} catch (ParseException e) {
			
			e.printStackTrace();
			
		} catch (InvalidArgumentException e) {
			
			e.printStackTrace();
			
		}
		// LETS CREATE OUR REQUEST AND
		list = new ArrayList();
		list.add(viaHeader);
		requestURI = null;

		Request inviteRequest=null;
		try {
			requestURI = binder.getAddressFactory().createURI("sip:torosvi@nist.gov");
			register = binder.getMessageFactory().createRequest(requestURI,
					Request.INVITE, callIdHeader, cseqHeader, fromHeader,
					toHeader, list, maxForwardsHeader, contentTypeHeader,
					"INVITE".getBytes());
			register.addHeader(routeHeader);
			register.addHeader(contactHeader);
		} catch (ParseException e) {
			
			e.printStackTrace();
			
		}
		
		CT = null;
		//ClientTransaction CTCancel = null;
		try {
			CT = binder.getProvider().getNewClientTransaction(register);
			// dial=sipProvider.getNewDialog(CT);
			// dial=CT.getDialog();
		} catch (TransactionUnavailableException e) {
			
			e.printStackTrace();
			
		} catch (SipException e) {
			
			e.printStackTrace();
			
		}

		logger.info("========== REQUEST ============\n" + register
				+ "\n=====================================");
		// ATLAST SENT IT
		try {
			// dial.sendRequest(CT);
			CT.sendRequest();
			//dial = CTInvite.getDialog();

		} catch (SipException e) {
			
			e.printStackTrace();
			
		}
		
		
		

		
		
		
		
		try {
			thr=Thread.currentThread();
			Thread.currentThread().sleep(45000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
	}
	
	public void processRequest(RequestEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("SOMETHING WENT WRONG["+arg0+"]");
		binder.stop();
		thr.interrupt();
	}

	public void processResponse(ResponseEvent resp) {
		// TODO Auto-generated method stub
		Response response=resp.getResponse();
		
		if(response.getStatusCode()==Response.OK && ((CSeqHeader)response.getHeader(CSeqHeader.NAME)).getMethod().equals(Request.REGISTER))
		{
			//YUPI, GOT OK
			logger.info("SIP RA AND REGISTAR ARE DEPLOYED PROPERLY!!!");
			registerOkReceived=true;
		}else if(response.getStatusCode()==Response.FORBIDDEN&& ((CSeqHeader)response.getHeader(CSeqHeader.NAME)).getMethod().equals(Request.INVITE))
		{
			
			if(forbidenResceived)
				logger.info("SOMETHING WENT WRONG GOT RESPONSE SECOND TIME[\n"+response+"\n]");
			else
			{
				forbidenResceived=true;
				logger.info("GOT 403 RESPONSE FROM SERVICE, DEPLOYMENT SEEMS CORRECT[\n"+response+"\n]");
				
				binder.stop();
				thr.interrupt();
			}
		}else
		{
			logger.info("SOMETHING WENT WRONG, UNEXPECTED RESP[\n"+response+"\n]");
			binder.stop();
			thr.interrupt();
		}
		
	}

	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("SOMETHING WENT WRONG["+arg0+"]");
		
		binder.stop();
		thr.interrupt();
	}

	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("SOMETHING WENT WRONG["+arg0+"]");
		
		binder.stop();
		thr.interrupt();
	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("SOMETHING WENT WRONG["+arg0+"]");
		
		binder.stop();
		thr.interrupt();
	}

	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("SOMETHING WENT WRONG["+arg0+"]");
		
		binder.stop();
		thr.interrupt();
	}

}
