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

package org.mobicents.media.server.control.mgcp;

import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpProvider;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import javax.management.MBeanServer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.facilities.ActivityContextNamingFacility;

import net.java.slee.resource.mgcp.MgcpActivityContextInterfaceFactory;

import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanServerLocator;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;

/**
 * 
 * @author amit bhayani
 * @author Oleg Kulikov
 */
public abstract class DeleteConnectionSbb implements Sbb {

	private SbbContext sbbContext;
	private Logger logger = Logger.getLogger(DeleteConnectionSbb.class);

	private JainMgcpProvider mgcpProvider;
	private MgcpActivityContextInterfaceFactory mgcpAcif;

	private ActivityContextNamingFacility activityContextNamingfacility;

	private MBeanServer mbeanServer;

	/** Creates a new instance of DeleteConnectionSbb */
	public DeleteConnectionSbb() {
	}

	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
		try {
			mbeanServer = MBeanServerLocator.locateJBoss();
			Context ctx = (Context) new InitialContext()
					.lookup("java:comp/env");
			mgcpProvider = (JainMgcpProvider) ctx
					.lookup("slee/resources/jainmgcp/2.0/provider");
			mgcpAcif = (MgcpActivityContextInterfaceFactory) ctx
					.lookup("slee/resources/jainmgcp/2.0/acifactory");
			activityContextNamingfacility = (ActivityContextNamingFacility) ctx
					.lookup("slee/facilities/activitycontextnaming");
		} catch (NamingException ne) {
			logger.warn("Could not set SBB context:" + ne.getMessage());
		}
	}

	public void onDeleteConnection(DeleteConnection event,
			ActivityContextInterface aci) {
		int txID = event.getTransactionHandle();
		logger.info("--> DLCX TX ID = " + txID);

		this.setTxId(txID);

		EndpointIdentifier endpointID = event.getEndpointIdentifier();
		CallIdentifier callID = event.getCallIdentifier();
		ConnectionIdentifier connectionID = event.getConnectionIdentifier();

		// TODO : Wildcard conventions shall not be used. Send Error Response if
		// found?

		if (endpointID != null && callID == null && connectionID == null) {
			// TODO : Delete all the connections for an End Point

		} else if (endpointID != null && callID != null && connectionID == null) {
			// TODO : Delete all the connection that relate to a Call for an
			// endpoint
		} else {
			ConnectionIdentifier connectionIdentifier = event
					.getConnectionIdentifier();

			ActivityContextInterface mediaACI = activityContextNamingfacility
					.lookup(connectionIdentifier.toString());

			mediaACI.attach(sbbContext.getSbbLocalObject());
			MsConnection msConnection = (MsConnection) mediaACI.getActivity();
			msConnection.release();
		}

	}

	public void onConnectionDeleted(MsConnectionEvent evt,
			ActivityContextInterface aci) {
		DeleteConnectionResponse response = new DeleteConnectionResponse(this,
				ReturnCode.Transaction_Executed_Normally);
		int txID = this.getTxId();

		response.setTransactionHandle(txID);
		logger.info("<-- TX ID = " + txID + ": " + response.getReturnCode());
		mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { response });
	}

	private void deleteForEndpoint(EndpointIdentifier endpointID, int txID) {

		String endpointName = endpointID.getLocalEndpointName();

		try {
			Endpoint endpoint = EndpointQuery.find(endpointName);
			endpoint.deleteAllConnections();

		} catch (NamingException e) {
			// TODO : Send appropriate Response
			e.printStackTrace();
		} catch (ResourceUnavailableException e) {
			// TODO Send appropriate Response
			e.printStackTrace();
		}

		DeleteConnectionResponse response = new DeleteConnectionResponse(this,
				ReturnCode.Transaction_Executed_Normally);
		mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { response });
		logger.info("<-- TX ID = " + txID + ": " + response.getReturnCode());
	}

	private void reject(int txID, ReturnCode reason) {
		DeleteConnectionResponse response = new DeleteConnectionResponse(this,
				reason);
		response.setTransactionHandle(txID);
		logger.info("<-- TX ID = " + txID + ": " + response.getReturnCode());
		mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { response });
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

	public abstract int getTxId();

	public abstract void setTxId(int txId);

}
