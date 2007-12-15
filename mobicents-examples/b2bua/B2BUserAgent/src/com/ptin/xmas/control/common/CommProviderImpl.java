//Source file: G:\\tools\\jainproxy\\JainComm.java

package com.ptin.xmas.control.common;

import com.ptin.xmas.control.common.interfaces.CallProvider;
import com.ptin.xmas.control.common.interfaces.CommFactory;
import com.ptin.xmas.control.common.interfaces.CommProvider;
import com.ptin.xmas.control.common.interfaces.CallListener;
import com.ptin.xmas.control.common.interfaces.Leg;
import com.ptin.xmas.control.common.interfaces.CallManager;
import com.ptin.xmas.control.common.Logx;
import com.ptin.xmas.control.common.SystemInfo;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sip.ClientTransaction;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.Transaction;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.mobicents.slee.resource.sip.SipFactoryProvider;


/**
 *  Singleton class that may be used to send messages and is used to receive messages.
 * 
 * @author  xest188
 */
public class CommProviderImpl implements CallProvider, CommProvider,java.io.Serializable,SipListener
{
  //singleton class variable
   private static CommProviderImpl myself=null;
    
   protected String serviceFile;
   protected boolean enableRecordRoute;
   
   private Hashtable appsList=null; 
   private Hashtable callsList=null;
   private Hashtable CDR_list=null;
   private Hashtable legsList=null;
   
    private Hashtable inCallLegsList = null; 
    private Hashtable outCommLegsList = null;
    private Hashtable inCallsList = null;
    private Hashtable outCommsList = null;
    
    
   //em testes
    protected SipFactory sipFactory = null; 
    private AddressFactory addressFactory= null; 
    private HeaderFactory headerFactory = null; 
    private Iterator listeningPoints = null; 
    private MessageFactory messageFactory;
    private SipStack stack;
    private ListeningPoint listeningPoint;
    private SipProvider sipProvider;
    //properties
    private String sipStackPath = null;
    private String publicAddress = null;
    private String displayName = null;
    private String transport = null;
    private String registrarAddress = null;
    private int localPort = -1;
    private int registrarPort = -1;
    private int registrationsExpiration = -1;
    private String registrarTransport = null;
    //mandatory stack properties
    private String stackAddress = null;
    private String stackName = "JsPhone-1.1";
    //Prebuilt Message headers
    private FromHeader fromHeader = null;
    private ContactHeader contactHeader = null;
    private ArrayList viaHeaders = null;
    private MaxForwardsHeader maxForwardsHeader = null;

