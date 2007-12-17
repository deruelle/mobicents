//Source file: F:\\Devel\\nist-sip\\inocrea\\Comm3pcc.java
package com.ptin.xmas.control.common;


import com.ptin.xmas.control.common.interfaces.CallControllerListener;
import com.ptin.xmas.control.common.interfaces.CallController;
import com.ptin.xmas.control.common.CommProviderImpl;
import com.ptin.xmas.control.common.interfaces.CallProvider;
import com.ptin.xmas.control.common.interfaces.Call;
import com.ptin.xmas.control.common.interfaces.CallListener;
import com.ptin.xmas.control.common.interfaces.InCallController;
import com.ptin.xmas.control.common.interfaces.Leg;
import javax.sdp.*;
import com.ptin.xmas.control.exceptions.*;
//import inocrea.data.CallData;
//import inocrea.system.SystemInfo;
//import inocrea.mail.EmailwithVCard;


public class Comm3pcc implements CallListener, InCallController, Call, CallController{
//  protected ExceptionManager exceptionManager=null;
  static int  STARTING = 0;
  static int  PROCESSING_INVITE_FROM = 1;
  static int  INVITING_TO = 2;
  static int  PROCESSING_OK_TO = 3;
  static int  RESPONDING_OK_FROM = 4;
  static int  ACKING = 5;
  static int  TALKING = 6;
  static int  BYE = 7;
  static int  HOLD =8;
  static int  WAIT =9;
  static int  REJECT =10;
  static int  BUSY =12;
  static int  NETWORKUNAVAILABLE =13;
  static int  NOANSWER =14;
  static int  UNAVAILABLE =15;
  static int  RINGING=11;
  final int ASTERISK=20;
  private String to1 = "";
  private String to2 = "";
  private String finalTo="";
  private String app = "";
  private String proxyserv = "";
  private String ipFrom="";
  private String ipTo="";
  SystemInfo info=null;
  private Leg toLeg=null;
  private Leg fromLeg=null;
  /*private UserManager userManager=null;
  private User userTo=null;*/
  private CallProvider callProvider=null;
  private String callIdTo="";
  private String callIdFrom="";
  private int status=0;
  private boolean forwarded=false;
  private boolean statusSet=false;           
 // private String cType,csubType,sdp="";
  private String sourceUri_,destinationUri_,finalToUri_,domain_="";
  private boolean done=false;
  private int estate=0;
  private boolean initTimeNotSet=true;
  private boolean endTimeNotSet=true;
  private boolean threadNotLaunched=true;
  private boolean fromIsUAC=true;
  private boolean toIsUAC=true;
  private String mediaSDP="";
  private String realFrom="";
  //private Credit credit=null;
  //private Manager manager=null;
  private String payUser=null;
  private boolean mailsent=false;
  private CallControllerListener listener = null;
  private int controlLevel = 0;  
  private String termReason="failure";
  private String creditedUser="";
  private String from = null;
  private String to = null;
 // private boolean asterisk=false;
  
  private String cTypeFrom="";
  private String cSubTypeFrom="";
  private String sdpFrom="";
 // private String cTypeFromLast="";
 // private String cSubTypeFromLast="";
 // private String sdpFromLast="";  
  private String cTypeTo="";
  private String cSubTypeTo="";
  private String sdpTo="";  
 // private String cTypeToLast="";
 // private String cSubTypeToLast="";
 // private String sdpToLast="";  
  
  private boolean inexCC_Enabled=false;
  
  private String fromName = "";
  private String toName = "";
  
  private int flow = 2;
   /**
    * @roseuid 3D6C04470012
    */
  /**
   *esta class apenas serve de interface para controlo de uma chamada "C2D".
   */
   public Comm3pcc(String owner, CallControllerListener listener_, String from_, String fromName_, String to_, String callId, int controlLevel_, int flow_)  
   {
       SystemInfo.log.debug("[comm3pcc] start from "+from_+" to "+to_);
       listener = listener_;
       fromName = fromName_;

      
       try{
           
       //info = SystemInfo.getReference("incom");
         }catch(Exception e){e.printStackTrace();};
         
       callIdFrom=callId;
       proxyserv=info.getProxy();
       app=owner+"@"+info.getIp();
 
       controlLevel = controlLevel_;
       flow = flow_;
       /*
       callInf=new CallInformation();
       callInf.setFrom(a);
       callInf.setTo(b);
       callInf.setStartAccessTime(System.currentTimeMillis());
       callDataRecord.setStartAccessTime(System.currentTimeMillis());*/
       from = from_;
       to = to_;
       to1=getURI(from);
       to2=getURI(to);

       SystemInfo.log.debug("[comm3pcc] creating inexCall ");
       
       
       //else hangup();
   }
   
