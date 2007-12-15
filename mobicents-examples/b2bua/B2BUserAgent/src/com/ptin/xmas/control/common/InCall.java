/** Java class "InCall.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.ptin.xmas.control.common;

import com.ptin.xmas.control.common.interfaces.CallControllerListener;
import com.ptin.xmas.control.common.interfaces.CallController;
import com.ptin.xmas.control.common.CommProviderImpl;
import com.ptin.xmas.control.common.interfaces.CallProvider;
import com.ptin.xmas.control.common.interfaces.Call;
import com.ptin.xmas.control.common.interfaces.CallListener;
import com.ptin.xmas.control.common.interfaces.InCallController;
import com.ptin.xmas.control.common.interfaces.Leg;
import com.ptin.xmas.control.common.interfaces.CallManager;
import com.ptin.xmas.control.common.CallData;
import com.ptin.xmas.control.common.SystemInfo;
import com.ptin.xmas.control.exceptions.*;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 * <p>
 * Class that is used for logic control of the call (2 legs).
 * </p>
 * </p>
 */

public class InCall implements Serializable, Call, CallListener, InCallController, CallController {

  ///////////////////////////////////////
  // attributes


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
    static int PROCESSING_INVITE_FROM = 1; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int INVITING_TO = 2; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int PROCESSING_OK_TO = 3; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int RESPONDING_OK_FROM = 4; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int ACKING = 5; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int TALKING = 6; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int BYE = 7; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int HOLD = 8; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int WAIT = 9; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int REJECT = 10; 
  static int  BUSY =12;
  static int  NETWORKUNAVAILABLE =13;
  static int  NOANSWER =14;
  static int  UNAVAILABLE =15;

/**
 * <p>
 * Represents ...
 * </p>
 */
    static int RINGING = 11; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private int status = 0; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String to1 = ""; 

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
    private String finalTo = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String app = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String proxyserv = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String ipFrom = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String ipTo = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private CallProvider callProvider = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String callIdTo = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String callIdFrom = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private boolean forwarded = false; 
    
    
    private int retriesNumber = 5;

/**
 * <p>
 * Represents ...
 * </p>
 */
    private boolean statusSet = false; 

  private String cTypeFrom="";
  private String cSubTypeFrom="";
  private String sdpFrom="";

  private String cTypeTo="";
  private String cSubTypeTo="";
  private String sdpTo="";  

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String sourceUri_; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String destinationUri_; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String finalToUri_; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String domain_ = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private boolean initTimeNotSet = true; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private boolean endTimeNotSet = true; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private boolean fromIsUAC = false; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private boolean toIsUAC = true; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private boolean mailsent = false; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String userAgent = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String mediaSDP = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private CallControllerListener listener = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String creditReceiver = ""; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private String termReason = "NoAnswer"; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private int controlLevel = 0; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    SystemInfo info = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private Leg toLeg = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private Leg fromLeg = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    private CallManager manager = null; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    
    private String fromName = "";
    private String toName = "";
    
    private boolean isInocreaCall = false;
    
  ///////////////////////////////////////
  // operations


/**
 * <p>
 * esta class apenas serve de interface para controlo de uma chamada Incom.
 * </p>
 * </p>
 */
   public String getAppName(){
    return app.split("@")[0];
   }
    