    private ArrayList listeners = new ArrayList();
    private CallManager managerListener=null;
    //XxxProcessing managers
//    RegisterProcessing registerProcessing = null;
//    CallProcessing callProcessing = null;
    private boolean isStarted = false;
    protected CommFactory commFactory;
    private Logx log=null;
   /**
    * Creates new JainComm
    * @roseuid 3D6A57490234
    */
   
   
   private CommProviderImpl()
   {
      //log=Logx.getReference();
       //System.out.println("CommProviderImpl() CONTRUCTOR\n*******************************************************************************************");
       setProperties();
       appsList=new Hashtable();
       callsList=new Hashtable();
       CDR_list=new Hashtable();
       legsList=new Hashtable();
       inCallsList = new Hashtable();
       outCommsList = new Hashtable();
       inCallLegsList = new Hashtable();
       outCommLegsList = new Hashtable();
   
        try{
            //this.start(); 
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            SipFactoryProvider fp;
            fp = (SipFactoryProvider) myEnv.lookup("slee/resources/jainsip/1.1/provider");
            sipProvider = fp.getSipProvider();
            commFactory=CommFactoryImpl.getReference(this);
        }
        catch(Exception e) {
            //System.out.println("ERRO CRITICO!!!");
            e.printStackTrace();
        }    
  

        
   }
       /**
     *Function that returns the SipProvider from CommProviderImpl.
     */
   public SipProvider getSipProvider(){
       return sipProvider;
   }
   
   
   
   
       /**
     *The method to start the stack!
     */
       public void start()
    {
        System.out.println("CommProviderImpl.start()");
        initProperties();
        this.sipFactory = SipFactory.getInstance();
        sipFactory.setPathName(sipStackPath);
        
        System.out.println("SipStackPath= "+sipStackPath);
        
        try {
            addressFactory = sipFactory.createAddressFactory();
            headerFactory = sipFactory.createHeaderFactory();
            messageFactory = sipFactory.createMessageFactory();
        }
        catch (PeerUnavailableException ex) {
          //System.out.println("Could not could not create factories!\n"+ex);
        }
        try {
            stack = sipFactory.createSipStack(System.getProperties());
            
        }
        catch (PeerUnavailableException ex) {
           // System.out.println("Very bad error!!!\nCheck: -property files on jboss\\bin may be corrupted\n- sip port might be in use by another service!(check with ps aux if another instance of jboss is running!)\nCould not could not create SipStack!\n"+ex);
        }
        try {
            boolean successfullyBound = false;
            while (!successfullyBound) {
                try{Thread.sleep(1000);}catch(InterruptedException ie){System.out.println(""+ie);}
                try {
                    listeningPoint = stack.createListeningPoint(localPort,
                        transport);
                }
                catch (InvalidArgumentException ex) {
                    //choose another port between 1024 and 65000
                    //localPort = (int) ( (65000 - 1024) * Math.random()) + 1024;
                    
                    System.out.println("cannot bind the port "+localPort+" using "+transport+"!!!");
                    ex.printStackTrace();
                    continue;
                }
                successfullyBound = true;
            }
            
            //set by getFromHeaderMethod
//            if(localPort != 5060)
//            	publicAddress += ':' + Integer.toString(localPort);
        }
        catch (TransportNotSupportedException ex) {
//System.out.println("Transport " + transport + " is not suppported by the stack!\n"+"Try specifying another transport in JsPhone property files.\n"+ex);
        }
        try {
            sipProvider = stack.createSipProvider(listeningPoint);
        }
        catch (ObjectInUseException ex) {
           // System.out.println("Could not could not create factories!\n"+ex);
        }
        try {
            System.out.println("before adding SipListener");
            sipProvider.addSipListener(this);
            System.out.println("after adding SipListener");
        }
        catch (TooManyListenersException exc) {
            //System.out.println("Could not register SipManager "+"as a listener with sipProvider!");
        }
        //Make sure prebuilt headers are nulled so that they get reinited
        //if this is a restart
        contactHeader = null;
        fromHeader = null;
        viaHeaders = null;
        maxForwardsHeader = null;
        isStarted = true;
    }
    private void initProperties()
    {
        // ------------------ stack properties --------------
        stackAddress = System.getProperty("javax.sip.IP_ADDRESS");
        stackAddress.trim();
        //ensure IPv6 address compliance
        if (stackAddress.indexOf(':') != stackAddress.lastIndexOf(':')
            && stackAddress.charAt(0) != '['
            )
            stackAddress = '[' + stackAddress + ']';
        stackName = System.getProperty("javax.sip.STACK_NAME");
        if (stackName == null) {
            stackName = "JsPhone-1.1@" + Integer.toString(hashCode());
            //Add the stack name to the properties that will pass the stack
            System.setProperty("javax.sip.STACK_NAME", stackName);
        }
        //------------ application properties --------------
        publicAddress = System.getProperty(
            "examples.jsphone.sip.PUBLIC_ADDRESS");
        if (publicAddress == null)
            publicAddress = System.getProperty("user.name") + "@" +
                stackAddress;
        if (!publicAddress.trim().toLowerCase().startsWith("sip:"))
            publicAddress = "sip:" + publicAddress.trim();
        registrarAddress = System.getProperty(
            "examples.jsphone.sip.REGISTRAR_ADDRESS");
        //registrart must remain null and exception should be thrown in register() method
        //if(registrarAddress == null || registrarAddress.trim().length() == 0)
        try {
            registrarPort = Integer.parseInt(System.getProperty(
                "examples.jsphone.sip.REGISTRAR_PORT"));
        }
        catch (NumberFormatException ex) {
            registrarPort = 5060;
        }
        registrarTransport = System.getProperty(
            "examples.jsphone.sip.REGISTRAR_TRANSPORT");
        if (registrarTransport == null)
            registrarTransport ="udp";

	// Added mranga
	String serverLog =  System.getProperty
		("gov.nist.javax.sip.SERVER_LOG");
	if (serverLog != null) {
		System.setProperty
		("gov.nist.javax.sip.TRACE_LEVEL","16");
	}
        sipStackPath = System.getProperty(
            "examples.jsphone.sip.STACK_PATH");
        if (sipStackPath == null)
            sipStackPath = "gov.nist";
        String routerPath = System.getProperty("javax.sip.ROUTER_PATH");
       // if (routerPath == null)
       //     System.setProperty("javax.sip.ROUTER_PATH",
       //                       "examples.jsphone.sip.JsPhoneRouter");
        transport = System.getProperty(
            "examples.jsphone.sip.TRANSPORT");
        if (transport == null)
            transport = "udp";
        String localPortStr = System.getProperty(
            "examples.jsphone.sip.PREFERRED_LOCAL_PORT");
        try {
            localPort = Integer.parseInt(localPortStr);
        }
        catch (NumberFormatException exc) {
            localPort = 5060;
        }
        displayName = System.getProperty("examples.jsphone.sip.DISPLAY_NAME");
    }

   
      public static void setProperties() {
        Properties p = null;
        // set up new properties object
	// from file "myProperties.txt"
       try {
        FileInputStream propFile = new FileInputStream(
                                           "nistsip12.properties");
        p = new Properties(System.getProperties());
        p.load(propFile);
        propFile.close();
       } catch (Exception e){}; 

        // set the system properties
        System.setProperties(p);
	// display new properties
        System.getProperties().list(System.out);
   }

