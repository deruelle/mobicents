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
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.FactoryException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.FacilityException;
import javax.slee.facilities.NameNotBoundException;

import net.java.slee.resource.mgcp.JainMgcpProvider;
import net.java.slee.resource.mgcp.MgcpActivityContextInterfaceFactory;
import net.java.slee.resource.mgcp.MgcpConnectionActivity;

import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;

/**
 * 
 * @author amit bhayani
 * @author Oleg Kulikov
 */
public abstract class DeleteConnectionSbb implements Sbb {

	private SbbContext sbbContext;
	private static final Logger logger = Logger.getLogger(DeleteConnectionSbb.class);

	private JainMgcpProvider mgcpProvider;
	private MgcpActivityContextInterfaceFactory mgcpAcif;

	private MsProvider msProvider;
	private MediaRaActivityContextInterfaceFactory msActivityFactory;

	private ActivityContextNamingFacility activityContextNamingfacility;

	/** Creates a new instance of DeleteConnectionSbb */
	public DeleteConnectionSbb() {
	}

	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
		try {

			Context ctx = (Context) new InitialContext().lookup("java:comp/env");

			msProvider = (MsProvider) ctx.lookup("slee/resources/media/1.0/provider");
			msActivityFactory = (MediaRaActivityContextInterfaceFactory) ctx
					.lookup("slee/resources/media/1.0/acifactory");

			mgcpProvider = (JainMgcpProvider) ctx.lookup("slee/resources/jainmgcp/2.0/provider");
			mgcpAcif = (MgcpActivityContextInterfaceFactory) ctx.lookup("slee/resources/jainmgcp/2.0/acifactory");
			activityContextNamingfacility = (ActivityContextNamingFacility) ctx
					.lookup("slee/facilities/activitycontextnaming");
		} catch (NamingException ne) {
			logger.warn("Could not set SBB context:" + ne.getMessage());
		}
	}

	public void onDeleteConnection(DeleteConnection event, ActivityContextInterface aci) {
		int txID = event.getTransactionHandle();
		//logger.info("--> DLCX TX ID = " + txID);

		this.setTxId(txID);
		this.setDeleteResponseSent(false);
		this.setReceivedTransactionID(event.getSource());

		EndpointIdentifier endpointID = event.getEndpointIdentifier();
		CallIdentifier callID = event.getCallIdentifier();
		ConnectionIdentifier connectionID = event.getConnectionIdentifier();		

		String enpointName = endpointID.getLocalEndpointName();
		if (enpointName.endsWith("/$")) {
			logger.error("Cannot execute DLCX command for Endpoint which has wild card " + enpointName);
			sendDeleteResponse(ReturnCode.Endpoint_Unknown);			
			aci.detach(sbbContext.getSbbLocalObject());
			return;
		}

		if (endpointID != null && callID == null && connectionID == null) {
			deleteForEndpoint(endpointID);

		} else if (endpointID != null && callID != null && connectionID == null) {
			deleteForEndpoint(endpointID);
		} else {
			ConnectionIdentifier connectionIdentifier = event.getConnectionIdentifier();

			ActivityContextInterface mediaACI = activityContextNamingfacility.lookup(connectionIdentifier.toString());

			mediaACI.attach(sbbContext.getSbbLocalObject());
			MsConnection msConnection = (MsConnection) mediaACI.getActivity();
			msConnection.release();
		}

	}

	public void onConnectionDisconnected(MsConnectionEvent evt, ActivityContextInterface aci) {
		if (!this.getDeleteResponseSent()) {
			sendDeleteResponse(ReturnCode.Transaction_Executed_Normally);
			//this.setDeleteResponseSent(true);
		}
	}

	private void sendDeleteResponse(ReturnCode returnCode) {
		DeleteConnectionResponse response = new DeleteConnectionResponse(this.getReceivedTransactionID(), returnCode);
		int txID = this.getTxId();

		response.setTransactionHandle(txID);
		//logger.info("<-- TX ID = " + txID + ": " + response.getReturnCode());
		mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { response });

	}

	private void deleteForEndpoint(EndpointIdentifier endpointID) {

		String endpointName = endpointID.getLocalEndpointName();

		List<MsConnection> msConnections = msProvider.getMsConnections(endpointName);

		List<MgcpConnectionActivity> mgcpConnectionActivities = mgcpProvider.getConnectionActivities(endpointID);

		for (MsConnection c : msConnections) {
			try {

				for (MgcpConnectionActivity a : mgcpConnectionActivities) {

					String connectionIdentifier = a.getConnectionIdentifier().toString();
					ActivityContextInterface mediaACI = activityContextNamingfacility.lookup(connectionIdentifier);
					MsConnection msConnection = (MsConnection) mediaACI.getActivity();

					if (msConnection.getId().equals(c.getId())) {
						//logger.debug("Releasing MgcpConnectionActivity = " + a.getConnectionIdentifier());
						a.release();
						mgcpConnectionActivities.remove(a);
						break;
					}

					try {
						activityContextNamingfacility.unbind(connectionIdentifier);
					} catch (TransactionRequiredLocalException e) {
						e.printStackTrace();
					} catch (FacilityException e) {
						e.printStackTrace();
					} catch (NameNotBoundException e) {
						e.printStackTrace();
					}
				}

				ActivityContextInterface msAci = msActivityFactory.getActivityContextInterface(c);
				msAci.attach(sbbContext.getSbbLocalObject());
				c.release();

				//logger.debug("Successfully deleted MsConnection ID = " + c.getId());

			} catch (FactoryException e) {
				logger.error("FactoryException while trying to retrieve the MS ACI for MsConnection ID = " + c.getId());
			} catch (NullPointerException e) {
				logger.error("NullPointerException while trying to retrieve the MS ACI for MsConnection ID = "
						+ c.getId());
			} catch (UnrecognizedActivityException e) {
				logger.error("UnrecognizedActivityException while trying to retrieve the MS ACI for MsConnection ID = "
						+ c.getId());
			}
		}// End of for loop
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
		logger.error("Runtime exception thrown in DLCX TxId = ");
	}

	public void sbbRolledBack(RolledBackContext rolledBackContext) {
		logger.error("Tx rolled back in DLCX TxId = ");
	}

	public abstract int getTxId();

	public abstract void setTxId(int txId);

	public abstract Object getReceivedTransactionID();

	public abstract void setReceivedTransactionID(Object receivedTransactionID);

	public abstract boolean getDeleteResponseSent();

	public abstract void setDeleteResponseSent(boolean deleteResponseSent);

}
