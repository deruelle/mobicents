/*
 * File Name     : Handler.java
 *
 * The Java Call Control API for SIP
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */


package org.mobicents.jcc.sip;

import java.util.ArrayList;
import java.util.ListIterator;

import java.net.InetAddress;

import javax.csapi.cc.jcc.JccEvent;
import javax.csapi.cc.jcc.JccConnection;

import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.ClientTransaction;
import javax.sip.SipProvider;
import javax.sip.SipFactory;

import javax.sip.message.MessageFactory;
import javax.sip.message.Response;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sip.message.MessageFactory;

import javax.sip.SipProvider;
import javax.sip.SipFactory;
import javax.sip.ServerTransaction;
import javax.sip.ClientTransaction;

import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ViaHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ContentLengthHeader;

import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class RequestHandler implements Runnable {
    
    private RequestEvent event;
    private JccProviderImpl provider;
    private Logger logger = Logger.getLogger(RequestHandler.class);
    
    /** Creates a new instance of Handler */
    public RequestHandler(RequestEvent event, JccProviderImpl provider) {
        this.event = event;
        this.provider = provider;
    }
    
    public void run() {
        Request request = event.getRequest();
        if (logger.isDebugEnabled()) {
            logger.debug("Running request handler: " +
                    request.getMethod() + ", URI=" + request.getRequestURI());
        }
        
        String method = request.getMethod();
        
        CallIdHeader callIdHeader = (CallIdHeader)request.getHeader("Call-ID");
        String callID  = callIdHeader.getCallId();
        
        FromHeader from = ((FromHeader) request.getHeader("From"));
        ToHeader to = ((ToHeader) request.getHeader("To"));
        
        SipProvider sipProvider = provider.sipProvider;
        SipFactory sipFactory = provider.sipFactory;
        
        JccCallImpl call = null;
        
        if (method.equals(Request.REGISTER)) {
            provider.registrar.register(request);
        } else if (method.equals(Request.INVITE)) {
            ServerTransaction serverTransaction = event.getServerTransaction();
            if (serverTransaction == null) {
                try {
                    serverTransaction = provider.sipProvider.getNewServerTransaction(request);
                } catch (Exception e) {
                    logger.error("Could not create transaction", e);
                    return;
                }
            }
            
            
            
            try {
                MessageFactory messageFactory = sipFactory.createMessageFactory();
                Response response = messageFactory.createResponse(
                        Response.TRYING, request);
                to.setTag(Long.toHexString(System.currentTimeMillis()));
                serverTransaction.sendResponse(response);
                
            } catch (Exception e) {
                logger.error("Unexpected error, caused by", e);
                return;
            }
            
            call = provider.createCall(callID);
            call.serverTransaction = serverTransaction;
            call.request = request;
            
            String originatingAddress = from.getAddress().getURI().toString();
            String targetAddress = to.getAddress().getURI().toString();
            
            try {
                JccConnectionImpl con = (JccConnectionImpl)
                call.createConnection(originatingAddress, null, null, null);
                
                con.destinationAddress = provider.createAddress(targetAddress);
                con.startDialog();
                
                con.remoteSdp = new String(request.getRawContent());
                con.setState(JccConnection.IDLE, JccEvent.CAUSE_NEW_CALL);
            } catch (Exception e) {
                logger.error("Unxpected error: ", e);
            }
        } else {
            call = provider.getCall(callID);
/*            ViaHeader via = (ViaHeader) request.getHeader("Via");
            call = provider.getCall(callID);
            
            try {
                InetAddress local = InetAddress.getLocalHost();
                InetAddress remote = InetAddress.getByName(via.getHost());
                
                JccAddressImpl address = remote.equals(local) ?
                    provider.createAddress(to.getAddress().toString()):
                    provider.createAddress(from.getAddress().toString());
                JccConnectionImpl connection = (JccConnectionImpl) 
                    call.connections.get(address.getURI());
                
                if (connection == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Unknown party " + address);
                    }
                    return;
                }
 */
            try {
                ServerTransaction tx = event.getServerTransaction();
                if (tx == null) {
                    logger.error("Unknown transaction");
                    return;
                }
                
                JccConnectionImpl connection = call.getConnection(tx.getDialog());
                if (connection == null) {
                    logger.error("Unknown connection " + tx.getDialog());
                }
                MessageHandler handler = new MessageHandler(connection, event);
                connection.processMessage(handler);
            } catch (Exception e) {
                logger.error("Could not process request " + request + ", caused by", e);
            }
        }
        
    }
}
