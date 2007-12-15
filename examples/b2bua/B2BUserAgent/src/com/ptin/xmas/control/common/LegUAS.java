package com.ptin.xmas.control.common;

import com.ptin.xmas.control.common.CommProviderImpl;
import com.ptin.xmas.control.common.interfaces.CallProvider;
import com.ptin.xmas.control.common.interfaces.CommFactory;
import com.ptin.xmas.control.common.interfaces.Call;
import com.ptin.xmas.control.common.interfaces.Leg;
import com.ptin.xmas.control.common.CallData;
import com.ptin.xmas.control.common.Logx;
import com.ptin.xmas.control.common.SystemInfo;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.Transaction;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 *This class implements interface Leg.
 *It constructs and sends ,receives and processes messages from and to the user agent client.
 *The logic processing of the call is done on InCall or Comm3pcc.
 */
public class LegUAS implements Leg {
    //  protected ExceptionManager exceptionManager;

    private String to1 = "";
    private String to2 = "";
    Dialog dialog=null;
  //  String app = "crear03incom@172.29.250.54";
 //   private FromHeader fromHeader = null;
 //   private ToHeader toHeader = null;
    private String reqUri1 = "";
    private String fromUri = "";
    private String toUri = "";
    private String to2Uri = "";
    private String redirectUri= "";
    //  private AppManager appManager=null;
    String proxyserv = "172.29.250.60";
  //  ContentTypeHeader fromC_TypeHeader=null;
  //  ContentTypeHeader toC_TypeHeader=null;
    ServerTransaction lastTransaction=null;
    //ServerTransaction invite1Tid = null;
  //  ServerTransaction invite2Tid = null;
  //  ServerTransaction reinvite1Tid = null;
  //  ServerTransaction ack0Tid = null;
  //  ServerTransaction ack1Tid = null;
  //  ServerTransaction ack2Tid = null;
    //  protected CommManager manager = null;
    protected CallProvider callProvider = null;
    protected CommFactory commFactory = null;
    protected  int status=0; // é preciso meter isto synchronized
    //  User userTo=null;
    //  UserManager userManager=null;
    static int  STARTING = 0;
    static int  PROCESSING_INVITE_FROM = 1;
    static int  INVITING_TO = 2;
    //static int  PROCESSING_OK_TO = 3;
    static int  RESPONDING_OK_FROM = 4;
    //static int  ACKING_TO = 5;
    static int  TALKING = 6;
    //static int  BYE = 7;
    static int  HOLD =8;
    static int  WAIT =9;
    static int  REJECT =10;
    static int  BUSY =12;
    static int  NETWORKUNAVAILABLE =13;
    static int  NOANSWER =14;
    static int  UNAVAILABLE =15;
    static int RINGING=11;
    static int MESSAGERCVD=16;
    static int SUBSCRIBERCVD=17;
    static int NOTIFYRCVD=18;
    static int INFO=19;
    private String sdp = "";
    private String cType="";
    private String csubType="";
    private String sdpRcv = "";
    private String cTypeRcv="";
    private String csubTypeRcv="";
//    private String fromTag="";
//    private String toTag="";
 //   private ViaHeader via = null;
    private Request invite1=null;
 //   private Request initial_Invite=null;
 //   private Request ackTo=null;
 //   private String ipFrom=null;
    private Request ack2=null;
    private String ipTo=null;
    private boolean sentAck=false;
    private int portFrom=5060;
    private int portTo=5060;
    
    private String callType = "";
    //  private CDR cdr=null;
    private String callId="";
    private SystemInfo info=null;
 //   private boolean mediaNotSet=false;
    private Call call=null;
    private boolean isDestination=false;
    private boolean ackReceived=false;
 //   private int messageCSeq=0;
    // private int infoCSeq=0;
 //   private String localTag=null;
 //   private String remoteTag=null;
 //   private Request firstRequest=null;
 //   private Response myOK=null;
 //   private ContactHeader imContactHeader=null;
    //as cenas pro IM,SUBSCRIBE,NOTIFY....
    private String userAgent=null;
    private ContactHeader contactHeader=null;
    private RecordRouteHeader recordRouteHeader=null;
 //   private int localCurrentCall=0;
 //   private int remoteCurrentCall=0;
    boolean alreadyDone=false;
    private Logx log=null;
    private boolean restart=false;
  
    private Request old_ack=null;
   
