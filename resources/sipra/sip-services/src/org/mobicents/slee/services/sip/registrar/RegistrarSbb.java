package org.mobicents.slee.services.sip.registrar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbID;
import javax.slee.SbbLocalObject;
import javax.slee.facilities.AlarmFacility;
import javax.slee.facilities.Level;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TraceFacility;
import javax.slee.nullactivity.NullActivityContextInterfaceFactory;
import javax.slee.nullactivity.NullActivityFactory;

import org.mobicents.slee.resource.sip.SipFactoryProvider;
import org.mobicents.slee.services.sip.proxy.RegistrationInformationAccess;


/**
 * 
 * this is the abstract class that will be deployed
 * 
 * Sbb abstract class provided by the Sbb developer
 * 
 * @author F.Moggia
 */
public abstract class RegistrarSbb  implements Sbb,RegistrationInformationAccess,ResourcesProvider {
    //private static Logger log;
    
    // For test only -- to test post conditions.
    //private SleeContainer myServiceContainer;
    
    private String errorMessage;
    
    private Address contactAddress;
    
    private SipURI requestURI;
    
    private static Logger logger = Logger.getLogger(RegistrarSbb.class.getName());
    
    /*static {
        log = Logger.getLogger(RegistrarSbb.class);
    }*/
    
    private void processRequest(ServerTransaction serverTransaction, Request request) {
    	trace(Level.FINEST, "processRequest: request = >>>>>>>>>\n" + request.toString() + "\n<<<<<<<<");
            try {
                // Create a worker to process this event           
                Registrar handler = new Registrar(this);
                // Go
                handler.processRequest(serverTransaction, request);
                    
            } catch (Exception e) {
                // Send error response so client can deal with it
                //e.printStackTrace();
                trace(Level.WARNING, "Exception during processRequest", e);
                try {
                    serverTransaction.sendResponse(getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR, request));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }            
        }  
    
    public void onRegisterEvent(RequestEvent event, ActivityContextInterface ac) {
    	trace(Level.INFO, "SipRegistrarSBB: " + this + ": got register event");
            try {
                ServerTransaction serverTransactionId = (ServerTransaction)ac.getActivity();
                Request request = event.getRequest();
                processRequest(serverTransactionId, request);
                    
            } catch (Exception e) {
                // Send error response so client can deal with it
                //e.printStackTrace();
                trace(Level.WARNING, "Exception during onRegisterEvent", e);
            }            
    }
    
    public void onTimerEvent( TimerEvent event, ActivityContextInterface ac ) {
        trace(Level.INFO, "Received timer event");

        try {
            RegistrarActivityContextInterface regACI = asSbbActivityContextInterface(ac);

            Registrar handler = new Registrar(this);

            handler.expireRegistration(regACI.getSipAddress(), regACI.getSipContactAddress(), regACI.getCallId(), regACI.getCSeq());

            // detach if no more events to come
            if (event.getRemainingRepetitions() == 0) regACI.detach(getSbbLocalObject());

        } catch (Exception e) {
            trace(Level.WARNING, "Exception during onTimerEvent", e);
        }
    }
   
    /*
     * this is implemented by bytecode rewriting. 
     * See ConcreteRegisrarSbb.java
     */
    public abstract RegistrarActivityContextInterface asSbbActivityContextInterface(
            ActivityContextInterface aci);

    public void setSbbContext(SbbContext context) {
        this.context = context;
        try {
            sbbEnv = (Context) new InitialContext().lookup("java:comp/env");
            id = context.getSbb();
            traceFacility = (TraceFacility) sbbEnv.lookup("slee/facilities/trace");
            timerFacility = (TimerFacility) sbbEnv.lookup("slee/facilities/timer");
            alarmFacility = (AlarmFacility) sbbEnv.lookup("slee/facilities/alarm");
            //profileFacility = (ProfileFacility) sbbEnv.lookup("slee/facilities/profile");
            nullACIFactory = (NullActivityContextInterfaceFactory)sbbEnv.lookup("slee/nullactivity/activitycontextinterfacefactory");
            nullActivityFactory = (NullActivityFactory)sbbEnv.lookup("slee/nullactivity/factory");
            fp = (SipFactoryProvider) sbbEnv.lookup("slee/resources/jainsip/1.2/provider");
            provider = fp.getSipProvider();
            addressFactory = fp.getAddressFactory();
            headerFactory = fp.getHeaderFactory();
            messageFactory = fp.getMessageFactory();
        } catch (NamingException ne) {
            logger.log(java.util.logging.Level.WARNING, "Could not set SBB context: ", ne);
        }
        
    }

    public void unsetSbbContext() {
        context = null;
    }

    public void sbbCreate() throws CreateException {
        //log.info("called sbb create");
    }

    public void sbbPostCreate() throws CreateException {
        //log.info("called sbb post create");
    }

    public void sbbRemove() {
//      this is called when the object is returned to the pool
        //log.info("called sbbRemove " + this.myServiceContainer.getActivityContextCount());
        //this.testResult.startPostProcessingTimer(this);
    }

    public void sbbPassivate() {
        // this is called when the object is returned to the pool
        //log.info("called sbbPassivate");
        
    }