    /**
    *function that returns the CommFactory
    */ 
 public CommFactory getCommFactory(){
     return commFactory;
 }      

 private String getUriUserName(URI uri)
 {
   return uri.toString().split("sip:")[1].split("@")[0];
     
 }

 private boolean processRequestOnListener(RequestEvent requestReceivedEvent)
 {
  Request request = (Request)requestReceivedEvent.getRequest();
  
  ServerTransaction serverTransaction = requestReceivedEvent.getServerTransaction();
  
  if (serverTransaction==null) 
  { 
      SystemInfo.log.debug("processRequestOnListener serverTransaction == null");
      try{
      serverTransaction=sipProvider.getNewServerTransaction(request);
      }catch(Exception e){e.printStackTrace();}
  }

  Leg inCallLeg = (Leg)inCallLegsList.get(((CallIdHeader)request.getHeader(CallIdHeader.NAME)).getCallId());
  Leg outCommLeg = (Leg)outCommLegsList.get(((CallIdHeader)request.getHeader(CallIdHeader.NAME)).getCallId());
    
  

  
  
  
try{
    if ( (inCallLeg != null ) &&  ((CSeqHeader)request.getHeader(CSeqHeader.NAME)).getSequenceNumber()!=1)
        {
        SystemInfo.log.debug("processRequestOnListener inCallLeg != null");
        inCallLeg.processRequest( request, serverTransaction );  
        return true;
        }
    else 
    if ( (outCommLeg != null ) &&  ((CSeqHeader)request.getHeader(CSeqHeader.NAME)).getSequenceNumber()!=1)
        {
        SystemInfo.log.debug("processRequestOnListener inCallLeg != null");
        outCommLeg.processRequest( request, serverTransaction );  
        return true;
        }
    else 
       {
        if ( !request.getMethod().equals(request.INVITE) )
          return false;
    
       String reqUriUserName = getUriUserName(request.getRequestURI() );
      // ManagerListener app = (ManagerListener) appsList.get(reqUriUserName); 
      // if ( !(app == null ) )
      // {
       String auxAddress =  (((ToHeader)request.getHeader(ToHeader.NAME)).getAddress().toString()).split("@")[0]; 
       auxAddress = auxAddress.split("sip")[1];
       System.out.println("processRequestOnListener "+ auxAddress);
     
       managerListener.processInvite(request,serverTransaction);//app.processInvite(request, serverTransaction); 
           return true;
         //  }
       }
}
catch(Exception e){e.printStackTrace();}
       return false;
 }