    /**
     *This constructor is only used when LegUAC has to start acting as a LegUAS.
     */
    public LegUAS(LegUAC old, String callType_){
        //log=Logx.getReference();
        try{
            info=SystemInfo.getReference("incom");
           }catch(Exception ex){ex.printStackTrace();};

        callProvider=(CallProvider)CommProviderImpl.getReference();
        
         if(old.getCallType().equals(CallData.INCALL))
            callProvider.removeInCallLeg(old.getCallId());
        else  if(old.getCallType().equals(CallData.OUTCOMM)) callProvider.removeOutCommLeg(old.getCallId());
         
           
           
           
        old_ack=old.getOldAck();
        callType = callType_;
       
        proxyserv=info.getProxy();
        call=old.getCall();
    dialog=old.getDialog(); 
        fromUri=old.getFromUri();
        lastTransaction=old.getInviteRcvd();
        invite1=lastTransaction.getRequest();
        sdp=old.getSDPEnv();
        cType=old.getCTypeEnv();
        csubType=old.getCSubTypeEnv();
        
      // invite1=old.getInvReq();
        
      //  invite1Tid=old.getInvTid();
        
        isDestination=old.isDestination();
        
   //     localCurrentCall=old.getLocalCurrentCall();
   //     remoteCurrentCall=old.getRemoteCurrentCall();
        
   //     CSeqHeader cSeqHeader=(CSeqHeader)invite1.getHeader(CSeqHeader.NAME);
        
  //      messageCSeq=cSeqHeader.getSequenceNumber();
        
        
        
        init();
    }
    /**
     *This construtor is used when a first Invite is received.
     */
    public LegUAS(String from_, String to_, Call call_, String callId_, String cType_, String csubType_, String sdp_, boolean isDestination_, ServerTransaction invite_Tid, Request inviteRcvd, String callType_) {
        
        //log=Logx.getReference();
        System.out.println("Constructor LegUAS");
        call=call_;
        fromUri=to_;
        callType = callType_;
        
        try{
            info=SystemInfo.getReference("incom");
           }catch(Exception ex){ex.printStackTrace();};
        proxyserv=info.getProxy();
       lastTransaction=invite_Tid;

        callId=callId_;
        // appManager=AppManager.getRef();
        dialog=invite_Tid.getDialog();
        //invite1Tid=invite_Tid;
        invite1=inviteRcvd;
    //    CSeqHeader cSeqHeader=(CSeqHeader)invite1.getHeader(CSeqHeader.NAME);
    //    messageCSeq=cSeqHeader.getSequenceNumber();
        isDestination=isDestination_;
        init();
        
    }
    
    
   /* public LegUAS(CallController call_,boolean isDestination_,String from_,String to_,String callId_){
        //log=Logx.getReference();
        fromUri=from_;
        toUri=to_;
        isDestination=isDestination_;
        call=call_;
        callId=callId_;
        callProvider=(CallProvider)CommProviderImpl.getReference();
        callProvider.addLeg(callId,(Leg)this);
        commFactory=callProvider.getCommFactory();
        info=new SystemInfo("incom");
        ackReceived=true;
        
    }*/
    
    
    private void init() {
        
        System.out.println("[LegUAS] constructor begin");
  //      initial_Invite=invite1;
  //      System.out.println("ipFrom="+ipFrom);
        
        CallIdHeader idHeader=dialog.getCallId();
        callId=idHeader.getCallId();
        callProvider=(CallProvider)CommProviderImpl.getReference();
        
        callProvider.addLeg(callId,(Leg)this);
         if(callType.equals(CallData.INCALL))
            callProvider.addInCallLeg(callId, (Leg) this);
        else  if(callType.equals(CallData.OUTCOMM)) callProvider.addOutCommLeg(callId, (Leg) this);
        
        commFactory=callProvider.getCommFactory();
        status = STARTING;
  //      this.messageCSeq=((CSeqHeader)initial_Invite.getHeader(CSeqHeader.NAME)).getSequenceNumber();
        try{
    //        if (invite1.getHeader(ContactHeader.NAME)!=null){
    //            contactHeader=(ContactHeader)invite1.getHeader(ContactHeader.NAME);
    //            ipFrom=getIp(((ContactHeader)invite1.getHeader(ContactHeader.NAME)).getAddress().toString());//   ipFrom=getIp(invite1.getContactHeaders().next().getValue());
    //            portFrom=getPort(((ContactHeader)invite1.getHeader(ContactHeader.NAME)).getAddress().toString());//   portFrom=getPort(invite1.getContactHeaders().next().getValue());
    //        }
            /*if (invite1.getHeader(RecordRouteHeader.NAME)!=null){
                recordRouteHeader=(RecordRouteHeader)invite1.getHeader(RecordRouteHeader.NAME);
            }*/
        }catch(Exception e){System.out.println("Exception on LegUAS.init(); "+e);}
    }
    
    
    /**
     *Function that returns the Ip/domain from the given contact uri.
     */
    private String getIp(String contact){
        String ip=info.getProxy();
        if (contact!=null){
            if(hasChar(contact,"@")){
                ip=contact.split("@")[1];
            }else ip=contact.split("sip:")[1];
            if (hasChar(ip,":")){
                ip=ip.split(":")[0];
            }else if (hasChar(ip,";")){
                ip=ip.split(";")[0];
            }else if (hasChar(ip,">")){
                ip=ip.split(">")[0];
            }
        }
        return ip;
    }
    
