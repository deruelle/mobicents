package org.mobicents.mgcp.stack;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.AuditConnection;
import jain.protocol.ip.mgcp.message.AuditConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.InfoCode;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.parser.MgcpContentHandler;
import org.mobicents.mgcp.stack.parser.MgcpMessageParser;

public class AuditConnectionHandler extends TransactionHandler {

	private Logger logger = Logger.getLogger(AuditConnectionHandler.class);

	private AuditConnection command;
	private AuditConnectionResponse response;
	private ConnectionIdentifier connectionIdentifier = null;
	private EndpointIdentifier endpointId = null;
	private InfoCode[] requestedInfo = null;
	int tid = 0;

	public AuditConnectionHandler(JainMgcpStackImpl stack) {
		super(stack);
	}

	public AuditConnectionHandler(JainMgcpStackImpl stack, InetAddress address, int port) {
		super(stack, address, port);
	}

	@Override
	public JainMgcpCommandEvent decodeCommand(String message) throws ParseException {
		if (logger.isDebugEnabled()) {
			logger.debug("Decoding AUEP command");
		}

		MgcpMessageParser parser = new MgcpMessageParser(new CommandContentHandle());
		try {
			parser.parse(message);
			command = new AuditConnection(getObjectSource(tid), endpointId, connectionIdentifier, requestedInfo);
			command.setTransactionHandle(tid);
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), -1);
		}

		return command;
	}

	@Override
	public JainMgcpResponseEvent decodeResponse(String message) throws ParseException {

		if (logger.isDebugEnabled()) {
			logger.debug("Decoding AUEP response command");
		}

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
		if (logger.isDebugEnabled()) {
			logger.debug("Encoding AuditConnection object into MGCP audit connection command");
		}

		// encode message header

		AuditConnection evt = (AuditConnection) event;
		String msg = "AUCX " + evt.getTransactionHandle() + " " + evt.getEndpointIdentifier() + " MGCP 1.0\n";

		// encode mandatory parameters
		if (evt.getConnectionIdentifier() != null) {
			msg += "I:" + evt.getConnectionIdentifier() + "\n";
		}

		InfoCode[] requestedInfos = evt.getRequestedInfo();
		if (requestedInfos != null) {
			msg += "F: " + utils.encodeInfoCodeList(requestedInfos);
		}

		return msg;
	}

	@Override
	public String encode(JainMgcpResponseEvent event) {
		return null;
	}

	@Override
	public JainMgcpResponseEvent getProvisionalResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	private class CommandContentHandle implements MgcpContentHandler {

		public CommandContentHandle() {
		}

		/**
		 * Receive notification of the header of a message. Parser will call
		 * this method to report about header reading.
		 * 
		 * @param header
		 *            the header from the message.
		 */
		public void header(String header) throws ParseException {
			String[] tokens = header.split("\\s");

			String verb = tokens[0].trim();
			String transactionID = tokens[1].trim();
			String version = tokens[3].trim() + " " + tokens[4].trim();

			tid = Integer.parseInt(transactionID);
			endpointId = utils.decodeEndpointIdentifier(tokens[2].trim());

			// Can't create the AuditConnection object here as
			// ConnectionIdentifier and InfoCode[] is required

		}

		/**
		 * Receive notification of the parameter of a message. Parser will call
		 * this method to report about parameter reading.
		 * 
		 * @param name
		 *            the name of the parameter
		 * @param value
		 *            the value of the parameter.
		 */
		public void param(String name, String value) throws ParseException {
			if (name.equalsIgnoreCase("I")) {
				connectionIdentifier = new ConnectionIdentifier(value);
			} else if (name.equalsIgnoreCase("F")) {
				requestedInfo = utils.decodeInfoCodeList(value);
			} else {
				logger.error("Unknown code " + name);
			}
		}

		/**
		 * Receive notification of the session description. Parser will call
		 * this method to report about session descriptor reading.
		 * 
		 * @param sd
		 *            the session description from message.
		 */
		public void sessionDescription(String sd) throws ParseException {
			throw new ParseException("SessionDescription shouldn't have been included in AUEP command", 0);
		}
	}

	private class ResponseContentHandle implements MgcpContentHandler {

		public ResponseContentHandle() {
		}

		/**
		 * Receive notification of the header of a message. Parser will call
		 * this method to report about header reading.
		 * 
		 * @param header
		 *            the header from the message.
		 */
		public void header(String header) throws ParseException {
			String[] tokens = header.split("\\s");

			int tid = Integer.parseInt(tokens[1]);
			response = new AuditConnectionResponse(stack, utils.decodeReturnCode(Integer.parseInt(tokens[0])));
			response.setTransactionHandle(tid);
		}

		/**
		 * Receive notification of the parameter of a message. Parser will call
		 * this method to report about parameter reading.
		 * 
		 * @param name
		 *            the name of the paremeter
		 * @param value
		 *            the value of the parameter.
		 */
		public void param(String name, String value) throws ParseException {
			if (name.equalsIgnoreCase("C")) {
				response.setCallIdentifier(new CallIdentifier(value));
			} else if (name.equalsIgnoreCase("N")) {
				NotifiedEntity n = utils.decodeNotifiedEntity(value, true);
				response.setNotifiedEntity(n);
			}
		}

		/**
		 * Receive notification of the session description. Parser will call
		 * this method to report about session descriptor reading.
		 * 
		 * @param sd
		 *            the session description from message.
		 */
		public void sessionDescription(String sd) throws ParseException {
			// response.setLocalConnectionDescriptor(new
			// ConnectionDescriptor(sd));
		}
	}

}
