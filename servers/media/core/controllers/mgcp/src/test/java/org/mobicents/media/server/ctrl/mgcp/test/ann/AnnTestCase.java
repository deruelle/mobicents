package org.mobicents.media.server.ctrl.mgcp.test.ann;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.DeleteConnectionResponse;
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

import java.io.File;
import java.net.URL;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.ctrl.mgcp.test.MgcpMicrocontainerTest;
import org.mobicents.media.server.impl.resource.audio.RecorderEvent;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.resource.Recorder;
import org.mobicents.mgcp.stack.JainMgcpExtendedListener;
import org.mobicents.mgcp.stack.JainMgcpStackProviderImpl;

/**
 * 
 * @author amit bhayani
 * 
 */
public class AnnTestCase extends MgcpMicrocontainerTest implements JainMgcpExtendedListener {

	private static Logger logger = Logger.getLogger(AnnTestCase.class);

	private EndpointImpl ivrEnp;

	private Recorder recorder;
	private Connection rxConnection;

	private URL url = null;

	private Semaphore semaphore;

	private boolean stopped = false;
	private boolean completed = false;
	private boolean failed = false;

	private boolean CRCXRespRecd = false;
	private boolean RQNTRespRecd = false;
	private boolean DLCXRespRecd = false;

	private boolean ntfyCmdRecd = false;

	private int mgStack = 0;
	private EndpointIdentifier endpointIdentifier;
	private ConnectionIdentifier allocatedConnection = null;
	private CallIdentifier callIdentifier;

	public AnnTestCase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		semaphore = new Semaphore(0);

		JainMgcpStackProviderImpl mgwProvider = (JainMgcpStackProviderImpl) mgcpServerStack.getMgcpProvider();
		mgStack = mgwProvider.getJainMgcpStack().getPort();

