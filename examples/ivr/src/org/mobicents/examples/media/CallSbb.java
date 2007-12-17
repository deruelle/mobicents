/*
 * CallSbb.java
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

package org.mobicents.examples.media;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;

import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.FactoryException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.ActivityContextNamingFacility;

import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ratype.AnnouncementContext;
import org.mobicents.slee.resource.media.ratype.Cause;
import org.mobicents.slee.resource.media.ratype.ConnectionEvent;
import org.mobicents.slee.resource.media.ratype.IVRContext;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;
import org.mobicents.slee.resource.media.ratype.MediaProvider;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;
import org.mobicents.slee.resource.media.ratype.RtpMediaConnection;

import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipResourceAdaptorSbbInterface;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class CallSbb implements Sbb {
    
    private SbbContext sbbContext;
    private SipResourceAdaptorSbbInterface fp;
    private SipProvider sipProvider;
    private AddressFactory addressFactory;
    private HeaderFactory headerFactory;
    private MessageFactory messageFactory;
    private SipActivityContextInterfaceFactory acif;
    
    private MediaProvider mediaProvider;
    private MediaRaActivityContextInterfaceFactory mediaAcif;
    private IVRContext mediaContext;
    private URL welcomeMessage;
    private ActivityContextNamingFacility namingFacility;
    private Dialog dialog;
    
    private Logger logger = Logger.getLogger(CallSbb.class);
    
    
    /**
     * Creates a new instance of CallSbb
     */
    public CallSbb() {
    }
    
    public void onInviteEvent(RequestEvent evt, ActivityContextInterface aci) {
        setRequestEvent(evt);
        logger.info("Incoming call " + evt.getRequest().getHeader(FromHeader.NAME));
        
        try {
            dialog = sipProvider.getNewDialog(evt.getServerTransaction());
            ActivityContextInterface daci = acif.getActivityContextInterface(dialog);
            daci.attach(sbbContext.getSbbLocalObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        sendResponse(evt, Response.TRYING);
        try {
            sendResponse(evt, Response.RINGING);
            createMediaConnection(evt);
        } catch (Exception e) {
            logger.error("Failed to create media connection:", e);
            sendResponse(evt, Response.SERVER_INTERNAL_ERROR);
        }
    }
    
    public void onConnectionConnecting(ConnectionEvent evt, ActivityContextInterface aci) {
        System.out.println("------------ CONNECTING ------------");
        
        RtpMediaConnection connection = (RtpMediaConnection) evt.getConnection();
        mediaContext = (IVRContext) connection.getMediaContext();
        
        logger.info("Allocated media context: " + mediaContext);
        
        String sdp = connection.getLocalDescriptor();
        
        Request request = getRequestEvent().getRequest();
        ServerTransaction tx = getRequestEvent().getServerTransaction();
        
        try {
            ContentTypeHeader contentType = headerFactory.createContentTypeHeader("application", "sdp");
            
            String localAddress = sipProvider.getListeningPoints()[0].getIPAddress();
            int localPort = sipProvider.getListeningPoints()[0].getPort();
            
            Address contactAddress = addressFactory.createAddress("sip:" + localAddress + ":" + localPort);
            ContactHeader contact = headerFactory.createContactHeader(contactAddress);
            Response response = messageFactory.createResponse(Response.OK, request, contentType, sdp.getBytes());
            response.setHeader(contact);
            tx.sendResponse(response);
            
            ActivityContextInterface contextAci = mediaAcif.getActivityContextInterface(mediaContext);
            contextAci.attach(sbbContext.getSbbLocalObject());
            
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            sendResponse(getRequestEvent(), Response.SERVER_INTERNAL_ERROR);
            connection.release();
        }
    }
    
    public void onConnectionConnected(ConnectionEvent evt, ActivityContextInterface aci) {
        MediaConnection connection = evt.getConnection();
        MediaContext mediaContext = connection.getMediaContext();
        
        //Obtain ACI for media context
        ActivityContextInterface mediaContextAci = null;
        try {
            mediaContextAci = mediaAcif.getActivityContextInterface(mediaContext);
        } catch (FactoryException ex) {
            //never happen
        } catch (NullPointerException ex) {
            logger.error("Connection " + connection + " is not attached to any media context. Disconnecting...");
            //TODO send BYE
        } catch (UnrecognizedActivityException ex) {
            //never happen
        }
        
        
        //create welcome sbb
        SbbLocalObject welcomeSbb = null;
        try {
            ChildRelation relation = getWelcomeSbb();
            welcomeSbb = relation.create();
        } catch (Exception e) {
            e.printStackTrace();
            return;
            //TODO send cancel;
        }
        
        mediaContextAci.attach(welcomeSbb);
        aci.attach(welcomeSbb);
    }
    
    
    public void onPlayerFailed(MediaContextEvent evt, ActivityContextInterface aci) {
        logger.info("Player failed. Cause: " +  evt.getCause());
        sendBye();
    }
    
        
    public void onRecorderFailed(MediaContextEvent evt, ActivityContextInterface aci) {
        logger.info("Player failed. Cause: " +  evt.getCause());
        sendBye();
    }

    private void sendBye() {
        try {
            Request request = dialog.createRequest(Request.BYE);
            sipProvider.sendRequest(request);
        } catch (SipException e) {
            logger.error("Unexpected error", e);
        }
    }
    
    public void onByeEvent(RequestEvent evt, ActivityContextInterface aci) {
        logger.info("---- BYE-----");
        ActivityContextInterface[] activities = sbbContext.getActivities();
        for (int i = 0; i < activities.length; i++) {
            Object activity = activities[i].getActivity();
            if (activity instanceof MediaConnection) {
                ((MediaConnection) activity).release();
                logger.info("Media connection " + activity + " released");
            }
        }
        
        try {
            Response response = messageFactory.createResponse(Response.OK, evt.getRequest());
            evt.getServerTransaction().sendResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void sendResponse(RequestEvent evt, int cause) {
        Request request = evt.getRequest();
        ServerTransaction tx = evt.getServerTransaction();
        
        try {
            Response response = messageFactory.createResponse(cause, request);
            tx.sendResponse(response);
        } catch (ParseException e) {
            logger.warn("Invalid request: " + request);
        } catch (SipException e) {
            logger.error("Unexpected error:", e);
        } catch (InvalidArgumentException e) {
            //should never happen
        }
    }
    
    private String getCallID(RequestEvent evt) {
        return evt.getRequest().getHeader(CallIdHeader.NAME).toString();
    }
    
    private void createMediaConnection(RequestEvent evt) throws ClassNotFoundException {
        String callID = getCallID(evt);
        System.out.println("-----CALL-ID: " + callID);
        
        try {
            welcomeMessage = new URL("file:/C:/sounds/welcome.wav");
        } catch (Exception e) {
            logger.error("Could not load message file:", e);
        }
        
        Request request = evt.getRequest();
        byte[] data = request.getRawContent();
        
        String sdp = new String(data);
        RtpMediaConnection connection = null;
        
        connection = (RtpMediaConnection) mediaProvider.createConnection(MediaConnection.RTP);
        try {
            ActivityContextInterface aci = mediaAcif.getActivityContextInterface(connection);
            aci.attach(sbbContext.getSbbLocalObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        connection.setRemoteDescriptor(sdp);
        connection.init(MediaContext.IVR);
    }
    
    public abstract RequestEvent getRequestEvent();
    public abstract void setRequestEvent(RequestEvent evt);
    
    /** Relation for Welcome message SBB */
    public abstract ChildRelation getWelcomeSbb();
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        try {
            logger.info("Called setSbbContext IVR demo!!!: SbbID=" + sbbContext.getSbb());
            
            Context ctx = (Context) new InitialContext().lookup("java:comp/env");
            namingFacility = (ActivityContextNamingFacility) ctx.lookup("slee/facilities/activitycontextnaming");
            
            //initialize SIP API
            fp = (SipResourceAdaptorSbbInterface) ctx.lookup("slee/resources/jainsip/1.2/provider");
            sipProvider = fp.getSipProvider();
            addressFactory = fp.getAddressFactory();
            headerFactory = fp.getHeaderFactory();
            messageFactory = fp.getMessageFactory();
            acif = (SipActivityContextInterfaceFactory) ctx.lookup("slee/resources/jainsip/1.2/acifactory");
            
            //initilize Media API
            mediaProvider = (MediaProvider)
            ctx.lookup("slee/resources/media/1.0/provider");
            mediaAcif = (MediaRaActivityContextInterfaceFactory)
            ctx.lookup("slee/resources/media/1.0/acifactory");
        } catch (Exception ne) {
            logger.error("Could not set SBB context:", ne);
        }
    }
    
    
    public void unsetSbbContext() {
    }
    
    public void sbbCreate() throws CreateException {
    }
    
    public void sbbPostCreate() throws CreateException {
    }
    
    public void sbbActivate() {
    }
    
    public void sbbPassivate() {
    }
    
    public void sbbLoad() {
    }
    
    public void sbbStore() {
    }
    
    public void sbbRemove() {
    }
    
    public void sbbExceptionThrown(Exception exception, Object object,
            ActivityContextInterface activityContextInterface) {
    }
    
    public void sbbRolledBack(RolledBackContext rolledBackContext) {
    }
    
}
