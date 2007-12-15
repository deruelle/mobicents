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
	public static void main(String[] args)
	{
		//HERE WE WILL SEND REGISTER REQUEST TO TEST deployment
		
		
		binder=SIPStackBinder.getInstance();
		binder.registerSipListener(new DeployTest());
		
		Request register=null; //This will be our request
		//WE NEED SOMETHING LIKE THIS
		//REGISTER sip:shadow.mobicents.org SIP/2.0
		//Via: SIP/2.0/UDP 192.168.1.101:14714
		//Max-Forwards: 70
		//From: <sip:baranowb@shadow.mobicents.org>;tag=0b6d440a7df348ac804c3a3fd7a717a5;epid=41d06eeece
		//To: <sip:baranowb@shadow.mobicents.org>
		//Call-ID: f9dcfc8669e24244a43357316908b8a4@192.168.1.101
		//CSeq: 1 REGISTER
		//Contact: <sip:192.168.1.101:14714>;methods="INVITE, MESSAGE, INFO, SUBSCRIBE, OPTIONS, BYE, CANCEL, NOTIFY, ACK, REFER"
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
					.createAddress("sip:fake_register@" + binder.getPeerAddres()+":"+binder.getPeerPort());
			// Address
			// toAddress=addressFactory.createAddress("sip:pingReceiver@"+peerAddres+":"+peerPort);
			Address toAddress = binder.getAddressFactory().createAddress("sip:fake_register@" + binder.getPeerAddres()+":"+binder.getPeerPort());
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

		Request inviteRequest=null;
		try {
			requestURI = binder.getAddressFactory().createURI("sip:" + binder.getStackAddress());
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
			Thread.currentThread().sleep(45000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			logger.info("SIP RA AND REGISTAR ARE DPLOYED PROPERLY!!!");
			System.out.println("SIP RA AND REGISTAR ARE DPLOYED PROPERLY!!!");
		}else
		{
			logger.info("SOMETHING WENT WRONG["+response+"]");
			System.out.println("SOMETHING WENT WRONG["+resp+"]");
		}
		binder.stop();
		thr.interrupt();
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