   /**
    *metodo chamado para por fim a chamada
    */ 
   
   public String getAppName(){
      return app; 
   }
   
   public void hangup(){
       SystemInfo.log.debug("hangup recebido de fora");
       SystemInfo.log.debug(" *** ENTROU NO HANGUP 3PCC ");
      if (toLeg!=null)
          toLeg.hangup();
       
      fromLeg.hangup();
  /*      if (endTimeNotSet){
                       callDataRecord.setCallCompletionReason("3PCC");
           if (toLeg.isDestination())
           termReason=toLeg.getCallEndReason();
           else termReason=fromLeg.getCallEndReason();
           endTimeNotSet=false;
           long currentTime=System.currentTimeMillis();
           callDataRecord.setEndTime(currentTime);
           callInf.setStopTime(currentTime);
           callInf.setStopAccessTime(currentTime);
           setAllIPDRFinalData();
           if (initTimeNotSet) callDataRecord.setInitTime(currentTime);
           credit.callEnded();
           try{Thread.sleep(1500);}catch(InterruptedException ie){log.log(""+ie);}
           CreditRcv creditRcv=new CreditRcv(callDataRecord);
           if (!mailsent){
               try{Thread.sleep(1500);}catch(InterruptedException ie){log.log(""+ie);}
           EmailwithVCard emailwithVCard = new EmailwithVCard(this.getFrom(),this.getTo(),callDataRecord,EmailwithVCard.received);
           mailsent=true;
           }
        //   callDataRecord.showData(); 
        }
        try{  manager.removeCall(this.getCallId());}catch(Exception e ){log.log(""+e);}
  */ }

   /**
    *este metodo é chamado após todas as variaveis estarem prontas.
    *dá inicio à chamada.
    *a partir daqui o processo de sinalização da chamada é processado pela class call, e das suas 2 legs
    */
   private void processInviting() {
       status=1;
       
       SystemInfo.log.debug("Process Invinting "+to2+" "+to1);
       
     //String from_, String to_, Call call_, String callId_, String cType_, String csubType_, String sdp_, boolean isDestination_, String realFrom_, String realTo_, String appName
     //pch: meter neste construtor o fromName a usar como displayname da app: é o 1º parametro?
       SdpFactory sdpFact = null;
       SessionDescription ssDesc = null;
       try{
       sdpFact = SdpFactory.getInstance(); 
	   ssDesc = sdpFact.createSessionDescription();
	   ssDesc.setOrigin(sdpFact.createOrigin("-",0,0,"IN","IP4",info.getIp()));
       }catch(Exception ex){SystemInfo.log.warn(ex.getMessage());};
       
       String staticSDP;
       if(ssDesc != null && flow == 2)
       {
    	   staticSDP = ssDesc.toString();
       }
       else if(flow == 4)staticSDP = this.getStaticHeldSDPBlackHoled();
       else staticSDP = this.getStaticHeldSDPNoMedia();
       
       fromLeg=new LegUAC(app,fromName,to1,this,callIdFrom,"application","sdp",staticSDP/*""*//*getStaticHeldSDPNoMedia()*/,false,"b2b"/*app*/,to1,app, CallData.OUTCOMM); 
       /*log.log("Comm3pcc.processInviting() A CRIAR LEGUAC  (FROM) ");*/
       callProvider=CommProviderImpl.getReference();
       //callIdTo=callProvider.getNewCallId();
       callProvider.addOutComm(callIdFrom,this);
   }
   /**
    *este método serve para direccionar a chamada
    *ATENCAO: Este método deve de ser chamado, mesmo que a chamada vá parar ao
    *numero original
    */
   public void forward(String sourceUri, String destinationUri, String finalToUri, String domain){
       if (forwarded==false){
    //       SystemInfo.log.debug("1");
            sourceUri_=sourceUri;
            
            destinationUri_=destinationUri;
            finalToUri_=finalToUri;
            domain_=domain;
            finalTo=finalToUri;
            forwarded=true;
            realFrom=app;
  //          SystemInfo.log.debug("2");
            /*if (manager==null)
                manager=inocrea.manager.ManagerImpl.getReference();
            manager.addTalk(sourceUri,finalToUri);*/
            SystemInfo.log.debug("3");
       }
       //log.log("Comm3pcc FORWARD RECEIVED!!! ");
//       SystemInfo.log.debug("4");
       //if (estate==2) processInvitingTo();
       processInvitingTo();
//       SystemInfo.log.debug("5");
   }
   public String getToName(){
       return toName;
   }
   public String getFromName(){
       return fromName;
   }
   public String getTo(){
       return to2;
   }
   public String getFrom(){
       return to1;
   }
   public String getFinalTo(){
       return finalTo;
   }
   /**
    *este método retorna o objecto User desta chamada
    *serve para controlo na parte WEB
    */
  /* public User getUser(){
       return userTo;
   }*/
   /**
    *este é método chamado para enviar a mensagem 180 Ringing para o UA chamador
    *este método deve de ser chamado na interface CallControl 
    */
   public void processRinging(){
       
       try{
           if (status==PROCESSING_INVITE_FROM)
               listener.processEvent(CallController.FROM_RINGING);
           else
                listener.processEvent(CallController.TO_RINGING);
       }catch(CallException ce){ce.printStackTrace();}
       
       fromLeg.setStatusCode(11);
   }
   /**
    *este é o método chamado para dar inicio a chamada
    */