    /**
     *Function that returns the port of a given contact uri.
     */
    private int getPort(String contact){
        System.out.println("InCall.getPort(contact)  \""+contact+"\"");
        int port=5060;
        String x=contact;
        if (x!=null&&hasChar(x,":")){
            if (x.split(":").length>=3){
                if(hasChar(x,":"))
                    x=x.split(":")[2];
                if(hasChar(x,">")){
                    x=x.split(">")[0];
                }
                if(hasChar(x,";"))
                    x=x.split(";")[0];
                if (x.length()>2){
                    System.out.println("Got the port: "+x);
                    port=Integer.parseInt(x);
                }
            }
        }
        return port;
    }
    private boolean hasChar(String text,String a){
        boolean x=false;
        int z=0;
        while (z<text.length()){
            if(text.charAt(z)==a.charAt(0)){
                x=true;
            }
            z++;
        }
        return x;
    }
    /**
     *Method used to send a 200OK response.
     */
    private void ok(){
        System.out.println("LegUAS --> sending OK");
        
        Response okResponse =commFactory.createResponse(invite1, sdp, cType, csubType);
        
       /*  if (okResponse.hasRecordRouteHeaders()){
            okResponse.removeRecordRouteHeaders();
            okResponse.setRecordRouteHeaders(commFactory.createRecordRouters(info.getUA()));
         }*/
       // okResponse.setHeader(commFactory.getRecordRouteHeader(info.getUA()));
        if (((ToHeader)okResponse.getHeader(ToHeader.NAME)).getTag()==null){
            ToHeader toHeader=(ToHeader)okResponse.getHeader(ToHeader.NAME);
            try{
                
               
                
                toHeader.setTag(Integer.toString(dialog.hashCode()));
            }catch(Exception e ){System.out.println(""+e);}
            okResponse.setHeader(toHeader);
        }
        
        okResponse.setHeader(commFactory.createContactHeader(call.getAppName()+"@"+info.getIp()));
//        this.toHeader=(ToHeader)okResponse.getHeader(ToHeader.NAME);
 //       this.fromHeader=(FromHeader)okResponse.getHeader(FromHeader.NAME);
        // try{   if (invite1.hasUserAgentHeader()) okResponse.setUserAgentHeader(commFactory.createUserAgent(invite1.getUserAgentHeader().getValue())); }catch(HeaderParseException hpe){System.out.println(""+hpe);}
        
        //callProvider.sendResponse(invite1Tid,okResponse);
        try{
           int n=((CSeqHeader)okResponse.getHeader(CSeqHeader.NAME)).getSequenceNumber();
           if (!isDestination) SystemInfo.log.debug("LegUAS.ok() from "+n);
           else SystemInfo.log.debug("LegUAS.ok() to "+n);
            lastTransaction.sendResponse(okResponse);
        }catch(SipException se){System.out.println(""+se);}
        
        //callProvider.sendResponse(invite1Tid,Response.OK,sdp,cType, csubType);
    }
    
