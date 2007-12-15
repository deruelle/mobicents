package com.ptin.xmas.control.common;

import com.ptin.xmas.control.common.interfaces.CommFactory;
import com.ptin.xmas.control.common.Logx;
import com.ptin.xmas.control.common.SystemInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.sip.ClientTransaction;
import javax.sip.InvalidArgumentException;
import javax.sip.PeerUnavailableException;
import javax.sip.ServerTransaction;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.Hop;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.AllowHeader;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 *Interface used to construct messages to send thru an interface implemented by CommProviderImpl
 */
public class CommFactoryImpl implements CommFactory, java.io.Serializable
{
    //singleton class variable
   static CommFactoryImpl myself=null;
   SystemInfo info=null;
   private AddressFactory addressFactory= null; 
   private HeaderFactory headerFactory = null; 
   private static MessageFactory messageFactory = null; 
   private SipFactory sipFactory=null;
   private SipProvider sipProvider=null;
   protected Hop nextHop;
   protected Hashtable branchIdTable;
   private CommProviderImpl commProvider;
   private Logx log=null; 
   /**
    * Creates new JainComm
    * @roseuid 3D6A57490234
    */
   private CommFactoryImpl(CommProviderImpl manager)
   {
       try{
         info=SystemInfo.getReference("incom");
            }catch(Exception ex){ex.printStackTrace();};

     //log=Logx.getReference();
     try{
     sipFactory=SipFactory.getInstance(); 
     addressFactory=sipFactory.createAddressFactory();
     headerFactory=sipFactory.createHeaderFactory();
     messageFactory=sipFactory.createMessageFactory();
     }catch(PeerUnavailableException pue){pue.printStackTrace();}
     commProvider=manager;
     sipProvider=commProvider.getSipProvider();
   }
   

                /*
                 * Baseado no createInvite!
                 * PCh: A fazer: Incluir parametro regExp para poder ser usado para criar mensagens de unregister
                 *
                 */
   
       public ClientTransaction createRegister(String userUri, String proxyid, int regExp) { 
           
         //SystemInfo.log.debug("[CommFactoryImpl.createRegister] user: "+userUri+" exp "+regExp);
         String userFrom=userUri.split("@")[0];
         Request register = null;
         SipURI reqUri=null;
         SipURI fromSipURI =null;
         Address fromAddress = null; 
         FromHeader fromHeader = null; 
         ToHeader toHeader = null; 
         SipURI toURI=null;
         CallIdHeader callIdHeader = null; 
         CSeqHeader cSeqHeader = null; 
         ViaHeader viaHeader = null; 
         ArrayList viaHeaders = null;
         ContentTypeHeader contentTypeHeader = null;
         //sipProvider=commProvider.getSipProvider();
         try { 
             fromSipURI=addressFactory.createSipURI(userFrom, proxyid);
             fromSipURI.setPort(commProvider.getPort());
             fromAddress=addressFactory.createAddress(userFrom,fromSipURI);
             fromHeader=headerFactory.createFromHeader(fromAddress, gov.nist.javax.sip.Utils.generateTag());
             ExpiresHeader expiresHeader=headerFactory.createExpiresHeader(regExp);
             toURI = addressFactory.createSipURI(userFrom,proxyid);
             toURI.setPort(sipProvider.getListeningPoint().getPort());
             toHeader=headerFactory.createToHeader(addressFactory.createAddress(toURI), null);
             reqUri=addressFactory.createSipURI(null,proxyid);
             viaHeader = headerFactory.createViaHeader(commProvider.getHost(),commProvider.getPort(), "udp", gov.nist.javax.sip.Utils.generateBranchId());
             viaHeaders = new ArrayList(); 
             viaHeaders.add(viaHeader); 
             contentTypeHeader = headerFactory.createContentTypeHeader("", ""); 
             callIdHeader = commProvider.getNewCallIdHeader();   
             cSeqHeader = headerFactory.createCSeqHeader(1, Request.REGISTER); 
             //SystemInfo.log.debug("[CommFactoryImpl.createRegister] creatig mesg ");
             
             register = messageFactory.createRequest(reqUri,Request.REGISTER,callIdHeader,cSeqHeader,fromHeader,toHeader,viaHeaders,getMaxForwardsHeader(null));
             Address a1 = addressFactory.createAddress("sip:"+userFrom+"@"+commProvider.getHost()+':'+commProvider.getPort());//.createAddress(sul);
             ContactHeader cl = headerFactory.createContactHeader(a1);
             cl.setExpires(regExp);
             register.setHeader(expiresHeader);
             register.setHeader(cl);
              //log.log("\n\nRegister created: \n"+register+" \n");
         } catch(Exception e) { 
	     e.printStackTrace();
             System.err.println(e.getMessage()); 
             System.exit(-1); 
	     e.printStackTrace();
             System.err.println(e.getMessage()); 
             System.exit(-1); 
         }
         try{
         return sipProvider.getNewClientTransaction(register);
         }catch(TransactionUnavailableException tue){tue.printStackTrace();}
         return null;
   }
       public static CommFactoryImpl getReference(){
           if (myself==null){System.out.println("CommFactoryImpl == null ... needs CommProviderImpl!!!"); }
           return myself;
       }
       public static CommFactoryImpl getReference(CommProviderImpl commProviderImpl){
           //if (myself==null){myself=new CommFactoryImpl(commProviderImpl);}
           //else {System.out.println("CommFactoryImpl already instanciated, commProviderImpl not needed!");}
           
           myself=new CommFactoryImpl(commProviderImpl);
           
           return myself;
       }
          /**
     *method used to create a Request message:
     *from --> FromHeader (String)
     *to --> ToHeader (String)
     *port --> the destination port of the message (int)
     *callId --> CallId (String)
     *contentType,contentSubType --> these must be filled when sending out message with SDP (String)
     *body --> the SDP message 
     *method --> the method of the request. ex: Request.INVITE
     *nameViewed --> the name wich will apear on the receivers user agent (String)
     *c_Seq --> the sequence number of the message in the c_Seq header (int)
     */ 
       
