package com.ptin.xmas.control.common;

import com.ptin.xmas.control.common.CommProviderImpl;
import com.ptin.xmas.control.common.interfaces.CallProvider;
import com.ptin.xmas.control.common.interfaces.CommFactory;
import com.ptin.xmas.control.common.interfaces.Call;
import com.ptin.xmas.control.common.interfaces.Leg;
//import inocrea.data.CallData;
import com.ptin.xmas.control.common.Logx;
import com.ptin.xmas.control.common.SystemInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ListIterator;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.Transaction;
import javax.sip.header.CSeqHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 *This class implements interface Leg.
 *It constructs and sends ,receives and processes messages from and to the user agent server.
 *The logic processing of the call is done on InCall or Comm3pcc.
 */
public class LegUAC implements Leg {  
//  protected ExceptionManager exceptionManager;
    Dialog dialog=null;
//    private boolean ackPermission=false;
  //String vmailUri = "vmail@172.29.250.7";
  //String vmail = "vmail";
//  private String msg = "";
  private String to1 = "";
  private String to2 = "";
  String app = "crear03incom@172.29.250.54";
//  private FromHeader fromHeader = null;
 // private ToHeader toHeader = null;
  String proxyserv = "172.29.250.60";
  ClientTransaction lastTransaction = null;
  //ClientTransaction reinvite1Tid = null;
  //ClientTransaction ack0Tid = null;
  protected CallProvider callProvider = null;
  protected CommFactory commFactory = null;
  protected  int status=0; 
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
  static int MESSAGE=16;
  final int ASTERISK=20;
  private String sdp = "";
  private String cType="";
  private String csubType="";
  private String sdpRcv = "";
  private String cTypeRcv="";
  private String csubTypeRcv="";
 // private String toTag="";
 // private String fromTag="";
 // private ViaHeader via = null; 
 // private Request invite=null;
 // private Request ackTo=null;
 // private Request ack2=null;
 // private String ipTo=null;
  //private boolean sentAck=false;
  private int portTo=5060;
//  private int localCurrentCall=1;
//  private int remoteCurrentCall=1;
  private String callId=null;
  private SystemInfo info=null;
  private String from,to=null;
  private String fromName ="";
  private Call call=null;
  private boolean isDestination=false;
  private boolean acksent=false;
 // private Response ok1=null;
 // private String userAgent="";
  private String realFrom="";
  private String realTo="";
 // private ContactHeader contactHeader=null;
  private String callEndReason="";
 // private ServerTransaction invite2Tid=null;
 // private boolean legDead=false;
  private Request old_ack=null;
  private boolean restartAfterAck=false;
  private Response ok1=null;
  private String callType = "";
  private ServerTransaction inviteRcvd;
  
  /**
   *This constructor is only used when LegUAS has to start acting as a LegUAC.
   */
  public LegUAC(LegUAS old, String callType_){
      
      callType = callType_;
      old_ack=old.getOldAck();
      dialog=old.getDialog();
      try{
          info=SystemInfo.getReference("incom");
       }catch(Exception ex){ex.printStackTrace();};
      
      proxyserv= info.getProxy();
      
      System.out.println("Changing LegUAS 3 Leg UAC <--> App Name : "+app);
      
      app= info.getUA();
     // vmailUri= info.getVmail();
      
      /*
      app=info.getUA();
      proxyserv=info.getProxy();
      vmailUri=info.getVmail();*/
      call=old.getCall();
      
    //  vmail=vmailUri.split("@")[0];
      isDestination=old.isDestination();
  //    localCurrentCall=old.getLocalCurrentCall();
  //    remoteCurrentCall=old.getRemoteCurrentCall();
      callId=old.getCallId();
                               //AKI COMEÇAM OS VALORES DIFICEIS
      
  //    realTo=old;
  //    from=call.getTo();
   //   realFrom=from;
      //invite=old.getInvReq();
      sdp=old.getSdpEnv();
      cType=old.getCTypeEnv();
      csubType=old.getCSubTypeEnv();
      
    //  toTag=old.getFromHeader().getTag();
    //  fromTag=old.getToHeader().getTag();
    //  contactHeader=old.getContactHeader();
    //  ipTo=getIp(contactHeader.getAddress().getURI().toString());
    //  portTo=getPort(contactHeader.getAddress().getURI().toString());
    //  localCurrentCall++;
      
   /*   to=contactHeader.getAddress().getURI().toString();
      if (to.split("ip:").length>1)
          to=to.split("ip:")[1];
      if (!this.hasChar(to,"@")){
          if (this.hasChar(call.getTo(),"@"))
          to=call.getTo().split("@")[0]+'@'+to;
          else to=call.getTo()+"@"+to;
      }*/
      init();
      System.out.println("13");
  }
  
  
  
