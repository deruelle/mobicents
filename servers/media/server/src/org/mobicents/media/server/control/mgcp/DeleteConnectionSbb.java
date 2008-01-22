/*
 * DeleteConnectionSbb.java
 *
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
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import java.util.Iterator;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import net.java.slee.resource.mgcp.MgcpActivityContextInterfaceFactory;
import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanServerLocator;
import org.mobicents.media.server.impl.BaseConnection;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class DeleteConnectionSbb implements Sbb {
    
    private SbbContext sbbContext;
    private Logger logger = Logger.getLogger(DeleteConnectionSbb.class);
    
    private JainMgcpProvider mgcpProvider;
    private MgcpActivityContextInterfaceFactory mgcpAcif;
    
    private MBeanServer mbeanServer;
    
    /** Creates a new instance of DeleteConnectionSbb */
    public DeleteConnectionSbb() {
    }
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        try {
            mbeanServer = MBeanServerLocator.locateJBoss();
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            mgcpProvider = (JainMgcpProvider) myEnv.lookup("slee/resources/jainmgcp/1.0/provider");
            mgcpAcif = (MgcpActivityContextInterfaceFactory) myEnv.lookup("slee/resources/jainmgcp/1.0/acifactory");
        } catch (NamingException ne) {
            logger.warn("Could not set SBB context:" + ne.getMessage());
        }
    }
    
    public void onDeleteConnection(DeleteConnection event, ActivityContextInterface aci) {
        int txID = event.getTransactionHandle();
        logger.info("--> DLCX TX ID = " + txID);
        
        EndpointIdentifier endpointID = event.getEndpointIdentifier();
        CallIdentifier callID = event.getCallIdentifier();
        ConnectionIdentifier connectionID = event.getConnectionIdentifier();
        
        if (endpointID != null && callID == null && connectionID == null) {
            deleteForEndpoint(endpointID, txID);
        }
        
            DeleteConnectionResponse response =
                    new DeleteConnectionResponse(this, ReturnCode.Transaction_Executed_Normally);
            mgcpProvider.sendMgcpEvents(new JainMgcpEvent[]{response});
            logger.info("<-- TX ID = " + txID + ": " + response.getReturnCode());
    }
    
    private void deleteForEndpoint(EndpointIdentifier endpointID, int txID) {
        
        DeleteConnectionResponse response =
                new DeleteConnectionResponse(this, ReturnCode.Transaction_Executed_Normally);
        mgcpProvider.sendMgcpEvents(new JainMgcpEvent[]{response});
        logger.info("<-- TX ID = " + txID + ": " + response.getReturnCode());
    }
    
    
    private void reject(int txID, ReturnCode reason) {
        DeleteConnectionResponse response = new DeleteConnectionResponse(this, reason);
        response.setTransactionHandle(txID);
        logger.info("<-- TX ID = " + txID + ": " + response.getReturnCode());
        mgcpProvider.sendMgcpEvents(new JainMgcpEvent[]{response});
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