    /**
     *method used to process a received bye msg
     *sends ok to UA who sent the bye msg and sends another bye to the UA on the other end
     */
    public void processBye(Request byeRcvd,ServerTransaction tid) {
        System.out.println("[Call] processBye begin! \n");
        Response responseToBye=commFactory.createResponse(Response.OK,tid.getRequest());
        try{
            tid.sendResponse(responseToBye);
        }catch(SipException se){System.out.println(""+se);}
        call.setStatusCode(this.BYE,isDestination);
        try {
            callProvider.removeLeg(/*cdr.getCallId()*/callId);
        } catch (Exception e ){System.out.println(""+e);}
    }
    private void unavailable(){
        System.out.println("LegUAS.unavailable init");
        try{
            lastTransaction.sendResponse(commFactory.createResponse(Response.BUSY_EVERYWHERE, lastTransaction.getRequest()));
        }catch(SipException se){System.out.println(""+se);}
        //       callProvider.sendResponse(invite1Tid,Response.BUSY_EVERYWHERE);
        callProvider.removeLeg(callId);
        System.out.println("LegUAS.unavailable end");
    }
    /**
     *Method used to send a bye message.
     */
    public void bye() {
        System.out.println("[InCall] BYE! init() \n");
        long byeTid = 0;
        //     DebugProxy.println("[InCall] sending bye to "+to2Uri);
        //     callProvider.sendBye(invite2Tid);
     //   System.out.println("[InCall] sending bye to "+fromUri.split("@")[0]+"@"+ipFrom);
        
        try{
            dialog.sendRequest(commFactory.getClientTransaction(dialog.createRequest(Request.BYE)));
        }catch(SipException se){System.out.println(""+se);}
        dialog.delete();
        //  Request byeMsg=commFactory.createBye(invite1,fromUri.split("@")[0]+"@"+ipFrom,app,/*portFrom*/5060);
        //   callProvider.sendRequest(byeMsg);
        status=this.BYE;
        System.out.println("[InCall] bye Removing from commManager!");
/*     CallInformation callInf= cdr.getCallInfo(currentCall);
     callInf.setStopTime(System.currentTimeMillis());
     cdr.addCallInfo(callInf);
     cdr.setEndTime(System.currentTimeMillis());
     showCallData();*/
        try {
            //manager.removeCall(/*cdr.getCallId()*/callId);
            callProvider.removeLeg(/*cdr.getCallId()*/callId);
        } catch (Exception e ){System.out.println(""+e);}
        System.out.println("[InCall] BYE! end() \n");
    }
    /**
     *method used to hangup an onGoingCall
     */
    public void hangup() {
        System.out.println("Hanging Up Call...");
        if (ackReceived){this.bye();}
        else {this.unavailable();}
    }
    /**
     *method used to process a received cancel msg
     *sends ok to UA who sent the cancel msg and sends another cancel message to the UA on the other end
     */
    public void processCancel(Request cancelRcvd,ServerTransaction tid){
        try{
            tid.sendResponse(commFactory.createResponse(Response.OK,tid.getRequest()));
        }catch(SipException se){System.out.println(""+se);}
        //callProvider.sendResponse(tid,Response.OK);
        call.setStatusCode(this.BYE,isDestination);
        try {
            callProvider.removeLeg(callId);
        } catch (Exception e ){System.out.println(""+e);}
    }
    /**
     *method used to send a cancel to the caller UA
     */
    
    
    public void processAck(Request ack,ServerTransaction tid){
        System.out.println("LegUAS.processAck(ack,tid)");
        ackReceived=true;
        try{
            if (ack.getRawContent()!=null)
                call.setSdp(((ContentTypeHeader)ack.getHeader(ContentTypeHeader.NAME)).getContentType(),((ContentTypeHeader)ack.getHeader(ContentTypeHeader.NAME)).getContentSubType(),ack.getRawContent().toString(),isDestination);
            else call.setSdp("", "", null, isDestination);
            call.setStatusCode(this.ACKING_TO, isDestination);
            if (restart){
                restart=false;
                call.restartMe(isDestination);
            }
        }catch(Exception e){System.out.println(""+e);}
    }
    
