/*
 * File Name     : ResponseHandler.java
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

import java.net.InetAddress;
import javax.csapi.cc.jcc.JccConnection;
import javax.csapi.cc.jcc.JccEvent;

import javax.sip.ResponseEvent;

import javax.sip.message.Response;

import javax.sip.header.CallIdHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ViaHeader;

import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class ResponseHandler implements Runnable {
    private ResponseEvent event;
    private JccProviderImpl provider;
    private Logger logger = Logger.getLogger(ResponseHandler.class);
    
    /** Creates a new instance of ResponseHandler */
    public ResponseHandler(ResponseEvent event, JccProviderImpl provider) {
        this.event = event;
        this.provider = provider;
    }
    
    public void run() {
        Response response = event.getResponse();
        if (logger.isDebugEnabled()) {
            logger.debug("Running response handler for " +
                    response.getReasonPhrase());
        }
        
        CallIdHeader callIdHeader = (CallIdHeader)response.getHeader("Call-ID");
        
        ToHeader to = (ToHeader) response.getHeader("To");
        FromHeader from = (FromHeader) response.getHeader("From");
        
        String callID  = callIdHeader.getCallId();
        JccCallImpl call = provider.getCall(callID);
        
        if (call == null || call.connections == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Unknown callID " + callID);
            }
            return;
        }
        
        ViaHeader via = (ViaHeader) response.getHeader("Via");
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
            
            MessageHandler handler = new MessageHandler(connection, event);
            connection.processMessage(handler);
        } catch (Exception e) {
            logger.error("Could not process response " + response + ", caused by", e);
        }
        
    }
    
}
