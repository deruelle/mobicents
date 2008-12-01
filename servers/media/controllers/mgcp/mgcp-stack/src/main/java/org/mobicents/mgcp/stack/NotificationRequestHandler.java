package org.mobicents.mgcp.stack;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.parms.DigitMap;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.parser.MgcpContentHandler;
import org.mobicents.mgcp.stack.parser.MgcpMessageParser;
import org.mobicents.mgcp.stack.parser.Utils;

/**
 * @author Oleg Kulikov
 * @author Amit Bhayani
 * 
 */

public class NotificationRequestHandler extends TransactionHandler {

	private Logger logger = Logger.getLogger(NotificationRequestHandler.class);

	private NotificationRequest command;
	private NotificationRequestResponse response;

	public NotificationRequestHandler(JainMgcpStackImpl stack) {
		super(stack);
	}

	public NotificationRequestHandler(JainMgcpStackImpl stack, InetAddress address, int port) {
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
		NotificationRequest req = (NotificationRequest) event;
		StringBuffer buffer = new StringBuffer();

		buffer.append("RQNT " + event.getTransactionHandle() + " " + req.getEndpointIdentifier() + " MGCP 1.0\n");

		if (req.getNotifiedEntity() != null) {
			buffer.append("N:" + req.getNotifiedEntity() + "\n");
		}

		buffer.append("X:" + req.getRequestIdentifier() + "\n");

		if (req.getDigitMap() != null) {
			// encode digit map
		}

		if (req.getSignalRequests() != null) {
			buffer.append("S:" + utils.encodeEventNames(req.getSignalRequests()) + "\n");
		}

		if (req.getRequestedEvents() != null) {
			buffer.append("R:" + utils.encodeRequestedEvents(req.getRequestedEvents()) + "\n");
		}

		if (req.getDetectEvents() != null) {
			buffer.append("T:" + utils.encodeEventNames(req.getDetectEvents()) + "\n");
		}

		return buffer.toString();

	}

	@Override
	public String encode(JainMgcpResponseEvent event) {

		NotificationRequestResponse response = (NotificationRequestResponse) event;
		ReturnCode returnCode = response.getReturnCode();

		String encodedEvent = (new StringBuffer().append(returnCode.getValue()).append(" ").append(
				response.getTransactionHandle()).append(" ").append(returnCode.getComment()).append("\n")).toString();

		if (logger.isDebugEnabled()) {
			logger.debug("NotificationRequestHandler : encode(JainMgcpResponseEvent event) = " + encodedEvent);
		}

		return encodedEvent;
	}

	private class CommandContentHandle implements MgcpContentHandler {

		public void header(String header) throws ParseException {
			String[] tokens = header.split("\\s");

			String verb = tokens[0].trim();
			String transactionID = tokens[1].trim();
			String version = tokens[3].trim() + " " + tokens[4].trim();

			int tid = Integer.parseInt(transactionID);
			EndpointIdentifier endpoint = utils.decodeEndpointIdentifier(tokens[2].trim());

			command = new NotificationRequest(getObjectSource(tid), endpoint, new RequestIdentifier("0"));
			command.setTransactionHandle(tid);
		}

		public void param(String name, String value) throws ParseException {
			if (name.equalsIgnoreCase("N")) {
				command.setNotifiedEntity(utils.decodeNotifiedEntity(value, true));
			} else if (name.equalsIgnoreCase("X")) {
				command.setRequestIdentifier(new RequestIdentifier(value));
			} else if (name.equalsIgnoreCase("R")) {
				command.setRequestedEvents(utils.decodeRequestedEventList(value));
			} else if (name.equalsIgnoreCase("S")) {
				command.setSignalRequests(utils.decodeEventNames(value));
			} else if (name.equalsIgnoreCase("T")) {
				command.setDetectEvents(utils.decodeEventNames(value));
			} else if (name.equalsIgnoreCase("D")) {
				command.setDigitMap(new DigitMap(value));
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
			response = new NotificationRequestResponse(stack, utils.decodeReturnCode(Integer.parseInt(tokens[0])));
			response.setTransactionHandle(tid);
		}

		public void param(String name, String value) throws ParseException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void sessionDescription(String sd) throws ParseException {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	@Override
	public JainMgcpResponseEvent getProvisionalResponse() {
		NotificationRequestResponse provisionalresponse = null;
		if (!sent) {
			provisionalresponse = new NotificationRequestResponse(commandEvent.getSource(),
					ReturnCode.Transaction_Being_Executed);
		}
		return provisionalresponse;

	}

}