  /*
  ( request.getCallIdHeader().getCallId() )
  */ 

 
 public void processRequest(RequestEvent requestReceivedEvent) 
   {
       System.out.println("\n\nRequest received: (CommProviderImpl)\n"+requestReceivedEvent.getRequest()+" \n");
    try {
      if (requestReceivedEvent.getRequest().getMethod().equalsIgnoreCase(Request.SUBSCRIBE)){
          final Request request=requestReceivedEvent.getRequest();
          //managerListener.processSubscribe(request);
      }
      else{
      if   ( !processRequestOnListener(requestReceivedEvent) ){
          System.out.println("Fim do commProvider 1");
          
          if (((Request)requestReceivedEvent.getRequest()).getMethod().equalsIgnoreCase(Request.INVITE)){
  //  System.out.println("é aki:"+requestReceivedEvent.getRequest().getRequestURI().toString().split("INVITE sip:")[1].split(":")[0]/*+"@"+this.getHost()*/);
              System.out.println("Fim do commProvider 1.3");
              ServerTransaction serverTransaction=requestReceivedEvent.getServerTransaction();
        if (serverTransaction==null){
            try{
                System.out.println("Fim do commProvider 1.5");
                serverTransaction=sipProvider.getNewServerTransaction(requestReceivedEvent.getRequest());
                System.out.println("Fim do commProvider 1.7");
            }catch(Exception e){System.out.println(""+e);}}
        
        
        System.out.println(" commProvider "+ (((ToHeader)requestReceivedEvent.getRequest().getHeader(ToHeader.NAME)).getAddress().toString()).split("@")[0]);
        managerListener.processInvite((Request)requestReceivedEvent.getRequest(),serverTransaction);
        
          }else if(((Request)requestReceivedEvent.getRequest()).getMethod().equals(Request.BYE)){
              System.out.println("Bye received for a call that does not exist anymore...");              
          }else if (requestReceivedEvent.getRequest().getMethod().equalsIgnoreCase(Request.MESSAGE)){
              final Request request=requestReceivedEvent.getRequest();
         
              System.out.println("Fim do commProvider 2");
              //managerListener.processIM(request);
          }

      }
       System.out.println("Fim do commProvider 2.5");
      }
      
       System.out.println("Fim do commProvider 3");
    }catch(Exception e){System.out.println(" Foi aki "+e);}

   }
    
   /**
    * @param responseReceivedEvent
    * @roseuid 3D6A574903D9
    */
 
  private boolean processResponseOnListener(Response response, ClientTransaction clientTransaction)
  
 {
  
//callsList
 
//CallListener call = (CallListener)callsList.get(response.getCallIdHeader().getCallId());
Leg inCallLeg = (Leg)inCallLegsList.get(((CallIdHeader)response.getHeader(CallIdHeader.NAME)).getCallId());
Leg outCommLeg = (Leg)outCommLegsList.get(((CallIdHeader)response.getHeader(CallIdHeader.NAME)).getCallId());
boolean isLeg = false;  

if ( !(inCallLeg == null ) )
    {
    System.out.println("processResponseOnListener inCallLeg != null");    
    inCallLeg.processResponse( response, clientTransaction );
    isLeg = true;
    }
if ( !(outCommLeg == null ) )
    {
    System.out.println("processResponseOnListener outCommLeg != null");    
    outCommLeg.processResponse( response, clientTransaction );
    isLeg = true;
    }
    return isLeg;
 }