    /**
     *method called by CommProviderImpl to process a Request
     */
    public void processRequest(Request request, ServerTransaction tid) {
        System.out.println("\n****************************************************************");
        System.out.println("LegUAS");
        String method=null;
        try{
            method=request.getMethod();
        }catch(Exception e){System.out.println(""+e);}
        if (method.equals(Request.ACK)/*&&status.getFlag()==this.RESPONDING_OK_FROM*/)
        {   this.processAck(request,tid);
        }else if (method.equals(Request.CANCEL))
        {   this.processCancel(request,tid);
        }else if (method.equals(Request.BYE)){
            this.processBye(request,tid);
        }else if (method.equals(Request.INVITE)/*&&ackReceived*/){
            //System.out.println("New Invite Received from "+((FromHeader)request.getHeader(FromHeader.NAME)).getAddress().getDisplayName());
           /*     CallInformation callInf= cdr.getCallInfo(currentCall);
                callInf.setStopTime(System.currentTimeMillis());
                cdr.addCallInfo(callInf);
                status.setFlag(this.PROCESSING_INVITE_FROM);
           processInviteFrom(request,tid);*/
           int n=((CSeqHeader)request.getHeader(CSeqHeader.NAME)).getSequenceNumber();
           if (!isDestination) SystemInfo.log.debug("LegUAS.processRequest(invite) from "+n);
           else SystemInfo.log.debug("LegUAS.processRequest(invite) to "+n);
            
       //     messageCSeq++;
       //     System.out.println(" message CSeq = "+messageCSeq);
            invite1=request;
            lastTransaction=tid;
            
            try{
                if (request.getContent()!=null) {
                  
                    sdpRcv=new String(request.getRawContent());
                    csubTypeRcv=((ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME)).getContentSubType();
                    cTypeRcv=((ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME)).getContentType();
                    call.setSdp(cTypeRcv,csubTypeRcv ,sdpRcv.toString() , isDestination);
                }
                call.setStatusCode(this.PROCESSING_INVITE_FROM, isDestination);
            }catch(Exception e){System.out.println(""+e);}
            
        }else if(method.equalsIgnoreCase(Request.MESSAGE)){
            processMessage(request,tid);
        }else if (method.equalsIgnoreCase(Request.INFO)){
            processInfo(request,tid);
        }else if (method.equalsIgnoreCase(Request.NOTIFY)){
            processNotify(request,tid);
        }else if (method.equalsIgnoreCase(Request.SUBSCRIBE)){
            processSubscribe(request,tid);
        }
    }
    
