/** Java class "ManagerImpl.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common;

import com.ptin.xmas.control.common.interfaces.CallControllerListener;
import com.ptin.xmas.control.common.interfaces.CallController;
import com.ptin.xmas.control.common.CommFactoryImpl;

import com.ptin.xmas.control.common.interfaces.CommFactory;
import com.ptin.xmas.control.common.interfaces.CommProvider;
//import inocrea.control.callControl.Comm;
import com.ptin.xmas.control.common.Comm3pcc;
import com.ptin.xmas.control.common.InCall;
import com.ptin.xmas.control.common.interfaces.InCallController;
import com.ptin.xmas.control.common.interfaces.Leg;
import com.ptin.xmas.control.common.interfaces.CallManager;
//import com.ptin.xmas.control.common.interfaces.ManagerListener;
import com.ptin.xmas.control.common.MediaData;
import com.ptin.xmas.control.common.CallControllerListenerImpl;
import com.ptin.xmas.control.common.Logx;
import com.ptin.xmas.control.common.SystemInfo;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;




/**
 * <p>
 * Class used as a call manager. Works as a singleton. When an invite is received
 * </p>
 * <p>
 * at CommProvider, it is sent here and processed at .processInvite() .
 * </p>
 * <p>
 * This class is used to start click 2 dial calls too.
 * </p>
 * <p>
 * Implements ManagerListener, interface used by CommProvider.
 * </p>
 * </p>
 */
public class ManagerImpl implements Serializable, CallManager { 

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 * 
 *
 */
    private static ManagerImpl myself = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private long registerTid = 0; 

    private static CallControllerListener _callControllerListener;
/**
 * <p>
 * Represents ...
 * </p>
 */
    static String appName = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static String reqUri1 = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static String from = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static String to = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    String proxyserv = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private Hashtable ongoingCalls = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected int status; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String to1; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String to2 = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int STARTING = 0; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int REGISTERING = 1; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int REGISTERED = 2; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int HALT = 3; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private Vector conversationsList = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private boolean ok = false; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String callEventUser = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private Hashtable services = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private SystemInfo info = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    Logx log = null; 

   ///////////////////////////////////////
   // associations

/**
 * <p>
 * 
 * </p>
 */
    protected CommProvider commProvider; 
/**
 * <p>
 * 
 * </p>
 */
    protected CommFactory commFactory; 
/**
 * <p>
 * 
 * </p>
 */
   // private InComListener listener; 

/**
 * <p>
 * Constructs, gets SystemInfo and registers to the proxy.
 * </p>
 * </p>
 */
    private SipProvider provider;
    