  /*
  ( request.getCallIdHeader().getCallId() )
  */ 
   /**
    *method used to add a Call to the Hashtable on CommProviderImpl
    */
 
  public void addCall(String callid,CallListener callListener){
     callsList.put(callid,callListener);
 }
  
  public void addInCall (String callid, CallListener callListener){
      
      inCallsList.put(callid,callListener);
  }
  
  public void addOutComm (String callid, CallListener callListener){
      outCommsList.put(callid,callListener);
  }
  
   /**
    *Adds a Leg (a sip listener) to the LegsList (an Hashtable), so that every Response/Request
    *with that same callid (used as a label) will be delivered to that Leg
    *
    *Atention: if a call has two legs, they MUST have different callids
    */
 public void addLeg(String callId,Leg leg){ 
     legsList.put(callId,leg); 
 }
 
 public void addOutCommLeg (String callId, Leg leg) {
    outCommLegsList.put(callId,leg);
 }
 
 public void addInCallLeg (String callId, Leg leg) {
     inCallLegsList.put(callId,leg);
 }

 private Leg getLeg(String callId){
    Leg leg = null;
    leg = (Leg) inCallLegsList.get(callId);
    if (leg == null)
         leg = (Leg) outCommLegsList.get(callId);
   
    return leg;
 }
   /**
    *method used to remove a Leg from the LegsList when the call no longer exists
    */
 public void removeLeg(String callId){
     legsList.remove(callId);
 }
 
 public void removeInCallLeg(String callId){
     inCallLegsList.remove(callId);
 }
 public void removeOutCommLeg(String callId){
     outCommLegsList.remove(callId);
 }
    /**
    *method used to remove a Call from the Hashtable on CommProviderImpl
    */
 public void removeCall(String callid){
     if (inCallsList.containsKey(callid)){
        inCallsList.remove(callid);     
     }else if (outCommsList.containsKey(callid)){
        outCommsList.remove(callid);     
     }else System.out.println("Call has already been removed earlier");
 }

   //inicio do metodo novo
   public void processResponse(ResponseEvent responseEventReceived) {
         System.out.println("\n\nResponse received \n"+responseEventReceived.getResponse()+" \n");
      
          ServerTransaction x=null;
          //System.out.println("[CommProviderImpl]                  Response Received...");
          Response response=responseEventReceived.getResponse();
          ClientTransaction clientTransaction=responseEventReceived.getClientTransaction();
   try{
      if (((CSeqHeader)response.getHeader(CSeqHeader.NAME)).getMethod().equalsIgnoreCase((Request.REGISTER))){
          //managerListener.processResponse2Register(response,clientTransaction);
      }else if ( !processResponseOnListener(response, clientTransaction) )
           {} //System.out.println("not registered on CommProvider application");
        if (response.getStatusCode()==Response.OK && response.getHeader(CSeqHeader.NAME).toString().equalsIgnoreCase("Bye")){
          //  System.out.println("Response Ok to Bye received, but the call no longer exists...");
        }

      }//catch(SipParseException spe){DebugProxy.println(""+spe);}
      catch(Exception e){System.out.println(""+e);}
 
    }
 
     
   /**
    *method used to send a request message previously constructed by CommFactory
    */
   public void sendRequest(Request request) 
   {
   System.out.println("[CommProviderImpl] sending request:\n" + request);
        try{
        sipProvider.sendRequest(request); 
       
             
         } catch(SipException e) { 
             // Another exception occurred 
	     e.printStackTrace();
             System.err.println(e.getMessage()); 
       //      System.exit(-1); 
         } 
   }
   
 
   
