/*
 * File Name     : JccConnectionImpl.java
 *
 * The Java Call Control RA
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
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;

import java.net.InetAddress;

import javax.csapi.cc.jcc.*;

import javax.sip.SipProvider;
import javax.sip.SipFactory;

import javax.sip.ClientTransaction;
import javax.sip.ServerTransaction;
import javax.sip.Dialog;

import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import javax.sip.address.Address;
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

import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import EDU.oswego.cs.dl.util.concurrent.Semaphore;

import org.mobicents.jcc.sip.MediaConnection;
import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class JccConnectionImpl implements Runnable, JccConnection {
    protected final static int FIRST_LEG = 1;
    protected final static int SECOND_LEG = 2;
    private final static int TIMEOUT = 7000;
    
    private JccAddressImpl address;
    private JccAddressImpl originatingAddress;
    protected JccAddressImpl destinationAddress;
    
    protected JccCallImpl call;
    private int leg;
    private int state = IDLE;
    private int cause = JccEvent.CAUSE_NORMAL;
    
    private boolean isBlocked = false;
    
    private QueuedExecutor stateQueue = new QueuedExecutor();
    private QueuedExecutor eventQueue = new QueuedExecutor();
    private QueuedExecutor messageQueue = new QueuedExecutor();
    
    private Semaphore semaphore = new Semaphore(0);
    
    private String tag;
    protected String remoteSdp;
    protected String localSdp;
    protected String id;
    
    protected Timer eventTimer = new Timer();
    protected Timer stateTimer = new Timer();
    
    private boolean isEventTimeout;
    private boolean isStateTimeout;
    private boolean isInUse = false;
    
    protected boolean isComplete;
    
    protected ClientTransaction clientTransaction;
    protected Dialog dialog;
    
    private Logger logger = Logger.getLogger(JccConnectionImpl.class);
    
    private class EventTimeoutTask extends TimerTask {
        public EventTimeoutTask() {
            isEventTimeout = false;
        }
        
        public void run() {
            isEventTimeout = true;
            semaphore.release();
        }
    }
    
    private class StateTimeoutTask extends TimerTask {
        public StateTimeoutTask() {
            isStateTimeout = false;
        }
        
        public void run() {
            if (state == DISCONNECTED) {
                releaseComplete();
                return;
            }
            
            try {
                logger.error(getLocalName() + ", State timer expired");
                release(JccEvent.CAUSE_TIMER_EXPIRY);
            } catch (Exception e) {
                logger.error(getLocalName() + ", Could not release normaly", e);
            }
        }
    }
    
    /** Creates a new instance of JccConnectionImpl */
    public JccConnectionImpl(JccCallImpl call, JccAddressImpl address,
            JccAddressImpl originatingAddress, int cause) {
        this.call = call;
        this.address = address;
        this.originatingAddress = originatingAddress;
        
        this.leg = address == originatingAddress ? FIRST_LEG : SECOND_LEG;
        
        if (originatingAddress == null && call.request != null) {
            FromHeader from = (FromHeader)call.request.getHeader("From");
            tag = from.getTag();
        } else {
            tag = Long.toString(System.currentTimeMillis());
        }
        
        this.tag = tag != null ? tag : Long.toString(System.currentTimeMillis());
    }
    
    public String getLocalSessionDescription() throws PrivilegeViolationException  {
        return localSdp;
    }
    
    public void setLocalSessionDescription(String localSdp) throws PrivilegeViolationException  {
        this.localSdp = localSdp;
    }
    
    public String getRemoteSessionDescription() throws PrivilegeViolationException  {
        return remoteSdp;
    }
    
    public void setRemoteSessionDescription(String remoteSdp) throws PrivilegeViolationException  {
        this.localSdp = localSdp;
    }
    
    public void answer() throws PrivilegeViolationException,
            ResourceUnavailableException, InvalidStateException,
            MethodNotSupportedException {
        if (leg == FIRST_LEG) {
            throw new PrivilegeViolationException(
                    PrivilegeViolationException.ORIGINATOR_VIOLATION,
                    "Trying to answer an outgoing call leg");
        }
        
        boolean validState =
                (state == JccConnection.CALL_DELIVERY) ||
                (state == JccConnection.ALERTING);
        
        if (!validState) {
            throw new InvalidStateException(this,
                    InvalidStateException.CONNECTION_OBJECT, state,
                    "Connection should be in CALL_DELIVERY or ALERTING state");
        }
        
        if (call.getState() != JccCall.ACTIVE) {
            throw new InvalidStateException(call,
                    InvalidStateException.CALL_OBJECT, call.getState(),
                    "Call should be in ACTIVE state");
        }
        
        if (call.provider.getState() != JccProvider.IN_SERVICE) {
            throw new InvalidStateException(call.provider,
                    InvalidStateException.PROVIDER_OBJECT, call.provider.getState(),
                    "Provider should be in IN_SERVICE state");
        }
        
        
    }
    
    public void attachMedia() throws PrivilegeViolationException,
            ResourceUnavailableException, InvalidStateException {
    }
    
    public void continueProcessing() throws PrivilegeViolationException,
            ResourceUnavailableException, InvalidStateException {
        if (logger.isDebugEnabled()) {
            logger.debug(getLocalName() + ", Resuming processing");
        }
        isBlocked = false;
        cause = JccConnectionEvent.CAUSE_NORMAL;
        this.semaphore.release();
        
        if (logger.isDebugEnabled()) {
            logger.debug(getLocalName() + ", Resumed processing");
        }
    }
    
    public void detachMedia() throws PrivilegeViolationException,
            ResourceUnavailableException, InvalidStateException {
    }
    
    public JccAddress getAddress() {
        return address;
    }
    
    public JccCall getCall() {
        return call;
    }
    
    public String getDestinationAddress() {
        return destinationAddress != null ? destinationAddress.toString() : null;
    }
    
    public String getLastAddress() {
        return null;
    }
    
    public MidCallData getMidCallData() throws InvalidStateException, ResourceUnavailableException, MethodNotSupportedException {
        return null;
    }
    
    public String getOriginalAddress() {
        return null;
    }
    
    public JccAddress getOriginatingAddress() {
        return originatingAddress != null ? originatingAddress : address;
    }
    
    public String getRedirectedAddress() {
        return null;
    }
    
    public int getState() {
        return state;
    }
    
    public boolean isBlocked() {
        return isBlocked;
    }
    
    /**
     * (Non-Javadoc).
     *
     * @see javax.csapi.cc.jcc.JccConnection#release(int).
     */
    public void release(int causeCode) throws PrivilegeViolationException,
            ResourceUnavailableException, InvalidStateException,
            InvalidArgumentException {
        stateQueue.restart();
        eventQueue.restart();
        
        if (isInUse) {
            bye();
        } else {
            cancel();
        }
        
        setState(DISCONNECTED, cause);
    }
    
    public void routeConnection(boolean attachMedia) throws InvalidStateException,
            ResourceUnavailableException, PrivilegeViolationException,
            MethodNotSupportedException, InvalidPartyException,
            InvalidArgumentException {
        try {
            SipProvider sipProvider = call.provider.sipProvider;
            SipFactory sipFactory = call.provider.sipFactory;
            
            MessageFactory messageFactory = sipFactory.createMessageFactory();
            HeaderFactory headerFactory = sipFactory.createHeaderFactory();
            
            CallIdHeader callID = headerFactory.createCallIdHeader(this.call.callID);
            FromHeader from = headerFactory.createFromHeader(originatingAddress.address, tag);
            ToHeader to = headerFactory.createToHeader(address.address, null);
            CSeqHeader cseq = headerFactory.createCSeqHeader(call.cseq, Request.INVITE);
            MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
            
            ArrayList userAgents = new ArrayList();
            userAgents.add("JSLEE/Mobicents");
            UserAgentHeader userAgent = headerFactory.createUserAgentHeader(userAgents);
            
            ArrayList list = new ArrayList();
            list.add(headerFactory.createViaHeader(
                    InetAddress.getLocalHost().getHostAddress(),
                    sipProvider.getListeningPoint().getPort(),
                    sipProvider.getListeningPoint().getTransport(), null));
            
            
            int port = call.provider.sipProvider.getListeningPoint().getPort();
            String host = InetAddress.getLocalHost().getHostAddress();
            
            String uri = "sip:jslee@" + host + ":" + port;
            Address ctc = sipFactory.createAddressFactory().createAddress(uri);
            
            ContactHeader contact = headerFactory.createContactHeader(ctc);
            
            Request request = messageFactory.createRequest(
                    call.provider.registrar.getURI(address.address),
                    Request.INVITE, callID, cseq, from, to, list, maxForwards);
            request.setHeader(userAgent);
            request.setHeader(contact);
            byte[] sd = localSdp.getBytes();
            
            ContentLengthHeader contentLength = headerFactory.createContentLengthHeader(sd.length);
            ContentTypeHeader contentType = headerFactory.createContentTypeHeader("application", "sdp");
            request.setContent(sd, contentType);
            
            if (logger.isDebugEnabled()) {
                logger.debug(getLocalName() + ", routeConnection: sending INVITE");
            }
            
            clientTransaction = sipProvider.getNewClientTransaction(request);
            clientTransaction.sendRequest();
            
            continueProcessing();
        } catch (Exception e) {
            logger.error("Unxpected error, caused by ", e);
            setState(FAILED, JccEvent.CAUSE_GENERAL_FAILURE);
        }
    }
    
    public void selectRoute(String address) throws MethodNotSupportedException,
            InvalidStateException, ResourceUnavailableException,
            PrivilegeViolationException, InvalidPartyException {
        try {
            destinationAddress = call.provider.createAddress(address);
        } catch (Exception e){
            throw new InvalidPartyException(
                    InvalidPartyException.DESTINATION_PARTY, e.getMessage());
        }
        continueProcessing();
    }
    
    /**
     * Suspends processing.
     *
     * When the implementation suspends processing, only the traversal of the
     * finite state machine by the corresponding JccConnection object is suspended.
     * All external events received for the blocked JccConnection object would
     * have to be queued and handled. Thus, the finite state machine state of
     * the corresponding JccConnection object does not change when
     * processing is suspended.
     */
    protected void block() {
        isBlocked = true;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug( getLocalName() + ", Block processing");
            }
            
            eventTimer = new Timer();
            eventTimer.schedule(new EventTimeoutTask(), TIMEOUT);
            
            semaphore.acquire();
            
        } catch (Exception e) {
            logger.error("Interrupted, caused by", e);
            setState(FAILED, JccEvent.CAUSE_GENERAL_FAILURE);
        }
        
        stopEventTimer();
    }
    
    protected void stopEventTimer() {
        try {
            eventTimer.cancel();
        } catch (Exception e) {
            //do nothing
        }
    }
    
    protected void stopStateTimer() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(getLocalName() + ", stop timer");
            }
            stateTimer.cancel();
        } catch (Exception e) {
            logger.error(getLocalName() + ", Could not stop timer", e);
        }
    }
    
    protected void resetStateTimer(int timeout) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(getLocalName() + ", reset timer");
            }
            stateTimer.cancel();
            
            stateTimer = new Timer();
            stateTimer.schedule(new StateTimeoutTask(), timeout);
        } catch (Exception e) {
            logger.error("Could not reset timer, ", e);
        }
    }
    
    /**
     * Changes states of the FSM.
     *
     * @param state the new state of the FSM.
     * @param cause the cause associated with this change.
     */
    protected void setState(int state, int cause) {
        if (this.state == DISCONNECTED) {
            return;
        }
        
        if (this.state == FAILED && state != DISCONNECTED) {
            return;
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug(getLocalName() + ", transits to state " +
                    getStateName(state));
        }
        
        this.state = state;
        this.cause = cause;
        
        try {
            stateQueue.execute(this);
        } catch (InterruptedException ie) {
            logger.error("Unexpected error:", ie);
        }
    }
    
    /**
     * Returns the name of the specified state of the FSM.
     *
     * @param state the state of the FSM.
     * @return the name of the specified state.
     */
    public String getStateName(int state) {
        switch (state) {
            case JccConnection.IDLE :
                return "JccConnection.IDLE";
            case JccConnection.ADDRESS_ANALYZE :
                return  "JccConnection.ADDRESS_ANALYZE";
            case JccConnection.ADDRESS_COLLECT :
                return  "JccConnection.ADDRESS_COLLECT";
            case JccConnection.ALERTING :
                return  "JccConnection.ALERTING";
            case JccConnection.AUTHORIZE_CALL_ATTEMPT :
                return  "JccConnection.AUTHORIZE_CALL_ATTEMPT";
            case JccConnection.CALL_DELIVERY :
                return  "JccConnection.CALL_DELIVERY";
            case JccConnection.CONNECTED :
                return  "JccConnection.CONNECTED";
            case JccConnection.DISCONNECTED :
                return "JccConnection.DISCONNECTED";
            case JccConnection.FAILED :
                return "JccConnection.FAILED";
            default :
                return "Unknown";
        }
    }
    
    protected void fireEvent(int eventID, int cause) throws TimeoutException {
        JccConnectionEvent event =
                new JccConnectionEventImpl(eventID, this, cause);
        this.cause = cause;
        fireConnectionEvent(event);
    }
    
    protected void fireConnectionEvent(JccConnectionEvent event) throws TimeoutException {
        //this.event = event;
        fireConnectionEvent(call.connectionListeners, event);
        fireConnectionEvent(call.provider.connectionListeners, event);
    }
    
    private void fireConnectionEvent(Vector listeners, JccConnectionEvent event)
    throws TimeoutException {
        int count = listeners.size();
        for (int i = 0; i < count; i++) {
            Object[] ls = (Object[])listeners.get(i);
            
            JccConnectionListener listener = (JccConnectionListener) ls[1];
            EventFilter filter = (EventFilter) ls[0];
            
            switch (filter.getEventDisposition(event)) {
                case EventFilter.EVENT_DISCARD :
                    if (logger.isDebugEnabled()) {
                        logger.debug(getLocalName() + ", fireConnectionEvent: event=" +
                                event +
                                " [event disposition: EVENT_DISCARD]");
                    }
                    break;
                case EventFilter.EVENT_BLOCK :
                    if (logger.isDebugEnabled()) {
                        logger.debug(getLocalName() + ", fireConnectionEvent: event=" +
                                event +
                                "[event disposition: EVENT_BLOCK]");
                    }
                    
                    try {
                        eventQueue.execute(
                                new EventProducer(listener, event));
                    } catch (InterruptedException e) {
                        cause = JccConnectionEvent.CAUSE_GENERAL_FAILURE;
                    }
                    
                    block();
                    
                    if (isEventTimeout) {
                        throw new TimeoutException();
                    }
                    
                    break;
                case EventFilter.EVENT_NOTIFY :
                    if (logger.isDebugEnabled()) {
                        logger.debug(getLocalName() + ", fireConnectionEvent: event=" +
                                event +
                                " [event disposition: EVENT_NOTIFY]");
                    }
                    
                    try {
                        eventQueue.execute(
                                new EventProducer(listener, event));
                    } catch (InterruptedException e) {
                        cause = JccConnectionEvent.CAUSE_GENERAL_FAILURE;
                    }
                    
                    break;
                default :
                    logger.warn("fireConnectionEvent: event=" + event + ", " +
                            "filter event disposition is unknown, Ignore");
            }
        }
    }
    
    protected void ringing() {
        SipProvider sipProvider = call.provider.sipProvider;
        SipFactory sipFactory = call.provider.sipFactory;
        
        try {
            MessageFactory messageFactory = sipFactory.createMessageFactory();
            HeaderFactory headerFactory = sipFactory.createHeaderFactory();
            
            Response response = messageFactory.createResponse(
                    Response.RINGING, call.request);
            call.serverTransaction.sendResponse(response);
            
            response = messageFactory.createResponse(
                    Response.SESSION_PROGRESS, call.request);
            //            byte[] sdp = localSdp.getBytes();
            
            //            ContentLengthHeader contentLength = headerFactory.createContentLengthHeader(sdp.length);
            //            ContentTypeHeader contentType = headerFactory.createContentTypeHeader("application", "sdp");
            
            //            response.setContent(sdp, contentType);
            //            call.serverTransaction.sendResponse(response);
        } catch (Exception e) {
            logger.error("Unexpected error, caused by", e);
            setState(FAILED, JccEvent.CAUSE_GENERAL_FAILURE);
        }
    }
    
    protected void startDialog() {
        if (getAddress() != getOriginatingAddress()) {
            dialog = clientTransaction.getDialog();
        } else {
            dialog = call.serverTransaction.getDialog();
        }
    }
    
    protected void ok(ServerTransaction tx, Request request, String descriptor) {
        SipProvider sipProvider = call.provider.sipProvider;
        SipFactory sipFactory = call.provider.sipFactory;
        
        try {
            MessageFactory messageFactory = sipFactory.createMessageFactory();
            HeaderFactory headerFactory = sipFactory.createHeaderFactory();
            
            Response response = messageFactory.createResponse(Response.OK, request);

            int port = call.provider.sipProvider.getListeningPoint().getPort();
            String host = InetAddress.getLocalHost().getHostAddress();
            
            String uri = "sip:" + host + ":" + port;
            Address address = sipFactory.createAddressFactory().createAddress(uri);
            
            ContactHeader contact = headerFactory.createContactHeader(address);
                
            response.setHeader(contact);
            
            if (descriptor != null) {
                byte[] sdp = descriptor.getBytes();
            
                ContentLengthHeader contentLength = headerFactory.createContentLengthHeader(sdp.length);
                ContentTypeHeader contentType = headerFactory.createContentTypeHeader("application", "sdp");
            
                response.setContent(sdp, contentType);
            }
            response.removeHeader("Max-Forwards");
            tx.sendResponse(response);
            continueProcessing();
        } catch (Exception e) {
            logger.error("Unexpected error, caused by", e);
            setState(FAILED, JccEvent.CAUSE_GENERAL_FAILURE);
        }
    }
    

    protected void sessionProgress(String descriptor) {
        SipProvider sipProvider = call.provider.sipProvider;
        SipFactory sipFactory = call.provider.sipFactory;
        
        try {
            MessageFactory messageFactory = sipFactory.createMessageFactory();
            HeaderFactory headerFactory = sipFactory.createHeaderFactory();
            
            Response response = messageFactory.createResponse(Response.SESSION_PROGRESS, 
                    call.request);

                
            
            if (descriptor != null) {
                byte[] sdp = descriptor.getBytes();
            
                ContentLengthHeader contentLength = headerFactory.createContentLengthHeader(sdp.length);
                ContentTypeHeader contentType = headerFactory.createContentTypeHeader("application", "sdp");
            
                response.setContent(sdp, contentType);
            }
            call.serverTransaction.sendResponse(response);
            continueProcessing();
        } catch (Exception e) {
            logger.error("Unexpected error, caused by", e);
            setState(FAILED, JccEvent.CAUSE_GENERAL_FAILURE);
        }
    }
    
    protected void ack() {
        SipProvider sipProvider = call.provider.sipProvider;
        SipFactory sipFactory = call.provider.sipFactory;
        try {
            Request request = clientTransaction.createAck();
            clientTransaction = sipProvider.getNewClientTransaction(request);
            clientTransaction.sendRequest();
            
/*            MessageFactory messageFactory = sipFactory.createMessageFactory();
            HeaderFactory headerFactory = sipFactory.createHeaderFactory();
            
            CallIdHeader callID = headerFactory.createCallIdHeader(this.call.callID);
            
            FromHeader from = null;
            ToHeader to = null;
            
            if (getAddress() == getOriginatingAddress()) {
                from = headerFactory.createFromHeader(destinationAddress.address, null);
                to = headerFactory.createToHeader(address.address, null);
            } else {
                from = headerFactory.createFromHeader(originatingAddress.address, null);
                to = headerFactory.createToHeader(address.address, null);
            }
            
            CSeqHeader cseq = headerFactory.createCSeqHeader(call.cseq++, Request.ACK);
            MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
            
            ArrayList userAgents = new ArrayList();
            userAgents.add("JSLEE/Mobicents");
            UserAgentHeader userAgent = headerFactory.createUserAgentHeader(userAgents);
            
            ArrayList list = new ArrayList();
            list.add(headerFactory.createViaHeader(
                    InetAddress.getLocalHost().getHostAddress(),
                    sipProvider.getListeningPoint().getPort(),
                    sipProvider.getListeningPoint().getTransport(), null));
            
            
            Request request = messageFactory.createRequest(
                    call.provider.registrar.getURI(address.address),
                    Request.ACK, callID, cseq, from, to, list, maxForwards);
            request.setHeader(userAgent);
            
            if (logger.isDebugEnabled()) {
                logger.debug(getLocalName() + ", ack: sending ACK");
            }
            
            clientTransaction = sipProvider.getNewClientTransaction(request);
            clientTransaction.sendRequest();
 */
        } catch (Exception e) {
            logger.error("Unxpected error, caused by ", e);
        }
    }
    
    protected void bye() {
        try {
            SipProvider sipProvider = call.provider.sipProvider;
            SipFactory sipFactory = call.provider.sipFactory;
            
            MessageFactory messageFactory = sipFactory.createMessageFactory();
            HeaderFactory headerFactory = sipFactory.createHeaderFactory();
            
            CallIdHeader callID = headerFactory.createCallIdHeader(this.call.callID);
            
            FromHeader from = null;
            ToHeader to = null;
            
            if (getAddress() == getOriginatingAddress()) {
                from = headerFactory.createFromHeader(destinationAddress.address, null);
                to = headerFactory.createToHeader(address.address, null);
            } else {
                from = headerFactory.createFromHeader(originatingAddress.address, null);
                to = headerFactory.createToHeader(address.address, null);
            }
            
            CSeqHeader cseq = headerFactory.createCSeqHeader(call.cseq++, Request.BYE);
            MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
            
            ArrayList userAgents = new ArrayList();
            userAgents.add("JSLEE/Mobicents");
            UserAgentHeader userAgent = headerFactory.createUserAgentHeader(userAgents);
            
            ArrayList list = new ArrayList();
            list.add(headerFactory.createViaHeader(
                    InetAddress.getLocalHost().getHostAddress(),
                    sipProvider.getListeningPoint().getPort(),
                    sipProvider.getListeningPoint().getTransport(), null));
            
            
            Request request = messageFactory.createRequest(
                    call.provider.registrar.getURI(address.address),
                    Request.BYE, callID, cseq, from, to, list, maxForwards);
            request.setHeader(userAgent);
            
            if (logger.isDebugEnabled()) {
                logger.debug(getLocalName() + ", releaseConnection: sending BYE");
            }
            
            clientTransaction = sipProvider.getNewClientTransaction(request);
            clientTransaction.sendRequest();
        } catch (Exception e) {
            logger.error("Unxpected error, caused by ", e);
        }
    }
    
    protected void cancel() {
        SipProvider sipProvider = call.provider.sipProvider;
        SipFactory sipFactory = call.provider.sipFactory;
        
        try {
            Request request = clientTransaction.createCancel();
            clientTransaction = sipProvider.getNewClientTransaction(request);
            clientTransaction.sendRequest();
            
            if (logger.isDebugEnabled()) {
                logger.debug(getLocalName() + ", releaseConnection: sending CANCEL");
            }
        } catch (Exception e) {
            logger.error("Unxpected error, caused by ", e);
        }
    }
    
    /**
     * Release entire call.
     *
     * @param causeCode an integer that represents a cause code.
     */
    protected void releaseCall(int causeCode) {
        try {
            call.release(cause);
        } catch (Exception e) {
            logger.warn("Could not release call gracefully", e);
        }
    }
    /**
     * Implements the Finite State Machine.
     *
     * An event is generated corresponding to every state change in the
     * finite state machine (FSM). All events pertaining to the JccConnection
     * object are reported via the JccCallListener interface on the JccCall
     * object associated with this JccConnection.
     *
     * All the events on the JccConnection are expected to be blockable.
     * In other words, after sending each event to the listener the
     * implementation can either suspend processing or continue with processing.
     * The implementation suspends processing if the event is to be fired in
     * a blocking mode and the implementation continues with processing if
     * the event is to be fired in a non-blocking mode.
     * When the implementation suspends processing, only the traversal of the
     * finite state machine by the corresponding JccConnection object is
     * suspended. All external events received for the blocked JccConnection
     * object would have to be queued and handled. Thus, the finite state
     * machine state of the corresponding JccConnection object does not
     * change when processing is suspended.
     */
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug(getLocalName() + ", FSM: processing state " + getStateName(state));
        }
        
        switch (state) {
            //This state is the initial state for all new Connections.
            //Connections which are in the IDLE state are not actively part
            //of a telephone call, yet their references to the Call and
            //Address objects are valid. Connections typically do not stay
            //in the IDLE state for long, quickly transitioning to other states
            case JccConnection.IDLE :
                try {
                    fireEvent(JccConnectionEvent.CONNECTION_CREATED,
                            JccConnectionEvent.CAUSE_NORMAL);
                } catch (TimeoutException e) {
                    setState(FAILED, JccEvent.CAUSE_TIMER_EXPIRY);
                }
                
                if (getAddress() != getOriginatingAddress()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Terminated connection exits from IDLE state, " +
                                " Originated connection transits into CONNECTED state");
                    }
                    JccConnectionImpl con = (JccConnectionImpl) call.connections.get(
                            ((JccAddressImpl)getOriginatingAddress()).getURI());
                    con.setState(CONNECTED, JccEvent.CAUSE_NORMAL);
                }
                
                setState(AUTHORIZE_CALL_ATTEMPT, JccEvent.CAUSE_NORMAL);
                resetStateTimer(TIMEOUT);
                break;
                
                
                //This state implies that the originating or terminating
                //terminal needs to be authorized for the call.
            case JccConnection.AUTHORIZE_CALL_ATTEMPT :
                try {
                    fireEvent(JccConnectionEvent.CONNECTION_AUTHORIZE_CALL_ATTEMPT,
                            JccConnectionEvent.CAUSE_NORMAL);
                    if (getAddress() == getOriginatingAddress()) {
                        setState(ADDRESS_COLLECT, JccEvent.CAUSE_NORMAL);
                    } else {
                        setState(CALL_DELIVERY, JccEvent.CAUSE_NORMAL);
                    }
                } catch (TimeoutException e) {
                    setState(FAILED, JccEvent.CAUSE_TIMER_EXPIRY);
                }
                
                resetStateTimer(TIMEOUT);
                break;
                
                //This state implies that more information needs to be collected
                //for the call
            case JccConnection.ADDRESS_COLLECT :
                try {
                    fireEvent(JccConnectionEvent.CONNECTION_ADDRESS_COLLECT,
                            JccConnectionEvent.CAUSE_NORMAL);
                    setState(ADDRESS_ANALYZE, JccEvent.CAUSE_NORMAL);
                } catch (TimeoutException e) {
                    setState(FAILED, JccEvent.CAUSE_TIMER_EXPIRY);
                }
                
                resetStateTimer(TIMEOUT);
                break;
                
                // This state implies that the address needs to be analyzed to
                //determine the resources needed.
            case JccConnection.ADDRESS_ANALYZE :
                try {
                    fireEvent(JccConnectionEvent.CONNECTION_ADDRESS_ANALYZE,
                            JccConnectionEvent.CAUSE_NORMAL);
                    setState(CALL_DELIVERY, JccEvent.CAUSE_NORMAL);
                } catch (TimeoutException e) {
                    setState(FAILED, JccEvent.CAUSE_TIMER_EXPIRY);
                }
                
                resetStateTimer(TIMEOUT);
                break;
                
                //This state implies that the resources (such as network links)
                //needed to deliver signaling messages are being used.
            case JccConnection.CALL_DELIVERY :
                try {
                    fireEvent(JccConnectionEvent.CONNECTION_CALL_DELIVERY,
                            JccEvent.CAUSE_NORMAL);
                } catch (TimeoutException e) {
                    setState(FAILED, JccEvent.CAUSE_TIMER_EXPIRY);
                    break;
                }
                
                if (getAddress() == getOriginatingAddress()) {
                    //Starting Terminated Connection object progresing
                    ringing();
                    if (logger.isDebugEnabled()) {
                        logger.debug(getLocalName() + " starting terminated connection progressing");
                    }
                    JccConnectionImpl con = (JccConnectionImpl)
                    call.connections.get(destinationAddress.toString());
                    if (con == null) {
                        try {
                            con = (JccConnectionImpl)call.createConnection(
                                    destinationAddress.toString(),
                                    address.toString(),
                                    null, null);
                        } catch (Exception e) {
                            logger.error("Could not create connection", e);
                        }
                    }
                    con.setState(IDLE, JccEvent.CAUSE_NEW_CALL);
                }
                
                resetStateTimer(TIMEOUT);
                break;
                
                //This state implies that the Address is being notified
                //of an incoming call.
            case JccConnection.ALERTING :
                try {
                    fireEvent(JccConnectionEvent.CONNECTION_ALERTING, cause);
                } catch (TimeoutException e) {
                    setState(FAILED, JccEvent.CAUSE_TIMER_EXPIRY);
                }
                
                resetStateTimer(60000);
                break;
                
                //This state implies that a Connection and its Address is
                //actively part of a telephone call. In common terms, two
                //people talking to one another are represented by two
                //Connections in the CONNECTED state
            case JccConnection.CONNECTED :
                stopStateTimer();
                try {
                    fireEvent(JccConnectionEvent.CONNECTION_CONNECTED,
                            JccEvent.CAUSE_NORMAL);
                } catch (TimeoutException e) {
                    //do nothing
                }
                
                if (getAddress() != getOriginatingAddress()) {
                    JccConnectionImpl con = (JccConnectionImpl) 
                        call.connections.get(originatingAddress.getURI());
                    ok(call.serverTransaction, call.request, con.localSdp);
                    isInUse = true;
                }
                break;
                
                //This state implies it is no longer part of the telephone call,
                //although its references to Call and Address still remain valid.
                //A Connection in this state is interpreted as once previously
                //belonging to this telephone call.
            case JccConnection.DISCONNECTED :
                resetStateTimer(TIMEOUT);
                                
                try {
                    fireEvent(JccConnectionEvent.CONNECTION_DISCONNECTED,
                            cause);
                } catch (TimeoutException e) {
                    //do nothing
                }

                //eventQueue.shutdownNow();
                //eventTimer.cancel();
                //eventTimer = null;
                
                if (isComplete) {
                    releaseComplete();
                }
                
                break;
                
                //This state indicates that a Connection to that end of the
                //call has failed for some reason. One reason why a Connection
                //would be in the FAILED state is because the party was busy.
                //Connections that are in the FAILED state are still connected
                //to the call.
            case JccConnection.FAILED :
                stopStateTimer();
                stateQueue.restart();
                try {
                    fireEvent(JccConnectionEvent.CONNECTION_FAILED, 
                            JccEvent.CAUSE_NORMAL);
                } catch (TimeoutException e) {
                    //do nothing
                }
                setState(DISCONNECTED, cause);
                break;
            default :
                return;
        }
    }
    
    protected void releaseComplete() {
        clientTransaction = null;
        call.connections.remove(address.toString());
        
        //stopStateTimer();
        //stateTimer = null;
        
        logger.info(getLocalName() + ", Release complete");
        
        if (getAddress() == getOriginatingAddress() && call.getConnections().length > 0) {
            releaseCall(cause);
        } else if (call.getConnections().length < 2) {
            try {
                call.release(cause);
            } catch (Exception e) {
                
            }
        } else if (call.getConnections().length == 0) {
            call.releaseComplete();
            call = null;
        }
    }
    
    protected void processMessage(MessageHandler handler) {
        if (logger.isDebugEnabled()) {
            logger.debug(getLocalName() + " Queueing response " + handler);
        }
        
        try {
            messageQueue.execute(handler);
        } catch (InterruptedException e) {
            logger.error(getLocalName() + ", Could not process message", e);
        }
    }
    
    public String getLocalName() {
        String s = (getAddress() == getOriginatingAddress()) ?
            "Origination connection " : "Terminated connection ";
        s += "(" + address + ") ";
        s += "state=" + getStateName(state);
        return s;
    }
    
    public String toString() {
        return id;
    }
}