   public InCall(Request inviteRcvd, String callId, ServerTransaction tid_,
            CallManager manager_) {

        // log=Logx.getReference();
        SystemInfo.log.debug("InCall constructor");

        Properties p = null;
        try {
            FileInputStream propFile = new FileInputStream("incom.properties");

            p = new Properties(System.getProperties());
            p.load(propFile);
            propFile.close();
        } catch (Exception e) {/*
                                * log.error("Not able to read
                                * incom.properties");
                                */
        }
        ;
        retriesNumber = new Integer((String) p.getProperty("retriesNumber", "5")).intValue();

        manager = manager_;
        try {
            info = SystemInfo.getReference("incom");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;

        app = inviteRcvd.getRequestURI().toString().split(":")[1]; //info.getUA();
        callIdFrom = callId;
        proxyserv = info.getProxy();
        String tmp = ((FromHeader) inviteRcvd.getHeader(FromHeader.NAME))
                .getAddress().toString();
        SystemInfo.log.debug("[InCall] fromAddress " + tmp);

        to1 = getURI(tmp);
        if (tmp.startsWith("\""))
            fromName = tmp.split("\"")[1].split("\"")[0];

        tmp = ((ToHeader) inviteRcvd.getHeader(ToHeader.NAME)).getAddress()
                .toString();
        to2 = getURI(tmp);
        if (tmp.startsWith("\""))
            toName = tmp.split("\"")[1].split("\"")[0];

        SystemInfo.log.debug("[InCall] fromName " + fromName + " toName"
                + toName);
    } // end InCall        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
   public void processInviteFrom(Request invite, ServerTransaction tid) {
        try {

            cTypeFrom = ((ContentTypeHeader) invite
                    .getHeader(ContentTypeHeader.NAME)).getContentType();//cType=invite.getContentTypeHeader().getContentType();
            cSubTypeFrom = ((ContentTypeHeader) invite
                    .getHeader(ContentTypeHeader.NAME)).getContentSubType();
            sdpFrom = new String(invite.getRawContent());//getBodyAsString();
            callProvider = CommProviderImpl.getReference();
            callProvider.addInCall(callIdFrom, this);
            if ((invite.getHeader(UserAgentHeader.NAME)) != null) {
                userAgent = (String) ((UserAgentHeader) invite
                        .getHeader(UserAgentHeader.NAME)).getProduct().next();
            }
            SystemInfo.log
                    .debug("Incall.processInviteFrom() A Instanciar LegUAS (From) ");
            SystemInfo.log.debug(" to1 " + to1 + " to2 " + to2 + " callIdFrom "
                    + callIdFrom);
            /*
             * callDataRecord.setFrom(to1); callDataRecord.setTo(to2);
             * callDataRecord.setCallId(callIdFrom); callDataRecord.newCall();
             */
            SystemInfo.log.debug("Antes do constructor LegUAS");

            fromLeg = new LegUAS(to2, to1, this, callIdFrom, "", "", "", false,
                    tid, invite, CallData.INCALL);
            fromLeg.setStatusCode(Call.RINGING);

            processNotify();
            /*
             * redit=new Credit(this); if (credit.canProceed()){
             */
            //processNotify();
            /*
             * }else { fromLeg.setStatusCode(this.REJECT); }
             */
        } catch (Exception e) {
            SystemInfo.log.debug("" + e);
        }
    } // end processInviteFrom        

/**
 * <p>
 * metodo chamado para por fim a chamada
 * </p>
 * </p>
 */
    public void hangup() {        
         if (toLeg!=null) toLeg.hangup();
          fromLeg.hangup();
          /*if (endTimeNotSet){
             endTimeNotSet=false;
             callInf.setStopTime(System.currentTimeMillis());
             callInf.setStopAccessTime(System.currentTimeMillis());
             callDataRecord.setCallCompletionReason("3PCC");
             if (toLeg.isDestination())
             termReason=toLeg.getCallEndReason();
             else termReason=fromLeg.getCallEndReason();
             credit.callEnded();
             try{Thread.sleep(1500);}catch(InterruptedException ie){SystemInfo.log.debug(""+ie);}
             CreditRcv creditRcv=new CreditRcv(callDataRecord);
             creditReceiver=creditRcv.getCreditReceiver();
             setAllIPDRFinalData();
             try{Thread.sleep(1500);}catch(InterruptedException ie){SystemInfo.log.debug(""+ie);}
             if (!mailsent){
              EmailwithVCard emailwithVCard = new EmailwithVCard(this.getFrom(),this.getTo(),callDataRecord,EmailwithVCard.received);
              mailsent=true;
             }
           //  callDataRecord.showData(); 
          }*/
        try{  manager.removeCall(this.getCallId());}catch(Exception e ){SystemInfo.log.debug(""+e);}
    } // end hangup        

/**
 * <p>
 * este método serve para direccionar a chamada
 * </p>
 * <p>
 * ATENCAO: Este método deve de ser chamado, mesmo que a chamada vá parar ao
 * </p>
 * <p>
 * numero original
 * </p>
 * </p>
 */
    public void forward(String sourceUri, String destinationUri, String finalToUri, String domain) {        
        
        if (forwarded==false){
            //callDataRecord.setRedirectedTo(finalToUri);
             sourceUri_=sourceUri;
             destinationUri_=destinationUri;
             finalToUri_=finalToUri;
             domain_=domain;
             finalTo=finalToUri;
             forwarded=true;
             callIdTo = manager.getSipProvider().getNewCallId().getCallId();
             //callIdTo=/*callIdFrom;*/callProvider.getNewCallIdHeader().getCallId();
             SystemInfo.log.debug("\n a Instanciar LEG UAC \n");
             //manager.addTalk(to1, finalToUri);
             toLeg = new LegUAC(sourceUri_,fromName, finalToUri_.split("@")[0]+"@"+domain_,this,callIdTo,cTypeFrom,cSubTypeFrom,sdpFrom,true,"b2b",destinationUri_,app, CallData.INCALL); 
        }
    } // end forward        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getTo() {        
        return to2;
    } // end getTo        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getFrom() {        
        return to1;
    } // end getFrom        
