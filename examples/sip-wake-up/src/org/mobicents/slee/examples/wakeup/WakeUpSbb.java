package org.mobicents.slee.examples.wakeup;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sip.ClientTransaction;
import javax.sip.InvalidArgumentException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;


import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.ComponentID;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.facilities.AlarmFacility;
import javax.slee.facilities.Level;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TraceFacility;
import javax.slee.nullactivity.NullActivity;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;
import javax.slee.serviceactivity.ServiceActivity;

import org.apache.log4j.Logger;
import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipFactoryProvider;
import org.mobicents.slee.services.sip.common.SipSendErrorResponseException;
import org.mobicents.slee.services.sip.registrar.LocationService;
import org.mobicents.slee.services.sip.registrar.LocationServiceException;

import org.mobicents.slee.services.sip.registrar.RegistrationBinding;





public abstract class WakeUpSbb implements javax.slee.Sbb {
	
	
	private static Logger logger=Logger.getLogger(WakeUpSbb.class);
	private LocationService locationService;

	
	
	
	public void onMessageEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {
		Request request = event.getRequest();

		try {
        	// Notifiy the client that we received the SIP MESSAGE request
			ServerTransaction st = (ServerTransaction) aci.getActivity();
			Response response = messageFactory.createResponse(Response.OK, request);
    		st.sendResponse(response);
    		
    		//   CREATE A NEW NULL ACTIVITIY
    		NullActivity timerBus = this.nullActivityFactory.createNullActivity();
    		
        	// ATTACH ITSELF TO THE NULL ACTIVITY 
        	// BY USING THE ACTIVITY CONTEXT INTERFACE
        	ActivityContextInterface timerBusACI = 
        		this.nullACIFactory.getActivityContextInterface(timerBus);
			timerBusACI.attach(sbbContext.getSbbLocalObject());
			
			// PARSING THE MESSAGE BODY
			String body = new String(request.getRawContent());
			int i = body.indexOf(" ");
			String timerValue = body.substring(0,i);
			int timer = Integer.parseInt(timerValue);
			String bodyMessage = body.substring(i+1);
			
			// SETTING VALUES ON THE ACTIVITY CONTEXT
			// USING THE SBB CUSTOM ACI
			WakeUpSbbActivityContextInterface myViewOfTimerBusACI = 
				this.asSbbActivityContextInterface(timerBusACI);
			myViewOfTimerBusACI.setBody(bodyMessage);
			// The From field of each SIP MESSAGE has the UA Address of Record (logical address), 
			// which can be mapped to a current physical contact address. The mapping is provided by the LocationService,
			// which works together with the SIP Registrar service.
			FromHeader fromHeader = (FromHeader)request.getHeader(FromHeader.NAME);
			Address fromAddress = fromHeader.getAddress();
			URI contactURI = findLocalTarget(fromHeader.getAddress().getURI());
			Address contactAddress = addressFactory.createAddress(contactURI);
			ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);

			myViewOfTimerBusACI.setContact(contactHeader);
			
			// SETTING THE TIMER BY USING THE VALUE 
			// IN THE SIP MESSAGE BODY
			TimerOptions options = new TimerOptions();
			options.setPersistent(true);
			this.timerFacility.setTimer(timerBusACI, 
					null, 
					System.currentTimeMillis()+timer*1000, 
					options);
			
		} catch (Exception e) {
			this.trace(Level.WARNING, 
					"Exception while retrieveing Activity Context Interface: ", e);
		}
	}
		
	
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
		// RETRIEVING STORED VALUE FROM THE ACTIVITY CONTEXT INTERFACE
		WakeUpSbbActivityContextInterface myViewOfACI = 
			this.asSbbActivityContextInterface(aci);
		Header contact = myViewOfACI.getContact();
		String body = myViewOfACI.getBody();
		
		// SENDING BACK THE WAKE UP CALL
		sendWakeUpCall(contact, body);
	}
	
	
	
	private void sendWakeUpCall(Header toContact, String body) {
		String strContact = toContact.toString();
		int beginIndex = strContact.indexOf('<');
		int endIndex = strContact.indexOf('>');
		String toAddressStr = strContact.substring(beginIndex+1, endIndex);
		try {
		SipURI fromAddress =
			addressFactory.createSipURI("wakeup", "nist.gov");

		javax.sip.address.Address fromNameAddress = addressFactory.createAddress(fromAddress);
		fromNameAddress.setDisplayName("WakeUp");
		FromHeader fromHeader =
			headerFactory.createFromHeader(fromNameAddress, "12345SomeTagID6789");
		
		javax.sip.address.Address toNameAddress = addressFactory.createAddress(toAddressStr);
		toNameAddress.setDisplayName("Some Sleepy User");
		
		ToHeader toHeader =
			headerFactory.createToHeader(toNameAddress, null);
		
		ArrayList viaHeaders = new ArrayList();
		
		ViaHeader viaHeader =
			headerFactory.createViaHeader(
					provider.getListeningPoints()[0].getIPAddress(),
				provider.getListeningPoints()[0].getPort(),
				provider.getListeningPoints()[0].getTransport(),
				null);

		// add via headers
		viaHeaders.add(viaHeader);
		
		MaxForwardsHeader maxForwards =
			this.headerFactory.createMaxForwardsHeader(70);
		
		
			URI uri = fp.getAddressFactory().createURI(toAddressStr);
			Request req = messageFactory.createRequest(uri, 
					Request.MESSAGE,
					this.provider.getNewCallId(),
					headerFactory.createCSeqHeader(1, Request.MESSAGE),
					fromHeader,
					toHeader,
					viaHeaders,
					maxForwards);
			ContentTypeHeader contentType = headerFactory.createContentTypeHeader("text", "plain");
			req.setContent(body,contentType);
			ClientTransaction ct = provider.getNewClientTransaction(req);
			ct.sendRequest();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public MessageFactory getMessageFactory() { return messageFactory; }

	
	/**
	 *  Initialize the component
	 */
	public void setSbbContext(SbbContext context) { 
		this.sbbContext = context;
		try {
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            // Storing Sbb Component ID
            id = sbbContext.getSbb();
            
            // Getting SLEE Factility
            traceFacility = (TraceFacility) myEnv.lookup("slee/facilities/trace");
            timerFacility = (TimerFacility) myEnv.lookup("slee/facilities/timer");
            alarmFacility = (AlarmFacility) myEnv.lookup("slee/facilities/alarm");
            nullACIFactory = (NullActivityContextInterfaceFactory)myEnv.
            				lookup("slee/nullactivity/activitycontextinterfacefactory");
            nullActivityFactory = (NullActivityFactory)myEnv.lookup("slee/nullactivity/factory");
            
            // Getting JAIN SIP Resource Adaptor interfaces            
            fp = (SipFactoryProvider) myEnv.lookup("slee/resources/jainsip/1.2/provider");
            
            provider = fp.getSipProvider();
            
            addressFactory = fp.getAddressFactory();
            headerFactory = fp.getHeaderFactory();
            messageFactory = fp.getMessageFactory();
            
            acif = (SipActivityContextInterfaceFactory) myEnv.
            		lookup("slee/resources/jainsip/1.2/acifactory");
            
        } catch (NamingException ne) {
            this.trace(Level.WARNING, "Exception During setSbbContext", ne);
            
        }
		
	}
    public void unsetSbbContext() { this.sbbContext = null; }
    
    // TODO: Implement the lifecycle methods if required
    public void sbbCreate() throws javax.slee.CreateException {}
    public void sbbPostCreate() throws javax.slee.CreateException { setup(); }
    public void sbbActivate() {}
    public void sbbPassivate() {}
    public void sbbRemove() {}
    public void sbbLoad() { setup(); }
    public void sbbStore() {}
    public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {}
    public void sbbRolledBack(RolledBackContext context) {}
	
	public abstract org.mobicents.slee.examples.wakeup.WakeUpSbbActivityContextInterface asSbbActivityContextInterface(ActivityContextInterface aci);

    private void setup() {
    	locationService = new LocationService(); 
    }

    /**
     * Attempts to find a locally registered contact address for the given URI,
     * using the location service interface.
     */
    public URI findLocalTarget(URI uri) throws SipSendErrorResponseException {
        String addressOfRecord = uri.toString();

        Map bindings = null;
        try {
            bindings = locationService.getBindings(addressOfRecord);
        } catch (LocationServiceException lse) {
            lse.printStackTrace();
        }

        if (bindings == null) {
            throw new SipSendErrorResponseException("User not found",
                    Response.NOT_FOUND);
        }
        if (bindings.isEmpty()) {
            throw new SipSendErrorResponseException(
                    "User temporarily unavailable",
                    Response.TEMPORARILY_UNAVAILABLE);
        }

        Iterator it = bindings.values().iterator();
        URI target = null;
        while (it.hasNext()) {
            RegistrationBinding binding = (RegistrationBinding) it.next();
            System.out.println("BINDINGS: " + binding);
            ContactHeader header = binding.getContactHeader(addressFactory, headerFactory);
            System.out.println("CONTACT HEADER: " + header);
            if (header == null) { // entry expired
                continue; // see if there are any more contacts...
            }
            Address na = header.getAddress();
            System.out.println("Address: " + na);
            target = na.getURI();
            break;
        }
        if (target == null) {
            System.err.println("findLocalTarget: No contacts for "
                    + addressOfRecord + " found.");
            throw new SipSendErrorResponseException(
                    "User temporarily unavailable",
                    Response.TEMPORARILY_UNAVAILABLE);
        }
        return target;
    }
    
	
	/**
	 * Convenience method to retrieve the SbbContext object stored in setSbbContext.
	 * 
	 * TODO: If your SBB doesn't require the SbbContext object you may remove this 
	 * method, the sbbContext variable and the variable assignment in setSbbContext().
	 *
	 * @return this SBB's SbbContext object
	 */
	 
	protected SbbContext getSbbContext() {
		
		return sbbContext;
	}

	private SbbContext sbbContext; // This SBB's SbbContext
	
	protected final void trace(Level level, String message) {
        try {
            traceFacility.createTrace(id, level, "WakeUpSbb", message, System.currentTimeMillis());
       } catch (Exception e) { }
    }
	
	protected final void trace(Level level, String message, Throwable t) {
        try {
            traceFacility.createTrace(id, level, "WakeUpSbb", message, t, System.currentTimeMillis());
        } catch (Exception e) { }
    }
	
	private MessageFactory messageFactory;
	private SipProvider provider;
	private SipFactoryProvider fp;
	private SipActivityContextInterfaceFactory acif;
	private TraceFacility traceFacility;
	private TimerFacility timerFacility;
	private AlarmFacility alarmFacility;
	private NullActivityContextInterfaceFactory nullACIFactory;
	private NullActivityFactory nullActivityFactory;
	private ComponentID id;
	private AddressFactory addressFactory;
	private HeaderFactory headerFactory;
	
	
	
	
	
	
	
	
	//DARK CORNER
	
	public void onStartServiceEvent(
			javax.slee.serviceactivity.ServiceStartedEvent event,
			ActivityContextInterface aci) {
		logger.info("onStartServiceEvent");
		//HERE WE WILL REGISTER sip:wakeup@nist.gov in locationService so proxy knows about us :)
		
		
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
		
		String localAddress=fp.getSipProvider().getListeningPoints()[0].getIPAddress();
		int localPort=fp.getSipProvider().getListeningPoints()[0].getPort();
		String localTransport=fp.getSipProvider().getListeningPoints()[0].getTransport();
				
		try {
			cseqHeader = fp.getHeaderFactory().createCSeqHeader(1, Request.REGISTER);
			viaHeader = fp.getHeaderFactory().createViaHeader(localAddress, localPort,
					localTransport, null);
			Address fromAddres = fp.getAddressFactory()
					.createAddress("sip:wakeup@nist.gov");
			// Address
			// toAddress=addressFactory.createAddress("sip:pingReceiver@"+peerAddres+":"+peerPort);
			Address toAddress = fp.getAddressFactory().createAddress("sip:wakeup@nist.gov");
			contactHeader = fp.getHeaderFactory().createContactHeader(fp.getAddressFactory()
					.createAddress("sip:"+localAddress+":"+localPort));
			//WE RELLY ON DEFUALT EXPIRES VALUE HERE
			//contactHeader.setExpires(3600);
			toHeader = fp.getHeaderFactory().createToHeader(toAddress, null);
			fromHeader = fp.getHeaderFactory().createFromHeader(fromAddres,
					"wakeupER");
			callIdHeader = fp.getSipProvider().getNewCallId();
			maxForwardsHeader = fp.getHeaderFactory().createMaxForwardsHeader(70);
			contentTypeHeader = fp.getHeaderFactory().createContentTypeHeader("text",
					"plain");
			Address routeAddress = fp.getAddressFactory().createAddress("sip:"
					+ localAddress + ":" + localPort);
			routeHeader = fp.getHeaderFactory().createRouteHeader(routeAddress);

		} catch (ParseException e) {
	
			e.printStackTrace();
			
		} catch (InvalidArgumentException e) {

			e.printStackTrace();
			
		}
		// LETS CREATE OUR REQUEST AND
		ArrayList list = new ArrayList();
		list.add(viaHeader);
		URI requestURI = null;
		Request request = null;

		try {
			requestURI = fp.getAddressFactory().createURI("sip:nist.gov");
			 request = fp.getMessageFactory().createRequest(requestURI,
					Request.REGISTER, callIdHeader, cseqHeader, fromHeader,
					toHeader, list, maxForwardsHeader, contentTypeHeader,
					"REGISTER ME, PLEASE".getBytes());
			request.addHeader(routeHeader);
			request.addHeader(contactHeader);
		} catch (ParseException e) {
	
			e.printStackTrace();
			
		}
		
		
		
		ClientTransaction ctx;
		try {
			ctx = fp.getSipProvider().getNewClientTransaction(request);
			ctx.sendRequest();
		} catch (TransactionUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		/*
		 * REGISTER sip:nist.gov SIP/2.0
		 * Via: SIP/2.0/UDP 192.168.1.101:11534
		 * Max-Forwards: 70
		 * From: <sip:baranowb@nist.gov>;tag=a6c42e896f8743e8b3c9260887bc6840;epid=7be998ef8c
		 * To: <sip:baranowb@nist.gov>
		 * Call-ID: 73924c2ed5644c658b547f0f11f1f891
		 * CSeq: 1 REGISTER
		 * Contact: <sip:192.168.1.101:11534>;methods="INVITE, MESSAGE, INFO, SUBSCRIBE, OPTIONS, BYE, CANCEL, NOTIFY, ACK, REFER, BENOTIFY"
		 * User-Agent: RTC/1.3.5470 (Messenger 5.1.0701)
		 * Event:  registration
		 * Allow-Events: presence
		 * Content-Length: 0
		 */
		
		
	}


	public void onActivityEndEvent(ActivityEndEvent event,
			ActivityContextInterface aci) {
		//WE WANT TO KNOW WHEN SERICE IS GOING DOWN TO UNREGISTER sip:wakeup@nist.gov FROM LOCATION SERVICE
		
		
		Object activity=aci.getActivity();
		if(activity instanceof ServiceActivity)
		{
			//TODO:UNBIND from location
			
		
			
			
			
			
			
			
			
			ContactHeader contactHeader = null;
			ToHeader toHeader = null;
			FromHeader fromHeader = null;
			CSeqHeader cseqHeader = null;
			ViaHeader viaHeader = null;
			CallIdHeader callIdHeader = null;
			MaxForwardsHeader maxForwardsHeader = null;
			ContentTypeHeader contentTypeHeader = null;
			RouteHeader routeHeader = null;
			ExpiresHeader expires=null;
			// LETS CREATEOUR HEADERS
			
			String localAddress=fp.getSipProvider().getListeningPoints()[0].getIPAddress();
			int localPort=fp.getSipProvider().getListeningPoints()[0].getPort();
			String localTransport=fp.getSipProvider().getListeningPoints()[0].getTransport();
					
			try {
				cseqHeader = fp.getHeaderFactory().createCSeqHeader(1, Request.REGISTER);
				viaHeader = fp.getHeaderFactory().createViaHeader(localAddress, localPort,
						localTransport, null);
				Address fromAddres = fp.getAddressFactory()
						.createAddress("sip:wakeup@nist.gov");
				// Address
				// toAddress=addressFactory.createAddress("sip:pingReceiver@"+peerAddres+":"+peerPort);
				Address toAddress = fp.getAddressFactory().createAddress("sip:wakeup@nist.gov");
				contactHeader = fp.getHeaderFactory().createContactHeader(fromAddres);
				toHeader = fp.getHeaderFactory().createToHeader(toAddress, null);
				fromHeader = fp.getHeaderFactory().createFromHeader(fromAddres,
						"wakeupER");
				callIdHeader = fp.getSipProvider().getNewCallId();
				maxForwardsHeader = fp.getHeaderFactory().createMaxForwardsHeader(70);
				contentTypeHeader = fp.getHeaderFactory().createContentTypeHeader("text",
						"plain");
				Address routeAddress = fp.getAddressFactory().createAddress("sip:"
						+ localAddress + ":" + localPort);
				routeHeader = fp.getHeaderFactory().createRouteHeader(routeAddress);
				expires=fp.getHeaderFactory().createExpiresHeader(0);
			} catch (ParseException e) {
		
				e.printStackTrace();
				
			} catch (InvalidArgumentException e) {

				e.printStackTrace();
				
			}
			// LETS CREATE OUR REQUEST AND
			ArrayList list = new ArrayList();
			list.add(viaHeader);
			URI requestURI = null;
			Request request = null;
			
			try {
				requestURI = fp.getAddressFactory().createURI("sip:nist.gov");
				 request = fp.getMessageFactory().createRequest(requestURI,
						Request.REGISTER, callIdHeader, cseqHeader, fromHeader,
						toHeader, list, maxForwardsHeader, contentTypeHeader,
						"REGISTER ME PLEASE".getBytes());
				request.addHeader(routeHeader);
				request.addHeader(contactHeader);
				request.addHeader(expires);
			} catch (ParseException e) {
		
				e.printStackTrace();
				
			}
			
			

			/*
			 * REGISTER sip:nist.gov SIP/2.0
			 * Via: SIP/2.0/UDP 192.168.1.101:11534
			 * Max-Forwards: 70
			 * From: <sip:baranowb@nist.gov>;tag=a6c42e896f8743e8b3c9260887bc6840;epid=7be998ef8c
			 * To: <sip:baranowb@nist.gov>
			 * Call-ID: 73924c2ed5644c658b547f0f11f1f891
			 * CSeq: 1 REGISTER
			 * Contact: <sip:192.168.1.101:11534>;methods="INVITE, MESSAGE, INFO, SUBSCRIBE, OPTIONS, BYE, CANCEL, NOTIFY, ACK, REFER, BENOTIFY"
			 * User-Agent: RTC/1.3.5470 (Messenger 5.1.0701)
			 * Event:  registration
			 * Allow-Events: presence
			 * Expires: 0
			 * Content-Length: 0
			 */
			
			
			ClientTransaction ctx;
			try {
				ctx = fp.getSipProvider().getNewClientTransaction(request);
				ctx.sendRequest();
			} catch (TransactionUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			
			
			
			
			
		}else
		{
			//WE DONT CARE :), propably tx's are ending their lifespan...
		}
	}
	
}