    public void sbbActivate() {
        //log.info("called sbb activiate");
    }

    public void sbbLoad() {
    }

    public void sbbStore() {
    }

    public void sbbExceptionThrown(Exception exception, Object event,
            ActivityContextInterface aci) {
    }

    public void sbbRolledBack(RolledBackContext context) {
    }
    
    /*public void doPostProcessingConsistencyCheck() {
        if ( this.myServiceContainer.getActivityContextCount() != 0 ) {
            testResult.appendMessage("activity context count is " + this.myServiceContainer.getActivityContextCount());
        } else { 
            log.debug("activityContextCount = " + this.myServiceContainer.getActivityContextCount())	;
            
        }
        testResult.testFinished();
    }*/
    
    
    
    protected String getTraceMessageType() { return "RegistrarSbb"; }
    
    protected final void trace(Level level, String message) {
        try {
            traceFacility.createTrace(id, level, getTraceMessageType(), message, System.currentTimeMillis());
       } catch (Exception e) { }
    }
    
    protected final void trace(Level level, String message, Throwable t) {
        try {
            traceFacility.createTrace(id, level, getTraceMessageType(), message, t, System.currentTimeMillis());
        } catch (Exception e) { }
    }
    
//  Inner class for SLEE-specific SIP registration stuff
    //BUT SINCE WE WORK IN SLEE, we will give a green light to completly SLEE dependant class ( actually only this method is dep.)
    
    //class MyRegistrationHandler extends Registrar {

    //    public MyRegistrationHandler(RegistrarSbb sbb) {
    //        super(sbb);
     //   }


        // override set timer method so we can use SLEE TimerFacility
     //   void setRegistrationTimer(String sipAddress, String sipContactAddress, long timeout, String callId, long cseq) {
            // first find out if we already have a timer set for this registration, and if so, cancel it.
      //      logger.fine("setRegistrationTimer(" + sipAddress + ", " + sipContactAddress + ", " + timeout + ", " + callId + ", " + cseq + ")");
     //       try {
                // set a one-shot timer. when it fires we expire the registration
      //          long expireTime = System.currentTimeMillis() + (timeout * 1000);

                // Create new ACI for this timer
     //           NullActivity nullAC = getNullActivityFactory().createNullActivity();
     //           ActivityContextInterface nullACI = getNullACIFactory().getActivityContextInterface(nullAC);
     //           RegistrarActivityContextInterface regACI = asSbbActivityContextInterface(nullACI);
     //           regACI.setSipAddress(sipAddress);
     //           regACI.setSipContactAddress(sipContactAddress);
                // callId and cseq used to identify a particular registration
     //           regACI.setCallId(callId);
     //           regACI.setCSeq(cseq);

                // attach so we will receive the timer event...
     //           regACI.attach(getSbbLocalObject());
                
      //          TimerOptions timerOpts = new TimerOptions();
      //          timerOpts.setPersistent(true);

      //          TimerID newTimer = getTimerFacility().setTimer(regACI, null, expireTime, timerOpts);

      //          logger.fine("set new timer for registration: " + sipAddress + " -> " + sipContactAddress + ", expires in " + timeout + "s");

      //      } catch (Exception e) {
       //         e.printStackTrace();
       //     }

       // }

    //}
    
    
    
    
    public Map getBindings(String sipAddress) {
		
    	
    	LocationService ls=new LocationService();
    	Map bindCopy=null;
    	try {
    		bindCopy=ls.getBindings(sipAddress);
		} catch (LocationServiceException e) {
			
			e.printStackTrace();
		}
		
		if(bindCopy==null)
			bindCopy=new HashMap();
		else
			bindCopy=new HashMap(bindCopy);
		return bindCopy;
	}

    
    public Set getRegisteredUsers()
    {
    	
    	return new LocationService().getRegistered();
    }

	private SipFactoryProvider factoryProvider;
    
    public SipProvider getSipProvider() { return provider; }
    public AddressFactory getAddressFactory() { return addressFactory; }
    public HeaderFactory getHeaderFactory() { return headerFactory; }
    public MessageFactory getMessageFactory() { return messageFactory; }
    public NullActivityFactory getNullActivityFactory() { return nullActivityFactory; }
    public  final TimerFacility getTimerFacility() { return timerFacility; }
    public NullActivityContextInterfaceFactory getNullACIFactory() { return nullACIFactory; }
    
    public final SbbLocalObject getSbbLocalObject() { return context.getSbbLocalObject(); }
    
    
    private SipFactoryProvider fp;
    private SipProvider provider;
    private AddressFactory addressFactory;
    private HeaderFactory headerFactory;
    private MessageFactory messageFactory;
    
    private SbbContext context;            
    private TraceFacility traceFacility;
    private TimerFacility timerFacility;
    private AlarmFacility alarmFacility;
    // Not implelemented yet
    //private ProfileFacility profileFacility;
    private SbbID id;
    private NullActivityFactory nullActivityFactory;
    private NullActivityContextInterfaceFactory nullACIFactory;
    private Context sbbEnv;

    private Context ctx;

    
    
    
    
    
    
    
    
}