/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getToName() {        
        return toName;
    } // end getTo        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getFromName() {        
        return fromName;
    } // end getFrom        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getFinalTo() {        
        return finalTo;
    } // end getFinalTo        

/**
 * <p>
 * este é método chamado para enviar a mensagem 180 Ringing para o UA chamador
 * </p>
 * <p>
 * este método deve de ser chamado na interface CallControl
 * </p>
 * </p>
 */
    public void processRinging() {        
        fromLeg.setStatusCode(11);
    } // end processRinging        

/**
 * <p>
 * este método devolve uma sipURL de um numero, nome, nome@dominio... etc
 * </p>
 * <p>
 * ex:é introduzido sergio, ou sip:sergio
 * </p>
 * <p>
 * ele devolve sergio@inocrea.org nos dois casos
 * </p>
 * <p>
 * se fosse introduzido sergio@172.29.250.57 ele manteria o dominio
 * </p>
 * </p>
 */
    private String getURI(String in) {        
        String out=in;
        if (hasChar(out,"<"))
            out=out.split("<")[1];
        if (hasChar(out,">"))
            out=out.split(">")[0];
        if (hasChar(out,":"))
            if (out.split(":")[0].equalsIgnoreCase("sip"))
                out=out.split(":")[1];
        if (!hasChar(out,"@"))
           out=out+"@"+info.getProxy();
        return out;
    } // end getURI        

/**
 * <p>
 * metodo criado para retornar um ip de um "CONTACT HEADER"
 * </p>
 * <p>
 * no formato String
 * </p>
 * </p>
 */
    private String getIp(String contact) {        
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
    } // end getIp        

/**
 * <p>
 * método criado para verificar se uma determinada String contém um determinado carater
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
 * </p>
 */
    public String getCallId() {        
        return callIdFrom;
    } // end getCallId        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param x ...
 * </p><p>
 * @param isDestination ...
 * </p>
 */
    /*public void setStatusCode(int x, boolean isDestination) {        
            try{
            SystemInfo.log.debug("\n\n\n\n InCall.setStatusCode("+x+");");
            if (x==1) this.processNewInviteRcvd(isDestination);
            
             if (x==5 && initTimeNotSet ){
                
                 listener.processEvent(CallController.callEstablished);
                
               /* SystemInfo.log.debug("Setting CDR... init time");
                 callDataRecord.setInitTime(System.currentTimeMillis());
                 this.getMedia(mediaSDP, callInf);
                 callInf.setFrom(to1);
                 callInf.setTo(to2);
                 callInf.setStartTime(System.currentTimeMillis());
                 callDataRecord.addCallInfo(callInf);
                 initTimeNotSet=false;
                 credit.cativar();*/
 /*            }
             if (x==this.REJECT){
                 SystemInfo.log.debug(" reject init - reason "+toLeg.getCallEndReason());
                 
                 if(toLeg.getCallEndReason().compareTo("rejected")==0)
                     listener.processEvent(CallController.toRejected);
                 else if(toLeg.getCallEndReason().compareTo("busy")==0)
                     listener.processEvent(CallController.toBusy);           
                 else if(toLeg.getCallEndReason().compareTo("notFound")==0)
                     { if(isDestination)     
                             listener.processEvent(CallController.toUnavailable);
                       else  listener.processEvent(CallController.fromUnavailable);
                     }
                 
                 /* if (credit!=null)credit.callEnded();
                
                 
                 callDataRecord.setCallTerminationReason("noAnswer");
                 termReason="noAnswer";*/
               /* if (toLeg.isDestination())
                callDataRecord.setCallTerminationReason(toLeg.getCallEndReason());
                else callDataRecord.setCallTerminationReason(fromLeg.getCallEndReason());*/
                 /*callDataRecord.setCallCompletionReason("CC");
                try{ setAllIPDRFinalData();}catch(Exception e ){SystemInfo.log.debug("error on InCall.setStatusCode(reject) at line setAllIPDRData();");}
                 if (!mailsent){
                     EmailwithVCard emailwithVCard=new EmailwithVCard(this.getFrom(),this.getTo(),callDataRecord,EmailwithVCard.rejected);
                     mailsent=true;
                 }*/
