/*
 * File Name     : JccProviderImpl.java
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

import java.util.Vector;
import java.util.HashMap;
import java.util.Properties;

import java.text.ParseException;
import javax.csapi.cc.jcc.*;

import javax.sip.SipProvider;
import javax.sip.SipFactory;
import javax.sip.SipStack;
import javax.sip.SipListener;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.TimeoutEvent;

import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sip.message.MessageFactory;

import javax.sip.header.*;

import javax.sip.address.AddressFactory;
import javax.sip.address.Address;

import org.apache.log4j.Logger;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

import jain.protocol.ip.JainIPFactory;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.JainMgcpStack;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class JccProviderImpl implements JccProvider, SipListener {
    
    protected final static String name = "Java call control provider for SIP 1.1";
    
    protected Vector callListeners;
    protected Vector connectionListeners;
    
    protected HashMap calls = new HashMap();
    protected HashMap mgcpTransactions = new HashMap();
    
    protected Registrar registrar;
    
    protected int state = JccProvider.OUT_OF_SERVICE;
    
    protected SipFactory sipFactory;
    protected SipProvider sipProvider;
    public JainMgcpProvider mgcpProvider;
    
    protected static int mgcpTransaction = 1;
    
    protected static AddressFactory addressFactory;
    private Logger logger = Logger.getLogger(JccProviderImpl.class);
    
    private PooledExecutor pooledExecutor = new PooledExecutor(10);
    
    /** Creates a new instance of JccProviderImpl */
    public JccProviderImpl(Properties properties) throws ProviderUnavailableException {
        callListeners = new Vector();
        connectionListeners = new Vector();
        try {
            sipFactory = SipFactory.getInstance();
            SipStack stack = sipFactory.createSipStack(properties);
            
            int port = 5060;
            if (properties.containsKey("javax.csapi.cc.jcc.sip.PORT")) {
                port = Integer.parseInt(properties.getProperty("javax.csapi.cc.jcc.sip.PORT"));
            }
            
            String transport = "UDP";
            if (properties.containsKey("javax.csapi.cc.jcc.sip.TRANSPORT")) {
                transport = properties.getProperty("javax.csapi.cc.jcc.sip.TRANSPORT");
            }
            
            ListeningPoint lp = stack.createListeningPoint(port, transport);
            sipProvider = stack.createSipProvider(lp);
            sipProvider.addSipListener(this);
            addressFactory = sipFactory.createAddressFactory();
            
            JainIPFactory mgcpFactory = JainIPFactory.getInstance();
            mgcpFactory.setPathName("org.mobicents.mgcp");
            JainMgcpStack mgcpStack = (JainMgcpStack) mgcpFactory.createIPObject("JainMgcpStackImpl");
            mgcpProvider = mgcpStack.createProvider();
            mgcpProvider.addJainMgcpListener(new MgcpListener(this));
            
            state = JccProvider.IN_SERVICE;
            registrar = new Registrar(this);
        } catch (Exception e) {
            logger.error("Peer unavailable, Caused by ", e);
            throw new ProviderUnavailableException(e.getMessage());
        }
    }
    
    /**
     * (Non java-doc)
     *
     * @see javax.csapi.cc.jcc.JccProvier#addCallListener.
     */
    public void addCallListener(JccCallListener listener)
    throws MethodNotSupportedException, ResourceUnavailableException {
        callListeners.add(listener);
    }
    
    /**
     * (Non java-doc)
     *
     * @see javax.csapi.cc.jcc.JccProvier#addCallLoadControlListener.
     */
    public void addCallLoadControlListener(CallLoadControlListener listener)
    throws MethodNotSupportedException, ResourceUnavailableException {
        throw new MethodNotSupportedException("Call load controll not supported");
    }
    
    /**
     * (Non java-doc)
     *
     * @see javax.csapi.cc.jcc.JccProvier#addConnectionListener.
     */
    public void addConnectionListener(JccConnectionListener listener, EventFilter filter)
    throws ResourceUnavailableException, MethodNotSupportedException {
        Object[] ls = new Object[2];
        ls[0] = filter;
        ls[1] = listener;
        connectionListeners.add(ls);
    }
    
    public void addProviderListener(JccProviderListener providerlistener)
    throws ResourceUnavailableException, MethodNotSupportedException {
        throw new MethodNotSupportedException();
    }
    
    /**
     * (Non Java-doc).
     * @see javax.csapi.cc.jcc.JccProvider#createCall().
     */
    public JccCall createCall()
    throws InvalidStateException, ResourceUnavailableException, PrivilegeViolationException, MethodNotSupportedException {
        if (state == JccProvider.IN_SERVICE) {
            return new JccCallImpl(this);
        }
        
        throw new InvalidStateException(this,
                InvalidStateException.PROVIDER_OBJECT, state);
    }
    
    public EventFilter createEventFilterAddressRange(String lowAddress, String highAddress, int matchDisposition, int nomatchDisposition)
    throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterAddressRegEx(String addressRegex, int matchDisposition, int nomatchDisposition)
    throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterAnd(EventFilter[] filters, int nomatchDisposition)
    throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterCauseCode(int causeCode, int matchDisposition, int nomatchDisposition)
    throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterDestAddressRange(
            String lowDestAddress, String highDestAddress, int matchDisposition, int nomatchDisposition) throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterDestAddressRegEx(
            String destAddressRegex, int matchDisposition, int nomatchDisposition) throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterEventSet(
            int[] blockEvents, int[] notifyEvents) throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterMidCallEvent(
            int midCallType, String midCallValue, int matchDisposition, int nomatchDisposition) throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterMinimunCollectedAddressLength(
            int minimumAddressLength, int matchDisposition, int nomatchDisposition) throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterOr(
            EventFilter[] filters, int nomatchDisposition) throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterOrigAddressRange(
            String lowOrigAddress, String highOrigAddress, int matchDisposition, int nomatchDisposition) throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public EventFilter createEventFilterOrigAddressRegEx(
            String origAddressRegex, int matchDisposition, int nomatchDisposition) throws ResourceUnavailableException, InvalidArgumentException {
        return null;
    }
    
    public JccAddress getAddress(String address) throws InvalidPartyException {
        return null;
    }
    
    public String getName() {
        return name;
    }
    
    public int getState() {
        return state;
    }
    
    public void removeCallListener(JccCallListener calllistener) {
    }
    
    public void removeCallLoadControlListener(CallLoadControlListener loadcontrollistener) {
    }
    
    public void removeConnectionListener(JccConnectionListener connectionlistener) {
    }
    
    public void removeProviderListener(JccProviderListener providerlistener) {
    }
    
    public void setCallLoadControl(JccAddress[] address, double duration, double[] mechanism, int[] treatment) throws MethodNotSupportedException {
    }
    
    public void shutdown() {
    }
    
    protected JccAddressImpl createAddress(String address) throws InvalidStateException, InvalidArgumentException {
        if (state != IN_SERVICE) {
            throw new InvalidStateException(this, InvalidStateException.PROVIDER_OBJECT,
                    state, "Provider should be in the IN_SERVICE state");
        }
        
        try {
            return new JccAddressImpl(this, addressFactory.createAddress(address));
        } catch (ParseException pe) {
            throw new InvalidArgumentException(pe.getMessage());
        }
    }
    
    /**
     * Creates new call with specified callID.
     *
     * @param callID the identifier of the call.
     * @return the call with specified callID.
     */
    protected JccCallImpl createCall(String callID) {
        JccCallImpl call = new JccCallImpl(this, callID);
        calls.put(callID, call);
        return call;
    }
    
    /**
     * Obtains call with specified callID.
     *
     * @param callID the identifier of the call.
     * @return the call with specified callID.
     */
    protected JccCallImpl getCall(String callID) {
        if (calls.containsKey(callID)) {
            if (logger.isDebugEnabled()) logger.debug("Obtain the call " + callID);
            return (JccCallImpl) calls.get(callID);
        }
        return null;
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.sip.SipListener#processRequest(RequestEvent).
     */
    public void processRequest(RequestEvent event) {
        if (logger.isDebugEnabled()) {
            logger.info("Receive request event: " +
                    event.getRequest().getMethod() + ", URI=" +
                    event.getRequest().getRequestURI());
        }
        
        try {
            pooledExecutor.execute(new RequestHandler(event, this));
        } catch (InterruptedException ie) {
            logger.error("Fialed to process request, caused by", ie);
        }
    }
    
    /**
     * (Non-Javadoc).
     * @see javax.sip.SipListener#processRequest(ResponeEvent).
     */
    public void processResponse(ResponseEvent responseEvent) {
        if (logger.isDebugEnabled()) {
            logger.info("Receive response event: " + responseEvent +
                    ", waiting for a new one");
        }
        try {
            pooledExecutor.execute(new ResponseHandler(responseEvent, this));
        } catch (InterruptedException ie) {
            logger.error("Fialed to process request, caused by", ie);
        }
    }
    
    public void processTimeout(TimeoutEvent event) {
        System.out.println("TIMEOUT : " + event.getTimeout());
    }
    
    
}
