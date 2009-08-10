package org.mobicents.media.server.ctrl.mgcp.test.tone;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mobicents.media.server.ctrl.mgcp.test.Connection;
import org.mobicents.media.server.ctrl.mgcp.test.MgcpMicrocontainerTest;

/**
 * 
 * @author amit.bhayani
 *
 */
public class MultiFreqToneTestCase extends MgcpMicrocontainerTest {

	private final static String AAP = "/mobicents/media/aap/$";
	private final static String IVR = "/mobicents/media/IVR/$";
	private final static String PR = "/mobicents/media/packetrelay/$";
	private Connection rxConnection;
	private Connection txConnection;
	private Semaphore semaphore;
	private RequestIdentifier genToneID;
	private RequestIdentifier detToneID;
	private JainMgcpResponseEvent response;
	private boolean oc = false;

	private EventName[] events;

	public MultiFreqToneTestCase(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {

		String localSdp;
		String otherSdp;
		super.setUp();
		semaphore = new Semaphore(0);

		txConnection = createConnection(PR, ConnectionMode.SendRecv, null);
		localSdp = txConnection.getLocalSdp();
		txConnection = createLocalConnection(txConnection.getEndpoint().getLocalEndpointName(),
				ConnectionMode.SendRecv, AAP);

		// txConnection = createConnection(AAP, ConnectionMode.SendOnly, null);
		rxConnection = createConnection(PR, ConnectionMode.SendRecv, localSdp);
		otherSdp = rxConnection.getLocalSdp();
		rxConnection = createLocalConnection(rxConnection.getEndpoint().getLocalEndpointName(),
				ConnectionMode.SendRecv, IVR);

		// rxConnection = createConnection(IVR, ConnectionMode.SendRecv, txConnection.getLocalSdp());

		modifyConnection(txConnection, otherSdp);

		genToneID = new RequestIdentifier("1");
		detToneID = new RequestIdentifier("2");

		Thread.currentThread().sleep(2000);
	}

	private void requestMFToneDet(MgcpEvent mfEvent) {
		NotificationRequest notificationRequest = new NotificationRequest(this, rxConnection.getSecondEndpoint(), detToneID);

		RequestedAction[] actions = new RequestedAction[] { RequestedAction.NotifyImmediately };

		RequestedEvent[] requestedEvents = { new RequestedEvent(new EventName(PackageName.Mf, mfEvent, rxConnection
				.getSecondConnId()), actions) };
		notificationRequest.setRequestedEvents(requestedEvents);

		NotifiedEntity notifiedEntity = new NotifiedEntity(caIPAddress.getHostName(), caIPAddress.getHostAddress(),
				caStack.getPort());
		notificationRequest.setNotifiedEntity(notifiedEntity);

		notificationRequest.setTransactionHandle(caProvider.getUniqueTransactionHandler());

		super.caProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { notificationRequest });
	}

	private void requestMFToneGen(MgcpEvent mfEvent) {
		NotificationRequest notificationRequest = new NotificationRequest(this, txConnection.getSecondEndpoint(), genToneID);

		EventName[] signalRequests = { new EventName(PackageName.Mf, mfEvent, txConnection.getSecondConnId()) };

		notificationRequest.setSignalRequests(signalRequests);
		NotifiedEntity notifiedEntity = new NotifiedEntity(caIPAddress.getHostName(), caIPAddress.getHostAddress(),
				caStack.getPort());
		notificationRequest.setNotifiedEntity(notifiedEntity);
		notificationRequest.setTransactionHandle(caProvider.getUniqueTransactionHandler());

		caProvider.sendMgcpEvents(new JainMgcpCommandEvent[] { notificationRequest });
	}

	@Test
	public void testDtmf() throws Exception {

		String generatorFreq = "1200-700-100-941-2000-100-1800-2800-100-700-1600-100-0-0-200";
		String detectorFreq = "1800-2800-100";

		this.requestMFToneDet(MgcpEvent.mf0.withParm(detectorFreq));

		Thread.currentThread().sleep(1000);

		this.requestMFToneGen(MgcpEvent.mf0.withParm(generatorFreq));

		oc = false;
		semaphore.tryAcquire(15, TimeUnit.SECONDS);

		//assertEquals(true, checkEvent(MgcpEvent.mf0));
		Thread.currentThread().sleep(2000);
	}

	@Override
	public void tearDown() throws Exception {
		deleteConnectionConnection(rxConnection);
		deleteConnectionConnection(txConnection);

		Thread.sleep(2000);

		super.tearDown();
	}

	private boolean checkEvent(MgcpEvent exp) {
		if (events == null) {
			return false;
		}

		if (events.length != 1) {
			return false;
		}

		return events[0].getEventIdentifier().getName().equals(exp.getName());
	}

	public void processMgcpCommandEvent(JainMgcpCommandEvent event) {
		System.out.println("******** CMD ***********");
		int msg = event.getObjectIdentifier();

		if (msg != Constants.CMD_NOTIFY) {
			return;
		}

		Notify ntfy = (Notify) event;
		events = ntfy.getObservedEvents();

		if (!oc) {
			oc = true;
			semaphore.release();
		}
	}

	@Override
	public void processMgcpResponseEvent(JainMgcpResponseEvent event) {
		super.processMgcpResponseEvent(event);
		response = event;
	}

}