/*                     SystemInfo.log.debug(" reject end ");
             }
             if (x==7 && endTimeNotSet){
                     /*       if (toLeg.isDestination())
                callDataRecord.setCallTerminationReason(toLeg.getCallEndReason());
                else callDataRecord.setCallTerminationReason(fromLeg.getCallEndReason());
                 SystemInfo.log.debug("Setting CDR... end time");*/
                /* callInf.setStopTime(System.currentTimeMillis());
                 callInf.setStopAccessTime(System.currentTimeMillis());
                 
                 callDataRecord.setCallCompletionReason("CC");
               //  callDataRecord.showData();
                 credit.callEnded();
                 endTimeNotSet=false;
                 try{Thread.sleep(500);}catch(InterruptedException ie){SystemInfo.log.debug(""+ie);}
                 CreditRcv creditRcv=new CreditRcv(callDataRecord);
                 creditReceiver = creditRcv.getCreditReceiver();*/
 /*                try{Thread.sleep(500);}catch(InterruptedException ie){SystemInfo.log.debug(""+ie);}
                   /*          if (!mailsent){
                     EmailwithVCard emailwithVCard = new EmailwithVCard(this.getFrom(),this.getTo(),callDataRecord,EmailwithVCard.received);
                     mailsent=true;
                 }*/
 /*               if (toLeg.isDestination())
                    termReason=toLeg.getCallEndReason();       
                else 
                     termReason=fromLeg.getCallEndReason();
                if(isDestination)     
                 listener.processEvent(CallController.toDisconnected);
                else listener.processEvent(CallController.fromDisconnected);
                /*     credit.callEnded();
                setAllIPDRFinalData();*/
                // try{  manager.removeCall(this.getCallId());}catch(Exception e ){SystemInfo.log.debug(""+e);}
 /*            }
             if (!isDestination && toLeg!=null) toLeg.setStatusCode(x);
             if (isDestination) fromLeg.setStatusCode(x);
        }catch(Exception e ){System.err.println("ERRO NUM SITIO CRITICO!!\nInCall.setStatusCode()\n: "+e);}


    } // end setStatusCode     
*/
    public void setStatusCode(int x,boolean isDestination){
       SystemInfo.log.debug("*******\nInCall - X code " + x);
       SystemInfo.log.debug("-------\nstatus= "+status+"\n*******");
       if (!isDestination){//fromLEG!
           SystemInfo.log.debug("setStatusCode("+x+','+"from)");
           if (x==InCall.PROCESSING_OK_TO){//recebeu 200 OK
               {//envia ack com sdp on hold e envia invite para to com sdp from

                   if (!fromLeg.getSDP().equals("")){
                            cTypeFrom=fromLeg.getCType();
                            cSubTypeFrom=fromLeg.getCSubType();
                            sdpFrom=fromLeg.getSDP();
                   }
                   fromLeg.setSdp(cTypeTo,cSubTypeTo, sdpTo); 
                   fromLeg.setStatusCode(5);
               }
           }else if (x==InCall.ACKING){//recebeu ACK
               try{
               listener.processEvent(CallController.callEstablished);
                }catch(CallException ce){ce.printStackTrace();}
           }else if (x==InCall.PROCESSING_INVITE_FROM){//recebeu re-INVITE
               //guarda SDP do FROM!; LegFrom=LegUAS; envia 200 OK com SDP on-hold
                   
                       if(!fromLeg.getSDP().equals("")){
                            cTypeFrom=fromLeg.getCType();
                            cSubTypeFrom=fromLeg.getCSubType();
                            sdpFrom=fromLeg.getSDP();
                       }
                       fromLeg.setSdp(cTypeTo,cSubTypeTo,sdpTo);
                       toLeg.setSdp(cTypeFrom,cSubTypeFrom,sdpFrom);
                       processNewInviteRcvd(false);
                   
           }else if(x==7){
               toLeg.setStatusCode(7);
               try{
                listener.processEvent(CallController.fromDisconnected);
               }catch(CallException ce){ce.printStackTrace();}
           }else if(x==this.UNAVAILABLE){//from unavailable recvd
               try{
                toLeg.setStatusCode(7);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.fromUnavailable);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==11){//from ringing rcvd
               try{
                listener.processEvent(CallController.FROM_RINGING);
               }catch(Exception e){e.printStackTrace();}
           }else if (x==this.BUSY){
               try{
                toLeg.setStatusCode(7);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.fromBusy);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==this.REJECT){
                try{
                toLeg.setStatusCode(7);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.fromRejected);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==this.NETWORKUNAVAILABLE){
                try{
                toLeg.setStatusCode(7);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.fromNetworkUnavailable);
               }catch(CallException ce){ce.printStackTrace();}
           }
       }else{//toLeg
           SystemInfo.log.debug("setStatusCode("+x+','+"to)");
           if (x==3){//recebeu 200 OK
               //envia ACK sdp to

                   if (!toLeg.getSDP().equals("")){
                        cTypeTo=toLeg.getCType();
                        cSubTypeTo=toLeg.getCSubType();
                        sdpTo=toLeg.getSDP();
                   }
                   if (status==0){
                        SystemInfo.log.debug("***\n\n\n\n\n\n\n\n\n*** A alterar o estado para 1 ****\n\n\n\n\n\n\n\n****");
                        status=1;
                        fromLeg.setSdp(cTypeTo,cSubTypeTo,sdpTo);
                        fromLeg.setStatusCode(3);
                   }
                   toLeg.setSdp(cTypeFrom,cSubTypeFrom,sdpFrom);
                   toLeg.setStatusCode(5);
                   try{
                        listener.processEvent(CallController.callEstablished);
                   }catch(Exception e){e.printStackTrace();}
           }else if (x==5){//recebeu ACK
               try{
               listener.processEvent(CallController.callEstablished);
                }catch(CallException ce){ce.printStackTrace();}
           }else if (x==1){//recebeu re-INVITE
               //guardaSDP TO!; toLeg->LegUAS; envia 200 OK sdp on-hold
               if (!toLeg.getSDP().equals("")){
                    cTypeTo=toLeg.getCType();
                    cSubTypeTo=toLeg.getCSubType();
                    sdpTo=toLeg.getSDP();
               }
               fromLeg.setSdp(cTypeTo,cSubTypeTo,sdpTo);
               toLeg.setSdp(cTypeFrom,cSubTypeFrom,sdpFrom);
               processNewInviteRcvd(true);
           }else if(x==7){
               fromLeg.setStatusCode(7);
               try{
                listener.processEvent(CallController.toDisconnected);
               }catch(CallException ce){ce.printStackTrace();}
           }else if(x==this.UNAVAILABLE){//from unavailable recvd
               try{
                fromLeg.setStatusCode(7);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.toUnavailable);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==11){//from ringing rcvd
               try{
                listener.processEvent(CallController.TO_RINGING);
               }catch(Exception e){e.printStackTrace();}
           }else if (x==this.BUSY){
               try{
                fromLeg.setStatusCode(7);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.toBusy);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==this.REJECT){
                try{
                fromLeg.setStatusCode(7);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.toRejected);
               }catch(CallException ce){ce.printStackTrace();}
           }else if (x==this.NETWORKUNAVAILABLE){
                try{
                fromLeg.setStatusCode(7);
               }catch(Exception e ){e.printStackTrace();}
               try{
                listener.processEvent(CallController.toNetworkUnavailable);
               }catch(CallException ce){ce.printStackTrace();}
           }
       }
   }


