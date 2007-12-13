/*
 * File Name     : JccCallImpl.java
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

import java.net.DatagramPacket;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import javax.csapi.cc.jcc.*;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;

import javax.sip.SipProvider;
import javax.sip.SipFactory;
import javax.sip.address.Address;

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

import javax.sdp.SessionDescription;
import javax.sdp.SdpFactory;

import org.apache.log4j.Logger;

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.Semaphore;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class JccCallImpl implements JccCall {
    /** The instance of the JCC Provider implementation */
    protected JccProviderImpl provider;
    
    /** The current state of this call */
    protected int state = JccCall.IDLE;
    
    /** The unique identifier of this call */
    protected String callID;
    
    /** Used to hold connections attached to this call */
    protected HashMap connections = new HashMap();
    
    /** Used to hold listeners for call related events */
    protected Vector callListeners = new Vector();
    
    /** Used to hold listeners for connection related events */
    protected Vector connectionListeners = new Vector();
    
    protected int cseq = 1;
    
    /** The SIP request causes this call creation */
    protected Request request;
    
    /** */
    protected ServerTransaction serverTransaction;
    
    /** The name of the endpoint wich servers media */
    protected String endpoint;
    
    /** Logger instance */
    private static Logger logger = Logger.getLogger(JccCallImpl.class);
    
    /**
     * Creates a new instance of JccCallImpl
     * Used by JCC implementaion to create an outgoing call.
     *
     * @param provider the reference to JCC Provider implementation.
     */
    public JccCallImpl(JccProviderImpl provider) {
        this.provider = provider;
        this.callID = Long.toHexString(System.currentTimeMillis());
        this.cseq++;
        this.state = ACTIVE;
    }
    
    /**
     * Creates new instance of this call.
     * Used by JccImplemetation for creating incoming call when INVAITE
     * message is received.
     *
     * @param provider the reference to JCC Provider implementation.
     * @param callID the value of the call-id header.
     */
    public JccCallImpl(JccProviderImpl provider, String callID) {
        this.provider = provider;
        this.callID = callID;
        this.cseq++;
        this.state = ACTIVE;
    }
    
    protected JccConnectionImpl getConnection(Dialog dialog) {
        Iterator list = connections.values().iterator();
        while (list.hasNext()) {
            JccConnectionImpl connection = (JccConnectionImpl) list.next();
            if (connection.dialog != null && 
                    connection.dialog.getDialogId().equals(dialog.getDialogId())) 
                return connection;
        }
        return null;
    }
    
    /**
     * Fires call realted events to listeners attached to this call.
     *
     * @param callListeners the list of listeners attached to this call.
     * @param event the call related event been fired.
     */
    protected void fireCallEvent(Vector callListeners, JccCallEvent event) {
        for (int i = 0; i < callListeners.size(); i++) {
            JccCallListener listener = (JccCallListener) callListeners.get(i);
            switch (event.getID()) {
                case JccCallEvent.CALL_ACTIVE :
                    listener.callActive(event);
                    break;
                case JccCallEvent.CALL_CREATED :
                    listener.callCreated(event);
                    break;
                case JccCallEvent.CALL_EVENT_TRANSMISSION_ENDED :
                    listener.callEventTransmissionEnded(event);
                    break;
                case JccCallEvent.CALL_INVALID :
                    listener.callInvalid(event);
                    break;
                case JccCallEvent.CALL_SUPERVISE_END :
                    listener.callSuperviseEnd(event);
                    break;
                case JccCallEvent.CALL_SUPERVISE_START :
                    listener.callSuperviseStart(event);
                    break;
            }
        }
    }
    
    /**
     * Fires call realted events to listeners attached to this call and
     * attached to the JccProvider.
     *
     * @param event the call related event been fired.
     */
    protected void fireCallEvent(JccCallEvent event) {
        fireCallEvent(callListeners, event);
        fireCallEvent(provider.callListeners, event);
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#addCallListener(JccCallListener).
     */
    public void addCallListener(JccCallListener listener)
    throws ResourceUnavailableException, MethodNotSupportedException {
        callListeners.add(listener);
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#addConnectionListener(JccConnectionListener, EventFilter).
     */
    public void addConnectionListener(JccConnectionListener cl, EventFilter filter)
    throws ResourceUnavailableException, MethodNotSupportedException {
        Object[] ls = new Object[2];
        ls[0] = filter;
        ls[1] = cl;
        connectionListeners.add(ls);
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#connect(JccAddress, String).
     */
    public JccConnection[] connect(JccAddress origaddr, String dialedDigits)
    throws ResourceUnavailableException, PrivilegeViolationException, InvalidPartyException, InvalidStateException, MethodNotSupportedException {
        return null;
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#createConnection(String, String, String, String).
     */
    public synchronized JccConnection createConnection(String targetAddress,
            String originatingAddress, String originalCalledAddress,
            String redirectingAddress)
            throws InvalidStateException, ResourceUnavailableException,
            PrivilegeViolationException, MethodNotSupportedException,
            InvalidArgumentException, InvalidPartyException {
        logger.info("Creating connection (" + targetAddress + ", " + originatingAddress +
                ", " + originalCalledAddress + ", " + redirectingAddress +")");
        
        if (state == INVALID) {
            throw new InvalidStateException(this, InvalidStateException.CALL_OBJECT, state);
        }
        
        if (provider.getState() != JccProvider.IN_SERVICE) {
            throw new InvalidStateException(provider, InvalidStateException.PROVIDER_OBJECT, state);
        }
        
        if (targetAddress == null) {
            throw new InvalidPartyException(InvalidPartyException.DESTINATION_PARTY,
                    "Target address can not be null");
        }
        
        JccAddressImpl target = provider.createAddress(targetAddress);
        JccAddressImpl originating = originatingAddress != null ?
            provider.createAddress(originatingAddress) : null;
        
        JccConnectionImpl connection = new JccConnectionImpl(this, target, originating, JccEvent.CAUSE_NORMAL);
        connections.put(target.getURI(), connection);
        
        if (logger.isDebugEnabled()) {
            logger.debug("Call[" + callID + "] attach connection " + target);
        }
        return connection;
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#getConnections().
     */
    public JccConnection[] getConnections() {
        if (state == JccCall.ACTIVE) {
            int count = 0;
            Object[] connections = this.connections.values().toArray();
            for (int i = 0; i < connections.length; i++) {
                JccConnection connection = (JccConnection) connections[i];
                if (connection.getState() != JccConnection.DISCONNECTED) count++;
            }
            
            JccConnection[] list = new JccConnection[count];
            int j = 0;
            
            for (int i = 0; i < connections.length; i++) {
                JccConnection connection = (JccConnection) connections[i];
                if (connection.getState() != JccConnection.DISCONNECTED) {
                    list[j++] = connection;
                }
            }
            
            return list;
        }
        
        return null;
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#getProvider().
     */
    public JccProvider getProvider() {
        return provider;
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#getState().
     */
    public int getState() {
        return state;
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#release(int).
     */
    public synchronized void release(int causeCode) throws PrivilegeViolationException,
            ResourceUnavailableException, InvalidStateException,
            InvalidArgumentException {
        JccConnection[] connections = getConnections();
        for (int i = 0; i < connections.length; i++) {
            connections[i].release(causeCode);
        }
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#removeCallListener(JccCallListener).
     */
    public void removeCallListener(JccCallListener listener) {
        callListeners.remove(listener);
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#removeConnectionListener(JccConnectionListener).
     */
    public void removeConnectionListener(JccConnectionListener cl) {
        
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#routeCall(String,String,String,String).
     */
    public synchronized JccConnection routeCall(
            String targetAddress,
            String originatingAddress,
            String originalDestinationAddress,
            String redirectingAddress)
            throws InvalidStateException,
            ResourceUnavailableException,
            PrivilegeViolationException,
            MethodNotSupportedException,
            InvalidPartyException, InvalidArgumentException {
        
        if (logger.isDebugEnabled()) {
            logger.debug("Route call [" + targetAddress + ", " +
                    originatingAddress + "," +
                    originalDestinationAddress + ", " +
                    redirectingAddress);
        }
        
        this.state = ACTIVE;
        
        JccConnection connection = createConnection(targetAddress, originatingAddress,
                originalDestinationAddress, redirectingAddress);
        
        if (logger.isDebugEnabled()) {
            logger.debug("Routing connection " +  connection);
        }
        
        connection.routeConnection(true);
        return connection;
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.csapi.cc.jcc.JccCall#superviceCall(JccCallListener, double, int).
     */
    public void superviseCall(JccCallListener calllistener, double time,
            int treatment) throws MethodNotSupportedException {
    }
    
    protected void releaseComplete() {
        connections = null;
        provider.calls.remove(callID);
        logger.info("Call [" + callID + "] release complete");
    }
    
    public String toString() {
        return endpoint;
    }
    
}
