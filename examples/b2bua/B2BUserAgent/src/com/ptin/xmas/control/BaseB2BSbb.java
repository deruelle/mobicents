/*
 * Created on 24/Ago/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.ptin.xmas.control;


import java.util.*;

import com.ptin.xmas.control.common.*;
import com.ptin.xmas.control.common.interfaces.*;
import com.ptin.xmas.control.events.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.ClientTransaction;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.*;

import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipFactoryProvider;
//import org.mobicents.slee.resource.xmpp.XmppResourceAdaptor.XmppRASbbInterfaceImpl;
//import org.jivesoftware.smack.packet.*;

import javax.slee.nullactivity.NullActivity;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;
import javax.slee.facilities.*;

public abstract class BaseB2BSbb implements javax.slee.Sbb {
	private SbbContext sbbContext; // This SBB's SbbContext
    private SipFactoryProvider fp;
    private SipProvider provider;
    private AddressFactory addressFactory;
    private HeaderFactory headerFactory;
    private MessageFactory messageFactory;
    private SipActivityContextInterfaceFactory acif;
    //private XmppRASbbInterfaceImpl xmppSbbInterface;
    private B2BCallController b2bCallController;
	private NullActivityContextInterfaceFactory nullACIFactory;
	private NullActivityFactory nullActivityFactory;
	private ActivityContextNamingFacility acNamingFacility;
	    
    //private ManagerListener managerListener;
    
    private class B2BCallControllerImpl implements B2BCallController {
        private Hashtable inCallsList;
        
        public B2BCallControllerImpl() {
            inCallsList = getInCallsList();
        }
        
        public void hangup(String callId) {
            InCall selectedCall = (InCall) inCallsList.get(callId);
            selectedCall.hangup();
            System.out.println("Hung up call with ID = " + callId);            
        }
        
        public void forward(String sourceUri, String destinationUri, String finalToUri, String domain) {}
        
        public String getTo(String callId) {
            InCall selectedCall = (InCall) inCallsList.get(callId);
            
            return selectedCall.getTo();
        }
        
        public String getFrom(String callId) {
            InCall selectedCall = (InCall) inCallsList.get(callId);
            
            return selectedCall.getFrom();            
        }
        
        public void reject(String callId) {
            InCall selectedCall = (InCall) inCallsList.get(callId);
            
            selectedCall.reject();
        }
    }
    
    private boolean processRequestOnListener(RequestEvent requestReceivedEvent) {
	    Hashtable inCallLegsList = getInCallLegsList();
	    Hashtable outCommLegsList = getOutCommLegsList();
        CallManager manager = getManager();
        
        Request request = (Request) requestReceivedEvent.getRequest();

        ServerTransaction serverTransaction = requestReceivedEvent
                .getServerTransaction();
        
        /*
        if (serverTransaction == null) {
            //SystemInfo.log.debug("processRequestOnListener serverTransaction == null");
            System.out.println("processRequestOnListener serverTransaction == null");
            try {
                serverTransaction = provider.getNewServerTransaction(request);
 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        */
        
        if(serverTransaction == null) { // one last measure to attempt to get the server transaction
            serverTransaction = getServerTransactionFromACI();
        }
        
        Leg inCallLeg = (Leg) inCallLegsList.get(((CallIdHeader) request
                .getHeader(CallIdHeader.NAME)).getCallId());
        Leg outCommLeg = (Leg) outCommLegsList.get(((CallIdHeader) request
                .getHeader(CallIdHeader.NAME)).getCallId());

        try {
            if ((inCallLeg != null) && ((CSeqHeader) request.getHeader(CSeqHeader.NAME))
                            .getSequenceNumber() != 1) {
                /*SystemInfo.log
                        .debug("processRequestOnListener inCallLeg != null");*/
                System.out.println("processRequestOnListener inCallLeg != null");
                inCallLeg.processRequest(request, serverTransaction);
                return true;
            } else if ((outCommLeg != null)
                    && ((CSeqHeader) request.getHeader(CSeqHeader.NAME))
                            .getSequenceNumber() != 1) {
                /*SystemInfo.log
                        .debug("processRequestOnListener inCallLeg != null");*/
                System.out.println("processRequestOnListener inCallLeg != null");
                outCommLeg.processRequest(request, serverTransaction);
                return true;
            } else {
                if (!request.getMethod().equals(Request.INVITE)) //mod request.INVITE
                    return false;

                manager.processInvite(request, serverTransaction);//app.processInvite(request, serverTransaction); 
                return true;
                //  }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public ServerTransaction getServerTransactionFromACI() {
        // We should only ever be attached to two activities (our client and server transactions) so find the attached activity
        // that is a server transaction
        ActivityContextInterface myacis[] = getSbbContext().getActivities();
        for (int i = 0; i < myacis.length; i++) {
            ActivityContextInterface myaci = myacis[i];
            if (myaci.getActivity() instanceof ServerTransaction) {
                return (ServerTransaction) myaci.getActivity();
            }
        }
        //trace(Level.WARNING, "Could not find an attached ServerTransaction");
        return null;
    }
	
    public void onInviteRequestEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {
        CallManager manager = getManager();
        manager.setSipProvider(provider);
        
	    try {
	        if (!processRequestOnListener(event)) {
		        if (((Request) event.getRequest()).getMethod().equalsIgnoreCase(Request.INVITE)) {
	                System.out.println("Fim do commProvider 1.3");
	                ServerTransaction serverTransaction = event.getServerTransaction();
	                /*
	                if (serverTransaction == null) {
	                    try {
	                        System.out.println("Fim do commProvider 1.5");
	                        serverTransaction = provider.getNewServerTransaction(event.getRequest());
	                        System.out.println("Fim do commProvider 1.7");
	                    } catch (Exception e) {
	                        System.out.println("" + e);
	                    }
	                }
	                */
	                if(serverTransaction == null) // one last measure to attempt to get the server transaction
	                    serverTransaction = getServerTransactionFromACI();
	                
	                System.out.println(" commProvider " + (((ToHeader) event.getRequest().getHeader(
	                                        ToHeader.NAME)).getAddress().toString()).split("@")[0]);
	                manager.processInvite((Request) event.getRequest(), serverTransaction);	        
		    }	        
	      }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }  
	}
	
	private void processResponse(ResponseEvent responseEvent, ActivityContextInterface aci) {
        System.out.println("\n\nResponse received \n"
                + responseEvent.getResponse() + " \n");

        ServerTransaction x = null;
        //System.out.println("[CommProviderImpl] Response Received...");
        Response response = responseEvent.getResponse();
        ClientTransaction clientTransaction = responseEvent.getClientTransaction();
        
        try {
            if (((CSeqHeader) response.getHeader(CSeqHeader.NAME)).getMethod()
                    .equalsIgnoreCase((Request.REGISTER))) {
                //managerListener.processResponse2Register(response,clientTransaction);
            } else if (!processResponseOnListener(response, clientTransaction)) {
            } //System.out.println("not registered on CommProvider
              // application");
            if (response.getStatusCode() == Response.OK
                    && response.getHeader(CSeqHeader.NAME).toString()
                            .equalsIgnoreCase("Bye")) {
                //  System.out.println("Response Ok to Bye received, but the call
                // no longer exists...");
            }
            /*
            int status_code = response.getStatusCode();
            String header_name = response.getHeader(CSeqHeader.NAME).toString();
            String method_name = null;
            StringTokenizer strTok = new StringTokenizer(header_name);
            for(int i = 0; i < 3; i++)
                method_name = strTok.nextToken();
            if(response.getStatusCode() == Response.OK && (method_name.compareTo("INVITE") == 0)) {
                // Setting a perpetual timer:
    			
                TimerOptions options = new TimerOptions();
    			options.setPersistent(true);
    			TimerID timerID = this.timerFacility.setTimer(aci, null,
    			        System.currentTimeMillis(), 1000, 0, options);
    			
                B2BSbbActivityContextInterface myViewOfAci = this.asSbbActivityContextInterface(aci);
                String callIdHeader = responseEvent.getResponse().getHeader(CallIdHeader.NAME).toString();
                String callId = null;
                strTok = new StringTokenizer(callIdHeader);
                for(int i = 0; i < 2; i++)
                    callId = strTok.nextToken();
                
                myViewOfAci.setCallId(callId);
                myViewOfAci.setTimerID(timerID);
            }
        	*/

        }//catch(SipParseException spe){DebugProxy.println(""+spe);}
        catch (Exception e) {
            System.out.println("" + e);
        }
    }
	   
	private boolean processResponseOnListener(Response response,
            ClientTransaction clientTransaction) {

	    Hashtable inCallLegsList = getInCallLegsList();
	    Hashtable outCommLegsList = getOutCommLegsList();
	    
        Leg inCallLeg = (Leg) inCallLegsList.get(((CallIdHeader) response
                .getHeader(CallIdHeader.NAME)).getCallId());
        Leg outCommLeg = (Leg) outCommLegsList.get(((CallIdHeader) response
                .getHeader(CallIdHeader.NAME)).getCallId());
        boolean isLeg = false;

        if (!(inCallLeg == null)) {
            System.out.println("processResponseOnListener inCallLeg != null");
            inCallLeg.processResponse(response, clientTransaction);
            isLeg = true;
        }
        if (!(outCommLeg == null)) {
            System.out.println("processResponseOnListener outCommLeg != null");
            outCommLeg.processResponse(response, clientTransaction);
            isLeg = true;
        }
        return isLeg;
    }

	public void onAckRequestEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {
        Request request = event.getRequest();
	    /*	
        TimerOptions options = new TimerOptions();
        options.setPersistent(true);
		TimerID timerID = this.timerFacility.setTimer(aci, null, System.currentTimeMillis(), 1000, 0, options);
			
		B2BSbbActivityContextInterface myViewOfAci = this.asSbbActivityContextInterface(aci);
		String callIdHeader = request.getHeader(CallIdHeader.NAME).toString();
		String callId = null;
		StringTokenizer strTok = new StringTokenizer(callIdHeader);
		for(int i = 0; i < 2; i++)
		    callId = strTok.nextToken();
            
		myViewOfAci.setCallId(callId);
		myViewOfAci.setTimerID(timerID);
		*/
        String callIdHeader = request.getHeader(CallIdHeader.NAME).toString();
        String callId = null;
        StringTokenizer strTok = new StringTokenizer(callIdHeader);
        for(int i = 0; i < 2; i++)
            callId = strTok.nextToken();
        
        CallEstablishedEvent evt = new CallEstablishedEvent(callId, getInCallsList());
        fireCallEstablishedEvent(evt, aci, null);
	}

	public void onCancelRequestEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {
	}

	public void onByeRequestEvent(javax.sip.RequestEvent event, ActivityContextInterface aci) {
        if (!processRequestOnListener(event)) {
            System.out.println("Bye received for a call that does not exist anymore...");
        }
        else {
        	// Attach itself to the null activity:
    		try {
    		    /*
    		    ActivityContextInterface timerBusACI = 
    		        this.nullACIFactory.getActivityContextInterface(getTimerBus());
    		    */
    		    //timerBusACI.attach(sbbContext.getSbbLocalObject());
    		    
    		    //B2BSbbActivityContextInterface myViewOfAci = this.asSbbActivityContextInterface(aci);
    		    
    		    /*
    		    // Stopping the timer: 
    		    TimerID timerID = myViewOfAci.getTimerID();
    		    if(timerID != null)
    		        this.timerFacility.cancelTimer(timerID);
    		    */
    		    Request request = event.getRequest();
    		    
    		    String callIdHeader = request.getHeader(CallIdHeader.NAME).toString();
    	        String callId = null;
    	        StringTokenizer strTok = new StringTokenizer(callIdHeader);
    	        for(int i = 0; i < 2; i++)
    	            callId = strTok.nextToken();
    	        
    	        ByeEvent evt = new ByeEvent(callId, getInCallsList());
    	        
    	        fireByeEvent(evt, aci, null);
    		} catch(Exception e) {
    		    e.printStackTrace();
    		}
        }
	}

	public void onInformationalResponseEvent(javax.sip.ResponseEvent event, ActivityContextInterface aci) {
	    processResponse(event, aci);	    
	}

	public void onSuccessResponseEvent(javax.sip.ResponseEvent event, ActivityContextInterface aci) {
	    processResponse(event, aci);	    
	}

	public void onRedirectionResponseEvent(javax.sip.ResponseEvent event, ActivityContextInterface aci) {
	    processResponse(event, aci);    
	}

	public void onClientErrorResponseEvent(javax.sip.ResponseEvent event, ActivityContextInterface aci) {
	    processResponse(event, aci);	    
	}

	public void onServerErrorResponseEvent(javax.sip.ResponseEvent event, ActivityContextInterface aci) {
	    processResponse(event, aci);	    
	}

	public void onGlobalFailureResponseEvent(javax.sip.ResponseEvent event, ActivityContextInterface aci) {
	    processResponse(event, aci);	    
	}

	public void onServerInternalErrorResponseEvent(javax.sip.ResponseEvent event, ActivityContextInterface aci) {
	    processResponse(event, aci);	    
	}

	public void onTransactionTimeoutEvent(javax.sip.TimeoutEvent event, ActivityContextInterface aci) {
	}

	public void onRetransmitTimeoutEvent(javax.sip.TimeoutEvent event, ActivityContextInterface aci) {
	}
	/*
    public void onMessage(Message event, ActivityContextInterface aci) {
        String inMsg;
        String outMsg = new String();
        Hashtable inCallsList = getInCallsList();
        Enumeration inCallsEnum = inCallsList.elements();
        inMsg = event.getBody();
        StringTokenizer strTok = new StringTokenizer(inMsg);
                
        if(inMsg.compareTo("SHOW CALLS") == 0) {
            outMsg = "Currently active calls: \n\n";
            while(inCallsEnum.hasMoreElements())
                outMsg += ((InCall) inCallsEnum.nextElement()).getCallId() + "\n";
        }
        else {
            String command = strTok.nextToken();
            String arg = strTok.nextToken();
            
            if(command.compareTo("HANGUP") == 0) {
                InCall selectedCall = (InCall) inCallsList.get(arg);
                selectedCall.hangup();
                outMsg = "Hung up call with ID = " + arg;
                System.out.println(outMsg);
            }
        }
        
        Message messagePkt = new Message("b2b_luis@10.112.128.7", Message.Type.CHAT);
        messagePkt.setFrom("b2b_luis@sip3.im.ptinovacao.pt");
        //messagePkt.setSubject("Call Control Dialog");
        messagePkt.setThread(event.getThread());
        messagePkt.setBody(outMsg);
        
        xmppSbbInterface.sendMessage(messagePkt); 
    }
    */
	
	public abstract void fireCallEstablishedEvent(com.ptin.xmas.control.events.CallEstablishedEvent event, 
	        ActivityContextInterface aci, javax.slee.Address address);
	
	public abstract void fireByeEvent(com.ptin.xmas.control.events.ByeEvent event, 
	        ActivityContextInterface aci, javax.slee.Address address);
    
	// TODO: Perform further operations if required in these methods.
	public void setSbbContext(SbbContext context) { 
	    this.sbbContext = context; 

	    try {
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");

            fp = (SipFactoryProvider) myEnv.lookup("slee/resources/jainsip/1.1/provider");
            //xmppSbbInterface = (XmppRASbbInterfaceImpl) myEnv.lookup("slee/resources/xmpp/1.0/xmppinterface");
            System.out.println("setSbbContext: got the fp " + fp);
            provider = fp.getSipProvider();
            addressFactory = fp.getAddressFactory();
            headerFactory = fp.getHeaderFactory();
            messageFactory = fp.getMessageFactory();
            
            nullACIFactory = (NullActivityContextInterfaceFactory) myEnv.lookup("slee/nullactivity/activitycontextinterfacefactory");
            nullActivityFactory = (NullActivityFactory) myEnv.lookup("slee/nullactivity/factory");
            acNamingFacility = (ActivityContextNamingFacility) myEnv.lookup("slee/facilities/activitycontextnaming");

        } catch (Exception e) {
            System.out.println("Could not set SBB context in BaseB2BSbb:" + e.getMessage());
        } 	
   	}
	
    public void unsetSbbContext() { this.sbbContext = null; }
    
    // TODO: Implement the lifecycle methods if required
    public void sbbCreate() throws javax.slee.CreateException {
        Hashtable legsList;
        Hashtable inCallLegsList; 
        Hashtable outCommLegsList;
        Hashtable inCallsList;
        Hashtable outCommsList;
        CallControllerListenerImpl callListener;
        CallManager manager = null;
        
        callListener = new CallControllerListenerImpl();
                
        try {
            manager = ManagerImpl.getReference(callListener);
            CommProviderImpl commProviderImpl = CommProviderImpl.getReference();
            legsList = commProviderImpl.getLegsList();
            inCallsList = commProviderImpl.getInCallsList();
            outCommsList = commProviderImpl.getOutCommsList();
            inCallLegsList = commProviderImpl.getInCallLegsList();
            outCommLegsList = commProviderImpl.getOutCommLegsList();
            b2bCallController = new B2BCallControllerImpl();
            
    		setLegsList(legsList);
    		setInCallsList(inCallsList);
    		setOutCommsList(outCommsList);
    		setInCallLegsList(inCallLegsList);
    		setOutCommLegsList(outCommLegsList);
    		
            setManager(manager);
        } catch (Exception e) {
            e.printStackTrace();
        } 
		setCallListener(callListener);
    }
    
    public void sbbPostCreate() throws javax.slee.CreateException {}
    public void sbbActivate() {}
    public void sbbPassivate() {}
    public void sbbRemove() {}
    
    public void sbbLoad() {
        try {
            NullActivity b2bCallControllerActivity = 
                this.nullActivityFactory.createNullActivity();
        	ActivityContextInterface b2bNullACI = 
        	    this.nullACIFactory.getActivityContextInterface(b2bCallControllerActivity);
        	b2bNullACI.attach(sbbContext.getSbbLocalObject());
    		B2BSbbActivityContextInterface b2bCallControllerACI = 
    			this.asSbbActivityContextInterface(b2bNullACI);
    		b2bCallControllerACI.setInCallsList(getInCallsList());
    		b2bCallControllerACI.setB2BCallController(b2bCallController);
    		acNamingFacility.bind(b2bCallControllerACI, "B2BControlActivity");            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sbbStore() {
    }
    
    public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {}
    public void sbbRolledBack(RolledBackContext context) {}
	

	
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

	private String getUriUserName(URI uri) {
        return uri.toString().split("sip:")[1].split("@")[0];

    }
	
	// 'legsList' CMP field setter
	public abstract void setLegsList(Hashtable value);
	// 'legsList' CMP field getter
	public abstract Hashtable getLegsList();

	// 'inCallsList' CMP field setter
	public abstract void setInCallsList(Hashtable value);
	// 'inCallsList' CMP field getter
	public abstract Hashtable getInCallsList();

	// 'outCommsList' CMP field setter
	public abstract void setOutCommsList(Hashtable value);
	// 'outCommsList' CMP field getter
	public abstract Hashtable getOutCommsList();

	// 'inCallLegsList' CMP field setter
	public abstract void setInCallLegsList(Hashtable value);
	// 'inCallLegsList' CMP field getter
	public abstract Hashtable getInCallLegsList();

	// 'outCommLegsList' CMP field setter
	public abstract void setOutCommLegsList(Hashtable value);
	// 'outCommLegsList' CMP field getter
	public abstract Hashtable getOutCommLegsList();

	// 'callListener' CMP field setter
	public abstract void setCallListener(CallControllerListenerImpl value);
	// 'callListener' CMP field getter
	public abstract CallControllerListenerImpl getCallListener();

	// 'manager' CMP field setter
	public abstract void setManager(CallManager value);
	// 'manager' CMP field getter
	public abstract CallManager getManager();
	
	public abstract B2BSbbActivityContextInterface asSbbActivityContextInterface(ActivityContextInterface aci);

}


