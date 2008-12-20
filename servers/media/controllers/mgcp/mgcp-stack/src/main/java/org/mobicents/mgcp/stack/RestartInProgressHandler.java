package org.mobicents.mgcp.stack;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.RestartInProgress;
import jain.protocol.ip.mgcp.message.RestartInProgressResponse;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RestartMethod;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.parser.MgcpContentHandler;
import org.mobicents.mgcp.stack.parser.MgcpMessageParser;

/**
 * Parse/encode RSIP commands.
 * 
 * @author Tom Uijldert
 * 
 */
public class RestartInProgressHandler extends TransactionHandler {

	private Logger logger = Logger.getLogger(RestartInProgressHandler.class);

	private RestartInProgress command;
	private RestartInProgressResponse response;

	public RestartInProgressHandler(JainMgcpStackImpl stack) {
		super(stack);
	}

	public RestartInProgressHandler(JainMgcpStackImpl stack, InetAddress address, int port) {
		super(stack, address, port);
	}

	@Override
	public JainMgcpCommandEvent decodeCommand(String message) throws ParseException {
		MgcpMessageParser parser = new MgcpMessageParser(new CommandContentHandle());
		try {
			parser.parse(message);
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), -1);
		}
		return command;
	}

	@Override
	public JainMgcpResponseEvent decodeResponse(String message) throws ParseException {
		MgcpMessageParser parser = new MgcpMessageParser(new ResponseContentHandle());
		try {
			parser.parse(message);
		} catch (IOException e) {
			// should never happen
		}
		return response;
	}

	@Override
	public String encode(JainMgcpCommandEvent event) {
		RestartInProgress rsip = (RestartInProgress) event;
		StringBuffer message = new StringBuffer();
		message.append("RSIP ").append(event.getTransactionHandle()).append(SINGLE_CHAR_SPACE).append(
				rsip.getEndpointIdentifier()).append(SINGLE_CHAR_SPACE).append(MGCP_VERSION).append(NEW_LINE);

		message.append("RM:").append(rsip.getRestartMethod()).append(NEW_LINE);
		if (rsip.getRestartDelay() != 0) {
			message.append("RD:").append(rsip.getRestartDelay()).append(NEW_LINE);
		}
		if (rsip.getReasonCode() != null) {
			message.append("E:").append(rsip.getReasonCode()).append(NEW_LINE);
		}
		return message.toString();
	}

	@Override
	public String encode(JainMgcpResponseEvent event) {

		RestartInProgressResponse response = (RestartInProgressResponse) event;
		ReturnCode returnCode = response.getReturnCode();
		StringBuffer s = new StringBuffer();
		s.append(returnCode.getValue()).append(SINGLE_CHAR_SPACE).append(response.getTransactionHandle()).append(
				SINGLE_CHAR_SPACE).append(returnCode.getComment()).append(NEW_LINE);


		// TODO should utils.encodeNotifiedEntity decide on port?
		if (response.getNotifiedEntity() != null) {
			s.append("N:").append(utils.encodeNotifiedEntity(response.getNotifiedEntity()) ).append(NEW_LINE);

		}
		return s.toString();
		// return msg;
	}

	private class CommandContentHandle implements MgcpContentHandler {

		public void header(String header) throws ParseException {
			String[] tokens = header.split("\\s");

			String verb = tokens[0].trim();
			String transactionID = tokens[1].trim();
			String version = tokens[3].trim() + " " + tokens[4].trim();

			int tid = Integer.parseInt(transactionID);
			EndpointIdentifier endpoint = utils.decodeEndpointIdentifier(tokens[2].trim());

			command = new RestartInProgress(getObjectSource(tid), endpoint, RestartMethod.Restart);
			command.setTransactionHandle(tid);
		}

		public void param(String name, String value) throws ParseException {
			if (name.equalsIgnoreCase("RM")) {
				command.setRestartMethod(utils.decodeRestartMethod(value));
			} else if (name.equalsIgnoreCase("RD")) {
				command.setRestartDelay(Integer.parseInt(value));
			} else if (name.equalsIgnoreCase("E")) {
				command.setReasonCode(utils.decodeReasonCode(value));
			}
		}

		public void sessionDescription(String sd) throws ParseException {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	private class ResponseContentHandle implements MgcpContentHandler {

		public void header(String header) throws ParseException {
			String[] tokens = header.split("\\s");

			int tid = Integer.parseInt(tokens[1]);
			response = new RestartInProgressResponse(source != null ? source : stack, utils.decodeReturnCode(Integer.parseInt(tokens[0])));
			response.setTransactionHandle(tid);
		}

		public void param(String name, String value) throws ParseException {
			if (name.equalsIgnoreCase("N")) {
				NotifiedEntity n = utils.decodeNotifiedEntity(value, true);
				response.setNotifiedEntity(n);
			} else {
				logger.warn("Unidentified AUCX Response parameter " + name + " with value = " + value);
			}
		}

		public void sessionDescription(String sd) throws ParseException {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	@Override
	public JainMgcpResponseEvent getProvisionalResponse() {
		RestartInProgressResponse provisionalResponse = null;
		if (!sent) {
			provisionalResponse = new RestartInProgressResponse(source != null ? source : stack,
					ReturnCode.Transaction_Being_Executed);
			provisionalResponse.setTransactionHandle(remoteTID);
		}
		return provisionalResponse;
	}
}