       public ClientTransaction createRequest(String from,String to,int port,String callId, String contentType, String contentSubType, String body,String method,String nameViewed,int c_Seq){
           return createRequest(from,to,"","",from,port,callId,contentType,contentSubType,body,method,nameViewed,c_Seq);
       }
       
          /**
    *method used to create a Request message:
    *from --> FromHeader (String)
    *to --> ToHeader (String)
    *reqUri --> Uri of the request line (String)
    *domain --> the domain of the Uri on the request line (String) (=inocrea.org if "")
    *contact --> contact header (String)
    *port --> the destination port of the message (int)
    *callId --> CallId (String)
    *contentType,contentSubType --> these must be filled when sending out message with SDP (String)
    *body --> the SDP message 
    *method --> the method of the request. ex: Request.INVITE
    *nameViewed --> the name wich will apear on the receivers user agent (String)
    *c_Seq --> the sequence number of the message in the c_Seq header (int)
    */
       public ClientTransaction createRequest(String from,String to,String reqUri,String domain,String contact,int port,String callId, String contentType, String contentSubType, String body,String method,String nameViewed,int c_Seq){
         Request request=null;
   
         if (reqUri==null)reqUri=to;
         else if (reqUri.equals("")) {reqUri=to;}
         if (domain==null)domain=info.getProxy();
         else if (domain.equals("")) {domain=info.getProxy();}
         try{
            
           CallIdHeader callIdHeader=null;
           
           CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(c_Seq, method);
           ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader(contentType, contentSubType); 
 
           if (callId.equals("")) {
                 callIdHeader = commProvider.getNewCallIdHeader(); 
           } else {
                 callIdHeader =headerFactory.createCallIdHeader(callId);
                 //callIdHeader.setCallId(callId);
           }
           
             if (body.equals(""))
             {
              
             request = messageFactory.createRequest(
                    createRequestURI(reqUri,domain,port),
                    method, 
                    callIdHeader, 
                    cSeqHeader,
                    createFromHeader(from,nameViewed), 
                    createToHeader(to,port), 
                    createViaHeaders() ,
                    getMaxForwardsHeader(null)); 
             }
             else
             {
                 
             request = messageFactory.createRequest(
                    createRequestURI(reqUri,domain,port),
                    method, 
                    callIdHeader, 
                    cSeqHeader,
                    createFromHeader(from,nameViewed), 
                    createToHeader(to,port), 
                    createViaHeaders(),
                    getMaxForwardsHeader(null),
                    contentTypeHeader,
                    body.getBytes()); 
             }

             if (!contact.equals("")) request.setHeader(createContactHeader(contact+":"+commProvider.getPort()));

         }catch(Exception e){e.printStackTrace();}
         try{
            
         return sipProvider.getNewClientTransaction(request);
         }catch(TransactionUnavailableException tue){tue.printStackTrace();}
         return null;
        // return request;
       }
       