    private void processInfo(Request request,ServerTransaction serverTransaction){
        System.out.println("LegUAS.processInfo begin");
        
        //if (alreadyDone)messageCSeq++;
        alreadyDone=true;
        ContentTypeHeader ctHeader=(ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME);
        call.setSdp(ctHeader.getContentType(),ctHeader.getContentSubType(),request.getRawContent().toString(),isDestination);
        call.setStatusCode(this.INFO, isDestination);
      //  if (request.getHeader(UserAgentHeader.NAME)!=null)call.setUA(((UserAgentHeader)request.getHeader(UserAgentHeader.NAME)).toString().split(":")[1].trim(),isDestination);
      //  if (request.getHeader(ContactHeader.NAME)!=null){
      //      ipFrom=getIp(((ContactHeader)request.getHeader(ContactHeader.NAME)).getAddress().toString());//   ipFrom=getIp(invite1.getContactHeaders().next().getValue());
      //      portFrom=getPort(((ContactHeader)request.getHeader(ContactHeader.NAME)).getAddress().toString());//   portFrom=getPort(invite1.getContactHeaders().next().getValue());
      //  }
        imOK(request,serverTransaction);
        System.out.println("LegUAS.processInfo end");
    }
    private void processNotify(Request request,ServerTransaction serverTransaction){
        System.out.println("LegUAS.processNotify begin");
        ContentTypeHeader ctHeader=(ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME);
        call.setSdp(ctHeader.getContentType(),ctHeader.getContentSubType(),request.getRawContent().toString(),isDestination);
        call.setStatusCode(NOTIFYRCVD, isDestination);
       // if (request.getHeader(ContactHeader.NAME)!=null){
       //     ipFrom=getIp(((ContactHeader)request.getHeader(ContactHeader.NAME)).getAddress().toString());//   ipFrom=getIp(invite1.getContactHeaders().next().getValue());
       //     portFrom=getPort(((ContactHeader)request.getHeader(ContactHeader.NAME)).getAddress().toString());//   portFrom=getPort(invite1.getContactHeaders().next().getValue());
       // }
        imOK(request,serverTransaction);
        System.out.println("LegUAS.processNotify end");
    }
    private void processSubscribe(Request request,ServerTransaction serverTransaction){
        System.out.println("LegUAS.processSubscribe begin");
        ContentTypeHeader ctHeader=(ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME);
        call.setSdp(ctHeader.getContentType(),ctHeader.getContentSubType(),request.getRawContent().toString(),isDestination);
        call.setStatusCode(SUBSCRIBERCVD, isDestination);
       // if (request.getHeader(ContactHeader.NAME)!=null){
       //     ipFrom=getIp(((ContactHeader)request.getHeader(ContactHeader.NAME)).getAddress().toString());//   ipFrom=getIp(invite1.getContactHeaders().next().getValue());
       //     portFrom=getPort(((ContactHeader)request.getHeader(ContactHeader.NAME)).getAddress().toString());//   portFrom=getPort(invite1.getContactHeaders().next().getValue());
       // }
        imOK(request,serverTransaction);
        System.out.println("LegUAS.processSubscribe end");
    }
    
    
    private void processMessage(Request request,ServerTransaction serverTransaction){
        System.out.println("LegUAS.processMessage begin");
        //messageCSeq++;
        
    //    firstRequest=request;
        if (request.getHeader(ContactHeader.NAME)!=null)
    //        imContactHeader=(ContactHeader)request.getHeader(ContactHeader.NAME);
     //   remoteTag = ((FromHeader)request.getHeader(FromHeader.NAME)).getTag();
 //       ContentTypeHeader ctHeader=(ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME);
//        call.setSdp(ctHeader.getContentType(),ctHeader.getContentSubType(),request.getRawContent(),isDestination);
        call.setStatusCode(MESSAGERCVD, isDestination);
        if (request.getHeader(UserAgentHeader.NAME)!=null)
          //  call.setUA(((UserAgentHeader)request.getHeader(UserAgentHeader.NAME)).toString().split(":")[1].trim(),isDestination);
     //   if (request.getHeader(ContactHeader.NAME)!=null){
     //       ipFrom=getIp(((ContactHeader)request.getHeader(ContactHeader.NAME)).getAddress().toString());//   ipFrom=getIp(invite1.getContactHeaders().next().getValue());
     //       portFrom=getPort(((ContactHeader)request.getHeader(ContactHeader.NAME)).getAddress().toString());//   portFrom=getPort(invite1.getContactHeaders().next().getValue());
     //   }
     //   try{
     //       remoteTag=((FromHeader)request.getHeader(FromHeader.NAME)).getTag();
     //   }catch(Exception e){System.out.println("unable to get remote tag... "+e);}
        imOK(request,serverTransaction);
        System.out.println("LegUAS.processMessage end");
    }
    private void imOK(Request request,ServerTransaction serverTransaction){
        System.out.println("LegUAS.imOK begin");
        Response okResponse =commFactory.createResponse(Response.OK,request);
        okResponse.setHeader(commFactory.getRecordRouteHeader(info.getUA()));
        if (((ToHeader)okResponse.getHeader(ToHeader.NAME)).getTag()==null){
            ToHeader toHeader=(ToHeader)okResponse.getHeader(ToHeader.NAME);
      /*      try{
                if (localTag==null) localTag=gov.nist.javax.sip.Utils.generateTag();
                toHeader.setTag(localTag);
            }catch(Exception e ){System.out.println(""+e);}*/
            okResponse.setHeader(toHeader);
        }
        okResponse.setHeader(commFactory.createContactHeader(call.getAppName()+'@'+info.getIp()));
    //    myOK=okResponse;
   //     System.out.println("\n...\n a mensagem OK a enviar... \n"+myOK+"\n....\n");
        //    pausa(5);
        if (serverTransaction==null)
            callProvider.sendResponse(okResponse);
        else
            try{
                
                serverTransaction.sendResponse(okResponse);
            }catch(SipException se){System.out.println(""+se);}
        System.out.println("LegUAS.imOK end");
    }
    
    /**
     *method used to process "ringing" responses
     */

