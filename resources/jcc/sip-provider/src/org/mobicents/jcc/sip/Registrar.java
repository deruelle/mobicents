/*
 * File Name     : Registrar.java
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
import java.util.HashMap;
import java.text.ParseException;

import javax.sip.SipProvider;
import javax.sip.SipFactory;

import javax.sip.PeerUnavailableException;
import javax.sip.SipException;

import javax.sip.address.Address;
import javax.sip.address.URI;

import javax.sip.message.Response;
import javax.sip.message.Request;
import javax.sip.message.MessageFactory;

import javax.sip.header.HeaderFactory;
import javax.sip.header.ViaHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;

import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;


import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class Registrar {
    private Logger logger = Logger.getLogger(Registrar.class);
    private JccProviderImpl provider;
    private HashMap sessions = new HashMap();
    
    /** Creates a new instance of Registrar */
    public Registrar(JccProviderImpl provider) {
        this.provider = provider;
    }
    
    public synchronized void register(Request request) {
        SipProvider sipProvider = provider.sipProvider;
        SipFactory sipFactory = provider.sipFactory;
        
        FromHeader from = (FromHeader) request.getHeader("From");
        ViaHeader via = (ViaHeader) request.getHeader("Via");
        
        
        String originURI = from.getAddress().getURI().toString();
        
        if (logger.isDebugEnabled()) {
            logger.debug("Origin URI: " + originURI);
        }
        
        String uri = originURI.substring(0, originURI.indexOf('@') + 1) +
                via.getHost() + ":" + via.getPort();
        
        if (logger.isDebugEnabled()) {
            logger.debug("Registered URI: " + uri);
        }
        
        sessions.put(from.getAddress().getURI().toString(), uri);
        
        MessageFactory messageFactory = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Creating message factory");
            }
            messageFactory = sipFactory.createMessageFactory();
        } catch (PeerUnavailableException e) {
            logger.error("Faile to create message factory, caused by", e);
            return;
        }
        
        Response response = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Creating response OK");
            }
            response = messageFactory.createResponse(Response.OK, request);
            response.setHeader(request.getHeader("Contact"));
        } catch (Exception e) {
            logger.error("Could not send message, cause by", e);
        }
        
        ContentLengthHeader lengthHeader = (ContentLengthHeader) request.getHeader("Content-Length");
        int len = lengthHeader.getContentLength();
        if (len > 0) {
            byte[] sdp = request.getRawContent();
            try {
                SdpFactory factory = SdpFactory.getInstance();
                SessionDescription sd = factory.createSessionDescription(new String(sdp));
                
                HeaderFactory headerFactory = sipFactory.createHeaderFactory();
                
                ContentLengthHeader contentLength = headerFactory.createContentLengthHeader(sdp.length);
                ContentTypeHeader contentType = headerFactory.createContentTypeHeader("application", "sdp");
                
                response.setContent(sdp, contentType);
            } catch (Exception e) {
                logger.error("Could not parse SDP, caused by", e);
            }
        }
        try {
            sipProvider.sendResponse(response);
        } catch (Exception e) {
            logger.error("Could not send OK, caused by", e);
        }
        
    }
    
    public URI getURI(Address address) {
        String uri = (String)sessions.get(address.getURI().toString());
        if (uri == null) {
            return address.getURI();
        }
        try {
            return provider.addressFactory.createURI(uri);
        } catch (Exception e) {
            return null;
        }
    }
}
