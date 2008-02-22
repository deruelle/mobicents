/*
 * File Name     : MgcpProviderImpl.java
 *
 * The JAIN MGCP API implementaion.
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

package org.mobicents.mgcp;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.Vector;
import java.util.Properties;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.JainMgcpStack;
import jain.protocol.ip.mgcp.JainMgcpListener;
import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Notify;

import jain.protocol.ip.mgcp.message.Constants;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class JainMgcpProviderImpl implements JainMgcpProvider {
    
    protected JainMgcpStackImpl stack;
    protected DatagramSocket socket;
    
    protected HashMap remoteTransactions = new HashMap();
    protected HashMap localTransactions = new HashMap();
    protected HashMap transactions = new HashMap();
    
    protected static int TID = 0;
    
    private Logger logger = Logger.getLogger(JainMgcpProviderImpl.class);
    private ExecutorService pooledExecutor = Executors.newFixedThreadPool(5);
    protected Vector listeners = new Vector();
    
    /** Creates a new instance of MgcpProviderImpl */
    public JainMgcpProviderImpl() {
    }
    
    public void addJainMgcpListener(JainMgcpListener listener)
    throws TooManyListenersException {
        listeners.add(listener);
    }
    
    public JainMgcpStack getJainMgcpStack() {
        return stack;
    }
    
    public void removeJainMgcpListener(JainMgcpListener listener) {
        listeners.remove(listener);
    }
    
    protected void fireEvent(JainMgcpCommandEvent event) {
        for (int i = 0; i < listeners.size(); i++) {
            JainMgcpListener listener = (JainMgcpListener) listeners.get(i);
            listener.processMgcpCommandEvent(event);
        }
    }
    
    protected void fireEvent(JainMgcpResponseEvent event) {
        for (int i = 0; i < listeners.size(); i++) {
            JainMgcpListener listener = (JainMgcpListener) listeners.get(i);
            listener.processMgcpResponseEvent(event);
        }
    }
    
    public void sendMgcpEvents(JainMgcpEvent[] events) throws IllegalArgumentException {
        for (int i = 0; i < events.length; i++) {
            if (events[i] instanceof JainMgcpCommandEvent) {
                JainMgcpCommandEvent event = (JainMgcpCommandEvent) events[i];
                TransactionHandle handle = null;
                switch (events[i].getObjectIdentifier()) {
                    case Constants.CMD_CREATE_CONNECTION :
                        if (logger.isDebugEnabled()) {
                            logger.debug("Sending CreateConnection object to " + 
                                    event.getEndpointIdentifier());
                        }
                        handle = new CreateConnectionHandle(stack);
                        break;
                    case Constants.CMD_MODIFY_CONNECTION :
                        if (logger.isDebugEnabled()) {
                            logger.debug("Sending ModifyConnection object to " + 
                                    event.getEndpointIdentifier());
                        }
                        handle = new ModifyConnectionHandle(stack);
                        break;
                    case Constants.CMD_DELETE_CONNECTION :
                        if (logger.isDebugEnabled()) {
                            logger.debug("Sending DeleteConnection object to " + 
                                    event.getEndpointIdentifier());
                        }
                        handle = new DeleteConnectionHandle(stack);
                        break;
                    case Constants.CMD_ENDPOINT_CONFIGURATION :
                        if (logger.isDebugEnabled()) {
                            logger.debug("Sending EndpointConfiguration object to " + 
                                    event.getEndpointIdentifier());
                        }
                        handle = new EndpointConfigurationHandle(stack);
                        break;
                    default :
                        throw new IllegalArgumentException("Could not send type of the message yet");
                }
                
                handle.send(event);
            } else  {
                JainMgcpResponseEvent event = (JainMgcpResponseEvent) events[i];
                int tid = events[i].getTransactionHandle();
                TransactionHandle handle = (TransactionHandle) 
                    stack.transactions.get(new Integer(tid));
                if (handle == null) {
                    throw new IllegalArgumentException("Unknown transaction handle: " + tid);
                }
                
                handle.send(event);
            }
        }
    }
    
}
