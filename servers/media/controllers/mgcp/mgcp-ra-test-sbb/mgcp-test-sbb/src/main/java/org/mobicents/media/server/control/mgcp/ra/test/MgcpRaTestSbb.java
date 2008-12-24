/*
 * MgcpSbb.java
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

package org.mobicents.media.server.control.mgcp.ra.test;

import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.EndpointConfiguration;
import jain.protocol.ip.mgcp.message.ModifyConnection;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.InitialEventSelector;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;

import net.java.slee.resource.mgcp.JainMgcpProvider;

import org.apache.log4j.Logger;

/**
 * @author amit bhayani
 * @author Oleg Kulikov
 */
public abstract class MgcpRaTestSbb implements Sbb {

	private SbbContext sbbContext;
	private static final Logger logger = Logger.getLogger(MgcpRaTestSbb.class);

	private JainMgcpProvider mgcpProvider;

	/** Creates a new instance of MgcpSbb */
	public MgcpRaTestSbb() {
	}

	public void onCreateConnection(CreateConnection event, ActivityContextInterface aci) {
		System.out.println("CRCX ---> " + event.getTransactionHandle());
		String identifier = ((CallIdentifier) mgcpProvider.getUniqueCallIdentifier()).toString();
		ConnectionIdentifier connectionIdentifier = new ConnectionIdentifier(identifier);

		CreateConnectionResponse response = new CreateConnectionResponse(event.getSource(),
				ReturnCode.Transaction_Executed_Normally, connectionIdentifier);

		response.setTransactionHandle(event.getTransactionHandle());
		response.setSpecificEndpointIdentifier(event.getEndpointIdentifier());
		mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { response });
		
		System.out.println("CRCX Response <--- " + event.getTransactionHandle());

	}

	public void onDeleteConnection(DeleteConnection event, ActivityContextInterface aci) {
		System.out.println("DLCX ---> " + event.getTransactionHandle());
		DeleteConnectionResponse dlcxResponse = new DeleteConnectionResponse(event.getSource(),
				ReturnCode.Transaction_Executed_Normally);

		dlcxResponse.setTransactionHandle(event.getTransactionHandle());

		mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { dlcxResponse });

		int i = getCompletedMgcpCycle();
		i++;
		this.setCompletedMgcpCycle(i);
		if (i % 100 == 0)
			System.out.println("MGCP cycle termination count = " + i);
		
		System.out.println("DLCX Response <--- " + event.getTransactionHandle());
	}

	public void onModifyConnection(ModifyConnection event, ActivityContextInterface aci) {
	}

	public void onEndpointConfiguration(EndpointConfiguration event, ActivityContextInterface aci) {
	}

	public void onNotificationRequest(NotificationRequest event, ActivityContextInterface aci) {
		System.out.println("RQNT ---> " + event.getTransactionHandle());
		NotificationRequestResponse ntrqResponse = new NotificationRequestResponse(event.getSource(),
				ReturnCode.Transaction_Executed_Normally);
		ntrqResponse.setTransactionHandle(event.getTransactionHandle());
		mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { ntrqResponse });
		
		System.out.println("RQNT Response <--- " + event.getTransactionHandle());
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

	public abstract int getCompletedMgcpCycle();

	public abstract void setCompletedMgcpCycle(int txId);

	public InitialEventSelector endpointIdSelect(InitialEventSelector ies) {
		Object event = ies.getEvent();
		String endpointId = null;

		if (event instanceof NotificationRequest) {
			// If request event, the convergence name to callId
			endpointId = ((NotificationRequest) event).getEndpointIdentifier().toString();
			if (logger.isDebugEnabled()) {
				logger.debug("endpointIdSelect() Setting the customName to " + endpointId);
			}
			ies.setCustomName(endpointId);
		}

		return ies;
	}

}
