/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/


package org.mobicents.conference;

import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;

import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;

import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.FacilityException;
import javax.slee.facilities.NameAlreadyBoundException;
import javax.slee.facilities.NameNotBoundException;

import org.mobicents.slee.resource.sip.SipActivityContextInterfaceFactory;
import org.mobicents.slee.resource.sip.SipResourceAdaptorSbbInterface;

import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.FactoryException;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.CreateException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceStartedEvent;
import javax.slee.serviceactivity.ServiceActivityFactory;
import javax.slee.ChildRelation;
import javax.slee.SbbLocalObject;

import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConflictingParameterException;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotificationRequestParms;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import net.java.slee.resource.mgcp.MgcpActivityContextInterfaceFactory;

import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class ConferenceSbb implements Sbb {
    
    private int GEN = 1;
    private int CALL_ID_GEN = 1;
    
    private SbbContext sbbContext;
    private Logger logger = Logger.getLogger(ConferenceSbb.class);
    
    private JainMgcpProvider mgcpProvider;
    private MgcpActivityContextInterfaceFactory mgcpAcif;
    
    private SipResourceAdaptorSbbInterface fp;
    private SipProvider provider;
    private AddressFactory addressFactory;
    private HeaderFactory headerFactory;
    private MessageFactory messageFactory;
    private SipActivityContextInterfaceFactory acif;
    private ActivityContextNamingFacility namingFacility;
    
    
    /** Creates a new instance of ConferenceSbb */
    public ConferenceSbb() {
    }
    
    public void onInviteEvent(RequestEvent evt, ActivityContextInterface aci) {
        try {
            logger.info("Trying...");
            sendTrying(evt);
        } catch (SipException e) {
            logger.error("Unexpected error. Caused by", e);
            return;
        }
        
        
        String confName = getConfName(evt.getRequest());
        ConfSbbActivityContextInterface conf = getConfActivityContext(confName);
        
        sendCreateConnection(conf, evt);
        logger.info("Sent CRCX request");
        
        try {
            sendRinging(evt);
            logger.info("Ringing....");
        } catch (SipException e) {
            sendReject(evt);
        }
    }
    
    public void onByeEvent(RequestEvent evt, ActivityContextInterface aci) {        
        String confName = getConfName(evt.getRequest());
        ConfSbbActivityContextInterface conf = getConfActivityContext(confName);
        
        logger.info("Conference name: " +  confName);
        
        ConnectionIdentifier connectionID = remove(conf, getLegID(evt.getRequest()));
        EndpointIdentifier endpointID = conf.getEndpoint();
        CallIdentifier callID = conf.getCallID();
        
        DeleteConnection deleteConnection = new DeleteConnection(this, callID, endpointID, connectionID );
                
        int txID = GEN++;
        
        deleteConnection.setTransactionHandle(txID);
        mgcpProvider.sendMgcpEvents(new JainMgcpEvent[]{deleteConnection});
        
        logger.info("Delete connection " + connectionID + " on " + endpointID);
        
        if (conf.getConnections().isEmpty()) {
            try {
                namingFacility.unbind(confName);
                logger.info("Unbound conference activity: " + confName);
            } catch (Exception ex) {
                logger.warn("Unexpected error. Caused by", ex);
            } 
        }
    }
    
    public void onCreateConnectionResponse(CreateConnectionResponse event, ActivityContextInterface aci) throws ParseException {
        logger.info("Receive CRCX response: " + event.getTransactionHandle());
        
        RequestEvent invite = asSbbActivityContextInterface(aci).getRequestEvent();
        ConfSbbActivityContextInterface conf = asSbbActivityContextInterface(aci).getConfActivityContextInterface();
        
        ReturnCode status = event.getReturnCode();
        
        switch (status.getValue()) {
            case ReturnCode.TRANSACTION_EXECUTED_NORMALLY :
                sendOk(invite, event.getLocalConnectionDescriptor().toString());
                logger.info("Sent OK response");
                
                ConnectionIdentifier connectionID = event.getConnectionIdentifier();
                
                append(conf, getLegID(invite.getRequest()), connectionID);
                conf.setEndpoint(event.getSpecificEndpointIdentifier());
                
                break;
            default :
                sendReject(invite);
        }
    }
    
    
    public abstract ConfSbbActivityContextInterface asSbbActivityContextInterface(ActivityContextInterface aci);
    
    /**
     * Appends reference to media connection for specified conf leg.
     *
     * @param conf the conference activity context interface.
     * @param confLeg the identifier of the conference leg.
     * @param connectionID the identifier of the media connection.
     */    
    private void append(ConfSbbActivityContextInterface conf, String confLeg, ConnectionIdentifier connectionID) {
        ConcurrentHashMap connections = conf.getConnections();
        connections.put(confLeg, connectionID);
    }

    /**
     * Removes reference to media connection for specified conf leg.
     *
     * @param conf the conference activity context interface.
     * @param confLeg the identifier of the conference leg.
     * @return  the identifier of the media connection.
     */    
    private ConnectionIdentifier remove(ConfSbbActivityContextInterface conf, String confLeg) {
        ConcurrentHashMap connections = conf.getConnections();
        return (ConnectionIdentifier) connections.remove(confLeg);
    }
    
    /**
     * Calculates conference call leg unique identifier.
     * 
     * @param request the sip request.
     * @return unique call leg identifier.
     */
    private String getLegID(Request request) {
        return request.getHeader("Call-ID").toString();
    }
    
    /**
     * Sends create connection request.
     *
     * @param conf the conference activity context interface.
     */
    private void sendCreateConnection(ConfSbbActivityContextInterface conf, RequestEvent invite) {
        CallIdentifier callID = conf.getCallID();
        EndpointIdentifier endpointID = conf.getEndpoint();
        
        CreateConnection createConnection = new CreateConnection(this,
                callID, endpointID, ConnectionMode.SendRecv);
        
        try {
            String sdp = new String(invite.getRequest().getRawContent());
            createConnection.setRemoteConnectionDescriptor(new ConnectionDescriptor(sdp));
        } catch (ConflictingParameterException e) {
            //should never happen
        }
        
        int txID = GEN++;
        createConnection.setTransactionHandle(txID);
        
        ActivityContextInterface tx = null;
        try {
            tx = mgcpAcif.getActivityContextInterface(new Integer(txID));
        } catch (FactoryException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } catch (UnrecognizedActivityException ex) {
            ex.printStackTrace();
        }
        
        asSbbActivityContextInterface(tx).setRequestEvent(invite);
        asSbbActivityContextInterface(tx).setConfActivityContextInterface(conf);
        
        tx.attach(sbbContext.getSbbLocalObject());
        logger.info("Attach SBB to MGCP TX activity");
        
        mgcpProvider.sendMgcpEvents(new JainMgcpEvent[]{createConnection});
    }
    
    /**
     * Gets conference activity.
     * If activity doesn't exists yet, create new and intialize.
     *
     * @param name the name of the conference.
     * @return conference activity context interface.
     */
    private ConfSbbActivityContextInterface getConfActivityContext(String name) {
        ActivityContextInterface aci = namingFacility.lookup(name);
        if (aci == null) {
            CallIdentifier callID = new CallIdentifier(Integer.toHexString(CALL_ID_GEN++));
            EndpointIdentifier endpointID = new EndpointIdentifier("conf/01", "localhost:2727");
            
            try {
                aci = mgcpAcif.getActivityContextInterface(callID);
            } catch (FactoryException ex) {
                // should never happen
            } catch (NullPointerException ex) {
                // should never happen
            } catch (UnrecognizedActivityException ex) {
                // should never happen
            }
            
            ConfSbbActivityContextInterface conf = asSbbActivityContextInterface(aci);
            
            conf.setCallID(callID);
            conf.setEndpoint(endpointID);
            conf.setConnections(new ConcurrentHashMap());
            
            try {
                namingFacility.bind(conf, name);
            } catch (TransactionRequiredLocalException ex) {
                // should never happen
            } catch (NullPointerException ex) {
                // should never happen
            } catch (IllegalArgumentException ex) {
                // should never happen
            } catch (NameAlreadyBoundException ex) {
                // should never happen
            }
            
            return conf;
        } else {
            return asSbbActivityContextInterface(aci);
        }
    }
    
    /**
     * Calculates conference name using SIP request event.
     * The conference name should be same for different requests for same conference.
     *
     * @param request SIP request.
     * @return the conference name.
     */
    private String getConfName(Request request) {
        return "conf:" + request.getHeader("To").toString();
    }
    
    /**
     * Sends TRYING response.
     *
     * @param invite the INVITE request event.
     */
    private void sendTrying(RequestEvent invite) throws SipException {
        ServerTransaction txn = invite.getServerTransaction();
        try {
            txn.sendResponse(messageFactory.createResponse(Response.TRYING, invite.getRequest()));
        } catch (ParseException ex) {
            //should never happen
        }
    }
    
    /**
     * Sends RINGING response.
     *
     * @param invite the INVITE request event.
     */
    private void sendRinging(RequestEvent invite) throws SipException {
        ServerTransaction txn = invite.getServerTransaction();
        try {
            txn.sendResponse(messageFactory.createResponse(Response.RINGING, invite.getRequest()));
        } catch (ParseException ex) {
            //should never happen
        }
    }
    
    /**
     * Sends SERVER_INTERNAL_ERROR response.
     *
     * @param invite the INVITE request event.
     */
    private void sendReject(RequestEvent invite)  {
        ServerTransaction txn = invite.getServerTransaction();
        try {
            txn.sendResponse(messageFactory.createResponse(Response.SERVER_INTERNAL_ERROR, invite.getRequest()));
        } catch (Exception ex) {
            logger.warn("Unexpected error:", ex);
        }
    }
    
    /**
     * Sends OK response.
     *
     * @param invite the INVITE request event.
     */
    private void sendOk(RequestEvent invite, String sdp)  {
        ServerTransaction txn = invite.getServerTransaction();
        byte[] raw = sdp.getBytes();
        
        try {
            Response response = messageFactory.createResponse(Response.OK, invite.getRequest());
            ToHeader to = (ToHeader) invite.getRequest().getHeader("To");
            ContactHeader contact = headerFactory.createContactHeader(to.getAddress());
            
            ContentLengthHeader contentLength = headerFactory.createContentLengthHeader(raw.length);
            ContentTypeHeader contentType = headerFactory.createContentTypeHeader("application", "sdp");
            
            response.setHeader(contact);
            response.setContent(raw, contentType);
            response.setContentLength(contentLength);
            txn.sendResponse(response);
        } catch (Exception ex) {
            logger.warn("Unexpected error:", ex);
        }
    }
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        try {
            logger.info("Called setSbbContext !!!");
            
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            namingFacility = (ActivityContextNamingFacility) myEnv.lookup("slee/facilities/activitycontextnaming");
            
            mgcpProvider = (JainMgcpProvider) myEnv.lookup("slee/resources/jainmgcp/1.0/provider");
            mgcpAcif = (MgcpActivityContextInterfaceFactory) myEnv.lookup("slee/resources/jainmgcp/1.0/acifactory");
            
            fp = (SipResourceAdaptorSbbInterface) myEnv.lookup("slee/resources/jainsip/1.1/provider");
            
            provider = fp.getSipProvider();
            addressFactory = fp.getAddressFactory();
            headerFactory = fp.getHeaderFactory();
            messageFactory = fp.getMessageFactory();
            acif = (SipActivityContextInterfaceFactory) myEnv.lookup("slee/resources/jainsip/1.1/acifactory");
        } catch (NamingException ne) {
            logger.warn("Could not set SBB context:" + ne.getMessage());
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
            ActivityContextInterface aci) {
    }
    
    public void sbbRolledBack(RolledBackContext rolledBackContext) {
    }
    
}
