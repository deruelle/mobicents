package test.msgflow.networkconnection;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.ModifyConnectionResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;

import java.util.TooManyListenersException;

import org.apache.log4j.Logger;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

public class MGW implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(MGW.class);
	private boolean responseSent = false;

	JainMgcpStackProviderImpl mgwProvider;

	public MGW(JainMgcpStackProviderImpl mgwProvider) {
		this.mgwProvider = mgwProvider;
		try {
			this.mgwProvider.addJainMgcpListener(this);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
			NetworkConnectionTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		NetworkConnectionTest.assertTrue("Expect to sent CRCX Response", responseSent);
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

		switch (jainmgcpcommandevent.getObjectIdentifier()) {
		case Constants.CMD_CREATE_CONNECTION:

			String identifier = ((CallIdentifier) mgwProvider.getUniqueCallIdentifier()).toString();
			ConnectionIdentifier connectionIdentifier = new ConnectionIdentifier(identifier);

			CreateConnectionResponse responseCRCX = new CreateConnectionResponse(jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally, connectionIdentifier);

			responseCRCX.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
			try {
				// FIXME: we asume there is wildcard - "any of"
				CreateConnection cc = (CreateConnection) jainmgcpcommandevent;
				EndpointIdentifier wildcard = cc.getEndpointIdentifier();
				EndpointIdentifier specific = new EndpointIdentifier(wildcard.getLocalEndpointName().replace("$", "")
						+ "test-1", wildcard.getDomainName());
				responseCRCX.setSpecificEndpointIdentifier(specific);
			} catch (Exception e) {
				e.printStackTrace();
			}

			String LOCAL_SDP = "v=0\n" + "m=audio 1234 RTP/AVP  0 \n" + "c=IN IP4 192.168.145.2\n"
					+ "a=rtpmap:0 PCMU/8000\n";

			ConnectionDescriptor localConnectionDescriptor = new ConnectionDescriptor(LOCAL_SDP);
			responseCRCX.setLocalConnectionDescriptor(localConnectionDescriptor);
			mgwProvider.sendMgcpEvents(new JainMgcpEvent[] { responseCRCX });

			responseSent = true;

			break;
		case Constants.CMD_MODIFY_CONNECTION:

			ModifyConnectionResponse response = new ModifyConnectionResponse(jainmgcpcommandevent.getSource(),
					ReturnCode.Transaction_Executed_Normally);
			response.setTransactionHandle(jainmgcpcommandevent.getTransactionHandle());
			mgwProvider.sendMgcpEvents(new JainMgcpEvent[] { response });

			responseSent = true;

			break;
		default:
			logger.warn("This REQUEST is unexpected " + jainmgcpcommandevent);
			break;

		}

	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {
		logger.info("processMgcpResponseEvent " + jainmgcpresponseevent);

	}

}