		ivrEnp = (EndpointImpl) getBean("IVREndpoint");
		assertNotNull(ivrEnp);

	}

	@Test
	public void testSimpleTransmission() throws Exception {

		url = AnnTestCase.class.getClassLoader()
				.getResource("org/mobicents/media/server/ctrl/mgcp/test/ann/8kulaw.wav");

		// set-up recorder
		recorder = (Recorder) ivrEnp.getComponent("audio.recorder");
		assertNotNull(recorder);

		String tempFilePath = url.getPath();
		String recordDir = tempFilePath.substring(0, tempFilePath.lastIndexOf('/'));

		recorder.setRecordDir(recordDir);
		// let us record for 7 sec
		recorder.setRecordTime(7);
		recorder.addListener(new RecorderListener());

		rxConnection = ivrEnp.createConnection(ConnectionMode.SEND_RECV);
		String sdpData = rxConnection.getLocalDescriptor();

		caProvider.addJainMgcpListener(this);

		callIdentifier = caProvider.getUniqueCallIdentifier();
		EndpointIdentifier endpointID = new EndpointIdentifier("/mobicents/media/aap/$", "127.0.0.1:" + mgStack);
		CreateConnection createConnection = new CreateConnection(this, callIdentifier, endpointID,
				jain.protocol.ip.mgcp.message.parms.ConnectionMode.RecvOnly);

		createConnection.setRemoteConnectionDescriptor(new ConnectionDescriptor(sdpData));

		createConnection.setTransactionHandle(caProvider.getUniqueTransactionHandler());

		caProvider.sendMgcpEvents(new JainMgcpEvent[] { createConnection });

		logger.debug(" CreateConnection command sent for TxId " + createConnection.getTransactionHandle()
				+ " and CallId " + callIdentifier);

		// wait for another few secs
		semaphore.tryAcquire(20, TimeUnit.SECONDS);

		// Test of RecorderListener
		assertFalse("Recorder Stoped ", stopped);
		assertTrue("Recorder Completed", completed);
		assertFalse("Recorder Failed", failed);

		// Test of RespRec'd
		assertTrue("CRCX Response received ", CRCXRespRecd);
		assertTrue("RQNT Response received ", RQNTRespRecd);

		// Test of cmd rece'd
		assertTrue("NTFY cmd received ", ntfyCmdRecd);

		assertTrue("DLCX Response received ", DLCXRespRecd);

		// Test of Recodedfile exist
		try {
			File file = new File(recordDir + "/" + "annTestCase/8kulaw.wav");
			assertEquals(true, file.exists());

			// File length greate than 50
			assertTrue(file.length() > 50);
		} catch (Exception e) {
			logger.error(e);
			fail("Recoded File 8kulaw.wav not created ");
		}

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
			Notify notify = (Notify) jainmgcpcommandevent;

			ReturnCode rc = ReturnCode.Transaction_Executed_Normally;
			NotifyResponse notifyResponse = new NotifyResponse(this, rc);
			notifyResponse.setTransactionHandle(notify.getTransactionHandle());
			super.caProvider.sendMgcpEvents(new JainMgcpEvent[] { notifyResponse });

			// Send DLCX
			sendDLCX(notify.getEndpointIdentifier());

			EventName[] eventNames = notify.getObservedEvents();

			for (EventName eveName : eventNames) {
				System.out.println(eveName);
				if (eveName.getEventIdentifier().intValue() == MgcpEvent.REPORT_ON_COMPLETION) {
					ntfyCmdRecd = true;
				} else {
					logger.error("NTFY event is not OC");
					fail("Failed to receive OC NTFY");
				}
			}

			break;
		default:
			logger.error("Command received is not NTFY");
			fail("Failed to receive NTFY");
			break;
		}
	}

	private void sendDLCX(EndpointIdentifier endpointIdentifier) {
		DeleteConnection dlcx = new DeleteConnection(this, endpointIdentifier);
		dlcx.setCallIdentifier(this.callIdentifier);
		dlcx.setConnectionIdentifier(this.allocatedConnection);
		dlcx.setTransactionHandle(caProvider.getUniqueTransactionHandler());
		super.caProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { dlcx });
	}

	public void processMgcpResponseEvent(JainMgcpResponseEvent jainmgcpresponseevent) {
		logger.debug("processMgcpResponseEvent = " + jainmgcpresponseevent);
		try {
			switch (jainmgcpresponseevent.getObjectIdentifier()) {
			case Constants.RESP_CREATE_CONNECTION:
				CreateConnectionResponse crcxResp = (CreateConnectionResponse) jainmgcpresponseevent;
				switch (crcxResp.getReturnCode().getValue()) {
				case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
					CRCXRespRecd = true;
					endpointIdentifier = crcxResp.getSpecificEndpointIdentifier();
					if (endpointIdentifier == null) {
						fail("The SpecificEndpointIdentifier returned by CRCX is null");
					} else {
						rxConnection.setRemoteDescriptor(crcxResp.getLocalConnectionDescriptor().toString());
						allocatedConnection = crcxResp.getConnectionIdentifier();
						sendRQNT(endpointIdentifier);
					}
					break;

				default:
					logger.error("CRCX Response is not successfull. Recived ReturCode = " + crcxResp.getReturnCode());
					fail("The CRCX didn't execute properly. Response = " + crcxResp);
					break;
				}
				break;
			case Constants.RESP_NOTIFICATION_REQUEST:
				NotificationRequestResponse rqntRes = (NotificationRequestResponse) jainmgcpresponseevent;
				switch (rqntRes.getReturnCode().getValue()) {
				case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
					// lets begin recording here
					recorder.start("annTestCase/8kulaw.wav");
					RQNTRespRecd = true;
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
					// lets begin recording here
					DLCXRespRecd = true;
					semaphore.release();
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

	private void sendRQNT(EndpointIdentifier endpointIdentifier) {
		RequestIdentifier ri = caProvider.getUniqueRequestIdentifier();
		NotificationRequest notificationRequest = new NotificationRequest(this, endpointIdentifier, ri);

		EventName[] signalRequests = { new EventName(PackageName.Announcement, MgcpEvent.ann.withParm(url
				.toExternalForm()), null) };

		notificationRequest.setSignalRequests(signalRequests);

		RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };

		RequestedEvent[] requestedEvents = { new RequestedEvent(new EventName(PackageName.Announcement, MgcpEvent.oc,
				null), actions) };
		notificationRequest.setRequestedEvents(requestedEvents);

		NotifiedEntity notifiedEntity = new NotifiedEntity(caIPAddress.getHostName(), caIPAddress.getHostAddress(),
				caStack.getPort());
		notificationRequest.setNotifiedEntity(notifiedEntity);

		notificationRequest.setTransactionHandle(caProvider.getUniqueTransactionHandler());

		super.caProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { notificationRequest });

	}

	private class RecorderListener implements NotificationListener {

		public void update(NotifyEvent event) {
			switch (event.getEventID()) {
			case RecorderEvent.STOPPED:
				System.out.println("Recorder STOPPED");
				stopped = true;
				// semaphore.release();
				break;
			case RecorderEvent.DURATION_OVER:
				System.out.println("Recorder DURATION_OVER");
				completed = true;
				// semaphore.release();
				break;
			case RecorderEvent.FAILED:
				System.out.println("Recorder FAILED");
				failed = true;
				// semaphore.release();
				break;
			}
		}

	}

}