   /**
    *este método devolve uma sipURL de um numero, nome, nome@dominio... etc
    *ex:é introduzido 112, ou sip:112
    *ele devolve 112@inocrea.org nos dois casos (caso o dominio onde se regista a aplicaçao seja inocrea.org )
    *se fosse introduzido 112@172.29.250.57 ele manteria o dominio
    */ 
   private String getURI(String entry){
       String uri="";
       if (hasChar(entry,":")){
           if (entry.split(":").length>=3){
               uri=entry.split(":")[1].split(":")[0];
           }else{ if (entry.split(":")[0].equalsIgnoreCase("sip")){
                    uri=entry.split(":")[1];
                  }else uri=entry.split(":")[0];
                }
       }else uri=entry;
       if (!hasChar(uri,"@")){
           uri=uri+"@"+proxyserv;
       }
       return uri;
   }
   /**
    *metodo criado para retornar um ip de um "CONTACT HEADER"
    *no formato String
    */
   private String getIp(String contact){
      String ip="";
        if(hasChar(contact,"@")){
            ip=contact.split("@")[1];
        }else ip=contact.split("<sip:")[1];
        if (hasChar(ip,":")){
            ip=ip.split(":")[0];
        }else if (hasChar(ip,";")){
            ip=ip.split(";")[0];
        }else if (hasChar(ip,">")){
            ip=ip.split(">")[0];
        }
        return ip;
   }
   /**
    *método criado para verificar se uma determinada String contém um determinado carater
    */
   private boolean hasChar(String text,String chr){
       boolean has=false;
       int counter=0;
            while(counter<text.length()){
                if (text.charAt(counter)==chr.charAt(0)) has=true;
                counter++;
            }
       return has;
   }
  
