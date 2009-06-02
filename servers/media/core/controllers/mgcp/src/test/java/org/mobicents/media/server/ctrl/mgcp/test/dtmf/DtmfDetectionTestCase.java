package org.mobicents.media.server.ctrl.mgcp.test.dtmf;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
import jain.protocol.ip.mgcp.message.ModifyConnection;
import jain.protocol.ip.mgcp.message.ModifyConnectionResponse;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.NotificationRequestResponse;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.NotifyResponse;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import java.net.URL;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mobicents.media.server.ctrl.mgcp.test.CallState;
import org.mobicents.media.server.ctrl.mgcp.test.MgcpMicrocontainerTest;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

/**
 * 
 * @author amit bhayani
 * 
 */
public class DtmfDetectionTestCase extends MgcpMicrocontainerTest implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(DtmfDetectionTestCase.class);

	private URL url = null;

	private Semaphore semaphore;

	private boolean CRCXRespRecd_ann = false;
	private boolean MDCXRespRecd_ann = false;
	private boolean RQNTRespRecd_ann = false;
	private boolean DLCXRespRecd_ann = false;

	private boolean ntfyCmdRecd = false;

	private int mgStack = 0;
	private EndpointIdentifier endpointIdentifier_ann;
	private String sdp_ann = null;
	private ConnectionIdentifier allocatedConnection_ann = null;
	private CallIdentifier callIdentifier_ann;
	private RequestIdentifier ri_ann;

	private boolean CRCXRespRecd_ivr = false;
	private boolean MDCXRespRecd_ivr = false;
	private boolean RQNTRespRecd_ivr = false;
	private boolean DLCXRespRecd_ivr = false;

	private EndpointIdentifier endpointIdentifier_ivr;
	private String sdp_ivr = null;
	private ConnectionIdentifier allocatedConnection_ivr;
	private CallIdentifier callIdentifier_ivr;
	private RequestIdentifier ri_ivr;

	private CallState state = CallState.INITIAL;
	private String recordDir;

	public DtmfDetectionTestCase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		semaphore = new Semaphore(0);

		JainMgcpStackProviderImpl mgwProvider = (JainMgcpStackProviderImpl) mgcpServerStack.getMgcpProvider();
		mgStack = mgwProvider.getJainMgcpStack().getPort();

	}

	@Test
	public void testDtmf() throws Exception {

		caProvider.addJainMgcpListener(this);

		callIdentifier_ann = caProvider.getUniqueCallIdentifier();

		EndpointIdentifier endpointID = new EndpointIdentifier("/mobicents/media/aap/$", "127.0.0.1:" + mgStack);
		CreateConnection createConnection = new CreateConnection(this, callIdentifier_ann, endpointID,
				jain.protocol.ip.mgcp.message.parms.ConnectionMode.SendOnly);

		createConnection.setTransactionHandle(caProvider.getUniqueTransactionHandler());

		this.state = CallState.SENT_CRCX1;

		caProvider.sendMgcpEvents(new JainMgcpEvent[] { createConnection });

		logger.debug(" CreateConnection command sent for TxId " + createConnection.getTransactionHandle()
				+ " and CallId " + callIdentifier_ann);

		// wait for another few secs
		semaphore.tryAcquire(10, TimeUnit.SECONDS);

		// Test of RespRec'd
		assertTrue("CRCX Ann Response received ", CRCXRespRecd_ann);
		assertTrue("CRCX Ivr Response received ", CRCXRespRecd_ivr);

		assertTrue("MDCX Ann Response received ", MDCXRespRecd_ann);
		assertTrue("MDCX Ivr Response received ", MDCXRespRecd_ivr);

		assertTrue("RQNT Ann Response received ", RQNTRespRecd_ann);
		assertTrue("RQNT Ivr Response received ", RQNTRespRecd_ivr);

		// Test of cmd rece'd
		assertTrue("NTFY cmd received ", ntfyCmdRecd);

		assertTrue("DLCX Ann Response received ", DLCXRespRecd_ann);
		assertTrue("DLCX Ivr Response received ", DLCXRespRecd_ivr);
	}

	public void transactionEnded(int paramInt) {
		// TODO Auto-generated method stub

	}

	public void transactionRxTimedOut(JainMgcpCommandEvent paramJainMgcpCommandEvent) {
		// TODO Auto-generated method stub

	}

	public void transactionTxTimedOut(JainMgcpCommandEvent paramJainMgcpCommandEvent) {
		// TODO Auto-generated method stub

	}

	public void processMgcpCommandEvent(JainMgcpCommandEvent jainmgcpcommandevent) {
		logger.debug("processMgcpCommandEvent = " + jainmgcpcommandevent);
		switch (jainmgcpcommandevent.getObjectIdentifier()) {
		case Constants.CMD_NOTIFY:
			if (this.state == CallState.SENT_RQNT2) {
				Notify notify = (Notify) jainmgcpcommandevent;

				ReturnCode rc = ReturnCode.Transaction_Executed_Normally;
				NotifyResponse notifyResponse = new NotifyResponse(this, rc);
				notifyResponse.setTransactionHandle(notify.getTransactionHandle());
				super.caProvider.sendMgcpEvents(new JainMgcpEvent[] { notifyResponse });

				EventName[] eventNames = notify.getObservedEvents();

				for (EventName eveName : eventNames) {
					System.out.println(eveName);
					if (eveName.getEventIdentifier().intValue() == MgcpEvent.DTMF_0) {
						ntfyCmdRecd = true;
					} else {
						logger.error("NTFY event is not OC");
						fail("Failed to receive OC NTFY");
					}
				}

				// Send DLCX
				DeleteConnection dlcx = new DeleteConnection(this, endpointIdentifier_ann);
				dlcx.setCallIdentifier(this.callIdentifier_ann);
				dlcx.setConnectionIdentifier(this.allocatedConnection_ann);
				dlcx.setTransactionHandle(caProvider.getUniqueTransactionHandler());
				this.state = CallState.SENT_DLCX1;
				super.caProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { dlcx });
			} else {
				fail("NTFY Command received but state is not CallState.SENT_RQNT2 ");
			}

			break;
		default:
			logger.error("Command received is not NTFY");
			fail("Failed to receive NTFY");
			break;
		}
	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {
		logger.debug("processMgcpResponseEvent = " + jainmgcpresponseevent);
		try {
			switch (jainmgcpresponseevent.getObjectIdentifier()) {
			case Constants.RESP_CREATE_CONNECTION:
				CreateConnectionResponse crcxResp = (CreateConnectionResponse) jainmgcpresponseevent;
				switch (crcxResp.getReturnCode().getValue()) {
				case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
					if (this.state == CallState.SENT_CRCX1) {
						CRCXRespRecd_ann = true;
						endpointIdentifier_ann = crcxResp.getSpecificEndpointIdentifier();
						if (endpointIdentifier_ann == null) {
							fail("The SpecificEndpointIdentifier returned by CRCX is null");
						} else {
							sdp_ann = crcxResp.getLocalConnectionDescriptor().toString();
							allocatedConnection_ann = crcxResp.getConnectionIdentifier();

							callIdentifier_ivr = caProvider.getUniqueCallIdentifier();
							EndpointIdentifier endpointID = new EndpointIdentifier("/mobicents/media/IVR/$",
									"127.0.0.1:" + mgStack);
							CreateConnection createConnection = new CreateConnection(this, callIdentifier_ivr,
									endpointID, jain.protocol.ip.mgcp.message.parms.ConnectionMode.SendOnly);

							createConnection.setTransactionHandle(caProvider.getUniqueTransactionHandler());

							this.state = CallState.SENT_CRCX2;

							caProvider.sendMgcpEvents(new JainMgcpEvent[] { createConnection });

							logger.debug(" CreateConnection command sent for TxId "
									+ createConnection.getTransactionHandle() + " and CallId " + callIdentifier_ivr);

						}
					} else if (this.state == CallState.SENT_CRCX2) {

						CRCXRespRecd_ivr = true;
						endpointIdentifier_ivr = crcxResp.getSpecificEndpointIdentifier();
						if (endpointIdentifier_ivr == null) {
							fail("The SpecificEndpointIdentifier returned by CRCX is null");
						} else {
							sdp_ivr = crcxResp.getLocalConnectionDescriptor().toString();
							allocatedConnection_ivr = crcxResp.getConnectionIdentifier();

							ModifyConnection modifyConnection = new ModifyConnection(this, callIdentifier_ann,
									endpointIdentifier_ann, allocatedConnection_ann);
							ConnectionDescriptor connectionDescriptor = new ConnectionDescriptor(this.sdp_ivr);
							modifyConnection.setRemoteConnectionDescriptor(connectionDescriptor);

							modifyConnection.setTransactionHandle(caProvider.getUniqueTransactionHandler());

							this.state = CallState.SENT_MDCX1;
							caProvider.sendMgcpEvents(new JainMgcpEvent[] { modifyConnection });

							logger.debug(" MDCX command sent for TxId " + modifyConnection.getTransactionHandle()
									+ " and CallId " + callIdentifier_ann);
						}

					} else {
						fail("CRCX Response received but state is neither CallState.SENT_CRCX1 nor CallState.SENT_CRCX2 ");
					}

					break;

				default:
					logger.error("CRCX Response is not successfull. Recived ReturCode = " + crcxResp.getReturnCode());
					fail("The CRCX didn't execute properly. Response = " + crcxResp);
					break;
				}
				break;
			case Constants.RESP_MODIFY_CONNECTION:
				ModifyConnectionResponse mdcxResp = (ModifyConnectionResponse) jainmgcpresponseevent;
				switch (mdcxResp.getReturnCode().getValue()) {
				case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
					if (this.state == CallState.SENT_MDCX1) {
						MDCXRespRecd_ann = true;

						ModifyConnection modifyConnection = new ModifyConnection(this, callIdentifier_ivr,
								endpointIdentifier_ivr, allocatedConnection_ivr);
						ConnectionDescriptor connectionDescriptor = new ConnectionDescriptor(this.sdp_ann);
						modifyConnection.setRemoteConnectionDescriptor(connectionDescriptor);

						modifyConnection.setTransactionHandle(caProvider.getUniqueTransactionHandler());

						this.state = CallState.SENT_MDCX2;
						caProvider.sendMgcpEvents(new JainMgcpEvent[] { modifyConnection });

						logger.debug(" MDCX command sent for TxId " + modifyConnection.getTransactionHandle()
								+ " and CallId " + callIdentifier_ann);

					} else if (this.state == CallState.SENT_MDCX2) {
						MDCXRespRecd_ivr = true;

						ri_ivr = caProvider.getUniqueRequestIdentifier();
						NotificationRequest notificationRequest = new NotificationRequest(this, endpointIdentifier_ivr,
								ri_ivr);

						RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };

						RequestedEvent[] requestedEvents = { new RequestedEvent(new EventName(PackageName.Dtmf,
								MgcpEvent.dtmf0, null), actions) };
						notificationRequest.setRequestedEvents(requestedEvents);

						NotifiedEntity notifiedEntity = new NotifiedEntity(caIPAddress.getHostName(), caIPAddress
								.getHostAddress(), caStack.getPort());
						notificationRequest.setNotifiedEntity(notifiedEntity);

						notificationRequest.setTransactionHandle(caProvider.getUniqueTransactionHandler());

						this.state = CallState.SENT_RQNT1;
						super.caProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { notificationRequest });

					} else {
						fail("MDCX Response received but state is neither CallState.SENT_MDCX1 nor CallState.SENT_MDCX2 ");
					}
					break;

				default:
					logger.error("MDCX Response is not successfull. Recived ReturCode = " + mdcxResp.getReturnCode());
					fail("The MDCX didn't execute properly. Response = " + mdcxResp);
					break;

				}

				break;
			case Constants.RESP_NOTIFICATION_REQUEST:
				NotificationRequestResponse rqntRes = (NotificationRequestResponse) jainmgcpresponseevent;
				switch (rqntRes.getReturnCode().getValue()) {
				case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
					if (this.state == CallState.SENT_RQNT1) {
						RQNTRespRecd_ann = true;
						ri_ann = caProvider.getUniqueRequestIdentifier();
						NotificationRequest notificationRequest = new NotificationRequest(this, endpointIdentifier_ann,
								ri_ann);

						EventName[] signalRequests = { new EventName(PackageName.Dtmf, MgcpEvent.dtmf0, null) };

						notificationRequest.setSignalRequests(signalRequests);
						NotifiedEntity notifiedEntity = new NotifiedEntity(caIPAddress.getHostName(), caIPAddress
								.getHostAddress(), caStack.getPort());
						notificationRequest.setNotifiedEntity(notifiedEntity);
						notificationRequest.setTransactionHandle(caProvider.getUniqueTransactionHandler());

						this.state = CallState.SENT_RQNT2;
						super.caProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { notificationRequest });

					} else if (this.state == CallState.SENT_RQNT2) {
						RQNTRespRecd_ivr = true;
					} else {
						fail("RQNT Response received but state is neither CallState.SENT_RQNT1 nor CallState.SENT_RQNT2 ");
					}

					break;
				default:
					logger.error("RQNT Response is not successfull. Recieved ReturCode = " + rqntRes.getReturnCode());
					fail("The RQNT didn't execute properly. Response = " + rqntRes);
					break;
				}

				break;
			case Constants.RESP_DELETE_CONNECTION:
				DeleteConnectionResponse dlcxResp = (DeleteConnectionResponse) jainmgcpresponseevent;
				switch (dlcxResp.getReturnCode().getValue()) {
				case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
					if (this.state == CallState.SENT_DLCX1) {
						DLCXRespRecd_ann = true;
						// Send DLCX
						DeleteConnection dlcx = new DeleteConnection(this, endpointIdentifier_ivr);
						dlcx.setCallIdentifier(this.callIdentifier_ivr);
						dlcx.setConnectionIdentifier(this.allocatedConnection_ivr);
						dlcx.setTransactionHandle(caProvider.getUniqueTransactionHandler());

						this.state = CallState.SENT_DLCX2;
						super.caProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { dlcx });
					} else if (this.state == CallState.SENT_DLCX2) {
						DLCXRespRecd_ivr = true;
						semaphore.release();
					} else {
						fail("DLCX Response received but state is neither CallState.SENT_DLCX1 nor CallState.SENT_DLCX2 ");
					}
					break;
				default:
					logger.error("DLCX Response is not successfull. Recieved ReturCode = " + dlcxResp.getReturnCode());
					fail("The DLCX didn't execute properly. Response = " + dlcxResp);
					break;
				}
				break;
			default:
				logger.warn("This RESPONSE is unexpected " + jainmgcpresponseevent);
				fail("Neither CRCX Resp, RQNT Resp received but " + jainmgcpresponseevent);
				break;

			}
		} catch (Exception e) {
			logger.error(e);
			fail("Failure at CRCX Response time");

		}
	}
}