    /**
     *method used to forward a call (used by User)
     *sourceUri->From URI
     *destinationUri->To URI
     *finalToUri->destination for the call (URI)
     *domain->the domain on the request URI
     */
    
    
/*
   public void showCallData(){
       int x=1;
       DebugProxy.println("\n****************************************************************");
       while (cdr.hasCall(x)){
           CallInformation callInf=cdr.getCallInfo(x);
           DebugProxy.println("\nCall From: "+callInf.getFrom()+"\nTo: "+callInf.getTo()+"\nStarted at:"+callInf.getInitialDate().toString()+"\nEnded at:"+callInf.getFinalDate().toString()+"\nDuration: "+callInf.getCallTime()+" seconds\nMedia Type: ");
           Integer y=new Integer(0);
           Hashtable media=callInf.getMediaType();
           while (media.containsKey(y.toString())){
               DebugProxy.println("-->"+(String)media.get(y.toString()));
               y=new Integer(y.intValue()+1);
           }
           x++;
           DebugProxy.println("\n----------------------------------------------------------------");
       }
       DebugProxy.println("\n****************************************************************");
   }
 */
    
    /**
     *Function used by LegAUC's constructor.
     */
    public String getCallId(){
        return callId;
    }
    /**
     *This is the method called by CallController give instructions to this object.
     */
    public void setStatusCode(int stts){
        status=stts;
        if (stts==this.PROCESSING_OK_TO) this.ok();
        else if (stts==this.REJECT) this.unavailable();
        else if (stts==this.BYE) this.hangup();
        else if (stts==this.RINGING) this.ringing();
        else if (stts==this.MESSAGERCVD) this.message();
        else if (stts==this.INFO) this.info();
        else if (stts==this.NOTIFYRCVD) this.sendNotify();
        else if (stts==this.SUBSCRIBERCVD) this.subscribe();
        
    }
    /**
     *Method used to send a RINGING response.
     */
    private void ringing(){
        
        try{
            System.out.println("lastTransaction:"+lastTransaction);
            lastTransaction.sendResponse(commFactory.createResponse(Response.RINGING, lastTransaction.getRequest()));
        }catch(Exception e){System.out.println(""+e);}
        System.out.println("Sent RINGING!!!");
        
    }
    /**
     *Method used by CallController to set the SDP of the next messages.
     */
    public void setSdp(String cType_,String cSubType_,String sdp_){
        cType=cType_;
        csubType=cSubType_;
        sdp=sdp_;
    }
    
    public void processResponse(Response response,ClientTransaction tid){
        String method=((CSeqHeader)response.getHeader(CSeqHeader.NAME)).getMethod();
    //    if (method.equalsIgnoreCase(Request.MESSAGE)||method.equalsIgnoreCase(Request.INFO))
    //        remoteTag=((ToHeader)response.getHeader(ToHeader.NAME)).getTag();
    }
    
    public void restart(){if (isDestination)SystemInfo.log.debug("to restart!   (LegUAS)");else Logx.getReference().log("from restart!   (LegUAS)");
       // if (ackReceived==true)
            call.restartMe(isDestination);
       // else restart=true;
    }
    /**
     *returns the from field of this leg.
     */
    public String getFromUri(){
        return fromUri;
    }
    /**
     *Function used by LegAUC's constructor.
     */
    public Request getInvReq(){
        return invite1;
    }
    /**
     *Function used by LegAUC's constructor.
     */
    public ServerTransaction getInvTid(){
        return lastTransaction;
    }
    /**
     * Returns true if this object represents the destination leg.
     */
    public boolean isDestination(){
        return isDestination;
    }
    /**
     *Returns the CallController
     */
    public Call getCall(){
        return call;
    }
    /**
     *Returns the current call number.(1 per each invite transaction)
     */
 /*   public int getCurrentCall(){
        return messageCSeq;
    }*/
    /**
     *Sets the value of the UserAgentHeader that will be used on the next messages.
     */
    public void setUserAgent(String ua){
        userAgent=ua;
    }
    
    public void setACKPermission(boolean prm) {
    }
    
