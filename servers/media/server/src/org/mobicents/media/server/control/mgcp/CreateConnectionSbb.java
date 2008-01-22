/*
 * CreateConnectionSbbonnectionSbb.java
 * Mobicents Media Gateway
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

package org.mobicents.media.sbb;

import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.management.MBeanServer;

import javax.slee.ActivityContextInterface;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.CreateException;

import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;

import net.java.slee.resource.mgcp.MgcpActivityContextInterfaceFactory;

import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanServerLocator;
import org.mobicents.media.server.impl.BaseConnection;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class CreateConnectionSbb implements Sbb {
    
    private SbbContext sbbContext;
    private Logger logger = Logger.getLogger(CreateConnectionSbb.class);
    
    private JainMgcpProvider mgcpProvider;
    private MgcpActivityContextInterfaceFactory mgcpAcif;
    
    private MBeanServer mbeanServer;
    
    /**
     * Creates a new instance of CreateConnectionSbb
     *
     */
    public CreateConnectionSbb() {
    }
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        try {
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            mgcpProvider = (JainMgcpProvider) myEnv.lookup("slee/resources/jainmgcp/1.0/provider");
            mgcpAcif = (MgcpActivityContextInterfaceFactory) myEnv.lookup("slee/resources/jainmgcp/1.0/acifactory");
        } catch (NamingException ne) {
            logger.warn("Could not set SBB context:" + ne.getMessage());
        }
    }
    
    public void onCreateConnection(CreateConnection event, ActivityContextInterface aci) {
        int txID = event.getTransactionHandle();
        EndpointIdentifier endpointID = event.getEndpointIdentifier();
        
        logger.info("--> CRCX TX ID = " + txID + " Endpoint = " + endpointID);
        
    }
    
    private void reject(int txID, ReturnCode reason) {
        CreateConnectionResponse response = new CreateConnectionResponse(this, reason,
                new ConnectionIdentifier("0"));
        response.setTransactionHandle(txID);
        logger.info("<-- TX ID = " + txID + ": " + response.getReturnCode());
        mgcpProvider.sendMgcpEvents(new JainMgcpEvent[]{response});
    }
    
    
    /**
     * Converts MGCP specific mode value into local value.
     *
     * @param mode the MGCP specific mode.
     * @return the local mode.
     */
    private int getMode(ConnectionMode mode) {
        switch (mode.getConnectionModeValue()) {
            case ConnectionMode.RECVONLY :
                return BaseConnection.MODE_RECV_ONLY;
            case ConnectionMode.SENDONLY :
                return BaseConnection.MODE_SEND_ONLY;
            case ConnectionMode.SENDRECV :
                return BaseConnection.MODE_SEND_RECV;
            default :
                return BaseConnection.MODE_SEND_RECV;
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