   public String getCallId(){
       return callIdFrom;
   }
// cada vez que uma leg pertencente a esta call tem que passar alguma informação para a outra leg,
// é este método que deve de ser chamado. Apenas se passa o estado em que deve de estar a chamada e se a leg é a do destino, ou a de origem
   public void setStatusCode(int x,boolean isDestination){
       SystemInfo.log.debug("[Comm3pcc.setStatusCode] - X code " + x);
       SystemInfo.log.debug("[Comm3pcc.setStatusCode] status= "+status);
       
/*       if (inexCallController== null)
           SystemInfo.log.fatal("[com3pcc.setStatusCode] INEXCALL IS NULL!! ");
  */     
       if (!isDestination){//fromLEG!
           SystemInfo.log.debug("[Comm3pcc.setStatusCode]: code "+x+','+" from)");
           if (x==Comm3pcc.PROCESSING_OK_TO){//recebeu 200 OK
               {//envia ack com sdp on hold e envia invite para to com sdp from

                   if (status==1){
                       try{
                        listener.processEvent(CallController.fromConnected);
                        }catch(CallException ce){ce.printStackTrace();}
                       
                       cTypeFrom=fromLeg.getCType();
                       cSubTypeFrom=fromLeg.getCSubType();
                       
                       if(flow == 2)
                       sdpFrom=fromLeg.getSDP();
                       else if(flow == 4)
                        sdpFrom="";

                       fromLeg.setSdp(cTypeFrom,cSubTypeFrom, this.createHeldSDP(sdpFrom));
                       /******/
                       String domain;
                       try{
                        domain=getTo().split("@")[1];
                       }catch(Exception e){domain=info.getProxy();}
                       
                       if(flow == 4)
                        fromLeg.setStatusCode(Leg.ACKING_TO);
                       
                       this.forward(getFrom(), getTo(), getTo(), domain);
                   }else {
                       if (!fromLeg.getSDP().equals("")){
                            cTypeFrom=fromLeg.getCType();
                            cSubTypeFrom=fromLeg.getCSubType();
                            sdpFrom=fromLeg.getSDP();
                            
                            if (flow == 4)//changes to flow IV - we can just send ack to second leg after receiving 200 ok from the restart of from leg
                            {
                            	toLeg.setSdp(cTypeFrom,cSubTypeFrom,sdpFrom);
                             toLeg.setStatusCode(Leg.ACKING_TO_WITH_SDP);
                            }
                       }
                       fromLeg.setSdp(cTypeTo,cSubTypeTo, sdpTo);
                       if(flow == 4)
                    	   fromLeg.setStatusCode(Leg.ACKING_TO);
                   }
                   if(flow == 2)
                    fromLeg.setStatusCode(Leg.ACKING_TO);
                   
               }
           }else if (x==Comm3pcc.ACKING){//recebeu ACK
               
           }else if (x==Comm3pcc.PROCESSING_INVITE_FROM){//recebeu re-INVITE
               //guarda SDP do FROM!; LegFrom=LegUAS; envia 200 OK com SDP on-hold
                   
                   if (status!=1){
                       if(!fromLeg.getSDP().equals("")){
                            cTypeFrom=fromLeg.getCType();
                            cSubTypeFrom=fromLeg.getCSubType();
                            sdpFrom=fromLeg.getSDP();
                       }
                       fromLeg.setSdp(cTypeTo,cSubTypeTo,sdpTo);
                       toLeg.setSdp(cTypeFrom,cSubTypeFrom,sdpFrom);
                       processNewInviteRcvd(false);
                   }else{
                       cTypeFrom=fromLeg.getCType();
                       cSubTypeFrom=fromLeg.getCSubType();
                       sdpFrom=fromLeg.getSDP();
                       if (fromIsUAC){
                            fromIsUAC=false;
                            LegUAS newLeg=new LegUAS((LegUAC)fromLeg, CallData.OUTCOMM);
                            fromLeg=newLeg;
                       }
                       fromLeg.setSdp(cTypeFrom,cSubTypeFrom,createHeldSDP(sdpFrom));
                       fromLeg.setStatusCode(Leg.PROCESSING_OK_TO);
                   }
           }else if(x==Comm3pcc.BYE){//from bye recvd
               toLeg.setStatusCode(Leg.BYE);
               try{
                listener.processEvent(CallController.fromDisconnected);
               }catch(CallException ce){ce.printStackTrace();}
           }else if(x==Comm3pcc.UNAVAILABLE){//from unavailable recvd
               try{
                toLeg.setStatusCode(Leg.BYE);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.fromUnavailable);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==Comm3pcc.RINGING){//from ringing rcvd
               try{
                listener.processEvent(CallController.FROM_RINGING);
               }catch(Exception e){e.printStackTrace();}
           }else if (x==Comm3pcc.BUSY){
               try{
                toLeg.setStatusCode(Leg.BYE);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.fromBusy);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==Comm3pcc.REJECT){
                try{
                toLeg.setStatusCode(Leg.BYE);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.fromRejected);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==Comm3pcc.NETWORKUNAVAILABLE){
                try{
                toLeg.setStatusCode(Leg.BYE);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.fromNetworkUnavailable);
               }catch(CallException ce){ce.printStackTrace();}
           }
       }else{//toLeg
           SystemInfo.log.debug("setStatusCode("+x+','+"to)");
           if (x==Comm3pcc.PROCESSING_OK_TO){//recebeu 200 OK
               
               try{
                listener.processEvent(CallController.TO_CONNECTED);
               }catch(CallException ce){ce.printStackTrace();}

               //envia ACK sdp to
                   
                   if (!toLeg.getSDP().equals("")){
                        cTypeTo=toLeg.getCType();
                        cSubTypeTo=toLeg.getCSubType();
                        sdpTo=toLeg.getSDP();
                   }
                   if (status==1){
                       status=2;
                       if (!fromIsUAC){
                            fromIsUAC=true;
                            fromLeg.setSdp(cTypeTo,cSubTypeTo,sdpTo);
                            Leg temp=new LegUAC((LegUAS)fromLeg, CallData.OUTCOMM);
                            fromLeg=temp;
                       }else {
                    	   if(flow == 4)
                    	   {
                    		   //we have to change the sdp received in 200 ok and then send it in second invite of first leg
                    		   try{
                    			   SdpFactory sdpFact = SdpFactory.getInstance(); 
                    			   SessionDescription ssDesc = sdpFact.createSessionDescription(sdpTo);
                    			   ssDesc.setOrigin(sdpFact.createOrigin("-",0,1,"IN","IP4",info.getIp()));
                    			   sdpTo = ssDesc.toString();
                    		   	}catch(Exception ex){SystemInfo.log.warn(ex.getMessage());};
                       	   }
                            fromLeg.setSdp(cTypeTo,cSubTypeTo,sdpTo);
                            
                            fromLeg.restart();
                       }
                   }
                   toLeg.setSdp(cTypeFrom,cSubTypeFrom,sdpFrom);
                   
                   //changes to flow IV - we can just send ack to second leg after receiving 200 ok from the restart of from leg
                   if(flow == 2)
                   toLeg.setStatusCode(5);
                   
           }else if (x==Comm3pcc.ACKING){//recebeu ACK
                
           }else if (x==Comm3pcc.PROCESSING_INVITE_FROM){//recebeu re-INVITE
               //guardaSDP TO!; toLeg->LegUAS; envia 200 OK sdp on-hold
               if (!toLeg.getSDP().equals("")){
                    cTypeTo=toLeg.getCType();
                    cSubTypeTo=toLeg.getCSubType();
                    sdpTo=toLeg.getSDP();
               }
               fromLeg.setSdp(cTypeTo,cSubTypeTo,sdpTo);
               toLeg.setSdp(cTypeFrom,cSubTypeFrom,sdpFrom);
               processNewInviteRcvd(true);
           }else if(x==Comm3pcc.BYE){
               fromLeg.setStatusCode(Leg.BYE);
               try{
                listener.processEvent(CallController.toDisconnected);
               }catch(CallException ce){ce.printStackTrace();}
           }else if(x==Comm3pcc.UNAVAILABLE){//from unavailable recvd
               try{
                fromLeg.setStatusCode(Leg.BYE);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.toUnavailable);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==Comm3pcc.RINGING){//from ringing rcvd
               try{
                listener.processEvent(CallController.TO_RINGING);
               }catch(Exception e){e.printStackTrace();}
           }else if (x==Comm3pcc.BUSY){
               try{
                fromLeg.setStatusCode(Leg.BYE);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.toBusy);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==Comm3pcc.REJECT){
                try{
                fromLeg.setStatusCode(Leg.BYE);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.toRejected);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==Comm3pcc.NETWORKUNAVAILABLE){
                try{
                fromLeg.setStatusCode(Leg.BYE);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.toNetworkUnavailable);
               }catch(CallException ce){ce.printStackTrace();}
           }
       }
       
