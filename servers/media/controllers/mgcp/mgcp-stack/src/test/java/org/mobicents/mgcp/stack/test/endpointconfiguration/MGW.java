package org.mobicents.mgcp.stack.test.endpointconfiguration;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.EndpointConfiguration;
import jain.protocol.ip.mgcp.message.EndpointConfigurationResponse;
import jain.protocol.ip.mgcp.message.parms.BearerInformation;
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
			EndpointConfigurationTest.fail("Unexpected Exception");
		}
	}

	public void checkState() {
		EndpointConfigurationTest.assertTrue("Expect to sent CRCX Response", responseSent);
	}

	public void transactionEnded(int handle) {
		logger.info("transactionEnded " + handle);

	}

	public void transactionRxTimedOut(JainMgcpCommandEvent command) {
		logger.info("transactionRxTimedOut " + command);

	}

	public void transactionTxTimedOut(JainMgcpCommandEvent command) {
		logger.info("transactionTxTimedOut " + command);
		EndpointConfigurationTest.fail("Transaction Timed Out while sending the EPCF Response");

	}

	public void processMgcpCommandEvent(JainMgcpCommandEvent jainmgcpcommandevent) {
		logger.info("processMgcpCommandEvent " + jainmgcpcommandevent);

		switch (jainmgcpcommandevent.getObjectIdentifier()) {
		case Constants.CMD_ENDPOINT_CONFIGURATION:
			EndpointConfiguration endpointConfiguration = (EndpointConfiguration) jainmgcpcommandevent;
			BearerInformation b = endpointConfiguration.getBearerInformation();

			EndpointConfigurationTest.assertEquals(BearerInformation.ENC_METHOD_A_LAW, b.getEncodingMethod());

			EndpointConfigurationResponse response = new EndpointConfigurationResponse(
					jainmgcpcommandevent.getSource(), ReturnCode.Transaction_Executed_Normally);

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
