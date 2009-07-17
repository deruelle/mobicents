/*
 * File Name     : CreateConnectionHandle.java
 *
 * The JAIN MGCP API implementaion.
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

package org.mobicents.mgcp.stack;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.NotificationRequestParms;
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
 * 
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class DeleteConnectionHandler extends TransactionHandler {

	private DeleteConnection command;
	private DeleteConnectionResponse response;

	private static final Logger logger = Logger.getLogger(DeleteConnectionHandler.class);

	/** Creates a new instance of CreateConnectionHandle */
	public DeleteConnectionHandler(JainMgcpStackImpl stack) {
		super(stack);
	}

	public DeleteConnectionHandler(JainMgcpStackImpl stack, InetAddress address, int port) {
		super(stack, address, port);
	}

	public JainMgcpCommandEvent decodeCommand(String message) throws ParseException {
		Utils utils = utilsFactory.allocate();
		MgcpMessageParser parser = new MgcpMessageParser(new CommandContentHandle(utils));
		try {
			parser.parse(message);
		} catch (IOException e) {
			logger.error("Decode of DLCX command failed", e);
		} finally {
			utilsFactory.deallocate(utils);
		}

		return command;
	}

	public JainMgcpResponseEvent decodeResponse(String message) throws ParseException {
		Utils utils = utilsFactory.allocate();
		MgcpMessageParser parser = new MgcpMessageParser(new ResponseContentHandle(utils));
		try {
			parser.parse(message);
		} catch (Exception e) {
			logger.error("Decode of DLCX Response failed", e);
		} finally {
			utilsFactory.deallocate(utils);
		}

		return response;
	}

	public String encode(JainMgcpCommandEvent event) {

		// encode message header
		Utils utils = utilsFactory.allocate();
		DeleteConnection evt = (DeleteConnection) event;
		StringBuffer s = new StringBuffer();
		s.append("DLCX ").append(evt.getTransactionHandle()).append(SINGLE_CHAR_SPACE).append(
				evt.getEndpointIdentifier()).append(SINGLE_CHAR_SPACE).append(MGCP_VERSION).append(NEW_LINE);

		// encode optional parameters

		if (evt.getBearerInformation() != null) {
			s.append("B:e:").append(evt.getBearerInformation()).append(NEW_LINE);

		}

		if (evt.getCallIdentifier() != null) {
			s.append("C:").append(evt.getCallIdentifier()).append(NEW_LINE);

		}

		if (evt.getConnectionIdentifier() != null) {
			s.append("I:").append(evt.getConnectionIdentifier()).append(NEW_LINE);

		}

		if (evt.getConnectionParms() != null) {
			s.append("P:").append(utils.encodeConnectionParms(evt.getConnectionParms())).append(NEW_LINE);

		}

		if (evt.getNotificationRequestParms() != null) {
			s.append(utils.encodeNotificationRequestParms(evt.getNotificationRequestParms()));

		}

		if (evt.getReasonCode() != null) {
			s.append("E:").append(evt.getReasonCode());

		}
		utilsFactory.deallocate(utils);
		return s.toString();

	}

	public String encode(JainMgcpResponseEvent event) {
		DeleteConnectionResponse response = (DeleteConnectionResponse) event;
		ReturnCode returnCode = response.getReturnCode();
		StringBuffer s = new StringBuffer();
		s.append(returnCode.getValue()).append(SINGLE_CHAR_SPACE).append(response.getTransactionHandle()).append(
				SINGLE_CHAR_SPACE).append(returnCode.getComment()).append(NEW_LINE);

		if (response.getConnectionParms() != null) {
			s.append(response.getConnectionParms()).append(NEW_LINE);

		}

		return s.toString();

	}

	public class CommandContentHandle implements MgcpContentHandler {
		private Utils utils = null;

		public CommandContentHandle(Utils utils) {
			this.utils = utils;
		}

		/**
		 * Receive notification of the header of a message. Parser will call
		 * this method to report about header reading.
		 * 
		 * @param header
		 *            the header from the message.
		 */
		public void header(String header) throws ParseException {
			
			command = new DeleteConnection(source != null ? source : stack, endpoint);
			command.setTransactionHandle(remoteTID);
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
			if (name.equalsIgnoreCase("B")) {
				command.setBearerInformation(utils.decodeBearerInformation(value));
			} else if (name.equalsIgnoreCase("c")) {
				command.setCallIdentifier(new CallIdentifier(value));
			} else if (name.equalsIgnoreCase("I")) {
				command.setConnectionIdentifier(new ConnectionIdentifier(value));
			} else if (name.equalsIgnoreCase("X")) {
				command.setNotificationRequestParms(new NotificationRequestParms(new RequestIdentifier(value)));
			} else if (name.equalsIgnoreCase("R")) {
				command.getNotificationRequestParms().setRequestedEvents(utils.decodeRequestedEventList(value));
			} else if (name.equalsIgnoreCase("S")) {
				command.getNotificationRequestParms().setSignalRequests(utils.decodeEventNames(value));
			} else if (name.equalsIgnoreCase("T")) {
				command.getNotificationRequestParms().setDetectEvents(utils.decodeEventNames(value));
			} else if (name.equalsIgnoreCase("P")) {
				command.setConnectionParms(utils.decodeConnectionParms(value));
			} else if (name.equalsIgnoreCase("E")) {
				command.setReasonCode(utils.decodeReasonCode(value));
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
			// do nothing
		}
	}

	private class ResponseContentHandle implements MgcpContentHandler {
		private Utils utils = null;

		public ResponseContentHandle(Utils utils) {
			this.utils = utils;
		}

		/**
		 * Receive notification of the header of a message. Parser will call
		 * this method to report about header reading.
		 * 
		 * @param header
		 *            the header from the message.
		 */
		public void header(String header) throws ParseException {
			String[] tokens = utils.splitStringBySpace(header);

			int tid = Integer.parseInt(tokens[1]);
			response = new DeleteConnectionResponse(source != null ? source : stack, utils.decodeReturnCode(Integer
					.parseInt(tokens[0])));
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
			if (name.equalsIgnoreCase("I")) {
				// TODO connection params
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
			// not used
		}
	}

	@Override
	public JainMgcpResponseEvent getProvisionalResponse() {
		DeleteConnectionResponse provisionalResponse = null;

		if (!sent) {
			provisionalResponse = new DeleteConnectionResponse(source != null ? source : stack,
					ReturnCode.Transaction_Being_Executed);
			provisionalResponse.setTransactionHandle(commandEvent.getTransactionHandle());
		}
		return provisionalResponse;
	}

}