/*       if (inexCallController== null)
           SystemInfo.log.fatal("[com3pcc.setStatusCode] INEXCALL IS NULL!! ");
*/
   }
       
   public void setSdp(String ctype_, String csubtype_, String sdp_, boolean isDestination){
      /*  if (!isDestination) {
            cTypeFrom=ctype_;
            cSubTypeFrom=csubtype_;
            sdpFrom=new String(sdp_);
        }else{
            cTypeTo=ctype_;
            cSubTypeTo=csubtype_;
            sdpTo=new String(sdp_);
        }*/
   }

   private void processInvitingTo(){
       SystemInfo.log.debug("processInvitingTo()");
       SystemInfo.log.debug("sourceUri_ "+sourceUri_);
       SystemInfo.log.debug("finalToUri_ "+finalToUri_);
       SystemInfo.log.debug("domain_ "+domain_);
       SystemInfo.log.debug("realFrom "+realFrom);
       SystemInfo.log.debug("destinationUri_ "+destinationUri_);
       if (callIdTo.equals("")){
       callIdTo=callProvider.getNewCallIdHeader().getCallId();
       //String from_, String to_, Call call_, String callId_, String cType_, String csubType_, String sdp_, boolean isDestination_, String realFrom_, String realTo_, String appName
       destinationUri_=destinationUri_.split("@")[0]+'@'+domain_;
       toLeg=new LegUAC(from,fromName, finalToUri_.split("@")[0]+"@"+domain_,this,callIdTo, cTypeFrom,cSubTypeFrom,sdpFrom,true,app,destinationUri_,app, CallData.OUTCOMM);
       }
   }
   /**
    *este é o método chamado para que ao fim de um minuto (60000 milisegundos) a chamada seja reencaminhada para o voicemail (caso nao tenha sido reencaminhada antes)
    */
      private void processNotify(){
     //  if (userTo.processInComm(to1,"blablabla",this,callIdFrom)!=this.WAIT){
      // }
       SystemInfo.log.debug("**** Entrou no process notify do Comm3pcc\n\n");   
       
       Thread timeOut=new Thread(){
                public void run(){
                    try{
                        
                       if (listener == null)
                         Thread.sleep(3000);
                    }catch(Exception e) {e.printStackTrace();};  
                    
                    try{
                    if (controlLevel == CallController.basicControl)
                      listener.processEvent(CallController.fromConnected);
                    else 
                       {
                         String domain;
                        try{
                            domain = to.split("@")[1];
                        }
                        catch(ArrayIndexOutOfBoundsException exc){ 
                            to = to+"@"+info.getProxy();
                            domain =  info.getProxy(); }
                        forward(from,to,to,domain); 
                       }
                    }catch(Exception e){e.printStackTrace();};
                    //listener.processNotify();
                   
                    }
                };
      
        timeOut.start();
   }
      /**
      *este método é usado para o processamento de um novo invite, pois pode implicar que uma LegUAS passe para LegUAC,
      *ou vice-versa!
      */
      private void processNewInviteRcvd(boolean isDestination){
          
          SystemInfo.log.debug("processNewInviteRcvd");
         // listener.newInviteReceived();
          
          //initTimeNotSet=true;
          
          if (!isDestination){
              SystemInfo.log.debug("!isDestination");
              if (fromIsUAC){  // caso o Invite venha da origem e esta se comportar como UAC, vai ter que passar a ser um UAS
                  Leg tempLeg=new LegUAS((LegUAC)fromLeg,CallData.OUTCOMM);
                  fromIsUAC=false;
                  fromLeg=tempLeg;
              }
                  if (toIsUAC){
                      SystemInfo.log.debug("toLeg.restart();");
                      toLeg.restart();
                  }else {
                      toLeg.restart();
                      toIsUAC=true;
                      SystemInfo.log.debug("Leg tempLeg=new LegUAC((LegUAS)toLeg);");
                      
                      //Leg tempLeg=new LegUAC((LegUAS)toLeg);
                      //toLeg=tempLeg;
                  }
              fromLeg.setStatusCode(3);
          }else{SystemInfo.log.debug("isDestination");
              if (toIsUAC){
                  toIsUAC=false;
                  Leg tempLeg=new LegUAS((LegUAC)toLeg,CallData.OUTCOMM);
                  toLeg=tempLeg;
              }
              if (fromIsUAC){
                  SystemInfo.log.debug("fromLeg.restart(); before");
                      fromLeg.restart();
                      SystemInfo.log.debug("fromLeg.restart(); after");
              }else {
                      SystemInfo.log.debug("Leg tempLeg=new LegUAC((LegUAS)fromLeg);");
                      fromLeg.restart(); 
                      fromIsUAC=true;
                    //  Leg tempLeg=new LegUAC((LegUAS)fromLeg);
                    //  fromLeg=tempLeg;
              }
              toLeg.setStatusCode(3);
          }
          
      }
      /**
       *esta funçao faz com que no SDP,o porto a receber dados passe a ser o porto 0, ou seja, é um MUTE
       */
      private String createHeldSDP(String sdp){
          return getStaticHeldSDP();
       /*   String held="";
         // StringBuffer buf=new StringBuffer();
          int a=0;
          while (a<sdp.split("\n").length){
            if (sdp.split("\n")[a].charAt(0)=="c".charAt(0)){
               // buf.append(held.split("\n")[a]);
                held=held+"c=IN IP4 0.0.0.0";
            }else{
                held=held+sdp.split("\n")[a];
            }
            held=held+"\n";
            a++;
          }
          
          return held;*/
          
      }
      
      /**
       *este método é usado pelas legs para passar o useragentHeader que recebem de uma para a outra
       */
    /*     public void setUA(String ua,boolean isDestination_){
       if (isDestination_) fromLeg.setUserAgent(ua);
       else toLeg.setUserAgent(ua);
       
   }*/
         
  
         
         /**
          *este método passa o media de uma mensagem para a CallInformation
          */
   /*           private void getMedia(String body,CallInformation callInf){
         
         log.log(" O SDP É !!!! ......\n"+body);
       String temp=body;
       int x=1;
       callInf.resetMediaCounter();
       log.log("Incall.getMedia("+body+");");
       while(temp.split("m=").length>=2 && x<temp.split("m=").length){
           log.log("length="+temp.split("m=").length);
           if (!temp.split(temp.split("m=")[x].split(" ")[0]+" ")[1].split(" ")[0].equals("0")){
                callInf.addMediaType(temp.split("m=")[x].split(" ")[0]);
                log.log("Adding media: "+temp.split("m=")[x].split(" ")[0]);
           }
           x++;
       }
   }
      */   
  
   
   
   
