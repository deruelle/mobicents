package org.mobicents.test;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
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
import javax.slee.AddressPlan;
import javax.slee.EventTypeID;
import javax.slee.connection.ExternalActivityHandle;
import javax.slee.connection.SleeConnection;

import org.mobicents.slee.connector.server.RemoteSleeService;

import se.jayway.sip.slee.event.ThirdPCCTriggerEvent;
import se.jayway.sip.util.DefaultStateCallback;


public class DeployTest implements SipListener {

	private static Logger logger=Logger.getLogger("DeployTest.class");
	private static SIPStackBinder binder=null;
	private static Thread thr=null;
	
	
	private static int invitesCount=0;
	private static SleeConnection connection=null;
    private static RemoteSleeService service=null;
    private static Request firstInvite,secondInvite;
    private static ServerTransaction lastTx=null;
    private static boolean passed=false;
    private static final byte[] sdp=("" +
    		"v=0\n"+
    		"o=- 0 0 IN IP4 192.168.1.100\n"+ //this is fake ip since we wont do anything with it
    		"s=session\n"+
    		"c=IN IP4 192.168.1.100\n"+
    		"b=CT:1000\n"+
    		"t=0 0\n"+
    		"m=audio 42738 RTP/AVP 97 111 112 6 0 8 4 5 3 101\n"+
    		"k=base64:H9PMXB1nFpu2pSkRVfFVUYHFUCamCl0MvpKpYGhsX94\n"+
    		"a=rtpmap:97 red/8000\n"+
    		"a=rtpmap:111 SIREN/16000\n"+
    		"a=fmtp:111 bitrate=16000\n"+
    		"a=rtpmap:112 G7221/16000\n"+
    		"a=fmtp:112 bitrate=24000\n"+
    		"a=rtpmap:6 DVI4/16000\n"+
    		"a=rtpmap:0 PCMU/8000\n"+
    		"a=rtpmap:8 PCMA/8000\n"+
    		"a=rtpmap:4 G723/8000\n"+
    		"a=rtpmap:5 DVI4/8000\n"+
    		"a=rtpmap:3 GSM/8000\n"+
    		"a=rtpmap:101 telephone-event/8000\n"+
    		"a=fmtp:101 0-16\n"+
    		"a=encryption:optional\n"+
    		"m=video 43220 RTP/AVP 34 31\n"+
    		"k=base64:uYZlKTIDG9oGjna19iJqB7t5fa7dLoX809MnO0PKHKw\n"+
    		"a=recvonly\n"+
    		"a=rtpmap:34 H263/90000\n"+
    		"a=rtpmap:31 H261/90000\n"+
    		"a=encryption:optional").getBytes();
	public static void main(String[] args)
	{
		// HERE WE WILL SEND REGISTER REQUEST TO TEST deployment
		
		
		binder=SIPStackBinder.getInstance();
		binder.registerSipListener(new DeployTest());
		
		// NOW LETS FIRE ThirdPCCTrigger event into SLEE - with both addresses
		// set to "us". This should make 3pcc to send 2xinvite to us.
		
		
		
		ThirdPCCTriggerEvent event=new ThirdPCCTriggerEvent();
		String address="sip:callme@"+binder.getStackAddress()+":"+binder.getPort();
		event.setCalleeURI(address);
		event.setCallerURI(address);
		event.setStateCallback(new DefaultStateCallback());
		
		
		
		
		
		try{
            Properties properties = new Properties();
            // JNDI lookup properties
            properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
            properties.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
            String tmpIP=binder.getPeerAddres();
            
            properties.put("java.naming.provider.url", "jnp://"+tmpIP+":1099");
            InitialContext ctx=new InitialContext(properties);
            
            // SleeConnectionFactory factory
			// =(SleeConnectionFactory)ctx.lookup("java:comp/env/slee/MySleeConnectionFactory");
            // Obtain a connection to the SLEE from the factory
            // SleeConnection connection = factory.getConnection();
            service=(RemoteSleeService)ctx.lookup("/SleeService");
        }catch(NamingException ne) {
            ne.printStackTrace();
        }catch(Exception E) {
            E.printStackTrace();
        }
        // }
        if(service!=null) {
            try {
                // Locate the event type, same data that
				// WakeUpRequest-event-jar.xml contains
                EventTypeID requestType = service.getEventTypeID("se.jayway.sip.slee.event.ThirdPCCTriggerEvent","Jayway","0.1");
                
                // Fire an asynchronous event
                ExternalActivityHandle handle = service.createActivityHandle();
                
                service.fireEvent(event, requestType, handle, null);
                
                // connection.endExternalActivity(handle);
            }catch(RemoteException RE) {
                System.out.println("REMOTE EXCEPTION!!!");
                RE.printStackTrace();
            }
        }       
		
		
		
		
		
		try {
			thr=Thread.currentThread();
			Thread.currentThread().sleep(45000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		binder.stop();
		
		if(passed)
			logger.info("\n=============================================\n" +
					"LOOKS LIKE IT WORKS" +
						"\n=============================================");
	}
	
	public void processRequest(RequestEvent req) {
		// TODO Auto-generated method stub
		Request request=req.getRequest();
		String method=request.getMethod();
		if(method.equals(Request.INVITE) )
		{
			invitesCount++;
			
			logger.info("GOT INVITE");
			if(invitesCount==1)
				firstInvite=request;
			if(invitesCount==2)
			{
				
				secondInvite=request;
				if(secondInvite.equals(firstInvite))
				{
					logger.info("SECOND INVITE EQUALS FIRST, SOMETHING IS WRONG");
				}else
				{
					logger.info("THIS IS SECOND INVITE -> 3pc INVEITED US TWICE, ITS WORKING");
					passed=true;
				}
			}
			if(invitesCount>2)
			{
				return;
			}
			
			// SEND OK
			try {
				ServerTransaction stx=req.getServerTransaction();
				if(stx==null)
					stx=binder.getProvider().getNewServerTransaction(req.getRequest());
				Response ok=binder.getMessageFactory().createResponse(Response.OK,request);
				ToHeader toHdr=(ToHeader)ok.getHeader(ToHeader.NAME);
				toHdr.setTag(""+Math.random());
				ok.removeHeader(ContactHeader.NAME);
				Address contactAddress = binder.getAddressFactory().createAddress("sip:"+binder.getStackAddress()+":"+binder.getPort());
				ContactHeader contact=binder.getHeaderFactory().createContactHeader(contactAddress);
				ContentTypeHeader contentType=binder.getHeaderFactory().createContentTypeHeader("application","sdp");
				ok.addHeader(contact);
		
				ok.setContent(sdp,contentType);
				logger.info("\n================================\nRESPONSE:\n==========================\n"+ok);
				

				lastTx=stx;

				stx.sendResponse(ok);
				if(passed)
					thr.interrupt();
				
			} catch (ParseException e) {
				logger.info(">>>>>>>>>>>>>>>>>>."+e.toString());
				e.printStackTrace();
			} catch (SipException e) {
				logger.info(">>>>>>>>>>>>>>>>>>."+e.toString());
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				logger.info(">>>>>>>>>>>>>>>>>>."+e.toString());
				e.printStackTrace();
			}catch(Exception e)
			{
				logger.info(">>>>>>>>>>>>>>>>>>."+e.toString());
				e.printStackTrace();
			}
		}else if(method.equals(Request.ACK))
		{
			logger.info("GOT ACK");
		}else
		{
		logger.info("SOMETHING WENT WRONG["+req+"]");
		// binder.stop();
		thr.interrupt();
		}
	}

	public void processResponse(ResponseEvent resp) {
		// TODO Auto-generated method stub
		Response response=resp.getResponse();
		
		if(response.getStatusCode()==Response.OK && ((CSeqHeader)response.getHeader(CSeqHeader.NAME)).getMethod().equals(Request.REGISTER))
		{
			// YUPI, GOT OK
			logger.info("SIP RA AND REGISTAR ARE DPLOYED PROPERLY!!!");
			System.out.println("SIP RA AND REGISTAR ARE DPLOYED PROPERLY!!!");
		}else
		{
			logger.info("SOMETHING WENT WRONG["+response+"]");
			System.out.println("SOMETHING WENT WRONG["+resp+"]");
		}
		// binder.stop();
		thr.interrupt();
	}

	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub
		// logger.info("SOMETHING WENT WRONG["+arg0+"]");
		
		// binder.stop();
		// thr.interrupt();
	}

	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("SOMETHING WENT WRONG["+arg0+"]");
		
		// binder.stop();
		thr.interrupt();
	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		// if(lastTx.equals(arg0.))
		// logger.info("SOMETHING WENT WRONG["+arg0+"]");
		
		// binder.stop();
		// thr.interrupt();
	}

	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("SOMETHING WENT WRONG["+arg0+"]");
		
		// binder.stop();
		thr.interrupt();
	}

}