    private void message(){
   //     System.out.println("fromUri="+fromUri+"toUri="+toUri+"portTo="+portTo+"callId="+callId+"cType"+cType+"csubType"+csubType+"sdp="+sdp+"messageCSeq"+messageCSeq);
     //   messageCSeq++;
        //USAR AKI A METODOLOJIA DO ACKNhOLEDGE!!!!!!!!!
        String toUri_=toUri;
        if (ipTo==null||ipTo.equals("")) ipTo=info.getProxy();
        else toUri_=toUri_.split("@")[0]+"@"+ipTo;
        
        ClientTransaction ct=null;
        if (isDestination)System.out.println("SOU O DESTINO");else System.out.println("SOU A ORIGEM!!!!");
   //     ct=commFactory.createSpecialRequest(firstRequest, myOK, sdp, cType, csubType, imContactHeader, Request.MESSAGE,dialog.getLocalTag(),dialog.getLocalTag(),dialog.getRemoteTag(),callId,toUri.split("@")[0],userAgent);
   //     localTag=((FromHeader)ct.getRequest().getHeader(FromHeader.NAME)).getTag();
        try{
            Request workOnRequest=ct.getRequest();
            workOnRequest.setHeader(commFactory.createContactHeader(call.getAppName()+'@'+info.getIp()));
            
            System.out.println("\n...\n a mensagem MESSAGE a enviar ... \n"+ct.getRequest()+"\n...\n");
            // pausa(5);
            ct.sendRequest();
        }catch(Exception e){System.out.println(""+e);}
    }
    private void sendNotify(){
        
 //       messageCSeq++;
        
 /*       ClientTransaction ct=commFactory.createRequest(fromUri,toUri,toUri,info.getProxy(),info.getUA(),portTo, callId, cType, csubType, sdp, Request.NOTIFY, fromUri, messageCSeq);
        try{
            
            ct.sendRequest();
        }catch(Exception e){System.out.println(""+e);}*/
    }
    private void info(){
  //      messageCSeq++;
        
        String toUri_=toUri;
        if (ipTo==null||ipTo.equals("")) ipTo=info.getProxy();
        else toUri_=toUri_.split("@")[0]+"@"+ipTo;
        
        ClientTransaction ct=null;
        
   //     ct=commFactory.createSpecialRequest(firstRequest, myOK, sdp, cType, csubType, imContactHeader, Request.INFO,messageCSeq,localTag,remoteTag,callId,toUri.split("@")[0],userAgent);
   //     localTag=((FromHeader)ct.getRequest().getHeader(FromHeader.NAME)).getTag();
        try{
            Request workOnRequest=ct.getRequest();
            workOnRequest.setHeader(commFactory.createContactHeader(info.getUA()));
            
            System.out.println("\n...\n a mensagem INFO a enviar ... \n"+ct.getRequest()+"\n...\n");
            // pausa(5);
            ct.sendRequest();
        }catch(Exception e){System.out.println(""+e);}
    }
    
    
    private void subscribe(){
    //    System.out.println("fromUri="+fromUri+"toUri="+toUri+"portTo="+portTo+"callId="+callId+"cType"+cType+"csubType"+csubType+"sdp="+sdp+"messageCSeq"+messageCSeq);
    //    messageCSeq++;
        
    //    ClientTransaction ct=commFactory.createRequest(fromUri,toUri,toUri,info.getProxy(),info.getUA(),portTo, callId, cType, csubType, sdp, Request.SUBSCRIBE, fromUri, messageCSeq);
    //    try{
            
    //        ct.sendRequest();
    //    }catch(Exception e){System.out.println(""+e);}
    }

    
    public void processTimeout(Request request, Transaction transaction) {
        System.out.println("Process timeout - LegUAS");
    }
    
    public String getCallEndReason() {
        return "";
    }
    
/*    public String getIP() {
        return ipFrom;
    }*/
    public String getSdp(){
        return sdpRcv;
    }
    public String getCType(){
        return cTypeRcv;
    }
    public String getCSubType(){
        return csubTypeRcv;
    }
    public String getSdpEnv(){
        return sdp;
    }
    public String getCTypeEnv(){
        return cType;
    }
    public String getCSubTypeEnv(){
        return csubType;
    }
    public String getCallType(){
        return callType;
    }
   /* public FromHeader getFromHeader(){
        return fromHeader;
    }
    public ToHeader getToHeader(){
        return toHeader;
    }*/
   /* public ContactHeader getContactHeader(){
        return contactHeader;
    }
    */
    public String getSDP() {
        return sdpRcv;
    }
    
 /*   public RecordRouteHeader getRecordRouteHeader() {
        return recordRouteHeader;
    }*/
    
 /*   public int getLocalCurrentCall() {
        return localCurrentCall;
    }
   */ 
   /* public int getRemoteCurrentCall() {
        return remoteCurrentCall;
    }*/
    public Request getOldAck(){
        return old_ack;
    }
    public Dialog getDialog(){
        return dialog;
    }
    
    public void sendInfo(String type, String subType, String body) {
    }
    
}
