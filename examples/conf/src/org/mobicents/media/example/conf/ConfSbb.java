/*
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

package org.mobicents.media.example.conf;

import java.text.ParseException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.FactoryException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.FacilityException;
import javax.slee.facilities.NameAlreadyBoundException;
import javax.slee.facilities.NameNotBoundException;
import org.apache.log4j.Logger;
import org.mobicents.slee.resource.media.ratype.ConfMediaContext;
import org.mobicents.slee.resource.media.ratype.ConnectionEvent;
import org.mobicents.slee.resource.media.ratype.MediaConnection;
import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaProvider;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;
import org.mobicents.slee.resource.media.ratype.RtpMediaConnection;
import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipResourceAdaptorSbbInterface;

/**
 *
 * @author Oleg Kulikov
 * @version $Revision: 1.1 $
 */
public abstract class ConfSbb implements Sbb {
    
    private SbbContext sbbContext;
    private SipResourceAdaptorSbbInterface fp;
    private SipProvider sipProvider;
    private AddressFactory addressFactory;
    private HeaderFactory headerFactory;
    private MessageFactory messageFactory;
    private SipActivityContextInterfaceFactory acif;
    
    private MediaProvider mediaProvider;
    private MediaRaActivityContextInterfaceFactory mediaAcif;
    private ConfMediaContext mediaContext;
    private RtpMediaConnection connection;
    
    private ActivityContextNamingFacility namingFacility;
    private Dialog dialog;
    private String confName;
    private boolean isFirstParty = true;
    
    private Request invite;
    private ServerTransaction tx;
    private RequestEvent requestEvent;
    
    private Logger logger = Logger.getLogger(ConfSbb.class);
    
    /** Creates a new instance of ConfSbb */
    public ConfSbb() {
    }

    public void onInviteEvent(RequestEvent evt, ActivityContextInterface aci) {
        logger.info("Incoming call " + evt.getRequest().getHeader(FromHeader.NAME));
        
        requestEvent = evt;
        invite = evt.getRequest();
        tx = evt.getServerTransaction();
        
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
        ActivityContextInterface confAci = null;
        connection = (RtpMediaConnection) evt.getConnection();

        mediaContext = (ConfMediaContext) connection.getMediaContext();
        logger.info("MediaContext=" + mediaContext + " IsFirstParty = " + isFirstParty);
        
        if (isFirstParty) {
            try {
                confAci = mediaAcif.getActivityContextInterface(mediaContext);
            } catch (Exception ex) {
                logger.error("Unexpected error: ", ex);
                sendResponse(requestEvent, Response.SERVER_INTERNAL_ERROR);
                connection.release();
            }
            
            try {
                namingFacility.bind(confAci, confName);
                logger.info("Bound media context: " + mediaContext + " to name " + confName);
            } catch (Exception ex) {
                logger.error("Unexpected error: ", ex);
                sendResponse(requestEvent, Response.SERVER_INTERNAL_ERROR);
                connection.release();
            } 
        } else {
            confAci = namingFacility.lookup(confName);
        } 

        confAci.attach(sbbContext.getSbbLocalObject());
        
        //send ok
        String sdp = connection.getLocalDescriptor();
        try {
            ContentTypeHeader contentType = headerFactory.createContentTypeHeader("application", "sdp");
            
            String localAddress = sipProvider.getListeningPoints()[0].getIPAddress();
            int localPort = sipProvider.getListeningPoints()[0].getPort();
            
            Address contactAddress = addressFactory.createAddress("sip:" + localAddress + ":" + localPort);
            ContactHeader contact = headerFactory.createContactHeader(contactAddress);
            Response response = messageFactory.createResponse(Response.OK, invite, contentType, sdp.getBytes());
            response.setHeader(contact);
            tx.sendResponse(response);
            
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            sendResponse(requestEvent, Response.SERVER_INTERNAL_ERROR);
            connection.release();
        }
    }
    
    public void onByeEvent(RequestEvent evt, ActivityContextInterface aci) {
        logger.info("---- BYE-----");
        connection.release();
        
        if (mediaContext.getConnections().size() == 1) {
            try {
                namingFacility.unbind(confName);
            } catch (FacilityException ex) {
                ex.printStackTrace();
            } catch (TransactionRequiredLocalException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            } catch (NameNotBoundException ex) {
                ex.printStackTrace();
            }
            logger.info("Unbound conf activity from naming facility");
        }
        
        try {
            Response response = messageFactory.createResponse(Response.OK, evt.getRequest());
            evt.getServerTransaction().sendResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createMediaConnection(RequestEvent evt) throws ClassNotFoundException {
        String callID = getCallID(evt);
        System.out.println("-----CALL-ID: " + callID);
                
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
        
        ToHeader toHeader = (ToHeader) request.getHeader(ToHeader.NAME);
        confName = "conf:" + toHeader.getAddress().getURI().toString();
        logger.info("** CONFERENCE NAME=" + confName);
        
        ActivityContextInterface confActivity = namingFacility.lookup(confName);
        isFirstParty = confActivity == null;
        if (!isFirstParty) {
            //MediaContext confContext = (MediaContext) confActivity.getActivity();
            logger.info("Creating new connection in existing context");
            connection.init(mediaContext);
        } else {
            logger.info("Creating new connection in a new conf context");
            connection.init(MediaContext.CONFERENCE);
        }
    }
    
    private String getCallID(RequestEvent evt) {
        return evt.getRequest().getHeader(CallIdHeader.NAME).toString();
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
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        try {
            logger.info("Called setSbbContext Conf demo!!!: SbbID=" + sbbContext.getSbb());
            
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

    public void sbbExceptionThrown(Exception exception, Object object, ActivityContextInterface activityContextInterface) {
    }

    public void sbbRolledBack(RolledBackContext rolledBackContext) {
    }

}
