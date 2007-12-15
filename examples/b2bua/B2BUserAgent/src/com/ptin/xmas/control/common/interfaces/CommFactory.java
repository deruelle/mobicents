package com.ptin.xmas.control.common.interfaces;
import javax.sip.message.*;
import javax.sip.header.*;
import javax.sip.address.*;
import java.util.*;
import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
/**
 *Interface used to construct messages to send thru an interface implemented by CommProviderImpl
 */
public interface CommFactory{

   public ClientTransaction createRegister(String userFrom, String proxyid, int regExp);
    

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
    public ClientTransaction createRequest(String from,String to,String reqUri,String domain,String contact,int port,String callId, String contentType, String contentSubType, String body,String method,String nameViewed,int c_Seq);
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
    public ClientTransaction createRequest(String from,String to,int port,String callId, String contentType, String contentSubType, String body,String method,String nameViewed,int c_Seq);
    /**
     *this function returns a ClientTransaction got from a CANCEL request based on a given request message (usually an invite)
     */
    public ClientTransaction createCancel(Request invite);
    /**
     *this function creates the Allow headers (also known as Accept)
     * Accept: INVITE, ACK, CANCEL, BYE, REFER, OPTIONS, NOTIFY, REGISTER, SUBSCRIBE
     */
    public AllowHeader createAllowHeader(String method);
    /**
     *function that returns a response to a given Request message with sdp
     */
    public Response createResponse(Request invite1,String sdp,String c_Type,String c_SubType);
    /**
     *function that returns a List with a RecordRouteHeader to a given URI (String)
     */
    public List createRecordRouters(String toRoute);
    /**
     *function that returns a ClientTransaction got from a Bye Request based on a given Request (usually a received invite)
     *the to field is used to create the request Uri line
     */
    public ClientTransaction createBye(Request invite,String to,String from,int port);
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
    public ClientTransaction createAck(Request invite,Response ok,String sdp,String contentType, String contentSubType,ContactHeader contactHeader);
    /**
     *function that returns a ClientTransaction got from an Ack Request based on a given Request and its Response
     *(usually an invite and its 200 OK)
     *
     *Request invite-> the request that generated the transaction
     *Response ok-> the response to be ACKed
     *int port-> the port where the message should arrive on its destination
     */
    public ClientTransaction createAck(Request invite,Response ok,ContactHeader contactHeader);
    /**
     *function that returns a RouteHeader
     *Parameters:
     *in--> a String Url
     */
   public RouteHeader createRouteHeader(String in);
    /**
     *Function the returns an ArrayList containing a ViaHeader corresponding to
     *the current stack
     */
    public ArrayList createViaHeaders();
    /**
     *Function that returns an UserAgentHeader based on a given String ua
     *
     *Attention: very useful when dealing with Messenger! I believe it will only
     *start a video call when theres something mentioned like "windows xx" on this header
     */
    public UserAgentHeader createUserAgent(String ua);
    /**
     *Function that returns a RecordRouteHeader based on String toRoute
     *
     *this header tells the other party where to send the next messages
     */
    public RecordRouteHeader getRecordRouteHeader(String toRoute);
    /**
     *Function that generates a RouteHeader based on a given toRoute and port
     *
     *String toRoute-> the destination of the message (it can be a proxy or the user agent)
     *int port-> the port where the message should arrive on its destination
     */
    public RouteHeader getRouteHeader(String toRoute,int toPort);
    /**
     *Function that returns a Response base on a given stats (ex. 200, 180, 486)
     *and the request that is to be answered
     */
    public Response createResponse(int stats,Request firstInvite);
    /**
     *Function that returns a ClientTransaction object based on a given request.
     */
    public ClientTransaction getClientTransaction(Request request);
    /**
     *Function that returns a ServerTransaction based on a given request.
     */
    public ServerTransaction getServerTransaction(Request request);
    /**
     *Function that returns a ContentTypeHeader based a given content type, and a 
     * content sub type.
     */
    public ContentTypeHeader createContentTypeHeader(String cType,String cSubType);
          public Request changeDestinationPort(Request req,int port);
          public ContactHeader createContactHeader(String contact);
          public ClientTransaction createSpecialRequest(Request firstRequest, Response myOK, String sdp, String contentType, String contentSubType, ContactHeader contactHeader,String method,int cSeq,String localTag,String remoteTag,String callId,String to,String userAgent);
          public SipURI createSipUri(String usr,String domain);
        public AuthorizationHeader createAuthorizationHeader(String userNam, String nonce,String response);
        public URI getURI(String in);
}