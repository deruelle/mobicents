package org.mobicents.media.server.ctrl.mgcp;

import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.ModifyConnection;
import jain.protocol.ip.mgcp.message.ModifyConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;

/**
 * 
 * @author amit bhayani
 *
 */
public class ModifyConnectionAction implements Callable {

	private static Logger logger = Logger.getLogger(ModifyConnectionAction.class);

	private ModifyConnection mdcx;
	private MgcpController controller;
	private MgcpUtils utils = new MgcpUtils();

	protected ModifyConnectionAction(MgcpController controller, ModifyConnection req) {
		this.controller = controller;
		this.mdcx = req;
	}

	public JainMgcpResponseEvent call() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Request TX= " + mdcx.getTransactionHandle() + ", CallID = " + mdcx.getCallIdentifier()
					+ ", Mode=" + mdcx.getMode() + ", Endpoint = " + mdcx.getEndpointIdentifier() + ", SDP present = "
					+ (mdcx.getRemoteConnectionDescriptor() != null));
		}

		ModifyConnectionResponse response = null;

		EndpointIdentifier endpointID = mdcx.getEndpointIdentifier();
		String localEndpoint = endpointID.getLocalEndpointName();

		if (localEndpoint.contains("*") || localEndpoint.contains("$")) {
			return reject(ReturnCode.Protocol_Error);
		}

		CallIdentifier callID = mdcx.getCallIdentifier();
		ConnectionIdentifier connectionID = mdcx.getConnectionIdentifier();
		ConnectionMode mode = null;
		if (mdcx.getMode() != null) {
			mode = utils.getMode(mdcx.getMode());
		}

		Endpoint endpoint = null;
		try {
			endpoint = controller.getNamingService().lookup(localEndpoint, true);
		} catch (Exception e) {
			return reject(ReturnCode.Endpoint_Unknown);
		}

		ConnectionActivity connectionActivity = controller.getActivity(localEndpoint, connectionID.toString());

		Connection connection = connectionActivity.getMediaConnection();

		if (connection.getMode() != mode) {
			connection.setMode(mode);
		}

		ConnectionDescriptor remoteConnectionDescriptor = mdcx.getRemoteConnectionDescriptor();
		if (remoteConnectionDescriptor != null) {
			connection.setRemoteDescriptor(remoteConnectionDescriptor.toString());
		}

		response = new ModifyConnectionResponse(this, ReturnCode.Transaction_Executed_Normally);
		response.setTransactionHandle(mdcx.getTransactionHandle());
		return response;
	}

	private ModifyConnectionResponse reject(ReturnCode code) {
		ModifyConnectionResponse response = new ModifyConnectionResponse(this, code);
		response.setTransactionHandle(mdcx.getTransactionHandle());
		return response;
	}

}