  public LegUAC(String from_, String fromName_, String to_, Call call_, String callId_, String cType_, String csubType_, String sdp_, boolean isDestination_, String realFrom_, String realTo_, String appName, String callType_) 
    {
        callType = callType_;
        realFrom=realFrom_;
        realTo=realTo_;
       // userAgent=userAgent_;
        isDestination=isDestination_;
        callId=callId_;
        fromName = fromName_;
        
        try{
            info=SystemInfo.getReference("incom"); 
               }catch(Exception ex){ex.printStackTrace();};

        proxyserv=  info.getProxy();
        
        System.out.println("LEG UAC <--> App Name : "+appName);
        
        app= appName; //info.getUA();
       // vmailUri= info.getVmail();
       // vmail=vmailUri.split("@")[0];
        from=from_;
        to=to_;
        call=call_;
        callId=callId_;
        cType=cType_;
        csubType=csubType_;
        sdp=sdp_;
        System.out.println("realFrom"+realFrom+"realTo"+realTo+"app"+app+"to"+to+"from"+from);
        init();
        
    }
 
/*  public LegUAC(String from_, String to_, Call call_, String callId_, String cType_, String csubType_, String sdp_, boolean isDestination_, String realFrom_, String realTo_) 
    {
        //log=Logx.getReference();
        
        realFrom=realFrom_;
        realTo=realTo_;
    //    userAgent=userAgent_;
        isDestination=isDestination_;
        callId=callId_;
        info=new SystemInfo("incom");
        proxyserv=  info.getProxy();
        System.out.println("LEG UAC <--> App Name : "+ realFrom);
        app= realFrom; //info.getUA();
       // vmailUri= info.getVmail();
       // vmail=vmailUri.split("@")[0];
        from=from_;
        to=to_;
        call=call_;
        callId=callId_;
        cType=cType_;
        csubType=csubType_;
        sdp=sdp_;
        
        init();
        
    }  */
  synchronized private void init()
    {
        //log.log("[LegUAC] constructor begin"); 
        callProvider=(CallProvider)CommProviderImpl.getReference();
        
        callProvider.addLeg(callId,(Leg)this);
        if(callType.equals(CallData.INCALL))
            callProvider.addInCallLeg(callId, (Leg) this);
        else  if(callType.equals(CallData.OUTCOMM)) callProvider.addOutCommLeg(callId, (Leg) this);
        
        
        commFactory=callProvider.getCommFactory(); // obter referência correctamente
        status = STARTING;
        //log.log("[LegUAC] right before .sendInviteTo(); ");
        sendInviteTo();
        
    }
 
  public String getCallType(){
      return callType;
  }
  
  private String getUA(){
   
      return app;
  }
  
  private String getProxy(){
      return proxyserv; 
  }
  
/**
 *Method used to send an Invite.
 */
   private void sendInviteTo() {
       //log.log("[LegUAC] inviteTo begin: finalToUri: " + to);
acksent=false;
       String domain="";

      if (realTo.split("@").length>1)   domain=to.split("@")[1];
      else domain=proxyserv;
                                                            //aqui era domain
      // System.out.println("realFrom:"+realFrom.split("@")[0]+"@"+proxyserv+"realTo:"+realTo+"to:"+to+"ipTo:"+info.getProxy()+"UA:"+getUA()+"portTo:"+portTo+"callId:"+callId+"cType:"+cType+"csubType:"+csubType+"sdp:"+sdp+"from:"+from);
       
      if (dialog!=null){
          try{
              Request inv=dialog.createRequest(Request.INVITE);
              inv.setHeader(commFactory.createContactHeader(call.getAppName()));
              inv.setContent(sdp, commFactory.createContentTypeHeader(cType,csubType));
              lastTransaction=commFactory.getClientTransaction(inv);
          }catch(Exception e){
              System.out.println("o nist-sip nao conseguiu gerar o invite..... ha algum parametro que nao esta bem!!!");
              e.printStackTrace();
          }
      }else{
        lastTransaction = commFactory.createRequest(realFrom.split("@")[0]+"@"+proxyserv, realTo, to ,domain,getUA(), portTo,/*cdr.getCallId()*/callId,cType, csubType, sdp, Request.INVITE,fromName,1);  
        dialog=lastTransaction.getDialog();
      }
       try{
           status=INVITING_TO;
           System.out.println(" -->"+lastTransaction.getRequest()); 
           //invite=lastTransaction.getRequest();
           if (isDestination)SystemInfo.log.debug("to sendInvite!");else SystemInfo.log.debug("from sendInvite!");
           //log.log(invite.toString());
          
           ClientTransaction cT=(ClientTransaction)lastTransaction;
           if(dialog.getState() == null)
       cT.sendRequest();
           else dialog.sendRequest(lastTransaction);
           
       
       /*try{
        File file=new File("logs_LEGUAC _all"); 
     
        FileOutputStream z=new FileOutputStream(file,true); 
     
        String date = java.util.Calendar.getInstance().getTime().toString();
        String log = new String("EJB - LEG UAC CALL "+ date +" FROM: "+realFrom.split("@")[0]+" TO: "+realTo.split("@")[0]+" \n"); 
        z.write(log.getBytes());
        z.close();
     
        }catch(Exception ex){ex.printStackTrace();};
       */
       
            //callProvider.sendRequest(invite);
       }catch(Exception e){e.printStackTrace();}

       //callProvider.sendRequest(invite);
       
      // DebugProxy.println("Sent request " + invite2.INVITE + "tid = " +  invite2Tid );
   }

