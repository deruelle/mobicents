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

import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.message.EndpointConfiguration;
import jain.protocol.ip.mgcp.message.EndpointConfigurationResponse;
import jain.protocol.ip.mgcp.message.parms.BearerInformation;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
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
//import org.mobicents.media.server.impl.sdp.AVProfile;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * @author amit bhayani
 */
public abstract class EndpointConfigurationSbb implements Sbb {

	private SbbContext sbbContext;
	private Logger logger = Logger.getLogger(EndpointConfigurationSbb.class);

	private JainMgcpProvider mgcpProvider;

	/**
	 * Creates a new instance of CreateConnectionSbb
	 * 
	 */
	public EndpointConfigurationSbb() {
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

	public void onEndpointConfiguration(EndpointConfiguration event, ActivityContextInterface aci) {

		EndpointIdentifier endpointID = event.getEndpointIdentifier();

		int txID = event.getTransactionHandle();

		this.setTxId(txID);
		
		this.setReceivedTransactionID(event.getSource());

		BearerInformation bearerInformation = event.getBearerInformation();
		int encodingMethod = bearerInformation.getEncodingMethod();

		String endpointName = endpointID.getLocalEndpointName();

		if (endpointName.endsWith("/$")) {
			// TODO Throw Exception as RFC 3435 says '"any of" wildcard
			// convention MUST NOT be used. If the "all of" wldcard convention
			// is used, the command applies to all the endpoints whose name
			// matches the wildcard.
		}

		logger.info("--> EPCF TX ID = " + txID + " Endpoint = " + endpointID);

		Endpoint endpoint = null;
		try {
			endpoint = EndpointQuery.lookup(endpointName);

			switch (encodingMethod) {
			case BearerInformation.ENC_METHOD_A_LAW:
//				endpoint.addFormat(AVProfile.getPayload(AVProfile.PCMA), AVProfile.PCMA);
				break;

			case BearerInformation.ENC_METHOD_MU_LAW:
//				endpoint.addFormat(AVProfile.getPayload(AVProfile.PCMU), AVProfile.PCMU);
				break;

			default:
				logger.warn("The encoding method supported is ENC_METHOD_A_LAW = " + BearerInformation.ENC_METHOD_A_LAW
						+ " and ENC_METHOD_MU_LAW  = " + BearerInformation.ENC_METHOD_A_LAW + " only. Passed is = "
						+ encodingMethod);
				break;
			}

			sendResponse(this.getTxId(), ReturnCode.Transaction_Executed_Normally);
			// TODO : ReturnCode is correct?
		} catch (NamingException e) {
			logger.error("NamingException while trying to get Endpoint for endpoint name = " + endpointID, e);
			sendResponse(this.getTxId(), ReturnCode.Endpoint_Unknown);
		} catch (ResourceUnavailableException e) {
			logger.error("ResourceUnavailableException while trying to get Endpoint for endpoint name = " + endpointID,
					e);
			sendResponse(this.getTxId(), ReturnCode.Endpoint_Unknown);
		}

	}

	private void sendResponse(int txID, ReturnCode reason) {
		EndpointConfigurationResponse response = new EndpointConfigurationResponse(this.getReceivedTransactionID(), reason);
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

	public void sbbExceptionThrown(Exception exception, Object object, ActivityContextInterface activityContextInterface) {
	}

	public void sbbRolledBack(RolledBackContext rolledBackContext) {
	}

	public abstract int getTxId();

	public abstract void setTxId(int txId);
	
	public abstract Object getReceivedTransactionID();

	public abstract void setReceivedTransactionID(Object receivedTransactionID);	
	

}