/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param ctype_ ...
 * </p><p>
 * @param csubtype_ ...
 * </p><p>
 * @param sdp__ ...
 * </p><p>
 * @param isDestination ...
 * </p>
 */
    public void setSdp(String ctype_, String csubtype_, String sdp_, boolean isDestination) {        
   /*     String sdp_="";
        if (sdp__!=null){
            sdp_=new String(sdp__);
        }
         if (isDestination) fromLeg.setSdp(ctype_,csubtype_,sdp_);
         if (!isDestination && toLeg!=null) toLeg.setSdp(ctype_,csubtype_,sdp_);
         else {
             cType=ctype_;
             csubType=csubtype_;
             sdp=sdp_;
         }
         if (!sdp_.equals("")) mediaSDP=sdp_;    */
    } // end setSdp        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param isDestination ...
 * </p>
 */
 /*private void processNewInviteRcvd(boolean isDestination) {        
        SystemInfo.log.debug("InCall.processNewInviteRcvd()");
        callInf.setStopTime(System.currentTimeMillis());
        callInf.setStopAccessTime(System.currentTimeMillis());
        callInf=new CallInformation();
        callInf.setStartAccessTime(System.currentTimeMillis());
        callDataRecord.newCall();
        initTimeNotSet=true;
        
        if (!isDestination){
            if (fromIsUAC){  // caso o Invite venha da origem e esta se comportar como UAC, vai ter que passar a ser um UAS
                SystemInfo.log.debug("Reversing from LegUAC to LegUAS");
                Leg tempLeg=new LegUAS((LegUAC)fromLeg);
                fromIsUAC=false;
                fromLeg=tempLeg;
                if (toIsUAC){
                    toLeg.restart();
                }else {
                    toIsUAC=true;
                    tempLeg=new LegUAC((LegUAS)toLeg);
                    toLeg=tempLeg;
                }
            }else{
             /*   Leg tempLeg=new LegUAS(toLeg);
                toLeg=tempLeg;
                fromLeg.restart();
                if (fromIsUAC){*/