/*   public Credit getCredit(){
       return credit;
   }*/
   
 
   public String getPayer(){
       return getURI(payUser);
   }
   /**
    *este é o método chamado quando uma chamada é rejeitada
    */
   public void reject() {
       if(!mailsent){
           mailsent=true;  
           fromLeg.setStatusCode(this.REJECT);
           if (toLeg!=null) {if (toLeg.isDestination())
            termReason=toLeg.getCallEndReason();
           }
           else termReason=fromLeg.getCallEndReason();
          //setAllIPDRFinalData();
           try{Thread.sleep(1500);}catch(InterruptedException ie){ie.printStackTrace();}
          // EmailwithVCard emailwithVCard = new EmailwithVCard(this.getFrom(),this.getTo(),callDataRecord,EmailwithVCard.rejected);
       }
   }
   public boolean isGone(){
       return !endTimeNotSet;
   }
   
   private String getCreditedUser(){
       return creditedUser;
   }
   
   public void setCallControllerListener(CallControllerListener listener_) {
       listener = listener_;
   }
   
   public void setControlLevel(int controlLevel_) {
       controlLevel = controlLevel_;
   }
   //pch: esta função n deverá ser chamada para comm3pdd: mandar excepção?
   
   public void connectTo(String to, String body) {
       forward(getFrom(),to,to,to.split("@")[1]);
 
   }
   
   public void restartMe(boolean isDestination) {
       if (!isDestination){
           fromIsUAC=true;
        Leg tempLeg=new LegUAC((LegUAS)fromLeg, CallData.OUTCOMM);
        fromLeg=tempLeg;
       }else{
           fromIsUAC=true;
        Leg tempLeg=new LegUAC((LegUAS)toLeg, CallData.OUTCOMM);
        toLeg=tempLeg;   
       }
   }  

   public String getCallerPicture() {
       return null;
   }
   
   public void resumeCall() {
   }
   
   public void start() {
       processInviting();
   }
   
   public String getStaticHeldSDP(){
    String sdp="v=0\r\n"+
    "o=- 0 0 IN IP4 "+info.getIp()+"\r\n"+
    "s=session\r\n"+
    "c=IN IP4 0.0.0.0\r\n"+
    "b=CT:1000\r\n"+
    "t=0 0\r\n"+
    "m=audio 13886 RTP/AVP 97 111 112 6 0 8 4 5 3 101\r\n"+
    "k=base64:jn/f5gSe8P8wJBCJT7XQqQx+i2kwacNuCfEOyigr3x0\r\n"+
    "a=rtpmap:97 red/8000\r\n"+
    "a=rtpmap:111 SIREN/16000\r\n"+
    "a=fmtp:111 bitrate=16000\r\n"+
    "a=rtpmap:112 G7221/16000\r\n"+
    "a=fmtp:112 bitrate=24000\r\n"+
    "a=rtpmap:6 DVI4/16000\r\n"+
    "a=rtpmap:0 PCMU/8000\r\n"+
    "a=rtpmap:8 PCMA/8000\r\n"+
    "a=rtpmap:4 G723/8000\r\n"+
    "a=rtpmap:5 DVI4/8000\r\n"+
    "a=rtpmap:3 GSM/8000\r\n"+
    "a=rtpmap:101 telephone-event/8000\r\n"+
    "a=fmtp:101 0-16\r\n";
    return sdp;
  }
  
   public String getStaticHeldSDPBlackHoled(){
	    String sdp="v=0\r\n"+
	    "o=- 0 0 IN IP4 "+info.getIp()+"\r\n"+
	    "s=session\r\n"+
	    "c=IN IP4 0.0.0.0\r\n"+
	    "b=CT:1000\r\n"+
	    "t=0 0\r\n"+
	    "m=audio 13886 RTP/AVP 3\r\n"+
	    "k=base64:jn/f5gSe8P8wJBCJT7XQqQx+i2kwacNuCfEOyigr3x0\r\n"+
	    "a=rtpmap:3 GSM/8000\r\n";
	    return sdp;
	  }
   
   public String getStaticHeldSDPNoMedia(){
	   String sdp="v=0\r\n"+
	    "o=- 0 0 IN IP4 "+info.getIp()+"\r\n"+
	    "s=session\r\n"+
	    "c=IN IP4 "+info.getIp()+"\r\n"+
	    "b=CT:1000\r\n"+
	    "t=0 0\r\n";
	    //"m=audio 13886 RTP/AVP 97 111 112 6 0 8 4 5 3 101\r\n"+
	    /*"k=base64:jn/f5gSe8P8wJBCJT7XQqQx+i2kwacNuCfEOyigr3x0\r\n"+
	    "a=rtpmap:97 red/8000\r\n"+
	    "a=rtpmap:111 SIREN/16000\r\n"+
	    "a=fmtp:111 bitrate=16000\r\n"+
	    "a=rtpmap:112 G7221/16000\r\n"+
	    "a=fmtp:112 bitrate=24000\r\n"+
	    "a=rtpmap:6 DVI4/16000\r\n"+
	    "a=rtpmap:0 PCMU/8000\r\n"+
	    "a=rtpmap:8 PCMA/8000\r\n"+
	    "a=rtpmap:4 G723/8000\r\n"+
	    "a=rtpmap:5 DVI4/8000\r\n"+
	    "a=rtpmap:3 GSM/8000\r\n"+
	    "a=rtpmap:101 telephone-event/8000\r\n"+
	    "a=fmtp:101 0-16\r\n";*/
	    return sdp;
  } 
  
  public void setSdpFrom(String sdp) {
  }
  
  public void setSdpTo(String sdp) {
  }
  
  public void setStatus(int status) {
  }
  
  public String getSdpFrom() {
      return sdpFrom;
  }
  
  
  public String getSdpTo() {
      return sdpTo;
  }
  
  public void ack() {
  }
  
  public void ringing() {
  }
  
}
