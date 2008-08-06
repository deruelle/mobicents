/*
 * EndpointConfigurationSbb.java
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
package org.mobicents.media.server.control.mgcp;

import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;

import net.java.slee.resource.mgcp.JainMgcpProvider;

import org.apache.log4j.Logger;

/**
 * @author amit bhayani
 */
public abstract class NotificationRequestSbb implements Sbb {

    private SbbContext sbbContext;
    private Logger logger = Logger.getLogger(NotificationRequestSbb.class);
    private JainMgcpProvider mgcpProvider;

    /**
     * Creates a new instance of CreateConnectionSbb
     * 
     */
    public NotificationRequestSbb() {
    }

    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        try {
            Context ctx = (Context) new InitialContext().lookup("java:comp/env");

            mgcpProvider = (JainMgcpProvider) ctx.lookup("slee/resources/jainmgcp/2.0/provider");

        } catch (NamingException ne) {
            logger.warn("Could not set SBB context:" + ne.getMessage());
        }
    }

    public void onNotificationRequest(NotificationRequest event, ActivityContextInterface aci) {
        logger.info("onNotificationRequest");
        EndpointIdentifier endpointID = event.getEndpointIdentifier();
        RequestedEvent[] reqEvents = event.getRequestedEvents();
        
        //reqEvents[0].getEventName().getConnectionIdentifier()

	
    }

    private void sendResponse(int txID, ReturnCode reason) {
//		EndpointConfigurationResponse response = new EndpointConfigurationResponse(this.getReceivedTransactionID(), reason);
//		response.setTransactionHandle(txID);
//		logger.info("<-- TX ID = " + txID + ": " + response.getReturnCode());
//		mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { response });
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

    public abstract int getTxId();

    public abstract void setTxId(int txId);

    public abstract Object getReceivedTransactionID();

    public abstract void setReceivedTransactionID(Object receivedTransactionID);
}