    public void sendRegister(Request register) {
        long clientTransactionId = 0;
        
   
        
        try {
            if (sipProvider==null) {System.out.println("BUGBUGBUGBUGBUG");}
            sipProvider.sendRequest(register);
            System.out.println("REGISTER sent :\n"+register);

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
        
                /*
                 * Baseado no createInvite!
                 */
    protected String getHost(){
        
        return System.getProperty("javax.sip.IP_ADDRESS");
    }
    protected int getPort(){
        return sipProvider.getListeningPoint().getPort();
    }

    protected String getTransport(){
        return sipProvider.getListeningPoint().getTransport();
    }
    public boolean hasManagerListener(String app){
        return appsList.containsKey(app);        
    }
      /**
     *Function that returns the ManagerListener based on a given name "app"
     */
    public CallManager getManagerListener(String app){
        return (CallManager)appsList.get(app);
    }
    /**
     *The method used to set the ManagerListener(interface implemented by Manager)
     *
     */
    public void setManagerListener(CallManager managerListener_){
        this.managerListener=managerListener_;
    }
    /**
     *Method that removes the ManagerListener(interface implemented by Manager) represented 
     *by its name "app"
     */
    public void removeManagerListener(String app){
        if (appsList.containsKey(app))
        appsList.remove(app);
    }
    public void addCallListener(CallListener callListener, String callId){
        callsList.put(callId, callListener);
    }
    public void removeCallListener(String callId){
        if (inCallsList.containsKey(callId)){
        inCallsList.remove(callId);
        }else if (outCommsList.containsKey(callId)){
        outCommsList.remove(callId);
        }else System.out.println("Call has already been removed earlier!");
    }


    public static CommProviderImpl getReference(){
        if (myself==null){myself=new CommProviderImpl();System.out.println("CommProviderImpl --> myself=null  (was...)");}
        return myself;
    }

   /**
    *method used to send a response message previously constructed by CommFactory
    */

    public void sendResponse(Response response){
        System.out.println("Sending Response:\n"+response);
        try{
            sipProvider.sendResponse(response);
        }catch(SipException se){
            se.printStackTrace();
            System.err.println(se.getMessage());
        }
    }
 
      /**
     *Function that returns a new CallIdHeader generated by SipProvider.
     */
    public CallIdHeader getNewCallIdHeader(){
        CallIdHeader callId=null;
        try{
            callId=sipProvider.getNewCallId();
        //callId= sipProvider.getNewCallIdHeader().getCallId();
        }catch(Exception e) {System.out.println("failed to create ID "+e);}
        return callId;
    }
    
    public void removeCommListener(String app) {
    }
 
    public void processTimeout(TimeoutEvent transactionTimeOutEvent)
    {
        System.out.println("Time Out Received!!!");
        Transaction transaction=null;
        //System.out.println(""+transactionTimeOutEvent.getClientTransaction().getDialog().getState().toString());
        if (transactionTimeOutEvent.isServerTransaction())
        transaction = transactionTimeOutEvent.getServerTransaction();
        if (transaction != null){
            Request request =transaction.getRequest();
            CallIdHeader callIdHeader=(CallIdHeader)request.getHeader(CallIdHeader.NAME);
            Leg leg = (Leg)getLeg(callIdHeader.getCallId());
            if (leg != null) leg.processTimeout( request, transaction ); 
        }
    }
    
    /**
     *The method to stop the stack!
     */
        public void stop() throws Exception
    {
        throw new UnsupportedOperationException(
            "Method stop() not yet implemented.");
        /* The stack behaves strangely after a stop
           Leave it out for the time being
           stack.deleteSipProvider(provider);
           // implementation dependant!!!
           ((SipStackImpl)stack).stop();
           stack = null;
           provider = null;
           factory = null;
         */
    }
    
    public Hashtable getLegsList() {
        return legsList;
    }
    
    public Hashtable getInCallsList() {
        return inCallsList;
    }
    
    public Hashtable getOutCommsList() {
        return outCommsList;
    }
    
    public Hashtable getInCallLegsList() {
        return inCallLegsList;
    }
    
    public Hashtable getOutCommLegsList() {
        return outCommLegsList;
    }
}