       public FromHeader createFromHeader(String from,String nameViewed){
           
           SipURI fromAddress=null;
           Address fromNameAddress=null;
           FromHeader fromHeader=null;
           try{
           fromAddress = addressFactory.createSipURI(from.split("@")[0],from.split("@")[1]); 
           fromNameAddress = addressFactory.createAddress(nameViewed.split("@")[0],fromAddress);
           fromHeader = headerFactory.createFromHeader(fromNameAddress,gov.nist.javax.sip.Utils.generateTag()); 
           }catch(Exception e){e.printStackTrace();}
          
           return fromHeader;
       }
       private ToHeader createToHeader(String to,int port){
           System.out.println(to);
           SipURI toAddress=null;
           Address toNameAddress=null;
           ToHeader toHeader=null;
           try{
            toAddress=addressFactory.createSipURI(to.split("@")[0],to.split("@")[1]);
            toNameAddress=addressFactory.createAddress(to.split("@")[0],toAddress);
            toHeader=headerFactory.createToHeader(toNameAddress,null);
           }catch(Exception e){e.printStackTrace();}
           
           return toHeader;
       }
       private URI createRequestURI(String reqUri,String domain,int port){
          
           SipURI uri=null;
           try{
              uri=addressFactory.createSipURI(reqUri.split("@")[0],domain);
              uri.setPort(port);
              uri.setTransportParam(commProvider.getTransport());
           }catch(Exception e){e.printStackTrace();}
           
           return uri;
       }
    /**
     *Function the returns an ArrayList containing a ViaHeader corresponding to
     *the current stack
     */
       public ArrayList createViaHeaders(){
         //log.log("CommFactoryImpl.createViaHeaders() start");
         ViaHeader viaHeader = null; 
         ArrayList viaHeaders = null; 
         String transport = commProvider.getTransport();
         try{
             //String host = commProvider.getHost();
             String host = info.getIp();
             int port = commProvider.getPort();
             String branch_id = gov.nist.javax.sip.Utils.generateBranchId();
             
             viaHeader = headerFactory.createViaHeader(host, port, transport, branch_id); 
             viaHeaders = new ArrayList(); 
             viaHeaders.add(viaHeader); 
         }catch(Exception e){e.printStackTrace();}
         //log.log("CommFactoryImpl.createViaHeaders() stop");
         return viaHeaders;
       }
       public ContactHeader createContactHeader(String contact){
           ContactHeader contactHeader=null;
           try{
             SipURI su1 = addressFactory.createSipURI(contact.split("@")[0], contact.split("@")[1]);
             Address a1 = addressFactory.createAddress(contact.split("@")[0],su1);
             contactHeader = headerFactory.createContactHeader(a1);
           }catch(Exception e){e.printStackTrace();}
           return contactHeader; 
       }
       private ArrayList createViaHeaders(ListIterator li){
         ViaHeader viaHeader = null; 
         ArrayList viaHeaders = null;
         String transport = commProvider.getTransport();
         try{
            viaHeaders = new ArrayList();
            while(li.hasNext()){
                viaHeader=(ViaHeader)li.next();
            }
            viaHeaders.add(viaHeader);
         }catch(Exception e){e.printStackTrace();}
         return viaHeaders; 
       }
    /**
     *this function returns a ClientTransaction got from a CANCEL request based on a given request message (usually an invite)
     */
       public ClientTransaction createCancel(Request invite){
           Request cancel=null;
           CSeqHeader cSeqHeader=(CSeqHeader)invite.getHeader(CSeqHeader.NAME);
           try{
           cSeqHeader.setMethod(Request.CANCEL);
           cancel=messageFactory.createRequest(
                    invite.getRequestURI(),
                    Request.CANCEL, 
                    (CallIdHeader)invite.getHeader(CallIdHeader.NAME),//getCallIdHeader(), 
                    cSeqHeader,
                    (FromHeader)invite.getHeader(FromHeader.NAME),//.getFromHeader(), 
                    (ToHeader)invite.getHeader(ToHeader.NAME),//.getToHeader(), 
                    createViaHeaders(invite.getHeaders(ViaHeader.NAME)),//createViaHeaders(invite.getViaHeaders()) ); 
                    getMaxForwardsHeader(invite.getHeader(MaxForwardsHeader.NAME))
                    );
                    
           }catch(Exception e){e.printStackTrace();}
           try{
           return sipProvider.getNewClientTransaction(cancel);
           }catch(TransactionUnavailableException tue){tue.printStackTrace();}
           return null;
       }
      private String getIp(String contact){
      String ip="172.29.250.60";
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
     *function that returns a ClientTransaction got from an Ack Request based on a given Request and its Response
     *(usually an invite and its 200 OK)
     *
     *Request invite-> the request that generated the transaction
     *Response ok-> the response to be ACKed
     *String sdp->the content of the message
     *String contentType-> the content type of the message
     *String contentSubType-> the content sub type of the message
     *int port-> the port where the message should arrive on its destination
     */
       public ClientTransaction createAck(Request invite, Response ok, String sdp, String contentType, String contentSubType, ContactHeader contactHeader){
           //log.log("CommFactory.createAck init ");
           Request ack=null;
           CSeqHeader cSeqHeader=(CSeqHeader)invite.getHeader(CSeqHeader.NAME);//getCSeqHeader();
           try{
            cSeqHeader.setMethod(Request.ACK);
            SipURI reqUri=null;
            
                         URI usr=invite.getRequestURI();
             String userx=null;
             if (hasChar(contactHeader.getAddress().getURI().toString(),"@")){
                 userx=contactHeader.getAddress().getURI().toString().split("@")[0];
             }if (hasChar (userx,":")){
                 userx=userx.split(":")[1];
             }
            
            reqUri= addressFactory.createSipURI(userx,getIp(contactHeader.getAddress().getURI().toString()));
           reqUri.setPort(getPort(contactHeader.getAddress().getURI().toString()));
           
           
           
            ToHeader toHeader=(ToHeader)ok.getHeader(ToHeader.NAME);//getToHeader();
            Address address=toHeader.getAddress();
            String endereco=address.getURI().toString().split("@")[0];
            
            if (endereco.split("ip:").length>1)
                endereco=endereco.split("ip:")[1];
            
            SipURI uri=addressFactory.createSipURI(endereco, address.getURI().toString().split("@")[1]);
            address=addressFactory.createAddress(address.getDisplayName(),uri);
            toHeader.setAddress(address);
            ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader(contentType, contentSubType); 
            ack=messageFactory.createRequest(
                    reqUri,
                    Request.ACK,
                    (CallIdHeader)invite.getHeader(CallIdHeader.NAME),
                    cSeqHeader,
                    (FromHeader)invite.getHeader(FromHeader.NAME),
                    toHeader,
                    createViaHeaders(invite.getHeaders(ViaHeader.NAME)),
                    getMaxForwardsHeader(invite.getHeader(MaxForwardsHeader.NAME)),
                    contentTypeHeader,
                    sdp.getBytes()
                    ); 
           }catch(Exception e){e.printStackTrace();}
           try{
              return sipProvider.getNewClientTransaction(ack);
           }catch(TransactionUnavailableException tue){tue.printStackTrace();}
           return null;
       }
           /**
     *function that returns a ClientTransaction got from an Ack Request based on a given Request and its Response
     *(usually an invite and its 200 OK)
     *
     *Request invite-> the request that generated the transaction
     *Response ok-> the response to be ACKed
     *int port-> the port where the message should arrive on its destination
     */
      public ClientTransaction createAck(Request invite, Response ok, ContactHeader contactHeader){
           Request ack=null;
           CSeqHeader cSeqHeader=(CSeqHeader)invite.getHeader(CSeqHeader.NAME);//getCSeqHeader();
            
            System.err.println("CommFactoryImpl... I got the ContactHeader: "+contactHeader.getAddress().getURI().toString());
            System.err.println("CommFactoryImpl... I got the IP: "+getIp(contactHeader.getAddress().getURI().toString()));
            System.err.println("CommFactoryImpl... I got the port: "+getPort(contactHeader.getAddress().getURI().toString()));
           try{
           
           cSeqHeader.setMethod(Request.ACK);

           ToHeader toHeader=(ToHeader)ok.getHeader(ToHeader.NAME);//getToHeader();

           Address address=toHeader.getAddress();

         
           String userNameTo=null;
           if (hasChar(address.getURI().toString(),"@"))
               userNameTo=address.getURI().toString().split("@")[0];
           if (userNameTo.split("ip:").length>1)
               userNameTo=userNameTo.split("ip:")[1];
           //log.log("userNameTo "+userNameTo);
           SipURI uri=addressFactory.createSipURI(userNameTo, getIp(address.getURI().toString()));
     
           //uri.setPort(port);
           address=addressFactory.createAddress(address.getDisplayName(),uri);

           toHeader.setAddress(address);
  
           SipURI reqUri=null;

           
             URI usr=invite.getRequestURI();
             String userx=null;
             if (hasChar(contactHeader.getAddress().getURI().toString(),"@")){
                 userx=contactHeader.getAddress().getURI().toString().split("@")[0];
             }
            
            reqUri= addressFactory.createSipURI(userx,getIp(contactHeader.getAddress().getURI().toString()));
           reqUri.setPort(getPort(contactHeader.getAddress().getURI().toString()));
           
           ack=messageFactory.createRequest(
                    reqUri,//invite.getRequestURI(),
                    Request.ACK, 
                    (CallIdHeader)invite.getHeader(CallIdHeader.NAME),//invite.getCallIdHeader(), 
                    cSeqHeader,
                    (FromHeader)invite.getHeader(FromHeader.NAME),//invite.getFromHeader(), 
                    toHeader,//ok.getToHeader(), 
                    createViaHeaders(invite.getHeaders(ViaHeader.NAME)),//createViaHeaders(invite.getViaHeaders()),
                    getMaxForwardsHeader(invite.getHeader(MaxForwardsHeader.NAME))
                 ); 

           }catch(Exception e){e.printStackTrace();}
           try{
           return sipProvider.getNewClientTransaction(ack);
           }catch(TransactionUnavailableException tue){tue.printStackTrace();}
           return null;
       }
                 /**
     *function that returns a ClientTransaction got from a Bye Request based on a given Request (usually a received invite)
     *the to field is used to create the request Uri line
     */
             public ClientTransaction createBye(Request invite, String to, String from, int port){
           Request cancel=null;
           //CSeqHeader cSeqHeader=invite.getCSeqHeader();
           CSeqHeader cSeqHeader=(CSeqHeader)invite.getHeader(CSeqHeader.NAME);
          /* try{
           cSeqHeader.setSequenceNumber(cSeqHeader.getSequenceNumber()+1);
           }catch(SipParseException spe){log.log(""+spe);}
           */
           try{
           cSeqHeader.setMethod(Request.BYE);
           cancel=messageFactory.createRequest(
                    this.createRequestURI(to,to.split("@")[1],port),
                    Request.BYE, 
                    (CallIdHeader)invite.getHeader(CallIdHeader.NAME), 
                    cSeqHeader,
                    (FromHeader)invite.getHeader(FromHeader.NAME),//createFromHeader(from,invite.get), 
                    (ToHeader)invite.getHeader(ToHeader.NAME),//createToHeader(to), 
                    createViaHeaders(invite.getHeaders(ViaHeader.NAME)),
                    getMaxForwardsHeader(invite.getHeader(MaxForwardsHeader.NAME))
                    ); 
           }catch(Exception e){e.printStackTrace();}
           try{
           return sipProvider.getNewClientTransaction(cancel);
           }catch(TransactionUnavailableException tue){tue.printStackTrace();}
           return null;
       }
                 /**
     *this function creates the Allow headers (also known as Accept)
     * Accept: INVITE, ACK, CANCEL, BYE, REFER, OPTIONS, NOTIFY, REGISTER, SUBSCRIBE
     */
       public AllowHeader createAllowHeader(String method){
           AllowHeader allowHeader=null;
try{
                allowHeader=headerFactory.createAllowHeader(method);
 
           }catch(Exception e){e.printStackTrace();}
           return allowHeader;
       }
    /**
     *function that returns a response to a given Request message with sdp
     */
       public Response createResponse(Request invite1,String sdp,String c_Type,String c_SubType){
           Response responseOk=null;
           try{
       ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader(c_Type, c_SubType); 
        responseOk=messageFactory.createResponse(Response.OK,invite1,contentTypeHeader,sdp.getBytes());
        //responseOk=messageFactory.createResponse(Response.OK,invite1,sdp,contentTypeHeader);
        
           }catch (Exception e){e.printStackTrace();}
           return responseOk;
       }
           /**
     *function that returns a List with a RecordRouteHeader to a given URI (String)
     */
       public List createRecordRouters(String toRoute){
           List recordRoute=new LinkedList();
           SipURI myURL=null;
           Address myAddress=null;
           try{
               myURL = addressFactory.createSipURI(toRoute.split("@")[0],toRoute.split("@")[1]); 
               //myURL.setPort(commProvider.getPort());
               myAddress = addressFactory.createAddress(toRoute.split("@")[0], myURL); 
               recordRoute.add(headerFactory.createRecordRouteHeader(myAddress));
           }catch(Exception e){e.printStackTrace();}
           return recordRoute;
       }
    /**
     *Function that returns a RecordRouteHeader based on String toRoute
     *
     *this header tells the other party where to send the next messages
     */
       public RecordRouteHeader getRecordRouteHeader(String toRoute){
           SipURI myURL=null;
           Address myAddress=null;
           RecordRouteHeader recRouteHeader=null;
           try{
               myURL = addressFactory.createSipURI(toRoute.split("@")[0],toRoute.split("@")[1]); 
               myURL.setPort(commProvider.getPort());
               myAddress = addressFactory.createAddress(toRoute.split("@")[0], myURL); 
               recRouteHeader=headerFactory.createRecordRouteHeader(myAddress);
           }catch(Exception e){e.printStackTrace();}
           return recRouteHeader;
       }
           /**
     *function that returns a List of RouteHeaders
     *Parameters:
     *list--> a String array of Url's
     */
       public RouteHeader createRouteHeader(String in){
           RouteHeader routeHeader=null;
           SipURI myURI=null;
           Address myAddress=null;
           int x=0;
           try{
               myAddress=addressFactory.createAddress(in);
           /*    if (list[x].split("@").length>1) myURI=addressFactory.createSipURI(list[x].split("@")[0],list[x].split("@")[1]);
               else myURI=addressFactory.createSipURI(null,list[x]); 
               myURI.setPort(port);
               if (list[x].split("@").length>1) myAddress=addressFactory.createAddress(list[x].split("@")[0],myURI);
               else myAddress=addressFactory.createAddress(null,myURI);*/
               routeHeader= headerFactory.createRouteHeader(myAddress);
               //x++;
               
               
           }catch(Exception e){e.printStackTrace();}
           return routeHeader;
       }
    /**
     *Function that generates a RouteHeader based on a given toRoute and port
     *
     *String toRoute-> the destination of the message (it can be a proxy or the user agent)
     *int port-> the port where the message should arrive on its destination
     */
       public RouteHeader getRouteHeader(String toRoute,int toPort){
           SipURI toURI=null;
           Address toAddress=null;
           RouteHeader routeHeader=null;
           
           try{
               String a=null;
               String b="";
               if (toRoute.split("@").length>1){
                   a=toRoute.split("@")[0];
                   b=toRoute.split("@")[1];
               }else b=toRoute;
               toURI=addressFactory.createSipURI(a, b);
               toURI.setPort(toPort);
               toAddress=addressFactory.createAddress(null,toURI);
               routeHeader=headerFactory.createRouteHeader(toAddress);
               
           }catch(Exception e){e.printStackTrace();}
           return routeHeader;
       }
           /**
     *Function that returns an UserAgentHeader based on a given String ua
     *
     *Attention: very useful when dealing with Messenger! I believe it will only
     *start a video call when theres something mentioned like "windows xx" on this header
     */
       public UserAgentHeader createUserAgent(String ua){
           List x=new ArrayList();
           x.add(ua);
           UserAgentHeader uah=null;
           try{
                uah=headerFactory.createUserAgentHeader(x);
           }catch(Exception e){e.printStackTrace();}
           return uah;
       }
       private MaxForwardsHeader getMaxForwardsHeader(Header inHeader){
          // log.log("CommFactoryImpl.getMaxForwardsHeader() start");
           MaxForwardsHeader outHeader=null;
           if (inHeader!=null){
               outHeader=(MaxForwardsHeader) inHeader;
           }else {
               try{
               outHeader=headerFactory.createMaxForwardsHeader(32);
               }catch(InvalidArgumentException iae){iae.printStackTrace();}
           }
           //log.log("CommFactoryImpl.getMaxForwardsHeader() stop");
           return outHeader;
               
       }
    /**
     *Function that returns a Response base on a given stats (ex. 200, 180, 486)
     *and the request that is to be answered
     */
       public Response createResponse(int stats,Request firstInvite){
           try{
           return messageFactory.createResponse(stats,firstInvite);
           }catch(Exception e){e.printStackTrace();}
           return null;
       }
     /**
     *Function that returns a ClientTransaction object based on a given request.
     */
       public ClientTransaction getClientTransaction(Request request){
           try{
               return sipProvider.getNewClientTransaction(request);
           }catch(Exception e){e.printStackTrace();}
           return null;
       }
    /**
     *Function that returns a ServerTransaction based on a given request.
     */
       public ServerTransaction getServerTransaction(Request request){
           try{
               return sipProvider.getNewServerTransaction(request);
           }catch(Exception e){e.printStackTrace();}
           return null;
       }
    /**
     *Function that returns a ContentTypeHeader based a given content type, and a 
     * content sub type.
     */
       public ContentTypeHeader createContentTypeHeader(String cType,String cSubType){
           try{
                return headerFactory.createContentTypeHeader(cType, cSubType);
           }catch(Exception e){e.printStackTrace();}
           return null;
       }
      public Request changeDestinationPort(Request req,int port){
          URI uri=req.getRequestURI();
          String user=null;
          String host=null;
          try{
              user=uri.toString().split("@")[0];
              host=uri.toString().split("@")[1];
          }catch(Exception e){
              host=uri.toString();
          }
          try{
          SipURI sipURI=addressFactory.createSipURI(user, host);
          sipURI.setPort(port);
          req.setRequestURI(sipURI);
          }catch(java.text.ParseException pe){pe.printStackTrace();}
          return req;
          
      }
      
      
      public ClientTransaction createSpecialRequest(Request firstRequest, Response myOK, String sdp, String contentType, String contentSubType, ContactHeader contactHeader,String method,int cSeq,String localTag,String remoteTag,String callId,String to,String userAgent){
           //log.log("CommFactory.createAck init ");
           Request ack=null;
           
           try{
             CSeqHeader cSeqHeader=headerFactory.createCSeqHeader(cSeq, method);
             SipURI reqUri=null;
                 reqUri=addressFactory.createSipURI(to,info.getProxy());

                 int portTo=5060;
                 String ipTo=info.getProxy();
                 if (contactHeader!=null){
                    portTo=getPort(contactHeader.getAddress().getURI().toString());
                    ipTo=getIp(contactHeader.getAddress().getURI().toString());
                 }                 
                 
           FromHeader fromHeader=createFromHeader(info.getUA(), info.getUA().split("@")[0]);
           if (localTag!=null) fromHeader.setTag(localTag);
           else{
               fromHeader.setTag(gov.nist.javax.sip.Utils.generateTag());
           }
           ToHeader toHeader=createToHeader(to+"@"+info.getProxy(),5060);
           if (remoteTag!=null)toHeader.setTag(remoteTag);
           
           
           RouteHeader routeHeader=null;
           ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader(contentType, contentSubType); 
           if (contentSubType.equalsIgnoreCase("x-msmsgsinvite")){
         //      reqUri=addressFactory.createSipURI(info.getUA().split("@")[0],info.getProxy());
         //      String routeAddress=to+'@'+ipTo;
             /*  if (contactHeader!=null)
               if (!hasChar(contactHeader.getAddress().getURI().toString(),"@"))
                   routeAddress=ipTo;*/
        //       if (contactHeader==null)
         //      routeHeader=headerFactory.createRouteHeader(addressFactory.createAddress("sip:"+routeAddress+':'+portTo));
         //      else routeHeader=headerFactory.createRouteHeader(contactHeader.getAddress());
                        
            contentTypeHeader.setParameter(" charset", "UTF-8"); 

           }
           
          CallIdHeader callIdHeader=headerFactory.createCallIdHeader(callId);
          ArrayList x=null;
          if (firstRequest!=null) x=createViaHeaders(firstRequest.getHeaders(ViaHeader.NAME));
          else x=createViaHeaders();
          MaxForwardsHeader h=null;
          if (firstRequest!=null) h=getMaxForwardsHeader(firstRequest.getHeader(MaxForwardsHeader.NAME));
          else h=headerFactory.createMaxForwardsHeader(32);
          
         
    
            ack=messageFactory.createRequest(
                    reqUri,
                    method,
                    callIdHeader,
                    cSeqHeader,
                    fromHeader,
                    toHeader,
                    x,
                    h,
                    contentTypeHeader,
                    sdp.getBytes()
                    ); 
            try{
          if (userAgent!=null && !userAgent.equals("")){
            List uaList=new ArrayList();
            uaList.add(userAgent);
            UserAgentHeader userAgentHeader=headerFactory.createUserAgentHeader(uaList);
            ack.setHeader(userAgentHeader);
          }
          if (routeHeader!=null){
              ack.setHeader(routeHeader);
          }
            }catch(Exception e ){e.printStackTrace();}
            
           }catch(Exception e){e.printStackTrace();}
           try{
              return sipProvider.getNewClientTransaction(ack);
           }catch(TransactionUnavailableException tue){tue.printStackTrace();}
           return null;
       }
      
      public SipURI createSipUri(String usr,String domain){
          try{
            return addressFactory.createSipURI(usr,domain);
          }catch(Exception pe){pe.printStackTrace();}
          return null;
      }
      
      public AuthorizationHeader createAuthorizationHeader(String userName, String nonce,String response){
          try{
          AuthorizationHeader authHeader=headerFactory.createAuthorizationHeader("Digest");
          authHeader.setUsername(userName);
          authHeader.setRealm(info.getProxy());
          authHeader.setAlgorithm("\"md5\"");
          URI uri=addressFactory.createSipURI(null,info.getProxy());
          authHeader.setURI(uri);
          authHeader.setNonce(nonce);
          authHeader.setResponse(response);
          return authHeader;
          }catch(Exception e){e.printStackTrace();return null;}
      }
      
      public URI getURI(String in){
          try{
          return addressFactory.createURI(in.split("<")[1].split(">")[0]);
          }catch(java.text.ParseException pe){pe.printStackTrace();}
          return null;
      }
      
      
      public UserAgentHeader getUserAgentHeader(String userAgent){
          UserAgentHeader userAgentHeader=null;
         try{
            List uaList=new ArrayList();
            uaList.add(userAgent);
            userAgentHeader=headerFactory.createUserAgentHeader(uaList);
         }catch(Exception e){e.printStackTrace();}
            return userAgentHeader;
      }
      
      
      
      public ClientTransaction createAck(ClientTransaction inviteTid,Response ok,String cType,String cSubType,String sdp){
                ClientTransaction ackTransaction=null;
       Request ack=null;
       try{
           ack=inviteTid.createAck();
       }catch(Exception e){System.out.println("xepxon maluka"+e);}
       
       if (sdp.equals("")){
           //callProvider.sendAck(inviteTid);
           try{
           ackTransaction=this.getClientTransaction(ack);
           
           }catch(Exception e){e.printStackTrace();}
           
       }else {
           //callProvider.sendAck(inviteTid,sdp,cType,csubType);
           try{
           
           try{
           ack.setContent((Object)sdp.getBytes(),this.createContentTypeHeader(cType,cSubType));
           }catch(java.text.ParseException pe){pe.printStackTrace();}
          // commFactory.changeDestinationPort(ack,portTo); 
           
           
           ackTransaction=this.getClientTransaction(ack);
           
           
           }catch(Exception e){e.printStackTrace();}
     
       }
       System.out.println("O ACK ANTES DE SE MEXER!");
       System.out.println(ackTransaction.getRequest());
 /*      
       URI reqURI=null;
       ListIterator routeSet = ok.getHeaders(RecordRouteHeader.NAME);
       ackTransaction.getRequest().removeHeader(RouteHeader.NAME);
       if (routeSet.hasPrevious()){
           RecordRouteHeader recordRoute=(RecordRouteHeader)routeSet.previous();
           reqURI=getURI(recordRoute.toString());
       }//o reqURI tem o último dos recordRoute.
           
       while(routeSet.hasPrevious()){
           RecordRoute recordRoute=(RecordRoute)routeSet.previous();
           Address routeAddress=null;
           try{
               System.out.println(recordRoute.toString());
            routeAddress=addressFactory.createAddress(recordRoute.toString());
           }catch(java.text.ParseException e){e.printStackTrace();}
           RouteHeader routeHeader=headerFactory.createRouteHeader(routeAddress);
           ackTransaction.getRequest().addHeader(routeHeader);
       }
       
       Address routeAddress=null;
       try{
            System.out.println(((ContactHeader)ok.getHeader(ContactHeader.NAME)).toString());
            routeAddress=addressFactory.createAddress(((ContactHeader)ok.getHeader(ContactHeader.NAME)).toString());
       }catch(java.text.ParseException e){e.printStackTrace();}
       RouteHeader routeHeader=headerFactory.createRouteHeader(routeAddress);
       ackTransaction.getRequest().addHeader(routeHeader);
     
      
       
       
       
       ackTransaction.getRequest().setRequestURI(reqURI);
       
       */
       
       
       

      return ackTransaction;
      }
      
}