/*                    SystemInfo.log.debug("Restarting toLeg... the invite was received on from the caller");
                    toLeg.restart();
                /*}else {
                    Leg tempLeg=new LegUAC(fromLeg);
                    fromLeg=tempLeg;
                }*/
/*            }
        }else{
            if (toIsUAC){
                SystemInfo.log.debug("Reversing from LegUAC to LegUAS");
                toIsUAC=false;
                SystemInfo.log.debug("1");
                Leg tempLeg=new LegUAS((LegUAC)toLeg);
                SystemInfo.log.debug("2");
                toLeg=tempLeg;
                SystemInfo.log.debug("3");
                fromLeg.restart();
                SystemInfo.log.debug("4");
                if (fromIsUAC){
                    fromLeg.restart();
                    SystemInfo.log.debug("5");
                }else {
                    fromIsUAC=true;
                    SystemInfo.log.debug("6");
                    tempLeg=new LegUAC((LegUAS)fromLeg);
                    SystemInfo.log.debug("7");
                    fromLeg=tempLeg;
                    SystemInfo.log.debug("8");
                }
            }else{
                SystemInfo.log.debug("Restarting fromLeg... the invite was received on from the callee");
                fromLeg.restart();
            }
        }
    } // end processNewInviteRcvd        
*/
    
      private void processNewInviteRcvd(boolean isDestination){
          
          SystemInfo.log.debug("processNewInviteRcvd");
         // listener.newInviteReceived();
          
          //initTimeNotSet=true;
          
          if (!isDestination){
              SystemInfo.log.debug("!isDestination");
              if (fromIsUAC){  // caso o Invite venha da origem e esta se comportar como UAC, vai ter que passar a ser um UAS
                  Leg tempLeg=new LegUAS((LegUAC)fromLeg, CallData.INCALL);
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
          }else{
              try{
              listener.processEvent(CallController.EVENT_MEDIA_CHANGE_REQ);
               }catch(CallException ce){ce.printStackTrace();}
              SystemInfo.log.debug("isDestination");
              if (toIsUAC){
                  toIsUAC=false;
                  Leg tempLeg=new LegUAS((LegUAC)toLeg, CallData.INCALL);
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
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
    private void processNotify() {        
           //Thread timeOut;
           SystemInfo.log.debug("ProcessNotify");
           /*	
               timeOut=new Thread(){
                public void run(){
                
               TODO: modificar este código de modo a disparar um evento "fromConnected": 
           */  
                   try{
                    listener.processEvent(CallController.fromConnected);
                   }catch(Exception e){e.printStackTrace();}
                   
           /*        
                };
                };
                timeOut.start();
          */
    } // end processNotify        

    

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
      public void processResponse(Response response, ClientTransaction clientTransaction) {        
         String to="";
        // try{
          to=((ToHeader)response.getHeader(ToHeader.NAME)).getAddress().toString().split("@")[0];
        // }catch(SipParseException hpe){SystemInfo.log.debug(""+hpe);}
         
         if (to.equals(to1.split("@")[0])){
             fromLeg.processResponse(response,clientTransaction);
         }else toLeg.processResponse(response,clientTransaction);
    } // end processResponse        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
    public void processRequest(Request request,ServerTransaction serverTransaction) {        
         //String from=request.getFromHeader().getNameAddress().getAddress().toString().split("@")[0];
         String from=((FromHeader)request.getHeader(FromHeader.NAME)).getAddress().toString().split("@")[0];
        // try{
         if (from.equals(to1.split("@")[0])) /*if (request.getMethod().equals(Request.INVITE)||request.getMethod().equals(Request.ACK))*/ toLeg.processRequest(request,serverTransaction); //else fromLeg.processRequest(request,tid);
         else /*if (request.getMethod().equals(Request.INVITE)||request.getMethod().equals(Request.ACK))*/ fromLeg.processRequest(request,serverTransaction);
    } // end processRequest        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param a ...
 * </p><p>
 * @param b ...
 * </p>
 */
    public void start(String a, String b) {        
        // your code here
    } // end start        


/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    public String getPayer() {        
        return getFrom();
    } // end getPayer        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p>
 */
    public void reject() {        
        if(!mailsent){
            mailsent=true;  
            fromLeg.setStatusCode(this.REJECT);
    //        callDataRecord.setCallCompletionReason("CC");
            if (toLeg.isDestination())
            termReason=toLeg.getCallEndReason();
            else termReason=fromLeg.getCallEndReason();
     //       setAllIPDRFinalData();
            //try{Thread.sleep(1500);}catch(InterruptedException ie){SystemInfo.log.debug(""+ie);}
           // EmailwithVCard emailwithVCard = new EmailwithVCard(this.getFrom(),this.getTo(),callDataRecord,EmailwithVCard.rejected);
        }
    } // end reject        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a boolean with ...
 * </p>
 */
    public boolean isGone() {        
        return !endTimeNotSet;
    } // end isGone        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * @return a String with ...
 * </p>
 */
    private String getCreditedUser() {        
        return creditReceiver;
    } // end getCreditedUser        

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
    public void setCallControllerListener(CallControllerListener listener_) {        
        listener = listener_;
    } // end setCallControllerListener        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param controlLevel_ ...
 * </p>
 */
    public void setControlLevel(int controlLevel_) {        
        controlLevel = controlLevel_;
    } // end setControlLevel        

/**
 * <p>
 * Does ...
 * </p><p>
 * 
 * </p><p>
 * 
 * @param to ...
 * </p>
 */
    public void connectTo(String to, String body) {       
        
        if ( body.equals("") ){
        to=this.getURI(to);
        forward(getFrom(),to,to,to.split("@")[1]);
        } else{
           /* CallController call = manager.setCallModel(this, CallController.CLASS_END_POINT_CALL_CONTROLLER, (Leg)fromLeg);
            ((Comm)call).setStatus(status);
            call.setCallControllerListener(listener);
            ((Comm)call).setSdp(this.cTypeFrom,this.cSubTypeFrom,this.sdpFrom,false);
            call.connectTo(to,body);*/
        }
            
    }
    
    /** <p>
     *
     * </p>
     * <p>
     *
     * @return url para um ficheiro com a imagem do originador da chamada se
     * disponivel nas mensagens SIP. Senao pode-se questionar a BD pelo
     * ficheiro em funcao do sipUrl.
     * </p>
     *
     */
    public String getCallerPicture() {
        return null;
    }
    
    /** <p>
     * Chamada em espera &#233; recuperada. So e valido se a chamada estiver em
     * espera, i.e., depois de receber o evento callOnHold (!!?)
     * </p>
     *
     *
     */
    public void resumeCall() {
    }
    
    public void restartMe(boolean isDestination) {
       if (!isDestination){
        fromIsUAC=true;
        Leg tempLeg=new LegUAC((LegUAS)fromLeg, CallData.INCALL);
        fromLeg=tempLeg;
       }else{
           fromIsUAC=true;
        Leg tempLeg=new LegUAC((LegUAS)toLeg, CallData.INCALL);
        toLeg=tempLeg;   
       }
    }
    
    public void start() {
    }
    
    public void setSdpFrom(String sdp) {
    }
    
    public void setSdpTo(String sdp) {
    }
    
    public void setStatus(int status_) {
        status = status_;
    }
    
    public String getSdpTo() {
        return sdpTo;
    }
    
    public String getSdpFrom() {
        return sdpFrom;
    }
    
    public void ack() {
    }
    
    public void ringing() {
    }
    
 // end connectTo        

    
} // end InCall