    public  ManagerImpl(String info_) {        
           //SystemInfo.log.debug("[ManagerImpl] properties file: "+info_);
          //log=Logx.getReference();
           
           info=new SystemInfo(info_);
           appName= info.getUA();
           proxyserv= info.getProxy();
           //SystemInfo.log.debug("[ManagerImpl] proxyserv: "+proxyserv);
           //log.log("UserAgent--->"+info.getUA());
        //   exceptionManager = null;
           commProvider =null;
           status = this.STARTING;
           ongoingCalls = new Hashtable();
           conversationsList=new Vector();
          
           System.out.println("OK 1 : a buscar CommProvider");
           commProvider=CommProviderImpl.getReference();
           System.out.println("OK 2 : a buscar CommFactory");
           commFactory=CommFactoryImpl.getReference();
           System.out.println("OK 3 : a buscar UserManager");
           
          // userManager=UserManagerImpl.getReference();
           //log.log("OK 4 : a registar");
           services = new Hashtable();
            //inexCCManager=InexCCManagerImpl.getReference();
            
            // Juntar o processo de registo do servidor juntamente c/ os outros servicos
            
           /*this.processServerRegistering();
           this.registerCycle();*/
           commProvider.setManagerListener(this);
    } // end ManagerImpl        

/**
 * <p>
 * Function that returns an InCallController object with the respective callId.
 * </p>
 * </p>
 */
    public InCallController getCall(String callid) {        
        return (InCallController)ongoingCalls.get(callid);
    } // end getCall        

/**
 * <p>
 * Method that is called by CommProvider to receive an incomming Invite and to process it.
 * </p>
 * <p>
 * It generates a new InCall object that runs on a new Thread.
 * </p>
 * </p>
 */
      public void processInvite(final Request inviteRequest,
            final ServerTransaction serverTransaction) throws Exception {
        //log.log("ServerTransaction @ "+serverTransaction);
        //SystemInfo.log.debug("[ManagerImpl.processInvite] InviteRceveid: \n"+
        // inviteRequest+"\n");

        final String callId = ((CallIdHeader) inviteRequest
                .getHeader(CallIdHeader.NAME)).getCallId();
        if (ongoingCalls.containsKey(callId)) {
            //SystemInfo.log.debug("[ManagerImpl.processInvite] CallId: " +
            // callId +" is an ongoing call!");
            throw new Exception();

        } else {
            ongoingCalls.put(callId, "");
            final CallManager manager = (CallManager) this;
            
            /*
            Thread processingInvitation = new Thread() {
                public void run() {
            */
                    SystemInfo.log.debug("[ManagerImpl.processInvite]  init\n");
                    //           InComTriggerCriteria trigger=null;
                    //final String callId =
                    // ((CallIdHeader)inviteRequest.getHeader(CallIdHeader.NAME)).getCallId();
                    try {
                        /*
                         * if ( ongoingCalls.containsKey(callId) ) {
                         * System.out.println("[InCom] processInvitationEvt
                         * Error: invitation event CallId: " + callId +" is an
                         * ongoing call!"); throw new Exception(); } else {
                         */
                        //  commLISTENER!!!

                        String auxAddress = (((ToHeader) inviteRequest
                                .getHeader(ToHeader.NAME)).getAddress()
                                .toString()).split("@")[0];
                        auxAddress = auxAddress.split(":")[1];
                        String reqUri = inviteRequest.getRequestURI()
                                .toString().split("@")[0].split(":")[1];
                        //SystemInfo.log.debug("[ManagerImpl.processInvite]
                        // Request URI: "+reqUri+" to: \n"+auxAddress +"\n");
                        /*
                         * InComListener auxListener = null;
                         * 
                         * if (appName.compareTo(reqUri)!=0) {
                         * //SystemInfo.log.debug("[ManagerImpl.processInvite]
                         * ReqUri != app "+appName); Service service = (Service)
                         * services.get(reqUri+"@"+proxyserv); if (service !=
                         * null){
                         * //SystemInfo.log.debug("[ManagerImpl.processInvite]
                         * service found: "+service.getAppName()); auxListener =
                         * service.getListener(); } //else
                         * //SystemInfo.log.debug("[ManagerImpl.processInvite]
                         * no service found for "+reqUri+"@"+proxyserv); } else
                         * auxListener = listener;
                         * 
                         * trigger = auxListener.getTrigger();
                         */

                        //SystemInfo.log.debug("[ManagerImpl.processInvite]
                        // trigger: "+trigger.getCriteria());
                        //User
                        // userTo=userManager.getUser(getURI(((ToHeader)inviteRequest.getHeader(ToHeader.NAME)).getAddress().toString()).split("@")[0]);
                        //System.out.println(" ---->
                        // "+getURI(((ToHeader)inviteRequest.getHeader(ToHeader.NAME)).getAddress().toString()).split("@")[0]);

                        /*
                         * if(trigger.isUser(auxAddress)) {
                         */
                        String from = getURI(((FromHeader) inviteRequest
                                .getHeader(FromHeader.NAME)).getAddress()
                                .toString());
                        String to = getURI(((ToHeader) inviteRequest
                                .getHeader(ToHeader.NAME)).getAddress()
                                .toString());
                        //SystemInfo.log.debug("[ManagerImpl.processInvite]
                        // call triggered for user: "+to);

                        InCall inCall = new InCall(inviteRequest, callId,
                                serverTransaction, manager);

                        //iCall.setInexCall(info_);
                        ongoingCalls.put(callId, inCall);

                        //SystemInfo.log.debug("[ManagerImpl.processInvite] A
                        // chamar o process Event do listener ");
                        inCall
                                .setCallControllerListener(_callControllerListener);

                        ((CallControllerListenerImpl) _callControllerListener).call = inCall;
                        inCall.processInviteFrom(inviteRequest,
                                serverTransaction);//alterado porque o
                                                   // auxListener estava a fazer
                                                   // o connecTo automaticamente
                                                   // para musiconhold, antes de
                                                   // fazer processInviteFrom na
                                                   // InCall (granda bronka)

                        /*
                         * auxListener.processEvent(inCall,
                         * CallController.incomingCall);
                         *  }
                         */
                        //else
                        //SystemInfo.log.debug("[ManagerImpl.processInvite]
                        // call not triggered for user: "+to);

                    } catch (Exception e) {
                        //if (trigger==null)
                        //SystemInfo.log.error("[ManagerImpl.processInvite]
                        // trigger is null!!");
                        e.printStackTrace();
                    }
            /*                    
                }
            };
            
            try {
                // log.log("A lançar nova THREAD");

                processingInvitation.start();
            } catch (Exception e) {
            }
            */
        }
        //SystemInfo.log.debug("[ManagerImpl.processInvite] end..");
    } // end processInvite        