    /**
     *Function that returns the Ip/domain from the given contact uri.
     */
      private String getIp(String contact){
      String ip=getProxy();
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
       //log.log("InCall.getPort(contact)  \""+contact+"\"");
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
                //log.log("Got the port: "+x);
                port=Integer.parseInt(x);
            }
           }
       }
       return port;
   }
   
   
    public String getCallId(){
        return callId;
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
    *method used to process a received bye msg
    *sends ok to UA who sent the bye msg and sends another bye to the UA on the other end
    */
   public void processBye(Request byeRcvd,ServerTransaction tid) 
   {

      /*  try{
        File file=new File("logs_OK_BYE"); 
     
        FileOutputStream z=new FileOutputStream(file,true); 
      
        String date = java.util.Calendar.getInstance().getTime().toString();
        String userFrom = ((FromHeader)byeRcvd.getHeader(FromHeader.NAME)).getAddress().toString().split("sip:")[1].split(":")[0];
        String userTo = ((ToHeader)byeRcvd.getHeader(ToHeader.NAME)).getAddress().toString().split("sip:")[1].split(":")[0];
        String  cSeqMethod=((CSeqHeader)byeRcvd.getHeader(CSeqHeader.NAME)).getMethod();
        
        String log = new String("LEG UAC - OK BYE  CALL "+ date +" Meth: "+cSeqMethod+" FROM: "+userFrom+" TO: "+userTo+" \n"); 
        z.write(log.getBytes());
        z.close();
     
        }catch(Exception ex){ex.printStackTrace();};
       */
       
       
       Dialog dialog=tid.getDialog();
       Response ok=commFactory.createResponse(Response.OK,dialog.getFirstTransaction().getRequest()); 
       ContactHeader contactHeader=(ContactHeader)dialog.getFirstTransaction().getRequest().getHeader(ContactHeader.NAME);       
       ok.setHeader(contactHeader);
       try{
       tid.sendResponse(ok);
       }catch(SipException se){se.printStackTrace();}
     //callProvider.sendResponse(tid,Response.OK);
     //long byeTid = 0;
     
     callProvider.removeLeg(callId);
     status=this.BYE;
     dialog.delete();
     call.setStatusCode(this.BYE,isDestination);
/*   CallInformation callInf= cdr.getCallInfo(currentCall);
     callInf.setStopTime(System.currentTimeMillis());
     cdr.addCallInfo(callInf);
     cdr.setEndTime(System.currentTimeMillis());
     showCallData();*/

  }
      
   /**
    *method used to send a bye message to both ends
    */
   
   public void bye() 
   {
       try{
       //n percebi o porquê disto	   
      /* if (dialog.getLocalSequenceNumber()>1){
           alternativeBye();
           return;
       }*/
     //log.log("[InCall] BYE! init() \n");
     
     //log.log("[InCall] sending bye to "+to);
     
   
     try{
         
    	 //CSeq header is wrong
         Request bye=dialog.createRequest(Request.BYE);
         //CSeqHeader cSeqHeader=(CSeqHeader)bye.getHeader(CSeqHeader.NAME);
         //cSeqHeader.setSequenceNumber(cSeqHeader.getSequenceNumber()+1);
         //bye.setHeader(cSeqHeader);
         //System.out.println("BYE: "+bye.toString());
         //log.log("the bye to be sent:"+bye);
         
         
         //commFactory.getClientTransaction(bye).sendRequest();
     dialog.sendRequest(commFactory.getClientTransaction(bye));
     
     }catch(SipException se){se.printStackTrace();}
dialog.delete();
     //callProvider.sendBye(inviteTid);
     
/*
     CallInformation callInf= cdr.getCallInfo(currentCall);
     callInf.setStopTime(System.currentTimeMillis());
     cdr.addCallInfo(callInf);
     cdr.setEndTime(System.currentTimeMillis());
     showCallData();*/
     try {
        callProvider.removeLeg(/*cdr.getCallId()*/callId);
     } catch (Exception e ){e.printStackTrace();}
     //log.log("[InCall] BYE! end() \n");
       }catch(Exception e){System.out.println("ERRO EM LegUAC.bye()");}
   }
   
   private void alternativeBye(){
       try{
     //log.log("[InCall] alternative BYE! init() \n");
     
     //log.log("[InCall] sending alternative bye to "+to);
     

     try{
         Request bye=dialog.createRequest(Request.BYE);
   /*      URI uri=   bye.getRequestURI();
         Integer integer=new Integer(getPort(contactHeader.getAddress().getURI().toString()));
         String urix="";
         if (userx==null)
             urix=getIp(contactHeader.getAddress().getURI().toString());
         else urix=userx+'@'+getIp(contactHeader.getAddress().getURI().toString());
         bye.setHeader(commFactory.getRouteHeader(urix,integer.intValue()));
         bye.setRequestURI(reqUri);
         CSeqHeader cSeqHeader=(CSeqHeader)bye.getHeader(CSeqHeader.NAME);
         cSeqHeader.setSequenceNumber(1);
         bye.setHeader(cSeqHeader);*/
         //log.log("the alternative bye to be sent:\n"+bye);
         
           ListIterator lista=old_ack.getHeaders(RouteHeader.NAME);
           if (bye.getHeader(RouteHeader.NAME)!=null)
               bye.removeHeader(RouteHeader.NAME);
           
           bye.setHeader((Header)lista.next());
           
           while (lista.hasNext())
               bye.addHeader((Header)lista.next());
           
           bye.setRequestURI(old_ack.getRequestURI()); 
           bye.setHeader(old_ack.getHeader(FromHeader.NAME));
         
         
     ClientTransaction b =commFactory.getClientTransaction(bye);
     b.sendRequest();
     }catch(Exception se){se.printStackTrace();}
dialog.delete();

     try {
        callProvider.removeLeg(callId);
     } catch (Exception e ){e.printStackTrace();}
     //log.log("[InCall] alternative BYE! end() \n");
       }catch(Exception e ){System.out.println("ERRO EM LegUAC.alternativeBye()");}
   }
   
   
   
   /**
    *method used to hangup the call.
    */
   public void hangup() {
       //log.log("Hanging Up Call...");
       if (acksent){this.bye();}
       else {this.cancel();}
   }
   /**
    *Method used to send a cancel request.
    */
   public void cancel(){
       try{
       //log.log("Cancel in progress...");
       Request cancel=null;
       try{
           ClientTransaction cT=(ClientTransaction)lastTransaction;
       cancel=cT.createCancel();
       }catch(SipException se){se.printStackTrace();}
       if (cancel.getHeader(ContactHeader.NAME)==null)
           cancel.setHeader(commFactory.createContactHeader(app));
       //cancel=commFactory.createCancel(invite);
       callProvider.sendRequest(cancel);
       }catch(Exception e){System.out.println("ERRO EM LegUAC.cancel()");}    
   }
      /**
    *method used to process a received cancel msg
    *sends ok to UA who sent the cancel msg and sends another cancel message to the UA on the other end
    */
   public void processCancel(Request cancelRcvd,ServerTransaction tid){
       try{
       tid.sendResponse(commFactory.createResponse(Response.OK,tid.getRequest()));
       }catch(SipException se){se.printStackTrace();}
           
       //callProvider.sendResponse(tid,Response.OK);
       //Request cancel=null;

           String userFrom=((FromHeader)cancelRcvd.getHeader(FromHeader.NAME)).getAddress().toString().split("sip:")[1].split(":")[0];
                //cancel=commFactory.createCancel(invite);
                //Dialog dialog=inviteTid.getDialog();
                try{
                    ClientTransaction cT=(ClientTransaction)lastTransaction;
                dialog.sendRequest(commFactory.getClientTransaction(cT.createCancel()));
                }catch(SipException se){SystemInfo.log.debug(""+se);}
                //callProvider.sendRequest(cancel);
          // log.log("[LEGUAC] cancel Removing from Provider!");
           try {
                callProvider.removeLeg(callId);
           } catch (Exception e ){e.printStackTrace();}
   }

   /**
    *method used to send a response 200 OK to an options request
    */
   private void processOptions(Request optionsRcvd,ServerTransaction tid){
       //log.log("Received and processing OPTIONS request");
       try{
        tid.sendResponse(commFactory.createResponse(Response.OK,tid.getRequest()));
       }catch(SipException se){se.printStackTrace();}
       //callProvider.sendOk(tid, Response.OK,sdp,cType,csubType);
   }
   /**
    *method called by CommProviderImpl to process a Request
    */
   public void processRequest(Request request, ServerTransaction serverTransaction) {
              //log.log("\n****************************************************************");
               //             log.log("LEGUAC");
       
       FromHeader fromHeader=(FromHeader)request.getHeader(FromHeader.NAME);
       String userFrom=((FromHeader)request.getHeader(FromHeader.NAME)).getAddress().toString().split("sip:")[1].split(":")[0];
       
       //log.log("userFrom="+userFrom);
       String method=null;
       try{
            method=request.getMethod();
       }catch(Exception e){e.printStackTrace();}
       if (method.equals(Request.BYE)){
           this.processBye(request,serverTransaction);
       }else if (method.equals(Request.OPTIONS)){
           this.processOptions(request,serverTransaction);
       }/*else if (method.equals(Request.INVITE)&&status==TALKING){
           //log.log("New Invite Received from "+((FromHeader)request.getHeader(FromHeader.NAME)).getAddress().getDisplayName());
                //CallInformation callInf= cdr.getCallInfo(currentCall);
                //callInf.setStopTime(System.currentTimeMillis());
                //cdr.addCallInfo(callInf);
                //status.setFlag(this.PROCESSING_INVITE_FROM);
           //processInviteFrom(request,tid);
           invite=request;
           invite2Tid=serverTransaction;
           try{
           call.setSdp(((ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME)).getContentType(), ((ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME)).getContentSubType(), request.getRawContent(), isDestination); 
           }catch(Exception e){e.printStackTrace();}
           try{
           call.setStatusCode(this.PROCESSING_INVITE_FROM, isDestination);
           }catch(Exception e){e.printStackTrace();}
       }*/
       else if (method.equals(Request.INVITE)){   //PARA TODO E QQ INVITE RECEBIDO   !!!!!
           int n=((CSeqHeader)request.getHeader(CSeqHeader.NAME)).getSequenceNumber();
           if (!isDestination) SystemInfo.log.debug("LegUAC.processRequest(invite) from "+n);
           else SystemInfo.log.debug("LegUAC.processRequest(invite) to "+n);
           inviteRcvd=serverTransaction;
       //    invite2Tid=serverTransaction;
           try{
               if (request.getContent()!=null){
                   cTypeRcv=((ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME)).getContentType();
                   csubTypeRcv=((ContentTypeHeader)request.getHeader(ContentTypeHeader.NAME)).getContentSubType();
                   sdpRcv=new String(request.getRawContent());
                   //call.setSdp(cType,csubType ,sdp.getBytes() , isDestination); 
               }
           
           }catch(Exception e){e.printStackTrace();}
           try{
           call.setStatusCode(this.PROCESSING_INVITE_FROM, isDestination);
           }catch(Exception e){e.printStackTrace();}
       }
   }
   /**
    *Method to process a received response message.
    */
   public void processResponse(Response response, ClientTransaction clientTransaction ) {
       System.out.println("LegUAC processResponse "+response.getStatusCode()+" "+dialog.getState().toString());
     int statusCode=0;
     long cSeq=0;
     try {
        statusCode=response.getStatusCode();
        cSeq=((CSeqHeader)response.getHeader(CSeqHeader.NAME)).getSequenceNumber();
     }catch(Exception e){e.printStackTrace();}
  //   DebugProxy.println("/n[Call] processResponse ,call status=" + status.getFlag() + " transaction: "+ tid +" response msg:\n " + response);
     //if (cSeq==dialog.getLocalSequenceNumber()){
         
        
         /*      try{
        File file=new File("logs_OK_BYE"); 
     
        FileOutputStream z=new FileOutputStream(file,true); 
      
        String date = java.util.Calendar.getInstance().getTime().toString();
        String userFrom = ((FromHeader)response.getHeader(FromHeader.NAME)).getAddress().toString().split("sip:")[1].split(":")[0];
        String userTo = ((ToHeader)response.getHeader(ToHeader.NAME)).getAddress().toString().split("sip:")[1].split(":")[0];
        String  cSeqMethod=((CSeqHeader)response.getHeader(CSeqHeader.NAME)).getMethod();
        
        String log = new String("LEG UAC - OK BYE  CALL "+ date +" Status: "+statusCode+" Meth: "+cSeqMethod+" FROM: "+userFrom+" TO: "+userTo+" \n"); 
        z.write(log.getBytes());
        z.close();
     
        }catch(Exception ex){ex.printStackTrace();};
       
         */
         
         
             
     if(statusCode==Response.RINGING){
        this.processRinging();
     }else if (statusCode==Response.OK)
     {
         callEndReason="NormalCallClearing";
       this.processOK(response,clientTransaction);
     }else if (statusCode==486||statusCode==600){
         callEndReason="busy";
         processUnavailable(response,clientTransaction,this.BUSY);
     }else if (statusCode==603){
            callEndReason="rejected";
            processUnavailable(response,clientTransaction,this.REJECT);
     }else if(statusCode==404){
             callEndReason="notFound";
             processUnavailable(response,clientTransaction,this.UNAVAILABLE);
     }else if (statusCode>=300){
            callEndReason="failure";
            processUnavailable(response,clientTransaction,this.NETWORKUNAVAILABLE);
     }
  //   }
   }
   
   /**
    *method used to process "unavailable" responses
    */
   private void processUnavailable(Response response,ClientTransaction tid,final int statusCode){
       //log.log("processing Busy");
       System.out.println("ProcessUnavailable init");
       Thread just=new Thread(){
           public void run(){
               call.setStatusCode(statusCode,isDestination);
           }
       };
       just.start();
       
        try{
            
          ClientTransaction xResponse=commFactory.getClientTransaction(tid.createAck());
          xResponse.sendRequest();
          
       }catch(Exception e){e.printStackTrace();}
       
       System.out.println("ProcessUnavailable end\n\n\n\n");
       
   
   }
   /**
    *method used to process "ringing" responses.
    */
   private void processRinging(){
       //log.log("Processing Ringing");
       call.setStatusCode(this.RINGING,isDestination);
   }

   /**
    *Method used to process 200OK responses.
    */
   private void processOK(Response ok,ClientTransaction tid){
       ok1=ok;
       SystemInfo.log.debug("[LegUAC.processOk] starting");
      if (isDestination)
    	  SystemInfo.log.debug("to receiveOK!\n"+ok+"\n");
      else SystemInfo.log.debug("from receiveOK!\n"+ok+"\n");
//       SystemInfo.log.debug(" LEG UAC processOK 2");
       try{
        //   fromTag=((FromHeader)ok1.getHeader(FromHeader.NAME)).getTag();
        //   toTag=((ToHeader)ok1.getHeader(ToHeader.NAME)).getTag();
               //log.log("TO TAG= "+toTag);
       /*    if (ok1.getHeader(ContactHeader.NAME)!=null){
               contactHeader=(ContactHeader)ok1.getHeader(ContactHeader.NAME);
               ipTo=getIp(contactHeader.getAddress().toString());
               portTo=getPort(contactHeader.getAddress().toString());
           }*/
         //  if (recordRouteHeaders==null) if (ok1.getHeader(RecordRouteHeader.NAME)!=null){
        //       recordRouteHeaders=ok1.getHeaders(RecordRouteHeader.NAME);
              //recordRouteHeader=(RecordRouteHeader)ok1.getHeader(RecordRouteHeader.NAME);
               
        //   }
  //     SystemInfo.log.debug("[LegUAC.processOk] starting");
           if (ok.getContent()!=null){
               cTypeRcv=((ContentTypeHeader)ok.getHeader(ContentTypeHeader.NAME)).getContentType();
               csubTypeRcv=((ContentTypeHeader)ok.getHeader(ContentTypeHeader.NAME)).getContentSubType();
               sdpRcv=new String(ok.getRawContent());
               call.setSdp(cType, csubType, sdp.toString(), isDestination);
           }
           else call.setSdp("", "", null, isDestination);
    //       System.out.println(" LEG UAC processOK 4");
       }catch(Exception e){e.printStackTrace();}
      
    //   System.out.println(" LEG UAC processOK 5");
       //log.log("O PORTO e "+portTo );
       //log.log("O IP e "+ipTo);
       // PCh: n existe diferenca entre ok do from e do to?
        call.setStatusCode(Call.PROCESSING_OK_TO, isDestination);
    //    System.out.println(" LEG UAC processOK 6");
   }
   
   /**
    *method used to show the call information.
    *(used generally at the end of the call)
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
  *//* 
   private void getMedia(String body,CallInformation callInf){
       String temp=body;
       int x=0;
      // if (callInf==null)log.log("CALL INFORMATION NULL!!!!!!!!!!!!!!!!! ");
       callInf.resetMediaCounter();
       log.log("Incall.getMedia("+body+");");
       /*while(temp.split("m=").length>=2){
           log.log("length="+temp.split("m=").length);
           callInf.addMediaType(temp.split("m=")[1].split(" ")[0]);
           log.log("Adding media: "+temp.split("m=")[1].split(" ")[0]);
           temp=temp.split("m=")[1];
       }
       log.log("Sem erros?");*/
      /* log.log("length-->"+(temp.split("m=").length));
       for (x=1;x<((temp.split("m=").length));x++){
           callInf.addMediaType(temp.split("m=")[x].split(" ")[0]);
           log.log("Adding media: "+temp.split("m=")[x].split(" ")[0]);
       }
    //   try{
     //  Thread.sleep(20000);
      // }catch(InterruptedException ie){log.log(""+ie);}
     /*  while(x<temp.length()){
           if (temp.charAt(x)=="m".charAt(0)&&temp.charAt(x+1)=="=".charAt(0)){
              // String h=temp.split("m=")[1].split(" ")[0];
               callInf.addMediaType(temp.split("m=")[1].split(" ")[0]);
               temp=temp.split(temp.split("m=")[1].split(" ")[0])[1];
               log.log("media:"+temp.split("m=")[1].split(" ")[0]);
               try{
                   Thread.sleep(1000);
               }catch(InterruptedException ie){log.log(""+ie);}
           }
           x++;
       }*/
       
  /* }
   public CDR getCDR(){
       return cdr;
   }
   public String getCallId(){
       return callId;
   }*/
   
   /**
    *Method used to send an ACK thru the proxy.
    */
   public void ack(){
       
       status=this.TALKING;
       /*Request ack=null;
       if (sdp.equals("")){
           ack=commFactory.createAck(invite,ok1,5060);
       }else {ack=commFactory.createAck(invite,ok1,sdp,cType,csubType,5060);}*/
       ClientTransaction ackTransaction=null;
       if (sdp.equals("")){
           //callProvider.sendAck(inviteTid);
           try{
               
           ackTransaction=commFactory.getClientTransaction(lastTransaction.createAck());
           //Request requesto=fixRouting(ok1,ackTransaction.getRequest());
           old_ack=ackTransaction.getRequest();
           ackTransaction.sendRequest();
           }catch(SipException se){se.printStackTrace();}
           
       }else {
           //callProvider.sendAck(inviteTid,sdp,cType,csubType);
           try{
           Request ack=lastTransaction.createAck();
           ackTransaction=commFactory.getClientTransaction(ack);
           //Request requesto=this.fixRouting(ok1,ackTransaction.getRequest());
           old_ack=ack;
           
           
          /* try{
           ack.setContent((Object)sdp.getBytes(),commFactory.createContentTypeHeader(cType,csubType));
           }catch(java.text.ParseException pe){pe.printStackTrace();}
          */
           
           
           // commFactory.changeDestinationPort(ack,portTo); 
           
           
           
           //log.log("ISTO FOI CHAMADO : A ENVIAR ACK CRIADO TRANSACTION... ACK ALTERNATIVO");
           ackTransaction.sendRequest();
           }catch(SipException se){se.printStackTrace();}
           //log.log("o ack a enviar:"+ackTransaction.getRequest()); 
       }
       //long tid = callProvider.sendRequest(ack);
/*       log.log("ack 1");
       Request ack=null;
            if (!sdp.equals(""))
                ack=commFactory.createAck(invite,ok1,sdp,cType,csubType,5060);
            else ack=commFactory.createAck(invite,ok1,5060); 
         
            

        ack.setRecordRouteHeaders(commFactory.createRecordRouters(info.getUA()));
        String[] routeList=new String[1];
        routeList[0]=to.split("@")[0]+"@"+ipTo;
        ack.setRouteHeaders(commFactory.createRouteHeaders(routeList,5060)); 

         long tid = callProvider.sendRequest(ack);//callProvider.sendAck(tid);
       log.log("ACK to "+to+"@"+ipTo+"\n"+ack);
       */
      // log.log("Ack sent ");
       acksent=true;
            if (restartAfterAck){
           restartAfterAck=false;
           restart();
       }
   }

  public void ackWithSDP(){
       
       status=this.TALKING;

       ClientTransaction ackTransaction=null;
       if (sdp.equals("")){
           try{
               
           ackTransaction=commFactory.getClientTransaction(lastTransaction.createAck());

           old_ack=ackTransaction.getRequest();
           ackTransaction.sendRequest();
           }catch(SipException se){se.printStackTrace();}
           
       }else {
           try{
           Request ack=lastTransaction.createAck();
           ackTransaction=commFactory.getClientTransaction(ack);
           old_ack=ack;
           try{
           ack.setContent((Object)sdp.getBytes(),commFactory.createContentTypeHeader(cType,csubType));
           }catch(java.text.ParseException pe){pe.printStackTrace();}
          
           ackTransaction.sendRequest();
           }catch(SipException se){se.printStackTrace();}
           //log.log("o ack a enviar:"+ackTransaction.getRequest()); 
       }
       acksent=true;
            if (restartAfterAck){
           restartAfterAck=false;
           restart();
       }
   }
   
   
   /**
    *Method used to send an ACK request directly to the contact.
    */
  /* public void ack_(){
       try{
       acksent=true;
       status=this.TALKING;
              //log.log("ack 1");
       Request ack=null;
       ClientTransaction ackTidx=null;

     //  log.log("\n\n\ninvite->"+invite+"ok1->"+ok1);
            if (!sdp.equals("")&&sdp!=null)
                ackTidx=commFactory.createAck(invite,ok1,sdp,cType,csubType,contactHeader);
       
            else ackTidx=commFactory.createAck(invite,ok1,contactHeader); 
        ack=ackTidx.getRequest();
        System.out.println("ack construido");
        ack.setHeader(commFactory.getRecordRouteHeader(getUA()));
        System.out.println("ack construido2");
        String route=to.split("@")[0]+"@"+ipTo;
        ack.setHeader(commFactory.getRouteHeader(route,portTo)); 
        System.out.println("ack construido3");
        //ackTidx=commFactory.getClientTransaction(ack);
        try{
            ackTidx.sendRequest();
            
            //callProvider.sendRequest(ack);
        }catch(Exception se){se.printStackTrace();}
       // long tid = callProvider.sendRequest(ack);//callProvider.sendAck(tid);
       //log.log("ACK to "+to+"@"+ipTo+"\n"+ack);
       
       //log.log("Ack sent");
       }catch(Exception e){
           //log.log(""+e+"\n... lets do things another way!");
           ack();
       }

   }*/
   /**
    *Method used to set the SDP to be used o the next messages.
    */
   public void setSdp(String cType_,String cSubType_,String sdp_){
       cType=cType_;
       csubType=cSubType_;
       sdp=sdp_;
   }
   /**
    *Method used by CallController to give instructions to this object.
    */
   public void setStatusCode(int stts){
       if (stts==this.BYE) this.hangup();
       else if (stts==this.ACKING_TO) {
           this.ack();
       }
       else if (stts==this.ACKING_TO_WITH_SDP) {
           this.ackWithSDP();
       }
       else if (stts==10){
           hangup();
       }
       
       
   } 
   /**
    *this method starts a new Invite transaction.
    */
   public void restart(){if (isDestination)SystemInfo.log.debug("to restart!   (LegUAC)");else SystemInfo.log.debug("from restart!   (LegUAC)");
       System.out.println("LegUAC.restart();");
       if (acksent){
    
       sendInviteTo();
       }else restartAfterAck=true;       
   }
   /**
    *Returns the from field of the leg.
    */
   public String getFromUri(){
       return to;
   }
   /**
    *Returns the inviteRequest that started the invite transaction.
    */
   public ClientTransaction getLastTransaction(){
       return lastTransaction;
   }
   /**
    *Returns the invite's ClientTransaction.
    */
  /* public ServerTransaction getInvTid(){
       return invite2Tid;
   }*/
   /**
    *Returns true if this object talks with the destination user agent.
    */
   public boolean isDestination(){
       return isDestination;
   }
   /**
    *Returns the CallController.
    */
   public Call getCall(){
       return call;
   }
   /**
    *Returns the current call.
    */
/*   public int getLocalCurrentCall(){
       return localCurrentCall;
   }*/
   /**
    *Method that sets the value of the user agent header that will be used on the next
    *messages.
    */
 /*  public void setUserAgent(String ua){
       userAgent=ua;
   }*/
   
 /*  public void setACKPermission(boolean prm) {
       ackPermission=prm;
   }*/
   
   public void processTimeout(Request request, Transaction transaction) {
       System.out.println("Process timeout - LegUAC");
       
       if (request.getMethod().equals(request.INVITE)){
           callEndReason="noAnswer";
                try{
                    hangup();
                }catch(Exception e){e.printStackTrace();}
                callProvider.removeLeg(callId);
                status=this.BYE;
                transaction.getDialog().delete();
                call.setStatusCode(this.NOANSWER,isDestination);
       }
   }
   public String getCallEndReason(){
       return callEndReason;
   }

 /*  public String getIP() {
       if (contactHeader!=null) return getIp(contactHeader.getAddress().getURI().toString());
       else return "";
   }*/
   
   public String getCSubType() {
       return csubTypeRcv;
   }
   
   public String getCType() {
       return cTypeRcv;
   }
   
   public String getSDP() {
       return sdpRcv;
   }
   protected String getCSubTypeEnv() {
       return csubType;
   }
   
   protected String getCTypeEnv() {
       return cType;
   }
   
   protected String getSDPEnv() {
       return sdp;
   }
   
   public Dialog getDialog(){
       return dialog;
   }
   
 /*  public int getRemoteCurrentCall() {
       return remoteCurrentCall;
   }
   */
  public Request getOldAck(){
      return old_ack;
  }
   
  public Request fixRouting(Response ok,Request in){
      Request out=in; 
      ListIterator list=ok.getHeaders(RecordRouteHeader.NAME);
      int numero=0;
      
      while(list.hasNext())   //aki poe a lista no fim, pois vai ser percorrida do fim para o principio
          list.next();
      if (list.hasPrevious()){ //aki vamos por o RequestURI com o ultimo dos RecordRoutes
          out.setRequestURI(commFactory.getURI(((RecordRouteHeader)list.previous()).toString()));
          numero++;
      }
      if (out.getHeader(RouteHeader.NAME)!=null)  //aki remove qualquer RouteHeader que exista no request
          out.removeHeader(RouteHeader.NAME);
      while (list.hasPrevious()){  //aki acrescenta todos os RouteHeaders, do fim para o principio sem contar com o ultimo
          out.addHeader(commFactory.createRouteHeader(((RecordRouteHeader)list.previous()).toString()));
          numero++;
      }
      if (ok.getHeader(ContactHeader.NAME)!=null){//aki acrescenta o contact header ao routeheader
          out.addHeader(commFactory.createRouteHeader(((ContactHeader)ok.getHeader(ContactHeader.NAME)).getAddress().toString( ))); 
      }
      System.out.println("******************");
      System.out.println("A lista tem "+numero);
      System.out.println("******************");
      return out;
      
  }
  protected ServerTransaction getInviteRcvd(){
      return inviteRcvd;
  }
  
  public void sendInfo(String type, String subType, String body) {
           try{
         Request info=dialog.createRequest(Request.INFO);
         
         info.setContent((Object)body.getBytes(),commFactory.createContentTypeHeader(type,subType));
         //log.log("the bye to be sent:"+bye);
     dialog.sendRequest(commFactory.getClientTransaction(info));
     
     }catch(Exception se){se.printStackTrace();}

      
  }  
   
}
