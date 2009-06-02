package org.mobicents.media.server.ctrl.mgcp.test;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class CA implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(CA.class);

	private JainMgcpStackProviderImpl caProvider;
	private int mgStack = 0;
	private boolean successCRCXRespReceived = false;
	private boolean successFormatNegotiationFail = false;

	public CA(JainMgcpStackProviderImpl caProvider, JainMgcpStackProviderImpl mgwProvider) {
		this.caProvider = caProvider;
		mgStack = mgwProvider.getJainMgcpStack().getPort();
	}

	public void sendSuccessCRCX() {

		try {
			caProvider.addJainMgcpListener(this);

			CallIdentifier callID = caProvider.getUniqueCallIdentifier();

			EndpointIdentifier endpointID = new EndpointIdentifier("/mobicents/media/aap/1", "127.0.0.1:" + mgStack);

			CreateConnection createConnection = new CreateConnection(this, callID, endpointID, ConnectionMode.SendRecv);

			String sdpData = "v=0\r\n" + "o=4855 13760799956958020 13760799956958020" + " IN IP4  127.0.0.1\r\n"
					+ "s=mysession session\r\n" + "p=+46 8 52018010\r\n" + "c=IN IP4  127.0.0.1\r\n" + "t=0 0\r\n"
					+ "m=audio 6022 RTP/AVP 0 4 18\r\n" + "a=rtpmap:0 PCMU/8000\r\n" + "a=rtpmap:4 G723/8000\r\n"
					+ "a=rtpmap:18 G729A/8000\r\n" + "a=ptime:20\r\n";

			createConnection.setRemoteConnectionDescriptor(new ConnectionDescriptor(sdpData));

			createConnection.setTransactionHandle(caProvider.getUniqueTransactionHandler());

			caProvider.sendMgcpEvents(new JainMgcpEvent[] { createConnection });

			logger.debug(" CreateConnection command sent for TxId " + createConnection.getTransactionHandle()
					+ " and CallId " + callID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MgcpMicrocontainerTest.fail("Unexpected Exception");
		}
	}

	public void sendFormatNegotiationFailCRCX() {

		try {
			caProvider.addJainMgcpListener(this);

			CallIdentifier callID = caProvider.getUniqueCallIdentifier();

			EndpointIdentifier endpointID = new EndpointIdentifier("/mobicents/media/aap/2", "127.0.0.1:" + mgStack);

			CreateConnection createConnection = new CreateConnection(this, callID, endpointID, ConnectionMode.SendRecv);

			String sdpData = "v=0\r\n" + "o=4855 13760799956958020 13760799956958020" + " IN IP4  127.0.0.1\r\n"
					+ "s=mysession session\r\n" + "p=+46 8 52018010\r\n" + "c=IN IP4  127.0.0.1\r\n" + "t=0 0\r\n"
					+ "m=audio 6023 RTP/AVP 102\r\n" + "a=rtpmap:102 G726-16/8000\r\n" + "a=ptime:20\r\n";

			createConnection.setRemoteConnectionDescriptor(new ConnectionDescriptor(sdpData));

			createConnection.setTransactionHandle(caProvider.getUniqueTransactionHandler());

			caProvider.sendMgcpEvents(new JainMgcpEvent[] { createConnection });

			logger.debug(" CreateConnection command sent for TxId " + createConnection.getTransactionHandle()
					+ " and CallId " + callID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MgcpMicrocontainerTest.fail("Unexpected Exception");
		}
	}

	public void checkSuccessCRCX() {
		MgcpMicrocontainerTest.assertTrue("Expect to receive CRCX Response", successCRCXRespReceived);

	}

	public void checkFormatNegotiationFailCRCX() {
		MgcpMicrocontainerTest.assertTrue(successFormatNegotiationFail);

	}

	public void transactionEnded(int handle) {
		logger.info("transactionEnded " + handle);

	}

	public void transactionRxTimedOut(JainMgcpCommandEvent command) {
		logger.info("transactionRxTimedOut " + command);

	}

	public void transactionTxTimedOut(JainMgcpCommandEvent command) {
		logger.info("transactionTxTimedOut " + command);

	}

	public void processMgcpCommandEvent(JainMgcpCommandEvent jainmgcpcommandevent) {
		logger.info("processMgcpCommandEvent " + jainmgcpcommandevent);
	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {
		logger.debug("processMgcpResponseEvent = " + jainmgcpresponseevent);

		switch (jainmgcpresponseevent.getObjectIdentifier()) {
		case Constants.RESP_CREATE_CONNECTION:
			CreateConnectionResponse crcxResp = (CreateConnectionResponse) jainmgcpresponseevent;
			switch (crcxResp.getReturnCode().getValue()) {
			case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
				successCRCXRespReceived = true;
				break;

			case ReturnCode.MISSING_REMOTECONNECTIONDESCRIPTOR:
				successFormatNegotiationFail = true;
				break;
			default:
				logger.error("CRCX Response is not successfull. Recived ReturCode = " + crcxResp.getReturnCode());
				successCRCXRespReceived = false;
				successFormatNegotiationFail = false;
				break;
			}
			break;
		default:
			logger.warn("This RESPONSE is unexpected " + jainmgcpresponseevent);
			break;

		}

	}

}