      public void processInvite(final CallController incom) throws Exception {
        //log.log("ServerTransaction @ "+serverTransaction);
        //SystemInfo.log.debug("*** Process Invite manager \n");

        final String callId = incom.getCallId();
        /*
         * if ( ongoingCalls.containsKey(callId) ) {
         * //SystemInfo.log.debug("[InCom] processInvitationEvt Error:
         * invitation event CallId: " + callId +" is an ongoing call!"); throw
         * new Exception();
         *  } else
         */{

            String to = incom.getTo();

            //Service service = (Service) services.get(to);

            final CallManager manager = (CallManager) this;

            ongoingCalls.put(callId, incom);

            //SystemInfo.log.debug("A chamar o process Event do listener ");

            try {
                //service.getListener().processEvent(incom,
                // CallController.incomingCall);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //if (service == null)
            //SystemInfo.log.error("[ManagerImpl.processInvite] no service for:
            // "+to);
        }
    } // end processInvite
      
/**
 * <p>
 * Does ...
 * </p>
 * <p>
 * 
 * </p>
 */
    public void init() {        
        //SystemInfo.log.debug("init");
        try {
            commProvider.setManagerListener(this); // o CommProvider e o appName deve ser definidos no ficheiro de propriedades
            if (CommFactoryImpl.getReference()==null) {
                commFactory=CommFactoryImpl.getReference((CommProviderImpl)commProvider);
            }else {commFactory=CommFactoryImpl.getReference();}
        } catch (Exception e){
        	//SystemInfo.log.error("I caught an Exception! :"+e);
        try{Thread.sleep(7500);}catch(InterruptedException ie){}};
    } // end init        

/**
 * <p>
 * Method used to set properties some system properties.
 * </p>
 * </p>
 */
    private void setProperties() {        
                Properties p = null;
                // set up new properties object
        	// from file "myProperties.txt"
               try {
                FileInputStream propFile = new FileInputStream(
                                                   "comm.properties");
                p = new Properties(System.getProperties());
                p.load(propFile);
               } catch (Exception e){}; 
                // set the system properties
                System.setProperties(p);
        	// display new properties
                System.getProperties().list(System.out);
    } // end setProperties        

/**
 * <p>
 * Function that returns commProvider... no longer used because CommProvider can
 * </p>
 * <p>
 * be referenced from the static function CommProviderImpl.getReference()
 * </p>
 * </p>
 */
    public CommProvider getProvider() {        
        return commProvider;
    } // end getProvider        

/**
 * <p>
 * Method used to start all SIP Services.
 * </p>
 * </p>
 */
    public void removeCall(String callid) throws Exception {        
        if (ongoingCalls.containsKey(callid))
        {
            //SystemInfo.log.debug("Removeu a chamada");
            ongoingCalls.remove(callid);
        }
    } // end removeCall        

/**
 * <p>
 * Method used to stop all SIP Services.
 * </p>
 * </p>
 */
    public void start() {        
        if ( status == HALT ) {
            status=STARTING;
            init();
        }
    } // end start        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
    public void stop() {        
        commProvider.removeManagerListener(appName);
        try {
        commProvider.stop();
        status=HALT;
        } catch (Exception e){}
    } // end stop        

/**
 * <p>
 * Function that returns an InCallController object that contains a from field
 * </p>
 * <p>
 * equal to the given parameter String from.
 * </p>
 * </p>
 */
    public InCallController getCallFrom(String from) throws Exception {        
        //log.log("[InCommManager] looking for Call with from: " + from.split("@")[0]);
        InCallController estamesmo=null;
        long time=0;
        for ( Enumeration calls = ongoingCalls.elements(); calls.hasMoreElements() ;){
            InCallController call = (InCallController) calls.nextElement();
            //log.log("[InCommManager] found Call with from: " + call.getFrom().split("@")[0]);
            
            /*if (call.getFrom().split("@")[0].equals(from.split("@")[0]) )
                if (call.getCDR().getInitialDate().getTime()>=time){
                     time=call.getCDR().getInitialDate().getTime();
                  */   estamesmo= call;
               // }
            
        }
        
          return estamesmo;
    } // end getCallFrom        

/**
 * <p>
 * Function used to get the reference to this singleton object.
 * </p>
 * </p>
 */
    public static CallManager getReference(CallControllerListener callControllerListener) {        
        
    	
        System.out.println("getting Manager reference...");
        if (myself==null) {
        	_callControllerListener = callControllerListener;
        	myself=new ManagerImpl("incom");
        }
        return myself;
    } // end getReference        

 // end startCall        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a CallController with ...
 * </p><p>
 * @param from ...
 * </p><p>
 * @param to ...
 * </p><p>
 * @param controlLevel_ ...
 * </p>
 */
    public CallController startCall(String uid, String owner, CallControllerListener listener, final String from, final String fromName, final String to, int controlLevel, int flow) {
        
// to be changed to support configurations where we have no Inex
        // pch: mudar para o comm3pcc
        
        
        final String id=((CallIdHeader)commProvider.getNewCallIdHeader()).getCallId();
        
        CallController call= null;
        /*UAController ua = null;*/
        try{
            
                
                call=(CallController) new Comm3pcc( owner, listener, from, fromName, to, id, controlLevel, flow);

        } catch(Exception e ) {}    
        
//        iCall.setListener((InexCallControllerListener)call);
//        InexCallInfo info_=new InexCallInfo((InexCCListener)iCall,owner,to,id);
       
//        ((InexCall)iCall).setInexCall(info_);
//        InexCCManagerImpl.getReference().setListener((InexCCListener)iCall,owner,to.split("@")[0],id);
//        InexCCManagerImpl.getReference().setListener((InexCCListener)iCall,owner,from.split("@")[0],id);
       // CallRegister callRegister=new CallRegister();
        call.start();
//pch        ongoingCalls.put(id, call);
        return call;
    } // end startCall        

/**
 * <p>
 * Function that returns and URI of the type:
 * </p>
 * <p>
 * 2132@inocrea.org
 * </p>
 * <p>
 * ATTENTION!: it mantains the domain existant domain. If the domain doesn't
 * </p>
 * <p>
 * exist, it uses the proxy domain!
 * </p>
 * <p>
 * entry may be:
 * </p>
 * <p>
 * sip:2132@inocrea.org:5060
 * </p>
 * <p>
 * 2132@inocrea.org
 * </p>
 * <p>
 * 2132@inocrea.org:5060
 * </p>
 * <p>
 * sip:2132
 * </p>
 * <p>
 * 2132
 * </p>
 * </p>
 */
    public String getURI(String in) {        
        String out=in;
        if (hasChar(out,"<"))
            out=out.split("<")[1];
        if (hasChar(out,">"))
            out=out.split(">")[0];
        if (hasChar(out,":"))
            if (out.split(":")[0].equalsIgnoreCase("sip"))
                out=out.split(":")[1];
        if (!hasChar(out,"@")){
          //SystemInfo.log.debug("[ManagerImpl.getURI] proxy: "+info.getProxy());

           out=out+"@"+info.getProxy();
        }
        return out;
    } // end getURI        

/**
 * <p>
 * Function that returns true if a String contains a specified char;
 * </p>
 * <p>
 * String text -> the String where the char will be searched.
 * </p>
 * <p>
 * String chr -> contains the char that will be searched (first char)
 * </p>
 * </p>
 */
    private boolean hasChar(String text, String chr) {        
        boolean has=false;
        int counter=0;
             while(counter<text.length()){
                 if (text.charAt(counter)==chr.charAt(0)) has=true;
                 counter++;
             }
        return has;
    } // end hasChar        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p><p>
 * @param in ...
 * </p>
 */
    public String whoIsTalkingTo(String in) {        
        Enumeration x=conversationsList.elements();
        Conversation conv=null;
        while (x.hasMoreElements()){
            conv = (Conversation)x.nextElement();
            if (conv.getFrom().equalsIgnoreCase(getUserNameOnly(in))){
                return conv.getTo();
            }else if(conv.getTo().equalsIgnoreCase(getUserNameOnly(in))){
                return conv.getFrom();
            }
        }
        return null;
    } // end whoIsTalkingTo        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p><p>
 * @param in ...
 * </p>
 */
    private String getUserNameOnly(String in) {        
        String out=in;
        if (hasChar(out,"@")){
            out=out.split("@")[0];
        }
        if (out.split("ip:").length>1)
            out=out.split("ip:")[1];
        return out;
    } // end getUserNameOnly        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param user1 ...
 * </p><p>
 * @param user2 ...
 * </p>
 */
    public void addTalk(String user1, String user2) {        
        conversationsList.add(new Conversation(getUserNameOnly(user1),getUserNameOnly(user2)));
    } // end addTalk        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param user1 ...
 * </p><p>
 * @param user2 ...
 * </p>
 */
    public void removeTalk(String user1, String user2) {        
        
        Enumeration x=conversationsList.elements();
        Conversation conv=null;
        while (x.hasMoreElements()){
            conv = (Conversation)x.nextElement();
            if (conv.getFrom().equalsIgnoreCase(getUserNameOnly(user1)) && conv.getTo().equalsIgnoreCase(getUserNameOnly(user2))){
                conversationsList.remove(conv);
                return;
            }
        }
    } // end removeTalk        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
    public void processIM(final Request request) {        
        final CallManager man=this;
        //log.log("man"+man+"request"+request);
        final String callId=((CallIdHeader)request.getHeader(CallIdHeader.NAME)).getCallId();
        Thread putAnotherSession =new Thread(){
            public void run(){
               // ongoingCalls.put(callId,new IMController(request,callId,man));
                
            }
        };
        putAnotherSession.start();
    } // end processIM        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param userTo ...
 * </p>
 */
    public void setCallEventUser(String userTo) {        
        callEventUser = userTo;
    } // end setCallEventUser        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param listener_ ...
 * </p>
 */
   /* public void setInComListener(InComListener listener_) {        
        listener = listener_;
    } */// end setInComListener        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
    public void processSubscribe(final Request request){
       
       // log.log("\n\n ****************************************\nRECEBI UM SUBSCRIBE!!!! VIVA!!!\n***********************\n\n");
    }
    
    public CallController setCallModel(CallController currentCallController, String callModel, Leg leg) {
        
        CallController call = null;
        
        /*if (callModel.equals(CallController.CLASS_END_POINT_CALL_CONTROLLER) ){
            call = new Comm(currentCallController,this, leg);
        
            ongoingCalls.put(call.getCallId(), call );
        }*/
        
        return call;
        
    }
    
    /**
     * <p>
     *
     * @param args the command line arguments
     * </p>
     * </p>
     */
    


  ///////////////////////////////////////
  // inner classes/interfaces

/**
 * <p>
 * 
 * </p>
 */
class Conversation {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    String from; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    String to; 

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a Conversation with ...
 * </p><p>
 * @param from_ ...
 * </p><p>
 * @param to_ ...
 * </p>
 */
     Conversation(String from_, String to_) {        
        from=from_;
        to=to_;
    } // end Conversation        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    String getFrom() {        
        return from;
    } // end getFrom        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    String getTo() {        
        return to;
    } // end getTo        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param in ...
 * </p>
 */
    void setFrom(String in) {        
        from=in;
    } // end setFrom        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param in ...
 * </p>
 */
    void setTo(String in) {        
        to=in;
    } // end setTo        

} // end ManagerImpl.Conversation

public SipProvider getSipProvider() {
    return provider;
}

public void setSipProvider(SipProvider provider) {
    this.provider = provider;
}  

} // end ManagerImpl





